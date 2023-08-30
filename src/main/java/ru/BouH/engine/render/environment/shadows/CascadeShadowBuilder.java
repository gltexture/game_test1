package ru.BouH.engine.render.environment.shadows;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector4d;

import java.util.List;

public class CascadeShadowBuilder {
    public static final int SHADOW_CASCADE_MAX = 3;
    public Matrix4d projectionViewMatrix;
    public double splitDistance;

    public CascadeShadowBuilder() {
        this.projectionViewMatrix = new Matrix4d();
    }

    public static void updateCascadeShadow(List<CascadeShadowBuilder> cascadeShadowBuilders, Vector4d lightPos, Matrix4d viewMatrix, Matrix4d projMatrix) {
        double cascadeSplitLambda = 0.95f;

        double[] cascadeSplits = new double[CascadeShadowBuilder.SHADOW_CASCADE_MAX];

        double nearClip = projMatrix.perspectiveNear();
        double farClip = projMatrix.perspectiveFar();
        double clipRange = farClip - nearClip;

        double maxZ = nearClip + clipRange;

        double range = maxZ - nearClip;
        double ratio = maxZ / nearClip;

        for (int i = 0; i < CascadeShadowBuilder.SHADOW_CASCADE_MAX; i++) {
            double p = (i + 1) / (double) (CascadeShadowBuilder.SHADOW_CASCADE_MAX);
            double log = (float) (nearClip * Math.pow(ratio, p));
            double uniform = nearClip + range * p;
            double d = cascadeSplitLambda * (log - uniform) + uniform;
            cascadeSplits[i] = (d - nearClip) / clipRange;
        }

        double lastSplitDist = 0.0f;
        for (int i = 0; i < CascadeShadowBuilder.SHADOW_CASCADE_MAX; i++) {
            double splitDist = cascadeSplits[i];

            Vector3d[] frustumCorners = new Vector3d[]{
                    new Vector3d(-1.0f, 1.0f, -1.0f),
                    new Vector3d(1.0f, 1.0f, -1.0f),
                    new Vector3d(1.0f, -1.0f, -1.0f),
                    new Vector3d(-1.0f, -1.0f, -1.0f),
                    new Vector3d(-1.0f, 1.0f, 1.0f),
                    new Vector3d(1.0f, 1.0f, 1.0f),
                    new Vector3d(1.0f, -1.0f, 1.0f),
                    new Vector3d(-1.0f, -1.0f, 1.0f),
            };

            Matrix4d invCam = (new Matrix4d(projMatrix).mul(viewMatrix)).invert();
            for (int j = 0; j < 8; j++) {
                Vector4d invCorner = new Vector4d(frustumCorners[j], 1.0f).mul(invCam);
                frustumCorners[j] = new Vector3d(invCorner.x / invCorner.w, invCorner.y / invCorner.w, invCorner.z / invCorner.w);
            }

            for (int j = 0; j < 4; j++) {
                Vector3d dist = new Vector3d(frustumCorners[j + 4]).sub(frustumCorners[j]);
                frustumCorners[j + 4] = new Vector3d(frustumCorners[j]).add(new Vector3d(dist).mul(splitDist));
                frustumCorners[j].add(frustumCorners[j], dist.mul(lastSplitDist));
            }

            Vector3d frustumCenter = new Vector3d(0.0f);
            for (int j = 0; j < 8; j++) {
                frustumCenter.add(frustumCorners[j]);
            }
            frustumCenter.div(8.0f);

            double radius = 0.0f;
            for (int j = 0; j < 8; j++) {
                double distance = (new Vector3d(frustumCorners[j]).sub(frustumCenter)).length();
                radius = Math.max(radius, distance);
            }
            radius = Math.ceil(radius * 16.0f) / 16.0f;

            Vector3d maxExtents = new Vector3d(radius);
            Vector3d minExtents = new Vector3d(maxExtents).mul(-1);

            Vector3d lightDir = (new Vector3d(lightPos.x, lightPos.y, lightPos.z).mul(-1)).normalize();
            Vector3d eye = new Vector3d(frustumCenter).sub(new Vector3d(lightDir).mul(-minExtents.z));
            Vector3d up = new Vector3d(0.0f, 1.0f, 0.0f);
            Matrix4d lightViewMatrix = new Matrix4d().lookAt(eye, frustumCenter, up);
            Matrix4d lightOrthographicMatrix = new Matrix4d().ortho(minExtents.x, maxExtents.x, minExtents.y, maxExtents.y, 0.0f, maxExtents.sub(minExtents).z, true);

            CascadeShadowBuilder cascadeShadowBuilder = cascadeShadowBuilders.get(i);
            cascadeShadowBuilder.splitDistance = (nearClip + splitDist * clipRange) * -1.0f;

            Matrix4d shadowMatrix = new Matrix4d(lightOrthographicMatrix.mul(lightViewMatrix));
            Vector4d shadowOrigin = new Vector4d(0.0d, 0.0d, 0.0d, 1.0d);
            shadowOrigin.mul(shadowMatrix, shadowOrigin);
            shadowOrigin.mul(DepthTexture.MAP_DIMENSIONS).div(2.0f);

            Vector4d roundedOrigin = new Vector4d();
            shadowOrigin.round(roundedOrigin);
            Vector4d roundOffset = new Vector4d(roundedOrigin).sub(shadowOrigin);
            roundOffset.mul(2.0d).div(DepthTexture.MAP_DIMENSIONS);
            roundOffset.z = 0.0d;
            roundOffset.w = 0.0d;

            Matrix4d shadowProj = new Matrix4d(lightOrthographicMatrix);
            shadowProj.m30(shadowProj.m30() + roundOffset.x);
            shadowProj.m31(shadowProj.m31() + roundOffset.y);
            shadowProj.m32(shadowProj.m32() + roundOffset.z);
            shadowProj.m33(shadowProj.m33() + roundOffset.w);
            cascadeShadowBuilder.projectionViewMatrix = shadowProj;
        }
    }

    public double getSplitDistance() {
        return this.splitDistance;
    }

    public Matrix4d getProjectionViewMatrix() {
        return this.projectionViewMatrix;
    }
}
