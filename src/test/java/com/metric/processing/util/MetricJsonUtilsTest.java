package com.metric.processing.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MetricJsonUtilsTest {

    MetricJsonUtils metricJsonUtils;
    ObjectMapper objectMapper;
    String value;

    @BeforeEach
    void init() throws Exception {
        value = Files.lines(Paths.get(ClassLoader.getSystemClassLoader().getResource("cpu-metric.json").toURI()))
            .parallel()
            .collect(Collectors.joining());

        metricJsonUtils = new MetricJsonUtils(new ObjectMapper());

        objectMapper = new ObjectMapper();
    }

    @DisplayName("getTotalCpuPercent 테스트")
    @Test
    void getTotalCpuPercentTest() throws Exception {
        // given
        double expectedValue = 5;

        // when
        double actualValue = metricJsonUtils.getTotalCpuPercent(value);

        // then
        assertThat(actualValue).isEqualTo(expectedValue);
    }

    @DisplayName("getMetricName 테스트")
    @Test
    void getMetricNameTest() throws Exception {
        // given
        String expectedValue = "cpu";

        // when
        String actualValue = metricJsonUtils.getMetricName(value);

        // then
        assertThat(actualValue).isEqualTo(expectedValue);
    }


    @DisplayName("getHostTimestamp 테스트")
    @Test
    void getHostTimestampTest() throws Exception {
        // given
        String expectedTimestamptValue = "2022-06-07T06:27:37.661Z";
        String expectedOsNameValue = "CentOS Linux";

        // when
        JsonNode actualValue = objectMapper.readTree(metricJsonUtils.getHostTimestamp(value));

        // then
        assertThat(actualValue.get("timestamp").asText()).isEqualTo(expectedTimestamptValue);
        assertThat(actualValue.get("os").get("name").asText()).isEqualTo(expectedOsNameValue);
    }


}
