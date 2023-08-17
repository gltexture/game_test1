package ru.BouH.engine.game.g_static.binding;

import org.lwjgl.glfw.GLFW;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.controller.Binding;
import ru.BouH.engine.game.controller.components.FunctionalKey;
import ru.BouH.engine.game.controller.components.IKeyAction;
import ru.BouH.engine.game.controller.components.Key;

public class BindingList {
    public static BindingList instance = new BindingList();
    public Key keyA = new Key(GLFW.GLFW_KEY_A);
    public Key keyD = new Key(GLFW.GLFW_KEY_D);
    public Key keyW = new Key(GLFW.GLFW_KEY_W);
    public Key keyS = new Key(GLFW.GLFW_KEY_S);
    public Key keyUp = new Key(GLFW.GLFW_KEY_SPACE);
    public Key keyDown = new Key(GLFW.GLFW_KEY_LEFT_SHIFT);
    public Key keyEsc = new FunctionalKey(e -> {
        if (e == IKeyAction.KeyAction.CLICK) {
            Game.getGame().destroyGame();
        }
    }, GLFW.GLFW_KEY_ESCAPE);

    public Key keyT = new FunctionalKey(e -> {
        if (e == IKeyAction.KeyAction.CLICK) {
            Game.getGame().getScreen().isInFocus = !Game.getGame().getScreen().isInFocus;
        }
    }, GLFW.GLFW_KEY_T);

    public BindingList() {
        Binding.createBinding(this.keyA, "Шаг влево");
        Binding.createBinding(this.keyD, "Шаг вправо");
        Binding.createBinding(this.keyW, "Шаг вперед");
        Binding.createBinding(this.keyS, "Шаг назад");
        Binding.createBinding(this.keyEsc, "Закрыть экран");
        Binding.createBinding(this.keyT, "Фокус");
        Binding.createBinding(this.keyUp, "Лететь вверх");
        Binding.createBinding(this.keyDown, "Лететь вниз");
    }
}
