import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class HtmlParser {
    fun parse(html: String): ParsedResult {
        val doc: Document = Jsoup.parse(html)
        val title = doc.select("h1").first()?.text() ?: ""

        val entityPermutations = mutableListOf<EntityPermutations>()
        val spexAssertions = mutableListOf<SpexAssertion>()

        doc.select("h2").forEach { header ->
            val nextElement = header.nextElementSibling()
            if (nextElement?.tagName().equals("table")) {
                val tableTitle = header.text()
                val headers = nextElement?.select("thead th")?.map { it.text() } ?: emptyList()
                val rows = nextElement?.select("tbody tr")?.map { row ->
                    row.select("td").map { cell -> cell.text() }
                } ?: emptyList()

                if (tableTitle != "Assertions") {
                    entityPermutations.add(EntityPermutations(tableTitle, headers, rows))
                } else {
                    rows.forEach { row ->
                        val userOf = row.first()
                        val permissions = headers.drop(1).zip(row.drop(1)).associate { (header, value) ->
                            header to (value.toLowerCase() == "y")
                        }
                        spexAssertions.add(SpexAssertion(userOf, permissions))
                    }
                }
            }
        }

        return ParsedResult(SpexState(title, entityPermutations), spexAssertions)
    }
}
