package geni.withercore.common.util;

import geni.witherapi.hydro.PenstockAnalysis;
import net.minecraft.core.BlockPos;

/**
 * Mathematischer Rechner für Penstock-Druckrohre und Wasserkraft-Physik.
 * Implementiert exakt das 45°-Optimum und die Grenzfälle (0 kW bei 0° waagerecht & 90° senkrecht).
 */
public final class HydroPhysicsCalculator {

    public static final double OPTIMAL_ANGLE_DEG = 45.0;
    public static final double BASE_FLOW_RATE = 10.0; // Liter bzw. Millibuckets / tick
    public static final double HEIGHT_COEFFICIENT = 0.25;
    public static final double LENGTH_COEFFICIENT = 0.04;
    public static final double POWER_FE_COEFFICIENT = 4.2; // FE pro (Flow * DeltaH)

    private HydroPhysicsCalculator() {}

    /**
     * Berechnet die Winkeleffizienz eines Rohrsegments im Bereich [0.0, 1.0].
     */
    public static double calculateAngleEfficiency(double angleDeg, boolean isUphill) {
        if (isUphill) {
            return 0.0;
        }

        double clamped = Math.max(0.0, Math.min(90.0, angleDeg));
        if (clamped <= 0.001 || clamped >= 89.999) {
            return 0.0;
        }

        double deviation = Math.abs(clamped - OPTIMAL_ANGLE_DEG);
        return Math.max(0.0, 1.0 - (deviation / OPTIMAL_ANGLE_DEG));
    }

    /**
     * Führt die vollständige physikalische Analyse zwischen Speicherblock und Turbine durch.
     */
    public static PenstockAnalysis analyzeConnection(BlockPos source, BlockPos target) {
        if (source == null || target == null || source.equals(target)) {
            return new PenstockAnalysis(
                    source != null ? source : BlockPos.ZERO,
                    target != null ? target : BlockPos.ZERO,
                    0, 0, 0, 0, 0, 0, false,
                    "Ungültige Verbindungskoordinaten."
            );
        }

        // Höhendifferenz: Quelle muss höher liegen als Turbine
        double dy = source.getY() - target.getY();
        if (dy <= 0) {
            return new PenstockAnalysis(
                    source, target,
                    Math.max(0, dy), 0, 0, 0, 0, 0, false,
                    dy == 0
                            ? "Kein Gefälle: Speicher und Turbine liegen auf gleicher Höhe (0 FE)."
                            : "Negatives Gefälle: Die Turbine liegt höher als der Speicherblock!"
            );
        }

        double dx = target.getX() - source.getX();
        double dz = target.getZ() - source.getZ();
        double horizontalDist = Math.sqrt(dx * dx + dz * dz);
        double totalLength = Math.sqrt(dx * dx + dy * dy + dz * dz);

        // Neigungswinkel zur Horizontalen
        double angleRad;
        if (horizontalDist <= 0.0001) {
            angleRad = Math.PI / 2.0; // 90° senkrecht
        } else {
            angleRad = Math.atan(dy / horizontalDist);
        }

        double angleDeg = Math.toDegrees(angleRad);
        double efficiency = calculateAngleEfficiency(angleDeg, false);

        if (efficiency <= 0.0001) {
            return new PenstockAnalysis(
                    source, target, dy, totalLength, angleDeg, 0.0, 0.0, 0, false,
                    angleDeg >= 89.9
                            ? "Senkrechter Sturz (90°): 0 % Effizienz durch fehlenden Impulsvektor."
                            : "Waagerechter Verlauf (0°): 0 % Effizienz."
            );
        }

        // Durchfluss: Q = BASE * (DeltaH * k_h) * (1 + Length * k_l)
        double heightFactor = dy * HEIGHT_COEFFICIENT;
        double lengthFactor = 1.0 + (totalLength * LENGTH_COEFFICIENT);
        double flowRate = BASE_FLOW_RATE * heightFactor * lengthFactor;

        // Leistung in FE/t
        int powerFE = (int) Math.round(flowRate * efficiency * dy * POWER_FE_COEFFICIENT);

        return new PenstockAnalysis(
                source,
                target,
                Math.round(dy * 100.0) / 100.0,
                Math.round(totalLength * 100.0) / 100.0,
                Math.round(angleDeg * 10.0) / 10.0,
                Math.round(efficiency * 1000.0) / 1000.0,
                Math.round(flowRate * 10.0) / 10.0,
                powerFE,
                true,
                String.format("Optimal! Gefälle %.1fm bei %.1f° (Effizienz: %.0f %%) -> %d FE/t",
                        dy, angleDeg, efficiency * 100.0, powerFE)
        );
    }
}
