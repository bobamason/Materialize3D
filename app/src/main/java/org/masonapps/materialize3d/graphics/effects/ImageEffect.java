package org.masonapps.materialize3d.graphics.effects;

/**
 * Created by Bob on 6/11/2015.
 */
public abstract class ImageEffect extends BaseEffect {

    public ImageEffect() {
        super();
    }

    @Override
    public int getTextureResource() {
        return -1;
    }

    @Override
    public float getTextureRepeat() {
        return 1f;
    }

    @Override
    public boolean isColorEnabled() {
        return false;
    }

    @Override
    public MaterialType getType() {
        return MaterialType.IMAGE;
    }
}
