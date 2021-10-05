package pl.lodz.pl.it.ssbd2021.ssbd05.util.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class EjbLoggerInterceptor extends LoggerInterceptor {

    @Resource
    private SessionContext sessionContext;

    private String prefix = "";

    private String getCallerName() {
        return sessionContext.getCallerPrincipal().getName();
    }

    private String prepareLog(InvocationContext context, String log) {
        var out = "";
        if (prefix.equals("")) {
            prefix = UUID.randomUUID().toString();
        }
        out = "[" + prefix + "] callerName: " + getCallerName() + ", " + log;
        return out;
    }

    @Override
    protected Logger getLogger(InvocationContext context) {
        return LogManager.getLogger(context.getMethod().getDeclaringClass().getName());
    }

    @Override
    protected List<String> getLogBeforeStart(InvocationContext context) {
        return Collections.singletonList(prepareLog(context, getCallString(context)));
    }

    @Override
    protected List<String> getLogOnException(InvocationContext context, Exception exception) {
        var sw = new StringWriter();
        var pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        String msg = "Exception message: " + exception.getMessage() + " " +
                "Exception type: " + exception.getClass().getName() + " " +
                "Stack trace: " + sw;
        return Collections.singletonList(prepareLog(context, msg));
    }

    private String getAsString(Object object) {
        if (Collection.class.isAssignableFrom(object.getClass())) {
            var stringBuilder = new StringBuilder();
            stringBuilder.append("Collection[");
            Collection<Object> collection = (Collection<Object>) object;
            collection.forEach(o ->
                    stringBuilder.append(" ").append(getAsString(o)).append(",")
            );
            var output = stringBuilder.toString();
            return output.substring(0, output.length() - 1) + " ]";
        }
        return this.asLoggableString(object);
    }

    @Override
    protected List<String> getLogAfterStart(InvocationContext context, Object returnValue, Class<?> returnType) {
        if (returnType == Void.TYPE) {
            return Collections.singletonList(prepareLog(context, "exit"));
        }
        return Collections.singletonList(prepareLog(context, "exit with returned object: " + getAsString(returnValue)));
    }

    @Override
    @AroundInvoke
    public Object intercept(InvocationContext context) throws Exception {
        return super.intercept(context);
    }
}