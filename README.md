# Entity versioning POC

POC that implements entity versioning (tracking changes to an entity, with the possibility of rolling back to a previous version).

[JSON Patch](http://jsonpatch.com/) is used in order to store the diffs between versions. 
In order for JSON patch to be able to work properly we need to have a *deterministic serialization algorithm*.
The implementation used here is inspired by [this post](https://www.stubbornjava.com/posts/creating-a-somewhat-deterministic-jackson-objectmapper).

**Algorithm implementation**

- Serialize the old version and the new version via the `deterministic ObjectMapper`.
- Compute a JSON Patch between the two entities via `JsonDiff.asJson`. 
In order to be able to navigate between versions a forward patch (old version to new version) 
and a backward patch (new version to old version) are stored.
- Store both the forward and backward patches.
- For revert, construct an oriented graph based on the patches, find a path from the current version to the target 
version and apply the patches subsequently via `JsonPatch.apply` as you traverse the graph.

## Running the POC 

**Building the project**
- The project can be built via maven: `./mvnw package`.

**Tests are used in order to demo the functionality**
- `JsonPatchServiceTest` tests the functionality of subsequently applying patches in order to revert operations on 
an entity.
- `VersioningAlgorithmTest` tests the graph traversal algorithm that is used in order to find a path between 
the current entity version and the target entity version.
- `StudentVersioningServiceTest` tests the functionality end-to-end, showing how the algorithm can be applied in
order to version the `Student` entity.
