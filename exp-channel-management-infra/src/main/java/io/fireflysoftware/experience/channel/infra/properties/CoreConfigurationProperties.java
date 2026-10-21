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


package io.fireflysoftware.experience.channel.infra.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the {@code domain-core-configuration} downstream service.
 * <p>
 * Binds to {@code api-configuration.domain-platform.core-configuration} in {@code application.yaml}.
 * The base path is resolved at startup and injected into {@link io.fireflysoftware.experience.channel.infra.factories.DomainConfigurationClientFactory}.
 */
@ConfigurationProperties(prefix = "api-configuration.domain-platform.core-configuration")
@Data
public class CoreConfigurationProperties {

    /** Base URL for the core configuration service (e.g., {@code http://localhost:8080}). */
    private String basePath;

}
