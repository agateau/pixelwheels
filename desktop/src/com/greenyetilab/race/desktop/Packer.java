package com.greenyetilab.race.desktop;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class Packer {
    public static void main(String[] args) {
        packTextures(args[0]);
    }

    private static void packTextures(String baseDir) {
        TexturePacker.Settings settings = new TexturePacker.Settings();
        settings.filterMin = Texture.TextureFilter.Linear;
        settings.filterMag = Texture.TextureFilter.Linear;
        settings.pot = false;
        settings.combineSubdirectories = true;

        String inputDir = baseDir + "/core/assets/sprites";
        String outputDir = baseDir + "/android/assets";
        String packName = "race";
        TexturePacker.process(settings, inputDir, outputDir, packName);
        System.out.println("Done");
    }
}
