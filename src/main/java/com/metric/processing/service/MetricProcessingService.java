package com.metric.processing.service;

import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Branched;
import org.apache.kafka.streams.kstream.BranchedKStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.springframework.stereotype.Service;

import com.metric.processing.util.MetricJsonUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MetricProcessingService {
    
    private static KafkaStreams streams;
	private final MetricJsonUtils metricJsonUtils;

    @PostConstruct
	public void run() throws Exception {
		Runtime.getRuntime().addShutdownHook(new ShutdownThread());

		Properties properties = new Properties();
		properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "metric-streams-application10");
		properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.100.204:9092");
		properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

		StreamsBuilder streamsBuilder = new StreamsBuilder();
		KStream<String, String> metrics = streamsBuilder.stream("metric.all");
		BranchedKStream<String, String> metricBranch = metrics.split()
			.branch((key, value) -> "cpu".equals(metricJsonUtils.getMetricName(value)), Branched.withConsumer(ks -> ks.to("metric.cpu")))
			.branch((key, value) -> "memory".equals(metricJsonUtils.getMetricName(value)), Branched.withConsumer(ks -> ks.to("metric.memory")));

        metrics.split().branch((key, value) -> "cpu".equals(metricJsonUtils.getMetricName(value)), Branched.withConsumer(
            ks -> ks.filter((key, value) -> metricJsonUtils.getTotalCpuPercent(value) > 0.0005)
                .mapValues(value -> metricJsonUtils.getHostTimestamp(value))
                .to("metric.cpu.alert")
        ));

		streams = new KafkaStreams(streamsBuilder.build(), properties);
        metrics.print(Printed.toSysOut());
		streams.start();
	}

	static class ShutdownThread extends Thread {
		public void run() {
			streams.close();
		}
	}
    
}
