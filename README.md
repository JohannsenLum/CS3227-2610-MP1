# Constella

Constella is an offline, cross-platform JavaFX memory journal. Memories become stars in a dark interactive sky, can belong to multiple constellations, and remain searchable through shared filters and a chronological timeline.

> Screenshot placeholder: add a repository-safe screenshot after the student completes final visual review. Verification screenshots taken during development included the surrounding desktop and are intentionally not committed.

## Features

- Create, view, edit, and confirmation-delete memories
- Local human-readable JSON persistence with stable UUIDs
- Genuine JavaFX 3D **My Sky** startup view with mood-coloured memory spheres, constellation-coloured light trails, a deep twinkling starfield, settled galaxy-like clusters, strict relationship focus, motion controls, default auto-rotation, and bounded camera exploration
- Case-insensitive search plus mood, tag, constellation, and year filters
- Alternating, year-grouped newest-first timeline with a central chronological axis
- No network access, authentication, analytics, or cloud services
- A detailed 24-memory fictional four-year NUS demo journal on first launch, including clustered and disconnected memories, removable with **Clear Journal**

## Prerequisites

- JDK 25, or internet access on the first development build so Gradle can provision it

## Build, test, and run

macOS/Linux:

```sh
./gradlew clean build
./gradlew test
./gradlew run
```

Windows:

```bat
gradlew.bat clean build
gradlew.bat test
gradlew.bat run
```

## Release

Run `./gradlew releaseJar` (or `gradlew.bat releaseJar`) on each target platform. The task writes a platform-labelled JAR under `release/` with Gson, JavaFX classes, and that platform's JavaFX native libraries bundled.

The currently verified artifact is `release/Constella-macos-arm64.jar`, built and launched on macOS ARM64. It is not a universal Windows/Linux JAR. Run it with JDK 25:

```sh
java --enable-native-access=ALL-UNNAMED -jar release/Constella-macos-arm64.jar
```

Build and verify separate artifacts on Windows and Linux before submission.

See the [User Guide](docs/UserGuide.md) and [Developer Guide](docs/DeveloperGuide.md).
