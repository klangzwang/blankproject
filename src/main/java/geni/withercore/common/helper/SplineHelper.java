package geni.withercore.common.helper;

import geni.witherapi.spline.ConveyorSplineMath;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.OptionalDouble;

public class SplineHelper {

	public static double[] splineParams(double p0, double p1, double d0, double d1) {
		return new double[]{
				2 * p0 - 2 * p1 + d0 + d1,
				-3 * p0 + 3 * p1 - 2 * d0 - d1,
				d0,
				p0
		};
	}

	public static double evalSpline(double t, double[] p) {
		return ((p[0] * t + p[1]) * t + p[2]) * t + p[3];
	}

	/**
	 * Checks whether the target block face exposes a NeoForge IItemHandler block capability
	 * (such as Vanilla chests, machines, or SupportPoles with their locked anchor handler).
	 */
	@Nullable
	public static IItemHandler getItemHandler(Level level, BlockPos pos, @Nullable Direction side) {
		if (!level.isLoaded(pos)) {
			return null;
		}
		IItemHandler sided = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, side);
		if (sided != null) {
			return sided;
		}
		return side != null ? level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null) : null;
	}

	public static boolean hasItemHandler(Level level, BlockPos pos, @Nullable Direction side) {
		return getItemHandler(level, pos, side) != null;
	}

	/**
	 * Computes the shortest ray-distance squared from a look ray (origin + dir * t) to a line segment [a, b],
	 * returning OptionalDouble.empty() if the closest hit exceeds maxRayLength or maxRadius.
	 */
	public static OptionalDouble intersectRayWithSpline(
			Vec3 rayOrigin,
			Vec3 rayDir,
			double maxRayLength,
			ConveyorSplineMath.SplinePath path,
			double hitRadius
	) {
		int samples = Math.max(8, (int) Math.ceil(path.arcLength() * 4.0));
		List<Vec3> pts = path.samplePolyline(samples);
		double bestRayT = Double.MAX_VALUE;
		double hitRadiusSq = hitRadius * hitRadius;

		for (int i = 0; i < pts.size() - 1; i++) {
			Vec3 a = pts.get(i);
			Vec3 b = pts.get(i + 1);
			double[] res = closestRayToSegment(rayOrigin, rayDir, a, b);
			double rayT = res[0];
			double distSq = res[1];
			if (rayT >= 0.0 && rayT <= maxRayLength && distSq <= hitRadiusSq && rayT < bestRayT) {
				bestRayT = rayT;
			}
		}

		return bestRayT < Double.MAX_VALUE ? OptionalDouble.of(bestRayT) : OptionalDouble.empty();
	}

	/**
	 * Returns [rayT, distSq] between ray (origin + dir * rayT, rayT >= 0) and segment [a, b].
	 */
	private static double[] closestRayToSegment(Vec3 rayOrigin, Vec3 rayDir, Vec3 a, Vec3 b) {
		Vec3 u = rayDir;
		Vec3 v = b.subtract(a);
		Vec3 w = rayOrigin.subtract(a);

		double aDot = u.dot(u);
		double bDot = u.dot(v);
		double cDot = v.dot(v);
		double dDot = u.dot(w);
		double eDot = v.dot(w);
		double denom = aDot * cDot - bDot * bDot;

		double sc;
		double tc;

		if (denom < 1e-8) {
			sc = 0.0;
			tc = Math.max(0.0, Math.min(1.0, cDot > 1e-8 ? eDot / cDot : 0.0));
		} else {
			sc = (bDot * eDot - cDot * dDot) / denom;
			tc = (aDot * eDot - bDot * dDot) / denom;
			if (sc < 0.0) {
				sc = 0.0;
				tc = Math.max(0.0, Math.min(1.0, cDot > 1e-8 ? eDot / cDot : 0.0));
			}
			if (tc < 0.0) {
				tc = 0.0;
				sc = Math.max(0.0, -dDot / Math.max(aDot, 1e-8));
			} else if (tc > 1.0) {
				tc = 1.0;
				sc = Math.max(0.0, (-dDot + bDot) / Math.max(aDot, 1e-8));
			}
		}

		Vec3 pRay = rayOrigin.add(u.scale(sc));
		Vec3 pSeg = a.add(v.scale(tc));
		return new double[]{sc, pRay.distanceToSqr(pSeg)};
	}

	public static AABB computeRenderBounds(ConveyorSplineMath.SplinePath path) {
		return path.computeBoundingBox(1.0);
	}

	/**
	 * Orthonormal local coordinate frame along a 3D spline computed via Parallel Transport (Bishop Frames).
	 * Guarantees right-handed basis (right, normal, tangent) with det = +1 and minimal twist.
	 */
	public record ParallelTransportFrame(
			float splineT,
			double hermiteU,
			double arcLength,
			Vec3 position,
			Vec3 tangent,
			Vec3 normal,
			Vec3 right,
			double curvatureAngleRad
	) {}

	/**
	 * Precomputed arc-length parameterized Parallel Transport Table for a 3D Cubic Hermite Spline.
	 */
	public static final class ParallelTransportTable {
		private final List<ParallelTransportFrame> frames;
		private final double totalArcLength;
		private final double minY;
		private final double maxY;
		private final Vec3 boundsMin;
		private final Vec3 boundsMax;

		private ParallelTransportTable(
				List<ParallelTransportFrame> frames,
				double totalArcLength,
				double minY,
				double maxY,
				Vec3 boundsMin,
				Vec3 boundsMax
		) {
			this.frames = Collections.unmodifiableList(frames);
			this.totalArcLength = totalArcLength;
			this.minY = minY;
			this.maxY = maxY;
			this.boundsMin = boundsMin;
			this.boundsMax = boundsMax;
		}

		public List<ParallelTransportFrame> getFrames() {
			return frames;
		}

		public double getTotalArcLength() {
			return totalArcLength;
		}

		public double getMinY() {
			return minY;
		}

		public double getMaxY() {
			return maxY;
		}

		public Vec3 getBoundsMin() {
			return boundsMin;
		}

		public Vec3 getBoundsMax() {
			return boundsMax;
		}

		/**
		 * Samples an interpolated Parallel Transport Frame by normalized arc length float splineT in [0, 1].
		 */
		public ParallelTransportFrame sampleAtSplineT(float splineT) {
			if (frames.isEmpty()) {
				return new ParallelTransportFrame(0f, 0, 0, Vec3.ZERO, new Vec3(0, 0, 1), new Vec3(0, 1, 0), new Vec3(1, 0, 0), 0);
			}
			float clamped = Math.max(0.0f, Math.min(1.0f, splineT));
			if (clamped <= 0.0f || frames.size() == 1) {
				return frames.getFirst();
			}
			if (clamped >= 1.0f) {
				return frames.getLast();
			}

			double targetDist = clamped * totalArcLength;
			int lo = 0;
			int hi = frames.size() - 1;
			while (lo + 1 < hi) {
				int mid = (lo + hi) >>> 1;
				if (frames.get(mid).arcLength() <= targetDist) {
					lo = mid;
				} else {
					hi = mid;
				}
			}

			ParallelTransportFrame f0 = frames.get(lo);
			ParallelTransportFrame f1 = frames.get(hi);
			double span = f1.arcLength() - f0.arcLength();
			double alpha = span > 1.0E-6 ? (targetDist - f0.arcLength()) / span : 0.0;

			Vec3 pos = f0.position().lerp(f1.position(), alpha);
			Vec3 tan = slerpOrSafeLerp(f0.tangent(), f1.tangent(), alpha);
			Vec3 right = orthonormalizeRight(tan, f0.right().lerp(f1.right(), alpha));
			Vec3 normal = tan.cross(right).normalize();
			if (right.cross(normal).dot(tan) < 0) {
				right = right.scale(-1.0);
			}

			double u = f0.hermiteU() + (f1.hermiteU() - f0.hermiteU()) * alpha;
			double curv = Math.max(f0.curvatureAngleRad(), f1.curvatureAngleRad());

			return new ParallelTransportFrame(clamped, u, targetDist, pos, tan, normal, right, curv);
		}

		/**
		 * Projects a world ray onto the spline centerline and returns the closest normalized arc length splineT,
		 * or -1f if the ray is farther away than maxDistanceBlocks.
		 */
		public float raycastClosestSplineT(Vec3 rayOrigin, Vec3 rayDirNormalized, double maxRayDistance, double maxTubeRadius) {
			if (frames.size() < 2) {
				return -1.0f;
			}
			double bestDistToRaySq = maxTubeRadius * maxTubeRadius;
			double bestAlongRay = Double.MAX_VALUE;
			float bestSplineT = -1.0f;

			for (int i = 0; i < frames.size() - 1; i++) {
				ParallelTransportFrame a = frames.get(i);
				ParallelTransportFrame b = frames.get(i + 1);

				ClosestSegmentRayResult res = closestBetweenRayAndSegment(rayOrigin, rayDirNormalized, a.position(), b.position());
				if (res.rayDist() < 0.0 || res.rayDist() > maxRayDistance) {
					continue;
				}
				if (res.distSq() <= bestDistToRaySq && (res.rayDist() < bestAlongRay + 0.15 || res.distSq() < bestDistToRaySq * 0.5)) {
					bestDistToRaySq = res.distSq();
					bestAlongRay = res.rayDist();
					bestSplineT = (float) (a.splineT() + (b.splineT() - a.splineT()) * res.segT());
				}
			}
			return bestSplineT;
		}

		/**
		 * Evaluates whether the region around splineT [splineT - halfSpanT, splineT + halfSpanT]
		 * is sufficiently straight for mounting a rigid inline pump.
		 */
		public boolean isStraightSectionAt(float splineT, double pumpLengthMeters, double maxBendDegrees) {
			if (totalArcLength < pumpLengthMeters + 0.25) {
				return false;
			}
			float halfSpanT = (float) ((pumpLengthMeters * 0.5) / totalArcLength);
			float tStart = splineT - halfSpanT;
			float tEnd = splineT + halfSpanT;
			if (tStart < 0.0f || tEnd > 1.0f) {
				return false;
			}

			ParallelTransportFrame startFrame = sampleAtSplineT(tStart);
			ParallelTransportFrame midFrame = sampleAtSplineT(splineT);
			ParallelTransportFrame endFrame = sampleAtSplineT(tEnd);

			double maxBendCos = Math.cos(Math.toRadians(maxBendDegrees));
			if (startFrame.tangent().dot(endFrame.tangent()) < maxBendCos) {
				return false;
			}
			if (startFrame.tangent().dot(midFrame.tangent()) < maxBendCos
					|| midFrame.tangent().dot(endFrame.tangent()) < maxBendCos) {
				return false;
			}

			for (ParallelTransportFrame frame : frames) {
				if (frame.splineT() >= tStart && frame.splineT() <= tEnd) {
					if (frame.tangent().dot(midFrame.tangent()) < maxBendCos) {
						return false;
					}
				}
			}
			return true;
		}
	}

	public static Vec3 evalHermitePoint(Vec3 p0, Vec3 m0, Vec3 p1, Vec3 m1, double u) {
		double u2 = u * u;
		double u3 = u2 * u;
		double h00 = 2 * u3 - 3 * u2 + 1;
		double h10 = u3 - 2 * u2 + u;
		double h01 = -2 * u3 + 3 * u2;
		double h11 = u3 - u2;
		return p0.scale(h00).add(m0.scale(h10)).add(p1.scale(h01)).add(m1.scale(h11));
	}

	public static Vec3 evalHermiteDerivative(Vec3 p0, Vec3 m0, Vec3 p1, Vec3 m1, double u) {
		double u2 = u * u;
		double dh00 = 6 * u2 - 6 * u;
		double dh10 = 3 * u2 - 4 * u + 1;
		double dh01 = -6 * u2 + 6 * u;
		double dh11 = 3 * u2 - 2 * u;
		return p0.scale(dh00).add(m0.scale(dh10)).add(p1.scale(dh01)).add(m1.scale(dh11));
	}

	/**
	 * Computes tangent handle scale matching Satisfactory / conveyor spline smoothness (~0.45 * distance, clamped).
	 */
	public static double computeHandleMagnitude(Vec3 p0, Vec3 p1) {
		double dist = p0.distanceTo(p1);
		return Math.max(0.75, Math.min(dist * 0.55, 12.0));
	}

	/**
	 * Builds an orthogonal (Manhattan / 90-degree) Parallel Transport Frame table for fluid pipes.
	 * Straight sections strictly follow Cartesian axes (X, Y, Z); 90-degree corners are formed
	 * with compact elbow pieces supporting 2x2 bends (R=1.0) and 1x1 bends.
	 */
	public static ParallelTransportTable buildParallelTransportTable(Vec3 p0, Vec3 dir0, Vec3 p1, Vec3 dir1, int sampleCount) {
		return buildOrthogonalTransportTable(p0, dir0, p1, dir1);
	}

	public static ParallelTransportTable buildOrthogonalTransportTable(Vec3 p0, Vec3 dir0, Vec3 p1, Vec3 dir1) {
		List<Vec3> waypoints = computeOrthogonalWaypoints(p0, dir0, p1, dir1);
		int n = waypoints.size();

		if (n <= 1) {
			return buildStraightTransportTable(p0, p1, snapToDominantAxis(dir0));
		}
		if (n == 2) {
			Vec3 diff = waypoints.get(1).subtract(waypoints.get(0));
			Vec3 tan = diff.lengthSqr() > 1e-6 ? diff.normalize() : snapToDominantAxis(dir0);
			return buildStraightTransportTable(waypoints.get(0), waypoints.get(1), tan);
		}

		// Calculate 90-degree corner elbow radii and anchor points
		// Radius is 1.0 for a 2x2 bend whenever space permits, or scales down to tight 1x1 elbow
		int cornerCount = n - 2;
		double[] radii = new double[cornerCount];
		Vec3[] cornerStarts = new Vec3[cornerCount];
		Vec3[] cornerEnds = new Vec3[cornerCount];
		Vec3[] cornerCenters = new Vec3[cornerCount];

		for (int c = 0; c < cornerCount; c++) {
			int wi = c + 1;
			Vec3 Wprev = waypoints.get(wi - 1);
			Vec3 Wcurr = waypoints.get(wi);
			Vec3 Wnext = waypoints.get(wi + 1);

			Vec3 Uin = Wcurr.subtract(Wprev).normalize();
			Vec3 Uout = Wnext.subtract(Wcurr).normalize();

			double Lin = Wcurr.distanceTo(Wprev);
			double Lout = Wnext.distanceTo(Wcurr);

			double maxR = Math.min(Lin, Lout) * 0.48;
			double R = maxR >= 1.0 ? 1.0 : Math.max(0.25, maxR);
			radii[c] = R;

			Vec3 A = Wcurr.subtract(Uin.scale(R));
			Vec3 B = Wcurr.add(Uout.scale(R));
			Vec3 C = A.add(Uout.scale(R));

			cornerStarts[c] = A;
			cornerEnds[c] = B;
			cornerCenters[c] = C;
		}

		// Initial reference frame
		Vec3 t0 = waypoints.get(1).subtract(waypoints.get(0)).normalize();
		Vec3 refUp = Math.abs(t0.y) < 0.94 ? new Vec3(0, 1, 0) : new Vec3(1, 0, 0);
		Vec3 right0 = refUp.cross(t0).normalize();
		Vec3 normal0 = t0.cross(right0).normalize();

		List<Vec3> samplePositions = new ArrayList<>();
		List<Vec3> sampleTangents = new ArrayList<>();
		List<Double> sampleBendAngles = new ArrayList<>();

		samplePositions.add(waypoints.get(0));
		sampleTangents.add(t0);
		sampleBendAngles.add(0.0);

		// Generate samples along alternating straight segments and elbow arcs
		for (int i = 0; i < n - 1; i++) {
			Vec3 segStart = (i == 0) ? waypoints.get(0) : cornerEnds[i - 1];
			Vec3 segEnd = (i == n - 2) ? waypoints.get(n - 1) : cornerStarts[i];
			Vec3 segDir = waypoints.get(i + 1).subtract(waypoints.get(i)).normalize();

			double segLen = segEnd.distanceTo(segStart);
			int straightSteps = Math.max(1, (int) Math.ceil(segLen / 0.5));

			for (int s = 1; s <= straightSteps; s++) {
				double u = (double) s / (double) straightSteps;
				samplePositions.add(segStart.lerp(segEnd, u));
				sampleTangents.add(segDir);
				sampleBendAngles.add(0.0);
			}

			// Add 90-degree circular elbow arc at waypoint i+1
			if (i < n - 2) {
				double R = radii[i];
				Vec3 C = cornerCenters[i];
				Vec3 Uin = segDir;
				Vec3 Uout = waypoints.get(i + 2).subtract(waypoints.get(i + 1)).normalize();

				int arcSteps = R >= 0.9 ? 8 : 6;
				double deltaTheta = (Math.PI * 0.5) / arcSteps;

				for (int k = 1; k <= arcSteps; k++) {
					double phi = (Math.PI * 0.5) * ((double) k / (double) arcSteps);
					double cosPhi = Math.cos(phi);
					double sinPhi = Math.sin(phi);

					Vec3 pt = C.subtract(Uout.scale(R * cosPhi)).add(Uin.scale(R * sinPhi));
					Vec3 tan = Uout.scale(sinPhi).add(Uin.scale(cosPhi)).normalize();

					samplePositions.add(pt);
					sampleTangents.add(tan);
					sampleBendAngles.add(deltaTheta);
				}
			}
		}

		int totalSamples = samplePositions.size();
		double[] arcLengths = new double[totalSamples];
		arcLengths[0] = 0.0;
		double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, minZ = Double.MAX_VALUE;
		double maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE, maxZ = -Double.MAX_VALUE;

		for (int i = 0; i < totalSamples; i++) {
			Vec3 pos = samplePositions.get(i);
			if (i > 0) {
				arcLengths[i] = arcLengths[i - 1] + pos.distanceTo(samplePositions.get(i - 1));
			}
			minX = Math.min(minX, pos.x);
			minY = Math.min(minY, pos.y);
			minZ = Math.min(minZ, pos.z);
			maxX = Math.max(maxX, pos.x);
			maxY = Math.max(maxY, pos.y);
			maxZ = Math.max(maxZ, pos.z);
		}

		double totalLength = Math.max(1e-4, arcLengths[totalSamples - 1]);
		List<ParallelTransportFrame> frames = new ArrayList<>(totalSamples);
		Vec3 currentRight = right0;
		Vec3 currentNormal = normal0;

		for (int i = 0; i < totalSamples; i++) {
			Vec3 tan = sampleTangents.get(i);
			double bendAngle = sampleBendAngles.get(i);

			if (i > 0 && bendAngle > 1e-6) {
				Vec3 prevTan = sampleTangents.get(i - 1);
				Vec3 axis = prevTan.cross(tan);
				double axisLen = axis.length();
				if (axisLen > 1e-7) {
					axis = axis.scale(1.0 / axisLen);
					currentRight = rotateVectorAroundAxis(currentRight, axis, bendAngle);
				}
			}
			currentRight = orthonormalizeRight(tan, currentRight);
			currentNormal = tan.cross(currentRight).normalize();

			float splineT = (float) (arcLengths[i] / totalLength);
			if (i == 0) splineT = 0.0f;
			if (i == totalSamples - 1) splineT = 1.0f;

			frames.add(new ParallelTransportFrame(
					splineT,
					(double) i / (double) (totalSamples - 1),
					arcLengths[i],
					samplePositions.get(i),
					tan,
					currentNormal,
					currentRight,
					bendAngle
			));
		}

		return new ParallelTransportTable(
				frames,
				totalLength,
				minY,
				maxY,
				new Vec3(minX, minY, minZ),
				new Vec3(maxX, maxY, maxZ)
		);
	}

	private static ParallelTransportTable buildStraightTransportTable(Vec3 p0, Vec3 p1, Vec3 tan) {
		double len = Math.max(1e-4, p0.distanceTo(p1));
		int steps = Math.max(4, (int) Math.ceil(len / 0.5));

		Vec3 refUp = Math.abs(tan.y) < 0.94 ? new Vec3(0, 1, 0) : new Vec3(1, 0, 0);
		Vec3 right = refUp.cross(tan).normalize();
		Vec3 normal = tan.cross(right).normalize();

		List<ParallelTransportFrame> frames = new ArrayList<>(steps + 1);
		double minX = Math.min(p0.x, p1.x), maxX = Math.max(p0.x, p1.x);
		double minY = Math.min(p0.y, p1.y), maxY = Math.max(p0.y, p1.y);
		double minZ = Math.min(p0.z, p1.z), maxZ = Math.max(p0.z, p1.z);

		for (int i = 0; i <= steps; i++) {
			double u = (double) i / (double) steps;
			Vec3 pos = p0.lerp(p1, u);
			float splineT = (float) u;
			frames.add(new ParallelTransportFrame(
					splineT, u, u * len, pos, tan, normal, right, 0.0
			));
		}
		return new ParallelTransportTable(frames, len, minY, maxY, new Vec3(minX, minY, minZ), new Vec3(maxX, maxY, maxZ));
	}

	public static List<Vec3> computeOrthogonalWaypoints(Vec3 p0, Vec3 dir0, Vec3 p1, Vec3 dir1) {
		Vec3 d0 = snapToDominantAxis(dir0);
		Vec3 d1 = snapToDominantAxis(dir1);

		Vec3 diff = p1.subtract(p0);
		double totalDist = diff.length();
		if (totalDist < 1e-4) {
			return List.of(p0, p1);
		}

		if (d0.dot(d1) > 0.999 && diff.normalize().dot(d0) > 0.999) {
			return List.of(p0, p1);
		}

		List<Vec3> rawWaypoints = new ArrayList<>();
		rawWaypoints.add(p0);

		int axis0 = getAxisIndex(d0);
		int axis1 = getAxisIndex(d1);

		double minLead = 0.75;
		double coord0Target = getCoordinate(p1, axis0);
		double coord0Start = getCoordinate(p0, axis0);
		double dir0Sign = getCoordinate(d0, axis0);

		double curX = p0.x, curY = p0.y, curZ = p0.z;

		if (axis0 == axis1) {
			// Start and end are along the same axis
			double midCoord;
			if ((coord0Target - coord0Start) * dir0Sign > 2.0 * minLead) {
				midCoord = (coord0Start + coord0Target) * 0.5;
			} else {
				midCoord = coord0Start + dir0Sign * minLead;
			}
			curX = (axis0 == 0) ? midCoord : curX;
			curY = (axis0 == 1) ? midCoord : curY;
			curZ = (axis0 == 2) ? midCoord : curZ;
			rawWaypoints.add(new Vec3(curX, curY, curZ));

			// First jog axis (prefer horizontal Z or X, then Y)
			int jog1 = (axis0 == 1) ? 0 : (axis0 == 0 ? 2 : 0);
			int jog2 = 3 - axis0 - jog1;

			curX = (jog1 == 0) ? p1.x : curX;
			curY = (jog1 == 1) ? p1.y : curY;
			curZ = (jog1 == 2) ? p1.z : curZ;
			rawWaypoints.add(new Vec3(curX, curY, curZ));

			curX = (jog2 == 0) ? p1.x : curX;
			curY = (jog2 == 1) ? p1.y : curY;
			curZ = (jog2 == 2) ? p1.z : curZ;
			rawWaypoints.add(new Vec3(curX, curY, curZ));

			rawWaypoints.add(p1);
		} else {
			// axis0 != axis1
			int midAxis = 3 - axis0 - axis1;

			if ((coord0Target - coord0Start) * dir0Sign >= minLead) {
				curX = (axis0 == 0) ? p1.x : curX;
				curY = (axis0 == 1) ? p1.y : curY;
				curZ = (axis0 == 2) ? p1.z : curZ;
				rawWaypoints.add(new Vec3(curX, curY, curZ));
			} else {
				double stepOut = coord0Start + dir0Sign * minLead;
				curX = (axis0 == 0) ? stepOut : curX;
				curY = (axis0 == 1) ? stepOut : curY;
				curZ = (axis0 == 2) ? stepOut : curZ;
				rawWaypoints.add(new Vec3(curX, curY, curZ));
			}

			curX = (midAxis == 0) ? p1.x : curX;
			curY = (midAxis == 1) ? p1.y : curY;
			curZ = (midAxis == 2) ? p1.z : curZ;
			rawWaypoints.add(new Vec3(curX, curY, curZ));

			rawWaypoints.add(p1);
		}

		return simplifyWaypoints(rawWaypoints);
	}

	private static List<Vec3> simplifyWaypoints(List<Vec3> raw) {
		if (raw.size() <= 2) {
			return raw;
		}
		List<Vec3> noDups = new ArrayList<>();
		for (Vec3 pt : raw) {
			if (noDups.isEmpty() || noDups.get(noDups.size() - 1).distanceToSqr(pt) > 1e-6) {
				noDups.add(pt);
			}
		}
		if (noDups.size() <= 2) {
			return noDups;
		}

		List<Vec3> simplified = new ArrayList<>();
		simplified.add(noDups.get(0));

		for (int i = 1; i < noDups.size() - 1; i++) {
			Vec3 prev = simplified.get(simplified.size() - 1);
			Vec3 curr = noDups.get(i);
			Vec3 next = noDups.get(i + 1);

			Vec3 dirIn = curr.subtract(prev).normalize();
			Vec3 dirOut = next.subtract(curr).normalize();

			if (dirIn.dot(dirOut) < 0.999) {
				simplified.add(curr);
			}
		}
		simplified.add(noDups.get(noDups.size() - 1));
		return simplified;
	}

	public static Vec3 snapToDominantAxis(Vec3 v) {
		if (v == null || v.lengthSqr() < 1e-6) {
			return new Vec3(0, 0, 1);
		}
		double ax = Math.abs(v.x);
		double ay = Math.abs(v.y);
		double az = Math.abs(v.z);
		if (ax >= ay && ax >= az) {
			return new Vec3(Math.signum(v.x), 0, 0);
		} else if (ay >= ax && ay >= az) {
			return new Vec3(0, Math.signum(v.y), 0);
		} else {
			return new Vec3(0, 0, Math.signum(v.z));
		}
	}

	public static int getAxisIndex(Vec3 v) {
		if (Math.abs(v.x) > 0.5) return 0;
		if (Math.abs(v.y) > 0.5) return 1;
		return 2;
	}

	private static double getCoordinate(Vec3 v, int axis) {
		return axis == 0 ? v.x : (axis == 1 ? v.y : v.z);
	}

	private static Vec3 rotateVectorAroundAxis(Vec3 v, Vec3 k, double theta) {
		double cos = Math.cos(theta);
		double sin = Math.sin(theta);
		// Rodrigues' rotation formula: v*cos + (k x v)*sin + k*(k.v)*(1-cos)
		return v.scale(cos)
				.add(k.cross(v).scale(sin))
				.add(k.scale(k.dot(v) * (1.0 - cos)));
	}

	private static Vec3 orthonormalizeRight(Vec3 tangent, Vec3 candidateRight) {
		Vec3 proj = candidateRight.subtract(tangent.scale(candidateRight.dot(tangent)));
		if (proj.lengthSqr() < 1.0E-8) {
			Vec3 fallback = Math.abs(tangent.y) < 0.9 ? new Vec3(0, 1, 0) : new Vec3(1, 0, 0);
			return fallback.cross(tangent).normalize();
		}
		return proj.normalize();
	}

	private static Vec3 slerpOrSafeLerp(Vec3 a, Vec3 b, double t) {
		Vec3 mixed = a.lerp(b, t);
		return mixed.lengthSqr() > 1.0E-8 ? mixed.normalize() : a;
	}

	private record ClosestSegmentRayResult(double distSq, double rayDist, double segT) {}

	private static ClosestSegmentRayResult closestBetweenRayAndSegment(Vec3 rayOrig, Vec3 rayDir, Vec3 segA, Vec3 segB) {
		Vec3 u = rayDir;
		Vec3 v = segB.subtract(segA);
		Vec3 w = rayOrig.subtract(segA);

		double a = u.dot(u); // 1.0
		double b = u.dot(v);
		double c = v.dot(v);
		double d = u.dot(w);
		double e = v.dot(w);
		double denom = a * c - b * b;

		double sc, tc;
		if (denom < 1.0E-7) {
			sc = Math.max(0.0, -d / a);
			tc = 0.0;
		} else {
			sc = (b * e - c * d) / denom;
			tc = (a * e - b * d) / denom;
		}

		if (tc < 0.0) {
			tc = 0.0;
			sc = Math.max(0.0, -d / a);
		} else if (tc > 1.0) {
			tc = 1.0;
			sc = Math.max(0.0, (b - d) / a);
		} else if (sc < 0.0) {
			sc = 0.0;
			tc = c > 1.0E-7 ? Math.max(0.0, Math.min(1.0, e / c)) : 0.0;
		}

		Vec3 closestRay = rayOrig.add(u.scale(sc));
		Vec3 closestSeg = segA.add(v.scale(tc));
		return new ClosestSegmentRayResult(closestRay.distanceToSqr(closestSeg), sc, tc);
	}
}
