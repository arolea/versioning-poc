package com.rolea.versioning.service.algorithms;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntityPatch {

    private Long id;
    private String patchName;
    private String sourceVersion;
    private String targetVersion;

    private JsonNode patch;

}
