package com.firefly.experience.channel.web.controllers;

import com.firefly.experience.channel.core.queries.BrandingDTO;
import com.firefly.experience.channel.core.queries.ChannelInitDTO;
import com.firefly.experience.channel.core.queries.LanguageDTO;
import com.firefly.experience.channel.core.queries.MasterDataDTO;
import com.firefly.experience.channel.core.services.ChannelManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * REST controller exposing channel management queries.
 * <p>
 * All endpoints are read-only (GET). Responses are fully non-blocking ({@code Mono}/{@code Flux}).
 * The controller delegates all business logic to {@link ChannelManagementService}; no
 * transformation or filtering is performed here beyond HTTP status mapping.
 */
@Tag(name = "Channel Management", description = "Channel initialization and configuration endpoints")
@RestController
@RequestMapping("/api/v1/experience/channel")
@RequiredArgsConstructor
@Slf4j
public class ChannelManagementController {

    private final ChannelManagementService channelManagementService;

    /**
     * Returns the combined channel initialisation payload (languages, branding, master data) in one call.
     *
     * @return {@code 200 OK} with {@link ChannelInitDTO}, or {@code 404} if the service emits empty
     */
    @GetMapping(value = "/init", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Full channel initialization",
            description = "Returns languages, active branding, and reference master data in a single call. "
                    + "Intended for frontend bootstrap — minimises round-trips on app start.")
    public Mono<ResponseEntity<ChannelInitDTO>> getChannelInit() {
        return channelManagementService.getChannelInit()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Lists all available language/locale entries for this channel.
     *
     * @return {@code 200 OK} with a (possibly empty) list of {@link LanguageDTO}
     */
    @GetMapping(value = "/languages", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Available languages and locales",
            description = "Lists all language/locale entries available for this channel.")
    public Mono<ResponseEntity<List<LanguageDTO>>> getLanguages() {
        return channelManagementService.getLanguages()
                .collectList()
                .map(ResponseEntity::ok);
    }

    /**
     * Retrieves a single language entry by its locale identifier.
     *
     * @param localeId UUID string of the locale (as returned by {@link #getLanguages()})
     * @return {@code 200 OK} with the matching {@link LanguageDTO}, or {@code 404} if not found
     */
    @GetMapping(value = "/languages/{localeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Language detail",
            description = "Retrieves a single language entry by its locale identifier.")
    public Mono<ResponseEntity<LanguageDTO>> getLanguage(
            @Parameter(description = "Locale identifier (UUID string)", required = true)
            @PathVariable String localeId) {
        return channelManagementService.getLanguage(localeId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Retrieves the active visual branding configuration for this channel.
     *
     * @return {@code 200 OK} with the active {@link BrandingDTO}, or {@code 404} if none is configured
     */
    @GetMapping(value = "/branding", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Active channel branding",
            description = "Retrieves the active visual branding configuration for this channel.")
    public Mono<ResponseEntity<BrandingDTO>> getChannelBranding() {
        return channelManagementService.getChannelBranding()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Returns all reference master data (countries, currencies, document types).
     *
     * @return {@code 200 OK} with the populated {@link MasterDataDTO}, or {@code 404} if the service emits empty
     */
    @GetMapping(value = "/master-data", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "All reference master data",
            description = "Returns all reference data (countries, currencies, document types) "
                    + "required for form rendering and validation.")
    public Mono<ResponseEntity<MasterDataDTO>> getMasterData() {
        return channelManagementService.getMasterData()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
