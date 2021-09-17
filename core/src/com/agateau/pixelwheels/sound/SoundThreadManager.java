/*
 * Copyright 2021 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Pixel Wheels is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.agateau.pixelwheels.sound;

import com.agateau.utils.log.NLog;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * This class plays sound effects on a separate thread.
 *
 * <p>This is required because of a bug in Android 10 which causes calls to Sound.play() to block.
 *
 * <p>Communication between the sound thread and the rest is done through a message queue. Messages
 * are created from a Pool to avoid allocations.
 *
 * @see <a href="https://github.com/libgdx/libgdx/issues/5786">libgdx issue #5786</a>
 */
public class SoundThreadManager implements Runnable {
    private static final int MESSAGE_QUEUE_SIZE = 80;

    /**
     * A message to send on the queue. The class can contains all the possible messages, so its
     * members are the union of all possible message parameters. This is not elegant but it makes it
     * possible to use a single message pool for all messages.
     */
    private static class Message {
        enum Type {
            PLAY_AND_FORGET,
            PLAY,
            LOOP,
            STOP,
            SET_VOLUME,
            SET_PITCH,
            SHUTDOWN,
        }

        public Type type;
        public long playId;
        public Sound sound;
        public float volume;
        public float pitch;

        static final Pool<Message> sPool =
                new Pool<Message>() {
                    @Override
                    protected Message newObject() {
                        return new Message();
                    }
                };
    }

    /**
     * Represents a sound as it is being played. This is used by the sound thread when it needs to
     * stop a playing sound, or adjust its volume or pitch.
     */
    private static class PlayingSound {
        /** The id returned by our play() or loop() methods */
        long playId;
        /** The id returned by the Sound.play() or Sound.loop() methods */
        long internalId;

        Sound sound;

        static Pool<PlayingSound> sPool =
                new Pool<PlayingSound>() {
                    @Override
                    protected PlayingSound newObject() {
                        return new PlayingSound();
                    }
                };
    }

    /**
     * All members of this struct are only accessed by the thread, so no synchronization is required
     * on them
     */
    private static class ThreadData {
        final Array<PlayingSound> playingSounds = new Array<>(/* ordered */ false, 16);

        PlayingSound findSound(long playId) {
            int idx = findSoundIndex(playId);
            return idx >= 0 ? playingSounds.get(idx) : null;
        }

        PlayingSound takeSound(long playId) {
            int idx = findSoundIndex(playId);
            return idx >= 0 ? playingSounds.removeIndex(idx) : null;
        }

        private int findSoundIndex(long playId) {
            for (int idx = 0, n = playingSounds.size; idx < n; idx++) {
                if (playingSounds.get(idx).playId == playId) {
                    return idx;
                }
            }
            return -1;
        }
    }

    private final Thread mThread = new Thread(this);
    private final ThreadData mThreadData = new ThreadData();
    private final ArrayBlockingQueue<Message> mMessageQueue =
            new ArrayBlockingQueue<>(MESSAGE_QUEUE_SIZE);

    private long mNextPlayId = 0;

    public SoundThreadManager() {
        Gdx.app.addLifecycleListener(
                new LifecycleListener() {
                    @Override
                    public void pause() {}

                    @Override
                    public void resume() {}

                    @Override
                    public void dispose() {
                        shutdownThread();
                    }
                });
        mThread.start();
    }

    @Override
    public void run() {
        while (true) {
            Message message;
            try {
                message = mMessageQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
            switch (message.type) {
                case SHUTDOWN:
                    NLog.i("stopped");
                    return;
                case PLAY_AND_FORGET:
                    {
                        message.sound.play(message.volume, 1, 0);
                        break;
                    }
                case PLAY:
                    {
                        PlayingSound pSound = PlayingSound.sPool.obtain();
                        pSound.playId = message.playId;
                        pSound.sound = message.sound;
                        pSound.internalId = message.sound.play(message.volume, message.pitch, 0);
                        mThreadData.playingSounds.add(pSound);
                        break;
                    }
                case LOOP:
                    {
                        PlayingSound pSound = PlayingSound.sPool.obtain();
                        pSound.playId = message.playId;
                        pSound.sound = message.sound;
                        pSound.internalId = message.sound.loop(message.volume, message.pitch, 0);
                        mThreadData.playingSounds.add(pSound);
                        break;
                    }
                case STOP:
                    {
                        PlayingSound pSound = mThreadData.takeSound(message.playId);
                        if (pSound == null) {
                            NLog.e("Invalid playId: %d", message.playId);
                            continue;
                        }
                        pSound.sound.stop(pSound.internalId);
                        PlayingSound.sPool.free(pSound);
                        break;
                    }
                case SET_VOLUME:
                    {
                        PlayingSound pSound = mThreadData.findSound(message.playId);
                        if (pSound == null) {
                            NLog.e("Invalid playId: %d", message.playId);
                            continue;
                        }
                        pSound.sound.setVolume(pSound.internalId, message.volume);
                        break;
                    }
                case SET_PITCH:
                    {
                        PlayingSound pSound = mThreadData.findSound(message.playId);
                        if (pSound == null) {
                            NLog.e("Invalid playId: %d", message.playId);
                            continue;
                        }
                        pSound.sound.setPitch(pSound.internalId, message.pitch);
                        break;
                    }
            }
            synchronized (Message.sPool) {
                Message.sPool.free(message);
            }
        }
    }

    /**
     * Play a sound but do not track it, meaning it is not possible to stop it or to adjust its
     * volume or pitch later
     */
    public void playAndForget(Sound sound, float volume) {
        Message message = obtainMessage();
        message.type = Message.Type.PLAY_AND_FORGET;
        message.sound = sound;
        message.volume = volume;
        queueMessage(message);
    }

    public long play(Sound sound, float volume) {
        return play(sound, volume, 1);
    }

    /**
     * Play a sound on a separate thread, return an id which can be used to control the sound with
     * setVolume(), setPitch() or stop() or -1 if the sound could not be played because the sound
     * thread is too busy
     */
    public long play(Sound sound, float volume, float pitch) {
        return internalPlay(sound, volume, pitch, /* loop */ false);
    }

    public long loop(Sound sound, float volume, float pitch) {
        return internalPlay(sound, volume, pitch, /* loop */ true);
    }

    public void stop(long playId) {
        Message message = obtainMessage();
        message.type = Message.Type.STOP;
        message.playId = playId;
        queueMessage(message);
    }

    public void setVolume(long playId, float volume) {
        Message message = obtainMessage();
        message.type = Message.Type.SET_VOLUME;
        message.playId = playId;
        message.volume = volume;
        queueMessage(message);
    }

    public void setPitch(long playId, float pitch) {
        Message message = obtainMessage();
        message.type = Message.Type.SET_PITCH;
        message.playId = playId;
        message.pitch = pitch;
        queueMessage(message);
    }

    private long internalPlay(Sound sound, float volume, float pitch, boolean loop) {
        long playId = mNextPlayId++;
        Message message = obtainMessage();
        message.type = loop ? Message.Type.LOOP : Message.Type.PLAY;
        message.playId = playId;
        message.sound = sound;
        message.volume = volume;
        message.pitch = pitch;
        if (!queueMessage(message)) {
            return -1;
        }
        return playId;
    }

    private Message obtainMessage() {
        synchronized (Message.sPool) {
            return Message.sPool.obtain();
        }
    }

    private boolean queueMessage(Message message) {
        if (mMessageQueue.offer(message)) {
            return true;
        }
        if (message.type != Message.Type.STOP) {
            NLog.e("Sound message queue is full, discarding message");
            return false;
        }
        // Only block if we want to send a STOP message, because if we skip a STOP message we might
        // end up with an infinite looping sound
        NLog.e("Sound message queue is full, blocking to send a STOP message");
        try {
            mMessageQueue.put(message);
        } catch (InterruptedException e) {
            NLog.e("Interrupted while putting a STOP message");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void shutdownThread() {
        Message message = obtainMessage();
        message.type = Message.Type.SHUTDOWN;
        queueMessage(message);
        try {
            mThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
