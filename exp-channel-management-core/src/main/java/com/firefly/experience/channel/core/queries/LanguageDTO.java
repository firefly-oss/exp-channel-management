package com.firefly.experience.channel.core.queries;

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
