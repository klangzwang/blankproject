package geni.withercore.math;

import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * Mathematische Catmull-Rom-Spline Implementierung mit Arc-Length Parameterization.
 * Garantiert $C^1$-Stetigkeit und konstante Bewegungsgeschwindigkeit.
 */
public class CatmullRomSpline3D {

    private final List<Vec3> controlPoints;
    private final boolean closedLoop;
    
    // Bogenlängen-Lookup-Tabelle (LUT)
    private final List<SamplePoint> samples = new ArrayList<>();
    private double totalLength = 0.0;

    public record SamplePoint(double cumulativeDistance, int segmentIndex, double localT, Vec3 position) {}

    public CatmullRomSpline3D(List<Vec3> points, boolean closedLoop, int samplesPerSegment) {
        this.controlPoints = new ArrayList<>(points);
        this.closedLoop = closedLoop;

        if (this.controlPoints.size() >= 2) {
            buildArcLengthTable(Math.max(4, samplesPerSegment));
        }
    }

    /**
     * Baut die Bogenlängen-Lookup-Tabelle für Bogenlängen-Parametrisierung auf.
     */
    private void buildArcLengthTable(int samplesPerSegment) {
        samples.clear();
        totalLength = 0.0;

        int numSegments = getSegmentCount();
        Vec3 lastPos = getPointOnSegment(0, 0.0);
        samples.add(new SamplePoint(0.0, 0, 0.0, lastPos));

        for (int i = 0; i < numSegments; i++) {
            for (int step = 1; step <= samplesPerSegment; step++) {
                double t = (double) step / samplesPerSegment;
                Vec3 currentPos = getPointOnSegment(i, t);
                
                double dist = lastPos.distanceTo(currentPos);
                totalLength += dist;

                samples.add(new SamplePoint(totalLength, i, t, currentPos));
                lastPos = currentPos;
            }
        }
    }

    public int getSegmentCount() {
        if (controlPoints.size() < 2) return 0;
        return closedLoop ? controlPoints.size() : controlPoints.size() - 1;
    }

    public double getTotalLength() {
        return totalLength;
    }

    /**
     * Ruft die genaue 3D-Position bei einer bestimmten zurückgelegten Bogenlänge (in Blöcken) ab.
     */
    public Vec3 getPointAtDistance(double distance) {
        if (samples.isEmpty()) return Vec3.ZERO;

        // Loop / Ping-Pong-Handling für Distanz-Überlauf
        if (closedLoop) {
            distance = distance % totalLength;
            if (distance < 0) distance += totalLength;
        } else {
            if (distance <= 0) return samples.get(0).position();
            if (distance >= totalLength) return samples.get(samples.size() - 1).position();
        }

        // Binäre Suche für schnellen Zugriff O(log N)
        int low = 0;
        int high = samples.size() - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            SamplePoint midSample = samples.get(mid);

            if (midSample.cumulativeDistance() < distance) {
                low = mid + 1;
            } else if (midSample.cumulativeDistance() > distance) {
                high = mid - 1;
            } else {
                return midSample.position();
            }
        }

        // Lineare Interpolation zwischen den zwei naheliegendsten LUT-Einträgen
        SamplePoint prev = samples.get(Math.max(0, high));
        SamplePoint next = samples.get(Math.min(samples.size() - 1, low));

        double segmentLength = next.cumulativeDistance() - prev.cumulativeDistance();
        if (segmentLength <= 0.00001) return prev.position();

        double factor = (distance - prev.cumulativeDistance()) / segmentLength;
        return prev.position().lerp(next.position(), factor);
    }

    /**
     * Berechnet den Tangentenvektor (Blickrichtung/Facing) an einer bestimmten Distanz auf der Kurve.
     */
    public Vec3 getTangentAtDistance(double distance) {
        double delta = 0.05;
        Vec3 p1 = getPointAtDistance(distance - delta);
        Vec3 p2 = getPointAtDistance(distance + delta);
        return p2.subtract(p1).normalize();
    }

    /**
     * Kern-Interpolation für ein einzelnes Catmull-Rom Segment.
     */
    public Vec3 getPointOnSegment(int segmentIndex, double t) {
        int numPoints = controlPoints.size();
        if (numPoints < 2) return Vec3.ZERO;

        Vec3 p0, p1, p2, p3;

        if (closedLoop) {
            p0 = controlPoints.get((segmentIndex - 1 + numPoints) % numPoints);
            p1 = controlPoints.get(segmentIndex % numPoints);
            p2 = controlPoints.get((segmentIndex + 1) % numPoints);
            p3 = controlPoints.get((segmentIndex + 2) % numPoints);
        } else {
            p1 = controlPoints.get(segmentIndex);
            p2 = controlPoints.get(segmentIndex + 1);

            // Virtuelle Endpunkte für offene Splines extrapolieren
            p0 = (segmentIndex == 0) 
                    ? p1.subtract(p2.subtract(p1)) 
                    : controlPoints.get(segmentIndex - 1);
            p3 = (segmentIndex == numPoints - 2) 
                    ? p2.add(p2.subtract(p1)) 
                    : controlPoints.get(segmentIndex + 2);
        }

        return calculateCatmullRom(p0, p1, p2, p3, t);
    }

    private static Vec3 calculateCatmullRom(Vec3 p0, Vec3 p1, Vec3 p2, Vec3 p3, double t) {
        double t2 = t * t;
        double t3 = t2 * t;

        double f0 = -0.5 * t3 + t2 - 0.5 * t;
        double f1 = 1.5 * t3 - 2.5 * t2 + 1.0;
        double f2 = -1.5 * t3 + 2.0 * t2 + 0.5 * t;
        double f3 = 0.5 * t3 - 0.5 * t2;

        return new Vec3(
            p0.x * f0 + p1.x * f1 + p2.x * f2 + p3.x * f3,
            p0.y * f0 + p1.y * f1 + p2.y * f2 + p3.y * f3,
            p0.z * f0 + p1.z * f1 + p2.z * f2 + p3.z * f3
        );
    }
}