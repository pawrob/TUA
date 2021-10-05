package pl.lodz.pl.it.ssbd2021.ssbd05.util.logger;

import pl.lodz.pl.it.ssbd2021.ssbd05.dto.AbstractDTO;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.AbstractEntity;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import java.util.Arrays;
import java.util.List;

public abstract class LoggerInterceptor {
    protected abstract org.apache.logging.log4j.Logger getLogger(InvocationContext context);

    protected String asLoggableString(Object object) {
        if (object == null) {
            return "null";
        } else if (AbstractEntity.class.isAssignableFrom(object.getClass())) {
            var abstractEntity = (AbstractEntity) object;
            long id = abstractEntity.getId();
            Long version = abstractEntity.getVersion();
            String className = object.getClass().getSimpleName();
            return className + "{ id: " + id + ", version: " + version + " }";
        } else if (AbstractDTO.class.isAssignableFrom(object.getClass())) {
            var abstractDTO = (AbstractDTO) object;
            long id = abstractDTO.getId();
            Long version = abstractDTO.getVersion();
            String className = object.getClass().getSimpleName();
            return className + "{ id: " + id + ", version: " + version + " }";
        } else {
            return " ? ";
        }
    }

    protected String getCallString(InvocationContext context) {
        var argumentString = "";
        Object[] parameters = context.getParameters();
        if (parameters != null && parameters.length > 0) {
            argumentString = Arrays.stream(parameters).map(this::asLoggableString).reduce("", (s, s2) -> s + ", " + s2);
        }
        return context.getMethod().getDeclaringClass().getName()
                + "::" +
                context.getMethod().getName()
                + "( " + argumentString + " )";
    }

    protected abstract List<String> getLogBeforeStart(InvocationContext context);

    protected abstract List<String> getLogOnException(InvocationContext context, Exception exception);

    protected abstract List<String> getLogAfterStart(InvocationContext context, Object returnValue, Class<?> returnType);

    @AroundInvoke
    public Object intercept(InvocationContext context) throws Exception {
        var logger = getLogger(context);
        getLogBeforeStart(context).forEach(logger::trace);
        try {
            Object returnValue = context.proceed();
            Class<?> returnType = context.getMethod().getReturnType();
            getLogAfterStart(context, returnValue, returnType).forEach(logger::trace);
            return returnValue;
        } catch (Exception e) {
            getLogOnException(context, e).forEach(logger::error);
            throw e;
        }
    }
}
