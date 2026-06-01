// port-lint: source src/vc_2008.rs
package io.github.kotlinmania.winsplit

/**
 * For this parser, we are following the updated 2008 rules, which are somewhat simpler than the
 * pre-2008 parsing rules. Given that it is 2022, any modern software that would need to take
 * advantage of Windows parameter parsing should be expected to abide by the modern rules given
 * that Windows Vista was released in 2006.
 *
 * ### C++ Rules
 *
 * These are the rules for parsing a command line passed by CreateProcess() to a program written
 * in C/C++:
 *
 * 1. Parameters are always separated by a space or tab (multiple spaces/tabs OK)
 * 2. If the parameter does not contain any spaces, tabs, or double quotes, then all the
 *    characters in the parameter are accepted as is (there is no need to enclose the parameter in
 *    double quotes).
 * 3. Enclose spaces and tabs in a double quoted part
 * 4. A double quoted part can be anywhere within a parameter
 * 5. 2n backslashes followed by a " produce n backslashes + start/end double quoted part
 * 6. 2n+1 backslashes followed by a " produce n backslashes + a literal quotation mark
 * 7. n backslashes not followed by a quotation mark produce n backslashes
 *
 * Undocumented rules regarding double quotes post 2008:
 *
 * 1. Outside a double quoted block a " starts a double quoted block.
 * 2. Inside a double quoted block a " followed by a different character (not another ") ends the
 *    double quoted block.
 * 3. Inside a double quoted block a " followed immediately by another " (i.e. "") causes a single
 *    " to be added to the output, and the double quoted block continues.
 *
 * ### Parsing Examples
 *
 * Command-Line | argv\[1\] | Comment
 * -- | -- | --
 * CallMeIshmael | CallMeIshmael | a plain parameter can contain any characters except {space} {tab}  \\  "
 * "Call Me Ishmael" | Call Me Ishmael | spaces enclosed in a double quoted part
 * Cal"l Me I"shmael | Call Me Ishmael | a double quoted part can be anywhere within a parameter
 * CallMe\\"Ishmael | CallMe"Ishmael | \\" → "
 * "CallMe\\"Ishmael" | CallMe"Ishmael | \\" → "  (whether or not in a double quoted part)
 * "Call Me Ishmael\\\\" | Call Me Ishmael\\ | \\\\" → \\ + " (which may begin or end a double quoted part)
 * "CallMe\\\\\\"Ishmael"  | CallMe\\"Ishmael | \\\\\\" → \\"     (\\\\ → \\)  (\\" → ")
 * a\\\\\\b | a\\\\\\b | backslashes not followed immediately by a double quotation mark are interpreted literally
 * "a\\\\\\b" | a\\\\\\b | whether or not the backslashes are in a double quoted part
 *
 * #### Command Tasks
 *
 * Command-Line | argv\[1\] | Comment
 * -- | -- | --
 * "\\"Call Me Ishmael\\"" | "Call Me Ishmael" | the parameter includes double quotes
 * "C:\\TEST A\\\\" | C:\\TEST A\\ | the parameter includes a trailing slash
 * "\\"C:\\TEST A\\\\\\"" | "C:\\TEST A\\" | the parameter includes double quotes and a trailing slash
 *
 * #### Examples Explained
 *
 * Command-Line Input | argv\[1\] | argv\[2\] | argv\[3\] | Comment
 * -- | -- | -- | -- | --
 * "a b c"  d  e | a b c | d | e | spaces enclosed in double quotes
 * "ab\\"c"  "\\\\"  d | ab"c | \\ | d | \\" → "\\\\" → \\ + begin or end a double quoted part
 * a\\\\\\b d"e f"g h | a\\\\\\b | de fg | h | backslashes not followed immediately by a double
 * quotation mark are interpreted literally. parameters are separated by spaces or tabs.
 * a double quoted part can be anywhere within a parameter. the space enclosed in double
 * quotation marks is not a delimiter.
 * a\\\\\\"b c d | a\\"b | c | d | 2n+1 backslashes before " → n backslashes + a literal "
 * a\\\\\\\\"b c" d e | a\\\\b c | d | e | 2n backslashes followed by a " produce n backslashes
 * + start/end double quoted part. parameters are separated by spaces or tabs.
 * a double quoted part can be anywhere within a parameter. the space enclosed in double
 * quotation marks is not a delimiter.
 *
 * #### Double Double Quote Examples
 *
 * Command-Line Input | argv\[1\] | argv\[2\] | argv\[3\] | argv\[4\] | argv\[5\] | Comment
 * -- | -- | -- | -- | -- | -- | --
 * "a b c"" | a b c" |   |   |   |   | " Begin double quoted part.
 * "" while in a double quoted part → accept 2nd " literally, double quoted part continues.
 * """CallMeIshmael"""  b  c | "CallMeIshmael" | b | c |   |   | " Begin double quoted part.
 * "" while in a double quoted part → accept 2nd " literally, double quoted part continues.
 * " not followed by another " (i.e. not "") while in a double quoted part → ends the double
 * quoted part. Parameters are delimited by spaces or tabs.
 * """Call Me Ishmael""" | "Call Me Ishmael"|   |   |   |   | " Begin double quoted part.
 * "" while in a double quoted part → accept 2nd " literally, double quoted part continues.
 * " not followed by another " (i.e. not "") while in a double quoted part → ends the double
 * quoted part.
 * """"Call Me Ishmael"" b c | "Call | Me | Ishmael | b | c | " Begin double quoted part.
 * "" while in a double quoted part → accept 2nd " literally, double quoted part continues.
 * " not followed by another " (i.e. not "") in a double quoted part → ends the double
 * quoted part. Parameters are delimited by spaces or tabs.
 * (note "" outside of double quoted block begins and then immediately ends a double quoted part.)
 *
 *
 *
 * #### Triple Double Quotes
 *
 * ```text
 *                                      ..."""Call Me Ishmael"""...
 *                                         ↑↑↑               ↑↑↑↑
 * quote #1: Begin double quoted part──────┘├┘               ├┘├┘
 * quotes #2 & 3: Skip 1st " take 2nd " ────┘                │ │
 *                                                           │ │
 * quotes 4 & 5: Skip 1st " take 2nd " ──────────────────────┘ │
 * quote #6: End double quoted part────────────────────────────┘
 * ```
 *
 * ```text
 *  >ShowParams.exe """Call Me Ishmael"""
 *  param 1 = "Call Me Ishmael"
 * ```
 *
 * an alternative method is
 *
 * ```text
 *                    ┌───────────────┐
 *  >ShowParams.exe \""Call Me Ishmael"\"
 *  param 1 = "Call Me Ishmael"
 * ```
 *
 * or
 *
 * ```text
 *                  ┌───────────────────┐
 *  >ShowParams.exe "\"Call Me Ishmael\""
 *  param 1 = "Call Me Ishmael"
 * ```
 *
 * #### Quadruple Double Quotes
 *
 * ```text
 *                                      ...""""Call me Ishmael""""...
 *                                         ↑↑↑↑↑              ↑↑↑↑↑
 * quote #1: Begin double quoted part──────┘├┘├┘              │├┘││
 * quotes #2 & 3: Skip 1st " take 2nd " ────┘ │               ││ ││
 * quote #4: End double quoted part───────────┘               ││ ││
 *                                                            ││ ││
 * quote #5: Begin double quoted part─────────────────────────┘│ ││
 * quotes #6 & 7: Skip 1st " take 2nd " ───────────────────────┘ ││
 * quote #8: End double quoted part──────────────────────────────┘│
 *           Assuming this isn't another " ───────────────────────┘
 * ```
 *
 * ```text
 * >ShowParams.exe """"Call Me Ishmael""""
 *  param 1 = "Call
 *  param 2 = Me
 *  param 3 = Ishmael"
 * ```
 *
 * an alternative method is
 *
 * ```text
 *  >ShowParams.exe \"Call Me Ishmael\"
 *  param 1 = "Call
 *  param 2 = Me
 *  param 3 = Ishmael"
 * ```
 *
 * ### The Rules
 *
 * This is a mirror from David Deley's website to ensure that we have a historical copy in case the website disappears.
 *
 * ![parsingrules](https://user-images.githubusercontent.com/2481802/182859707-008040c5-39eb-4e2a-949a-89911fa5a973.png)
 */

private class ParserState(
    val s: String,
) {
    val args = mutableListOf<String>()
    var arg = StringBuilder()
    var backslashCnt = 0
    var inQuote = false
    var i = 0
    val n = s.length

    fun hasNext(): Boolean = i < n

    fun nextChar(): Char {
        val c = s[i]
        i++
        return c
    }

    private fun isQuoteNext(): Boolean = i < n && s[i] == '"'

    fun handleQuote(): Boolean {
        val evenBackslashCnt = backslashCnt % 2 == 0
        val isQuoteNext = isQuoteNext()
        var skipAddingChar = false

        if (evenBackslashCnt) {
            if (inQuote) {
                if (isQuoteNext) {
                    // Move to second quote (essentially skip it since both are ")
                    i++
                } else {
                    // Flag that we are no longer in a quote
                    inQuote = false
                    // Don't add this doublequote as it is just marking the end of a quote
                    skipAddingChar = true
                }
            } else {
                // Flag that we are now in a quote
                inQuote = true
                // Don't add this doublequote as it is just marking the start of a quote
                skipAddingChar = true
            }
        }
        backslashCnt /= 2
        return skipAddingChar
    }
}

/** Parses a command line string into arguments using the VC++ 2008 rules */
fun parse(s: String): List<String> {
    val state = ParserState(s)
    while (state.hasNext()) {
        val c = state.nextChar()
        if (c == '\\') {
            state.backslashCnt += 1
            continue
        }

        val skipAddingChar =
            if (c == '"') {
                state.handleQuote()
            } else {
                false
            }

        // Add backslashes to arg and reset counter
        if (state.backslashCnt > 0) {
            addNBackslashes(state.arg, state.backslashCnt)
            state.backslashCnt = 0
        }

        // If we are not in a quote, then once we hit whitespace we want to finish the arg,
        // otherwise we consume everything.
        if (!state.inQuote && isWhitespaceOrNull(c)) {
            if (state.arg.isNotEmpty()) {
                state.args.add(state.arg.toString())
                state.arg = StringBuilder()
            }
        } else if (!skipAddingChar) {
            state.arg.append(c)
        }
    }

    // Add any remaining backslashes as these were at the end of the string
    if (state.backslashCnt > 0) {
        addNBackslashes(state.arg, state.backslashCnt)
    }

    if (state.arg.isNotEmpty()) {
        state.args.add(state.arg.toString())
    }

    return state.args
}

private fun addNBackslashes(
    s: StringBuilder,
    n: Int,
) {
    repeat(n) {
        s.append('\\')
    }
}

private fun isWhitespace(c: Char): Boolean = c == ' ' || c == '\t' || c == '\r' || c == '\n'

private fun isWhitespaceOrNull(c: Char): Boolean = isWhitespace(c) || c == '\u0000'
