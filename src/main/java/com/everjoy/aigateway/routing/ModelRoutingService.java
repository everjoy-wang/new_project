package com.everjoy.aigateway.routing;

import org.springframework.stereotype.Service;

@Service
public class ModelRoutingService {

    public String chooseModel(String taskType, String prompt) {
        if ("analysis".equalsIgnoreCase(taskType)) {
            return "smart-provider";
        }
        if (prompt != null && prompt.length() > 1000) {
            return "smart-provider";
        }
        return "fast-provider";
    }
}
