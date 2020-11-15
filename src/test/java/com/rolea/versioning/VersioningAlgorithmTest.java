package com.rolea.versioning;

import com.rolea.versioning.service.algorithms.EntityPatch;
import com.rolea.versioning.service.algorithms.VersioningAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class VersioningAlgorithmTest {

    /**
     *           v1
     *         |    \     \
     *      v2.1    v2.2   v2.3
     *      /   \     \
     *   v3.1   v3.2  v3.3
     *                /   \
     *              v4.1  v4.2 (current)
     */
    @Test
    public void test_findVersionPath() {
        List<EntityPatch> entityPatches = asList(
                EntityPatch.builder().id(1L).patchName("ForwardPatchV1V2.1")
                        .sourceVersion("v1").targetVersion("v2.1").build(),
                EntityPatch.builder().id(2L).patchName("BackwardPatchV2.1V1")
                        .sourceVersion("v2.1").targetVersion("v1").build(),
                EntityPatch.builder().id(3L).patchName("ForwardPatchV2.1V3.1")
                        .sourceVersion("v2.1").targetVersion("v3.1").build(),
                EntityPatch.builder().id(4L).patchName("BackwardPatchV3.1V2.1")
                        .sourceVersion("v3.1").targetVersion("v2.1").build(),
                EntityPatch.builder().id(5L).patchName("ForwardPatchV2.1V3.2")
                        .sourceVersion("v2.1").targetVersion("v3.2").build(),
                EntityPatch.builder().id(6L).patchName("BackwardPatchV3.21V2.1")
                        .sourceVersion("v3.2").targetVersion("v2.1").build(),
                EntityPatch.builder().id(7L).patchName("ForwardPatchV1V2.2")
                        .sourceVersion("v1").targetVersion("v2.2").build(),
                EntityPatch.builder().id(8L).patchName("BackwardPatchV2.2V1")
                        .sourceVersion("v2.2").targetVersion("v1").build(),
                EntityPatch.builder().id(9L).patchName("ForwardPatchV2.2V3.3")
                        .sourceVersion("v2.2").targetVersion("v3.3").build(),
                EntityPatch.builder().id(10L).patchName("BackwardPatchV3.3V2.2")
                        .sourceVersion("v3.3").targetVersion("v2.2").build(),
                EntityPatch.builder().id(11L).patchName("ForwardPatchV3.3V4.1")
                        .sourceVersion("v3.3").targetVersion("v4.1").build(),
                EntityPatch.builder().id(12L).patchName("BackwardPatchV4.1V3.3")
                        .sourceVersion("v4.1").targetVersion("v3.3").build(),
                EntityPatch.builder().id(13L).patchName("ForwardPatchV3.3V4.2")
                        .sourceVersion("v3.3").targetVersion("v4.2").build(),
                EntityPatch.builder().id(14L).patchName("BackwardPatchV4.2V3.3")
                        .sourceVersion("v4.2").targetVersion("v3.3").build(),
                EntityPatch.builder().id(15L).patchName("ForwardPatchV1V2.3")
                        .sourceVersion("v1").targetVersion("v2.3").build(),
                EntityPatch.builder().id(16L).patchName("BackwardPatchV2.3V1")
                        .sourceVersion("v2.3").targetVersion("v1").build()
        );

        log.info("Test simple rollback");
        List<EntityPatch> path = VersioningAlgorithm.getPath("v4.2", "v3.3", entityPatches);
        path.forEach(patch -> log.info(patch.toString()));
        assertThat(path).isNotNull();

        log.info("Test backward-forward");
        path = VersioningAlgorithm.getPath("v4.2", "v3.2", entityPatches);
        path.forEach(patch -> log.info(patch.toString()));
        assertThat(path).isNotNull();
    }

}
