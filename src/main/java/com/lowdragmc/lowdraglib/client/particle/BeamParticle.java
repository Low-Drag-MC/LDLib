package com.lowdragmc.lowdraglib.client.particle;

import com.lowdragmc.lowdraglib.utils.Vector3;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nonnull;

/**
 * @author KilaBash
 * @date 2022/06/15
 * @implNote BeamParticle
 */
public abstract class BeamParticle extends LParticle {
    protected Vector3 from;
    protected Vector3 end;
    protected float width;
    protected float emit;

    protected BeamParticle(ClientLevel level, Vector3 from, Vector3 end) {
        super(level, from.x, from.y, from.z);
        this.setBeam(from, end);
        width = 0.5f;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setBeam(Vector3 from, Vector3 end) {
        this.from = from;
        this.end = end;
        setBoundingBox(new AABB(from.vec3(), end.vec3()));
    }

    public void setEmit(float emit) {
        this.emit = emit;
    }


    public void render(@Nonnull VertexConsumer pBuffer, @Nonnull Camera camera, float partialTicks) {
        Vector3 cameraPos = new Vector3(camera.getPosition());
        PoseStack poseStack = new PoseStack();
        float x = (float)(from.x - cameraPos.x);
        float y = (float)(from.y - cameraPos.y);
        float z = (float)(from.z - cameraPos.z);
        poseStack.pushPose();
        poseStack.translate(x, y, z);
        float offset = - emit * (getAge() + partialTicks);
        renderRawBeam(poseStack, pBuffer, from, end.copy().subtract(from), cameraPos, getU0(partialTicks) + offset, getU1(partialTicks) + offset, getV0(partialTicks), getV1(partialTicks), getWidth(partialTicks), getLightColor(partialTicks));
        poseStack.popPose();
    }

    public static void renderRawBeam(PoseStack poseStack, VertexConsumer bufferbuilder, Vector3 o, Vector3 direction, Vector3 cameraPos, float u0, float v0, float u1, float v1, float beamHeight, int color){
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
        if (cameraPos != null) {
            // Linear algebra drives me crazy
            Vector3 toO = o.copy().subtract(cameraPos);
            Vector3 n = toO.copy().crossProduct(direction);
            Vector3 u = new Vector3(0,1,0);
            float rowX = (float)Math.toDegrees(n.copy().angle(u));

            if (toO.y > 0) {
                rowX = -rowX;
            }
            poseStack.mulPose(new Quaternion(new Vector3f(1.0F, 0.0F, 0.0F), rowX, true));
            Matrix4f mat = poseStack.last().pose();
            bufferbuilder.vertex(mat, 0, - beamHeight, 0).uv(u0, v0).color(1.0f, 1.0f, 1.0f, 1).uv2(color).endVertex();
            bufferbuilder.vertex(mat, 0, beamHeight, 0).uv(u0, v1).color(1.0f, 1.0f, 1.0f, 1).uv2(color).endVertex();
            bufferbuilder.vertex(mat, distance, beamHeight, 0).uv(u1, v1).color(1.0f, 1.0f, 1.0f, 1).uv2(color).endVertex();
            bufferbuilder.vertex(mat, distance, - beamHeight, 0).uv(u1, v0).color(1.0f, 1.0f, 1.0f, 1).uv2(color).endVertex();
        }
    }

    public float getWidth(float pPartialTicks) {
        return width;
    }

    @Override
    protected float getU0(float pPartialTicks) {
        return 0;
    }

    @Override
    protected float getU1(float pPartialTicks) {
        return 1;
    }

    @Override
    protected float getV0(float pPartialTicks) {
        return 0;
    }

    @Override
    protected float getV1(float pPartialTicks) {
        return 1;
    }
}
