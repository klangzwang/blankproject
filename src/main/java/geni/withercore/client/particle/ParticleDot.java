package geni.withercore.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle;

public class ParticleDot extends SingleQuadParticle {

	public ParticleDot(ClientLevel level, double posXIn, double posYIn, double posZIn, float r, float g, float b) {
		super(level, posXIn, posYIn, posZIn, 0, 0, 0);
		xd *= 0.05F;
		yd *= 0.05F;
		zd *= 0.05F;
		rCol = r;
		gCol = g;
		bCol = b;
		bbWidth = 2;
		bbHeight = 2;
		this.quadSize = 0;
	}

	@Override
	public void render(VertexConsumer buffer, Camera camera, float partialTicks) {}

	@Override
	protected float getU0() {
		return 0;
	}

	@Override
	protected float getU1() {
		return 0;
	}

	@Override
	protected float getV0() {
		return 0;
	}

	@Override
	protected float getV1() {
		return 0;
	}

	@Override
	public ParticleRenderType getRenderType() {return null;}
}