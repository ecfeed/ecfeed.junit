package com.ecfeed.junit.main;

import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.junit.utils.Localization;
import joptsimple.OptionSet;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static com.ecfeed.junit.main.CommandLineConstants.*;

public final class InputProcessor {

    static Optional<Path> extractFileInputPath(OptionSet options) {
        Optional<String> pathProvided = checkRedundantArguments(options, FILE_INPUT_LONG, FILE_INPUT_SHORT);
        return pathProvided.isPresent() ? validateFileInput(pathProvided.get()) : Optional.empty();
    }

    static private Optional<String> checkRedundantArguments(OptionSet options, String nameLong, String nameShort) {
        Object parameter = null;

        if (options.hasArgument(nameLong)) {
            parameter = options.valueOf(nameLong);
        }

        if (options.hasArgument(nameShort)) {

            if (parameter != null) {
                throw new IllegalArgumentException(Localization.bundle.getString("inputProcessorDuplicateArguments")
                        + nameLong + "', '" + nameShort + "'");
            }

            parameter = options.valueOf(nameShort);
        }

        return parameter == null ? Optional.empty() : Optional.of(parameter.toString());
    }

    static private Optional<Path> validateFileInput(String pathProvided) {
        Path path = Paths.get(pathProvided);

        if (!Files.exists(path)) {
            throw new NullPointerException(Localization.bundle.getString("inputProcessorMissingFile") + path);
        }

        if (!Files.isRegularFile(path)) {
            throw new IllegalArgumentException(Localization.bundle.getString("inputProcessorDirectory") + path);
        }

        if (!Files.isReadable(path)) {
            throw new IllegalArgumentException(Localization.bundle.getString("inputProcessorNotReadable") + path);
        }

        return Optional.ofNullable(path);
    }

    static Optional<String> extractUserData(OptionSet options) {
        return checkRedundantArguments(options, USER_INPUT_LONG, USER_INPUT_SHORT);
    }

    static boolean extractVerbose(OptionSet options) {
        if (options.has(VERBOSE_LONG) || options.has(VERBOSE_SHORT)) {
            return true;
        }

        return false;
    }

    static Optional<String> extractMethod(OptionSet options)
    {
        if(options.hasArgument(METHOD_LONG))
            return Optional.of(options.valueOf(METHOD_LONG).toString());
        if(options.hasArgument(METHOD_SHORT))
            return Optional.of(options.valueOf(METHOD_SHORT).toString());
        return Optional.empty();
    }

    static Integer extractN(OptionSet options) {
        Integer N = null;
        if (options.hasArgument(TUPLE_ARITY_SHORT)) {
            try {
                N = Integer.parseInt(options.valueOf(TUPLE_ARITY_SHORT).toString());
            }
            catch (NumberFormatException e)
            {
                ExceptionHelper.reportRuntimeException("Value of N provided must be an integer.");
            }
        }
        return N;
    }

}
