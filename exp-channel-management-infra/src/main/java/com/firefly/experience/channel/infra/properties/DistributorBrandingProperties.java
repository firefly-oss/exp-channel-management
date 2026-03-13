package com.firefly.experience.channel.infra.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the {@code domain-distributor-branding} downstream service.
 * <p>
 * Binds to {@code api-configuration.domain-platform.distributor-branding} in {@code application.yaml}.
 * The base path is resolved at startup and injected into {@link com.firefly.experience.channel.infra.factories.DistributorBrandingClientFactory}.
 */
@ConfigurationProperties(prefix = "api-configuration.domain-platform.distributor-branding")
@Data
public class DistributorBrandingProperties {

    /** Base URL for the distributor branding service (e.g., {@code http://localhost:8050}). */
    private String basePath;

}
