package com.firefly.experience.channel.core.models;

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
