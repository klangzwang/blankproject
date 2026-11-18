package geni.withercore.client.particle;

import geni.withercore.common.helper.SplineHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.Vec3;

public class ParticleSplineDot extends ParticleDot {

	Vec3 startPos;
	Vec3 endPos;
	Vec3 startVel;
	Vec3 endVel;

	double[] xParam;
	double[] yParam;
	double[] zParam;

	public ParticleSplineDot(ClientLevel level, Vec3 startPos, Vec3 endPos, Vec3 startVel, Vec3 endVel, float r, float g, float b, int lifetime)
	{
		super(level, startPos.x, startPos.y, startPos.z, r, g, b);

		this.startPos = startPos;
		this.endPos = endPos;
		this.startVel = startVel;
		this.endVel = endVel;

		xParam = SplineHelper.splineParams(startPos.x, endPos.x, startVel.x, endVel.x);
		yParam = SplineHelper.splineParams(startPos.y, endPos.y, startVel.y, endVel.y);
		zParam = SplineHelper.splineParams(startPos.z, endPos.z, startVel.z, endVel.z);

		this.lifetime = lifetime;
	}

	@Override
	public void move(double x, double y, double z) {
		this.setBoundingBox(this.getBoundingBox().move(x, y, z));
		/*this.resetPositionToBB();*/
	}

	@Override
	public void tick() {
		if (this.age++ >= this.lifetime)
			this.remove();

		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;

		double h = ((double) age) / lifetime;

		x = SplineHelper.evalSpline(h, xParam);
		y = SplineHelper.evalSpline(h, yParam);
		z = SplineHelper.evalSpline(h, zParam);

		xo = SplineHelper.evalSpline(h, xParam) - xo;
		yo = SplineHelper.evalSpline(h, yParam) - yo;
		zo = SplineHelper.evalSpline(h, zParam) - zo;

		move(xd, yd, zd);
	}
}