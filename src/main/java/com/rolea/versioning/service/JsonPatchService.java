package com.rolea.versioning.service;

import com.fasterxml.jackson.databind.JsonNode;

public interface JsonPatchService {

    /**
     * Computes a JSON Patch that captures changes in the new version relative to the old version
     * Applying the resulting patch to the old version will result in the new version
     */
    JsonNode getDiff(Object oldVersion, Object newVersion);

    /**
     * Applies the JSON Patch to the target object
     */
    JsonNode applyPatch(JsonNode patch, Object target);
}
