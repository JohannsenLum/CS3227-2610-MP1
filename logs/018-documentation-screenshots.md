# 018 — Documentation screenshots

Date: 31 August 2026

## Objective

Capture and add four repository-safe screenshots that demonstrate Constella's launch state, memory editor, selected-memory graph behavior, and timeline.

## Original prompt (verbatim)

> okay now look into helping me add the screenshots **1. my-sky.png — the hero shot** *(most important)*
>  Default view on launch with the demo journal intact. Whole window, all 24 mood-coloured
>  spheres visible with constellation edges drawn. This is what a peer tester compares
>  against to know the app launched correctly.
>
>  **2. new-memory.png — the New Memory dialog**
>  Press ⌘N. Fill in a title, date, mood and importance so it isn't blank, and make sure the
>  seven-row constellation picker is in frame.
>
>  **3. my-sky-selected.png — a selected memory**
>  Click one sphere so its details panel opens, unrelated spheres dim, and only its attached
>  edges remain. Worth having because your test checklist explicitly asks testers to verify
>  this behaviour.
>
>  **4. timeline.png — the Timeline view**
>  Cards alternating either side of the central axis, with year markers visible.

## Response summary

Launched Constella with disposable missing data paths so each run received only the fictional 24-memory demo. Captured four macOS window-only PNGs, visually inspected each state, and completed the existing User Guide image references.

## Assumptions

- Screenshots should show real application output rather than generated mockups.
- Window chrome is acceptable, while the surrounding desktop and personal data must be excluded.
- The picker requirement refers to its seven-row viewport; the demo currently contains five named constellations and leaves the remaining visible capacity blank.

## Design decisions

- Used `CONSTELLA_DATA_FILE` with a new temporary directory for every scenario, preventing access to or modification of the user's actual journal.
- Used window-ID capture rather than full-screen capture.
- Because macOS denied accessibility keystrokes, used a temporary JavaFX driver under `/tmp` to invoke existing controls and events inside the running app. The helper was never added to the repository and production code was not changed.
- Preserved the screenshots at Retina resolution because each PNG remained below 600 KB and text stayed readable.

## Files changed

- `docs/UserGuide.md`
- `docs/images/my-sky.png`
- `docs/images/new-memory.png`
- `docs/images/my-sky-selected.png`
- `docs/images/timeline.png`
- `logs/018-documentation-screenshots.md`

## Commands actually executed

- Direct JDK 25 launches using isolated `CONSTELLA_DATA_FILE` paths
- macOS CoreGraphics window enumeration
- window-only `screencapture` commands
- temporary JavaFX helper compilation and scenario launches from `/tmp`
- PNG metadata, size, Markdown-reference, Git diff, and structure checks
- visual inspection of every final image

## Actual build and test results

No production code, tests, build configuration, or release artifact changed, so the 86-test quality gate was not rerun. All four PNG files were successfully decoded and visually inspected. Their final sizes are below 600 KB each, and every User Guide image reference resolves to a repository file.

## Problems or limitations

- macOS blocked `osascript` from sending Command+N because Accessibility permission was unavailable.
- The screenshots prove the macOS visual states only; Windows and Linux window chrome and rendering can differ.
- The animated graph orientation changes over time, so exact node projection may differ between launches even though data and connections are deterministic.

## Changes made after verification

The first automated dialog capture left the title blank because the modal `showAndWait` nested event loop delayed the fill routine. The temporary helper was corrected to schedule the fill before opening the modal dialog, and the screenshot was recaptured with a populated title, date, mood, and importance. The first selected-memory attempt targeted a decorative sphere; the helper was corrected to choose a sphere with a memory click handler, then recaptured with the details panel and edge isolation visible.

## Outcome

The User Guide now contains four accurate, repository-safe screenshots covering the principal peer-testing states requested by the student.

Suggested commit message: `docs: add verified application screenshots`

## Student review

- [ ] I confirmed that the original prompt is accurate.
- [ ] I confirmed that the changed-file list is accurate.
- [ ] I confirmed that recorded commands were actually executed.
- [ ] I confirmed that build and test results are accurate.
- [ ] I added any mistakes or disagreements omitted by the AI.

Reviewed by:
Review date:
