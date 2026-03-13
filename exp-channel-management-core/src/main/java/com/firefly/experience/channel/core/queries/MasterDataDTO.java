package com.firefly.experience.channel.core.queries;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Journey-layer representation of reference master data required for channel initialisation.
 * <p>
 * Each list contains {@code LookupDomainDTO} items sourced from the reference-master-data SDK,
 * partitioned by conventional domain-code prefix (COUNTRY*, CURRENCY*, DOCUMENT_TYPE*).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterDataDTO {

    /** Available country lookup entries. */
    private List<Object> countries;

    /** Available currency lookup entries. */
    private List<Object> currencies;

    /** Available document-type lookup entries. */
    private List<Object> documentTypes;

}
