package com.firefly.experience.channel.core.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.firefly.common.config.sdk.api.TenantBrandingsApi;
import com.firefly.common.config.sdk.model.PaginationResponseTenantBrandingDTO;
import com.firefly.common.config.sdk.model.TenantBrandingDTO;
import com.firefly.common.reference.master.data.sdk.api.LanguageLocaleApi;
import com.firefly.common.reference.master.data.sdk.api.LookupDomainsApi;
import com.firefly.common.reference.master.data.sdk.model.LanguageLocaleDTO;
import com.firefly.common.reference.master.data.sdk.model.LookupDomainDTO;
import com.firefly.common.reference.master.data.sdk.model.PaginationResponse;
import com.firefly.experience.channel.core.queries.BrandingDTO;
import com.firefly.experience.channel.core.queries.ChannelInitDTO;
import com.firefly.experience.channel.core.queries.LanguageDTO;
import com.firefly.experience.channel.core.queries.MasterDataDTO;
import com.firefly.experience.channel.core.services.impl.ChannelManagementServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ChannelManagementServiceImpl}.
 * <p>
 * All downstream SDK APIs are mocked. A real {@link ObjectMapper} (with JavaTimeModule) is
 * used to exercise the {@code convertValue()} paths that re-hydrate {@code List<Object>}
 * pagination content into typed SDK model objects.
 */
@ExtendWith(MockitoExtension.class)
class ChannelManagementServiceImplTest {

    @Mock
    private LanguageLocaleApi languageLocaleApi;
    @Mock
    private LookupDomainsApi lookupDomainsApi;
    @Mock
    private TenantBrandingsApi tenantBrandingsApi;

    private ChannelManagementServiceImpl service;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        service = new ChannelManagementServiceImpl(languageLocaleApi, lookupDomainsApi, tenantBrandingsApi, objectMapper);
    }

    // -------------------------------------------------------------------------
    // getLanguages()
    // -------------------------------------------------------------------------

    @Test
    void getLanguages_mapsLocaleListToLanguageDTOs() {
        UUID localeId = UUID.randomUUID();
        LanguageLocaleDTO sdkLocale = new LanguageLocaleDTO()
                .localeId(localeId)
                .languageName("English")
                .nativeName("English")
                .sortOrder(0);

        PaginationResponse page = new PaginationResponse().content(List.of(sdkLocale));
        when(languageLocaleApi.listLanguageLocales(anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(Mono.just(page));

        StepVerifier.create(service.getLanguages())
                .assertNext(dto -> {
                    assertThat(dto.getLocaleId()).isEqualTo(localeId.toString());
                    assertThat(dto.getName()).isEqualTo("English");
                    assertThat(dto.getNativeName()).isEqualTo("English");
                    assertThat(dto.isDefault()).isTrue();
                })
                .verifyComplete();
    }

    @Test
    void getLanguages_returnsEmpty_whenPageContentIsNull() {
        PaginationResponse page = new PaginationResponse();
        when(languageLocaleApi.listLanguageLocales(anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(Mono.just(page));

        StepVerifier.create(service.getLanguages())
                .verifyComplete();
    }

    @Test
    void getLanguages_isDefault_falseForNonZeroSortOrder() {
        UUID localeId = UUID.randomUUID();
        LanguageLocaleDTO sdkLocale = new LanguageLocaleDTO()
                .localeId(localeId)
                .languageName("Spanish")
                .nativeName("Español")
                .sortOrder(1);

        PaginationResponse page = new PaginationResponse().content(List.of(sdkLocale));
        when(languageLocaleApi.listLanguageLocales(anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(Mono.just(page));

        StepVerifier.create(service.getLanguages())
                .assertNext(dto -> assertThat(dto.isDefault()).isFalse())
                .verifyComplete();
    }

    // -------------------------------------------------------------------------
    // getLanguage(localeId)
    // -------------------------------------------------------------------------

    @Test
    void getLanguage_fetchesSingleLocaleById() {
        UUID localeId = UUID.randomUUID();
        LanguageLocaleDTO sdkLocale = new LanguageLocaleDTO()
                .localeId(localeId)
                .languageName("French")
                .nativeName("Français")
                .sortOrder(2);

        when(languageLocaleApi.getLanguageLocale(eq(localeId))).thenReturn(Mono.just(sdkLocale));

        StepVerifier.create(service.getLanguage(localeId.toString()))
                .assertNext(dto -> {
                    assertThat(dto.getLocaleId()).isEqualTo(localeId.toString());
                    assertThat(dto.getName()).isEqualTo("French");
                    assertThat(dto.getNativeName()).isEqualTo("Français");
                    assertThat(dto.isDefault()).isFalse();
                })
                .verifyComplete();
    }

    // -------------------------------------------------------------------------
    // getChannelBranding()
    // -------------------------------------------------------------------------

    @Test
    void getChannelBranding_mapsActiveBrandingToBrandingDTO() {
        TenantBrandingDTO sdkBranding = new TenantBrandingDTO()
                .logoUrl("https://example.com/logo.png")
                .primaryColor("#FF5733")
                .secondaryColor("#33FF57")
                .active(true);

        PaginationResponseTenantBrandingDTO page =
                new PaginationResponseTenantBrandingDTO().content(List.of(sdkBranding));

        // Option C: verify active=true is always passed; ignore other params
        when(tenantBrandingsApi.filterTenantBrandings(
                anyInt(), anyInt(),
                any(), any(), any(), any(),
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),
                eq(true),
                any(), any(), any(), any(), any(), any(), any(), any(), any(),
                any()
        )).thenReturn(Mono.just(page));

        StepVerifier.create(service.getChannelBranding())
                .assertNext(dto -> {
                    assertThat(dto.getLogoUrl()).isEqualTo("https://example.com/logo.png");
                    assertThat(dto.getPrimaryColor()).isEqualTo("#FF5733");
                    assertThat(dto.getSecondaryColor()).isEqualTo("#33FF57");
                    assertThat(dto.getTheme()).isNull();
                })
                .verifyComplete();
    }

    @Test
    void getChannelBranding_returnsEmpty_whenNoBrandingFound() {
        PaginationResponseTenantBrandingDTO emptyPage =
                new PaginationResponseTenantBrandingDTO().content(List.of());

        when(tenantBrandingsApi.filterTenantBrandings(
                anyInt(), anyInt(),
                any(), any(), any(), any(),
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),
                eq(true),
                any(), any(), any(), any(), any(), any(), any(), any(), any(),
                any()
        )).thenReturn(Mono.just(emptyPage));

        StepVerifier.create(service.getChannelBranding())
                .verifyComplete();
    }

    // -------------------------------------------------------------------------
    // getMasterData()
    // -------------------------------------------------------------------------

    @Test
    void getMasterData_partitionsDomainsByCodePrefix() {
        LookupDomainDTO country = new LookupDomainDTO().domainCode("COUNTRY_ES").domainName("Spain").status(LookupDomainDTO.StatusEnum.ACTIVE);
        LookupDomainDTO currency = new LookupDomainDTO().domainCode("CURRENCY_EUR").domainName("Euro").status(LookupDomainDTO.StatusEnum.ACTIVE);
        LookupDomainDTO docType = new LookupDomainDTO().domainCode("DOCUMENT_TYPE_PASSPORT").domainName("Passport").status(LookupDomainDTO.StatusEnum.ACTIVE);
        LookupDomainDTO other = new LookupDomainDTO().domainCode("OTHER_THING").domainName("Other").status(LookupDomainDTO.StatusEnum.ACTIVE);

        PaginationResponse page = new PaginationResponse()
                .content(List.of(country, currency, docType, other));

        when(lookupDomainsApi.listDomains(anyInt(), anyInt(), any(), any()))
                .thenReturn(Mono.just(page));

        StepVerifier.create(service.getMasterData())
                .assertNext(dto -> {
                    assertThat(dto.getCountries()).hasSize(1);
                    assertThat(dto.getCurrencies()).hasSize(1);
                    assertThat(dto.getDocumentTypes()).hasSize(1);
                })
                .verifyComplete();
    }

    @Test
    void getMasterData_returnsEmptyLists_whenNoDomains() {
        PaginationResponse page = new PaginationResponse().content(List.of());
        when(lookupDomainsApi.listDomains(anyInt(), anyInt(), any(), any()))
                .thenReturn(Mono.just(page));

        StepVerifier.create(service.getMasterData())
                .assertNext(dto -> {
                    assertThat(dto.getCountries()).isEmpty();
                    assertThat(dto.getCurrencies()).isEmpty();
                    assertThat(dto.getDocumentTypes()).isEmpty();
                })
                .verifyComplete();
    }

    // -------------------------------------------------------------------------
    // getChannelInit()
    // -------------------------------------------------------------------------

    @Test
    void getChannelInit_aggregatesAllThreeSourcesInParallel() {
        // Languages
        LanguageLocaleDTO sdkLocale = new LanguageLocaleDTO()
                .localeId(UUID.randomUUID()).languageName("English").nativeName("English").sortOrder(0);
        PaginationResponse langPage = new PaginationResponse().content(List.of(sdkLocale));
        when(languageLocaleApi.listLanguageLocales(anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(Mono.just(langPage));

        // Branding
        TenantBrandingDTO sdkBranding = new TenantBrandingDTO()
                .logoUrl("https://example.com/logo.png").primaryColor("#111111").secondaryColor("#222222");
        PaginationResponseTenantBrandingDTO brandPage =
                new PaginationResponseTenantBrandingDTO().content(List.of(sdkBranding));
        when(tenantBrandingsApi.filterTenantBrandings(
                anyInt(), anyInt(),
                any(), any(), any(), any(),
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),
                eq(true),
                any(), any(), any(), any(), any(), any(), any(), any(), any(),
                any()
        )).thenReturn(Mono.just(brandPage));

        // Master data
        LookupDomainDTO country = new LookupDomainDTO().domainCode("COUNTRY_US").domainName("USA").status(LookupDomainDTO.StatusEnum.ACTIVE);
        PaginationResponse mdPage = new PaginationResponse().content(List.of(country));
        when(lookupDomainsApi.listDomains(anyInt(), anyInt(), any(), any()))
                .thenReturn(Mono.just(mdPage));

        StepVerifier.create(service.getChannelInit())
                .assertNext(dto -> {
                    assertThat(dto.getLanguages()).hasSize(1);
                    assertThat(dto.getLanguages().get(0).getName()).isEqualTo("English");

                    assertThat(dto.getBranding().getLogoUrl()).isEqualTo("https://example.com/logo.png");
                    assertThat(dto.getBranding().getPrimaryColor()).isEqualTo("#111111");

                    assertThat(dto.getMasterData().getCountries()).hasSize(1);
                    assertThat(dto.getMasterData().getCurrencies()).isEmpty();
                })
                .verifyComplete();
    }

}
