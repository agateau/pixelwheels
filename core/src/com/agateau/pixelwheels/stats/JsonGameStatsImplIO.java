/*
 * Copyright 2018 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.pixelwheels.stats;

import com.agateau.pixelwheels.gamesetup.Difficulty;
import com.agateau.utils.log.NLog;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Loads player game statistics from a JSON file.
 *
 * <p>Format v1:
 *
 * <pre>
 * {
 *     "trackStats": {
 *         "$trackId": {
 *             "lap": [
 *                  {
 *                      "vehicle": "$vehicleId",
 *                      "value": $floatValue
 *                  },
 *                  ...
 *             ],
 *             "total": [
 *                  {
 *                      "vehicle": "$vehicleId",
 *                      "value": ""
 *                  },
 *                  ...
 *             ]
 *         },
 *         ...
 *     },
 *     "bestChampionshipRank": {
 *         "$trackId": $intRank,
 *         ...
 *     },
 *     "events": {
 *         "$eventId": $intCount,
 *         ...
 *     }
 * }
 * </pre>
 *
 * <p>Format v2:
 *
 * <pre>
 * {
 *     "version": 2,
 *     "trackStats": {
 *         "$difficultyId": {
 *             "$trackId": {
 *                 "lap": [
 *                      {
 *                          "vehicle": "$vehicleId",
 *                          "value": $floatValue
 *                      },
 *                      ...
 *                 ],
 *                 "total": [
 *                      {
 *                          "vehicle": "$vehicleId",
 *                          "value": ""
 *                      },
 *                      ...
 *                 ]
 *             },
 *             ...
 *         }
 *     },
 *     "bestChampionshipRank": {
 *         "$difficultyId": {
 *             "$trackId": $intRank,
 *             ...
 *          },
 *          ...
 *     },
 *     "events": {
 *         "$eventId": $intCount,
 *         ...
 *     }
 * }
 * </pre>
 */
public class JsonGameStatsImplIO implements GameStatsImpl.IO {
    private static final int CURRENT_VERSION = 2;

    private final FileHandle mHandle;
    private final Gson mGson = new GsonBuilder().setPrettyPrinting().create();

    public JsonGameStatsImplIO(FileHandle handle) {
        mHandle = handle;
    }

    @Override
    public void load(GameStatsImpl gameStats) {
        if (!mHandle.exists()) {
            return;
        }
        String json = mHandle.readString("UTF-8");
        JsonParser parser = new JsonParser();
        JsonObject root = parser.parse(json).getAsJsonObject();

        int version = root.has("version") ? root.get("version").getAsInt() : 1;
        switch (version) {
            case 2:
                loadV2(gameStats, root);
                return;
            case 1:
                loadV1(gameStats, root);
                return;
            default:
                NLog.e(
                        "Don't know how to load stats from version %d, not loading anything",
                        version);
        }
    }

    /** Load v1 stats as stats for Difficulty.HARD */
    private void loadV1(GameStatsImpl gameStats, JsonObject root) {
        Difficulty difficulty = Difficulty.HARD;

        // trackStats / $trackId
        JsonObject trackStatsObject = root.getAsJsonObject("trackStats");
        if (trackStatsObject != null) {
            HashMap<String, TrackStats> trackStatsByTrack =
                    gameStats.mTrackStatsByDifficulty.get(difficulty);
            trackStatsByTrack.clear();
            for (Map.Entry<String, JsonElement> kv : trackStatsObject.entrySet()) {
                String trackId = kv.getKey();
                TrackStats trackStats = new TrackStats(gameStats);
                trackStatsByTrack.put(trackId, trackStats);
                loadTrackStats(trackStats, kv.getValue().getAsJsonObject());
            }
        }

        // bestChampionshipRank
        JsonObject ranksObject = root.getAsJsonObject("bestChampionshipRank");
        loadStringIntMap(gameStats.mBestChampionshipRankByDifficulty.get(difficulty), ranksObject);

        loadStringIntMap(gameStats.mEvents, root.getAsJsonObject("events"));
    }

    private void loadV2(GameStatsImpl gameStats, JsonObject root) {
        // trackStats / $difficultyId / $trackId
        JsonObject trackStatsByDifficultyObject = root.getAsJsonObject("trackStats");
        for (Difficulty difficulty : Difficulty.values()) {
            JsonObject trackStatsObject =
                    trackStatsByDifficultyObject.getAsJsonObject(difficulty.name());
            if (trackStatsObject == null) {
                continue;
            }
            HashMap<String, TrackStats> trackStatsByTrack =
                    gameStats.mTrackStatsByDifficulty.get(difficulty);
            trackStatsByTrack.clear();
            for (Map.Entry<String, JsonElement> kv : trackStatsObject.entrySet()) {
                String trackId = kv.getKey();
                TrackStats trackStats = new TrackStats(gameStats);
                trackStatsByTrack.put(trackId, trackStats);
                loadTrackStats(trackStats, kv.getValue().getAsJsonObject());
            }
        }

        // bestChampionshipRank / $difficultyId
        JsonObject ranksByDifficultyObject = root.getAsJsonObject("bestChampionshipRank");
        for (Difficulty difficulty : Difficulty.values()) {
            loadStringIntMap(
                    gameStats.mBestChampionshipRankByDifficulty.get(difficulty),
                    ranksByDifficultyObject.getAsJsonObject(difficulty.name()));
        }
        loadStringIntMap(gameStats.mEvents, root.getAsJsonObject("events"));
    }

    private void loadTrackStats(TrackStats trackStats, JsonObject object) {
        loadResults(trackStats.mLapRecords, object.getAsJsonArray("lap"));
        loadResults(trackStats.mTotalRecords, object.getAsJsonArray("total"));
    }

    private static void loadStringIntMap(Map<String, Integer> map, JsonObject object) {
        map.clear();
        if (object == null) {
            return;
        }
        for (Map.Entry<String, JsonElement> kv : object.entrySet()) {
            String id = kv.getKey();
            int value = kv.getValue().getAsInt();
            map.put(id, value);
        }
    }

    private void loadResults(ArrayList<TrackResult> results, JsonArray array) {
        results.clear();
        for (JsonElement element : array) {
            TrackResult result = mGson.fromJson(element, TrackResult.class);
            results.add(result);
        }
    }

    @Override
    public void save(GameStatsImpl gameStats) {
        JsonObject root = new JsonObject();

        root.addProperty("version", CURRENT_VERSION);

        // Add track stats
        JsonObject trackStatsByDifficultyObject = new JsonObject();
        root.add("trackStats", trackStatsByDifficultyObject);
        for (Difficulty difficulty : Difficulty.values()) {
            JsonObject trackStatsObject = new JsonObject();
            trackStatsByDifficultyObject.add(difficulty.name(), trackStatsObject);

            for (Map.Entry<String, TrackStats> kv :
                    gameStats.mTrackStatsByDifficulty.get(difficulty).entrySet()) {
                trackStatsObject.add(kv.getKey(), createJsonForTrack(kv.getValue()));
            }
        }

        // Add championship ranks
        root.add(
                "bestChampionshipRank",
                mGson.toJsonTree(gameStats.mBestChampionshipRankByDifficulty));

        // Add events
        root.add("events", mGson.toJsonTree(gameStats.mEvents));

        String json = mGson.toJson(root);
        mHandle.writeString(json, false /* append */);
    }

    private JsonObject createJsonForTrack(TrackStats trackStats) {
        JsonObject root = new JsonObject();
        root.add("lap", createJsonForResults(trackStats.get(TrackStats.ResultType.LAP)));
        root.add("total", createJsonForResults(trackStats.get(TrackStats.ResultType.TOTAL)));
        return root;
    }

    private JsonArray createJsonForResults(ArrayList<TrackResult> results) {
        JsonArray array = new JsonArray();
        for (TrackResult result : results) {
            if (result.vehicle.equals(TrackStats.DEFAULT_RECORD_VEHICLE)) {
                continue;
            }
            array.add(mGson.toJsonTree(result));
        }
        return array;
    }
}
