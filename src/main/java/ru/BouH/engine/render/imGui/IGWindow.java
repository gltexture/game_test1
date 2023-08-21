package ru.BouH.engine.render.imGui;

import imgui.ImDrawData;
import imgui.ImGui;
import ru.BouH.engine.render.screen.window.Window;

public class IGWindow {
    private final Window window;

    public IGWindow(Window window) {
        this.window = window;
    }

    public void renderIMG() {

    }

    private void mainRender(ImDrawData drawData) {

    }

    public void destroy() {

    }

    public Window getWindow() {
        return this.window;
    }
}
