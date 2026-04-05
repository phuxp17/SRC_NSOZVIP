package com.tea.util;

import com.tea.server.Config;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class SystemCommand {

    private SystemCommand() {
    }

    public static boolean isWindows() {
        String osName = System.getProperty("os.name", "");
        return osName.toLowerCase(Locale.ROOT).contains("win");
    }

    public static boolean runConfiguredCommand(String command, String description) {
        if (StringUtils.isNullOrEmpty(command)) {
            Log.warn(description + " command is not configured, skipping.");
            return false;
        }

        List<String> shellCommand = new ArrayList<>();
        if (isWindows()) {
            shellCommand.add("cmd");
            shellCommand.add("/c");
        } else {
            shellCommand.add("/bin/sh");
            shellCommand.add("-lc");
        }
        shellCommand.add(command);

        ProcessBuilder builder = new ProcessBuilder(shellCommand);
        builder.directory(new File(Config.getInstance().getServerDir()));
        builder.redirectErrorStream(true);
        builder.inheritIO();

        try {
            builder.start();
            Log.info("Started " + description + " command.");
            return true;
        } catch (IOException ex) {
            Log.error("Can not start " + description + " command: " + ex.getMessage(), ex);
            return false;
        }
    }
}
