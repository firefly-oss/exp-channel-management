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

import java.util.List;

/**
 * Composite response for the channel initialisation endpoint.
 * <p>
 * Aggregates languages, branding, and master data in a single payload so the frontend
 * can bootstrap itself in one round-trip. All three upstream calls are made in parallel
 * via {@code Mono.zip()}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChannelInitDTO {

    /** Available locales for this channel. */
    private List<LanguageDTO> languages;

    /** Active visual branding configuration. */
    private BrandingDTO branding;

    /** Reference master data (countries, currencies, document types). */
    private MasterDataDTO masterData;

}
