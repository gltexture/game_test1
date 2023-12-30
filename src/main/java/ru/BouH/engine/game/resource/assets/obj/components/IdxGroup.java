package ru.BouH.engine.game.resource.assets.obj.components;

public class IdxGroup {
    public static final int NULL = -1;
    public int idxPos;
    public int idxTextCoordinates;
    public int idxVecNormal;

    public IdxGroup() {
        this.idxPos = IdxGroup.NULL;
        this.idxTextCoordinates = IdxGroup.NULL;
        this.idxVecNormal = IdxGroup.NULL;
    }
}