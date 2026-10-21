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

/**
 * Channel-friendly projection of a single {@code lookup_item} record.
 * Carries only the fields the front-end needs to render a select/dropdown:
 * a stable code, a localised label and a display order.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LookupEntryDTO {

    /** Stable business code, used as the select option value. */
    private String code;

    /** Human-readable label, used as the option text. */
    private String label;

    /** Display order, ascending. */
    private Integer order;

}
