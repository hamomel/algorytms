import java.io.File

object TestCases {

    private val path = FenParser::class.java.getResource("Chess-Tasks")
    private val file = File(path.toURI())
    private val cases = file.listFiles()

    fun getCasesFor(caseType: String): Array<out File> =
        TestCases.cases.first { it.name == caseType }.listFiles()
}