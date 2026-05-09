package com.firefly.experience.channel.core.queries;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Journey-layer representation of reference master data required for channel initialisation.
 * <p>
 * Each list contains {@link LookupEntryDTO} items sourced from the
 * domain-core-configuration {@code /lookup-items} endpoint, partitioned by
 * the well-known domain codes that the channel renders.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterDataDTO {

    private List<LookupEntryDTO> countries;
    private List<LookupEntryDTO> currencies;
    private List<LookupEntryDTO> documentTypes;
    private List<LookupEntryDTO> employmentStatuses;
    private List<LookupEntryDTO> contractTypes;
    private List<LookupEntryDTO> housingTypes;
    private List<LookupEntryDTO> maritalStatuses;
    private List<LookupEntryDTO> loanPurposes;
    private List<LookupEntryDTO> sectors;
    private List<LookupEntryDTO> assetTypes;
    private List<LookupEntryDTO> legalForms;
    private List<LookupEntryDTO> employeeRanges;
    private List<LookupEntryDTO> representativeRoles;

}
