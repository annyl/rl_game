package ru.hse.checker.actor;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

import ru.hse.checker.utils.CheckersAM;

public class CellActor extends BoardActor {
    private TextureAtlas.AtlasRegion atlasRegion;
    private Type type;
    private Type baseType;

    public enum Type {
        BLACK, WHITE, MOVE, HIT
    }

    private static TextureAtlas.AtlasRegion getTexture(CellActor.Type type) {
        switch (type) {
            case BLACK:
                return CheckersAM.getInstance().getTexture(CheckersAM.BLACK_CELL);
            case WHITE:
                return CheckersAM.getInstance().getTexture(CheckersAM.WHITE_CELL);
            case MOVE:
                return CheckersAM.getInstance().getTexture(CheckersAM.CELL_MOVE);
            case HIT:
                return CheckersAM.getInstance().getTexture(CheckersAM.CELL_HIT);
        }
        throw new RuntimeException("There's no such type of cell:" + type);

    }


    public CellActor(CellActor.Type type) {
        atlasRegion = new TextureAtlas.AtlasRegion(getTexture(type));
        this.baseType = type;
        setCellType(type);
        setBounds(getX(), getY(), getWidth(), getHeight());
    }

    public void setCellType(CellActor.Type type) {
        this.type = type;
        atlasRegion.setRegion(getTexture(type));
    }

    public void setToBaseType() {
        setCellType(baseType);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        setCellType(type);
        batch.draw(atlasRegion, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(),
                getScaleX(), getScaleY(), getRotation());
    }

}
