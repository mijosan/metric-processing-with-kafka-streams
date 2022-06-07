package com.metric.processing.util;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class MetricJsonUtils {

    private final ObjectMapper objectMapper;

    public double getTotalCpuPercent(String value) throws JsonProcessingException, JsonMappingException {
        return objectMapper.readTree(value)
            .get("system")
            .get("cpu")
            .get("total")
            .get("norm")
            .get("pct")
            .doubleValue();
    }

    public String getMetricName(String value) throws JsonProcessingException, JsonMappingException {
        return objectMapper.readTree(value)
            .get("metricset")
            .get("name")
            .asText();
    }

    public String getHostTimestamp(String value) throws JsonProcessingException, JsonMappingException {
        ObjectNode jsonObject = (ObjectNode) objectMapper.readTree(value);
        ObjectNode result = (ObjectNode) jsonObject.get("host");

        result.set("timestamp", jsonObject.get("@timestamp"));

        return result.toString();
    }

}
