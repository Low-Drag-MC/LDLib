package com.lowdragmc.lowdraglib.client.particle;

import com.google.common.collect.Queues;
import com.lowdragmc.lowdraglib.utils.Vector3;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
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
        if (lifetime != -11111) return;
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
        tickTimer++;
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

    public void render(@Nonnull VertexConsumer pBuffer, @Nonnull Camera camera, float pPartialTicks) {
        int texture = getTexture();
        if (texture > 0) {
            RenderSystem.setShaderTexture(0, getTexture());
        }
        Vec3 vec3 = camera.getPosition();

        Vector3d lastTail = null;
        PoseStack poseStack = new PoseStack();
        if (tails.size() > 0) {
            int i = 0;
            for (Vector3d tail : tails) {
                if (lastTail != null) {
//                    renderTail(false, poseStack, new Vector3(camera.getLookVector()), vec3, pBuffer, getU0(i), getV0(i), getU1(i), getV1(i), lastTail, tail, getLightColor(i, pPartialTicks), getWidth(i, pPartialTicks));
                    i++;
                }
                lastTail = tail;
            }
            double x = (Mth.lerp(1, this.xo, this.x));
            double y = (Mth.lerp(1, this.yo, this.y));
            double z = (Mth.lerp(1, this.zo, this.z));
            renderTail(true, poseStack, new Vector3(camera.getLookVector()), vec3, pBuffer, getU0(i), getV0(i), getU1(i), getV1(i), lastTail, new Vector3d(x, y, z), getLightColor(i, pPartialTicks), getWidth(i, pPartialTicks));
        }
    }

    public void renderTail(boolean isLast, PoseStack poseStack, Vector3 cameraDirection, Vec3 cameraP, VertexConsumer pBuffer, float u0, float v0, float u1, float v1, Vector3d lastTail, Vector3d tail, int color, float size) {
        float x = (float)(lastTail.x - cameraP.x());
        float y = (float)(lastTail.y - cameraP.y());
        float z = (float)(lastTail.z - cameraP.z());
        poseStack.pushPose();
        poseStack.translate(x, y, z);
        renderRawBeam(isLast, poseStack, pBuffer, new Vector3(lastTail), new Vector3(tail).subtract(new Vector3(lastTail)), new Vector3(cameraP), u0, v0, u1, v1, size, color);
        poseStack.popPose();
    }

    public static void renderRawBeam(boolean isLast, PoseStack poseStack, VertexConsumer bufferbuilder, Vector3 o,Vector3 direction, Vector3 cameraDirection, float u0, float v0, float u1, float v1, float beamHeight, int color){
        if (direction.x == direction.z && direction.x == 0) {
            direction = direction.copy().add(0.00001, 0, 0.00001);
        }

        float distance = (float) direction.mag();

        float degree = (float)Math.toDegrees(new Vector3(direction.x, 0, -direction.z).angle(new Vector3(1,0,0)));
        if (direction.z > 0) {
            degree = -degree;
        }
        poseStack.mulPose(new Quaternion(new Vector3f(0.0F, 1.0F, 0.0F), degree, true));
        poseStack.mulPose(new Quaternion(new Vector3f(0, 0, 1), 90 - (float)Math.toDegrees(direction.copy().angle(new Vector3(0,1,0))), true));
        if (cameraDirection != null) {
            // Linear algebra drives me crazy

            Vector3 n = o.copy().subtract(cameraDirection).crossProduct(direction);
            Vector3 u = new Vector3(0,1,0);
            Vector3 projectPlane = u.copy().subtract(u.copy().project(n.copy()));
            float rowX = (float)Math.toDegrees(projectPlane.copy().angle(u));

//            Vector3 vec1 = cameraDirection.copy().project(direction).subtract(cameraDirection);
//            Vector3 vec2 = new Vector3(0,1,0).crossProduct(direction);
//            float rowX = (float)Math.toDegrees(vec1.copy().angle(vec2));
//            boolean reverse = vec1.add(vec2).y < 0;
//            if (rowX < 45 && reverse) rowX = - rowX + 0; else
//            if (reverse) rowX = -rowX;
//            rowX = -rowX;
            if (rowX < 45) rowX = rowX-90;
            poseStack.mulPose(new Quaternion(new Vector3f(1.0F, 0.0F, 0.0F), rowX, true));
            Matrix4f mat = poseStack.last().pose();
//            if (u0 == 0) {
//                bufferbuilder.vertex(mat, 0, - beamHeight, 0).uv(u0, v0).color(1.0f, 1.0f, 1.0f, 1).uv2(color).endVertex();
//                bufferbuilder.vertex(mat, 0, beamHeight, 0).uv(u0, v1).color(1.0f, 1.0f, 1.0f, 1).uv2(color).endVertex();
//            }
            bufferbuilder.vertex(mat, 0, - beamHeight, 0).uv(u0, v0).color(1.0f, 1.0f, 1.0f, 1).uv2(color).endVertex();
            bufferbuilder.vertex(mat, 0, beamHeight, 0).uv(u0, v1).color(1.0f, 1.0f, 1.0f, 1).uv2(color).endVertex();
            bufferbuilder.vertex(mat, distance, beamHeight, 0).uv(u1, v1).color(1.0f, 1.0f, 1.0f, 1).uv2(color).endVertex();
            bufferbuilder.vertex(mat, distance, - beamHeight, 0).uv(u1, v0).color(1.0f, 1.0f, 1.0f, 1).uv2(color).endVertex();
//            if (!isLast) {
//                bufferbuilder.vertex(mat, distance, - beamHeight, 0).uv(u1, v0).color(1.0f, 1.0f, 1.0f, 1).uv2(color).endVertex();
//                bufferbuilder.vertex(mat, distance, beamHeight, 0).uv(u1, v1).color(1.0f, 1.0f, 1.0f, 1).uv2(color).endVertex();
//            }
        }
    }
//
    public void renderTail2(Vec3 cameraP, Quaternion cameraQ, VertexConsumer pBuffer, float u0, float v0, float u1, float v1, Vector3d lastTail, Vector3d tail, int color, float size) {
        float f = (float)(lastTail.x - cameraP.x());
        float f1 = (float)(lastTail.y - cameraP.y());
        float f2 = (float)(lastTail.z - cameraP.z());

        Vector3f vec = new Vector3f(0, -size, 0);
        vec.transform(cameraQ);
        vec.add(f, f1, f2);

        pBuffer.vertex(vec.x(), vec.y(), vec.z()).uv(u0, v0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(color).endVertex();

        vec = new Vector3f(0, size, 0);
        vec.transform(cameraQ);
        vec.add(f, f1, f2);

        pBuffer.vertex(vec.x(), vec.y(), vec.z()).uv(u0, v1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(color).endVertex();

        f = (float)(tail.x - cameraP.x());
        f1 = (float)(tail.y - cameraP.y());
        f2 = (float)(tail.z - cameraP.z());

        vec = new Vector3f(0, size, 0);
        vec.transform(cameraQ);
        vec.add(f, f1, f2);

        pBuffer.vertex(vec.x(), vec.y(), vec.z()).uv(u1, v1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(color).endVertex();

        vec = new Vector3f(0, -size, 0);
        vec.transform(cameraQ);
        vec.add(f, f1, f2);

        pBuffer.vertex(vec.x(), vec.y(), vec.z()).uv(u1, v0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(color).endVertex();
    }

    @Override
    public boolean shouldCull() {
        return false;
    }

    public float getWidth(int tail, float pPartialTicks) {
        return 0.4f;
    }

    public int getLightColor(int tail, float pPartialTicks) {
        return 15728880;
    }

    protected abstract float getU0(int tail);

    protected abstract float getU1(int tail);

    protected abstract float getV0(int tail);

    protected abstract float getV1(int tail);

    protected int getTexture() {
        return 0;
    }
}
