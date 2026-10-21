/*
 * Copyright 2025 Firefly Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package io.fireflysoftware.experience.channel.infra.factories;

import io.fireflysoftware.domain.configuration.sdk.api.ConfigurationQueriesApi;
import io.fireflysoftware.domain.configuration.sdk.invoker.ApiClient;
import io.fireflysoftware.experience.channel.infra.properties.CoreConfigurationProperties;
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
