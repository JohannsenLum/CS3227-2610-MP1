# Interaction Log 011 — JavaFX 3D Space

Date: 27 August 2026

## Original prompt

```text
Continue developing Constella inside the existing MP1 project.

Read and follow MP1-spec.md, AGENTS.md, the existing code,
documentation, tests, and interaction logs.

## Objective

Add an optional genuine JavaFX 3D memory graph called `3D Space`.

Do not replace or break My Sky, Timeline, memory CRUD, persistence,
search, filters, constellations, demo seeding, or Clear Journal.

Before editing:

1. Inspect the current Git state and uncommitted changes.
2. Preserve all existing work.
3. Run the complete baseline test suite.
4. Inspect the existing graph, service, filtering, and selection code.
5. Explain the proposed architecture and files to change.

## Required 3D experience

Use genuine JavaFX 3D:

- `SubScene`
- `PerspectiveCamera`
- `Sphere` for memories
- `Cylinder` for connections
- `PhongMaterial`
- restrained ambient and point lighting

Each memory sphere must:

- use importance for size,
- use mood for colour,
- support hover and selection,
- update the existing memory-details panel,
- and have a deterministic X, Y, and Z position.

The layout must contain meaningful depth rather than placing every node
at Z = 0.

## Graph relationships

Connect memories belonging to the same constellation.

Avoid connecting every possible pair. For each constellation, order
members deterministically by date and UUID, then connect consecutive
memories as a sparse chronological path.

Deduplicate edges created by overlapping constellations.

When hovering or selecting a memory:

- highlight its direct neighbors and attached edges,
- dim unrelated graph elements,
- and show its title and date in a readable 2D overlay.

When selecting a constellation:

- highlight its memories and edges,
- dim unrelated content,
- and preserve overlapping memberships correctly.

Search and filters must update the 3D graph using the existing AND
semantics.

## Camera controls

Implement:

- primary-drag on empty space to rotate,
- scroll to zoom,
- Shift-drag or secondary-drag to pan,
- `Reset Camera`,
- and `Focus Selected`.

Add sensible limits so users cannot lose the graph or move through it.
Clicking or dragging a sphere must not accidentally rotate the camera.

Use a normal 2D JavaFX overlay for labels and controls so text remains
readable.

## Architecture

Keep graph and layout mathematics independent of JavaFX.

Separate:

- graph construction,
- 3D vector and layout calculations,
- JavaFX rendering,
- camera control,
- and picking/selection.

Do not store JavaFX objects or camera state in the domain model.

Avoid changing the JSON format. Recalculate deterministic 3D positions
from existing journal data unless persistence is clearly necessary.

Do not add game engines, browser renderers, external 3D libraries,
network services, or unrelated features.

## Performance and fallback

The seeded 16-memory graph should feel smooth and a 100-memory graph
should remain usable.

Avoid:

- permanent force simulations,
- rebuilding all geometry on every mouse movement,
- expensive effects on every node,
- unbounded animations,
- and persistence writes during camera movement.

If JavaFX 3D cannot initialize, show a useful message and preserve My
Sky and Timeline as functional alternatives.

## Testing

Add UI-independent tests for:

- graph construction,
- sparse and deduplicated edges,
- overlapping constellations,
- 3D vector operations,
- connection midpoint, length, and orientation,
- zero-length connections,
- deterministic layout,
- finite and bounded coordinates,
- meaningful Z depth,
- zoom and rotation bounds,
- empty and one-node graphs,
- and a 100-node graph.

Preserve all existing tests.

## Verification

After implementation:

1. Run the complete tests and clean build.
2. Launch the seeded journal.
3. Verify rotation, zoom, pan, reset, sphere hover/selection, Focus
   Selected, constellation highlighting, search, filters, empty state,
   navigation between views, and keyboard access.
4. Rebuild and directly launch the macOS ARM64 release JAR.
5. Record exact results, artifact size, and SHA-256.
6. Do not claim Windows or Linux verification unless actually performed.

## Documentation and log

Update README.md, UserGuide.md, and DeveloperGuide.md accurately.

Create the next sequential interaction log, expected to be:

`logs/011-javafx-3d-space.md`

Include this prompt verbatim and follow AGENTS.md. Do not complete the
Student Review checklist.

## Final response

Report:

- architecture and interactions implemented,
- files changed,
- tests and exact results,
- manual checks performed,
- release result and checksum,
- documentation changes,
- limitations,
- and items requiring my review.

Do not push, publish, deploy, or remove the existing 2D views.
```

## Objective

Add an optional genuine JavaFX 3D memory graph without replacing or changing the persisted behavior of existing views.

## Response summary

Implemented UI-independent 3D graph/layout/camera mathematics, a genuine JavaFX 3D renderer, shared-filter/navigation integration, focused tests, documentation, fallback guidance, and release verification.

## Assumptions

- 3D positions and camera state are transient and deterministic; existing JSON and saved My Sky positions remain unchanged.
- Chronological date then UUID is the authoritative path order.
- Duplicate endpoint pairs retain every contributing constellation ID so overlap highlighting remains correct.
- A bounded chronological spiral provides meaningful depth without a force simulation.
- UI pointer automation unavailable through macOS Accessibility must remain student manual review rather than being claimed as verified.

## Design decisions

- `Vector3` and `ConnectionGeometry` own finite vector/cylinder mathematics without JavaFX.
- `MemoryGraphBuilder` constructs sparse, deduplicated graphs; `Memory3DLayout` supplies deterministic bounded depth.
- `CameraState` bounds pitch, yaw, distance, and pan and implements reset/focus behavior.
- `Space3DView` owns rendering, camera event mapping, picking, selection, highlights, and fallback only.
- Geometry is rebuilt for content/filter changes, never for mouse movement.
- A 2D StackPane overlay keeps labels and fallback messages readable.

## Files changed

- `src/main/java/constella/application/Vector3.java`
- `src/main/java/constella/application/ConnectionGeometry.java`
- `src/main/java/constella/application/MemoryGraphEdge.java`
- `src/main/java/constella/application/MemoryGraph.java`
- `src/main/java/constella/application/Memory3DLayout.java`
- `src/main/java/constella/application/MemoryGraphBuilder.java`
- `src/main/java/constella/application/CameraState.java`
- `src/main/java/constella/application/GraphSelectionState.java`
- `src/main/java/constella/ui/Space3DView.java`
- `src/main/java/constella/ui/ConstellaView.java`
- `src/main/resources/constella/ui/constella.css`
- `src/test/java/constella/application/Vector3Test.java`
- `src/test/java/constella/application/ConnectionGeometryTest.java`
- `src/test/java/constella/application/MemoryGraphBuilderTest.java`
- `src/test/java/constella/application/CameraStateTest.java`
- `src/test/java/constella/application/GraphSelectionStateTest.java`
- `README.md`
- `docs/UserGuide.md`
- `docs/DeveloperGuide.md`
- `logs/011-javafx-3d-space.md`

## Commands executed

- `git status --short --branch`
- `cat AGENTS.md`, `cat MP1-spec.md`, project/documentation/log inspection commands using `cat`, `rg`, and `rg --files`
- `./gradlew test --no-daemon` (baseline)
- `./gradlew test --tests 'constella.application.Vector3Test' --tests 'constella.application.ConnectionGeometryTest' --tests 'constella.application.MemoryGraphBuilderTest' --tests 'constella.application.CameraStateTest' --no-daemon`
- `./gradlew test --no-daemon` (implementation check)
- `./gradlew run --no-daemon` with a temporary `CONSTELLA_DATA_FILE` (normal startup)
- `/opt/homebrew/bin/cliclick c:750,730` (failed due missing Accessibility privileges)
- A temporary `showSpace3D()` startup change, `./gradlew run --no-daemon`, screenshot inspection, then restoration to `showSky()`
- Final verification commands and results are recorded below.
- `./gradlew clean build --no-daemon` and XML result aggregation (final after selection extraction)
- `./gradlew releaseJar --no-daemon`
- `unzip -t release/Constella-macos-arm64.jar`
- `stat -f 'bytes=%z' release/Constella-macos-arm64.jar`
- `shasum -a 256 release/Constella-macos-arm64.jar`
- Direct launch with Gradle-provisioned Temurin JDK 25 and a temporary `CONSTELLA_DATA_FILE`
- `rg` scans for required JavaFX 3D types and prohibited application/model coupling

## Actual build and test results

- Baseline: BUILD SUCCESSFUL; 69 tests, 0 failures, 0 errors, 0 skipped.
- Focused 3D tests: BUILD SUCCESSFUL.
- First complete implementation check: BUILD SUCCESSFUL.
- Final clean build: BUILD SUCCESSFUL in 7s; all 9 actionable tasks executed.
- Final complete suite: 81 tests, 0 failures, 0 errors, 0 skipped.
- Release build: BUILD SUCCESSFUL in 5s.
- Archive verification: no errors detected in compressed data.
- Artifact: `release/Constella-macos-arm64.jar`, 9,266,309 bytes.
- SHA-256: `f7d43422dfb6aae4c587aa5d3797d9d13b0a0b86da75e230cfeab001026aea93`.
- Direct JDK 25 launch: successful; the existing JavaFX unnamed-module warning was emitted. The app was stopped with Ctrl+C after initialization.
- Coupling scan: JavaFX 3D types occur in `Space3DView`; new graph/layout/camera/selection classes have no JavaFX, Gson, or persistence coupling, and no 3D state appears in model/persistence packages.

## Manual checks actually performed

- Launched the seeded journal normally through Gradle and confirmed My Sky, Timeline navigation presence, 3D Space navigation presence, filters, and the existing 2D graph still rendered.
- Launched a temporary startup-to-3D build with fresh seeded data and confirmed 16 lit, mood-coloured, importance-sized spheres, sparse cylinder paths, visible perspective/depth, controls, and the 2D overlay area rendered without fallback or visible delay.
- Restored normal My Sky startup, rebuilt, and directly launched the final release JAR on macOS ARM64 using JDK 25.
- Rotation, zoom, pan, reset, sphere hover/click/keyboard selection, Focus Selected, live constellation focus, live search/filter/empty transitions, and the unsupported-3D fallback were not manually exercised because desktop input automation lacked Accessibility permission. Their state/math paths are covered where UI-independent; they remain unchecked in the User Guide and Student Review.

## Performance observations

- The seeded 16-node 3D view initialized and rendered without visible delay.
- The 100-node test constructs exactly 99 edges for one constellation and validates bounded coordinates.
- Geometry is retained for hover, selection, and camera movement; no force loop, effect loop, animation, or persistence write is used. A live 100-node pointer-performance session remains student review work.

## Problems and limitations

- The directory is not a Git repository, so Git status/checkpoint information is unavailable.
- macOS Accessibility permission was not enabled for `cliclick`; pointer/keyboard interaction automation was not claimed.
- The default 3D view was visually inspected through a temporary startup switch that was reverted before final build.
- Windows and Linux are not verified.

## Changes made after verification

- Restored My Sky as the initial view after visually inspecting 3D Space.
- Extracted hover/selection precedence from the JavaFX renderer into tested `GraphSelectionState` after the architecture review.

## Outcome

Implemented optional genuine JavaFX 3D Space without replacing the existing 2D views or changing JSON. All 81 tests pass, the clean build and release build succeed, the archive validates, and the final macOS ARM64 artifact launches on JDK 25. Blocked pointer/keyboard manual states are explicitly deferred for student review rather than claimed.

## Suggested commit message

`feat: add optional JavaFX 3D memory space`

## Follow-up prompt — force-directed visual polish

```text
Refine the current JavaFX 3D Space using the newly provided reference.

The desired result is a genuine 3D force-directed knowledge graph:
many small nodes, extremely thin edges, organic clusters, meaningful
depth, restrained colour, and free camera exploration.

Preserve existing functionality and do not discard working changes.

## Graph projection

Build the visual graph from existing journal data using these node types:

- Memory
- Constellation
- Tag
- Person
- Location

Memory nodes represent real memories.

Constellation, tag, person, and location nodes are transient graph
projections. They must not become persisted domain entities and must not
change the JSON schema.

Create edges for:

- memory → constellation membership,
- memory → tag,
- memory → person,
- memory → location.

Do not connect unrelated memories or invent data.

Normalize projected nodes consistently with the existing domain rules.
Deduplicate projected nodes and edges deterministically.

## Visual hierarchy

Render all graph elements as genuine JavaFX 3D geometry.

Use:

- small spheres for memory nodes,
- slightly larger spheres for constellation hubs,
- smaller spheres for tags, people, and locations,
- and extremely thin cylinders for edges.

Use restrained colours by node type:

- memories: cool white or pale blue,
- constellations: muted gold,
- tags: muted violet,
- people: muted teal,
- locations: muted coral.

Mood may appear as a subtle memory-node accent, but avoid making every
memory a saturated planet.

Memory importance should produce moderate size differences.
Hub connection count may produce limited size differences.

Selected nodes should brighten and enlarge slightly. Direct neighbors
and attached edges should brighten. Unrelated elements should dim.

## Force-directed 3D layout

Replace the decorative clustered placement with a deterministic,
bounded 3D force-directed layout.

Include:

- repulsion between all nodes,
- attraction along edges,
- collision separation,
- weak gravity toward the centre,
- velocity damping,
- maximum velocity,
- bounded X/Y/Z coordinates,
- deterministic initial coordinates,
- a fixed iteration limit,
- and early stopping after stabilization.

The algorithm must be independent of JavaFX and covered by tests.

Do not run a permanent simulation. Calculate a settled layout, then
animate rendered nodes briefly toward their final positions.

The same input must produce the same layout.

Highly connected hubs should naturally form cluster centres.
Disconnected components should remain visible without overlapping the
main graph.

## Rendering

Make the graph fill the central workspace.

Remove the prominent rounded border and planet-like styling.

Use:

- dark near-black background,
- small restrained nodes,
- hairline connections,
- minimal glow,
- subtle depth-based brightness,
- and limited labels.

Always display labels for:

- hovered node,
- selected node,
- and selected-node direct neighbors where space allows.

Use a screen-facing 2D overlay for labels.

Selecting:

- a memory opens existing memory details,
- a constellation applies existing constellation focus,
- a tag, person, or location highlights associated memories.

Projected metadata nodes must not expose memory edit/delete actions.

## Controls

Preserve:

- drag to rotate,
- Shift-drag or secondary-drag to pan,
- scroll to zoom,
- Reset Camera,
- Focus Selected,
- keyboard access,
- search,
- filters,
- and the non-3D fallback.

Add compact visibility toggles for:

- Constellations
- Tags
- People
- Locations

Memories must always remain visible.

Changing a toggle should rebuild or update the graph predictably without
changing persisted journal data.

## Performance

The seeded demo should generate approximately 40–70 meaningful graph
nodes from its existing data.

The graph should remain usable with at least 100 memories and their
projected metadata nodes.

Avoid permanent simulations, excessive transparency, per-frame scene
rebuilding, and expensive effects on every node.

## Testing

Add tests for:

- projected node construction,
- deterministic IDs for metadata nodes,
- normalization and deduplication,
- edge construction and deduplication,
- visibility toggles,
- selection neighborhoods,
- deterministic 3D force layout,
- finite and bounded coordinates,
- collision separation,
- meaningful depth,
- disconnected components,
- and a 100-memory graph.

Run the complete suite and clean build.

Launch the seeded demo and verify the new visual density, cluster
structure, node-type selection, toggles, camera controls, filtering,
empty state, and existing views.

Rebuild and directly launch the release JAR.

Update documentation and append this as a follow-up in the current 3D
visual-polish interaction log. If that log is complete, create the next
sequential log.

Do not change persistence, add fictional memories merely for visual
density, introduce external 3D engines, or remove My Sky and Timeline.
```

## Follow-up assumptions and decisions

- The written reference was treated as authoritative because no separate reference image was available in the turn.
- Metadata projection uses normalized type/value name-based UUIDs; memory nodes retain real UUIDs.
- The default enables constellations, tags, and people but leaves locations off, producing 61 seeded nodes within the requested approximate range. Locations remain one click away and the all-types projection contains 77 nodes.
- A fixed deterministic simulation settles before rendering; only a 340 ms group-scale transition runs afterward.
- Disconnected components receive deterministic weak anchors in addition to repulsion and gravity.
- At most seven direct-neighbor labels accompany the focused node to limit overlap.

## Follow-up files changed

- Added `ProjectedNodeType`, `ProjectionVisibility`, `ProjectedNode`, `ProjectedEdge`, `ProjectedGraph`, `JournalGraphProjector`, and `ForceDirected3DLayout` under `src/main/java/constella/application/`.
- Reworked `src/main/java/constella/ui/Space3DView.java`.
- Updated `src/main/java/constella/ui/ConstellaView.java` and `src/main/resources/constella/ui/constella.css`.
- Added `JournalGraphProjectorTest` and `ForceDirected3DLayoutTest`.
- Updated `README.md`, `docs/UserGuide.md`, `docs/DeveloperGuide.md`, and this log.

## Follow-up commands and actual intermediate results

- `./gradlew test --no-daemon` baseline — BUILD SUCCESSFUL; 81 tests, 0 failures/errors/skips.
- First focused projection/layout test command — compilation failed because reassigned record parameters were captured by a lambda; no tests ran.
- Repeated focused command after constructor correction — 7 tests ran, 1 disconnected-component assertion failed.
- Repeated disconnected test with diagnostics — component centres were both zero because nodes had exploded to coordinate bounds.
- Corrected the edge-attraction sign; repeated focused projection/layout suite — BUILD SUCCESSFUL, 7 tests passed.
- `./gradlew test --no-daemon` after renderer integration — BUILD SUCCESSFUL.
- JDK 25 JShell measurement — all-types seed projection: 77 nodes and 112 edges.
- Temporary startup-to-3D `./gradlew run --no-daemon` with disposable data — rendered the 61-node default force graph without visible delay; the temporary startup change was reverted to My Sky.
- Final clean-build, test, and release results are recorded in the follow-up outcome below.

## Follow-up problems and changes after verification

- The force spring initially used the wrong sign and pushed connected endpoints apart. Bounds hid unbounded movement but the disconnected-component test exposed it. The force direction was corrected rather than weakening the test.
- Global repulsion alone allowed disconnected component centroids to interleave. Deterministic weak component anchors were added.
- All projected types yielded 77 seeded nodes, slightly above the requested default range. Locations now start disabled, giving 61 default nodes while preserving the toggle and all data.
- macOS Accessibility still prevents reliable scripted pointer/keyboard interaction. Those live states remain manual review items and are not claimed as verified.

## Follow-up outcome

- Final `./gradlew clean build --no-daemon`: BUILD SUCCESSFUL in 9s; all 9 actionable tasks executed.
- Final complete suite: 89 tests, 0 failures, 0 errors, 0 skipped.
- Coupling scans found no JavaFX, Gson, or persistence imports in the new projection/force classes and no projected graph types in model or persistence packages.
- `./gradlew releaseJar --no-daemon`: BUILD SUCCESSFUL in 5s.
- `unzip -t release/Constella-macos-arm64.jar`: no errors detected.
- Final artifact size: 9,287,829 bytes.
- Final SHA-256: `edda29a5a8ca86bbf5463a3c19873506db03bbf1a65b8679f2d722c3797a2554`.
- Direct launch of the final JAR with Gradle-provisioned Temurin JDK 25 and disposable data succeeded; the existing JavaFX unnamed-module warning appeared, and the app was stopped with Ctrl+C after initialization.
- Outcome: the optional 3D view now projects real journal metadata into a deterministic settled force graph while preserving My Sky startup and all persistence/domain behavior. Live camera/toggle/node/filter/empty-state input paths still require Student Review because macOS blocked desktop automation.

## Follow-up suggested commit message

`feat: project journal metadata into a force-directed 3D graph`

## Correction prompt — memory-only graph

```text
Correct the current Constella 3D graph implementation.

The previous metadata-projection approach was wrong. It produced
artificial hub-and-spoke structures that do not represent the desired
Obsidian-style memory graph.

Preserve the working journal, 2D views, persistence, CRUD, filters,
camera controls, and release configuration.

## Required graph semantics

The 3D graph must contain:

- exactly one node for each visible Memory,
- and only memory-to-memory edges emitted by `MemoryGraphBuilder`.

An edge must exist if and only if it is present in
`MemoryGraph.edges()`.

Do not render nodes for:

- constellations,
- tags,
- people,
- locations,
- moods,
- years,
- or any other metadata.

Do not render metadata spokes, constellation hubs, inferred similarity
edges, decorative graph nodes, or duplicate edges.

Remove the metadata visibility toggles from the 3D interface.

Stop using `JournalGraphProjector`, `ProjectedGraph`, `ProjectedNode`,
`ProjectedEdge`, `ProjectedNodeType`, and `ProjectionVisibility` in the
3D view.

If those classes were introduced only for this incorrect graph feature
and have no other consumers, remove them and their obsolete tests
carefully. Do not remove code used by other application features.

## Edge rules

Use the existing `MemoryGraphBuilder` behavior:

- constellation members are ordered chronologically,
- consecutive memories form a sparse path,
- overlapping constellation edges are deduplicated,
- and each edge retains its contributing constellation IDs.

Do not create a complete graph between every pair of constellation
members.

Add an invariant test confirming:

- every rendered node corresponds to one visible memory,
- every rendered edge corresponds to one `MemoryGraphEdge`,
- every edge endpoint exists in the visible memory set,
- and no other node or edge is created.

## Layout

Create a deterministic 3D force-directed layout using only the real
memory nodes and real memory edges.

Use:

- repulsion between memory nodes,
- attraction only along existing edges,
- collision separation,
- weak centring force,
- velocity damping,
- bounded velocity,
- bounded X/Y/Z coordinates,
- deterministic initial positions,
- fixed iteration limit,
- and early stopping after stabilization.

Connected memories should form organic clusters.

Disconnected memories may float separately, but must remain inside the
initial camera view.

Do not arrange nodes into radial spokes, straight rows, rigid rings, or
metadata-centred stars.

The same graph input must produce the same settled layout.

Do not run a permanent simulation.

## Visual style

Match the visual language of a restrained Obsidian-style 3D graph.

Use:

- a near-black background,
- small memory spheres,
- very thin memory-to-memory connections,
- subtle depth,
- restrained colours,
- and minimal glow.

Suggested proportions:

- memory sphere radius approximately 2.5–5.5,
- default connection cylinder radius approximately 0.15–0.35,
- selected connection radius no more than approximately 0.6,
- moderate importance-based size differences.

Treat these as starting points and adjust through visual inspection.

Memory nodes should primarily use cool white, silver, or muted blue.
Mood may provide a subtle tint, not a saturated planet colour.

Selected nodes may:

- brighten,
- grow slightly,
- and receive a restrained halo or ring.

Direct neighbors and their existing edges should brighten.
Unrelated nodes and edges should dim but remain visible.

## Labels

Use a screen-facing 2D overlay.

Show labels only for:

- the hovered memory,
- the selected memory,
- and optionally the most important memories when space permits.

Do not place every title permanently in the 3D world.

Selecting a memory must continue to update the existing detail panel.

## Camera and interactions

Preserve:

- primary-drag to rotate,
- Shift-drag or secondary-drag to pan,
- scroll to zoom,
- Reset Camera,
- Focus Selected,
- hover highlighting,
- selection,
- keyboard access,
- search,
- filters,
- and JavaFX 3D fallback.

Search and filters must rebuild the graph using only the currently
visible memories and the valid edges between them.

Clear the selection if its memory becomes hidden.

## Visual composition

Make the graph occupy the central workspace without a heavy rounded
frame or decorative panel.

Keep camera controls compact and visually subordinate.

Do not add fake background nodes to make the graph appear denser.

The demo has only 16 memories, so prioritize a clean, readable graph
instead of pretending it contains hundreds of nodes.

If greater density is desired later, it must come from additional real
memories and real relationships.

## Testing

Update or add tests for:

- one rendered node per visible memory,
- no metadata nodes,
- edge equality with `MemoryGraph.edges()`,
- no invalid endpoints,
- edge deduplication,
- overlapping constellations,
- empty graph,
- one-memory graph,
- disconnected memories,
- deterministic force layout,
- finite and bounded X/Y/Z coordinates,
- collision separation,
- meaningful depth,
- and a 100-memory graph.

Remove tests that exist only to validate the rejected metadata
projection behavior.

Run the complete test suite and clean build.

## Manual verification

Launch the seeded journal and confirm:

- exactly 16 memory nodes are displayed when all 16 are visible,
- no tag/person/location/constellation nodes exist,
- no metadata spokes exist,
- every visible line connects two real memories,
- connected memories form organic clusters,
- isolated memories remain visible,
- nodes are small,
- lines are thin,
- hover and selection highlight only real graph neighbors,
- search and filters update nodes and edges correctly,
- camera controls still work,
- and My Sky and Timeline remain unchanged.

Rebuild and directly launch the macOS release JAR.

## Documentation and logging

Remove documentation describing projected metadata nodes or metadata
visibility toggles.

Document the exact rule:

“3D Space contains one node per visible memory. Edges are the sparse
chronological memory connections contributed by constellations.”

Record this correction in the current visual-polish log as a follow-up
prompt, or create the next sequential log if that interaction is
already complete.

Explain that the metadata projection was rejected because it introduced
artificial nodes and misleading hub-and-spoke geometry.

Do not change persistence, add fake memories, add metadata graph nodes,
or remove the existing non-3D views.
```

## Correction summary and decisions

- Rejected the metadata projection because it introduced artificial nodes and misleading hub-and-spoke geometry rather than the requested Obsidian-style memory graph.
- Restored `MemoryGraphBuilder` as the sole graph-construction authority.
- Changed `ForceDirected3DLayout` to accept `MemoryGraph` and apply forces only to real memory UUIDs and `MemoryGraphEdge`s.
- Added `MemoryGraphRenderPlan` as the tested exact renderer boundary: one node per graph memory, the exact graph edge list, and no invalid endpoints.
- Removed the metadata projection classes, visibility switches, metadata renderer branches, obsolete tests, interface toggles, and corresponding documentation.
- Preserved the deterministic fixed-step force solver, component anchors, camera controls, selection, fallback, filtering, and brief settle animation.
- Tightened styling to 2.7–5.3 radius cool memory spheres, 0.24 default edge cylinders, and 0.48 selected edge cylinders.
- Limited 2D overlay labels strictly to the hovered or selected memory.

## Correction files changed

- Updated `ForceDirected3DLayout`, `CameraState`, `Space3DView`, `ConstellaView`, CSS, camera/layout tests, README, User Guide, Developer Guide, and this log.
- Added `MemoryGraphRenderPlan` and `MemoryGraphRenderPlanTest`.
- Removed `ProjectedNodeType`, `ProjectionVisibility`, `ProjectedNode`, `ProjectedEdge`, `ProjectedGraph`, `JournalGraphProjector`, `JournalGraphProjectorTest`, and their obsolete renderer/UI usage.

## Correction commands and intermediate results

- `git status --short --branch` — no Git repository exists at or above MP1.
- Consumer scan with `rg` confirmed rejected projection code was used only by 3D Space and its tests/documentation.
- Baseline `./gradlew test --no-daemon` — BUILD SUCCESSFUL; 89 tests, 0 failures, 0 errors, 0 skipped.
- `./gradlew test --no-daemon` after correction — BUILD SUCCESSFUL.
- Temporary startup-to-3D Gradle launch with disposable seed data — window-specific capture confirmed exactly 16 small memory spheres, no metadata hubs/spokes, and only real memory lines.
- Visual inspection found the initial camera slightly distant and idle labels outside the SubScene; default camera distance was changed from -950 to -720, idle labels were removed, and SubScene-aware label projection was retained for hover/selection.
- Temporary startup was restored to My Sky before final build.
- Final build/release results are recorded below after verification.

## Correction limitations

- macOS Accessibility still blocks reliable scripted hover, click, keyboard, filter, and camera input. Exact geometry and graph invariants are automated; those live paths remain Student Review items.
- Windows and Linux remain unverified.

## Correction outcome

- Final `./gradlew clean build --no-daemon`: BUILD SUCCESSFUL in 8s; all 9 actionable tasks executed.
- Final complete suite: 87 tests, 0 failures, 0 errors, 0 skipped. The count decreased from 89 because five rejected projection tests were removed and replaced by corrected force/render invariant coverage.
- Coupling scans found no JavaFX, Gson, or persistence imports in the corrected force/render-plan classes and no new graph types in model/persistence packages.
- Source/documentation scan found no executable metadata projection or visibility-toggle references; the User Guide retains only the explicit manual check that metadata nodes must be absent.
- `./gradlew releaseJar --no-daemon`: BUILD SUCCESSFUL in 5s.
- `unzip -t release/Constella-macos-arm64.jar`: no errors detected.
- Final artifact size: 9,275,610 bytes.
- Final SHA-256: `b20165bc228927d0e3949355e9484dbfe256004d4802de25f912ee78036c2e46`.
- Direct final-JAR launch with Gradle-provisioned Temurin JDK 25 and disposable data succeeded; the existing JavaFX unnamed-module warning appeared, and the process was stopped with Ctrl+C after initialization.
- A stale Gradle visual-verification JVM from the earlier launch was identified with `ps` and terminated gracefully with `kill` after verification.
- Outcome: 3D Space now renders exactly one node per visible memory and exactly the sparse builder edges, with deterministic memory-only force layout. Persistence, My Sky startup, Timeline, CRUD, filters, camera controls, and release configuration remain intact.

## Correction suggested commit message

`fix: render only real memories in the 3D graph`

## Follow-up prompt — stars, richer seed, controls, and timeline

```text
there should be also some items that are not connected right? I want a more detailed seeder so we can see more items in the 3d space and please make it a star instead of just a ball.  and a toggle to show connection and off the lines. I also want a auto rotate function in the 3d space. and also zoom in and out.

For timeline it looks back, i want a real timeline view
```

The prompt included the screenshot `/var/folders/7h/q0hhtj5s0qbg3_ms417dyfp40000gn/T/codex-clipboard-d715eb1f-e3cc-4b5f-b303-a6d50758e20f.png`. It showed the Timeline route with filters and a selected detail panel, but its main scroll area appeared as a large blank blue rectangle.

## Follow-up assumptions and decisions

- Interpreted “not connected” as real memories outside every constellation, because 3D edges continue to come only from constellation membership; no artificial graph nodes or relationships were added.
- Expanded only the deterministic first-run seed. Existing journal files remain unchanged, and Clear Journal still persists an intentionally empty journal rather than silently restoring demo data.
- Expanded the seed from 16 to 24 memories across the four-year NUS story, including exchange, travel, internship, volunteering, holidays, and quieter personal events. Exactly three seeded memories belong to no constellation.
- Replaced memory spheres with extruded five-point `TriangleMesh` stars. These are genuine JavaFX 3D meshes with front, back, and side faces, not 2D icons.
- Kept connection visibility, auto-rotation, and zoom as transient renderer/camera state. They never change memories, constellations, or JSON.
- Added reversible connection and auto-rotate checkboxes plus explicit minus/plus zoom buttons; scroll zoom, drag rotation, pan, reset, and focus remain available.
- Replaced the generic timeline list with a dedicated alternating timeline: year markers, central axis, event markers, and left/right memory cards, while retaining the shared newest-first filtered result and existing details panel.

## Follow-up files changed

- `src/main/java/constella/application/DemoJournalSeeder.java`
- `src/main/java/constella/ui/ConstellaView.java`
- `src/main/java/constella/ui/Space3DView.java`
- `src/main/java/constella/ui/StarMeshFactory.java` (new)
- `src/main/java/constella/ui/TimelineView.java` (new)
- `src/main/resources/constella/ui/constella.css`
- `src/test/java/constella/application/DemoJournalSeederTest.java`
- `src/test/java/constella/application/JournalSessionTest.java`
- `README.md`
- `docs/UserGuide.md`
- `docs/DeveloperGuide.md`
- `logs/011-javafx-3d-space.md`

## Follow-up commands and actual results

- Inspected Git state: this directory is still not a Git repository, so no Git status or diff is available; all existing files were preserved.
- Baseline `./gradlew test --no-daemon`: BUILD SUCCESSFUL; 87 tests, 0 failures, 0 errors, 0 skipped.
- Inspected current seeder, graph builder, force layout, selection, renderer, filter flow, timeline implementation, CSS, documentation, tests, and this log with `sed`, `find`, and `rg`.
- Inspected the supplied screenshot with the local image viewer; confirmed the blank-looking timeline body described above.
- Intermediate `./gradlew test --no-daemon`: BUILD SUCCESSFUL after the renderer/timeline changes.
- Final `./gradlew clean build --no-daemon`: BUILD SUCCESSFUL in 8s; all 9 actionable tasks executed.
- Parsed final JUnit XML: 88 tests, 0 failures, 0 errors, 0 skipped.
- Coupling scan with `rg`: no JavaFX, Gson, or persistence references in `ForceDirected3DLayout`, `MemoryGraphBuilder`, or the model package.
- `./gradlew releaseJar --no-daemon`: BUILD SUCCESSFUL in 4s.
- `unzip -t release/Constella-macos-arm64.jar`: no errors detected.
- Release artifact size: 9,283,503 bytes.
- Release SHA-256: `3dd896eb574c2b474fc394c0533b97a98a3ba1ad294df3ebb2e28178c31e1674`.
- Direct launch using the system Java 21 failed as expected because the project targets Java 25 (`UnsupportedClassVersionError`, class version 69 versus supported 65).
- Direct launch using Gradle-provisioned Temurin JDK 25.0.4.1 and a disposable fresh journal succeeded; the existing JavaFX unnamed-module warning appeared. The process was stopped with Ctrl+C after initialization.

## Follow-up problems, limitations, and outcome

- Existing user journals are deliberately not overwritten, so an already-seeded 16-memory journal will not automatically become the 24-memory demo. A genuinely missing data file receives the richer seed; this avoids destroying personal edits.
- The running release initialized successfully with a fresh 24-memory journal, but macOS Accessibility permission still prevents reliable scripted navigation and pointer testing. Star hover/click, toggles, auto-rotation, zoom buttons, and timeline card selection remain Student Review checks rather than falsely claimed manual checks.
- Windows and Linux were not tested.
- Outcome: the first-run demo now has 24 meaningful memories and three intentional isolates; 3D Space uses extruded stars and adds line visibility, auto-rotate, and explicit zoom controls; Timeline is a genuine axis-based chronological view. Persistence and JSON are unchanged, and My Sky remains intact.

## Follow-up suggested commit message

`feat: add 3D stars, exploration controls, and chronological timeline`

## Follow-up prompt — compact timeline and coloured sphere graph

```text
the timeline component for the items are too long.. please fix this. 


For the 3d space, use balls again, make it such that for different connection use a different colour connection line and colour each ball with their colours
```

The prompt included `/var/folders/7h/q0hhtj5s0qbg3_ms417dyfp40000gn/T/codex-clipboard-e4aff252-6d23-497e-9289-72e37a08c21d.png`. The screenshot showed a single timeline card and its axis stretching for almost the full viewport height.

## Compact-timeline and colour decisions

- Capped timeline rows at 104 px and cards at 84 px in both Java code and CSS, with a two-line summary bound. This prevents either the card or central axis from claiming unused viewport height.
- Restored genuine JavaFX `Sphere` memory nodes and removed the now-unused star mesh factory.
- Assigned each mood a clear, restrained colour and retained depth shading, selection brightening, importance sizing, and `PhongMaterial` lighting.
- Assigned constellations a deterministic six-colour palette after sorting by normalized name and UUID. Each edge uses the colour of its contributing constellation.
- A deduplicated edge contributed by multiple overlapping constellations uses the deterministic channel-average of their palette colours. This preserves overlap without duplicating cylinders or arbitrarily discarding memberships.
- Edge highlighting brightens its own base colour instead of replacing every focused edge with the same white colour.
- No domain, persistence, JSON, graph construction, seed, filter, or camera behavior changed.

## Compact-timeline and colour files changed

- `src/main/java/constella/ui/TimelineView.java`
- `src/main/java/constella/ui/Space3DView.java`
- `src/main/java/constella/ui/ConstellaView.java`
- `src/main/resources/constella/ui/constella.css`
- Removed `src/main/java/constella/ui/StarMeshFactory.java`
- `README.md`
- `docs/UserGuide.md`
- `docs/DeveloperGuide.md`
- `logs/011-javafx-3d-space.md`

## Compact-timeline and colour verification

- Baseline `./gradlew test --no-daemon`: BUILD SUCCESSFUL; 88 tests available and up to date.
- Intermediate `./gradlew test --no-daemon`: BUILD SUCCESSFUL in 7s after the renderer and timeline changes.
- Final `./gradlew clean build --no-daemon`: BUILD SUCCESSFUL in 8s; 9 actionable tasks executed.
- Parsed final JUnit XML: 88 tests, 0 failures, 0 errors, 0 skipped.
- `./gradlew releaseJar --no-daemon`: BUILD SUCCESSFUL in 5s.
- `unzip -t release/Constella-macos-arm64.jar`: no errors detected.
- Release artifact size: 9,283,303 bytes.
- Release SHA-256: `33222862bc47a3d97573b5b00521f3855144759762b9a5e52652b98fdf57e4ba`.
- Direct release launch with Gradle-provisioned Temurin JDK 25.0.4.1 and disposable fresh data initialized successfully. The existing unnamed-module JavaFX warning appeared; the process was then stopped with Ctrl+C.

## Compact-timeline and colour limitations/outcome

- macOS Accessibility still prevents reliable scripted navigation to the views, so the precise rendered card proportions and live colour differentiation remain Student Review checks.
- Windows and Linux were not tested.
- Outcome: timeline cards remain compact regardless of result count; 3D memories are coloured spheres again; different constellations produce different line colours while overlapping edges remain deduplicated and visibly blended.

## Suggested commit message

`fix: compact timeline cards and colour 3D graph relationships`

## Follow-up prompt — working constellation focus and All option

```text
also for the 3d space when i click the different focus, it does not change, i also need a all option
```

The prompt included `/var/folders/7h/q0hhtj5s0qbg3_ms417dyfp40000gn/T/codex-clipboard-b7cd574a-daa9-4c42-a509-bbc29515da6d.png`. It showed “Stockholm Exchange” selected while a labelled memory selection was still visibly active, supporting the stale-selection precedence diagnosis.

## Focus fix decisions and changes

- Root cause: `GraphSelectionState.focusId()` correctly gives hover/selected memory focus precedence, but changing the constellation dropdown did not clear that state. `focusConstellation` therefore reapplied the old memory neighborhood instead of the selected constellation highlight.
- Changing constellation focus now clears hover and selected-memory state, clears the details panel, and calls constellation highlighting with no memory focus.
- Added a real `All memories` dropdown entry using a transient `SpaceFocusOption` UI record. No synthetic `Constellation` is created and persistence/domain data remain unchanged.
- `Clear focus` now selects `All memories`, making both controls consistent.
- Updated the User Guide and Developer Guide with the exact focus behavior.

## Focus fix files changed

- `src/main/java/constella/ui/ConstellaView.java`
- `src/main/java/constella/ui/Space3DView.java`
- `docs/UserGuide.md`
- `docs/DeveloperGuide.md`
- `logs/011-javafx-3d-space.md`

## Focus fix commands and results

- Baseline `./gradlew test --no-daemon`: BUILD SUCCESSFUL; 88 tests available and up to date.
- Intermediate `./gradlew test --no-daemon`: BUILD SUCCESSFUL in 12s.
- Final `./gradlew clean build --no-daemon`: BUILD SUCCESSFUL in 16s; all 9 actionable tasks executed.
- Parsed final JUnit XML: 88 tests, 0 failures, 0 errors, 0 skipped.
- `./gradlew releaseJar --no-daemon`: BUILD SUCCESSFUL; archive validation reported no errors.
- Release artifact size: 9,285,790 bytes.
- Release SHA-256: `a8a3f00a38665d249c26d55d271b177e046b7bbc4538b43fbc8570c870cce40e`.
- Direct release launch with Gradle-provisioned Temurin JDK 25.0.4.1 and a disposable journal initialized successfully; the existing JavaFX unnamed-module warning appeared, then the process was stopped with Ctrl+C.

## Focus fix limitations and outcome

- macOS Accessibility still prevents reliably scripting dropdown interaction, so visual confirmation of each focus transition remains a Student Review item.
- Windows and Linux were not tested.
- Outcome: each constellation selection can now visibly take control of highlighting, `All memories` restores the full graph, and Clear focus has the same behavior.

## Suggested commit message

`fix: clear stale memory selection when focusing 3D constellations`

## Follow-up prompt — researched galaxy and constellation motion

```text
please research on java fx 3d motion and animatoin, make it such that there are nice constellation kind of animation for the 3d space, the point of the 3d space is such that it looks like space when there are alot of memory in, seeing "galaxies", being able to select different related graph to see like a constellation
```

## Motion research and resulting design

- Researched the official JavaFX 25 `AnimationTimer`, `Timeline`, animation package, `PerspectiveCamera`, and `SubScene` documentation. The official APIs establish that `AnimationTimer` executes once per active frame on the JavaFX Application Thread; `Timeline` interpolates writable properties through keyframes; indefinite animations must be explicitly stopped to avoid retaining animated objects; a depth-buffered anti-aliased `SubScene` and perspective camera are appropriate for separated 3D content with a readable 2D overlay.
- Sources consulted:
  - https://openjfx.io/javadoc/25/javafx.graphics/javafx/animation/AnimationTimer.html
  - https://openjfx.io/javadoc/25/javafx.graphics/javafx/animation/Timeline.html
  - https://openjfx.io/javadoc/25/javafx.graphics/javafx/animation/package-summary.html
  - https://openjfx.io/javadoc/25/javafx.graphics/javafx/animation/Animation.html
  - https://docs.oracle.com/en/java/java-components/javafx/25/docs/javafx.graphics/javafx/scene/PerspectiveCamera.html
  - https://docs.oracle.com/javafx/8/3d_graphics/subscene.htm
- Retained the settled deterministic force layout rather than running a permanent simulation. The “galaxy” appearance comes from depth, clustering, background stars, independent twinkle phases, and light movement along real graph relationships.
- Added 96 deterministic, low-division 3D background stars at varied X/Y/Z depths. They are created once and only opacity is animated.
- Added subtle deterministic breathing to memory spheres. Selected memories and selected-constellation members receive slightly stronger bounded pulses without changing their settled coordinates.
- Added one small colour-matched traveller to each real deduplicated graph edge. Travellers interpolate along the connection; constellation focus makes only that constellation's real trails visible.
- Added a Motion checkbox, enabled by default. It pauses/resumes star twinkle, memory pulses, and connection travellers. Navigating away from 3D Space explicitly stops the motion timer; returning restarts it only when the saved UI preference remains enabled.
- Used one approximately 30 Hz `AnimationTimer` for ambient/trail motion and retained the existing finite rebuild-settle `Timeline`. Geometry is not rebuilt per frame, the force solver is never advanced by animation, and no persistence writes occur.
- Extracted `SpaceMotion` as UI-independent deterministic mathematics for UUID phases, bounded pulses, edge progress, and vector interpolation.

## Motion files changed

- Added `src/main/java/constella/application/SpaceMotion.java`
- Added `src/test/java/constella/application/SpaceMotionTest.java`
- Updated `src/main/java/constella/ui/Space3DView.java`
- Updated `src/main/java/constella/ui/ConstellaView.java`
- Updated `README.md`
- Updated `docs/UserGuide.md`
- Updated `docs/DeveloperGuide.md`
- Updated `logs/011-javafx-3d-space.md`

## Motion commands and actual results

- Baseline `./gradlew test --no-daemon`: BUILD SUCCESSFUL in 10s; 88 tests available and up to date.
- Official-documentation web searches for JavaFX 25 AnimationTimer, Timeline, animation lifecycle, PerspectiveCamera, and SubScene returned the primary sources listed above.
- Intermediate `./gradlew test --no-daemon`: BUILD SUCCESSFUL in 6s after implementing motion.
- Final `./gradlew clean build --no-daemon`: BUILD SUCCESSFUL in 7s; all 9 actionable tasks executed.
- Parsed final JUnit XML: 90 tests, 0 failures, 0 errors, 0 skipped.
- Coupling scan with `rg`: no JavaFX, Gson, or persistence imports in `SpaceMotion`.
- `./gradlew releaseJar --no-daemon`: BUILD SUCCESSFUL in 3s.
- `unzip -t release/Constella-macos-arm64.jar`: no errors detected.
- Release artifact size: 9,291,013 bytes.
- Release SHA-256: `051afa89f4426a54e688fffc1273ca8f2de72ac4a4504a870f98e4f25f0bb1e4`.
- Direct release launch with Gradle-provisioned Temurin JDK 25.0.4.1 and a disposable fresh journal initialized successfully. The existing JavaFX unnamed-module warning appeared; the process was stopped with Ctrl+C after initialization.

## Motion problems, limitations, and outcome

- macOS Accessibility still prevents reliable scripted navigation and interaction, so live visual judgment of twinkle restraint, traveller speed, Motion toggle, every constellation focus, and camera/motion combinations remains a Student Review item.
- The background is deliberately a restrained starfield rather than a GPU particle engine. JavaFX has no native high-volume 3D particle system, and adding an external engine would violate the project's architecture and scope.
- Windows and Linux were not tested.
- Outcome: 3D Space now reads as a deeper memory galaxy, with settled related clusters, ambient stars, and animated constellation paths while keeping every animated relationship grounded in real journal data.

## Suggested commit message

`feat: add deterministic galaxy motion to 3D Space`

## Follow-up prompt — strict focused-edge isolation

```text
when pressing through all memories, and my other memories, it does not seem like anything changed. When im in my individual memory, i should not be able to see the other lines that are connected, only my specific memory line connected. 
```

The prompt included `/var/folders/7h/q0hhtj5s0qbg3_ms417dyfp40000gn/T/codex-clipboard-f7396d41-03df-4f96-9829-aa09181c7788.png`. It showed “My NUS Journey” selected while faint lines from unrelated graph relationships remained visible, making the focus change difficult to perceive.

## Edge-isolation diagnosis and decisions

- The dropdown and selection state were changing, but `applyHighlight` kept inactive cylinders at opacity `0.08`. On a black background those lines remained clearly visible, so constellation focus looked too similar to All memories.
- Replaced translucent inactive edges with strict visibility. All memories shows every current edge; a selected constellation shows only edges whose retained contributing IDs include that constellation; a selected or hovered memory shows only edges directly touching it.
- Memory focus deliberately takes precedence over constellation focus, so inspecting one memory never leaks other lines from its currently selected constellation.
- Travelling motion lights use exactly the same visibility predicate as cylinders. Toggling Connections back on also reapplies the active focus instead of revealing every edge.
- Unrelated memory spheres remain at 7% opacity as spatial context; only unrelated relationship lines and travellers are completely hidden.
- Extracted `GraphFocusVisibility` as a JavaFX-independent single authority and added tests for All, exact constellation focus, overlaps, direct-memory focus, and precedence.

## Edge-isolation files changed

- Added `src/main/java/constella/application/GraphFocusVisibility.java`
- Added `src/test/java/constella/application/GraphFocusVisibilityTest.java`
- Updated `src/main/java/constella/ui/Space3DView.java`
- Updated `docs/UserGuide.md`
- Updated `docs/DeveloperGuide.md`
- Updated `logs/011-javafx-3d-space.md`

## Edge-isolation commands and actual results

- Baseline `./gradlew test --no-daemon`: BUILD SUCCESSFUL in 5s; 90 tests available and up to date.
- Intermediate `./gradlew test --no-daemon`: BUILD SUCCESSFUL in 6s after implementation.
- Final `./gradlew clean build --no-daemon`: BUILD SUCCESSFUL in 7s; all 9 actionable tasks executed.
- Parsed final JUnit XML: 92 tests, 0 failures, 0 errors, 0 skipped.
- Coupling scan with `rg`: no JavaFX, Gson, or persistence references in `GraphFocusVisibility`.
- `./gradlew releaseJar --no-daemon`: BUILD SUCCESSFUL in 5s.
- `unzip -t release/Constella-macos-arm64.jar`: no errors detected.
- Release artifact size: 9,291,680 bytes.
- Release SHA-256: `891c7a0d5c7c377522da632509662ccb34835ff0553b9bf2912a52fb7f2148cb`.
- Direct release launch with Gradle-provisioned Temurin JDK 25.0.4.1 and a disposable journal initialized successfully; the existing JavaFX unnamed-module warning appeared, then the process was stopped with Ctrl+C.

## Edge-isolation limitations and outcome

- macOS Accessibility still prevents reliably scripting dropdown and sphere interaction, so live visual confirmation of every transition remains a Student Review item.
- Windows and Linux were not tested.
- Outcome: focus changes are now visually unambiguous because unrelated lines do not render; All restores every line, constellation focus isolates that relationship graph, and memory focus isolates only direct connections.

## Suggested commit message

`fix: strictly isolate focused 3D graph edges`

## Follow-up prompt — make 3D My Sky the default

```text
keep auto rotate on as default, Then remove my sky in terms of just putting the 3d space to replace the current "my sky" tab instead. 
```

## Default-3D decisions and changes

- Interpreted the request as retaining the **My Sky** navigation name while replacing its former 2D renderer with the completed 3D galaxy. Removed the duplicate **3D Space** navigation item.
- `ConstellaView` now constructs only `Space3DView`, starts in the 3D route, labels the content **My Sky**, and routes the My Sky sidebar button and post-clear return to that view.
- Removed the `SkyView` field, its construction callback, `showSky`, and the `SKY` view state from the application shell. The legacy source file remains untouched but is no longer instantiated or reachable through navigation.
- Auto rotate preference now defaults to `true`. Its checkbox therefore starts selected and rotation starts when My Sky opens. Navigation still stops runtime rotation while the 3D scene is detached; returning restarts it when the selected preference remains enabled.
- Updated 3D failure guidance to direct users to Timeline rather than referring to a removed 2D alternative.
- Preserved Timeline, Constellations, filters, CRUD, Clear Journal, motion, connection focus, camera controls, and persistence.

## Default-3D files changed

- `src/main/java/constella/ui/ConstellaView.java`
- `src/main/java/constella/ui/Space3DView.java`
- `README.md`
- `docs/UserGuide.md`
- `docs/DeveloperGuide.md`
- `logs/011-javafx-3d-space.md`

## Default-3D commands and actual results

- Baseline `./gradlew test --no-daemon`: BUILD SUCCESSFUL in 4s; 92 tests available and up to date.
- Post-change `./gradlew test --no-daemon`: BUILD SUCCESSFUL in 5s. The combined command returned status 1 only because the following `rg` correctly found no stale `showSky`, `View.SKY`, private `SkyView`, or `3D Space` route references.
- Documentation and source scans with `rg` removed obsolete 2D controls, checklist steps, duplicate route language, and stale startup claims.
- Final `./gradlew clean build --no-daemon`: BUILD SUCCESSFUL in 7s; all 9 actionable tasks executed.
- Parsed final JUnit XML: 92 tests, 0 failures, 0 errors, 0 skipped.
- `./gradlew releaseJar --no-daemon`: BUILD SUCCESSFUL in 4s.
- `unzip -t release/Constella-macos-arm64.jar`: no errors detected.
- Release artifact size: 9,290,618 bytes.
- Release SHA-256: `032de87f119ce9c4f3f8b7e0c42aa202ad5c73297f948e5714cbd567b825dbd5`.
- Direct release launch with Gradle-provisioned Temurin JDK 25.0.4.1 and a disposable fresh journal initialized successfully; the existing JavaFX unnamed-module warning appeared, then the process was stopped with Ctrl+C.

## Default-3D limitations and outcome

- The former `SkyView` implementation and its UI-independent helpers/tests remain in source for now, but no application route constructs or displays it. Removing all legacy classes and persisted star-position compatibility would be a separate cleanup with broader migration implications.
- macOS Accessibility still prevents scripted visual confirmation of the initially checked Auto rotate control and startup rotation; source state and initialization paths were verified, and this remains a Student Review item.
- Windows and Linux were not tested.
- Outcome: Constella now opens directly into the animated 3D My Sky, exposes no duplicate graph tab, and auto-rotates by default.

## Suggested commit message

`feat: make animated 3D My Sky the default graph`

## Follow-up prompt — auto-rotation stops after focus

```text
Right now auto rotate breaks when i click into the individual focus, then when i toggle the auto rotate it does not work anymore.
```

## Auto-rotation diagnosis and fix

- Found the definitive state bug in `CameraState`: yaw was clamped to `[-180, 180]`. Default auto-rotation reached +180° after roughly twenty seconds and then every positive rotation update remained clamped. Toggling stopped and restarted the timer correctly but could not move the already saturated camera state.
- Changed yaw to normalize across the ±180° boundary rather than clamp. Pitch, zoom, and pan remain bounded. Added a regression test that crosses the positive boundary, continues rotating, then crosses in the opposite direction.
- Found a focus-related performance amplifier: every auto-rotation frame called `Platform.runLater(updateLabels)`. When a focused memory introduced a projected label, these deferred scene-coordinate calculations could accumulate and make the UI appear stalled.
- Auto-rotation now updates camera transforms directly on its existing JavaFX animation callback and projects focused labels at approximately 15 Hz. Normal mouse, reset, zoom, and focus operations update labels synchronously. No per-frame deferred queue remains.
- Constellation focus, strict edge isolation, default auto-rotate preference, and camera controls otherwise remain unchanged.

## Auto-rotation files changed

- `src/main/java/constella/application/CameraState.java`
- `src/test/java/constella/application/CameraStateTest.java`
- `src/main/java/constella/ui/Space3DView.java`
- `docs/UserGuide.md`
- `docs/DeveloperGuide.md`
- `logs/011-javafx-3d-space.md`

## Auto-rotation commands and actual results

- Baseline `./gradlew test --no-daemon`: BUILD SUCCESSFUL in 5s; 92 tests available and up to date.
- Intermediate `./gradlew test --no-daemon`: BUILD SUCCESSFUL in 10s after the yaw and label-projection fixes.
- Final `./gradlew clean build --no-daemon`: BUILD SUCCESSFUL in 7s; all 9 actionable tasks executed.
- Parsed final JUnit XML: 93 tests, 0 failures, 0 errors, 0 skipped.
- `./gradlew releaseJar --no-daemon`: BUILD SUCCESSFUL in 4s.
- `unzip -t release/Constella-macos-arm64.jar`: no errors detected.
- Release artifact size: 9,290,797 bytes.
- Release SHA-256: `c301c0a085dc1a63beb3c46297b31780649d2502a0947096375a5ab8d2d1380a`.
- Direct release launch with Gradle-provisioned Temurin JDK 25.0.4.1 and a disposable journal initialized successfully; the existing JavaFX unnamed-module warning appeared, then the process was stopped with Ctrl+C.

## Auto-rotation limitations and outcome

- macOS Accessibility still prevents scripting the exact dropdown/toggle sequence, so final live confirmation remains a Student Review item. The underlying boundary failure and focus-label queue are both removed and covered proportionally by automated state tests and source inspection.
- Windows and Linux were not tested.
- Outcome: auto-rotation can continue indefinitely across yaw boundaries, remains active through constellation or memory focus, and can be stopped and restarted after any duration.

## Suggested commit message

`fix: keep 3D auto-rotation running through focus changes`

## Follow-up prompt — readable memory editor and searchable constellations

```text
fix my new memory screen, i should be able to see the words on the left in full, as well as for the constellation, being able to see a list of 7 instead as well as a search on top of the list so its easier to find the constellation if needed. 
```

The prompt included `/var/folders/7h/q0hhtj5s0qbg3_ms417dyfp40000gn/T/codex-clipboard-352d199b-b7f7-4273-868a-01fef2c4df96.png`. It showed Description, Mood, Importance, People, Location, and Constellations ellipsized in the left column, and a short two-row constellation list without search.

## Memory-editor decisions and changes

- Increased the dialog preferred width from 560 to 700 px and added explicit grid column constraints: the label column is fixed at 130 px and cannot shrink, while the form-control column receives remaining width. Each label also uses its preferred minimum width.
- Reduced Description from three preferred rows to two to offset the taller constellation picker and keep the dialog practical on smaller displays.
- Replaced modifier-key multiple selection with a checkbox list. Each available constellation has a UUID-keyed `BooleanProperty`, so membership state is independent of which filtered rows are currently displayed.
- Set constellation rows to 32 px and the list viewport to exactly seven rows (226 px including its border).
- Added a `Search constellations` field directly above the list with accessible text and live case-insensitive substring filtering.
- Added UI-independent `ConstellationSearch` matching with null/blank-query and case-insensitive tests.
- Editing an existing memory initializes checkbox properties from its memberships. Creating/saving collects every checked property, including selections temporarily hidden by search.

## Memory-editor files changed

- Added `src/main/java/constella/application/ConstellationSearch.java`
- Added `src/test/java/constella/application/ConstellationSearchTest.java`
- Updated `src/main/java/constella/ui/MemoryEditorDialog.java`
- Updated `docs/UserGuide.md`
- Updated `docs/DeveloperGuide.md`
- Updated `logs/011-javafx-3d-space.md`

## Memory-editor commands and actual results

- Baseline `./gradlew test --no-daemon`: BUILD SUCCESSFUL in 7s; 93 tests available and up to date.
- Intermediate `./gradlew test --no-daemon`: BUILD SUCCESSFUL in 5s after editor/search implementation.
- Final `./gradlew clean build --no-daemon`: BUILD SUCCESSFUL in 6s; all 9 actionable tasks executed.
- Parsed final JUnit XML: 95 tests, 0 failures, 0 errors, 0 skipped.
- `./gradlew releaseJar --no-daemon`: BUILD SUCCESSFUL in 3s.
- `unzip -t release/Constella-macos-arm64.jar`: no errors detected.
- Release artifact size: 9,293,029 bytes.
- Release SHA-256: `8f45d213869efbe5fb0c96118ac190d4717c3a7ecbb6d25dd0d7fb2f875e10c2`.
- Direct release launch with Gradle-provisioned Temurin JDK 25.0.4.1 and a disposable journal initialized successfully; the existing JavaFX unnamed-module warning appeared, then the process was stopped with Ctrl+C.

## Memory-editor limitations and outcome

- macOS Accessibility still prevents scripted activation and visual measurement of the modal dialog, so final confirmation of seven visible rows and labels at the smallest display remains a Student Review item.
- Windows and Linux were not tested.
- Outcome: the New/Edit Memory form provides a stable readable label column and a seven-row searchable checkbox list whose selections survive filtering.

## Suggested commit message

`fix: improve memory editor labels and constellation search`

## Follow-up prompt — invisible constellation names

```text
i am not able to see the constellations to select, please fix it
```

The prompt included `/var/folders/7h/q0hhtj5s0qbg3_ms417dyfp40000gn/T/codex-clipboard-43208eb2-8053-4788-8003-fe4240cc9b9a.png`. It showed checkboxes in all five populated rows but no visible constellation names.

## Visible-name diagnosis and fix

- The JavaFX `CheckBoxListCell` convenience skin rendered the checkbox graphic but did not reliably render its converter-derived text under the project's dark stylesheet. Global list-cell padding also exceeded the fixed 32 px row height and could clip content.
- Replaced `CheckBoxListCell` with an explicit reusable `ConstellationCheckCell` containing a `CheckBox` and normal styled `Label` in an `HBox`.
- Each cell unbinds its former bidirectional property before reuse, copies the new UUID-backed property's state, binds to it, sets the full constellation name, and provides checkbox accessible text.
- Added a dedicated cell style with compact 4 px vertical padding and an explicit light text colour. This avoids skin-dependent inherited text colours and fits within the seven-row fixed-height viewport.
- Search, hidden-selection preservation, existing-memory initialization, and result collection remain unchanged.

## Visible-name files changed

- `src/main/java/constella/ui/MemoryEditorDialog.java`
- `src/main/resources/constella/ui/constella.css`
- `docs/DeveloperGuide.md`
- `logs/011-javafx-3d-space.md`

## Visible-name commands and actual results

- Baseline `./gradlew test --no-daemon`: BUILD SUCCESSFUL in 5s; 95 tests available and up to date.
- Intermediate `./gradlew test --no-daemon`: BUILD SUCCESSFUL in 5s after the custom-cell fix.
- Final `./gradlew clean build --no-daemon`: BUILD SUCCESSFUL in 6s; all 9 actionable tasks executed.
- Parsed final JUnit XML: 95 tests, 0 failures, 0 errors, 0 skipped.
- `./gradlew releaseJar --no-daemon`: BUILD SUCCESSFUL in 3s.
- `unzip -t release/Constella-macos-arm64.jar`: no errors detected.
- Release artifact size: 9,293,916 bytes.
- Release SHA-256: `8d489e9ecdfa396e57ec3374f70c97935987791cff73186c0faba81af303061a`.
- Direct release launch with Gradle-provisioned Temurin JDK 25.0.4.1 and a disposable journal initialized successfully; the existing JavaFX unnamed-module warning appeared, then the process was stopped with Ctrl+C.

## Visible-name limitations and outcome

- macOS Accessibility still prevents scripted opening and screenshot confirmation of the modal, so final visual confirmation remains a Student Review item. The cell now uses the same explicit `Label` styling already visible elsewhere in the application rather than relying on the failing skin text.
- Windows and Linux were not tested.
- Outcome: every populated constellation row now has an explicit visible name beside its checkbox while retaining the seven-row searchable picker.

## Suggested commit message

`fix: render constellation names in memory editor`

## Follow-up prompt — whole-row constellation toggle

```text
make sure that when i press the row for constellation when adding a new memory, the checkbox will still toggle rather than only pressing the checkbox. 
```

## Whole-row toggle decisions and changes

- Made the custom constellation list cell focusable and interactive across its full row, including the name and empty cell space.
- A primary click outside the checkbox toggles the UUID-backed membership property directly.
- A click on the checkbox or any of its skin descendants is detected through parent-node ancestry and is left to the checkbox control, preventing the event from toggling twice.
- Space and Enter toggle the focused row and are consumed to avoid conflicting list selection behavior.
- Added full-row accessibility text. The existing checkbox retains a specific “Include [name]” accessible label.

## Whole-row toggle files changed

- `src/main/java/constella/ui/MemoryEditorDialog.java`
- `docs/UserGuide.md`
- `docs/DeveloperGuide.md`
- `logs/011-javafx-3d-space.md`

## Whole-row toggle commands and actual results

- Baseline `./gradlew test --no-daemon`: BUILD SUCCESSFUL in 5s; 95 tests available and up to date.
- Intermediate `./gradlew test --no-daemon`: BUILD SUCCESSFUL in 5s after the row interaction change.
- Final `./gradlew clean build --no-daemon`: BUILD SUCCESSFUL in 8s; all 9 actionable tasks executed.
- Parsed final JUnit XML: 95 tests, 0 failures, 0 errors, 0 skipped.
- `./gradlew releaseJar --no-daemon`: BUILD SUCCESSFUL in 3s.
- `unzip -t release/Constella-macos-arm64.jar`: no errors detected.
- Release artifact size: 9,294,966 bytes.
- Release SHA-256: `31b2f3bcc49ba668a741e206f63e826367f8c0724c3bae9ee721bff8739621c6`.
- Direct release launch with Gradle-provisioned Temurin JDK 25.0.4.1 and a disposable journal initialized successfully; the existing JavaFX unnamed-module warning appeared, then the process was stopped with Ctrl+C.

## Whole-row toggle limitations and outcome

- macOS Accessibility still prevents scripted clicks inside the modal dialog, so direct visual confirmation remains a Student Review item. The handler explicitly distinguishes checkbox-origin events from every other row click.
- Windows and Linux were not tested.
- Outcome: users can now toggle a constellation by clicking anywhere across its row or by keyboard, while direct checkbox clicks continue to toggle exactly once.

## Suggested commit message

`fix: toggle constellation membership from the whole editor row`

## Student review

- [x] I confirmed that the original prompt is accurate.
- [x] I confirmed that the changed-file list is accurate.
- [x] I confirmed that recorded commands were actually executed.
- [x] I confirmed that build and test results are accurate.
- [x] I added any mistakes or disagreements omitted by the AI.

Reviewed by: A0273503L Lum Yi Ren Johannsen
Review date: 31 August 2026
