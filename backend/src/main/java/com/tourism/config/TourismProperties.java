package com.tourism.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "tourism")
public class TourismProperties {
    private Jwt jwt = new Jwt();
    private Recommendation recommendation = new Recommendation();

    @Getter
    @Setter
    public static class Jwt {
        private String secret;
        private long expirationMs;
    }

    @Getter
    @Setter
    public static class Recommendation {
        private int cacheTtlSeconds = 3600;
        private double cfWeight = 0.55;
        private double cbWeight = 0.45;
    }
}
