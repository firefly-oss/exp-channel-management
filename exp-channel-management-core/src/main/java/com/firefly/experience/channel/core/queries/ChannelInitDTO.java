package com.firefly.experience.channel.core.queries;

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
