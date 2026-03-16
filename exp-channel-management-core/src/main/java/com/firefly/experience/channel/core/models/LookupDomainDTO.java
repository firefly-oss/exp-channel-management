package com.firefly.experience.channel.core.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Local representation of a lookup-domain record returned by the
 * {@code domain-core-configuration} service inside a {@code PaginationResponse}.
 * <p>
 * Used as the target type for {@link com.fasterxml.jackson.databind.ObjectMapper#convertValue}
 * when deserialising the untyped {@code List<Object>} content of the pagination envelope.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LookupDomainDTO {

    private UUID domainId;
    private String domainCode;
    private String domainName;
    private String domainDesc;
    private UUID parentDomainId;
    private Boolean multiselectAllowed;
    private Boolean hierarchyAllowed;
    private Boolean tenantOverridable;
    private String extraJson;
    private UUID tenantId;
    private String status;

}
