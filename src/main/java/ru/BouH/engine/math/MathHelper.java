package ru.BouH.engine.math;

import com.bulletphysics.linearmath.QuaternionUtil;
import org.joml.Math;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import javax.vecmath.Quat4d;
import javax.vecmath.Quat4f;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathHelper {
    private static final int BF_SIN_BITS = 12;
    private static final int BF_SIN_MASK;
    private static final int BF_SIN_COUNT;
    private static final float BF_radFull;
    private static final float BF_radToIndex;
    private static final float BF_degFull;
    private static final float BF_degToIndex;
    private static final float[] BF_sin;
    private static final float[] BF_cos;

    static {
        BF_SIN_MASK = ~(-1 << BF_SIN_BITS);
        BF_SIN_COUNT = BF_SIN_MASK + 1;
        BF_radFull = 6.2831855F;
        BF_degFull = 360.0F;
        BF_radToIndex = (float) BF_SIN_COUNT / BF_radFull;
        BF_degToIndex = (float) BF_SIN_COUNT / BF_degFull;
        BF_sin = new float[BF_SIN_COUNT];
        BF_cos = new float[BF_SIN_COUNT];
        int i;
        for (i = 0; i < BF_SIN_COUNT; ++i) {
            BF_sin[i] = (float) Math.sin(((float) i + 0.5F) / (float) BF_SIN_COUNT * BF_radFull);
            BF_cos[i] = (float) Math.cos(((float) i + 0.5F) / (float) BF_SIN_COUNT * BF_radFull);
        }
        for (i = 0; i < 360; i += 90) {
            BF_sin[(int) ((float) i * BF_degToIndex) & BF_SIN_MASK] = (float) Math.sin((double) i * Math.PI / 180.0);
            BF_cos[(int) ((float) i * BF_degToIndex) & BF_SIN_MASK] = (float) Math.cos((double) i * Math.PI / 180.0);
        }
    }

    public static float toDegrees(float radians) {
        BigDecimal r = BigDecimal.valueOf(radians);
        BigDecimal d = r.multiply(BigDecimal.valueOf(180)).divide(BigDecimal.valueOf(Math.PI), 10, RoundingMode.HALF_UP);
        return d.floatValue();
    }

    public static Vector3d toDegrees(Quat4f q) {
        Quaterniond quaterniond = new Quaterniond(q.x, q.y, q.z, q.w);
        Vector3d vector3d1 = new Vector3d();
        quaterniond.getEulerAnglesXYZ(vector3d1);
        return new Vector3d(Math.toDegrees(vector3d1.x), Math.toDegrees(vector3d1.y), Math.toDegrees(vector3d1.z));
    }

    public static float sin(double rad) {
        return BF_sin[(int) (rad * BF_radToIndex) & BF_SIN_MASK];
    }

    public static float cos(double rad) {
        return BF_cos[(int) (rad * BF_radToIndex) & BF_SIN_MASK];
    }

    public static double clamp(double d1, double d2, double d3) {
        return d1 < d2 ? d2 : MathHelper.min(d1, d3);
    }

    public static double min(double d1, double d2) {
        return Math.min(d1, d2);
    }

    public static double max(double d1, double d2) {
        return Math.max(d1, d2);
    }
}
