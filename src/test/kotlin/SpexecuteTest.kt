import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SpexecuteTest {
    @Test
    fun `can extract a markdown file with multiple tables and build a model`() {
        val markdown = this::class.java.getResource("/render.html")?.readText()!!

        val model = HtmlParser().parse(markdown)

        val testScenarioApplication = TestScenarioApplication()

        testScenarioApplication.assert(model.state, model.spexAssertions)
    }
}

interface SpexAsserter {
    fun assert(state: SpexState, assertions: List<SpexAssertion>)
    val stateRetriever: Map<String, Map<String, Boolean?>>
}

class TestScenarioApplication: SpexAsserter {
    override val stateRetriever = mapOf(
        "C1 Org Admin" to mapOf("C1" to true, "C2" to true, "C2S" to true, "C1S" to true, "C3" to false, "C3S" to false),
        "C2 Org Admin" to mapOf("C1" to false, "C2" to true, "C2S" to true, "C1S" to false, "C3" to false, "C3S" to false),
        "C2S Site Admin" to mapOf("C1" to false, "C2" to false, "C2S" to true, "C1S" to false, "C3" to false, "C3S" to false),
    )

    override fun assert(state: SpexState, assertions: List<SpexAssertion>) {
        assertions.forEach { expectedState ->
            val applicationState = stateRetriever[expectedState.actor]
            assertEquals(applicationState, expectedState.permissions)
        }
    }

}

