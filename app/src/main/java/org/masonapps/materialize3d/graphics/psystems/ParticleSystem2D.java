package org.masonapps.materialize3d.graphics.psystems;

import android.graphics.RectF;
import android.opengl.Matrix;

import org.masonapps.materialize3d.graphics.Vector3;
import org.masonapps.materialize3d.graphics.cameras.BaseCamera;
import org.masonapps.materialize3d.graphics.materials.AnimatedSpriteMaterial;
import org.masonapps.materialize3d.graphics.materials.Material;
import org.masonapps.materialize3d.graphics.meshes.Mesh;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Bob on 5/3/2015.
 */
public class ParticleSystem2D {

    private static final float RAD2DEG = 180f / (float) Math.PI;
    private final Mesh mesh;
    private final Random random;
    private final ParticleSystemParams params;
    private int count = 0;
    private RectF bounds;
    protected float[] modelMatrix = new float[16];
    private List<Particle> particles;
    private float z = -3f;
    private boolean isLoaded = false;

    public ParticleSystem2D(Mesh mesh, int count, RectF bounds, ParticleSystemParams params) {
        if (params == null) {
            this.params = new ParticleSystemParams();
        } else {
            this.params = params;
        }
        this.mesh = mesh;
        particles = new ArrayList<>();
        this.count = count;
        this.bounds = bounds;
        random = new Random();
        final Material material = mesh.getMaterial();
        this.params.animated = material instanceof AnimatedSpriteMaterial;
        initList();
    }

    private void initList() {
        isLoaded = false;
        for (int i = 0; i < count; i++) {
            if (params.animated) {
                particles.add(new Particle(((AnimatedSpriteMaterial) mesh.getMaterial()).getFrameCount()));
            } else {
                particles.add(new Particle(i, count));
            }
        }
        isLoaded = true;
    }

    public void draw(BaseCamera camera, double elapsedTime) {
        if (mesh.loaded && isLoaded) {
            for (int i = 0; i < particles.size(); i++) {
                final Particle particle = particles.get(i);
                particle.update(elapsedTime);
                Matrix.setIdentityM(modelMatrix, 0);
                Matrix.translateM(modelMatrix, 0, particle.loc.x, particle.loc.y, particle.loc.z);
                if (params.useHeading) {
                    Matrix.rotateM(modelMatrix, 0, headingDegrees(particle.vel), 0f, 0f, 1f);
                } else {
                    Matrix.rotateM(modelMatrix, 0, particle.rotation, 0f, 0f, 1f);
                }
                Matrix.scaleM(modelMatrix, 0, particle.scale, particle.scale, 1f);
                if (params.animated) {
                    ((AnimatedSpriteMaterial) mesh.getMaterial()).setCurrentFrame(particle.currentFrame);
                }
                mesh.draw(camera, modelMatrix);
            }
        }
    }

    public void setBounds(RectF bounds) {
        this.bounds.set(bounds);
        initList();
    }

    private float headingDegrees(Vector3 vec) {
        return (float) Math.atan2(vec.y, vec.x) * RAD2DEG;
    }

    private class Particle {
        private float rotation;
        private float scale;
        private Vector3 loc;
        private Vector3 vel;
        private float rotVel;
        private int currentFrame = 0;
        private int frameCount = 1;

//        public Particle() {
//            rotation = 0f;
//            rotVel = random.nextFloat() * (params.maxRotVel - params.minRotVel) + params.minRotVel;
//            scale = random.nextFloat() * (params.maxScale - params.minScale) + params.minScale;
//            loc = new Vector3(random.nextFloat() * bounds.width() + bounds.left, random.nextFloat() * Math.abs(bounds.height()) - bounds.top, z);
//            final float heading = (random.nextFloat() * (params.maxHeading - params.minHeading) + params.minHeading) / RAD2DEG;
//            final float mag = random.nextFloat() * (params.maxVel - params.minVel) + params.minVel;
//            vel = new Vector3((float) Math.cos(heading) * mag, (float) Math.sin(heading) * mag, 0f);
//        }

        public Particle(int pos, int count) {
            rotation = 0f;
            rotVel = random.nextFloat() * (params.maxRotVel - params.minRotVel) + params.minRotVel;
            scale = (float) pos / count * (params.maxScale - params.minScale) + params.minScale;
            loc = new Vector3(random.nextFloat() * bounds.width() + bounds.left, random.nextFloat() * Math.abs(bounds.height()) - bounds.top, z);
            final float heading = (random.nextFloat() * (params.maxHeading - params.minHeading) + params.minHeading) / RAD2DEG;
            final float mag = random.nextFloat() * (params.maxVel - params.minVel) + params.minVel;
            vel = new Vector3((float) Math.cos(heading) * mag, (float) Math.sin(heading) * mag, 0f);
        }

        public Particle(int frameCount) {
            rotation = 0f;
            rotVel = random.nextFloat() * (params.maxRotVel - params.minRotVel) + params.minRotVel;
            scale = 1f;
            loc = new Vector3(random.nextFloat() * bounds.width() + bounds.left, random.nextFloat() * Math.abs(bounds.height()) - bounds.top, z);
            final float heading = (random.nextFloat() * (params.maxHeading - params.minHeading) + params.minHeading) / RAD2DEG;
            final float mag = random.nextFloat() * (params.maxVel - params.minVel) + params.minVel;
            vel = new Vector3((float) Math.cos(heading) * mag, (float) Math.sin(heading) * mag, 0f);
            this.frameCount = frameCount;
            currentFrame = random.nextInt(frameCount);
        }

        public void update(double elapsedTime) {
            loc.x += vel.x * elapsedTime;
            loc.y += vel.y * elapsedTime;
            rotation += rotVel * elapsedTime;
            rotation %= 360f;
            if (loc.y > bounds.top) {
                loc.y = bounds.bottom;
            } else if (loc.y < bounds.bottom) {
                loc.y = bounds.top;
            }
            if (loc.x > bounds.right) {
                loc.x = bounds.left;
            } else if (loc.x < bounds.left) {
                loc.x = bounds.right;
            }

            if (params.animated) {
                currentFrame++;
                currentFrame %= frameCount;
            }
        }
    }

    public static class ParticleSystemParams {

//        private float minVelX = 60f;
//        private float minVelY = 60f;

        private float minVel = 100f;
        private float maxVel = 300f;
        private float minHeading = 0f;
        private float maxHeading = 360f;
        private float minRotVel = -180f;
        private float maxRotVel = 180f;
        private float minScale = 0.8f;
        private float maxScale = 1.4f;
        private boolean useHeading = false;
        private boolean animated = false;


        public ParticleSystemParams() {

        }

        public void setMagnitudeParams(float minVel, float maxVel) {
            this.minVel = minVel;
            this.maxVel = maxVel;
        }

        public void setHeadingParams(float min, float max) {
            this.minHeading = min;
            this.maxHeading = max;
        }

        public void setRotationVelocityParams(float min, float max) {
            minRotVel = min;
            maxRotVel = max;
        }

        public void setScaleParams(float min, float max) {
            minScale = min;
            maxScale = max;
        }

        public void rotateToHeading(boolean useHeading) {
            this.useHeading = useHeading;
        }
    }
}
