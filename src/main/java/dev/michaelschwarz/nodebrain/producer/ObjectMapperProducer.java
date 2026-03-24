package dev.michaelschwarz.nodebrain.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class ObjectMapperProducer {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Produces
    public ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }
}
