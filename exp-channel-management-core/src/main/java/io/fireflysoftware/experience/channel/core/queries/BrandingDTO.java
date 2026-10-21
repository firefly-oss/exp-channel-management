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


package io.fireflysoftware.experience.channel.core.queries;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Journey-layer representation of a channel's active visual branding.
 * <p>
 * Mapped from {@code TenantBrandingDTO} returned by the core-common-config-mgmt SDK.
 * Only the visual properties needed for frontend bootstrapping are included.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrandingDTO {

    /** URL of the channel logo asset. */
    private String logoUrl;

    /** Primary brand colour in hex format (e.g. {@code "#FF5733"}). */
    private String primaryColor;

    /** Secondary brand colour in hex format (e.g. {@code "#33FF57"}). */
    private String secondaryColor;

    /** UI theme name (e.g. {@code "LIGHT"}, {@code "DARK"}). May be {@code null}. */
    private String theme;

}
