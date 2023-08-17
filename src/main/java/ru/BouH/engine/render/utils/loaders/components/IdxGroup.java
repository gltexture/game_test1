package ru.BouH.engine.render.utils.loaders.components;

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