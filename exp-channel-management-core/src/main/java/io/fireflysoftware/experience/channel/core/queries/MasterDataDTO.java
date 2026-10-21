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


package io.fireflysoftware.experience.channel.core.queries;

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
