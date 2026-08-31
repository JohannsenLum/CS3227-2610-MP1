# Constella

[![Gradle CI](https://github.com/JohannsenLum/CS3227-2610-MP1/actions/workflows/gradle.yml/badge.svg?branch=master)](https://github.com/JohannsenLum/CS3227-2610-MP1/actions/workflows/gradle.yml)

Constella is an offline JavaFX memory journal that turns personal memories into an explorable 3D constellation graph. Memories can belong to multiple constellations, remain searchable through shared filters, and are also available through a chronological timeline.

Constella is an individual CS3227 MP1 project. It is a personal journal rather than a to-do manager and does not require an account, network connection, or external API.

## Highlights

- Create, inspect, edit, and confirmation-delete memories.
- Record a typed date, mood, importance, description, tags, people, location, and constellation memberships.
- Explore one genuine 3D node per visible memory in **My Sky**.
- View only real, sparse memory-to-memory connections contributed by constellations.
- Rotate, pan, zoom, focus, filter, and pause the 3D presentation.
- Search case-insensitively across title, description, tags, people, and location.
- Combine mood, tag, constellation, and year filters using AND semantics.
- Browse a newest-first chronological timeline.
- Create, rename, delete, and manage overlapping constellations.
- Save every mutation to local, human-readable UTF-8 JSON.
- Recover safely from missing, malformed, or unsupported data without silently overwriting it.
- Start with a removable, fictional 24-memory NUS demo journal.

## Requirements

- JDK 25
- A graphical desktop environment
- macOS, Windows, or Linux

The Gradle build can provision a Java 25 toolchain on the first connected development build. Running the packaged JAR directly still requires JDK 25 on the target computer.

## Build, check, and run

Use the committed Gradle Wrapper from the repository root.

macOS and Linux:

```sh
./gradlew clean check
./gradlew run
```

Windows:

```bat
gradlew.bat clean check
gradlew.bat run
```

`check` runs all JUnit 5 tests and Checkstyle against production and test sources. GitHub Actions runs a clean check and platform release build on Linux, Windows, and macOS for pushes, pull requests, and manual workflow runs.

## Release artifact

Build a platform-specific fat JAR on the target operating system:

```sh
./gradlew releaseJar
```

The task writes `Constella-<platform>-<architecture>.jar` under `release/` with Constella, Gson, JavaFX classes, and the current platform's JavaFX native libraries bundled.

The repository contains platform-labelled artifacts for the three supported desktop platforms:

```text
release/Constella-macos-arm64.jar
release/Constella-linux-x86_64.jar
release/Constella-windows-x86_64.jar
```

Run the artifact matching the operating system:

```sh
# macOS Apple Silicon
java --enable-native-access=ALL-UNNAMED -jar release/Constella-macos-arm64.jar

# Linux x86-64
java --enable-native-access=ALL-UNNAMED -jar release/Constella-linux-x86_64.jar
```

```bat
java --enable-native-access=ALL-UNNAMED -jar release\Constella-windows-x86_64.jar
```

Each JAR is platform-specific because it bundles JavaFX native libraries. The macOS artifact was built and launched locally; the Windows and Linux artifacts were built by the successful GitHub Actions matrix and archive-verified after download. Interactive GUI testing is still required on each target desktop.

GitHub Actions also uploads separately built `Constella-Linux`, `Constella-Windows`, and `Constella-macOS` workflow artifacts. Open a successful **Gradle CI** run on GitHub and download the artifact for the target operating system. These builds verify compilation, Checkstyle, and automated tests on each hosted runner; they do not replace manual GUI testing on a real desktop.

## Project structure

```text
src/main/java/constella/
├── application/   UI-independent use cases, graph logic, filters, and session coordination
├── model/         Immutable journal domain objects
├── persistence/   Storage abstraction and validated JSON implementation
└── ui/            JavaFX application, dialogs, timeline, and 3D renderer

src/test/java/     JUnit 5 tests
docs/              User Guide, Developer Guide, and AI-assisted SE reflections
logs/              Verified summaries of AI-assisted development interactions
release/           Latest platform-specific bundled JARs and checksums
```

## Documentation

- [User Guide](docs/UserGuide.md) — setup, complete feature instructions, data handling, limitations, and manual tests
- [Developer Guide](docs/DeveloperGuide.md) — architecture, design decisions, SE process, quality controls, testing, and packaging
- [Reflections](docs/Reflections.md) — reflections on AI-assisted software engineering

## Platform status

- macOS ARM64: local build, tests, Checkstyle, and direct JAR launch verified
- Windows: GitHub Actions build, Checkstyle, tests, and platform JAR verified
- Linux: GitHub Actions build, Checkstyle, tests, and platform JAR verified

The product contains only fictional demo content. No personal journal data, credentials, analytics, or network services are included in the repository.
