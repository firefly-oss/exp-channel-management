package com.firefly.experience.channel.infra.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the {@code domain-core-configuration} downstream service.
 * <p>
 * Binds to {@code api-configuration.domain-platform.core-configuration} in {@code application.yaml}.
 * The base path is resolved at startup and injected into {@link com.firefly.experience.channel.infra.factories.DomainConfigurationClientFactory}.
 */
@ConfigurationProperties(prefix = "api-configuration.domain-platform.core-configuration")
@Data
public class CoreConfigurationProperties {

    /** Base URL for the core configuration service (e.g., {@code http://localhost:8080}). */
    private String basePath;

}
