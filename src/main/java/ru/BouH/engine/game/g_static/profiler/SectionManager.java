package ru.BouH.engine.game.g_static.profiler;

import ru.BouH.engine.game.profiler.Section;

public class SectionManager {
    public static Section game = Section.constructNewSection("game", null);
    public static Section physX = Section.constructNewSection("physX", game);
    public static Section physWorld = Section.constructNewSection("physWorld", physX);
    public static Section bulletPhysWorld = Section.constructNewSection("bulletPhysWorld", physX);
    public static Section renderE = Section.constructNewSection("renderE", game);
    public static Section renderWorld = Section.constructNewSection("renderWorld", renderE);
    public static Section startSystem = Section.constructNewSection("startSystem", game);
    public static Section preLoading = Section.constructNewSection("preLoading", null);
}
