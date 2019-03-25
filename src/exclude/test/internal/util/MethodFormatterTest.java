package internal.util;

import com.ecfeed.web.service.service.util.MethodSignatureFormatter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MethodFormatterTest {

    @Test
    void formatFullSignature() {
        String result =
                MethodSignatureFormatter.format(
                        "testMethod(int arg1, int arg2, java.lang.String arg3)");
        assertEquals("testMethod(int, int, String)", result);
    }

    @Test
    void formatWithoutParamNames() {
        String result =
                MethodSignatureFormatter.format(
                        "void testMethod(int, int, String)");
        assertEquals("testMethod(int, int, String)", result);
    }

}
