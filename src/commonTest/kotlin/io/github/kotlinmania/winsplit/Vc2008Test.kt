// port-lint: source src/vc_2008.rs
package io.github.kotlinmania.winsplit

import kotlin.test.Test
import kotlin.test.assertEquals

class Vc2008Test {

    @Test
    fun shouldSupportSingleWord() {
        val args = parse("word")
        assertEquals(listOf("word"), args)
    }

    @Test
    fun shouldSupportProgramPathAtBeginning() {
        val args = parse("C:\\path\\to\\program.exe")
        assertEquals(listOf("C:\\path\\to\\program.exe"), args)
    }

    @Test
    fun shouldSupportQuotedPathAtBeginning() {
        val args = parse("\"C:\\path\\to the\\program.exe\" arg -arg2 --arg3")
        assertEquals(
            listOf("C:\\path\\to the\\program.exe", "arg", "-arg2", "--arg3"),
            args,
        )
    }

    @Test
    fun shouldSupportQuotedArgs() {
        val args = parse("\"quoted arg\"")
        assertEquals(listOf("quoted arg"), args)
    }

    @Test
    fun shouldTrimWhitespaceAtFront() {
        val args = parse(" \targ")
        assertEquals(listOf("arg"), args)
    }

    @Test
    fun shouldSupportMultipleArgs() {
        val args = parse("one two three")
        assertEquals(listOf("one", "two", "three"), args)
    }

    @Test
    fun shouldSupportMultipleArgsWithQuotes() {
        val args = parse("one \"two and uh\" three")
        assertEquals(listOf("one", "two and uh", "three"), args)
    }

    @Test
    fun shouldSupportEscapingQuotes() {
        val args = parse("one \\\"two\\\" \"three four\" five")
        assertEquals(listOf("one", "\"two\"", "three four", "five"), args)
    }

    @Test
    fun shouldKeepEscapeCharacterIfNotFollowingDoubleQuote() {
        val args = parse("\\\\\\\\")
        assertEquals(listOf("\\\\\\\\"), args)
    }

    @Test
    fun shouldSupportEscapingTheEscapeCharacterAndQuote() {
        val args = parse("\\\\\\\\\\\" some quote ")
        assertEquals(listOf("\\\\\"", "some", "quote"), args)
    }

    @Test
    fun shouldSupportClosingQuoteFollowedByAnotherQuoteIncludingAQuote() {
        val args = parse("one \"two\"\" three")
        assertEquals(listOf("one", "two\" three"), args)
    }

    @Test
    fun shouldSupportTabsAsDelimiters() {
        val args = parse(" \ta \tb\t c\t ")
        assertEquals(listOf("a", "b", "c"), args)
    }

    // Extra tests from https://daviddeley.com/autohotkey/parameters/parameters.htm#WIN
    @Test
    fun extraFromDavidDeleyExamples() {
        // Single word is okay
        assertEquals(listOf("CallMeIshmael"), parse("CallMeIshmael"))

        // Quotes can be used to include whitespace in parameter
        assertEquals(listOf("Call Me Ishmael"), parse("\"Call Me Ishmael\""))

        // Quotes can be anywhere in parameter
        assertEquals(listOf("Call Me Ishmael"), parse("Cal\"l Me I\"shmael"))

        // Escaped quote yields just the quote
        assertEquals(listOf("CallMe\"Ishmael"), parse("CallMe\\\"Ishmael"))

        // Escaped quote yields just the quote even within a quote
        assertEquals(listOf("CallMe\"Ishmael"), parse("\"CallMe\\\"Ishmael\""))

        // Multiple backslash get converted
        //
        // \\\" -> \"
        // (\\ -> \) (\" -> ")
        assertEquals(listOf("CallMe\\\"Ishmael"), parse("\"CallMe\\\\\\\"Ishmael\""))

        // Backslashes not followed immediately by a double quotation mark are interpreted
        // literally
        assertEquals(listOf("a\\\\\\b"), parse("a\\\\\\b"))

        // Backslashes not followed immediately by a double quotation mark are interpreted
        // literally even within quotes
        assertEquals(listOf("a\\\\\\b"), parse("\"a\\\\\\b\""))
    }

    @Test
    fun extraFromDavidDeleyCommonTasks() {
        // Parameter includes double quotes
        assertEquals(listOf("\"Call Me Ishmael\""), parse("\"\\\"Call Me Ishmael\\\"\""))

        // Parameter includes trailing slash
        assertEquals(listOf("C:\\TEST A\\"), parse("\"C:\\TEST A\\\\\""))

        // Parameter includes double quotes and trailing slash
        assertEquals(listOf("\"C:\\TEST A\\\""), parse("\"\\\"C:\\TEST A\\\\\\\"\""))
    }

    @Test
    fun extraFromDavidDeleyExplainedExamples() {
        // Spaces enclosed in double quotes
        assertEquals(listOf("a b c", "d", "e"), parse("\"a b c\"  d  e"))

        // Some escaped quotes
        assertEquals(listOf("ab\"c", "\\", "d"), parse("\"ab\\\"c\"  \"\\\\\"  d"))

        // Backslashes not followed immediately by a double quotation mark are interpreted
        // literally
        assertEquals(listOf("a\\\\\\b", "de fg", "h"), parse("a\\\\\\b d\"e f\"g h"))

        // 2n+1 backslashes before " → n backslashes + a literal "
        assertEquals(listOf("a\\\"b", "c", "d"), parse("a\\\\\\\"b c d"))

        // 2n backslashes followed by a " produce n backslashes + start/end double quoted part
        //
        // the space enclosed in double quotation marks is not a delimiter
        assertEquals(listOf("a\\\\b c", "d", "e"), parse("a\\\\\\\\\"b c\" d e"))
    }

    @Test
    fun extraFromDavidDeleyDoubleDoubleQuoteExamples() {
        assertEquals(listOf("a b c\""), parse("\"a b c\"\""))
        assertEquals(
            listOf("\"CallMeIshmael\"", "b", "c"),
            parse("\"\"\"CallMeIshmael\"\"\"  b  c"),
        )
        assertEquals(listOf("\"Call Me Ishmael\""), parse("\"\"\"Call Me Ishmael\"\"\""))
        assertEquals(
            listOf("\"Call", "Me", "Ishmael", "b", "c"),
            parse("\"\"\"\"Call Me Ishmael\"\" b c"),
        )
    }

    @Test
    fun extraFromDavidDeleyTripleDoubleQuoteExamples() {
        assertEquals(listOf("\"Call Me Ishmael\""), parse("\"\"\"Call Me Ishmael\"\"\""))

        // Same as above
        assertEquals(listOf("\"Call Me Ishmael\""), parse("\\\"\"Call Me Ishmael\"\\\""))

        // Same as above
        assertEquals(listOf("\"Call Me Ishmael\""), parse("\"\\\"Call Me Ishmael\\\"\""))
    }

    @Test
    fun extraFromDavidDeleyQuadrupleDoubleQuoteExamples() {
        assertEquals(
            listOf("\"Call", "Me", "Ishmael\""),
            parse("\"\"\"\"Call Me Ishmael\"\"\"\""),
        )

        // Same as above
        assertEquals(
            listOf("\"Call", "Me", "Ishmael\""),
            parse("\\\"Call Me Ishmael\\\""),
        )
    }
}
