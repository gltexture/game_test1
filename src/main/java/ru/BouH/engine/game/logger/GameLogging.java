package ru.BouH.engine.game.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.g_static.profiler.SectionManager;

import javax.swing.*;

public class GameLogging {
    private final Logger log;

    public GameLogging() {
        this.log = LogManager.getRootLogger();
    }

    public void log(String message, Object... objects) {
        this.log.info(message, objects);
    }

    public void debug(String message, Object... objects) {
        this.log.debug(message, objects);
    }

    public void warn(String message, Object... objects) {
        this.log.warn(message, objects);
    }

    public void bigWarn(String message, Object... objects) {
        this.log.error("****************************************");
        this.log.error("* " + message, objects);
        this.log.error("****************************************");
    }

    public void error(String message, Object... objects) {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        StringBuilder stringBuilder = this.getStringBuilder(message, objects, trace);
        this.log.fatal(stringBuilder.toString());
        Game.getGame().getProfiler().crashSection(SectionManager.game);
        Game.getGame().getProfiler().stopAllSections();
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
        Game.getGame().destroyGame();
    }

    private StringBuilder getStringBuilder(String message, Object[] objects, StackTraceElement[] trace) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n");
        stringBuilder.append("****************************************");
        stringBuilder.append("\n");
        stringBuilder.append(String.format("* " + message, objects));
        stringBuilder.append("\n");
        for (int i = 2; i < 8 && i < trace.length; i++) {
            stringBuilder.append(String.format("* at %s%s", trace[i].toString(), i == 7 ? "..." : ""));
            stringBuilder.append("\n");
        }
        stringBuilder.append("****************************************");
        return stringBuilder;
    }
}
