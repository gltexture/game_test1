package ru.BouH.engine.game.resource.assets.obj.components;

public class Face {
    private final IdxGroup[] idxGroups;

    public Face(String v1, String v2, String v3) {
        this.idxGroups = new IdxGroup[3];
        this.idxGroups[0] = this.parse(v1);
        this.idxGroups[1] = this.parse(v2);
        this.idxGroups[2] = this.parse(v3);
    }

    private IdxGroup parse(String line) {
        IdxGroup idxGroup = new IdxGroup();
        String[] lineTokens = line.split("/");
        int length = lineTokens.length;
        idxGroup.idxPos = Integer.parseInt(lineTokens[0]) - 1;
        if (length > 1) {
            String textCrd = lineTokens[1];
            idxGroup.idxTextCoordinates = !textCrd.isEmpty() ? Integer.parseInt(textCrd) - 1 : IdxGroup.NULL;
            if (length > 2) {
                idxGroup.idxVecNormal = Integer.parseInt(lineTokens[2]) - 1;
            }
        }
        return idxGroup;
    }

    public IdxGroup[] getIdxGroups() {
        return this.idxGroups;
    }
}
