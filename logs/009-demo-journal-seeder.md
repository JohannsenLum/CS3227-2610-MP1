# Interaction 009 — Demo journal seeder

- Date: 2026-08-26
- Objective: Add a removable fictional four-year NUS journal for first launch.

## Original prompt (verbatim)

<!-- BEGIN ORIGINAL PROMPT -->

Create a seeder for constella where you are a nus student year 4 -> went through 4 years of experience in NUS, taking different mods and also going out to different exchange, trips, holidays &#x20;

then seed it in as well at the start while being able to clear this data to key in my own data in the future!

<!-- END ORIGINAL PROMPT -->

## Response summary

Added a deterministic fictional demo snapshot with 16 memories from 2022–2026 and 5 overlapping constellations. It is saved only when no journal file exists. Added a confirmation-protected Clear Journal action that persists an empty journal and prevents reseeding.

## Assumptions and design decisions

- Seed content is fictional rather than presented as the student's real experience.
- “At the start” means first launch with a missing data file, not every launch or every empty journal.
- File existence is the seed marker: clearing creates an empty valid file, so the demo stays cleared without expanding the JSON schema.
- Seed UUIDs and resulting star positions are deterministic.
- Clearing removes both demo and user-created data, so the UI states this explicitly and requires confirmation.

## Files changed

- Added `application/DemoJournalSeeder.java` and `DemoJournalSeederTest.java`.
- Updated `JournalStorage`, `JsonJournalStorage`, `JournalService`, `JournalSession`, and `JournalSessionTest`.
- Updated `ConstellaApplication`, `ConstellaView`, and `constella.css`.
- Updated README, User Guide, and Developer Guide.
- Added this log.

## Commands executed and actual results

- Read AGENTS instructions and the relevant session, storage, application, and UI code using `sed`.
- Baseline `./gradlew test --no-daemon`: `BUILD SUCCESSFUL`; the established suite had 55 tests.
- Focused seeder/session tests: `BUILD SUCCESSFUL`.
- Launched `./gradlew run --no-daemon` with `CONSTELLA_DATA_FILE` in a fresh `mktemp` directory; startup reached the JavaFX run task and the new Clear Journal control was visible. The central seeded sky was obscured by another desktop window, and macOS denied `cliclick` accessibility control, so star visibility was not claimed as manually verified.
- Stopped the graphical run with Ctrl-C after inspection; the run task ended with exit code 130.
- Final `./gradlew test --no-daemon`: `BUILD SUCCESSFUL`.
- Final `./gradlew clean build --no-daemon`: `BUILD SUCCESSFUL`; 9 actionable tasks executed.
- Final `./gradlew releaseJar --no-daemon`: `BUILD SUCCESSFUL`; rebuilt the macOS ARM64 release.
- Exact results: 60 tests, 0 skipped, 0 failures, 0 errors.
- Updated release SHA-256: `4c0e9add10200302730d78cb56c6a011d9f9c75be7549b2bed2e70dd6a4ac11f`.

## Problems and limitations

- No focused test failure occurred.
- One combined documentation patch was rejected without changing files because it targeted the User Guide twice; it was consolidated and applied successfully.
- Clear Journal is intentionally destructive after confirmation and has no undo.
- The demo is not injected into an existing valid empty journal; this is necessary so an intentionally cleared journal remains empty.

## Changes made after verification

Documentation was consolidated after the patch-format failure. No production-code repair was required after automated verification.

## Outcome

The application now starts with a representative NUS demo only on a genuinely fresh data path and provides a safe, persistent route to an empty personal journal.

## Suggested commit message

`feat: add removable NUS demo journal`

## Student review

- [x] I confirmed that the original prompt is accurate.
- [x] I confirmed that the changed-file list is accurate.
- [x] I confirmed that recorded commands were actually executed.
- [x] I confirmed that build and test results are accurate.
- [x] I added any mistakes or disagreements omitted by the AI.

Reviewed by: A0273503L Lum Yi Ren Johannsen
Review date: 31 August 2026
