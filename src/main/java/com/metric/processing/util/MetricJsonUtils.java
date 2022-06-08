package com.metric.processing.util;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class MetricJsonUtils {

    private final ObjectMapper objectMapper;

    public double getTotalCpuPercent(String value) {
        double resultValue = 0.0;
        try {
            resultValue = objectMapper.readTree(value)
                .get("system")
                .get("cpu")
                .get("total")
                .get("norm")
                .get("pct")
                .doubleValue();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return resultValue;
    }

    public String getMetricName(String value) {
        String resultValue = "";
        try {
            resultValue = objectMapper.readTree(value)
                .get("metricset")
                .get("name")
                .asText();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return resultValue;
    }

    public String getHostTimestamp(String value) {
        ObjectNode jsonObject = null;
        try {
            jsonObject = (ObjectNode) objectMapper.readTree(value);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        ObjectNode result = (ObjectNode) jsonObject.get("host");

        result.set("timestamp", jsonObject.get("@timestamp"));

        return result.toString();
    }

}
