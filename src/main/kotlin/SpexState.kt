data class SpexState(val title: String, val entityPermutations: List<EntityPermutations>)
data class EntityPermutations(val title: String, val headers: List<String>, val rows: List<List<String>>)
data class SpexAssertion(
    val actor: String,
    val permissions: Map<String, Boolean> // Maps the entity to whether the user has access
)

data class ParsedResult(val state: SpexState, val spexAssertions: List<SpexAssertion>)