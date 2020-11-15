package com.rolea.versioning.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rolea.versioning.entities.Student;
import com.rolea.versioning.service.JsonPatchService;
import com.rolea.versioning.service.StudentVersioningService;
import com.rolea.versioning.service.algorithms.EntityPatch;
import com.rolea.versioning.service.algorithms.VersioningAlgorithm;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
@Slf4j
public class StudentVersioningServiceImpl implements StudentVersioningService {

    @Autowired
    private JsonPatchService jsonPatchService;

    @Autowired
    private ObjectMapper objectMapper;

    // This is only a POC, this should be stored in the DB
    private final List<EntityPatch> entityPatches = new LinkedList<>();
    private Long idGenerator = 0L;

    @Override
    public void versionStudent(Student oldVersion, Student newVersion) {
        JsonNode forwardPatch = jsonPatchService.getDiff(oldVersion, newVersion);
        JsonNode backwardPatch = jsonPatchService.getDiff(newVersion, oldVersion);

        entityPatches.add(EntityPatch.builder()
                .id(idGenerator++)
                .patchName("ForwardPatch" + oldVersion.getVersion() + newVersion.getVersion())
                .patch(forwardPatch)
                .sourceVersion(oldVersion.getVersion())
                .targetVersion(newVersion.getVersion())
                .build());

        entityPatches.add(EntityPatch.builder()
                .id(idGenerator++)
                .patchName("BackwardPatch" + newVersion.getVersion() + oldVersion.getVersion())
                .patch(backwardPatch)
                .sourceVersion(newVersion.getVersion())
                .targetVersion(oldVersion.getVersion())
                .build());
    }

    @Override
    @SneakyThrows
    public Student rollbackStudent(String currentVersion, String targetVersion, Student student) {
        List<EntityPatch> patchesList = VersioningAlgorithm.getPath(currentVersion, targetVersion, entityPatches);

        Student result = student;
        for(EntityPatch patch : patchesList) {
            log.info("Applying patch {}", patch.getPatchName());
            String patchJson = jsonPatchService.applyPatch(patch.getPatch(), result).toString();
            result = objectMapper.readValue(patchJson, Student.class);
            log.info("Successfully applied patch {}", patch.getPatchName());
        }

        return result;
    }

}
