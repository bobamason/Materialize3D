package org.masonapps.materialize3d.graphics.effects;

/**
 * Created by Bob on 6/11/2015.
 */
public abstract class ResourceTextureEffect extends BaseEffect{

    public ResourceTextureEffect() {
        super();
    }

    @Override
    public MaterialType getType() {
        return MaterialType.RESOURCE;
    }
}
