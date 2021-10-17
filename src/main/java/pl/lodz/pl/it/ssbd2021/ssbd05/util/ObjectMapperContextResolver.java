//package pl.lodz.pl.it.ssbd2021.ssbd05.util;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import javax.ws.rs.ext.ContextResolver;
//import javax.ws.rs.ext.Provider;
//
//@Provider
//public class ObjectMapperContextResolver implements ContextResolver<ObjectMapper> {
//
//    private final ObjectMapper objectMapper = initializeMapper();
//
//
//    private ObjectMapper initializeMapper() {
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.findAndRegisterModules();
//        return mapper;
//    }
//
//    @Override
//    public ObjectMapper getContext(Class<?> type) {
//        return objectMapper;
//    }
//}
