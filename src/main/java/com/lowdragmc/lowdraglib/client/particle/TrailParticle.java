package com.lowdragmc.lowdraglib.client.particle;

import com.lowdragmc.lowdraglib.utils.Vector3;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;

/**
 * @author KilaBash
 * @date 2022/05/30
 * @implNote TrailParticle
 */
@OnlyIn(Dist.CLIENT)
public abstract class TrailParticle extends LParticle {
    public ArrayList<Vector3> tails = new ArrayList<>();
    public int maxTail;
    public int freq;
    public float width;

    protected TrailParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
        maxTail = 40;
        freq = 1;
        width = 0.5f;
        cull = false;
    }

    protected TrailParticle(ClientLevel level, double x, double y, double z, double sX, double sY, double sZ) {
        super(level, x, y, z, sX, sY, sZ);
        cull = false;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    protected boolean shouldAddTail(Vector3 newTail) {
        return true;
    }

    @Override
    public final void tick() {
        if (age % freq == 0) {
            Vector3 tail = getTail();
            if (shouldAddTail(tail)) {
                tails.add(tail);
            }
            while (tails.size() > maxTail) {
                tails.remove(0);
            }
        }
        super.tick();
    }

    protected Vector3 getTail() {
        return new Vector3(this.xo, this.yo, this.zo);
    }

    @Override
    public void render(@NotNull VertexConsumer pBuffer, Camera pRenderInfo, float pPartialTicks) {
        renderInternal(pBuffer, pRenderInfo, pPartialTicks);
    }

    public void renderInternal(@Nonnull VertexConsumer pBuffer, @Nonnull Camera camera, float partialTicks) {
        Vector3[] verts = new Vector3[tails.size() * 2];
        double x = (Mth.lerp(partialTicks, this.xo, this.x));
        double y = (Mth.lerp(partialTicks, this.yo, this.y));
        double z = (Mth.lerp(partialTicks, this.zo, this.z));
        float a = Mth.lerp(partialTicks, this.alphao, this.alpha);
        Vector3 lastTail = new Vector3(x, y, z);
        Vector3 cameraPos = new Vector3(camera.getPosition());
        int size = tails.size() - 1;
        for (int i = size; i >= 0; i--) {
            Vector3 tail = new Vector3(tails.get(i));
            renderTail(verts, size - i, cameraPos, lastTail, tail, partialTicks);
            lastTail = tail;
        }
        for (int i = 0; i < (verts.length / 2) - 1; i++) {
            Vector3 currentU = verts[i * 2];
            Vector3 currentD = verts[i * 2 + 1];
            Vector3 nextU = verts[(i + 1) * 2];
            Vector3 nextD = verts[(i + 1) * 2 + 1];

            float u0 = getU0(i, partialTicks);
            float u1 = getU1(i, partialTicks);
            float v0 = getV0(i, partialTicks);
            float v1 = getV1(i, partialTicks);
            int light = getLightColor(i, partialTicks);

            pBuffer.vertex(currentD.x, currentD.y, currentD.z).uv(u1, v0).color(this.rCol, this.gCol, this.bCol, a).uv2(light).endVertex();
            pBuffer.vertex(currentU.x, currentU.y, currentU.z).uv(u1, v1).color(this.rCol, this.gCol, this.bCol, a).uv2(light).endVertex();
            pBuffer.vertex(nextD.x, nextD.y, nextD.z).uv(u0, v0).color(this.rCol, this.gCol, this.bCol, a).uv2(light).endVertex();

            pBuffer.vertex(nextD.x, nextD.y, nextD.z).uv(u0, v0).color(this.rCol, this.gCol, this.bCol, a).uv2(light).endVertex();
            pBuffer.vertex(currentU.x, currentU.y, currentU.z).uv(u1, v1).color(this.rCol, this.gCol, this.bCol, a).uv2(light).endVertex();
            pBuffer.vertex(nextU.x, nextU.y, nextU.z).uv(u0, v1).color(this.rCol, this.gCol, this.bCol, a).uv2(light).endVertex();
        }
    }

    public void renderTail(Vector3[] verts, int i, Vector3 cameraPos, Vector3 current, Vector3 nextTail, float partialTicks) {
        float size = getWidth(i, partialTicks);
        Vector3 direction = nextTail.copy().subtract(current);
        Vector3 toTail = current.copy().subtract(cameraPos);
        Vector3 normal = toTail.copy().crossProduct(direction).normalize();
        verts[i * 2] = current.copy().add(normal.copy().multiply(size)).subtract(cameraPos);
        verts[i * 2 + 1] = current.copy().add(normal.copy().multiply(-size)).subtract(cameraPos);
    }

    public float getWidth(int tail, float pPartialTicks) {
        return width;
    }

    public int getLightColor(int tail, float pPartialTicks) {
        return getLightColor(pPartialTicks);
    }

    protected float getU0(int tail, float pPartialTicks) {
        return  1 - (tail + 1 + pPartialTicks) / (maxTail - 1f);
    }

    protected float getV0(int tail, float pPartialTicks) {
        return 0;
    }

    protected float getU1(int tail, float pPartialTicks) {
        return  1 - (tail + pPartialTicks) / (maxTail - 1f);
    }

    protected float getV1(int tail, float pPartialTicks) {
        return 1;
    }

    @Override
    protected final float getU0(float pPartialTicks) {
        return 0;
    }

    @Override
    protected final float getU1(float pPartialTicks) {
        return 0;
    }

    @Override
    protected final float getV0(float pPartialTicks) {
        return 0;
    }

    @Override
    protected final float getV1(float pPartialTicks) {
        return 0;
    }
}
