# Interaction 003 — Journal service

- Date: 2026-08-26
- Objective: Implement and verify the in-memory journal application service.

## Original master prompt (verbatim)

<!-- BEGIN ORIGINAL PROMPT -->
Continue developing Constella inside the existing MP1 project.

Read completely and follow:

- MP1-spec.md
- AGENTS.md
- README.md
- docs/DeveloperGuide.md
- docs/UserGuide.md
- the existing source code and tests
- the existing interaction logs

## Objective

Complete a polished, functional MVP of Constella.

Constella is a cross-platform JavaFX desktop journal in which users
record memories as stars, organize related memories into constellations,
search and filter them, and explore them chronologically.

This request covers the complete MVP. However, work through the
specified checkpoints sequentially. Verify each checkpoint before
continuing, and create a separate interaction log for each checkpoint.

Do not turn the project into a to-do manager. Do not add tasks,
deadlines, completion checkboxes, recurring reminders, or scheduling
functionality.

## Existing foundation

The project already contains:

- Java 25
- Gradle Wrapper
- JavaFX 25
- JUnit 5
- a working JavaFX welcome window
- an immutable `Memory` model
- a `Mood` enum
- domain-model tests
- documentation and logging structure

Preserve validated behavior unless a change is necessary. If an existing
design must change, explain why, update its tests, and record the decision
in the appropriate log.

## Required MVP functionality

The finished MVP must support:

1. Creating memories
2. Viewing memory details
3. Editing memories
4. Deleting memories with confirmation
5. Persisting memories between application launches
6. Displaying memories as stars in an interactive sky
7. Encoding importance through star size
8. Encoding mood through star colour
9. Selecting a star to open its details
10. Creating and renaming constellations
11. Assigning memories to constellations
12. Removing memories from constellations
13. Displaying visual connections between memories in a constellation
14. Searching memories
15. Filtering by mood, tag, constellation, and year
16. Viewing memories chronologically in a timeline
17. Handling empty states and invalid inputs clearly
18. Recovering gracefully from missing or malformed data
19. Preserving user data safely
20. Running without network access

Do not add AI APIs, authentication, cloud storage, databases, image
attachments, reminders, analytics, or speculative features.

## UX requirements

Use a dark night-sky visual theme suitable for Constella.

The main application should contain:

- A sidebar or equivalent navigation for:
  - My Sky
  - Timeline
  - Constellations
- A clearly visible `New Memory` action
- Search and filtering controls
- A main interactive content area
- A memory detail panel or dialog
- Clear empty states
- Clear error and confirmation dialogs

### Sky view

The sky should:

- Display every currently visible memory as a star
- Use memory importance to determine star size
- Use memory mood to determine star colour
- Display the memory title through a tooltip or nearby label
- Open memory details when a star is selected
- Display lines between memories assigned to the selected constellation
- Remain usable when there are zero memories
- Remain usable when there are many memories
- Use deterministic positions so stars do not randomly move every launch

Do not store JavaFX objects in the domain model.

If star positions are persisted, represent them using UI-independent
domain data such as a dedicated `StarPosition` value object.

### Memory editor

The create/edit interface must support:

- title,
- date,
- description,
- mood,
- importance from 1 to 5,
- tags,
- people,
- optional location,
- and optional constellation assignment.

Validate user input before saving. Display understandable validation
messages rather than raw stack traces.

### Timeline

The timeline should:

- order memories by date,
- display useful summaries,
- allow selection of a memory,
- respect active search and filters where practical,
- and handle an empty journal.

### Constellations

A constellation should have:

- stable UUID,
- non-blank name,
- optional description,
- and membership relationships to memories.

Clearly define whether a memory can belong to zero, one, or multiple
constellations. For this MVP, prefer multiple constellation membership
unless the existing architecture gives a strong reason not to.

Prevent or handle dangling references when a memory or constellation is
deleted.

## Architecture requirements

Maintain clear separation between:

- `model` — domain entities and value objects
- `service` or `application` — use cases and business rules
- `persistence` — loading and saving
- `ui` — JavaFX views and controllers

Required abstractions should include the equivalent of:

- a memory repository,
- a constellation repository or aggregate persistence mechanism,
- an application-level journal service,
- and a storage implementation.

Do not introduce unnecessary interfaces. Introduce an interface where it
creates a meaningful test seam or keeps application logic independent of
file storage.

Keep business logic out of JavaFX event handlers.

## Persistence requirements

Use a local, human-readable format such as JSON.

Requirements:

- Store data in an appropriate per-user application-data directory
  rather than inside the installed application directory.
- Keep persistence paths cross-platform.
- Handle a missing data file as an empty journal.
- Validate loaded data.
- Do not silently overwrite malformed data.
- Show a useful user-facing error when loading fails.
- Save atomically using a temporary file followed by replacement where
  supported.
- Use UTF-8.
- Keep saved-data classes or serialization concerns separate from the
  domain model where practical.
- Preserve stable UUIDs across save/load cycles.
- Preserve constellation memberships and star positions.
- Add round-trip tests.
- Test missing files and malformed files.
- Do not commit personal journal data to Git.

If a small JSON dependency is needed, choose one maintained dependency
and document why it was added.

## Search and filtering

Search should be case-insensitive and cover at least:

- title,
- description,
- tags,
- people,
- and location.

Filters should support:

- mood,
- tag,
- constellation,
- and year.

Multiple active filters should combine predictably. Document the chosen
semantics and test them independently of JavaFX.

Provide a clear way to reset filters.

## Error handling

Handle at least:

- invalid editor input,
- duplicate constellation names,
- missing data file,
- malformed data file,
- failed save,
- deleting a memory,
- deleting a constellation,
- and stale constellation references.

Do not catch broad exceptions without either recovering appropriately or
reporting useful context.

Do not display stack traces directly to users.

## Testing requirements

Add meaningful automated tests for:

- journal-service operations,
- adding and retrieving memories,
- editing and deleting memories,
- UUID identity behavior,
- constellation creation and validation,
- constellation membership,
- duplicate constellation handling,
- deletion and dangling-reference behavior,
- search behavior,
- combined filters,
- deterministic star positioning,
- persistence round trips,
- missing files,
- malformed files,
- UTF-8 content,
- atomic-save behavior where testable,
- and regression behavior in the existing `Memory` model.

Tests must use temporary directories for persistence and must not alter
real user data.

Do not add superficial tests merely to inflate the test count.

For important UI behavior that cannot reasonably be automated, create a
manual test checklist in the User Guide.

## Documentation requirements

Update all relevant documentation to match the implemented product
exactly.

### README.md

Include:

- product summary,
- screenshot placeholder if no verified screenshot exists,
- major features,
- prerequisites,
- build instructions,
- test instructions,
- run instructions,
- release information,
- and links to the guides.

### docs/UserGuide.md

Include:

- installation and launch instructions,
- complete feature instructions,
- field-validation rules,
- search and filter behavior,
- data-storage behavior,
- error recovery,
- manual test instructions,
- and known limitations.

Do not document unimplemented functionality.

### docs/DeveloperGuide.md

Include:

- architecture and package responsibilities,
- important domain decisions,
- data flow,
- persistence design,
- star-positioning approach,
- search and filtering design,
- error handling,
- testing strategy,
- build and packaging approach,
- known limitations,
- and an acknowledgements section.

Use Mermaid diagrams where a diagram materially improves the
explanation.

### docs/Reflections.md

Do not fabricate personal reflections.

Create a useful structure and extract factual candidates from the
interaction logs, but leave subjective student conclusions clearly
marked for student completion.

Include at least three candidate interactions:

1. Project scaffolding and JUnit/Gradle issue
2. Memory-model design and normalization decisions
3. A meaningful implementation or debugging issue from this full-MVP
   increment

For each, provide factual evidence from logs and leave explicit prompts
for my personal reflection.

## Required checkpoints

Work through the following checkpoints in order.

### Checkpoint 1 — Application service

Implement an in-memory journal service covering memory operations,
constellations, membership, search, filtering, and deletion semantics.

Verify it before continuing.

Create:

`logs/003-journal-service.md`

### Checkpoint 2 — Persistence

Implement safe local JSON persistence and its tests.

Verify persistence round trips and malformed-data behavior before
continuing.

Create:

`logs/004-json-persistence.md`

### Checkpoint 3 — Memory editor and application shell

Build the main JavaFX navigation, memory editor, validation feedback,
details view, and create/edit/delete flow.

Verify the application manually if graphical execution is available.

Create:

`logs/005-memory-interface.md`

### Checkpoint 4 — Interactive sky and constellations

Implement star rendering, deterministic positions, selection,
constellation management, and connection lines.

Verify empty, single-memory, and multiple-memory states.

Create:

`logs/006-interactive-sky.md`

### Checkpoint 5 — Timeline, search, and filters

Complete the timeline interface and connect search/filter controls to
the application service.

Verify combinations and reset behavior.

Create:

`logs/007-search-timeline.md`

### Checkpoint 6 — Documentation and release preparation

Complete documentation, improve accessibility and keyboard behavior,
perform final regression testing, and prepare the release build.

Create:

`logs/008-release-preparation.md`

For every checkpoint:

1. Inspect the current state.
2. State assumptions and planned files.
3. Implement the smallest coherent checkpoint.
4. Add or update tests.
5. Run relevant tests.
6. Run the complete test suite.
7. Update relevant documentation.
8. Create the required interaction log.
9. Continue only if the checkpoint is in a working state.

If a checkpoint fails, diagnose and repair it before proceeding. Record
the failure and resolution honestly.

Include this complete master prompt verbatim in
`logs/003-journal-service.md`. Later checkpoint logs may reference it
and reproduce only the checkpoint-specific instructions and any
follow-up prompts; do not duplicate this entire prompt into every log.

Do not mark any student-review checklist as completed.

## Visual quality

Aim for a coherent, polished interface, but prioritize:

1. correctness,
2. data safety,
3. usability,
4. testability,
5. documentation accuracy,
6. visual polish.

Use JavaFX CSS rather than embedding all styles in Java code.

Avoid relying on external fonts, remote images, or network resources.

Use subtle animations only if they do not threaten stability or
accessibility.

## Accessibility

Where practical:

- use readable contrast,
- provide text labels or tooltips for non-text controls,
- support logical keyboard traversal,
- provide accessible text for stars,
- avoid communicating meaning through colour alone,
- and keep controls usable when the window is resized.

## Release preparation

The specification requires a release artifact with required libraries
bundled.

Investigate the existing Gradle and JavaFX setup before selecting a
packaging approach.

Requirements:

- Produce the best valid release artifact supported by the current
  environment.
- Do not claim one JAR is universally compatible if it contains
  platform-specific JavaFX native libraries.
- Document exactly what was built and on which platform.
- Document how Windows and Linux artifacts must be built or verified.
- Prefer a reproducible Gradle release task.
- Place the verified release artifact under `release/`.
- Do not commit temporary build output.
- Do not publish or upload anything.
- Do not create GitHub releases or modify remote repositories.

Test the release artifact separately from `./gradlew run` if possible.

If a truly portable bundled JAR is not technically honest with the
chosen JavaFX packaging, document that limitation precisely and provide
reproducible platform-specific build instructions.

## Final verification

At the end:

1. Run the complete automated test suite.
2. Run a clean build.
3. Launch the application.
4. Manually verify the primary flow:
   - start with an empty journal,
   - create multiple memories,
   - create a constellation,
   - assign memories,
   - view connected stars,
   - search and filter,
   - use the timeline,
   - edit a memory,
   - close and reopen the application,
   - confirm persistence,
   - delete a memory,
   - and confirm references remain valid.
5. Test malformed-data handling using a safe temporary test location,
   not real user data.
6. Inspect documentation against the actual interface.
7. Inspect the repository for secrets, personal journal data,
   accidental build outputs, and platform-specific junk.
8. Report Windows and Linux verification as outstanding unless they were
   actually performed on those systems.
9. Do not claim success for any unperformed check.

## Final response

Report:

- implemented features,
- architecture,
- materially changed files,
- test commands and exact results,
- manual verification performed,
- release artifact and platform limitations,
- unresolved defects or risks,
- documentation status,
- logs created,
- items requiring my review,
- Windows/Linux verification still required,
- and suggested commit boundaries.

Do not publish, deploy, push, or create a remote repository.
<!-- END ORIGINAL PROMPT -->

## Response summary

Implemented immutable constellation and star-position domain values plus an in-memory journal service supporting memory CRUD, multiple constellation membership, cleanup on deletion, search, combined filters, snapshots, and deterministic positions.

## Assumptions and design decisions

- A memory may belong to multiple constellations.
- Duplicate constellation names are rejected case-insensitively after whitespace normalization.
- Deleting a memory removes its star position and every constellation membership; deleting a constellation preserves memories.
- Search fields within a memory use OR semantics. Search and separate active filter categories use AND semantics.
- Results are newest-first, then title.
- Positions are deterministic normalized domain values derived from UUIDs and use a five-percent edge margin.
- A `JournalSnapshot` aggregate is the later persistence seam; no speculative per-entity repository interfaces were introduced.

## Files changed

- Added `model/Constellation.java` and `model/StarPosition.java`.
- Added `application/JournalFilter.java`, `JournalSnapshot.java`, and `JournalService.java`.
- Added `ConstellationTest.java` and `JournalServiceTest.java`.
- Updated `docs/DeveloperGuide.md`.
- Added this log.

## Commands executed and actual results

- Read all required specifications, guides, source, tests, and prior logs using `sed` and `find`.
- Baseline `./gradlew test --no-daemon`: successful; 24 tests had previously been established.
- Focused service/model tests: `BUILD SUCCESSFUL`.
- Complete `./gradlew test --no-daemon`: `BUILD SUCCESSFUL`; 39 tests, 0 skipped, 0 failures, 0 errors.
- Used an `apply_patch` pipeline to insert the attached 536-line master prompt verbatim. The first attempt failed because the attachment has no trailing newline; the corrected attempt succeeded.

## Problems, limitations, and changes after verification

- No checkpoint test failure occurred. One logging-only patch failed due to the attachment's missing final newline and was retried with an explicit separator.
- Persistence and all UI integration remain deliberately absent at this checkpoint.
- Stale membership references supplied through a snapshot are removed during service construction; persistence will report malformed structural data before that recovery where appropriate.
- No changes were required after verification.

## Outcome

Checkpoint 1 is in a working state and provides the tested application-level behavior needed by persistence and UI checkpoints.

## Suggested commit message

`feat: add journal service and constellation model`

## Student review

- [ ] I confirmed that the original prompt is accurate.
- [ ] I confirmed that the changed-file list is accurate.
- [ ] I confirmed that recorded commands were actually executed.
- [ ] I confirmed that build and test results are accurate.
- [ ] I added any mistakes or disagreements omitted by the AI.

Reviewed by:
Review date:
