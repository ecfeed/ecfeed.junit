package com.ecfeed.junit.main;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class CommandLineConstants {

    public final static Path DEFAULT_FILE_INPUT_PATH = Paths.get("test.ect");

    public final static String FILE_INPUT_LONG = "fileInput";
    public final static String FILE_INPUT_SHORT = "i";

    public final static String USER_INPUT_LONG = "userInput";
    public final static String USER_INPUT_SHORT = "u";

    public final static String VERBOSE_LONG = "verbose";
    public final static String VERBOSE_SHORT = "v";
}
