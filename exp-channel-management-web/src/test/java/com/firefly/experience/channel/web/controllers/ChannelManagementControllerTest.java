package com.firefly.experience.channel.web.controllers;

import com.firefly.experience.channel.core.queries.BrandingDTO;
import com.firefly.experience.channel.core.queries.ChannelInitDTO;
import com.firefly.experience.channel.core.queries.LanguageDTO;
import com.firefly.experience.channel.core.queries.MasterDataDTO;
import com.firefly.experience.channel.core.services.ChannelManagementService;
import org.fireflyframework.web.error.config.ErrorHandlingProperties;
import org.fireflyframework.web.error.converter.ExceptionConverterService;
import org.fireflyframework.web.error.service.ErrorResponseNegotiator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Controller slice tests for {@link ChannelManagementController}.
 * <p>
 * Uses {@link WebFluxTest} to boot only the reactive web layer. The
 * {@link ChannelManagementService} is mocked via {@link MockBean}.
 * <p>
 * The framework's {@code GlobalExceptionHandler} is a {@code @RestControllerAdvice} that
 * gets included in the test slice but requires infrastructure beans not present in the
 * slice context. These three {@code @MockBean}s satisfy its constructor dependencies.
 */
@WebFluxTest(
        controllers = ChannelManagementController.class,
        excludeAutoConfiguration = {
                ReactiveSecurityAutoConfiguration.class,
                ReactiveUserDetailsServiceAutoConfiguration.class
        })
class ChannelManagementControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ChannelManagementService channelManagementService;

    // --- Framework GlobalExceptionHandler dependencies (required by @WebFluxTest slice) ---
    @MockBean
    private ExceptionConverterService exceptionConverterService;
    @MockBean
    private ErrorHandlingProperties errorHandlingProperties;
    @MockBean
    private ErrorResponseNegotiator errorResponseNegotiator;

    // -------------------------------------------------------------------------
    // GET /api/v1/experience/channel/init
    // -------------------------------------------------------------------------

    @Test
    void getChannelInit_returns200WithChannelInitDTO() {
        ChannelInitDTO dto = ChannelInitDTO.builder()
                .languages(List.of(LanguageDTO.builder().localeId("en-GB").name("English")
                        .nativeName("English").isDefault(true).build()))
                .branding(BrandingDTO.builder()
                        .logoUrl("https://example.com/logo.png")
                        .primaryColor("#FF5733")
                        .secondaryColor("#33FF57")
                        .build())
                .masterData(MasterDataDTO.builder()
                        .countries(List.of())
                        .currencies(List.of())
                        .documentTypes(List.of())
                        .build())
                .build();

        when(channelManagementService.getChannelInit()).thenReturn(Mono.just(dto));

        webTestClient.get()
                .uri("/api/v1/experience/channel/init")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ChannelInitDTO.class)
                .value(response -> {
                    assertThat(response.getLanguages()).hasSize(1);
                    assertThat(response.getLanguages().get(0).getName()).isEqualTo("English");
                    assertThat(response.getBranding().getLogoUrl()).isEqualTo("https://example.com/logo.png");
                    assertThat(response.getMasterData().getCountries()).isEmpty();
                });
    }

    @Test
    void getChannelInit_returns404_whenServiceReturnsEmpty() {
        when(channelManagementService.getChannelInit()).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/api/v1/experience/channel/init")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    // -------------------------------------------------------------------------
    // GET /api/v1/experience/channel/languages
    // -------------------------------------------------------------------------

    @Test
    void getLanguages_returns200WithLanguageList() {
        LanguageDTO english = LanguageDTO.builder()
                .localeId("en-GB").name("English").nativeName("English").isDefault(true).build();
        LanguageDTO spanish = LanguageDTO.builder()
                .localeId("es-ES").name("Spanish").nativeName("Español").isDefault(false).build();

        when(channelManagementService.getLanguages()).thenReturn(Flux.just(english, spanish));

        webTestClient.get()
                .uri("/api/v1/experience/channel/languages")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(LanguageDTO.class)
                .value(list -> {
                    assertThat(list).hasSize(2);
                    assertThat(list.get(0).getLocaleId()).isEqualTo("en-GB");
                    assertThat(list.get(1).getNativeName()).isEqualTo("Español");
                });
    }

    @Test
    void getLanguages_returns200WithEmptyList_whenNoLanguagesAvailable() {
        when(channelManagementService.getLanguages()).thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/api/v1/experience/channel/languages")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(LanguageDTO.class)
                .value(list -> assertThat(list).isEmpty());
    }

    // -------------------------------------------------------------------------
    // GET /api/v1/experience/channel/languages/{localeId}
    // -------------------------------------------------------------------------

    @Test
    void getLanguage_returns200WithLanguageDTO() {
        LanguageDTO dto = LanguageDTO.builder()
                .localeId("fr-FR").name("French").nativeName("Français").isDefault(false).build();

        when(channelManagementService.getLanguage("fr-FR")).thenReturn(Mono.just(dto));

        webTestClient.get()
                .uri("/api/v1/experience/channel/languages/fr-FR")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(LanguageDTO.class)
                .value(response -> {
                    assertThat(response.getLocaleId()).isEqualTo("fr-FR");
                    assertThat(response.getName()).isEqualTo("French");
                    assertThat(response.getNativeName()).isEqualTo("Français");
                    assertThat(response.isDefault()).isFalse();
                });
    }

    @Test
    void getLanguage_returns404_whenLocaleNotFound() {
        when(channelManagementService.getLanguage("xx-XX")).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/api/v1/experience/channel/languages/xx-XX")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    // -------------------------------------------------------------------------
    // GET /api/v1/experience/channel/branding
    // -------------------------------------------------------------------------

    @Test
    void getChannelBranding_returns200WithBrandingDTO() {
        BrandingDTO dto = BrandingDTO.builder()
                .logoUrl("https://example.com/logo.png")
                .primaryColor("#FF5733")
                .secondaryColor("#33FF57")
                .theme("LIGHT")
                .build();

        when(channelManagementService.getChannelBranding()).thenReturn(Mono.just(dto));

        webTestClient.get()
                .uri("/api/v1/experience/channel/branding")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BrandingDTO.class)
                .value(response -> {
                    assertThat(response.getLogoUrl()).isEqualTo("https://example.com/logo.png");
                    assertThat(response.getPrimaryColor()).isEqualTo("#FF5733");
                    assertThat(response.getSecondaryColor()).isEqualTo("#33FF57");
                    assertThat(response.getTheme()).isEqualTo("LIGHT");
                });
    }

    @Test
    void getChannelBranding_returns404_whenNoBrandingConfigured() {
        when(channelManagementService.getChannelBranding()).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/api/v1/experience/channel/branding")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    // -------------------------------------------------------------------------
    // GET /api/v1/experience/channel/master-data
    // -------------------------------------------------------------------------

    @Test
    void getMasterData_returns200WithMasterDataDTO() {
        MasterDataDTO dto = MasterDataDTO.builder()
                .countries(List.of("COUNTRY_ES"))
                .currencies(List.of("CURRENCY_EUR"))
                .documentTypes(List.of("DOCUMENT_TYPE_PASSPORT"))
                .build();

        when(channelManagementService.getMasterData()).thenReturn(Mono.just(dto));

        webTestClient.get()
                .uri("/api/v1/experience/channel/master-data")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MasterDataDTO.class)
                .value(response -> {
                    assertThat(response.getCountries()).hasSize(1);
                    assertThat(response.getCurrencies()).hasSize(1);
                    assertThat(response.getDocumentTypes()).hasSize(1);
                });
    }

    @Test
    void getMasterData_returns404_whenServiceReturnsEmpty() {
        when(channelManagementService.getMasterData()).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/api/v1/experience/channel/master-data")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }
}
