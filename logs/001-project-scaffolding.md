# Interaction 001 — Project scaffolding

- Date: 2026-08-26
- Objective: Create the smallest maintainable Java 25, JavaFX, Gradle, and JUnit 5 foundation for Constella.

## Original prompt (verbatim)

<!-- BEGIN ORIGINAL PROMPT -->

We are beginning an individual university software-engineering project
called Constella. Work directly inside the existing `MP1` folder.

Before making changes:
1. Inspect the current workspace and `MP1` folder.
2. Read `MP1-spec.md` completely.
3. Report what currently exists.
4. Identify assumptions, ambiguities, and risks.
5. Preserve all existing files.

## Product concept

Constella is a cross-platform personal memory journal built as a Java
desktop application.

Users will eventually be able to:
- record personal memories,
- visualize memories as stars,
- group related memories into constellations,
- connect related memories,
- search and filter their journal,
- and explore memories chronologically through a timeline.

This is not a task manager. Do not add tasks, deadlines, completion
checkboxes, recurring reminders, or other to-do-manager functionality.

## Technical constraints

- Use Java SE 25.
- Use JavaFX for the desktop interface.
- Use Gradle with the Gradle Wrapper.
- Use JUnit 5 for automated tests.
- The application must work on Windows, Linux, and macOS.
- Keep domain logic independent of JavaFX.
- Prefer a simple, maintainable architecture.
- Do not add unnecessary frameworks or dependencies.
- Do not use external APIs, cloud services, databases, authentication,
  analytics, or network access.
- User data will eventually be stored locally, but persistence must not
  be implemented in this increment.
- Do not initialize or connect to a remote GitHub repository.
- Do not create a release JAR yet unless it naturally results from the
  basic Gradle build.
- Do not implement the complete product in this increment.

## Submission requirements to preserve

The final repository must eventually contain:

- `src/`
- `release/`
- `docs/UserGuide.md`
- `docs/DeveloperGuide.md`
- `docs/Reflections.md`
- `logs/`

The final public GitHub repository will be named exactly
`CS3227-2610-MP1`, and its submission branch will be `master`.

Do not rename the current folder or publish anything during this
increment.

## First increment

For this increment, perform only the following work:

1. Create a maintainable Gradle project using Java 25, JavaFX, and
   JUnit 5.

2. Include the Gradle Wrapper so the project can be built using:
   - `./gradlew` on macOS and Linux
   - `gradlew.bat` on Windows

3. Create the smallest working JavaFX application that:
   - starts successfully,
   - opens one desktop window,
   - uses the title `Constella`,
   - displays a simple welcome message,
   - and contains no journal feature implementation yet.

4. Create an initial package structure that separates:
   - domain models,
   - application or service logic,
   - persistence,
   - and JavaFX UI code.

   Empty Java placeholder classes are not required. Create package
   directories or files only when they provide immediate value.

5. Add at least one meaningful automated test that does not require
   launching the JavaFX interface.

6. Create:
   - `README.md`
   - `.gitignore`
   - `docs/UserGuide.md`
   - `docs/DeveloperGuide.md`
   - `docs/Reflections.md`
   - `logs/README.md`
   - `AGENTS.md`

7. Keep the initial documentation honest and minimal. Clearly mark
   planned features as planned. Do not document them as already
   implemented.

## Required AGENTS.md contents

Create a repository-level `AGENTS.md` containing durable development
instructions for future AI-assisted work.

It must include the following rules:

### Scope and requirements

- Read `MP1-spec.md` before making requirement-sensitive decisions.
- Preserve the distinction between implemented and planned features.
- Do not turn Constella into a to-do manager.
- Maintain compatibility with Windows, Linux, and macOS.
- Keep domain and application logic independent of JavaFX.
- Avoid unnecessary dependencies and speculative features.
- Work in small, testable increments.
- Do not publish, deploy, push, or modify remote services unless the
  user explicitly requests it.

### Working process

For each development task:

1. Inspect the existing implementation before editing.
2. State important assumptions.
3. Propose the smallest appropriate change.
4. Implement only the requested increment.
5. Add or update relevant tests.
6. Run relevant tests and builds.
7. Update affected documentation.
8. Create or update the corresponding interaction log.
9. Report actual results and remaining limitations.

Do not claim that a command, build, test, or manual check succeeded
unless it was actually performed successfully.

### Code quality

- Use clear names and small, focused classes and methods.
- Validate inputs at appropriate boundaries.
- Avoid duplicated logic and dead code.
- Do not couple domain objects to JavaFX controls.
- Keep persistence behind an abstraction.
- Add comments only when they explain non-obvious reasoning.
- Prefer testable logic over logic embedded inside UI event handlers.
- Preserve existing user changes and avoid unrelated rewrites.

### Testing

- Use JUnit 5.
- Add tests for normal cases, boundary values, and invalid inputs.
- Run the complete relevant test suite after changes.
- Record actual test counts and failures accurately.
- Do not replace meaningful tests with tests that only increase coverage.
- UI behavior that cannot be automated yet must be documented as a
  manual verification step.

### Documentation

- Keep `README.md`, the User Guide, and Developer Guide consistent with
  the current implementation.
- Never describe planned functionality as available.
- Add acknowledgements for reused code, assets, ideas, and documentation.
- Treat documentation inaccuracies as defects.

### AI interaction logging

For every meaningful development task:

1. Create or update a Markdown file under `logs/`.
2. Use sequential filenames such as
   `001-project-scaffolding.md`.
3. Include the user's original prompt verbatim.
4. Include:
   - date,
   - objective,
   - response summary,
   - assumptions,
   - design decisions,
   - files changed,
   - commands actually executed,
   - actual build and test results,
   - problems or limitations,
   - changes made after verification,
   - outcome,
   - and suggested commit message.
5. Group closely related follow-up prompts in the same log.
6. Never invent prompts, commands, results, student opinions, or commit
   hashes.
7. Do not mark a log as reviewed by the student.

End every interaction log with:

## First interaction log

Create:

`logs/001-project-scaffolding.md`

It must contain this entire original prompt verbatim.

After completing the work, add an accurate summary of:

- what was created,
- assumptions made,
- design decisions,
- every materially changed file,
- commands actually executed,
- actual build and test results,
- encountered problems,
- unresolved risks,
- and the recommended next increment.

Do not mark the student-review checklist as completed.

If recording this exact prompt inside the log creates a recursion problem,
include the prompt once inside a clearly delimited section and do not
reproduce the generated log within itself.

## Verification requirements

After implementation:

1. Run the complete automated test suite.
2. Run a clean Gradle build.
3. If the environment supports graphical applications, launch Constella
   and confirm that the window opens with the correct title and welcome
   message.
4. If graphical launch verification is unavailable, state that clearly
   and provide precise manual verification steps.
5. Inspect the generated project structure for accidental build files,
   secrets, IDE metadata, or OS-specific files that should be ignored.

Do not conceal or work around failures. Diagnose them and report them
accurately.

## Final response

Conclude with:

- a concise description of the completed foundation,
- files created or changed,
- build and test commands executed,
- exact results,
- assumptions and limitations,
- anything requiring my manual verification,
- and one recommended next increment.

Do not implement the next increment yet.

<!-- END ORIGINAL PROMPT -->

## Response summary

Created a Java 25 Gradle application with the Gradle Wrapper, JavaFX 25.0.2, JUnit 5, a single welcome window, UI-independent welcome content, documented package boundaries, minimal project documentation, and three non-UI automated tests. No journal features, persistence, remote services, Git repository, or release artifact directory were added.

## Assumptions

- The first increment should create only a development build; the required final bundled release JAR is deferred.
- Package documentation files provide immediate architectural value without speculative placeholder classes.
- A Gradle-provisioned JDK 25 is acceptable when the host has a different JDK installed.
- Cross-platform compatibility is established structurally but still requires later Windows and Linux testing.

## Design decisions

- Pinned Gradle 9.7.1 because it supports Java 25 and runs on the host Java 21 installation.
- Used Gradle toolchains with the Foojay resolver so JDK 25 can be provisioned.
- Kept JavaFX exclusively in `constella.ui`; `WelcomeContent` remains testable without JavaFX.
- Used package documentation rather than empty model and persistence classes.
- Enabled native access for `javafx.graphics` to avoid the Java 25 restricted-native-access warning.

## Files changed

- Preserved: `MP1-spec.md`, `.DS_Store`.
- Build and wrapper: `settings.gradle`, `build.gradle`, `gradlew`, `gradlew.bat`, `gradle/wrapper/gradle-wrapper.jar`, `gradle/wrapper/gradle-wrapper.properties`.
- Source: `src/main/java/constella/application/WelcomeContent.java`, `src/main/java/constella/ui/ConstellaApplication.java`, `src/main/java/constella/model/package-info.java`, `src/main/java/constella/persistence/package-info.java`.
- Tests: `src/test/java/constella/application/WelcomeContentTest.java`.
- Documentation and guidance: `README.md`, `.gitignore`, `AGENTS.md`, `docs/UserGuide.md`, `docs/DeveloperGuide.md`, `docs/Reflections.md`, `logs/README.md`, `logs/001-project-scaffolding.md`.

## Commands actually executed

- Inspected the prompt, workspace, complete specification, file tree, and local Java/Gradle versions using `sed`, `wc`, `rg`, `find`, `java -version`, `javac -version`, and `gradle --version`.
- Downloaded Gradle 9.7.1 with `curl`, extracted it with `unzip`, generated the wrapper with `gradle wrapper --gradle-version 9.7.1 --distribution-type bin`, and made `gradlew` executable.
- Ran `./gradlew test --no-daemon` (initial failure, then successful reruns).
- Ran `./gradlew clean build --no-daemon` twice successfully after the fix.
- Ran `./gradlew run --no-daemon` twice and stopped each process with Ctrl-C after inspection.
- Attempted macOS window inspection with `osascript`; it failed because System Events lacked assistive-access permission.
- Captured the desktop with `screencapture` and visually inspected the image.
- Inspected the final non-build file structure with `find` and the JUnit XML report with `sed`.
- Compared the logged prompt with the attachment using `diff`, scanned for common secret/build-file patterns with `find`, and ran the test suite once more.

## Actual build and test results

- Initial test run: failed because Gradle 9 could not load JUnit Platform without an explicit launcher runtime dependency.
- After adding `testRuntimeOnly 'org.junit.platform:junit-platform-launcher'`: test suite passed.
- Final `./gradlew test --no-daemon`: `BUILD SUCCESSFUL`; 3 tests, 0 failures, 0 errors, 0 skipped.
- The post-log test rerun was also `BUILD SUCCESSFUL` with all test tasks up to date.
- Final `./gradlew clean build --no-daemon`: `BUILD SUCCESSFUL`; 8 actionable tasks executed.
- Graphical launch: visually confirmed on macOS that one window opened, its title was `Constella`, and it displayed `Welcome to Constella`.

## Problems or limitations

- The host default is Java 21, not Java 25; Gradle successfully provisioned and used a Java 25 toolchain.
- The first test run failed due to the missing JUnit Platform launcher and was corrected.
- Accessibility-based window inspection was denied by macOS, so a screenshot was used instead.
- The run process was intentionally interrupted after visual verification, so the Gradle `run` task ended with exit code 130 rather than a normal application close.
- Windows and Linux behavior has not been tested.
- The final bundled release JAR, journal features, and persistence remain unimplemented as required for this increment.
- The preserved `.DS_Store` is ignored by `.gitignore`; it should not be committed.
- Prompt comparison found identical textual content; `diff` returned 1 solely because the source attachment has no final newline while the delimited Markdown section necessarily does.

## Changes made after verification

- Added the explicit JUnit Platform launcher dependency after the failed test run.
- Added `--enable-native-access=javafx.graphics` after the first launch emitted a Java 25 native-access warning.

## Outcome

The requested first-increment foundation is complete and verified on macOS. The project builds through its wrapper and maintains the intended UI, application, model, and persistence boundaries without implementing journal functionality.

## Recommended next increment

Define the smallest UI-independent `Memory` domain model and its validation tests, without persistence or additional UI behavior.

## Suggested commit message

`chore: scaffold Constella JavaFX project`

## Student review

- [x] I confirmed that the original prompt is accurate.
- [x] I confirmed that the changed-file list is accurate.
- [x] I confirmed that recorded commands were actually executed.
- [x] I confirmed that build and test results are accurate.
- [x] I added any mistakes or disagreements omitted by the AI.

Reviewed by: A0273503L Lum Yi Ren Johannsen
Review date: 31 August 2026
