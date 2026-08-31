# Constella User Guide

## Current functionality

Constella opens a dark desktop journal where you can create, inspect, edit, and delete memories. Memories are saved locally between launches and appear as stars in **My Sky**.

## First-launch demo journal

When no journal file exists, Constella creates a fictional four-year NUS journal with 24 memories spanning modules, campus life, exchange, internships, trips, holidays, volunteering, and personal milestones. Five constellations demonstrate overlapping groups and connection lines; three memories intentionally remain outside every constellation. This happens only once: Constella never replaces an existing journal, including an intentionally empty one.

Choose **Clear Journal** at the bottom of the sidebar when you are ready to enter your own memories. Confirming permanently removes every demo or personal memory and constellation and saves an empty journal. The demo does not return after restart.

## Memories

Choose **New Memory**, enter a title and date, select a mood and importance from 1 to 5, then optionally add a description, comma-separated tags, comma-separated people, location, and constellation assignments. The constellation picker shows seven rows with checkboxes. Click anywhere across a constellation row—not only its checkbox—to toggle membership, or focus the row and press Space/Enter. Use **Search constellations** above it for case-insensitive name matching; checked memberships remain selected even when filtering hides them. Choose **Create** to save. Blank titles, missing dates or moods, and blank comma-separated entries are rejected in the dialog.

Select a memory in **My Sky** to see its details. Use **Edit** to update it or **Delete** and confirm to remove it. Deleting also removes its constellation memberships.

## My Sky

Constella opens directly in the genuine JavaFX 3D **My Sky** memory graph. There is no separate 2D sky or duplicate 3D Space navigation item. **My Sky contains one node per visible memory. Edges are the sparse chronological memory connections contributed by constellations.** It never creates nodes for constellations, tags, people, locations, moods, or other metadata. Every line joins two real visible memories and exactly matches an edge emitted by the application graph builder.

The graph calculates a deterministic, bounded force-directed layout once after content or filters change, then briefly settles into place. Existing edges attract related memories into organic clusters; memory repulsion, collision separation, weak centring/component forces, and genuine Z depth keep disconnected memories visible without running a permanent simulation. The complete seeded demo displays exactly 24 coloured sphere nodes, including three without constellation connections. A sphere's colour represents its mood. Connection colours identify their contributing constellations; a line shared by multiple constellations uses a consistent blended colour.

The surrounding deep starfield twinkles gently, memory spheres pulse at different deterministic phases, and small coloured lights travel along real connection paths. Choosing a constellation restricts these light trails to that related graph, making the selected cluster read like a constellation within the larger memory galaxy. This is presentation-only motion: memory positions remain settled and no animation is saved.

Use **Connections** to show or hide every line without changing journal data. Use **Motion** to pause or resume twinkling, memory pulses, and travelling connection lights. **Auto rotate starts enabled**, continues through constellation or memory focus, and can be stopped and restarted with its checkbox. Scroll or use **− / +** to zoom within safe limits. Drag empty space to rotate, Shift-drag or secondary-drag to pan, **Reset Camera** to restore the default view, and **Focus Selected** to move toward the selected memory. Motion and rotation stop while you visit another screen and resume according to their selected controls when you return to My Sky.

The focus dropdown contains **All memories** followed by every constellation. Choosing a constellation clears any previous memory selection, shows only lines contributed by that constellation, and leaves unrelated memory balls as very faint spatial context. Selecting one memory temporarily shows only lines directly attached to that memory. Choose **All memories** or **Clear focus** to restore every connection.

- Primary-drag empty space to rotate.
- Shift-primary-drag or secondary-drag empty space to pan.
- Scroll over the scene to zoom within safe limits.
- Choose **Reset Camera** to recover the initial view.
- Select a sphere, then choose **Focus Selected** to move the camera toward it.
- Hover a memory to show its screen-facing title/date label and emphasize only its real graph neighbors and attached edges.
- Click a memory to keep it selected and open the existing memory-details panel. Focus it and press Space or Enter for the same action.
- Use the constellation control to isolate its contributed edges; choose **All memories** or **Clear focus** to restore every edge.

Search and the mood, tag, constellation, and year filters use the same AND semantics in My Sky and Timeline. If JavaFX 3D is unavailable, My Sky shows useful guidance and Timeline remains available.

## Search and filters

The controls above My Sky and Timeline share the current search and filter state. Search is case-insensitive across title, description, tags, people, and location. Mood, tag, constellation, and year filters are exact selections. Every populated category combines using **AND**, so a memory must satisfy all of them. Choose **Reset filters** to show everything.

## Timeline

Open **Timeline** to see filtered memories newest first on a central chronological axis. Compact memory cards alternate left and right under visible year markers; each card shows its date, title, and a short summary. Select a card to open its details. An empty journal and a filter with no matches display different guidance.

## Constellations

Open **Constellations** and choose **New Constellation** to enter a unique name and optional description. Select a constellation to assign or remove any number of memories. **Rename** changes its name. **Delete** asks for confirmation and removes only the grouping; its memories remain.

## Data storage and errors

Constella stores UTF-8 JSON in the operating system's per-user application-data directory. The storage layer treats a missing file as an empty journal; normal application startup then seeds the 24-memory demo once and saves it. After **Clear Journal**, the empty file exists, so startup preserves that intentional empty journal rather than reseeding. If data is malformed, Constella reports the path, leaves the file untouched, and opens an empty working view; back up or repair the file before saving.

## Running Constella

Install JDK 25, then open a terminal in the repository root.

- macOS/Linux: run `./gradlew run`.
- Windows: run `gradlew.bat run`.

The application requires a graphical desktop environment.

The verified bundled release is `release/Constella-macos-arm64.jar`. With JDK 25 on an Apple Silicon Mac, run:

```sh
java --enable-native-access=ALL-UNNAMED -jar release/Constella-macos-arm64.jar
```

Windows and Linux users must build and use the matching platform-labelled artifact because JavaFX native libraries are platform-specific.

## Current limitations

- The settled My Sky layout is recalculated rather than manually arranged; there is no manual node-position editing or undo.
- Windows and Linux execution still require verification.

## Manual test checklist

Use a disposable data path for testing where possible, for example set `CONSTELLA_DATA_FILE` to a temporary `journal.json` path before launch.

- [ ] On a genuinely missing data file, confirm the 24-memory NUS demo appears; clear it and confirm it stays empty after restart.
- [ ] After clearing the demo, confirm the empty My Sky guidance.
- [ ] Create three memories using different moods and importance values; verify star colour, size, tooltip, and details.
- [ ] Open New Memory at the minimum supported window size and confirm every field label is fully visible.
- [ ] Search the seven-row constellation checklist, select results across different searches, and confirm all checked memberships save.
- [ ] Try a blank title and a trailing blank tag/person entry; verify readable validation without closing the editor.
- [ ] Edit one memory and verify its UUID-backed star remains present with updated details.
- [ ] Create and rename a constellation; assign multiple memories and confirm lines appear in My Sky.
- [ ] Hover and select overlapping-constellation spheres; confirm only direct edges remain and unrelated spheres dim.
- [ ] Zoom, rotate, and pan empty space; use Reset Camera and confirm filters and detail selection still work.
- [ ] Remove one membership and confirm the connection path updates.
- [ ] Search by title, description, tag, person, and location with mixed letter case.
- [ ] Combine mood, tag, constellation, and year filters; confirm only memories satisfying every filter remain.
- [ ] Reset filters and confirm all memories return.
- [ ] Open Timeline, confirm newest-first summaries, select an entry, and inspect its details.
- [ ] Close and reopen; confirm memories and memberships persist.
- [ ] Delete a memory after confirmation; confirm it disappears and memberships remain valid.
- [ ] Delete a constellation after confirmation; confirm its memories remain.
- [ ] Put malformed JSON at a disposable data path; confirm a useful load error and unchanged file.
- [ ] Navigate controls using Tab/Shift+Tab, activate stars and timeline entries with the keyboard, and use Command/Ctrl+N for New Memory.
- [ ] Resize to the minimum window and confirm controls remain usable, with scrolling where required.
- [ ] Load approximately 100 in-memory spheres and check that zoom, rotation, pan, hover, focus, and filtering remain responsive.
- [ ] Launch Constella and confirm My Sky immediately displays exactly 24 small mood-coloured spheres, including three disconnected memories and no metadata nodes or spokes.
- [ ] Confirm Auto rotate starts enabled, can be stopped, and resumes when selected after returning to My Sky.
- [ ] Confirm different constellations use visibly different connection colours and overlapping edges have a stable blended colour.
- [ ] Toggle connection lines, auto-rotation, and both zoom buttons; confirm each can be reversed and the graph remains recoverable.
- [ ] Toggle Motion; confirm the background twinkle, sphere pulses, and travelling edge lights pause and resume, and stop after leaving My Sky.
- [ ] Select each constellation and confirm only that constellation's real lines and travelling lights remain visible; choose All memories and confirm every edge returns.
- [ ] Select or hover one memory and confirm only its directly attached lines remain visible until memory focus clears.
- [ ] Rotate empty space; pan with Shift-drag and secondary-drag; zoom; then verify Reset Camera recovers the scene.
- [ ] Hover and select spheres, activate a focused sphere with Space/Enter, and confirm the overlay and details panel update.
- [ ] Focus each constellation and confirm only its real memories and contributed sparse edges brighten.
- [ ] Use search and combined filters in My Sky, verify its empty state, and navigate to Timeline and back.
- [ ] Select a sphere and use Focus Selected; confirm sphere manipulation does not accidentally rotate the camera.
