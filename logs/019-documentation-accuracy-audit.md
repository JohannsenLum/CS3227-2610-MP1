# 019 — Documentation accuracy audit

Date: 31 August 2026

## Objective

Correct misleading constellation-picker wording and verify the documented count of demo memories outside every constellation.

## Original prompt (verbatim)

> Your guide says, twice:
>
> ▎ *"The constellation picker shows **seven rows** with checkboxes"*
> ▎ *"Search the **seven-row** constellation checklist"*
>
> But MemoryEditorDialog.java:98-101:
>
> constellations.setFixedCellSize(32);
> constellations.setPrefHeight(32 * 7 + 2);   // viewport sized for 7 rows
>
> That's the **viewport height** — the box is sized to display up to seven rows without
> scrolling. It isn't seven constellations. And DemoJournalSeederTest.java:15 asserts:
>
> assertEquals(5, snapshot.constellations().size());
>
> So a peer tester opens New Memory with the demo journal, counts **five** checkbox rows, reads
> "seven rows", and files a bug. That's precisely the "inaccuracies will be considered bugs"
> case — against your **Features 20%**, not just Documentation.
>
> **Suggested fix:** *"The constellation picker is sized to show up to seven rows without
> scrolling; the demo journal provides five."*
>
> **🟡 One claim I couldn't verify**
>
> ▎ *"**three** memories intentionally remain outside every constellation" (and "including **three**
> ▎ *without constellation connections")*
>
> **No test asserts this.** DemoJournalSeederTest checks 24 memories, 5 constellations and the
> year set, but never the unconnected count. My attempt to derive it from the seeder source
> didn't parse cleanly, so I genuinely don't know if it's three. It appears in your own
> manual checklist, so worth counting once in the running app — if it's not exactly three,
> that's a second reported bug.

## Response summary

Corrected the User Guide, its screenshot alternative text, its manual checklist, and the Developer Guide so they distinguish the picker's seven-row viewport capacity from the demo journal's five constellations. Verified that the existing focused seeder test already asserts exactly three disconnected memories.

## Assumptions

- “Rows” should describe visible data only when that many constellations exist; otherwise documentation should describe viewport capacity.
- A memory outside every constellation is equivalent to an unconnected memory in the seeded graph because graph edges are contributed only by constellation membership paths.

## Design decisions

- Retained the three-disconnected-memory claim because it has a direct set-based regression assertion.
- Corrected the Developer Guide as well as the two reported User Guide statements because it contained the same conceptual ambiguity.
- Did not add a duplicate test when an appropriately named assertion already existed.

## Files changed

- `docs/UserGuide.md`
- `docs/DeveloperGuide.md`
- `logs/019-documentation-accuracy-audit.md`

## Commands actually executed

- Repository-wide searches for seven-row and disconnected-memory claims
- Source inspection of `MemoryEditorDialog`, `DemoJournalSeeder`, and `DemoJournalSeederTest`
- Focused seeder test and full Gradle quality gate
- Documentation diff, structure, and consistency checks

## Actual build and test results

- Focused `DemoJournalSeederTest`: BUILD SUCCESSFUL; all four seeder tests passed, including the exact disconnected-memory count.
- `./gradlew clean check releaseJar --no-daemon`: BUILD SUCCESSFUL; 86 tests passed with no failures, errors, or skips, and both Checkstyle tasks passed.
- `release/SHA256SUMS.txt` successfully verified the rebuilt `Constella.jar`.
- The official structure script reported every required item present, with only the known local-folder-name warning.

## Problems or limitations

- The reviewer's statement that no disconnected-count test existed did not match the current checkout; the test may have been added after the reviewed revision.
- The picker screenshot naturally displays five populated rows because the demo contains five constellations, while its viewport has capacity for seven.

## Changes made after verification

No further product or documentation correction was required after the focused and full checks.

## Outcome

The picker documentation now accurately describes capacity rather than a fixed data count, while the three-disconnected-memory statement remains supported by an explicit regression test.

Suggested commit message: `docs: clarify constellation picker capacity`

## Student review

- [ ] I confirmed that the original prompt is accurate.
- [ ] I confirmed that the changed-file list is accurate.
- [ ] I confirmed that recorded commands were actually executed.
- [ ] I confirmed that build and test results are accurate.
- [ ] I added any mistakes or disagreements omitted by the AI.

Reviewed by:
Review date:
