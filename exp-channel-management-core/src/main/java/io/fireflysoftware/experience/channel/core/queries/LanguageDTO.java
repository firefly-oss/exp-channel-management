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
 * Journey-layer representation of a language/locale entry.
 * <p>
 * Mapped from {@code LanguageLocaleDTO} returned by the reference-master-data SDK.
 * Only the fields relevant to channel initialisation are surfaced here — SDK
 * internals (audit timestamps, status codes) are intentionally omitted.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LanguageDTO {

    /** Locale identifier (UUID as string, e.g. {@code "en-GB"}). */
    private String localeId;

    /** Display name of the language in English (e.g. {@code "English"}). */
    private String name;

    /** Display name of the language in its own script (e.g. {@code "English"}). */
    private String nativeName;

    /** Whether this locale is the channel default (derived from {@code sortOrder == 0}). */
    private boolean isDefault;

}
