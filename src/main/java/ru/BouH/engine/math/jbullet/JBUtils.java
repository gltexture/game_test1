package ru.BouH.engine.math.jbullet;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.CollisionWorld;
import com.bulletphysics.collision.shapes.ConvexShape;
import com.bulletphysics.linearmath.AabbUtil2;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.linearmath.TransformUtil;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public class JBUtils {
    // FIX
    public static void convexSweepTest(CollisionWorld collisionWorld, ConvexShape castShape, Transform convexFromWorld, Transform convexToWorld, CollisionWorld.ConvexResultCallback resultCallback) {
        Transform convexFromTrans = new Transform();
        Transform convexToTrans = new Transform();

        convexFromTrans.set(convexFromWorld);
        convexToTrans.set(convexToWorld);

        Vector3f castShapeAabbMin = new Vector3f();
        Vector3f castShapeAabbMax = new Vector3f();

        // Compute AABB that encompasses angular movement
        {
            Vector3f linVel = new Vector3f();
            Vector3f angVel = new Vector3f();
            TransformUtil.calculateVelocity(convexFromTrans, convexToTrans, 1f, linVel, angVel);
            Transform R = new Transform();
            R.setIdentity();
            R.setRotation(convexFromTrans.getRotation(new Quat4f()));
            castShape.calculateTemporalAabb(R, linVel, angVel, 1f, castShapeAabbMin, castShapeAabbMax);
        }

        Transform tmpTrans = new Transform();
        Vector3f collisionObjectAabbMin = new Vector3f();
        Vector3f collisionObjectAabbMax = new Vector3f();
        float[] hitLambda = new float[1];

        // go over all objects, and if the ray intersects their aabb + cast shape aabb,
        // do a ray-shape query using convexCaster (CCD)
        for (int i = 0; i < collisionWorld.getCollisionObjectArray().size(); i++) {
            CollisionObject collisionObject = collisionWorld.getCollisionObjectArray().getQuick(i);

            // only perform raycast if filterMask matches
            if (collisionObject != null && collisionObject.getBroadphaseHandle() != null && resultCallback.needsCollision(collisionObject.getBroadphaseHandle())) {
                //RigidcollisionObject* collisionObject = ctrl->GetRigidcollisionObject();
                collisionObject.getWorldTransform(tmpTrans);
                collisionObject.getCollisionShape().getAabb(tmpTrans, collisionObjectAabbMin, collisionObjectAabbMax);
                AabbUtil2.aabbExpand(collisionObjectAabbMin, collisionObjectAabbMax, castShapeAabbMin, castShapeAabbMax);
                hitLambda[0] = 1f; // could use resultCallback.closestHitFraction, but needs testing
                Vector3f hitNormal = new Vector3f();
                if (AabbUtil2.rayAabb(convexFromWorld.origin, convexToWorld.origin, collisionObjectAabbMin, collisionObjectAabbMax, hitLambda, hitNormal)) {
                    CollisionWorld.objectQuerySingle(castShape, convexFromTrans, convexToTrans,
                            collisionObject,
                            collisionObject.getCollisionShape(),
                            tmpTrans,
                            resultCallback,
                            collisionWorld.getDispatchInfo().allowedCcdPenetration);
                }
            }
        }
    }
}
