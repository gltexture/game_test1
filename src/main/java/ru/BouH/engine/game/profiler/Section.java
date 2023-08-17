package ru.BouH.engine.game.profiler;

import ru.BouH.engine.game.Game;

public class Section {
    private final String name;
    private final Section parent;
    private Status status;

    protected Section(String name, Section parent) {
        this.name = name;
        this.parent = parent;
        this.status = Status.WAITING;
    }

    protected Section(String name) {
        this(name, null);
    }

    public static Section constructNewSection(String string, Section parent) {
        if (string.isEmpty()) {
            string = "unknown";
        }
        int i1 = Game.getGame().getProfiler().checkSectionSet(string);
        if (i1 > 0) {
            string += "(" + i1 + ")";
        }
        Section section = new Section(string, parent);
        Game.getGame().getProfiler().addNewSection(section);
        return section;
    }

    public void detectCrash() {
        this.status = Status.CRASHED;
        if (this.parent != null) {
            this.parent.detectCrash();
        }
    }

    public void setSuccess() {
        this.status = Status.SUCCESS;
    }

    public void startSection() {
        this.status = Status.IN_PROCESS;
    }

    public void stopSection() {
        this.status = Status.STOPPED;
    }

    public Status getStatus() {
        return this.status;
    }

    public Section getParent() {
        return this.parent;
    }

    public String getName() {
        return this.name;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.getName()).append("=[").append(this.getStatus().toString()).append("]");
        if (this.getParent() != null) {
            stringBuilder.append(" => ").append(this.getParent());
        }
        return stringBuilder.toString();
    }

    public enum Status {
        IN_PROCESS,
        WAITING,
        CRASHED,
        SUCCESS,
        STOPPED
    }
}
