# Interaction 012 — Submission hardening

- Date: 2026-08-31
- Objective: Perform the final MP1 engineering and submission audit without adding product features.

## Original prompt (verbatim)

```text
Perform a final MP1 engineering and submission audit of Constella.

Work inside the existing MP1 folder. Read and follow:

- MP1-spec.md
- AGENTS.md
- check_mp1_structure.sh
- current source code, tests, documentation, and logs

Do not add new product features or redesign the UI.

## Baseline

Before editing:

1. Inspect the complete project and Git status.
2. Run `./check_mp1_structure.sh .`.
3. Run `./gradlew clean test releaseJar --no-daemon`.
4. Record the exact baseline results.
5. Preserve all working functionality and user data.

The current expected baseline is 93 tests passing. Investigate any
difference before continuing.

## 1. Add Checkstyle

Add Gradle Checkstyle support for main and test source code.

Requirements:

- Apply the Gradle `checkstyle` plugin.
- Add an explicit maintained Checkstyle tool version compatible with
  Java 25 syntax.
- Create a repository-owned Checkstyle configuration under `config/`.
- Use meaningful rules covering indentation, imports, naming, braces,
  whitespace, line length, empty blocks, and common code-quality issues.
- Do not disable checks broadly merely to obtain a passing build.
- Fix violations without changing application behavior.
- Make `./gradlew check` run Checkstyle and all tests.
- Ensure both `checkstyleMain` and `checkstyleTest` pass.

Document the coding-standard approach in DeveloperGuide.md.

## 2. Add GitHub Actions CI

Create `.github/workflows/gradle.yml`.

It should:

- run on pushes and pull requests,
- check out the repository,
- set up Java 25,
- use the committed Gradle Wrapper,
- validate the wrapper if the official validation action supports the
  current wrapper,
- run `./gradlew clean check --no-daemon`,
- avoid publishing or deploying anything,
- and use minimal permissions.

Review the workflow syntax, but do not claim that GitHub CI passed until
it has actually run on GitHub.

## 3. Review production assertions

Inspect internal algorithms for meaningful invariant assertions.

Add only a small number of defensible Java assertions where they expose
programmer errors, such as:

- finite force-layout coordinates after calculation,
- valid internal graph endpoints after construction,
- or consistent retained rendering state.

Do not use assertions for user input, persistence errors, or recoverable
runtime conditions. Those must continue using validation and exceptions.

Add tests where useful, and document why assertions are limited to
internal invariants.

## 4. Do not force artificial inheritance

Audit the absence of a three-type domain inheritance hierarchy.

Do not redesign Memory or create fake subclasses merely to imitate the
CS2103 iP. Constella already demonstrates OOP through domain objects,
immutable value objects, interfaces, composition, polymorphic storage,
and separated application services.

Add a concise design explanation to DeveloperGuide.md describing why
composition is used instead of artificial memory subclasses.

If MP1-spec.md explicitly requires a three-type hierarchy, stop and
report the exact requirement instead of performing a risky redesign.

## 5. Remove dead and obsolete code

Identify code that is no longer reachable from the current application.

In particular, inspect the former `SkyView` implementation and its
supporting classes/tests.

Remove a file only if:

- it has no current production caller,
- it is not required for compatibility,
- and its removal does not reduce current functionality.

Remove or update obsolete tests and documentation together.

Do not remove reusable application logic used by the 3D graph.

Run the complete test suite after cleanup.

## 6. Correct documentation

Review README.md, UserGuide.md, and DeveloperGuide.md against the actual
application.

Fix at least these known issues:

- UserGuide currently says both that a missing journal creates the demo
  and that a missing file starts an empty journal. Explain accurately
  that the storage layer loads missing data as empty, while normal
  application startup seeds the demo once when no journal file exists.
- DeveloperGuide calls storage “forthcoming” even though it exists.
- Remove or correct claims about manually moved 2D star positions if the
  current My Sky does not support them.
- Ensure known limitations agree with the implemented collision and
  force-layout behavior.
- Remove documentation for deleted or inaccessible features.
- Ensure all current GUI controls are documented accurately.
- Keep Windows and Linux marked unverified.
- Keep the macOS release explicitly platform-specific.
- Preserve and verify the acknowledgements section.

Do not claim that manual testing, CI, Windows, or Linux verification has
been completed unless there is direct evidence.

Leave the README screenshot placeholder if no clean, repository-safe
screenshot is available. Do not capture the surrounding desktop.

## 7. Prepare reflections without fabricating them

Inspect docs/Reflections.md and logs/001 through the latest log.

Do not write subjective student opinions or pretend to be the student.

Improve the factual scaffolding where necessary and ensure it presents
at least three strong interaction candidates covering:

- Gradle/JUnit scaffolding failure,
- memory identity and normalization decisions,
- safe persistence or 3D graph correction.

For each candidate, retain explicit prompts for the student to answer:

- why the prompt was formulated that way,
- assumptions made by AI,
- what AI got wrong,
- how the result was verified,
- how prompting evolved,
- what engineering judgement remained,
- and what should change next time.

Clearly mark every unfinished personal section.

## 8. Prepare interaction logs for student review

Audit logs/001 through the latest log for structural consistency.

Do not check Student Review boxes or sign on the student’s behalf.

Report:

- logs missing an original prompt,
- logs with unverified claims,
- duplicate Student Review sections,
- stale file lists,
- inconsistent test counts,
- and missing limitations.

Correct factual or structural problems when evidence is available.
Leave subjective review and approval to the student.

Create:

`logs/012-submission-hardening.md`

Use the next sequential number if 012 already exists. Include this
prompt verbatim and follow AGENTS.md.

## 9. Initialize local Git safely

If the MP1 folder is not already a Git repository:

- initialize it with `master` as the initial branch,
- do not create a remote,
- do not create a GitHub repository,
- do not push,
- and do not commit until all generated files and exclusions have been
  inspected.

Verify `.gitignore` excludes:

- `.DS_Store`
- `.gradle/`
- `build/`
- IDE metadata
- personal journal data
- temporary files
- secrets and environment files

Show the proposed staged-file list before creating a commit.

If safe, create one truthful initial commit:

`Complete Constella MP1 implementation`

Do not fabricate historical checkpoint commits.

If any suspicious personal data or secret is present, stop before
staging or committing.

## 10. Final local verification

After all changes:

1. Run `./gradlew clean check releaseJar --no-daemon`.
2. Run `./check_mp1_structure.sh .`.
3. Confirm exact test totals.
4. Confirm Checkstyle passes for main and test.
5. Confirm the release JAR exists.
6. Run `unzip -t` on the release JAR.
7. Confirm Gson, JavaFX classes, and macOS native libraries are bundled.
8. Record JAR size and SHA-256.
9. Inspect the repository for secrets, personal data and build output.
10. Confirm the current branch is `master`.
11. Confirm the working tree is clean after the commit, if committed.

Do not claim:

- GitHub repository creation,
- GitHub Actions success,
- public visibility,
- remote push,
- Windows verification,
- Linux verification,
- student reflection completion,
- student log review,
- screenshot completion,
- or Canvas submission.

## Final response

Provide:

- baseline and final test results,
- Checkstyle status,
- CI workflow created,
- production assertions added,
- dead code removed,
- documentation corrections,
- reflection/log items still requiring student work,
- Git and branch status,
- release filename, size and checksum,
- grading-script result,
- remaining blockers,
- and exact manual steps the student must complete before submission.

Do not add new features, publish anything, or modify remote services.
```

## Baseline

- `git status` reported that this folder was not a Git repository.
- `./check_mp1_structure.sh .` passed all required contents, with the expected warning that the local folder is named `MP1` rather than `CS3227-2610-MP1`.
- `./gradlew clean test releaseJar --no-daemon` succeeded with 95 tests, 0 failures, 0 errors, and 0 skipped. The expected 93 was stale: two `ConstellationSearch` tests had been added in the preceding editor-search work.

## Assumptions and design decisions

- Checkstyle 14.0.0 was selected because it supports Java 25; the configuration is a small repository-owned baseline, not a broad suppression.
- `SkyView` and its exclusive 2D graph helpers had no production callers after My Sky moved to `Space3DView`; they were removed. Persisted `StarPosition` data remains because it is part of compatibility storage.
- Assertions are limited to graph endpoint and settled-coordinate internal invariants. Runtime and input errors continue to use normal validation.
- Composition remains appropriate for `Memory`; the specification contains no requirement for artificial memory subclasses.

## Changes

- Added Gradle Checkstyle configuration and `.github/workflows/gradle.yml`.
- Fixed eight Checkstyle violations without altering behavior.
- Added two internal invariant assertions.
- Removed the unreachable 2D Sky renderer, its obsolete helpers, and their tests.
- Corrected user/developer documentation and expanded factual reflection prompts.
- Normalized the duplicated/nonstandard historical Student review sections in logs 001 and 010.

## Commands and actual results

- `./gradlew checkstyleMain checkstyleTest --no-daemon` — BUILD SUCCESSFUL.
- `./gradlew test --no-daemon` — BUILD SUCCESSFUL.
- `./gradlew clean check releaseJar --no-daemon` — BUILD SUCCESSFUL; 86 tests, 0 failures, 0 errors, 0 skipped. The count is nine lower than the baseline because the three removed 2D-only test classes contained nine obsolete tests.
- `./check_mp1_structure.sh .` — all required contents present; local folder-name warning remains.
- `unzip -t release/Constella-macos-arm64.jar` — no errors detected.

## Problems, limitations, and remaining student work

- GitHub Actions has not run because no remote repository was created or pushed.
- Windows and Linux remain unverified; the release artifact is macOS ARM64 only.
- The screenshot placeholder remains intentionally unfilled.
- `docs/Reflections.md` is factual scaffolding only and needs the student's personal answers.
- Logs 004–008 retain checkpoint excerpts and reference the full master prompt in log 003 rather than reproducing it in each file. This is a historical structural limitation for student review.
- Historical test totals vary because features and tests changed over time; the current total is recorded above.

## Outcome

The audit hardens the existing application without adding product functionality. The direct macOS ARM64 release launch started successfully with the Gradle-provisioned Temurin JDK 25.0.4.1 and was stopped after eight seconds. The final release archive passed `unzip -t`, contains Gson, JavaFX `SubScene`, `libglass.dylib`, and `libprism_es2.dylib`, is 9,276,922 bytes, and has SHA-256 `333372225021e47188bdad2021ea9cefb8c913ed9c45e0ef36f5138caa95fcea`. The source scan found no unignored journal, environment, certificate, or key file and no likely credential assignment outside Git's generated hook samples.

## Git result

- `git init -b master` initialized the requested local-only repository; no remote was configured or contacted.
- `git add -n .` listed only source, tests, documentation, logs, Gradle files, the CI workflow, and `release/Constella-macos-arm64.jar`; this proposed list was inspected before staging.
- `git add . && git commit -m 'Complete Constella MP1 implementation'` created the single truthful root commit on `master`.
- The final commit is amended after this log update so this log is included in the committed audit record.

## Suggested commit message

`Complete Constella MP1 implementation`

## Student review

- [x] I confirmed that the original prompt is accurate.
- [x] I confirmed that the changed-file list is accurate.
- [x] I confirmed that recorded commands were actually executed.
- [x] I confirmed that build and test results are accurate.
- [x] I added any mistakes or disagreements omitted by the AI.

Reviewed by: A0273503L Lum Yi Ren Johannsen
Review date: 31 August 2026
