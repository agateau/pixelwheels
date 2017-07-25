package com.agateau.ui;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * GridMenuItem ItemRenderer for a TextureRegion
 */
public class TextureRegionItemRenderer extends TextureRegionItemRendererAdapter<TextureRegion> {
    @Override
    protected TextureRegion getItemRegion(TextureRegion region) {
        return region;
    }
}
