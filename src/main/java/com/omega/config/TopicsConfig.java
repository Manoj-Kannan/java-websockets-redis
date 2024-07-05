package com.omega.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import javax.annotation.PostConstruct;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class TopicsConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(TopicsConfig.class);
    private static List<String> topics;

    @PostConstruct
    public void loadTopics() {
        Yaml yaml = new Yaml();
        try (InputStream inputStream = TopicsConfig.class.getClassLoader().getResourceAsStream("topics.yml")) {
            Map<String, Object> config = yaml.load(inputStream);
            topics = (List<String>) config.get("topics");
        } catch (Exception e) {
            LOGGER.error("Error reading topics", e);
        }
    }

    public List<String> getTopics() {
        return topics;
    }
}
