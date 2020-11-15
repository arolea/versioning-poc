package com.rolea.versioning.service.algorithms;

import java.util.*;

import static java.util.Collections.reverse;
import static java.util.stream.Collectors.toList;

public class VersioningAlgorithm {

    /**
     * Finds a paths from the source node to the target node given the list of edges
     *
     * @param source The current version of the object, modeled as a graph node
     * @param target The desired version of the object, modeled as a graph node
     * @param edges  All the available JSON Patches on the entity, modeled as graph edges
     * @return An ordered list of JSON Patches that need to be applied on the source (current) version
     * in order to get the target version
     */
    public static List<EntityPatch> getPath(String source, String target, List<EntityPatch> edges) {
        return getPath(source, target, edges, new HashMap<>());
    }

    private static List<EntityPatch> getPath(String source, String target, List<EntityPatch> edges, Map<String, EntityPatch> predecessors) {
        buildPredecessorsMap(source, edges, predecessors);

        if (predecessors.get(target) == null) {
            return null;
        }

        List<EntityPatch> patchPath = new LinkedList<>();
        String currentVersion = target;
        while (predecessors.get(currentVersion) != null) {
            currentVersion = predecessors.get(currentVersion).getSourceVersion();
            patchPath.add(predecessors.get(currentVersion));
        }
        String initialSource = patchPath.get(0) != null ? patchPath.get(0).getTargetVersion() : source;
        patchPath.add(0, getPatch(initialSource, target, edges));

        reverse(patchPath);

        return patchPath.stream()
                .filter(Objects::nonNull)
                .collect(toList());
    }

    private static void buildPredecessorsMap(String source, List<EntityPatch> edges, Map<String, EntityPatch> predecessors) {
        if (!predecessors.containsKey(source)) {
            predecessors.put(source, null);
        }

        List<String> adjacentVersions = getNeighbors(source, edges).stream()
                .filter(node -> !predecessors.containsKey(node))
                .collect(toList());

        adjacentVersions.forEach(version -> predecessors.put(version, getPatch(source, version, edges)));
        adjacentVersions.forEach(version -> buildPredecessorsMap(version, edges, predecessors));
    }

    private static List<String> getNeighbors(String node, List<EntityPatch> edges) {
        return edges.stream()
                .filter(entityPatch -> entityPatch.getSourceVersion().equals(node))
                .map(EntityPatch::getTargetVersion)
                .collect(toList());
    }

    private static EntityPatch getPatch(String source, String target, List<EntityPatch> edges) {
        return edges.stream()
                .filter(entityPatch -> entityPatch.getSourceVersion().equals(source) &&
                        entityPatch.getTargetVersion().endsWith(target))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Graph is inconsistent"));
    }

}
