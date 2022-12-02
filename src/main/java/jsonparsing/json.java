package jsonparsing;

import com.fasterxml.jackson.databind.ObjectMapper;

public class json {
    private static ObjectMapper objectMapper = getDefaultObjectMapper();

    private static ObjectMapper getDefaultObjectMapper(){
        ObjectMapper defaultObjectMapper = new ObjectMapper();

        return defaultObjectMapper;
    }
}
