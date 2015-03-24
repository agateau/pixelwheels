package com.greenyetilab.race;

/**
 * A pilot controlled by the player
 */
public class PlayerPilot implements Pilot {
    private static final float SHOOT_RECOIL = 0.1f;
    private static final float DIRECTION_CORRECTION_STRENGTH = 0.008f;

    private final Assets mAssets;
    private final GameWorld mGameWorld;
    private final Racer mRacer;
    private final HealthComponent mHealthComponent;

    private GameInputHandler mInputHandler;
    private boolean mAutoCorrectDirection = false;

    private float mShootRecoilTime = 0;

    public PlayerPilot(Assets assets, GameWorld gameWorld, Racer racer) {
        mAssets = assets;
        mGameWorld = gameWorld;
        mRacer = racer;
        mHealthComponent = mRacer.getHealthComponent();

        String inputHandlerId = RaceGame.getPreferences().getString("input", "");
        GameInputHandlerFactory factory = GameInputHandlerFactories.getFactoryById(inputHandlerId);
        mInputHandler = factory.create();
        mInputHandler.createHud(assets, mGameWorld.getHudBridge());

        mAutoCorrectDirection = RaceGame.getPreferences().getBoolean(PrefConstants.AUTO_CORRECT, PrefConstants.AUTO_CORRECT_DEFAULT);
    }

    @Override
    public boolean act(float dt) {
        Vehicle vehicle = mRacer.getVehicle();
        if (mHealthComponent.getHealth() == 0) {
            vehicle.setBraking(true);
            vehicle.setAccelerating(false);
            return true;
        }

        if (mShootRecoilTime > 0) {
            mShootRecoilTime -= dt;
        }

        if (vehicle.getY() > mGameWorld.getTopVisibleY()) {
            mGameWorld.setState(GameWorld.State.FINISHED);
            vehicle.setAccelerating(false);
        }

        if (mGameWorld.getState() == GameWorld.State.RUNNING) {
            GameInput input = mInputHandler.getGameInput();
            if (mAutoCorrectDirection && input.direction == 0) {
                input.direction = computeCorrectedDirection();
            }
            vehicle.setDirection(input.direction);
            vehicle.setAccelerating(input.accelerating);
            vehicle.setBraking(input.braking);
            if (input.triggeringBonus && mShootRecoilTime <= 0) {
                mGameWorld.addGameObject(Bullet.create(mAssets, mGameWorld, mRacer, vehicle.getX(), vehicle.getY(), vehicle.getAngle()));
                mShootRecoilTime = SHOOT_RECOIL;
            }
        }
        return true;
    }

    private float computeCorrectedDirection() {
        Vehicle vehicle = mRacer.getVehicle();
        float directionAngle = mGameWorld.getMapInfo().getDirectionAt(vehicle.getX(), vehicle.getY());
        float angle = vehicle.getAngle();
        float delta = Math.abs(angle - directionAngle);
        if (delta < 2) {
            return 0;
        }
        float correctionIntensity = Math.min(1, delta * DIRECTION_CORRECTION_STRENGTH);
        if (directionAngle > angle) {
            return correctionIntensity;
        } else {
            return -correctionIntensity;
        }
    }
}
