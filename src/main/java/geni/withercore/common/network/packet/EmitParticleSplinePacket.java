package geni.withercore.common.network.packet;

import geni.withercore.client.particle.ParticleDot;
import geni.withercore.common.helper.ColorHelper;
import geni.withercore.common.helper.SplineHelper;
import geni.witherutils.WitherUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record EmitParticleSplinePacket(ParticleOptions particleOptions,
                                       Vec3 startPos,
                                       Vec3 endPos,
                                       Vec3 startVel,
                                       Vec3 endVel,
                                       int color) implements CustomPacketPayload {

    public static final Type<EmitParticleSplinePacket> TYPE = new Type<>(WitherUtils.loc("emit_spline_particle"));

    public static final StreamCodec<RegistryFriendlyByteBuf, EmitParticleSplinePacket> STREAM_CODEC = StreamCodec.of(
            EmitParticleSplinePacket::toNetwork,
            EmitParticleSplinePacket::fromNetwork
    );

    public static void writeParticle(RegistryFriendlyByteBuf buffer, ParticleOptions particle) {
        ParticleTypes.STREAM_CODEC.encode(buffer, particle);
    }

    public static ParticleOptions readParticle(RegistryFriendlyByteBuf buffer) {
        return ParticleTypes.STREAM_CODEC.decode(buffer);
    }

    public static void writeVec3(RegistryFriendlyByteBuf buffer, Vec3 vec) {
        buffer.writeDouble(vec.x);
        buffer.writeDouble(vec.y);
        buffer.writeDouble(vec.z);
    }

    public static Vec3 readVec3(RegistryFriendlyByteBuf buffer) {
        double x = buffer.readDouble();
        double y = buffer.readDouble();
        double z = buffer.readDouble();
        return new Vec3(x, y, z);
    }

    public static void toNetwork(RegistryFriendlyByteBuf buf, EmitParticleSplinePacket packet) {
        writeParticle(buf, packet.particleOptions);
        writeVec3(buf, packet.startPos);
        writeVec3(buf, packet.endPos);
        writeVec3(buf, packet.startVel);
        writeVec3(buf, packet.endVel);
        buf.writeInt(packet.color);
    }

    public static EmitParticleSplinePacket fromNetwork(RegistryFriendlyByteBuf buf) {
        ParticleOptions particle = readParticle(buf);
        Vec3 startPos = readVec3(buf);
        Vec3 endPos = readVec3(buf);
        Vec3 startVel = readVec3(buf);
        Vec3 endVel = readVec3(buf);
        int color = buf.readInt();
        return new EmitParticleSplinePacket(particle, startPos, endPos, startVel, endVel, color);
    }

    @Override
    public Type<EmitParticleSplinePacket> type() {
        return TYPE;
    }

    public static void handle(EmitParticleSplinePacket message, IPayloadContext ctx) {

        double v = 0.05 / message.endPos.subtract(message.startPos).length();

        double[] xParam = SplineHelper.splineParams(message.startPos.x, message.endPos.x, message.startVel.x, message.endVel.x);
        double[] yParam = SplineHelper.splineParams(message.startPos.y, message.endPos.y, message.startVel.y, message.endVel.y);
        double[] zParam = SplineHelper.splineParams(message.startPos.z, message.endPos.z, message.startVel.z, message.endVel.z);

        for (double h = v; h <= 1; h += v) {

            double x = SplineHelper.evalSpline(h, xParam);
            double y = SplineHelper.evalSpline(h, yParam);
            double z = SplineHelper.evalSpline(h, zParam);

            Minecraft.getInstance().particleEngine.add(
                    new ParticleDot(
                            Minecraft.getInstance().level,
                            x, y, z,
                            (0.8F + Minecraft.getInstance().level.random.nextFloat() * 0.2F) * (float) ColorHelper.unpack(message.color)[0],
                            (0.8F + Minecraft.getInstance().level.random.nextFloat() * 0.2F) * (float) ColorHelper.unpack(message.color)[1],
                            (0.8F + Minecraft.getInstance().level.random.nextFloat() * 0.2F) * (float) ColorHelper.unpack(message.color)[2]
                    )
            );
        }
    }

    public EmitParticleSplinePacket(ParticleOptions particleOptions, Vec3 startPos, Vec3 endPos, Vec3 startVel, Vec3 endVel, int color) {
        this.particleOptions = particleOptions;
        this.startPos = startPos;
        this.endPos = endPos;
        this.startVel = startVel;
        this.endVel = endVel;
        this.color = color;
    }
}
