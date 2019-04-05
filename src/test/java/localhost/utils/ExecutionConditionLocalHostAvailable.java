package localhost.utils;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public final class ExecutionConditionLocalHostAvailable implements ExecutionCondition {

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        if (pingHost("localhost", 8095, 5)) {
            return ConditionEvaluationResult.enabled("The local server is running");
        }

        return ConditionEvaluationResult.disabled("The local server is not available");
    }

    public boolean pingHost(String host, int port, int timeout) { // TODO - get from core
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeout);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
