package com.everjoy.aigateway.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class SchemaGuardService {
    private static final Set<String> SUPPORT_TICKET_REQUIRED = Set.of("title", "priority", "summary");
    private final ObjectMapper objectMapper = new ObjectMapper();

    public boolean validate(String schemaType, String jsonContent) {
        if (schemaType == null || schemaType.isBlank()) {
            return true;
        }
        try {
            JsonNode root = objectMapper.readTree(jsonContent);
            if ("support_ticket".equalsIgnoreCase(schemaType)) {
                return SUPPORT_TICKET_REQUIRED.stream().allMatch(root::hasNonNull);
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
