# Development Instructions

## Scope and requirements

- Read `MP1-spec.md` before making requirement-sensitive decisions.
- Preserve the distinction between implemented and planned features.
- Do not turn Constella into a to-do manager.
- Maintain compatibility with Windows, Linux, and macOS.
- Keep domain and application logic independent of JavaFX.
- Avoid unnecessary dependencies and speculative features.
- Work in small, testable increments.
- Do not publish, deploy, push, or modify remote services unless the user explicitly requests it.

## Working process

For each development task:

1. Inspect the existing implementation before editing.
2. State important assumptions.
3. Propose the smallest appropriate change.
4. Implement only the requested increment.
5. Add or update relevant tests.
6. Run relevant tests and builds.
7. Update affected documentation.
8. Create or update the corresponding interaction log.
9. Report actual results and remaining limitations.

Do not claim that a command, build, test, or manual check succeeded unless it was actually performed successfully.

## Code quality

- Use clear names and small, focused classes and methods.
- Validate inputs at appropriate boundaries.
- Avoid duplicated logic and dead code.
- Do not couple domain objects to JavaFX controls.
- Keep persistence behind an abstraction.
- Add comments only when they explain non-obvious reasoning.
- Prefer testable logic over logic embedded inside UI event handlers.
- Preserve existing user changes and avoid unrelated rewrites.

## Testing

- Use JUnit 5.
- Add tests for normal cases, boundary values, and invalid inputs.
- Run the complete relevant test suite after changes.
- Record actual test counts and failures accurately.
- Do not replace meaningful tests with tests that only increase coverage.
- UI behavior that cannot be automated yet must be documented as a manual verification step.

## Documentation

- Keep `README.md`, the User Guide, and Developer Guide consistent with the current implementation.
- Never describe planned functionality as available.
- Add acknowledgements for reused code, assets, ideas, and documentation.
- Treat documentation inaccuracies as defects.

## AI interaction logging

For every meaningful development task:

1. Create or update a Markdown file under `logs/`.
2. Use sequential filenames such as `001-project-scaffolding.md`.
3. Include the user's original prompt verbatim.
4. Include:
   - date,
   - objective,
   - response summary,
   - assumptions,
   - design decisions,
   - files changed,
   - commands actually executed,
   - actual build and test results,
   - problems or limitations,
   - changes made after verification,
   - outcome,
   - and suggested commit message.
5. Group closely related follow-up prompts in the same log.
6. Never invent prompts, commands, results, student opinions, or commit hashes.
7. Do not mark a log as reviewed by the student.

End every interaction log with:

## Student review

- [ ] I confirmed that the original prompt is accurate.
- [ ] I confirmed that the changed-file list is accurate.
- [ ] I confirmed that recorded commands were actually executed.
- [ ] I confirmed that build and test results are accurate.
- [ ] I added any mistakes or disagreements omitted by the AI.

Reviewed by: Johannsen
Review date: 26 August 2026
