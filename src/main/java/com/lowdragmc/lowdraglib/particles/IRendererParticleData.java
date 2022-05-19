package com.lowdragmc.lowdraglib.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author KilaBash
 * @date 2022/05/18
 * @implNote IRendererParticleData
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class IRendererParticleData implements IParticleData {
    public static final IParticleData.IDeserializer<IRendererParticleData> DESERIALIZER = new IParticleData.IDeserializer<IRendererParticleData>() {
        public IRendererParticleData fromCommand(ParticleType<IRendererParticleData> pParticleType, StringReader pReader) throws CommandSyntaxException {
            pReader.expect(' ');
            return new IRendererParticleData(pParticleType, BlockPos.ZERO);
        }

        public IRendererParticleData fromNetwork(ParticleType<IRendererParticleData> pParticleType, PacketBuffer pBuffer) {
            return new IRendererParticleData(pParticleType, pBuffer.readBlockPos());
        }
    };

    public static Codec<IRendererParticleData> codec(ParticleType<IRendererParticleData> type) {
        return BlockPos.CODEC.xmap((pos) -> new IRendererParticleData(type, pos), (data) -> data.pos);
    }
    
    private final ParticleType<IRendererParticleData> type;
    private final BlockPos pos;

    public IRendererParticleData(ParticleType<IRendererParticleData> pParticleType, BlockPos pos) {
        type = pParticleType;
        this.pos = pos;
    }

    @Override
    public ParticleType<IRendererParticleData> getType() {
        return type;
    }

    @Override
    public void writeToNetwork(PacketBuffer pBuffer) {
        pBuffer.writeBlockPos(pos);
    }

    @Override
    public String writeToString() {
        return Registry.PARTICLE_TYPE.getKey(this.getType()) + " " + pos.toShortString();
    }

    public BlockPos getPos() {
        return pos;
    }
}
