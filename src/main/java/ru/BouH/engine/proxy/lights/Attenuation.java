package ru.BouH.engine.proxy.lights;

public class Attenuation {
    private float constant;
    private float exponent;
    private float linear;

    public Attenuation(float constant, float linear, float exponent) {
        this.constant = constant;
        this.linear = linear;
        this.exponent = exponent;
    }

    public float getConstant() {
        return this.constant;
    }

    public float getExponent() {
        return this.exponent;
    }

    public float getLinear() {
        return this.linear;
    }

    public void setConstant(float constant) {
        this.constant = constant;
    }

    public void setExponent(float exponent) {
        this.exponent = exponent;
    }

    public void setLinear(float linear) {
        this.linear = linear;
    }
}
