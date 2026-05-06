# winsplit-kotlin in Kotlin

[![GitHub link](https://img.shields.io/badge/GitHub-KotlinMania%2Fwinsplit--kotlin-blue.svg)](https://github.com/KotlinMania/winsplit-kotlin)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.kotlinmania/winsplit-kotlin)](https://central.sonatype.com/artifact/io.github.kotlinmania/winsplit-kotlin)
[![Build status](https://img.shields.io/github/actions/workflow/status/KotlinMania/winsplit-kotlin/ci.yml?branch=main)](https://github.com/KotlinMania/winsplit-kotlin/actions)

This is a Kotlin Multiplatform line-by-line transliteration port of [`chipsenkbeil/winsplit-rs`](https://github.com/chipsenkbeil/winsplit-rs).

**Original Project:** This port is based on [`chipsenkbeil/winsplit-rs`](https://github.com/chipsenkbeil/winsplit-rs). All design credit and project intent belong to the upstream authors; this repository is a faithful port to Kotlin Multiplatform with no behavioural changes intended.

### Porting status

This is an **in-progress port**. The goal is feature parity with the upstream Rust crate while providing a native Kotlin Multiplatform API. Every Kotlin file carries a `// port-lint: source <path>` header naming its upstream Rust counterpart so the AST-distance tool can track provenance.

---

## Upstream README — `chipsenkbeil/winsplit-rs`

> The text below is reproduced and lightly edited from [`https://github.com/chipsenkbeil/winsplit-rs`](https://github.com/chipsenkbeil/winsplit-rs). It is the upstream project's own description and remains under the upstream authors' authorship; links have been rewritten to absolute upstream URLs so they continue to resolve from this repository.

## winsplit

[![Crates.io][crates_img]][crates_lnk]
[![Docs][docs_img]][docs_lnk]
[![CI][ci_img]][ci_lnk]

[ci_img]: https://github.com/chipsenkbeil/winsplit-rs/actions/workflows/ci.yml/badge.svg
[ci_lnk]: https://github.com/chipsenkbeil/winsplit-rs/actions/workflows/ci.yml

[crates_img]: https://img.shields.io/crates/v/winsplit.svg
[crates_lnk]: https://crates.io/crates/winsplit

[docs_img]: https://docs.rs/winsplit/badge.svg
[docs_lnk]: https://docs.rs/winsplit

Like [shell-words](https://crates.io/crates/shell-words), but for Windows that
somewhat mirrors
[CommandLineToArgvW](https://docs.microsoft.com/en-us/windows/win32/api/shellapi/nf-shellapi-commandlinetoargvw),
following VC++ 2008 parsing rules.

Written purely in Rust, so runs on any operating system! Windows is _not_ a
requirement!

Minimum tested Rust version is `1.56.1`, but this may compile and work on
earlier versions!

## Installation

```toml
[Dependencies]
winsplit = "0.1"
```

If you want to use this without `std` library, this library can be compiled for
use with `alloc` by disabling the `std` feature:

```toml
[Dependencies]
winsplit = { version = "0.1", default-features = false }
```

## Usage

```rust
let args = winsplit::split(
    r#"C:\ProgramFiles\Example\example.exe --key "some value" arg1 arg2"#
);
assert_eq!(
    args, 
    &[
        r"C:\ProgramFiles\Example\example.exe",
        "--key",
        "some value",
        "arg1",
        "arg2"
    ]
);
```

## Parsing Rules

This library follows the 2008 parsing rules for VC++ 9.9 (msvcr90.dll) that was
released with Visual Studio 2008. See [C/C++ parameter parsing
rules](https://daviddeley.com/autohotkey/parameters/parameters.htm#WIN) for
more details.

You can also check out the mirror of the rules and examples at the [wiki
documentation
page](https://github.com/chipsenkbeil/winsplit-rs/wiki/Argument-Parsing-Process-w--Examples)
for this repository.

## Special Thanks

Goes to [David Deley](https://daviddeley.com/index.php) for documenting the
complexities of the Windows parameter parsing logic and providing numerous
examples found at
[https://daviddeley.com/autohotkey/parameters/parameters.htm](https://daviddeley.com/autohotkey/parameters/parameters.htm).

## License

This project is licensed under either of

Apache License, Version 2.0, (LICENSE-APACHE or
[apache-license][apache-license]) MIT license (LICENSE-MIT or
[mit-license][mit-license]) at your option.

[apache-license]: http://www.apache.org/licenses/LICENSE-2.0
[mit-license]: http://opensource.org/licenses/MIT

---

## About this Kotlin port

### Installation

```kotlin
dependencies {
    implementation("io.github.kotlinmania:winsplit-kotlin:0.1.0-SNAPSHOT")
}
```

### Building

```bash
./gradlew build
./gradlew test
```

### Targets

- macOS arm64
- Linux x64
- Windows mingw-x64
- iOS arm64 / simulator-arm64 (Swift export + XCFramework)
- JS (browser + Node.js)
- Wasm-JS (browser + Node.js)
- Android (API 24+)

### Porting guidelines

See [AGENTS.md](AGENTS.md) and [CLAUDE.md](CLAUDE.md) for translator discipline, port-lint header convention, and Rust → Kotlin idiom mapping.

### License

This Kotlin port is distributed under the same MIT license as the upstream [`chipsenkbeil/winsplit-rs`](https://github.com/chipsenkbeil/winsplit-rs). See [LICENSE](LICENSE) (and any sibling `LICENSE-*` / `NOTICE` files mirrored from upstream) for the full text.

Original work copyrighted by the winsplit-rs authors.  
Kotlin port: Copyright (c) 2026 Sydney Renee and The Solace Project.

### Acknowledgments

Thanks to the [`chipsenkbeil/winsplit-rs`](https://github.com/chipsenkbeil/winsplit-rs) maintainers and contributors for the original Rust implementation. This port reproduces their work in Kotlin Multiplatform; bug reports about upstream design or behavior should go to the upstream repository.
