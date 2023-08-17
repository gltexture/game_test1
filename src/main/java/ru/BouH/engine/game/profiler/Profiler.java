package ru.BouH.engine.game.profiler;

import ru.BouH.engine.game.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Profiler {
    private final List<Section> sectionList;

    public Profiler() {
        this.sectionList = new ArrayList<>();
    }

    public void startSection(Section section) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[PRF] Start section '").append(section.getName()).append("'");
        if (section.getParent() != null) {
            stringBuilder.append(" | Parent - '").append(section.getParent().getName()).append("'");
        }
        Game.getGame().getLogManager().debug(stringBuilder.toString());
        section.startSection();
    }

    public void endSection(Section section) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[PRF] End section '").append(section.getName()).append("'");
        if (section.getParent() != null) {
            stringBuilder.append(" | Parent - '").append(section.getParent().getName()).append("'");
        }
        Game.getGame().getLogManager().debug(stringBuilder.toString());
        section.setSuccess();
    }

    public void crashSection(Section section) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[PRF] Crash section '").append(section.getName()).append("'");
        if (section.getParent() != null) {
            stringBuilder.append(" | Parent - '").append(section.getParent().getName()).append("'");
        }
        Game.getGame().getLogManager().debug(stringBuilder.toString());
        section.detectCrash();
    }

    public List<Section> allSections() {
        return this.sectionList;
    }

    public void stopAllSections() {
        this.allSections().forEach(e -> {
            if (e.getStatus() == Section.Status.IN_PROCESS) {
                e.stopSection();
            }
        });
    }

    public void stopSection(Section section) {
        if (section.getStatus() != Section.Status.IN_PROCESS) {
            Game.getGame().getLogManager().debug("[PRF] Couldn't stop section: " + section);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[PRF] Stop section '").append(section.getName()).append("'");
        if (section.getParent() != null) {
            stringBuilder.append(" | Parent - '").append(section.getParent().getName()).append("'");
        }
        Game.getGame().getLogManager().debug(stringBuilder.toString());
        section.stopSection();
    }

    private Section findSection(String name) {
        Optional<Section> optional = this.allSections().stream().filter(e -> e.getName().equals(name)).findFirst();
        if (!optional.isPresent()) {
            Game.getGame().getLogManager().debug("[PRF] Section '" + name + "' doesn't exist");
            return null;
        } else {
            return optional.get();
        }
    }

    public int checkSectionSet(String s) {
        return (int) this.sectionList.stream().filter(e -> e.getName().startsWith(s)).count();
    }

    public int checkSectionSet(Section s) {
        return (int) this.sectionList.stream().filter(e -> e.equals(s)).count();
    }

    public void addNewSection(Section section) {
        this.allSections().add(section);
    }
}