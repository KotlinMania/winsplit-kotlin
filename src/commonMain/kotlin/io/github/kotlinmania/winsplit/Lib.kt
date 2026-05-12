// port-lint: source src/lib.rs
package io.github.kotlinmania.winsplit

/** Splits the given string into arguments following VC++ 2008 rules. */
fun split(s: String): List<String> = parse(s)
