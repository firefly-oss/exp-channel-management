package com.firefly.experience.channel.infra.factories;

import com.firefly.domain.configuration.sdk.api.ConfigurationQueriesApi;
import com.firefly.domain.configuration.sdk.invoker.ApiClient;
import com.firefly.experience.channel.infra.properties.CoreConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Spring factory that wires reactive API clients for the {@code domain-core-configuration} service.
 * <p>
 * A single {@link ApiClient} is shared across all API beans produced by this factory, pointing
 * to the base URL configured under {@code api-configuration.domain-platform.core-configuration}.
 * <p>
 * Available API classes from the SDK:
 * <ul>
 *   <li>{@link ConfigurationQueriesApi} -- language catalogue, lookup domains, and tenant brandings</li>
 * </ul>
 */
@Component
public class DomainConfigurationClientFactory {

    private final ApiClient apiClient;

    public DomainConfigurationClientFactory(CoreConfigurationProperties properties) {
        this.apiClient = new ApiClient();
        this.apiClient.setBasePath(properties.getBasePath());
    }

    /**
     * Provides a reactive client for configuration query operations (languages,
     * lookup domains, and tenant brandings).
     *
     * @return a configured {@link ConfigurationQueriesApi} instance
     */
    @Bean
    public ConfigurationQueriesApi configurationQueriesApi() {
        return new ConfigurationQueriesApi(apiClient);
    }

}
