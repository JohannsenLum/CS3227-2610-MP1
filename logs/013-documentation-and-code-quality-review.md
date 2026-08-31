# Interaction 013 — Documentation and code-quality review

## Date

31 August 2026

## Objective

Bring the README, User Guide, and Developer Guide into precise alignment with the final Constella implementation and MP1 submission requirements, then review the existing engineering controls against sensible Java software-engineering practices.

## Original prompt

> update my readme, user doc and develoepr doc, make sure it fits the requirement and also take a look into my coding requirements if we need to make it better and have the best practices

## Follow-up prompt

> push it please. Also, i will need a way to test or build the jar for linux, windows using github actions, can that be done?

## Second follow-up prompt

> also remove that limitation from the docs

## Response summary

The documentation was audited against `MP1-spec.md`, `AGENTS.md`, the current Java source, Gradle configuration, Checkstyle rules, CI workflow, tests, release process, and repository state. The three requested documents were updated without adding product functionality.

## Assumptions

- The final product scope is frozen; this task authorizes documentation corrections and only low-risk engineering corrections where evidence shows they are necessary.
- Windows and Linux remain unverified and must not be described as tested.
- A clean repository-safe product screenshot is not currently available, so documentation must not claim that one was added.
- Subjective reflections and Student Review approvals remain the student's responsibility.

## Design and documentation decisions

- README now acts as a concise repository entry point with truthful features, prerequisites, verification commands, release limitations, project structure, documentation links, platform status, and the live CI badge.
- User Guide now separates product usage from setup, states field normalization and validation rules, identifies exact default data locations, explains safe persistence behavior, and retains a release-oriented manual test checklist.
- Developer Guide now corrects the persistence-polymorphism description, accurately treats `StarPosition` as legacy compatibility data, documents the incremental SE process and CI gate, fixes stale renderer wording, and expands acknowledgements for Codex assistance and Obsidian graph-view inspiration.
- GitHub Actions now runs a Linux/Windows/macOS matrix, performs the complete clean quality gate, builds each platform-specific JavaFX fat JAR, and uploads the JAR for seven days as a workflow artifact.
- No production-code change was made because the existing boundaries, JUnit suite, Checkstyle rules, assertions, CI workflow, and platform-specific packaging already form a proportionate quality baseline for MP1.

## Files changed

- `README.md`
- `docs/UserGuide.md`
- `docs/DeveloperGuide.md`
- `.github/workflows/gradle.yml`
- `logs/013-documentation-and-code-quality-review.md`

## Commands executed

- Read `AGENTS.md`, `MP1-spec.md`, the three documentation files, `build.gradle`, `.github/workflows/gradle.yml`, Checkstyle configuration, source/test file lists, assertion locations, Git status, and recent GitHub Actions runs.
- Ran `git diff --check`, `git diff --stat`, and targeted `rg` searches for stale or contradictory documentation claims.
- Ran `./gradlew clean check releaseJar --no-daemon`.
- After adding the cross-platform matrix, ran `./gradlew check releaseJar --no-daemon` and the structure script again before committing.
- Ran `./check_mp1_structure.sh .`.
- Aggregated JUnit XML totals, inspected the release file, calculated SHA-256, and ran `unzip -t` against the bundled JAR.

## Actual verification results

- `git diff --check` reported no whitespace errors.
- GitHub Actions' existing `Gradle CI` run on `master` was `completed/success`; the documentation changes in this interaction still require a new push before CI can validate them remotely.
- `./gradlew clean check releaseJar --no-daemon` completed with `BUILD SUCCESSFUL`; Checkstyle passed for production and test sources.
- JUnit result: 86 tests, 0 failures, 0 errors, 0 skipped.
- The exact structure script found every required item. It emitted only the known local-folder warning because the local directory is `MP1`; the GitHub repository itself is correctly named `CS3227-2610-MP1`.
- `release/Constella-macos-arm64.jar` was rebuilt at 9,276,922 bytes. SHA-256: `333372225021e47188bdad2021ea9cefb8c913ed9c45e0ef36f5138caa95fcea`.
- `unzip -t` reported no compressed-data errors.
- The updated workflow was prepared for remote validation with Linux, Windows, and macOS jobs; final job results were intentionally not claimed before the push.
- The first matrix run completed successfully on Linux, Windows, and macOS and uploaded all three artifacts. GitHub reported Node.js/action deprecation annotations for the earlier v4 action majors, so the workflow was updated to the current checkout v7, Gradle Actions v6, setup-java v6, and upload-artifact v7 major releases for a follow-up run.
- After the current-action follow-up run also passed on all three operating systems, the blanket Windows/Linux limitation was removed from the README, User Guide, and Developer Guide. The documentation still distinguishes automated cross-platform verification from optional human GUI evidence.

## Problems and limitations

- JavaFX remains platform-native; only the macOS ARM64 bundled JAR has been run locally.
- Hosted GitHub runners can compile and test the JavaFX project but cannot replace manual pointer, keyboard, rendering, and persistence verification on a real desktop session.
- Full pointer, keyboard, resize, and cross-platform behavior still depends on the User Guide manual checklist.
- Documentation accuracy can regress if additional features are added after this review.
- The student must still review this interaction record and complete subjective reflections personally.

## Outcome

The requested documentation now matches the final release more closely and exposes setup, verification, architecture, SE process, platform limits, and acknowledgements at the appropriate level. The code-quality review found no proportionate production-code change necessary; the clean automated quality gate remains green. Student review, personal reflections, manual GUI testing, and Windows/Linux verification remain intentionally outstanding.

## Suggested commit message

`docs: align guides with final Constella release`

## Student review

- [x] I confirmed that the original prompt is accurate.
- [x] I confirmed that the changed-file list is accurate.
- [x] I confirmed that recorded commands were actually executed.
- [x] I confirmed that build and test results are accurate.
- [x] I added any mistakes or disagreements omitted by the AI.

Reviewed by: A0273503L Lum Yi Ren Johannsen
Review date: 31 August 2026
