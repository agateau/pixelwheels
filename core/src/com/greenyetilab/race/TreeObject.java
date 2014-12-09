package com.greenyetilab.race;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;

/**
 * A tree
 */
public class TreeObject implements GameObject, Collidable {
    private static final float TRUNK_RADIUS_RATIO = 0.4f;
    private final Sprite mSprite;
    private Body mBody;
    private boolean mDead = false;

    public TreeObject(GameWorld world, Assets assets, float posX, float posY) {
        TextureRegion region = assets.atlas.findRegion("tree");
        mSprite = new Sprite(region);

        float w = Constants.UNIT_FOR_PIXEL * region.getRegionWidth();
        float h = Constants.UNIT_FOR_PIXEL * region.getRegionHeight();
        mSprite.setSize(w, h);
        mSprite.setOriginCenter();

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(posX, posY);
        bodyDef.angle = MathUtils.degreesToRadians * MathUtils.random(360);
        mBody = world.getBox2DWorld().createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(Math.min(w, h) / 2 * TRUNK_RADIUS_RATIO);
        mBody.createFixture(shape, 1f);

        mBody.setUserData(this);
    }

    @Override
    public boolean act(float delta) {
        if (mDead && mBody != null) {
            World world = mBody.getWorld();
            world.destroyBody(mBody);
            mBody = null;
        }
        return !mDead;
    }

    @Override
    public void draw(Batch batch, int zIndex) {
        if (zIndex == Constants.Z_SHADOWS) {
            DrawUtils.drawBodySpriteShadow(batch, mBody, mSprite);
        } else if (zIndex == Constants.Z_OBSTACLES) {
            DrawUtils.drawBodySprite(batch, mBody, mSprite);
        }
    }

    @Override
    public void beginContact(Contact contact, Fixture otherFixture) {
    }

    @Override
    public void endContact(Contact contact, Fixture otherFixture) {
        Body other = otherFixture.getBody();
        float impactSpeed = other.getLinearVelocity().len();
        if (impactSpeed > 3) {
            mDead = true;
        }
    }
}
