package ru.BouH.engine.math;

import org.joml.Vector3d;

import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

public class BPVector3f extends Vector3f {
    public BPVector3f(float var1, float var2, float var3) {
        super(var1, var2, var3);
    }

    public BPVector3f(float var1) {
        super(var1, var1, var1);
    }

    public BPVector3f(float[] var1) {
        super(var1);
    }

    public BPVector3f(BPVector3f var1) {
        super(var1);
    }

    public BPVector3f(Vector3d var1) {
        super((float) var1.x, (float) var1.y, (float) var1.z);
    }

    public BPVector3f(Vector3f var1) {
        super(var1.x, var1.y, var1.z);
    }

    public BPVector3f(javax.vecmath.Vector3d var1) {
        super(var1);
    }

    public BPVector3f(Tuple3f var1) {
        super(var1);
    }

    public BPVector3f(Tuple3d var1) {
        super(var1);
    }

    public BPVector3f() {
    }
}
