package geni.witherapi.gui;

/**
 * Interface for GUI widgets that require per-tick updates
 * (animations, timers, transitions).
 */
public interface ITickableWidget {

    void tickWidget();
}
