package com.firefly.experience.channel.core.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firefly.domain.configuration.sdk.api.ConfigurationQueriesApi;
import com.firefly.domain.configuration.sdk.model.LookupItemDTO;
import com.firefly.domain.configuration.sdk.model.TenantBrandingDTO;
import com.firefly.experience.channel.core.models.LanguageLocaleDTO;
import com.firefly.experience.channel.core.queries.BrandingDTO;
import com.firefly.experience.channel.core.queries.ChannelInitDTO;
import com.firefly.experience.channel.core.queries.LanguageDTO;
import com.firefly.experience.channel.core.queries.LookupEntryDTO;
import com.firefly.experience.channel.core.queries.MasterDataDTO;
import com.firefly.experience.channel.core.services.ChannelManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.Map;


/**
 * Stateless composition implementation of {@link ChannelManagementService}.
 * <p>
 * Aggregates data from the {@code domain-core-configuration} service via
 * {@link ConfigurationQueriesApi}, which exposes language catalogues,
 * tenant brandings and reference master data through a single unified SDK.
 * <p>
 * All upstream calls are non-blocking (Reactor {@code Mono}/{@code Flux}).
 * The {@link #getChannelInit()} method subscribes to all three concurrently via {@code Mono.zip()}.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChannelManagementServiceImpl implements ChannelManagementService {

    /** Well-known lookup_domain codes the channel renders. Must match the codes seeded in core-common-reference-master-data. */
    static final String DOMAIN_COUNTRY = "COUNTRY";
    static final String DOMAIN_CURRENCY = "CURRENCY";
    static final String DOMAIN_DOCUMENT_TYPE = "DOCUMENT_TYPE";
    static final String DOMAIN_EMPLOYMENT_STATUS = "EMPLOYMENT_STATUS";
    static final String DOMAIN_CONTRACT_TYPE = "CONTRACT_TYPE";
    static final String DOMAIN_HOUSING_TYPE = "HOUSING_TYPE";
    static final String DOMAIN_MARITAL_STATUS = "MARITAL_STATUS";
    static final String DOMAIN_LOAN_PURPOSE = "LOAN_PURPOSE";
    static final String DOMAIN_SECTOR = "SECTOR";
    static final String DOMAIN_ASSET_TYPE = "ASSET_TYPE";
    static final String DOMAIN_LEGAL_FORM = "LEGAL_FORM";
    static final String DOMAIN_EMPLOYEE_RANGE = "EMPLOYEE_RANGE";
    static final String DOMAIN_REPRESENTATIVE_ROLE = "REPRESENTATIVE_ROLE";

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
        return configurationQueriesApi.getMasterDataLookups(null)
                .map(this::toMasterDataDTO);
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
     * Projects the domain-level lookup map into the channel-friendly
     * {@link MasterDataDTO}. Domains that are missing in the map produce
     * empty lists so the front-end never has to null-check a category.
     */
    private MasterDataDTO toMasterDataDTO(Map<String, List<LookupItemDTO>> lookups) {
        return MasterDataDTO.builder()
                .countries(toEntries(lookups, DOMAIN_COUNTRY))
                .currencies(toEntries(lookups, DOMAIN_CURRENCY))
                .documentTypes(toEntries(lookups, DOMAIN_DOCUMENT_TYPE))
                .employmentStatuses(toEntries(lookups, DOMAIN_EMPLOYMENT_STATUS))
                .contractTypes(toEntries(lookups, DOMAIN_CONTRACT_TYPE))
                .housingTypes(toEntries(lookups, DOMAIN_HOUSING_TYPE))
                .maritalStatuses(toEntries(lookups, DOMAIN_MARITAL_STATUS))
                .loanPurposes(toEntries(lookups, DOMAIN_LOAN_PURPOSE))
                .sectors(toEntries(lookups, DOMAIN_SECTOR))
                .assetTypes(toEntries(lookups, DOMAIN_ASSET_TYPE))
                .legalForms(toEntries(lookups, DOMAIN_LEGAL_FORM))
                .employeeRanges(toEntries(lookups, DOMAIN_EMPLOYEE_RANGE))
                .representativeRoles(toEntries(lookups, DOMAIN_REPRESENTATIVE_ROLE))
                .build();
    }

    private List<LookupEntryDTO> toEntries(Map<String, List<LookupItemDTO>> lookups, String domainCode) {
        if (lookups == null) {
            return List.of();
        }
        List<LookupItemDTO> items = lookups.getOrDefault(domainCode, List.of());
        return items.stream()
                .sorted(Comparator.comparing(
                        LookupItemDTO::getSortOrder,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .map(item -> LookupEntryDTO.builder()
                        .code(item.getItemCode())
                        .label(item.getItemLabelDefault())
                        .order(item.getSortOrder())
                        .build())
                .toList();
    }

}
