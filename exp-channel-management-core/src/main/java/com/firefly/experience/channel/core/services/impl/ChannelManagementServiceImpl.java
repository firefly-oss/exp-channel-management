package com.firefly.experience.channel.core.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firefly.domain.configuration.sdk.api.ConfigurationQueriesApi;
import com.firefly.domain.configuration.sdk.model.TenantBrandingDTO;
import com.firefly.experience.channel.core.models.LanguageLocaleDTO;
import com.firefly.experience.channel.core.models.LookupDomainDTO;
import com.firefly.experience.channel.core.queries.BrandingDTO;
import com.firefly.experience.channel.core.queries.ChannelInitDTO;
import com.firefly.experience.channel.core.queries.LanguageDTO;
import com.firefly.experience.channel.core.queries.MasterDataDTO;
import com.firefly.experience.channel.core.services.ChannelManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;


/**
 * Stateless composition implementation of {@link ChannelManagementService}.
 * <p>
 * Aggregates data from the {@code domain-core-configuration} service via
 * {@link ConfigurationQueriesApi}, which exposes language catalogues,
 * lookup domains, and tenant brandings through a single unified SDK.
 * <p>
 * All upstream calls are non-blocking (Reactor {@code Mono}/{@code Flux}).
 * The {@link #getChannelInit()} method subscribes to all three concurrently via {@code Mono.zip()}.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChannelManagementServiceImpl implements ChannelManagementService {

    /** Domain category bucket for country lookups. */
    public static final String DOMAIN_CATEGORY_COUNTRY = "COUNTRY";

    /** Domain category bucket for currency lookups. */
    public static final String DOMAIN_CATEGORY_CURRENCY = "CURRENCY";

    /** Domain category bucket for document-type lookups. */
    public static final String DOMAIN_CATEGORY_DOCUMENT_TYPE = "DOCUMENT_TYPE";

    /** Domain category bucket for all unrecognised domain codes. */
    public static final String DOMAIN_CATEGORY_OTHER = "OTHER";

    private final ConfigurationQueriesApi configurationQueriesApi;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<ChannelInitDTO> getChannelInit() {
        log.debug("Aggregating channel init data in parallel");
        return Mono.zip(
                getLanguages().collectList(),
                getChannelBranding(),
                getMasterData()
        ).map(tuple -> ChannelInitDTO.builder()
                .languages(tuple.getT1())
                .branding(tuple.getT2())
                .masterData(tuple.getT3())
                .build());
    }

    @Override
    public Flux<LanguageDTO> getLanguages() {
        log.debug("Fetching language locale list");
        return configurationQueriesApi.getLanguages(null)
                .flatMapMany(page -> Flux.fromIterable(
                        page.getContent() != null ? page.getContent() : List.of()
                ))
                .map(item -> objectMapper.convertValue(item, LanguageLocaleDTO.class))
                .map(this::toLanguageDTO);
    }

    @Override
    public Mono<LanguageDTO> getLanguage(String localeId) {
        log.debug("Fetching language locale for localeId={}", localeId);
        return configurationQueriesApi.getLanguages(null)
                .flatMapMany(page -> Flux.fromIterable(
                        page.getContent() != null ? page.getContent() : List.of()
                ))
                .map(item -> objectMapper.convertValue(item, LanguageLocaleDTO.class))
                .filter(dto -> dto.getLocaleId() != null && dto.getLocaleId().toString().equals(localeId))
                .next()
                .map(this::toLanguageDTO);
    }

    @Override
    public Mono<BrandingDTO> getChannelBranding() {
        log.debug("Fetching active channel branding");
        return configurationQueriesApi.getTenantBrandings(null)
                .filter(branding -> Boolean.TRUE.equals(branding.getActive()))
                .next()
                .map(this::toBrandingDTO);
    }

    @Override
    public Mono<MasterDataDTO> getMasterData() {
        log.debug("Fetching reference master data");
        return configurationQueriesApi.getLookupDomains(null)
                .flatMapMany(page -> Flux.fromIterable(
                        page.getContent() != null ? page.getContent() : List.of()
                ))
                .map(item -> objectMapper.convertValue(item, LookupDomainDTO.class))
                .collectMultimap(dto -> domainCategory(dto.getDomainCode()))
                .map(map -> MasterDataDTO.builder()
                        .countries(new ArrayList<>(map.getOrDefault(DOMAIN_CATEGORY_COUNTRY, List.of())))
                        .currencies(new ArrayList<>(map.getOrDefault(DOMAIN_CATEGORY_CURRENCY, List.of())))
                        .documentTypes(new ArrayList<>(map.getOrDefault(DOMAIN_CATEGORY_DOCUMENT_TYPE, List.of())))
                        .build());
    }

    // --- Mapping helpers ---

    private LanguageDTO toLanguageDTO(LanguageLocaleDTO dto) {
        return LanguageDTO.builder()
                .localeId(dto.getLocaleId() != null ? dto.getLocaleId().toString() : null)
                .name(dto.getLanguageName())
                .nativeName(dto.getNativeName())
                .isDefault(dto.getSortOrder() != null && dto.getSortOrder() == 0)
                .build();
    }

    private BrandingDTO toBrandingDTO(TenantBrandingDTO dto) {
        return BrandingDTO.builder()
                .logoUrl(dto.getLogoUrl())
                .primaryColor(dto.getPrimaryColor())
                .secondaryColor(dto.getSecondaryColor())
                .theme(null)
                .build();
    }

    /**
     * Derives a master-data category bucket from a domain code.
     * Domains whose code starts with {@code COUNTRY}, {@code CURRENCY}, or
     * {@code DOCUMENT_TYPE} / {@code DOC_TYPE} are placed in the corresponding bucket;
     * all others fall into {@code OTHER}.
     */
    private String domainCategory(String domainCode) {
        if (domainCode == null) {
            return DOMAIN_CATEGORY_OTHER;
        }
        String upper = domainCode.toUpperCase();
        if (upper.startsWith("COUNTRY")) return DOMAIN_CATEGORY_COUNTRY;
        if (upper.startsWith("CURRENCY")) return DOMAIN_CATEGORY_CURRENCY;
        if (upper.startsWith("DOCUMENT_TYPE") || upper.startsWith("DOC_TYPE")) return DOMAIN_CATEGORY_DOCUMENT_TYPE;
        return DOMAIN_CATEGORY_OTHER;
    }

}
