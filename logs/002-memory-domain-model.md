# Interaction 002 — Memory domain model

- Date: 2026-08-26
- Objective: Add a small, immutable, UI-independent domain model for one journal memory.

## Original prompt (verbatim)

<!-- BEGIN ORIGINAL PROMPT -->

Continue developing Constella inside the existing MP1 project.

Read and follow:

- MP1-spec.md
- AGENTS.md
- the existing implementation and documentation

This is the second development increment.

## Objective

Implement a small, UI-independent domain model representing one
journal memory. Do not implement persistence, JSON serialization,
memory-editing screens, the interactive sky, or other UI features yet.

Before editing:

1. Inspect the current project structure and existing tests.
2. Confirm that the existing build and tests pass.
3. Summarize the proposed model and its invariants.
4. State important assumptions and trade-offs.
5. Identify any requirement that remains ambiguous.

## Memory requirements

Create a `Memory` domain model supporting:

- a stable UUID,
- a non-blank title,
- the date on which the memory occurred,
- an optional description,
- a mood,
- an importance value from 1 to 5,
- zero or more tags,
- zero or more people,
- and an optional location.

Create a suitable `Mood` representation.

Use an intentionally small initial mood set, such as:

- JOYFUL
- PEACEFUL
- EXCITED
- NOSTALGIC
- SAD
- ANXIOUS
- NEUTRAL

If you believe a different representation is materially better,
explain it before implementing it.

## Design requirements

- Keep all domain code independent of JavaFX.
- Keep all domain code independent of JSON and persistence libraries.
- Clearly define whether `Memory` is immutable or mutable and justify
  the choice.
- Clearly define equality and identity semantics.
- Prevent callers from mutating internal collections.
- Reject null or invalid mandatory values.
- Normalize titles, tags, people, and location consistently where
  appropriate.
- Reject blank tags and blank people names.
- Prevent duplicate tags after normalization.
- Prevent duplicate people after normalization.
- Use meaningful exception messages.
- Do not add visual star coordinates directly to `Memory`.
- Do not add constellation membership yet.
- Do not introduce builders, dependency-injection frameworks, database
  abstractions, or speculative design patterns unless immediately
  justified by the requirements.
- Do not add getters or methods that have no current purpose.

## Testing requirements

Add focused JUnit 5 tests covering at least:

- creation with valid complete data,
- creation with valid minimal data,
- generated UUID behavior if UUID generation belongs to the model,
- reconstructing a memory using an existing UUID,
- null and blank title rejection,
- null date rejection,
- null mood rejection,
- importance values 1 and 5,
- rejection of importance values 0 and 6,
- description behavior,
- optional-location behavior,
- tag normalization,
- duplicate-tag handling,
- blank-tag rejection,
- people-name normalization,
- duplicate-person handling,
- blank-person rejection,
- collection immutability,
- equality and hash-code behavior.

Test behavior rather than implementation details.

## Documentation

Update documentation only where necessary.

The documentation must distinguish between:

- functionality supported by the domain model, and
- functionality currently available through the JavaFX interface.

Do not claim that users can create or view memories through the
application yet.

## Interaction log

Follow the logging rules in AGENTS.md.

Create:

`logs/002-memory-domain-model.md`

Include this original prompt verbatim and accurately record:

- assumptions,
- design decisions,
- changed files,
- commands executed,
- actual test results,
- problems encountered,
- limitations,
- outcome,
- and suggested commit message.

Do not mark the student-review checklist as completed.

## Verification

After implementation:

1. Run the complete test suite.
2. Run a clean Gradle build.
3. Report exact test results.
4. Inspect the change for accidental JavaFX or persistence coupling.
5. Confirm that the existing welcome application still builds.
6. Report any unresolved design question instead of silently deciding
   it if the decision would significantly affect later features.

## Final response

Conclude with:

- the model that was implemented,
- important invariants,
- files changed,
- commands and actual results,
- assumptions and trade-offs,
- limitations,
- anything requiring my review,
- and one recommended next increment.

<!-- END ORIGINAL PROMPT -->

## Response summary

Implemented an immutable `Memory` domain class and a seven-value `Mood` enum without changing the JavaFX interface. Added focused tests and updated developer/user documentation to distinguish internal domain support from user-visible functionality.

## Assumptions

- Titles, descriptions, locations, tags, and people names are trimmed and internal whitespace is collapsed.
- Tags are normalized to lowercase.
- People names preserve the first normalized spelling and deduplicate case-insensitively.
- Duplicate tags and people are coalesced rather than rejected.
- Blank descriptions and locations represent absence.
- Dates are not restricted to the past because no such rule was specified.
- Tag and people iteration order preserves first occurrence for predictable future display.

## Design decisions

- `Memory` is immutable so validated state cannot change unexpectedly and collections can be safely shared.
- `Memory.create(...)` generates a UUID; the public constructor accepts an existing UUID for later reconstruction.
- UUID alone defines identity, equality, and hash code. Other fields describe the current snapshot of that identity.
- Optional description and location accessors return `Optional<String>`.
- Tags and people are exposed as unmodifiable insertion-ordered sets built from defensive copies.
- The requested small mood set is represented by an enum: `JOYFUL`, `PEACEFUL`, `EXCITED`, `NOSTALGIC`, `SAD`, `ANXIOUS`, and `NEUTRAL`.
- No builder, persistence interface, serialization annotation, JavaFX type, coordinate, or constellation field was added.

## Files changed

- Added `src/main/java/constella/model/Memory.java`.
- Added `src/main/java/constella/model/Mood.java`.
- Updated `src/main/java/constella/model/package-info.java`.
- Added `src/test/java/constella/model/MemoryTest.java`.
- Updated `docs/DeveloperGuide.md`.
- Updated `docs/UserGuide.md`.
- Added `logs/002-memory-domain-model.md`.

## Commands actually executed

- Used `sed` and `find` to read `MP1-spec.md`, `AGENTS.md`, the build configuration, current source/tests, documentation, and project structure.
- Ran baseline `./gradlew test --no-daemon` before editing.
- Ran `./gradlew test --no-daemon` after implementation.
- Ran `./gradlew clean build --no-daemon` after implementation.
- Used `sed` to inspect both JUnit XML result files and obtain exact test counts.
- Used `rg` to scan the model source/tests for JavaFX, JSON, and persistence coupling.
- Used `find` to inspect the final model source/test files.

## Actual build and test results

- Baseline suite: `BUILD SUCCESSFUL`; 3 existing tests passed with no failures.
- Post-implementation suite: `BUILD SUCCESSFUL`.
- Clean build: `BUILD SUCCESSFUL` in 6 seconds; 8 actionable tasks executed.
- Exact clean-build results: 24 tests total, 0 failures, 0 errors, 0 skipped (21 `MemoryTest` tests and 3 existing `WelcomeContentTest` tests).
- The clean build compiled the existing JavaFX welcome application successfully.
- Coupling scan returned no JavaFX, JSON, persistence, Gson, or Jackson references in the model source or tests.

## Problems encountered

No build, compilation, or test failures occurred during this increment.

## Limitations and unresolved questions

- The `Memory` model is not connected to the JavaFX interface; users cannot create or view memories.
- Persistence and JSON serialization remain unimplemented.
- Duplicate handling was ambiguous: this increment coalesces duplicates after normalization. The student should review whether later UX should instead report duplicates as input errors.
- No date constraint was specified. Future dates are currently accepted and this should be revisited only if product requirements demand otherwise.
- The constructor has several parameters, but a builder was deliberately avoided because the prompt prohibits speculative patterns and the model currently has only one construction use case.

## Changes made after verification

No code changes were required after verification.

## Outcome

The second increment is complete: Constella now has a validated, immutable, UI-independent representation of one journal memory while the visible application remains the original welcome screen.

## Suggested commit message

`feat: add immutable memory domain model`

## Student review

- [ ] I confirmed that the original prompt is accurate.
- [ ] I confirmed that the changed-file list is accurate.
- [ ] I confirmed that recorded commands were actually executed.
- [ ] I confirmed that build and test results are accurate.
- [ ] I added any mistakes or disagreements omitted by the AI.

Reviewed by:
Review date:
