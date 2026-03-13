package com.firefly.experience.channel.infra.factories;

import com.firefly.common.config.sdk.api.TenantBrandingsApi;
import com.firefly.common.config.sdk.invoker.ApiClient;
import com.firefly.experience.channel.infra.properties.CoreConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Spring factory that wires reactive API clients for the {@code core-common-config-mgmt} service.
 * <p>
 * A single {@link ApiClient} is shared across all API beans produced by this factory, pointing
 * to the base URL configured under {@code api-configuration.domain-platform.core-configuration}.
 * <p>
 * The core-common-config-mgmt SDK exposes channel and tenant configuration APIs including:
 * <ul>
 *   <li>{@link TenantBrandingsApi} — tenant visual branding (logo, colours, theme)</li>
 * </ul>
 * <p>
 * ARCH-EXCEPTION: domain-core-configuration-sdk exposes only ConfigurationApi; TenantBrandingsApi
 * is sourced directly from core-common-config-mgmt-sdk as no domain wrapper exists.
 */
@Component
public class CoreConfigurationClientFactory {

    private final ApiClient apiClient;

    public CoreConfigurationClientFactory(CoreConfigurationProperties properties) {
        this.apiClient = new ApiClient();
        this.apiClient.setBasePath(properties.getBasePath());
    }

    /**
     * Provides a reactive client for tenant branding configuration queries.
     *
     * @return a configured {@link TenantBrandingsApi} instance
     */
    @Bean
    public TenantBrandingsApi tenantBrandingsApi() {
        return new TenantBrandingsApi(apiClient);
    }

}
