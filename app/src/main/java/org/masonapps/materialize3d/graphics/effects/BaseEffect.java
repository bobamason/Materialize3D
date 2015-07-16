package org.masonapps.materialize3d.graphics.effects;


import org.masonapps.materialize3d.graphics.materials.BumpMapMaterial;

/**
 * Created by Bob on 3/21/2015.
 */
public abstract class BaseEffect {

    private int color = 0xFFFFFFFF;

    public BaseEffect() {
    }

    public abstract int getTextureResource();

    public int getColor(){
        return isColorEnabled() ? color : 0xFFFFFFFF;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public abstract String getEffectName();

    public abstract String getCategory();

    public abstract boolean isColorEnabled();

    public abstract float getTextureRepeat();

    public abstract BumpMapMaterial.LightingParams getLightingParams();

    public abstract MaterialType getType();

    public abstract int getIconResource();

    public enum MaterialType{
        RESOURCE, IMAGE, COLOR
    }

    @Override
    public String toString() {
        return getEffectName();
    }
}
