# Interaction Log 010 — Immersive Graph

## Original prompt (verbatim)

```text
Continue developing Constella inside the existing MP1 project.

Read completely and follow:

- MP1-spec.md
- AGENTS.md
- README.md
- docs/UserGuide.md
- docs/DeveloperGuide.md
- the existing source code and tests
- logs/001 through logs/009

## Objective

Transform Constella's existing My Sky view into a polished, interactive
2.5D constellation graph inspired by modern knowledge-graph interfaces.

The result should feel spatial, fluid, and immersive while remaining a
JavaFX desktop interface.

Do not implement genuine JavaFX 3D, PerspectiveCamera, Sphere, Cylinder,
or a separate 3D rendering engine in this increment.

Instead, create a 2.5D effect through:

- zooming,
- panning,
- smooth node movement,
- scale,
- glow,
- opacity,
- visual layering,
- focus highlighting,
- and restrained parallax where appropriate.

This is primarily a UI and interaction refinement. Preserve all existing
features, domain rules, persistence behavior, seeded-demo behavior,
search semantics, filter semantics, and deletion behavior.

## Safety and baseline

Before editing:

1. Inspect the current Git state.
2. Do not discard or overwrite uncommitted user work.
3. Inspect the current sky implementation, JavaFX CSS, star positioning,
   constellation relationships, filters, and memory selection flow.
4. Run the complete existing test suite.
5. Launch the current seeded demo journal and inspect the existing sky.
6. Identify concrete usability and visual weaknesses.
7. Propose the smallest architecture for the graph interaction.
8. List the files expected to change.
9. State assumptions and risks.

If Git is available and the working tree is clean, recommend an
appropriate checkpoint or branch before editing, but do not publish,
push, or modify a remote repository.

Do not continue if the existing test suite fails for reasons unrelated
to this increment. Report and diagnose the baseline failure first.

## Desired experience

The My Sky view should behave like an interactive memory graph.

### Default state

When no memory or constellation is selected:

- Display all memories matching the current search and filters.
- Display subtle constellation connections.
- Keep the graph visually balanced.
- Make important memories more visually prominent.
- Avoid excessive labels and overlapping text.
- Keep the central graph as the dominant visual element.

### Memory hover

When the pointer hovers over a memory star:

- Increase its visual prominence.
- Show its title and date through a tooltip or compact overlay.
- Highlight directly related memories.
- Highlight relevant connection lines.
- Dim unrelated memories slightly.
- Do not make unrelated content completely disappear.
- Restore the previous state smoothly when the pointer leaves.

### Memory selection

When a memory is selected:

- Clearly distinguish the selected star.
- Keep directly related memories visible.
- Highlight relevant connections.
- Dim unrelated memories.
- Open or update the existing memory-detail inspector.
- Preserve keyboard activation.
- Preserve accessible text.

### Constellation selection

When a constellation is selected:

- Emphasize memories belonging to that constellation.
- Emphasize its connection path.
- Dim memories outside the constellation.
- Display the constellation name clearly.
- Keep overlapping constellation memberships correct.
- Preserve existing constellation-management behavior.

### Search and filtering

When search or filters change:

- Update the visible graph correctly.
- Recalculate the visual layout when appropriate.
- Use a short, restrained transition instead of abrupt jumping.
- Ensure hidden memories cannot still receive pointer interaction.
- Preserve the existing AND filter semantics.
- Preserve the Reset Filters action.

## Required interactions

### Zoom

Implement graph zooming with the pointer wheel or trackpad.

Requirements:

- Zoom toward the pointer position where practical.
- Define sensible minimum and maximum zoom values.
- Prevent the graph from becoming unusably small or enormous.
- Keep toolbar, navigation, filters, and detail panels fixed.
- Zoom only the graph canvas.
- Provide a visible Reset View or Fit Graph action.
- Avoid accidental zoom while scrolling unrelated interface regions.

### Pan

Allow users to pan the graph canvas.

Requirements:

- Drag empty graph space to pan.
- Do not pan when clicking a star.
- Keep selection behavior reliable.
- Use a sensible pointer cursor during panning.
- Prevent the graph from becoming permanently lost.
- Reset View or Fit Graph must restore a useful viewport.

### Star dragging

If compatible with the existing persisted `StarPosition` design, allow
users to drag individual stars.

Requirements:

- Distinguish star dragging from canvas panning.
- Update connection lines continuously during dragging.
- Keep the star within reasonable graph bounds.
- Persist the new position safely.
- Do not save on every individual mouse-movement event; save at the end
  of the drag or through an appropriate debounced mechanism.
- Preserve deterministic positions for memories that have never been
  manually moved.
- Do not change the persistence format incompatibly without a migration
  or backward-compatible default.

If reliable dragging would create excessive risk, implement zoom,
panning, focus highlighting, and Fit Graph first. Clearly report star
dragging as deferred rather than implementing a fragile version.

### Keyboard support

Preserve or add:

- Tab traversal to graph controls
- Keyboard activation of focusable stars
- Escape to clear graph selection where appropriate
- A keyboard-accessible Reset View or Fit Graph action
- Visible keyboard focus indicators

Mouse-only operation is not acceptable for primary memory selection.

## Visual design

Use a dark celestial visual system:

- Deep navy background
- Subtle blue-purple surface variation
- Warm gold for selected and primary states
- Existing mood colours for semantic identity
- Thin, softly luminous connection lines
- Restrained star glow
- High-contrast readable text
- Consistent spacing and typography

### Star appearance

Star size must continue to reflect importance.

Mood colour must remain identifiable, but importance and selection must
not be communicated through colour alone.

Use visual properties such as:

- size,
- halo radius,
- opacity,
- outline,
- label,
- and focus ring.

Avoid using expensive effects excessively across every node.

### Labels

Prevent the graph from becoming covered in text.

Prefer:

- always showing a label for the selected memory,
- showing a label for the hovered memory,
- showing limited labels for highly important memories,
- and revealing more information as the user zooms in.

Labels should remain readable and should not rotate or scale into
unusable text.

### Connections

Connection lines should:

- update when stars move,
- remain behind stars,
- become brighter when relevant,
- become dimmer when unrelated,
- avoid overpowering the graph,
- and correctly represent overlapping constellations.

Do not represent a relationship that does not exist in the domain data.

## Layout behavior

Preserve deterministic initial positions.

Improve the existing layout only if necessary to:

- reduce obvious overlap,
- use available canvas space,
- keep related memories visually coherent,
- and remain deterministic between launches.

Do not add a permanently running force simulation.

If using force-directed calculations:

- make the algorithm deterministic,
- use a fixed iteration limit,
- keep it independent of JavaFX where practical,
- stop once the layout stabilizes,
- and add automated tests for determinism and bounds.

Avoid adding a heavyweight graph library unless there is a compelling
and documented reason. Prefer existing JavaFX primitives.

## Performance

The seeded 16-memory graph should feel immediate and smooth.

The implementation should also remain usable with at least 100
memories.

Requirements:

- Avoid recreating the entire JavaFX scene graph on every pointer move.
- Avoid repeatedly writing persistence data while dragging.
- Avoid applying expensive blur or shadow effects to every element.
- Avoid unbounded animation creation.
- Stop or replace obsolete animations when filters change rapidly.
- Remove event handlers or bindings when graph elements are discarded.
- Keep UI updates on the JavaFX Application Thread.
- Keep non-UI layout calculations independent where practical.

Do not perform premature micro-optimization, but avoid obvious
per-frame allocation and persistence problems.

## Architecture

Keep responsibilities separated.

Prefer components equivalent to:

- graph viewport state — zoom and translation,
- graph interaction controller — pan, selection, and dragging,
- graph renderer — stars, labels, and lines,
- graph layout logic — deterministic coordinates,
- JavaFX CSS — visual states.

Do not place all behavior into one enormous JavaFX class.

Do not move graph interaction state into the `Memory` domain model.

Persist only durable state such as user-adjusted star positions. Do not
persist transient hover, zoom, pan, focus, or animation state unless
there is a clearly justified user requirement.

## Regression constraints

Preserve:

- all Memory invariants,
- UUID identity behavior,
- existing journal data compatibility,
- safe atomic persistence,
- malformed-file handling,
- demo seeding only on fresh installations,
- no reseeding after Clear Journal,
- constellation multi-membership,
- stale-reference cleanup,
- search behavior,
- filter AND semantics,
- timeline behavior,
- create/edit/delete behavior,
- and platform-labelled release behavior.

Do not add APIs, network access, authentication, databases, analytics,
external fonts, remote assets, or genuine 3D dependencies.

## Testing

Add focused automated tests for non-UI logic introduced by this
increment.

Where applicable, test:

- viewport zoom bounds,
- zoom calculations,
- pan translation calculations,
- reset or Fit Graph calculations,
- deterministic layout,
- layout bounds,
- unchanged persisted positions,
- default positions for new memories,
- filter-to-visible-node behavior,
- graph-neighborhood calculation,
- overlapping constellation relationships,
- and backwards-compatible position loading.

Do not create superficial tests for JavaFX property getters.

Preserve all existing tests.

## Manual verification

After implementation, launch Constella with the fictional seeded
journal and manually inspect:

1. Default populated sky
2. Hovering a memory
3. Selecting a memory
4. Clearing selection
5. Selecting each constellation
6. Overlapping constellation membership
7. Zooming in and out
8. Zoom bounds
9. Panning the canvas
10. Reset View or Fit Graph
11. Star dragging, if implemented
12. Updated connection lines during dragging
13. Persisted dragged position after restart, if implemented
14. Search transitions
15. Every filter type
16. Combined filters
17. Empty filter result
18. Empty journal state
19. Keyboard focus and activation
20. Minimum supported window size
21. A journal with approximately 100 generated in-memory test memories,
    without writing personal data

Do not claim manual verification for interactions that could not
actually be performed.

## Documentation

Update documentation accurately.

### User Guide

Explain:

- graph selection,
- hover behavior,
- zooming,
- panning,
- Reset View or Fit Graph,
- keyboard access,
- star dragging if implemented,
- and how graph state is or is not persisted.

Update the manual test checklist.

### Developer Guide

Explain:

- graph architecture,
- viewport transformations,
- layout behavior,
- selection and neighborhood logic,
- persisted versus transient state,
- performance considerations,
- and accessibility limitations.

Do not describe genuine 3D if the implementation is 2.5D.

### README

Update the feature description only after the behavior is implemented
and verified.

Do not add a final screenshot unless it:

- shows the polished graph clearly,
- contains only fictional seeded data,
- excludes the surrounding desktop,
- and accurately represents the final product.

## Interaction log

Create:

`logs/010-immersive-graph.md`

Include this complete prompt verbatim.

Follow every interaction-logging requirement in AGENTS.md, including:

- assumptions,
- proposed design,
- changed files,
- commands actually executed,
- exact test results,
- visual states actually inspected,
- problems encountered,
- rejected or deferred ideas,
- limitations,
- outcome,
- and suggested commit message.

Do not mark the Student Review checklist as completed.

## Verification commands

At minimum:

1. Run the baseline complete test suite before editing.
2. Run focused tests during implementation.
3. Run the complete test suite after implementation.
4. Run a clean Gradle build.
5. Launch through Gradle.
6. Rebuild the macOS ARM64 release JAR.
7. Verify the JAR archive.
8. Directly launch the rebuilt release JAR.

Report exact commands and results.

## Stop conditions

Stop and report before continuing if:

- the existing persistence format would require a destructive migration,
- the implementation would overwrite user positions,
- the graph requires an unmaintained or heavyweight dependency,
- the existing application cannot be kept functional,
- baseline tests fail unexpectedly,
- or the proposed work expands into genuine 3D rendering.

Do not conceal failures or reduce test coverage to make the increment
pass.

## Final response

Report:

- baseline status,
- weaknesses found in the original sky,
- architecture introduced,
- interactions implemented,
- visual refinements,
- behavior preserved,
- star dragging status,
- tests added,
- complete test results,
- clean-build result,
- manual states verified,
- performance observations,
- release result and checksum,
- files changed,
- documentation changes,
- deferred work,
- remaining usability or accessibility limitations,
- and items requiring my review.
```

## Baseline

- No Git repository was available at the project root, so no branch/checkpoint could be created.
- The complete pre-edit suite passed: 60 tests, 0 failures, 0 errors, 0 skipped.
- The seeded journal launched through Gradle with 16 stars and existing connection paths.
- Weaknesses observed: a fixed oversized canvas created scrollbars and clipped content; only one constellation's lines appeared; there was no zoom, pan, fit, neighborhood hover, durable selection styling, persistent drag, or selective labels; filter changes rebuilt abruptly.

## Assumptions and proposed design

- Use JavaFX 2D groups and transforms only; no genuine 3D API or added dependency.
- Keep deterministic UUID-derived initial positions and persist only explicit star moves.
- Use bounded zoom (0.55–2.25), pointer-centred zoom, bounded panning, and a reset-to-fit action.
- Retain separate edge and star layers and reuse nodes during pointer interactions.
- Calculate viewport state and graph neighborhoods in the application package without JavaFX.
- Show all constellation paths subtly; emphasize a selected constellation and direct hover/selection neighbors.
- Save a dragged star once on release. Zoom, pan, hover, and selection remain transient.
- Read missing legacy coordinates using the existing deterministic fallback; continue writing positions on save without changing format version.

## Design decisions

- `GraphViewportState` owns testable scale/translation behavior.
- `GraphNeighborhood` and `ConstellationConnection` retain relationship and edge identity independent of JavaFX.
- `SkyView` is a retained JavaFX 2D renderer with an edge group behind a star group.
- One replaceable 150 ms timeline changes opacity for focus transitions.
- Star drag clamps normalized coordinates to 0.03–0.97, updates incident lines continuously, and calls `JournalSession` only on release.
- Important memories (importance 5) retain labels; other labels appear contextually.
- A simple path per constellation remains preferable to all-to-all edges.

## Commands executed and results

- `./gradlew test --no-daemon` (baseline) — BUILD SUCCESSFUL; 60 tests passed.
- `./gradlew run --no-daemon` with a temporary `CONSTELLA_DATA_FILE` (baseline) — seeded UI launched; stopped with Ctrl+C after inspection.
- `./gradlew test --no-daemon` (first implementation compile/full check) — BUILD SUCCESSFUL.
- `./gradlew test --no-daemon` (after focused tests) — BUILD SUCCESSFUL.
- `./gradlew run --no-daemon` with a temporary `CONSTELLA_DATA_FILE` (implementation visual check) — seeded UI launched; stopped with Ctrl+C.
- `./gradlew clean build --no-daemon` — BUILD SUCCESSFUL in 8s; 9 actionable tasks executed; 69 tests, 0 failures, 0 errors, 0 skipped.
- `./gradlew releaseJar --no-daemon` — BUILD SUCCESSFUL in 5s; rebuilt `release/Constella-macos-arm64.jar`.
- `unzip -t release/Constella-macos-arm64.jar` — no errors detected.
- `shasum -a 256 release/Constella-macos-arm64.jar` — `e7c6b00940b5af796ef300ced91ede466d7021d8aa5b0ffd74f2e073b760f8fc`.
- `java --enable-native-access=ALL-UNNAMED -jar release/Constella-macos-arm64.jar` using shell Java 21 — expected runtime mismatch: class version 69 is newer than supported version 65.
- `./gradlew -q javaToolchains` — located Gradle-provisioned Eclipse Temurin JDK 25.0.4.1 ARM64.
- `/Users/johannsenlum/.gradle/jdks/eclipse_adoptium-25-aarch64-os_x.2/jdk-25.0.4.1+1/Contents/Home/bin/java --enable-native-access=ALL-UNNAMED -jar release/Constella-macos-arm64.jar` with a temporary data path — launched the seeded application successfully; emitted the documented JavaFX unnamed-module warning; stopped with Ctrl+C after visual inspection.
- `rg` coupling scans — no JavaFX, Gson, or persistence imports in the new graph application/model classes and no genuine JavaFX 3D types under `src`.

## Visual states actually inspected

- Baseline seeded default My Sky.
- Updated seeded default My Sky with subtle global paths, selected-constellation emphasis, compact labels, no inner graph scrollbars, graph toolbar, and Fit Graph control.
- Pointer automation was unavailable because macOS accessibility input permission was not granted; hover, click, pan, zoom, keyboard, and drag behavior are covered by code inspection/pure-state tests but remain on the Student Review manual checklist.

## Problems encountered

- The first screenshot was captured while JavaFX was still showing its initial blank stage; waiting for initialization and capturing again showed the rendered graph.
- A combined delete/add `apply_patch` for `SkyView` was rejected because it targeted one file twice; it made no change and was reapplied as separate patches.
- The desktop input helper lacked macOS Accessibility permission, so scripted pointer-state verification was not claimed.
- The shell default is Java 21, so the first direct release launch could not load Java 25 class files. Re-running with Gradle's provisioned JDK 25 launched successfully; the release itself did not need a change.

## Changed files

- `src/main/java/constella/application/GraphViewportState.java`
- `src/main/java/constella/application/GraphNeighborhood.java`
- `src/main/java/constella/application/ConstellationConnection.java`
- `src/main/java/constella/application/JournalService.java`
- `src/main/java/constella/application/JournalSession.java`
- `src/main/java/constella/persistence/JsonJournalStorage.java`
- `src/main/java/constella/ui/SkyView.java`
- `src/main/java/constella/ui/ConstellaView.java`
- `src/main/resources/constella/ui/constella.css`
- `src/test/java/constella/application/GraphViewportStateTest.java`
- `src/test/java/constella/application/GraphNeighborhoodTest.java`
- `src/test/java/constella/application/JournalServiceTest.java`
- `src/test/java/constella/application/JournalSessionTest.java`
- `src/test/java/constella/persistence/JsonJournalStorageTest.java`
- `README.md`
- `docs/UserGuide.md`
- `docs/DeveloperGuide.md`
- `logs/010-immersive-graph.md`

## Rejected or deferred ideas

- Genuine JavaFX 3D, PerspectiveCamera, Sphere, Cylinder, and external render engines are explicitly out of scope.
- Force-directed simulation and collision solving would reduce deterministic behavior and add complexity.
- Parallax was omitted because zoom, layering, opacity, and restrained glow already establish depth without distracting motion.
- Undo for star movement, overview/minimap, and saved viewport state are deferred.

## Limitations

- Dense or manually arranged nodes can overlap.
- There is no movement undo or collision solver.
- Pointer/keyboard interaction still requires student manual review on the target desktop.
- Windows and Linux visual/release verification remain outstanding.

## Performance observations

- The renderer retains star and edge nodes during hover/selection/drag and changes only properties/styles on pointer movement.
- Connections remain linear paths rather than all-to-all graphs. The focused 100-memory test produced 99 edges and completed as part of the 69-test suite.
- The seeded 16-memory Gradle and direct-JAR views initialized and rendered without visible delay. Full interactive 100-memory UI profiling remains manual review work.

## Outcome

Implemented the requested JavaFX 2D immersive graph increment without genuine 3D or a new dependency. Existing CRUD, filtering, timeline, constellation, seeded-demo, clearing, and persistence behavior remains wired through the existing application layers. All 69 tests pass, the clean build succeeds, and the rebuilt macOS ARM64 artifact validates and launches on JDK 25.

## Suggested commit message

`feat: make My Sky an interactive 2.5D memory graph`

## Student review

- [ ] I confirmed that the original prompt is accurate.
- [ ] I confirmed that the changed-file list is accurate.
- [ ] I confirmed that recorded commands were actually executed.
- [ ] I confirmed that build and test results are accurate.
- [ ] I added any mistakes or disagreements omitted by the AI.

Reviewed by: Johannsen
Review date: 26 August 2026
