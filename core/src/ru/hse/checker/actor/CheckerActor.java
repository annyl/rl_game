package ru.hse.checker.actor;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ru.hse.checker.utils.CheckersAM;

public class CheckerActor extends BoardActor {
    private TextureAtlas.AtlasRegion atlasRegion;
    private Type type;

    enum Type {
        WHITE,
        BLACK,
        WHITE_QUEEN,
        BLACK_QUEEN
    }

    public CheckerActor(CheckerActor.Type type) {
        super();
        atlasRegion = new TextureAtlas.AtlasRegion(getTexture(type));
        setType(type);
        setBounds(getX(), getY(), getWidth(), getHeight());
    }

    private static TextureAtlas.AtlasRegion getTexture(CheckerActor.Type type) {
        switch (type) {
            case BLACK:
                return CheckersAM.getInstance().getTexture(CheckersAM.BLACK_CHECKER);
            case WHITE:
                return CheckersAM.getInstance().getTexture(CheckersAM.WHITE_CHECKER);
            case BLACK_QUEEN:
                return CheckersAM.getInstance().getTexture(CheckersAM.BLACK_QUEEN);
            case WHITE_QUEEN:
                return CheckersAM.getInstance().getTexture(CheckersAM.WHITE_QUEEN);
        }
        throw new RuntimeException("There's no such type of checker:" + type);
    }

    public void setType(CheckerActor.Type type) {
        this.type = type;
        atlasRegion.setRegion(getTexture(type));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        setType(type);
        batch.draw(atlasRegion, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(),
                getScaleX(), getScaleY(), getRotation());
    }
}
