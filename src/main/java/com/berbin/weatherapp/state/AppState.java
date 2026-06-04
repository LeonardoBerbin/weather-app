package com.berbin.weatherapp.state;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Central application state holder.
 *
 * This class stores reactive global state for the application,
 * such as the selected unit system (Metric / Imperial).
 *
 * It uses JavaFX properties to allow UI components to observe
 * changes automatically and react when state updates.
 *
 * This avoids manual refresh calls in controllers.
 */
public class AppState {

    /**
     * Reactive property holding the current unit system.
     *
     * Default value is METRIC (Celsius, m/s).
     */
    private final ObjectProperty<UnitSystem> unit =
        new SimpleObjectProperty<>(UnitSystem.METRIC);

    /**
     * Toggles between METRIC and IMPERIAL unit systems.
     *
     * METRIC → IMPERIAL
     * IMPERIAL → METRIC
     *
     * This change will automatically notify all listeners
     * bound to unitProperty().
     */
    public void toggleUnit() {
        if (unit.get() == UnitSystem.METRIC) {
            unit.set(UnitSystem.IMPERIAL);
        } else {
            unit.set(UnitSystem.METRIC);
        }
    }

    /**
     * Returns the current unit system value.
     *
     * @return current UnitSystem
     */
    public UnitSystem getUnit() {
        return unit.get();
    }

    /**
     * Exposes the JavaFX property for binding/listening.
     *
     * Useful for:
     * - UI auto-refresh
     * - reactive updates in controllers
     * - avoiding manual render calls
     *
     * @return observable unit system property
     */
    public ObjectProperty<UnitSystem> unitProperty() {
        return unit;
    }
}