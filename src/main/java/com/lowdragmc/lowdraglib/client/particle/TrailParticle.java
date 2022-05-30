package com.lowdragmc.lowdraglib.client.particle;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3d;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.Queue;

/**
 * @author KilaBash
 * @date 2022/05/30
 * @implNote TraiTrailParticle
 */
@OnlyIn(Dist.CLIENT)
public abstract class TrailParticle extends Particle {
    protected boolean moveless;
    protected Queue<Vector3d> tails = Queues.newArrayDeque();
    protected int maxTail;
    protected int freq;
    protected int tickTimer;

    protected TrailParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
    }

    protected TrailParticle(ClientLevel level, double x, double y, double z, double sX, double sY, double sZ) {
        super(level, x, y, z, sX, sY, sZ);
    }

    public TrailParticle setMoveless(boolean moveless) {
        this.moveless = moveless;
        return this;
    }


    @Override
    public final void tick() {
        if (tickTimer % freq == 0) {
            tails.add(getTail());
            while (tails.size() > maxTail) {
                tails.remove();
            }
        }
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime && lifetime > 0) {
            this.remove();
        } else {
            update();
        }
    }

    protected void update() {
         if (!moveless){
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

    protected Vector3d getTail() {
        return new Vector3d(this.xo, this.yo, this.zo);
    }

    public void render(@Nonnull VertexConsumer pBuffer, Camera pRenderInfo, float pPartialTicks) {
        Vec3 vec3 = pRenderInfo.getPosition();
        Quaternion quaternion;
        if (this.roll == 0.0F) {
            quaternion = pRenderInfo.rotation();
        } else {
            quaternion = new Quaternion(pRenderInfo.rotation());
            float f3 = Mth.lerp(pPartialTicks, this.oRoll, this.roll);
            quaternion.mul(Vector3f.ZP.rotation(f3));
        }

        Vector3d lastTail = null;
        if (tails.size() > 0) {
            int i = 0;
            for (Vector3d tail : tails) {
                if (lastTail != null) {
                    renderTail(vec3, quaternion, pBuffer, getU0(i), getV0(i), getU1(i), getV1(i), lastTail, tail, getLightColor(i, pPartialTicks), getWidth(i, pPartialTicks), pPartialTicks);
                }
                i++;
                lastTail = tail;
            }
            double x = (Mth.lerp(pPartialTicks, this.xo, this.x) - vec3.x());
            double y = (Mth.lerp(pPartialTicks, this.yo, this.y) - vec3.y());
            double z = (Mth.lerp(pPartialTicks, this.zo, this.z) - vec3.z());
            renderTail(vec3, quaternion, pBuffer, getU0(i), getV0(i), getU1(i), getV1(i), lastTail, new Vector3d(x, y, z), getLightColor(i, pPartialTicks), getWidth(i, pPartialTicks), pPartialTicks);
        }
    }

    public void renderTail(Vec3 cameraP, Quaternion cameraQ, VertexConsumer pBuffer, float u0, float v0, float u1, float v1, Vector3d lastTail, Vector3d tail, int color, float size, float pPartialTicks) {
        float f = (float)(lastTail.x - cameraP.x());
        float f1 = (float)(lastTail.y - cameraP.y());
        float f2 = (float)(lastTail.z - cameraP.z());

        Vector3f vec = new Vector3f(0, size, 0);
        vec.transform(cameraQ);
        vec.add(f, f1, f2);

        pBuffer.vertex(vec.x(), vec.y(), vec.z()).uv(u1, v1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(color).endVertex();

        vec = new Vector3f(0, -size, 0);
        vec.transform(cameraQ);
        vec.add(f, f1, f2);

        pBuffer.vertex(vec.x(), vec.y(), vec.z()).uv(u1, v0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(color).endVertex();

        f = (float)(tail.x - cameraP.x());
        f1 = (float)(tail.y - cameraP.y());
        f2 = (float)(tail.z - cameraP.z());

        vec = new Vector3f(0, size, 0);
        vec.transform(cameraQ);
        vec.add(f, f1, f2);

        pBuffer.vertex(vec.x(), vec.y(), vec.z()).uv(u0, v0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(color).endVertex();

        vec = new Vector3f(0, -size, 0);
        vec.transform(cameraQ);
        vec.add(f, f1, f2);

        pBuffer.vertex(vec.x(), vec.y(), vec.z()).uv(u0, v1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(color).endVertex();
    }

    public float getWidth(int tail, float pPartialTicks) {
        return 1;
    }

    public int getLightColor(int tail, float pPartialTicks) {
        return 15728880;
    }

    protected abstract float getU0(int tail);

    protected abstract float getU1(int tail);

    protected abstract float getV0(int tail);

    protected abstract float getV1(int tail);
}
