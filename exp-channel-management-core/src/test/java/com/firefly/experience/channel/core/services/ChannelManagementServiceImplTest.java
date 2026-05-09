package com.firefly.experience.channel.core.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.firefly.domain.configuration.sdk.api.ConfigurationQueriesApi;
import com.firefly.domain.configuration.sdk.model.LookupItemDTO;
import com.firefly.domain.configuration.sdk.model.PaginationResponse;
import com.firefly.domain.configuration.sdk.model.TenantBrandingDTO;
import com.firefly.experience.channel.core.models.LanguageLocaleDTO;
import com.firefly.experience.channel.core.services.impl.ChannelManagementServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ChannelManagementServiceImpl}.
 * <p>
 * The downstream {@link ConfigurationQueriesApi} is mocked. A real {@link ObjectMapper}
 * (with JavaTimeModule) is used to exercise the {@code convertValue()} paths that
 * re-hydrate {@code List<Object>} pagination content into typed model objects.
 */
@ExtendWith(MockitoExtension.class)
class ChannelManagementServiceImplTest {

    @Mock
    private ConfigurationQueriesApi configurationQueriesApi;

    private ChannelManagementServiceImpl service;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        service = new ChannelManagementServiceImpl(configurationQueriesApi, objectMapper);
    }

    // -------------------------------------------------------------------------
    // getLanguages()
    // -------------------------------------------------------------------------

    @Test
    void getLanguages_mapsLocaleListToLanguageDTOs() {
        UUID localeId = UUID.randomUUID();
        LanguageLocaleDTO sdkLocale = LanguageLocaleDTO.builder()
                .localeId(localeId)
                .languageName("English")
                .nativeName("English")
                .sortOrder(0)
                .build();

        PaginationResponse page = new PaginationResponse().content(List.of(sdkLocale));
        when(configurationQueriesApi.getLanguages(any()))
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
        when(configurationQueriesApi.getLanguages(any()))
                .thenReturn(Mono.just(page));

        StepVerifier.create(service.getLanguages())
                .verifyComplete();
    }

    @Test
    void getLanguages_isDefault_falseForNonZeroSortOrder() {
        UUID localeId = UUID.randomUUID();
        LanguageLocaleDTO sdkLocale = LanguageLocaleDTO.builder()
                .localeId(localeId)
                .languageName("Spanish")
                .nativeName("Español")
                .sortOrder(1)
                .build();

        PaginationResponse page = new PaginationResponse().content(List.of(sdkLocale));
        when(configurationQueriesApi.getLanguages(any()))
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
        LanguageLocaleDTO sdkLocale = LanguageLocaleDTO.builder()
                .localeId(localeId)
                .languageName("French")
                .nativeName("Français")
                .sortOrder(2)
                .build();

        PaginationResponse page = new PaginationResponse().content(List.of(sdkLocale));
        when(configurationQueriesApi.getLanguages(any()))
                .thenReturn(Mono.just(page));

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

        when(configurationQueriesApi.getTenantBrandings(any()))
                .thenReturn(Flux.just(sdkBranding));

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
        when(configurationQueriesApi.getTenantBrandings(any()))
                .thenReturn(Flux.empty());

        StepVerifier.create(service.getChannelBranding())
                .verifyComplete();
    }

    // -------------------------------------------------------------------------
    // getMasterData()
    // -------------------------------------------------------------------------

    @Test
    void getMasterData_projectsLookupItemsByDomainCode() {
        LookupItemDTO spain = new LookupItemDTO().itemCode("ES").itemLabelDefault("Spain").sortOrder(0);
        LookupItemDTO france = new LookupItemDTO().itemCode("FR").itemLabelDefault("France").sortOrder(1);
        LookupItemDTO eur = new LookupItemDTO().itemCode("EUR").itemLabelDefault("Euro").sortOrder(0);
        LookupItemDTO passport = new LookupItemDTO().itemCode("PASSPORT").itemLabelDefault("Passport").sortOrder(0);

        Map<String, List<LookupItemDTO>> lookups = new LinkedHashMap<>();
        lookups.put("COUNTRY", List.of(spain, france));
        lookups.put("CURRENCY", List.of(eur));
        lookups.put("DOCUMENT_TYPE", List.of(passport));

        when(configurationQueriesApi.getMasterDataLookups(any()))
                .thenReturn(Mono.just(lookups));

        StepVerifier.create(service.getMasterData())
                .assertNext(dto -> {
                    assertThat(dto.getCountries()).hasSize(2);
                    assertThat(dto.getCountries().get(0).getCode()).isEqualTo("ES");
                    assertThat(dto.getCountries().get(0).getLabel()).isEqualTo("Spain");
                    assertThat(dto.getCurrencies()).hasSize(1);
                    assertThat(dto.getCurrencies().get(0).getCode()).isEqualTo("EUR");
                    assertThat(dto.getDocumentTypes()).hasSize(1);
                    assertThat(dto.getDocumentTypes().get(0).getCode()).isEqualTo("PASSPORT");
                    assertThat(dto.getEmploymentStatuses()).isEmpty();
                })
                .verifyComplete();
    }

    @Test
    void getMasterData_returnsEmptyLists_whenLookupsAreEmpty() {
        when(configurationQueriesApi.getMasterDataLookups(any()))
                .thenReturn(Mono.just(new LinkedHashMap<>()));

        StepVerifier.create(service.getMasterData())
                .assertNext(dto -> {
                    assertThat(dto.getCountries()).isEmpty();
                    assertThat(dto.getCurrencies()).isEmpty();
                    assertThat(dto.getDocumentTypes()).isEmpty();
                    assertThat(dto.getRepresentativeRoles()).isEmpty();
                })
                .verifyComplete();
    }

    @Test
    void getMasterData_sortsItemsByAscendingSortOrder() {
        LookupItemDTO third = new LookupItemDTO().itemCode("C").itemLabelDefault("Third").sortOrder(2);
        LookupItemDTO first = new LookupItemDTO().itemCode("A").itemLabelDefault("First").sortOrder(0);
        LookupItemDTO second = new LookupItemDTO().itemCode("B").itemLabelDefault("Second").sortOrder(1);

        Map<String, List<LookupItemDTO>> lookups = Map.of(
                "COUNTRY", List.of(third, first, second));

        when(configurationQueriesApi.getMasterDataLookups(any()))
                .thenReturn(Mono.just(lookups));

        StepVerifier.create(service.getMasterData())
                .assertNext(dto -> {
                    assertThat(dto.getCountries()).extracting("code")
                            .containsExactly("A", "B", "C");
                })
                .verifyComplete();
    }

    // -------------------------------------------------------------------------
    // getChannelInit()
    // -------------------------------------------------------------------------

    @Test
    void getChannelInit_aggregatesAllThreeSourcesInParallel() {
        // Languages
        LanguageLocaleDTO sdkLocale = LanguageLocaleDTO.builder()
                .localeId(UUID.randomUUID()).languageName("English").nativeName("English").sortOrder(0).build();
        PaginationResponse langPage = new PaginationResponse().content(List.of(sdkLocale));
        when(configurationQueriesApi.getLanguages(any()))
                .thenReturn(Mono.just(langPage));

        // Branding
        TenantBrandingDTO sdkBranding = new TenantBrandingDTO()
                .logoUrl("https://example.com/logo.png").primaryColor("#111111").secondaryColor("#222222").active(true);
        when(configurationQueriesApi.getTenantBrandings(any()))
                .thenReturn(Flux.just(sdkBranding));

        // Master data
        LookupItemDTO usa = new LookupItemDTO().itemCode("US").itemLabelDefault("United States").sortOrder(0);
        Map<String, List<LookupItemDTO>> lookups = Map.of("COUNTRY", List.of(usa));
        when(configurationQueriesApi.getMasterDataLookups(any()))
                .thenReturn(Mono.just(lookups));

        StepVerifier.create(service.getChannelInit())
                .assertNext(dto -> {
                    assertThat(dto.getLanguages()).hasSize(1);
                    assertThat(dto.getLanguages().get(0).getName()).isEqualTo("English");

                    assertThat(dto.getBranding().getLogoUrl()).isEqualTo("https://example.com/logo.png");
                    assertThat(dto.getBranding().getPrimaryColor()).isEqualTo("#111111");

                    assertThat(dto.getMasterData().getCountries()).hasSize(1);
                    assertThat(dto.getMasterData().getCountries().get(0).getCode()).isEqualTo("US");
                    assertThat(dto.getMasterData().getCurrencies()).isEmpty();
                })
                .verifyComplete();
    }

}
