package ru.hse.checker.utils;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import javax.xml.soap.Text;

public class CheckersAM {
    private final AssetManager assetManager = new AssetManager();
    private static final CheckersAM instance = new CheckersAM();
    public final static String WHITE_CHECKER = "white";
    public final static String BLACK_CHECKER = "black";
    public final static String BLACK_QUEEN = "black_queen";
    public final static String WHITE_QUEEN = "white_queen";
    public final static String BLACK_CELL = "black_cell";
    public final static String WHITE_CELL = "white_cell";
    public final static String CELL_HIT = "cell_hit";
    public final static String CELL_MOVE = "cell_move";
    public final static String BACKGROUND = "background";
    private TextureAtlas textureAtlas;

    private CheckersAM() {
        loadTextures();
    }

    public static CheckersAM getInstance() {
        return instance;
    }

    private void loadTextures() {
        final String FILENAME_SPRITES = "sprites.txt";
        assetManager.load(FILENAME_SPRITES, TextureAtlas.class);
        assetManager.finishLoading(); //block thread, may progress
        textureAtlas = assetManager.get(FILENAME_SPRITES);

    }

    public TextureAtlas.AtlasRegion getTexture(String txtName) {
        return textureAtlas.findRegion(txtName);
    }

    public void dispose() {
        assetManager.dispose();
    }
}
