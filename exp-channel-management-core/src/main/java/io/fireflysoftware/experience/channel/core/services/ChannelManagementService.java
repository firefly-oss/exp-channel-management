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


package io.fireflysoftware.experience.channel.core.services;

import io.fireflysoftware.experience.channel.core.queries.BrandingDTO;
import io.fireflysoftware.experience.channel.core.queries.ChannelInitDTO;
import io.fireflysoftware.experience.channel.core.queries.LanguageDTO;
import io.fireflysoftware.experience.channel.core.queries.MasterDataDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service contract for channel management queries.
 * <p>
 * All methods are stateless compositions: they aggregate data from downstream domain
 * services and map it to journey-specific DTOs. No persistent state is maintained.
 */
public interface ChannelManagementService {

    /**
     * Aggregates languages, branding, and master data in a single parallel call.
     * Intended as the channel bootstrap endpoint for frontend initialisation.
     *
     * @return a {@link Mono} emitting the combined {@link ChannelInitDTO}
     */
    Mono<ChannelInitDTO> getChannelInit();

    /**
     * Lists all available language/locale entries for this channel.
     *
     * @return a {@link Flux} emitting each {@link LanguageDTO}
     */
    Flux<LanguageDTO> getLanguages();

    /**
     * Retrieves a single language by its locale identifier.
     *
     * @param localeId UUID string of the locale (as returned by {@link #getLanguages()})
     * @return a {@link Mono} emitting the matching {@link LanguageDTO}
     */
    Mono<LanguageDTO> getLanguage(String localeId);

    /**
     * Retrieves the active visual branding configuration for this channel.
     *
     * @return a {@link Mono} emitting the active {@link BrandingDTO}
     */
    Mono<BrandingDTO> getChannelBranding();

    /**
     * Retrieves all reference master data (countries, currencies, document types).
     *
     * @return a {@link Mono} emitting the populated {@link MasterDataDTO}
     */
    Mono<MasterDataDTO> getMasterData();

}
