package com.lowdragmc.lowdraglib.client.particle;

import com.lowdragmc.lowdraglib.client.scene.ParticleManager;
import com.lowdragmc.lowdraglib.utils.DummyWorld;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

/**
 * @author KilaBash
 * @date 2022/05/30
 * @implNote LParticle
 */
@OnlyIn(Dist.CLIENT)
public abstract class LParticle extends Particle {
    public float quadSize = 1;
    public int fadeIn = -1;
    public int fadeOut = -1;
    public boolean moveless;
    public int delay;
    protected Consumer<LParticle> onUpdate;
    public int lightColor = -1;
    public boolean cull = true;
    private Level realLevel;
    protected float alphao = 1;

    protected LParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
        this.realLevel = level;
        if (level == null) {
            hasPhysics = false;
        }
    }

    protected LParticle(ClientLevel level, double x, double y, double z, double sX, double sY, double sZ) {
        super(level, x, y, z, sX, sY, sZ);
        this.realLevel = level;
        if (level == null) {
            hasPhysics = false;
        }
    }

    public Level getLevel() {
        return realLevel == null ? super.level : realLevel;
    }

    public void setLevel(Level level) {
        this.realLevel = level;
    }

    public void setMoveless(boolean moveless) {
        this.moveless = moveless;
    }

    public void setPhysics(boolean hasPhysics) {
        this.hasPhysics = hasPhysics;
    }

    public void setFullLight() {
        setLight(0xf000f0);
    }

    public void setOnUpdate(Consumer<LParticle> onUpdate) {
        this.onUpdate = onUpdate;
    }

    public boolean isMoveless() {
        return moveless;
    }

    public void setFadeIn(int fadeIn) {
        this.fadeIn = fadeIn;
        this.setAlpha(0, true);
    }

    public void setFadeOut(int fadeOut) {
        this.fadeOut = fadeOut;
    }

    public void setFade(int fade) {
        setFadeIn(fade);
        setFadeOut(fade);
    }

    public void setRoll(float roll, boolean setOrigin) {
        this.roll = roll;
        if (setOrigin) {
            this.oRoll = roll;
        }
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    @Override
    public void tick() {
        if (delay > 0) {
            delay--;
            return;
        }
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.alphao = this.alpha;
        if (this.age++ >= this.lifetime && lifetime > 0) {
            this.remove();
        } else if (onUpdate == null) {
            update();
        } else {
            onUpdate.accept(this);
        }
        if (fadeIn > 0 && this.age <= fadeIn) {
            this.alpha = this.age * 1f / fadeIn;
        } else if (fadeOut > 0 && this.lifetime > 0 && (this.lifetime - this.age) <= fadeOut) {
            this.alpha = (this.lifetime - this.age) * 1f / fadeOut;
        }
    }

    protected void update() {
        if (!moveless) {
            this.yd -= 0.04D * this.gravity;
            this.move(this.xd, this.yd, this.zd);
            if (this.speedUpWhenYMotionIsBlocked && this.y == this.yo) {
                this.xd *= 1.1D;
                this.zd *= 1.1D;
            }
            this.xd *= this.friction;
            this.yd *= this.friction;
            this.zd *= this.friction;
            if (this.onGround) {
                this.xd *= 0.7F;
                this.zd *= 0.7F;
            }
        }
    }

    public void render(@Nonnull VertexConsumer pBuffer, Camera pRenderInfo, float pPartialTicks) {
        if (delay <= 0) {
            renderInternal(pBuffer, pRenderInfo, pPartialTicks);
        }
    }

    public void renderInternal(@Nonnull VertexConsumer pBuffer, Camera pRenderInfo, float pPartialTicks) {
        Vec3 vec3 = pRenderInfo.getPosition();
        float f = (float)(Mth.lerp(pPartialTicks, this.xo, this.x) - vec3.x());
        float f1 = (float)(Mth.lerp(pPartialTicks, this.yo, this.y) - vec3.y());
        float f2 = (float)(Mth.lerp(pPartialTicks, this.zo, this.z) - vec3.z());
        float a = Mth.lerp(pPartialTicks, this.alphao, this.alpha);
        Quaternion quaternion;
        if (this.roll == 0.0F) {
            quaternion = pRenderInfo.rotation();
        } else {
            quaternion = new Quaternion(pRenderInfo.rotation());
            float f3 = Mth.lerp(pPartialTicks, this.oRoll, this.roll);
            quaternion.mul(Vector3f.ZP.rotation(f3));
        }

        Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
        float f4 = this.getQuadSize(pPartialTicks);

        for(int i = 0; i < 4; ++i) {
            Vector3f vector3f = avector3f[i];
            vector3f.transform(quaternion);
            vector3f.mul(f4);
            vector3f.add(f, f1, f2);
        }

        float f7 = this.getU0(pPartialTicks);
        float f8 = this.getU1(pPartialTicks);
        float f5 = this.getV0(pPartialTicks);
        float f6 = this.getV1(pPartialTicks);
        int j = this.getLightColor(pPartialTicks);
        pBuffer.vertex(avector3f[0].x(), avector3f[0].y(), avector3f[0].z()).uv(f8, f6).color(this.rCol, this.gCol, this.bCol, a).uv2(j).endVertex();
        pBuffer.vertex(avector3f[1].x(), avector3f[1].y(), avector3f[1].z()).uv(f8, f5).color(this.rCol, this.gCol, this.bCol, a).uv2(j).endVertex();
        pBuffer.vertex(avector3f[2].x(), avector3f[2].y(), avector3f[2].z()).uv(f7, f5).color(this.rCol, this.gCol, this.bCol, a).uv2(j).endVertex();
        pBuffer.vertex(avector3f[3].x(), avector3f[3].y(), avector3f[3].z()).uv(f7, f6).color(this.rCol, this.gCol, this.bCol, a).uv2(j).endVertex();
    }

    public float getQuadSize(float pPartialTicks) {
        return this.quadSize;
    }

    @Nonnull
    public LParticle scale(float pScale) {
        this.quadSize = pScale;
        super.scale(pScale);
        return this;
    }

    public void setAlpha(float alpha, boolean setOrigin) {
        this.alpha = alpha;
        if (setOrigin) {
            this.alphao = alpha;
        }
    }

    public void setPos(double pX, double pY, double pZ, boolean setOrigin) {
        this.x = pX;
        this.y = pY;
        this.z = pZ;
        if (setOrigin) {
            this.xo = x;
            this.yo = y;
            this.zo = z;
        }
        float f = this.bbWidth / 2.0F;
        float f1 = this.bbHeight;
        this.setBoundingBox(new AABB(pX - (double)f, pY, pZ - (double)f, pX + (double)f, pY + (double)f1, pZ + (double)f));
    }

    public void setGravity(float gravity) {
        this.gravity = gravity;
    }

    public float getGravity() {
        return gravity;
    }

    @Override
    protected int getLightColor(float pPartialTick) {
        if (lightColor >= 0) return lightColor;
        if (level == null) return 0xf000f0;
        return super.getLightColor(pPartialTick);
    }
    public void setLight(int light) {
        this.lightColor = light;
    }

    public void setColor(int color) {
        this.setColor((float) FastColor.ARGB32.red(color) / 255, (float)FastColor.ARGB32.green(color) / 255, (float)FastColor.ARGB32.blue(color) / 255);
    }
    @Override
    public boolean shouldCull() {
        return cull;
    }

    protected float getU0(float pPartialTicks) {
        return 0;
    }

    protected float getU1(float pPartialTicks) {
        return 1;
    }

    protected float getV0(float pPartialTicks) {
        return 0;
    }

    protected float getV1(float pPartialTicks) {
        return 1;
    }

    public int getAge() {
        return age;
    }

    public void setCull(boolean cull) {
        this.cull = cull;
    }

    public void setImmortal() {
        setLifetime(-1);
    }

    public void setFriction(float friction) {
        this.friction = friction;
    }

    public void addParticle() {
        if (getLevel() instanceof DummyWorld dummyWorld) {
            ParticleManager particleManager = dummyWorld.getParticleManager();
            if (particleManager != null) {
                particleManager.addParticle(this);
            }
        } else {
            Minecraft.getInstance().particleEngine.add(this);
        }
    }

    public void resetAge() {
        this.age = 0;
    }
}
