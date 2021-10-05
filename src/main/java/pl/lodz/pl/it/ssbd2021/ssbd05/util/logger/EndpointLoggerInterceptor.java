package pl.lodz.pl.it.ssbd2021.ssbd05.util.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.interceptor.InvocationContext;
import javax.ws.rs.Path;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EndpointLoggerInterceptor extends LoggerInterceptor {
    @Override
    protected Logger getLogger(InvocationContext context) {
        return LogManager.getLogger(context.getMethod().getDeclaringClass().getName());
    }

    @Override
    protected List<String> getLogBeforeStart(InvocationContext context) {

        var callString = getCallString(context);
        var pathAnnotationInfo = "";

        var pathAnnotation = context.getMethod().getAnnotation(Path.class);
        if (pathAnnotation != null) {
            pathAnnotationInfo = "Path annotation: " + pathAnnotation.value();
        }

        return Collections.singletonList(pathAnnotationInfo + " " + callString);
    }

    @Override
    protected List<String> getLogOnException(InvocationContext context, Exception exception) {
        var sw = new StringWriter();
        var pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        String msg = "Exception message: " + exception.getMessage() + " " +
                "Exception type: " + exception.getClass().getName() + " " +
                "Stack trace: " + sw;
        return Collections.singletonList(msg);
    }

    @Override
    protected List<String> getLogAfterStart(InvocationContext context, Object returnValue, Class<?> returnType) {
        return new ArrayList<>();
    }
}
