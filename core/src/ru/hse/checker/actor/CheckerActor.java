package ru.hse.checker.actor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.ColorAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;

import ru.hse.checker.utils.CheckersAM;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

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
        setOrigin(getWidth()/2, getHeight()/2); //to properly scaling
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

    public Type getType() {return type;}

    private static class NonClampColor extends Color {
        public NonClampColor(float r, float g, float b, float a) {
            super(r, g, b, a);
        }
        @Override
        public Color clamp() {
            return this;
        }
    }

    private Color selectingColor = getColor();
    private float dimmerConstant = 0.25f;

    private class NonClampColorAction extends ColorAction {
        public NonClampColorAction(NonClampColor color) {
            setEndColor(color);
        }
        @Override
        protected void update(float percent) {
            super.update(percent);
            selectingColor = getColor();
        }
    }

    public void select() {
        float duration = 0.5f;
        NonClampColorAction bright = new NonClampColorAction(new NonClampColor(1+dimmerConstant, 1+dimmerConstant, 1+dimmerConstant, 1));
        bright.setDuration(duration/2);
        bright.setInterpolation(Interpolation.smoother);
        bright.setColor(new NonClampColor(1f, 1f, 1f, 1));
        addAction(bright);
        animateSelecting();
    }

    public void deselect() {
        float duration = 0.5f;
        NonClampColorAction dimmer = new NonClampColorAction(new NonClampColor(1+dimmerConstant, 1+dimmerConstant, 1+dimmerConstant, 1));
        dimmer.setDuration(duration/2);
        dimmer.setInterpolation(Interpolation.smoother);
        dimmer.setColor(new NonClampColor(1f, 1f, 1f, 1));
        dimmer.setReverse(true);
        addAction(dimmer);
    }

    public void animateSelecting() {
        float scaleVal = 1.05f;
        float duration = 0.5f;

        ScaleToAction scale = scaleTo(scaleVal, scaleVal, duration/2, Interpolation.smoother);
        ScaleToAction shrink = scaleTo(1f, 1f, duration/2, Interpolation.smoother);
        Action action = sequence(scale, shrink);
        addAction(action);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        setType(type);
        Color color = selectingColor;
        batch.setColor(color.r - dimmerConstant, color.g - dimmerConstant, color.b - dimmerConstant, getColor().a * parentAlpha);
        batch.draw(atlasRegion, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(),
                getScaleX(), getScaleY(), getRotation());
        batch.setColor(color.r, color.g, color.b, 1f);
    }
}
