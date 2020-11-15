package com.rolea.versioning.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.zjsonpatch.JsonDiff;
import com.flipkart.zjsonpatch.JsonPatch;
import com.rolea.versioning.service.JsonPatchService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JsonPatchServiceImpl implements JsonPatchService {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @SneakyThrows
    public JsonNode getDiff(Object oldVersion, Object newVersion) {
        JsonNode oldJsonTree = objectMapper.readTree(objectMapper.writeValueAsString(oldVersion));
        JsonNode newJsonTree = objectMapper.readTree(objectMapper.writeValueAsString(newVersion));

        return JsonDiff.asJson(oldJsonTree, newJsonTree);
    }

    @Override
    @SneakyThrows
    public JsonNode applyPatch(JsonNode patch, Object target) {
        JsonNode targetJsonTree = objectMapper.readTree(objectMapper.writeValueAsString(target));

        return JsonPatch.apply(patch, targetJsonTree);
    }

}
