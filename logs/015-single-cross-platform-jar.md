# 015 — Single cross-platform release JAR

Date: 31 August 2026

## Objective

Replace the three platform-labelled submission JARs with one latest `release/Constella.jar` while bundling JavaFX native libraries for Windows x86-64, Linux x86-64, and Apple Silicon macOS.

## Original prompt (verbatim)

> do the change for me please

## Prompt context

The request followed a discussion of the specification's singular wording—“a jar file” and “the latest jar file”—together with its requirement that the application function on Windows, Linux, and macOS. The agreed change was to keep one latest submission JAR and retain three-platform CI verification.

## Response summary

Changed `releaseJar` to create one reproducible `Constella.jar`, added isolated JavaFX native dependency configurations for all three target operating systems, reduced `release/` to that JAR and its checksum, updated CI to upload the same filename from every runner, and corrected all current documentation.

## Assumptions

- “All three major operating systems” is satisfied for the architectures exercised by the project: Windows x86-64, Linux x86-64, and Apple Silicon macOS.
- Intel macOS and ARM Linux/Windows are outside the currently packaged architecture scope and must be stated rather than implied.
- CI compilation and automated tests do not substitute for manual JavaFX interaction on Windows and Linux.

## Design decisions

- Used separate Gradle configurations for Linux, Windows, and macOS JavaFX native artifacts because resolving several platform variants in one configuration causes a capability conflict.
- Disabled transitive resolution for those explicit classifier dependencies; ordinary runtime dependencies already provide the shared JavaFX classes.
- Enabled reproducible JAR ordering and normalized timestamps so matrix outputs can be compared reliably.
- Kept the filename unqualified as `Constella.jar` because the same packaged file contains all three targeted native sets.

## Files changed

- `build.gradle`
- `.github/workflows/gradle.yml`
- `README.md`
- `docs/UserGuide.md`
- `docs/DeveloperGuide.md`
- `docs/Reflections.md`
- `release/Constella.jar`
- `release/SHA256SUMS.txt`
- removed the three platform-labelled release JARs
- `logs/015-single-cross-platform-jar.md`

## Commands actually executed

- `./gradlew dependencies --configuration runtimeClasspath --no-daemon`
- Three attempts of `./gradlew clean check releaseJar --no-daemon` while correcting dependency resolution
- `unzip -t release/Constella.jar`
- `unzip -l release/Constella.jar` with native-library checks
- direct JDK 25 launch of `release/Constella.jar` using an isolated data file
- SHA-256, structure, Git diff, and test-result checks

## Actual build and test results

- The first universal configuration failed because mutually exclusive JavaFX platform capabilities were placed in one Gradle configuration.
- The first separated-configuration attempt failed because transitive dependencies lacked platform attributes.
- Disabling transitive resolution for the explicit native artifacts fixed the dependency model.
- Final `./gradlew clean check releaseJar --no-daemon` — BUILD SUCCESSFUL.
- JUnit result — 86 tests, 0 failures, 0 errors, 0 skipped.
- Checkstyle production and test checks passed.
- The JAR passed ZIP integrity validation and contains `.dll`, `.so`, and `.dylib` JavaFX graphics natives.
- Direct launch with the Gradle-provisioned JDK 25 succeeded on Apple Silicon macOS; it was stopped manually after startup confirmation.

## Problems or limitations

- The host default `java` is JDK 21 and correctly rejected Java 25 bytecode; launch verification therefore used the Gradle-provisioned JDK 25 as documented.
- Manual Windows and Linux GUI interaction is still outstanding.
- The packaged architecture set does not include Intel macOS or ARM Linux/Windows.
- `docs/Reflections.md` still requires the student's personal answers.

## Changes made after verification

The dependency configuration was redesigned twice in response to actual Gradle variant-resolution failures. Documentation was scoped to the exact operating-system and architecture coverage rather than calling the artifact universally portable without qualification.

## Outcome

`release/` contains one latest standalone JAR with the relevant JavaFX native libraries for the three targeted operating-system environments and one checksum file.

Suggested commit message: `release: build one cross-platform jar`

## Student review

- [ ] I confirmed that the original prompt is accurate.
- [ ] I confirmed that the changed-file list is accurate.
- [ ] I confirmed that recorded commands were actually executed.
- [ ] I confirmed that build and test results are accurate.
- [ ] I added any mistakes or disagreements omitted by the AI.

Reviewed by: Johannsen
Review date: 26 August 2026
