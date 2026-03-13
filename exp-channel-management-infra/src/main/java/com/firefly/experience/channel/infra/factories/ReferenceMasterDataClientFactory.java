package com.firefly.experience.channel.infra.factories;

import com.firefly.common.reference.master.data.sdk.api.LanguageLocaleApi;
import com.firefly.common.reference.master.data.sdk.api.LookupDomainsApi;
import com.firefly.common.reference.master.data.sdk.invoker.ApiClient;
import com.firefly.experience.channel.infra.properties.ReferenceMasterDataProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Spring factory that wires reactive API clients for the {@code core-common-reference-master-data} service.
 * <p>
 * A single {@link ApiClient} is shared across all API beans produced by this factory, pointing
 * to the base URL configured under {@code api-configuration.core-platform.reference-master-data}.
 * <p>
 * Available API classes from the SDK (selected for channel management needs):
 * <ul>
 *   <li>{@link LanguageLocaleApi} — language and locale catalogue (locales, display names, RTL flags)</li>
 *   <li>{@link LookupDomainsApi} — generic reference-data domain catalogue (lookup categories)</li>
 * </ul>
 */
@Component
public class ReferenceMasterDataClientFactory {

    private final ApiClient apiClient;

    public ReferenceMasterDataClientFactory(ReferenceMasterDataProperties properties) {
        this.apiClient = new ApiClient();
        this.apiClient.setBasePath(properties.getBasePath());
    }

    /**
     * Provides a reactive client for language and locale lookups.
     *
     * @return a configured {@link LanguageLocaleApi} instance
     */
    @Bean
    public LanguageLocaleApi languageLocaleApi() {
        return new LanguageLocaleApi(apiClient);
    }

    /**
     * Provides a reactive client for generic reference-data domain lookups.
     *
     * @return a configured {@link LookupDomainsApi} instance
     */
    @Bean
    public LookupDomainsApi lookupDomainsApi() {
        return new LookupDomainsApi(apiClient);
    }

}
