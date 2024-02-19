import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.File

object SpexSaver: (ParsedResult) -> Unit {
    override fun invoke(parsedResult: ParsedResult) {
        val (state, assertions) = parsedResult
        val sb = StringBuilder()
        // Start of HTML document
        sb.append(
            """
            <html>
            <head>
            <title>${state.title}</title>
            <style>
                body { font-family: Arial, sans-serif; margin: 20px; }
                h1, h2 { color: #333; }
                table { border-collapse: collapse; margin-top: 20px; }
                th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
                th { background-color: #f2f2f2; }
                tr:nth-child(even) { background-color: #f9f9f9; }
            </style>
            </head>
            <body>
            <h1>${state.title}</h1>
            <hr>
        """.trimIndent()
        )
        // Tables
        state.entityPermutations.forEach { table ->
            sb.append("<h2>${table.title}</h2>\n")
            sb.append("<table border=\"1\">\n<thead>\n<tr>")
            table.headers.forEach { header ->
                sb.append("<th>$header</th>")
            }
            sb.append("</tr>\n</thead>\n<tbody>\n")
            table.rows.forEach { row ->
                sb.append("<tr>")
                row.forEach { cell ->
                    if (cell == "-") sb.append("<td></td>")
                    else sb.append("<td>$cell</td>")
                }
                sb.append("</tr>\n")
            }
            sb.append("</tbody>\n</table>\n")
            sb.append("<hr>\n")
        }
        // Assertions
        if (assertions.isNotEmpty<SpexAssertion>()) {
            sb.append("<h2>Assertions</h2>\n")
            sb.append("<table border=\"1\">\n<thead>\n<tr>")
            val headers = listOf("User of") + assertions.first().permissions.keys.sorted()
            headers.forEach { header ->
                sb.append("<th>$header</th>")
            }
            sb.append("</tr>\n</thead>\n<tbody>\n")
            assertions.forEach<SpexAssertion> { assertion ->
                sb.append("<tr>")
                sb.append("<td>${assertion.actor}</td>")
                headers.drop(1).forEach { key ->
                    val access = if (assertion.permissions[key] == true) "y" else ""
                    sb.append("<td>$access</td>")
                }
                sb.append("</tr>\n")
            }
            sb.append("</tbody>\n</table>\n")
        }
        // End of HTML document
        sb.append("</body>\n</html>")
        val doc: Document = Jsoup.parse(sb.toString())
        doc.outputSettings().prettyPrint(true) // Enable pretty printing
            .indentAmount(4) // Set indentation (e.g., 4 spaces)
            .outline(true) // Enable outline mode
        val html = doc.outerHtml()
        File("spex.html").writeText(html)
    }
}
