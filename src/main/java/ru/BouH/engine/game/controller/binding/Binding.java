package ru.BouH.engine.game.controller.binding;

import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.controller.components.Key;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Binding {
    private static List<Binding> bindingList = new ArrayList<>();
    private final String description;
    private Key key;

    private Binding(Key key, String description) {
        this.key = key;
        this.description = description;
    }

    private Binding(String description) {
        this(null, description);
    }

    public static Binding createBinding(Key key, String description) {
        Binding binding = new Binding(key, description);
        if (key != null) {
            Binding.bindingList = Binding.bindingList.stream().filter(e -> {
                if (e.getKey().getKeyCode() == key.getKeyCode()) {
                    Game.getGame().getLogManager().warn("Binding with code " + key.getKeyName() + " already exists!");
                    return false;
                }
                return true;
            }).collect(Collectors.toList());
        }
        Binding.bindingList.add(binding);
        return binding;
    }

    public static List<Binding> getBindingList() {
        return new ArrayList<>(bindingList);
    }

    public void setKeyToBinding(Key key) {
        this.key = key;
    }

    public String getDescription() {
        return this.description;
    }

    public Key getKey() {
        return this.key;
    }

    public String toString() {
        String keyName = this.getKey() == null ? "NULL" : this.getKey().getKeyName();
        return keyName + " - " + this.getDescription();
    }
}
