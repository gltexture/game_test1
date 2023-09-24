package ru.BouH.engine.render.scene.debug.jbullet;

import org.bytedeco.bullet.LinearMath.btIDebugDraw;
import org.bytedeco.bullet.LinearMath.btVector3;
import org.joml.Vector3d;
import ru.BouH.engine.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class JBDebugDraw extends btIDebugDraw {
    private int debugMode;

    public void drawLine(btVector3 from, btVector3 to, btVector3 color) {
    }

    @Override
    public void setDebugMode(int debugMode) {
        this.debugMode = debugMode;
    }

    @Override
    public int getDebugMode() {
        return this.debugMode;
    }
}
