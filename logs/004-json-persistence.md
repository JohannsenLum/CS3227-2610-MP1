# Interaction 004 — JSON persistence

- Date: 2026-08-26
- Objective: Implement and verify safe local JSON persistence.

## Checkpoint instructions

> Implement safe local JSON persistence and its tests.
>
> Verify persistence round trips and malformed-data behavior before continuing.

The complete master prompt is recorded verbatim in `logs/003-journal-service.md`.

## Response summary

Added an aggregate storage interface, cross-platform per-user path resolution, and a Gson-backed UTF-8 JSON implementation with validated DTO mapping and temporary-file replacement.

## Assumptions and design decisions

- Gson 2.14.0 is the sole JSON dependency; saved-data records remain private to persistence.
- Existing malformed data blocks saving so it cannot be silently overwritten.
- Missing data means an empty journal.
- Stale memberships are removed on load; other invalid values cause a contextual load failure.
- Atomic move is attempted first with a same-directory temporary file, with replacement fallback where the filesystem does not support atomic moves.

## Files changed

- Updated `build.gradle`.
- Added `JournalStorage.java`, `JournalStorageException.java`, `ApplicationDataPath.java`, and `JsonJournalStorage.java`.
- Updated persistence package documentation.
- Added `JsonJournalStorageTest.java` and `ApplicationDataPathTest.java`.
- Updated `docs/DeveloperGuide.md`.
- Added this log.

## Commands executed and actual results

- Consulted the official Gson project documentation and release listing before selecting version 2.14.0.
- Focused `./gradlew test --tests 'constella.persistence.*' --no-daemon`: `BUILD SUCCESSFUL`.
- Complete `./gradlew test --no-daemon`: `BUILD SUCCESSFUL`; 50 tests, 0 skipped, 0 failures, 0 errors.
- Inspected temporary build paths for leftover `journal-*.tmp` files; none were found.

## Problems, limitations, and changes after verification

- No persistence test failure occurred.
- Atomic-move fallback is implemented, while direct proof of the operating system's atomicity guarantee is outside a unit test's control.
- The storage layer is not connected to the UI until Checkpoint 3.
- No changes were required after verification.

## Outcome

Checkpoint 2 is working: round trips preserve UUIDs, Unicode, memberships, and positions; missing files load empty; malformed files are reported and preserved.

## Suggested commit message

`feat: add safe local JSON persistence`

## Student review

- [ ] I confirmed that the original prompt is accurate.
- [ ] I confirmed that the changed-file list is accurate.
- [ ] I confirmed that recorded commands were actually executed.
- [ ] I confirmed that build and test results are accurate.
- [ ] I added any mistakes or disagreements omitted by the AI.

Reviewed by:
Review date:
