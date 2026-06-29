package com.everjoy.aigateway.schema;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SchemaGuardServiceTest {

    @Test
    void shouldValidateSupportTicketSchema() {
        SchemaGuardService service = new SchemaGuardService();
        String payload = """
                {
                  "title": "支付失败",
                  "priority": "P1",
                  "summary": "用户无法完成支付"
                }
                """;

        boolean valid = service.validate("support_ticket", payload);

        assertTrue(valid);
    }

    @Test
    void shouldRejectSupportTicketWhenFieldMissing() {
        SchemaGuardService service = new SchemaGuardService();
        String payload = """
                {
                  "title": "支付失败",
                  "summary": "用户无法完成支付"
                }
                """;

        boolean valid = service.validate("support_ticket", payload);

        assertFalse(valid);
    }
}
