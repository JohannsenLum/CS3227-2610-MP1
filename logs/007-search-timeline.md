# Interaction 007 — Search, filters, and timeline

- Date: 2026-08-26
- Objective: Connect search and filters to My Sky and implement the chronological timeline.

## Checkpoint instructions

> Complete the timeline interface and connect search/filter controls to
> the application service.
>
> Verify combinations and reset behavior.

The complete master prompt is recorded verbatim in `logs/003-journal-service.md`.

## Response summary

Added shared search/filter controls to My Sky and Timeline, reset behavior, responsive filter wrapping, and a newest-first timeline of selectable memory summaries with distinct empty states.

## Assumptions and design decisions

- Search and filter state is shared when navigating between My Sky and Timeline.
- Mood, tag, constellation, and year each offer one exact selection; categories combine with AND.
- Existing service-level search and combined-filter behavior remains the authoritative, UI-independent implementation.

## Files changed

- Updated `ui/ConstellaView.java` with the filter bar and timeline.
- Updated `constella.css` for filters, readable input text, and timeline cards.
- Updated User and Developer Guides.
- Added this log.

## Commands executed and actual results

- Complete `./gradlew test --no-daemon`: `BUILD SUCCESSFUL`; 58 tests, 0 skipped, 0 failures, 0 errors.
- Launched the isolated three-memory fixture and visually confirmed the responsive filter bar above My Sky.
- Attempted to use `cliclick` to navigate to Timeline; macOS denied Accessibility permission, so the click did not occur. This did not affect the application or data.
- Stopped the graphical run with Ctrl-C after inspection; the run task ended with exit code 130.

## Problems, limitations, and changes after verification

- Timeline layout compiled and its ordering/filter data is covered by service tests, but the Timeline screen itself was not visually inspected because desktop input automation lacked permission. It remains on the final manual checklist.
- Screenshot inspection showed that search placeholder contrast was too low. CSS was updated with explicit input and prompt text colours afterward.

## Outcome

Checkpoint 5 is working at the compiled and service-tested level. Filter controls were visually verified; timeline interaction still requires final manual verification.

## Suggested commit message

`feat: add search filters and timeline`

## Student review

- [x] I confirmed that the original prompt is accurate.
- [x] I confirmed that the changed-file list is accurate.
- [x] I confirmed that recorded commands were actually executed.
- [x] I confirmed that build and test results are accurate.
- [x] I added any mistakes or disagreements omitted by the AI.

Reviewed by: A0273503L Lum Yi Ren Johannsen
Review date: 31 August 2026
