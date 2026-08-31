# Reflections on AI-assisted Software Engineering

This document separates factual evidence from the student's personal conclusions. Complete every **Student reflection** section in your own words before submission.

## Candidate 1 — Project scaffolding and Gradle/JUnit compatibility

### Factual evidence

The first scaffold prompt explicitly required truthful verification and a UI-independent test. The initial Gradle 9 test run failed because the JUnit Platform launcher was absent. The dependency `testRuntimeOnly 'org.junit.platform:junit-platform-launcher'` was added, after which three tests and the clean build passed. See `logs/001-project-scaffolding.md`.

### Student reflection — complete personally

- Why did the original prompt emphasize actual command results?
- Why was the prompt formulated with a UI-independent test and truthful verification requirement?
- Which assumptions did the AI make about the Gradle/JUnit setup?
- What did the failure reveal about generated build configurations?
- What did the AI get wrong or omit before the launcher dependency was added?
- Was the diagnosis efficient, and how did you independently verify it?
- How did the prompting evolve after the first failed command?
- What engineering judgment remained with you rather than the AI?
- What would you ask differently when scaffolding another project?

## Candidate 2 — Memory normalization and identity

### Factual evidence

The second prompt forced explicit immutability, normalization, and equality decisions. The implementation chose immutable memories, UUID-only identity, lowercase tags, case-insensitive people deduplication that preserves first spelling, and blank optional text as absent. The prompt left duplicate handling ambiguous; the implementation coalesced duplicates and disclosed that choice. See `logs/002-memory-domain-model.md`.

### Student reflection — complete personally

- Which normalization decisions were product decisions rather than purely technical ones?
- Why was the original prompt formulated to require explicit identity, normalization, and immutability decisions?
- Which assumptions did the AI make about duplicate handling and optional text?
- Do you agree with coalescing duplicates? Why?
- What did the AI get wrong, or what would you correct after reviewing the implementation?
- How did tests help expose assumptions hidden in the prompt?
- How did the prompting evolve as those ambiguities became clear?
- What engineering judgment remained with you rather than the AI?
- What would become harder if equality included all fields?

## Candidate 3 — Safe persistence and release honesty

### Factual evidence

The MVP prompt required malformed-data preservation, atomic saving, and an honest cross-platform release. Persistence tests covered missing/malformed files, Unicode, round trips, stale references, and temporary replacement. Release work first produced platform-labelled files, then the submission interpretation was revisited and the build was changed to produce one `Constella.jar` containing native libraries for the three tested OS/architecture targets. See `logs/004-json-persistence.md`, `logs/008-release-preparation.md`, `logs/014-cross-platform-release-artifacts.md`, and `logs/015-single-cross-platform-jar.md`.

### Student reflection — complete personally

- How did the data-safety requirements change the persistence design?
- Why was the original prompt formulated to insist on malformed-data preservation and honest release claims?
- Which assumptions did the AI make about atomic replacement, data safety, or cross-platform packaging?
- How did checking the exact specification change the packaging decision from platform-labelled files to one carefully scoped cross-platform JAR?
- What did the AI get wrong, or what did later 3D/release work require correcting?
- What engineering judgment was still needed despite the detailed master prompt?
- How did the prompting evolve after persistence or graph verification findings?
- What manual UI evidence would still complement the successful Windows/Linux CI builds?

## Overall reflection — complete personally

Discuss where prompting was effective, where manual inspection was essential, mistakes or disagreements found during your review of the logs, and how your workflow would change on a future project.
