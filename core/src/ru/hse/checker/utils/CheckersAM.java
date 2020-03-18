package ru.hse.checker.utils;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class CheckersAM {
    private final AssetManager assetManager = new AssetManager();
    private static CheckersAM instance;
    public final static String WHITE_CHECKER = "white";
    public final static String BLACK_CHECKER = "black";
    public final static String BLACK_QUEEN = "black_queen";
    public final static String WHITE_QUEEN = "white_queen";
    public final static String BLACK_CELL = "black_cell";
    public final static String WHITE_CELL = "white_cell";
    public final static String CELL_HIT = "cell_hit";
    public final static String CELL_MOVE = "cell_move";
    public final static String BACKGROUND = "background";

    private final static String skin = "skin.json";

    private TextureAtlas textureAtlas;

    private CheckersAM() {}

    public static CheckersAM getInstance() {
        if (instance == null)
            instance = new CheckersAM();
        return instance;
    }

    public void loadResources() {
        loadTextures();
        loadSkin();
    }

    private void loadTextures() {
        final String FILENAME_SPRITES = "sprites2.txt";
        assetManager.load(FILENAME_SPRITES, TextureAtlas.class);
        assetManager.finishLoading(); //block thread, may progress
        textureAtlas = assetManager.get(FILENAME_SPRITES);
    }

    public void loadSkin() {
        SkinLoader.SkinParameter params = new SkinLoader.SkinParameter("skin.atlas");
        assetManager.load(skin, Skin.class, params);
        assetManager.finishLoading();
    }

    public TextureAtlas.AtlasRegion getTexture(String txtName) {
        return textureAtlas.findRegion(txtName);
    }

    public Skin getSkin() {
        return assetManager.get(skin);
    }

    public void dispose() {
        assetManager.dispose();
        instance = null;
    }
}
