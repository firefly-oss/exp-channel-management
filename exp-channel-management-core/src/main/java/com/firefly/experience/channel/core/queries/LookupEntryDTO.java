package com.firefly.experience.channel.core.queries;

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
