package net.soko.pyrotechnics.capability.fieriness;

import net.minecraft.util.Mth;


// Taken from cappin's FogLooksGoodNow mod, thanks cappin!
public class InterpolatedValue {
    public float defaultValue;

    private float interpolationSpeed;
    private float previousValue;
    private float currentValue;

    public InterpolatedValue(float defaultValue, float interpolationSpeed) {
        this.defaultValue = defaultValue;
        this.currentValue = defaultValue;
        this.interpolationSpeed = interpolationSpeed;
    }

    public InterpolatedValue(float defaultValue) {
        this(defaultValue, 0.05f);
    }

    public void set(float value) {
        this.previousValue = this.currentValue;
        this.currentValue = value;
    }
    public void set(double value) {
        this.previousValue = this.currentValue;
        this.currentValue = (float) value;
    }

    public void setDefaultValue(float value) {
        this.defaultValue = value;
    }
    public void setDefaultValue(double value) {
        this.defaultValue = (float)value;
    }

    public void interpolate(float value, float interpolationSpeed) {
        this.set(Float.isNaN(value) ? Mth.lerp(interpolationSpeed, currentValue, defaultValue) : Mth.lerp(interpolationSpeed, currentValue, value));
    }
    public void interpolate(double value, float interpolationSpeed) {
        this.set(Double.isNaN(value) ? Mth.lerp(interpolationSpeed, currentValue, defaultValue) : Mth.lerp(interpolationSpeed, currentValue, value));
    }
    public void interpolate(float value) {
        this.interpolate(value, this.interpolationSpeed);
    }
    public void interpolate(double value) {
        this.interpolate(value, this.interpolationSpeed);
    }
    public void interpolate() {
        this.set(Mth.lerp(interpolationSpeed, currentValue, defaultValue));
    }

    public float get(float partialTick) {
        return Mth.lerp(partialTick, previousValue, currentValue);
    }
}