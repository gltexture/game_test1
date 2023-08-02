package ru.BouH.engine.render.environment;

import ru.BouH.engine.render.environment.sky.Sky;

public class Environment {
    private Sky sky;

    public Environment() {
        this.sky = new Sky("environment/skybox/skybox1.png");
    }

    public Sky getSky() {
        return this.sky;
    }

    public void setSky(Sky sky) {
        this.sky = sky;
    }
}
