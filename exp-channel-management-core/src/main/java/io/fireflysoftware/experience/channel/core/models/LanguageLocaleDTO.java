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


package io.fireflysoftware.experience.channel.core.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Local representation of a language-locale record returned by the
 * {@code domain-core-configuration} service inside a {@code PaginationResponse}.
 * <p>
 * Used as the target type for {@link com.fasterxml.jackson.databind.ObjectMapper#convertValue}
 * when deserialising the untyped {@code List<Object>} content of the pagination envelope.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LanguageLocaleDTO {

    private UUID localeId;
    private String languageCode;
    private String countryCode;
    private String localeCode;
    private String languageName;
    private String nativeName;
    private String regionName;
    private Boolean rtl;
    private Integer sortOrder;
    private String status;

}
