package com.firefly.experience.channel.infra.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the {@code core-common-reference-master-data} downstream service.
 * <p>
 * Binds to {@code api-configuration.core-platform.reference-master-data} in {@code application.yaml}.
 * The base path is resolved at startup and injected into {@link com.firefly.experience.channel.infra.factories.ReferenceMasterDataClientFactory}.
 */
@ConfigurationProperties(prefix = "api-configuration.core-platform.reference-master-data")
@Data
public class ReferenceMasterDataProperties {

    /** Base URL for the reference master data service (e.g., {@code http://localhost:8060}). */
    private String basePath;

}
