# 016 — Final submission audit

Date: 31 August 2026

## Objective

Review the complete MP1 submission, fix objective defects, verify the repository and release, and push the audited state while leaving the student's personal reflection and review decisions untouched.

## Original prompt (verbatim)

> please help me to review everything and push. Ill look into reflection.md now

## Response summary

Audited submission structure, Git and GitHub state, tracked files, build configuration, source/tests, documentation claims, interaction logs, the single cross-platform release JAR, checksums, native libraries, and the three-platform CI result. Corrected `clean` so final verification cannot accidentally reuse a stale checked-in JAR.

## Assumptions

- “Everything” means an objective submission-readiness audit, not writing personal reflection answers or falsely confirming student review checkboxes.
- The user is editing `docs/Reflections.md`, so this audit does not modify that file.
- Manual Windows and Linux GUI interaction remains student/peer work even when CI passes.

## Design decisions

- Kept all Student review checkboxes unchecked because only the student can attest to them.
- Treated the local-folder-name warning as cosmetic because the public GitHub repository has the exact required name.
- Made `clean` delete only the generated `Constella.jar`, not the entire `release/` directory, so the checksum file remains available and every clean release command rebuilds the binary.

## Files changed

- `build.gradle`
- `docs/DeveloperGuide.md`
- `logs/016-final-submission-audit.md`

`docs/Reflections.md` was deliberately not changed.

## Commands actually executed

- `git status`, `git log`, tracked-file and branch/remote inspections
- `./check_mp1_structure.sh .`
- `./gradlew clean check releaseJar --no-daemon`
- `sha256sum -c release/SHA256SUMS.txt`
- `unzip -t`, manifest inspection, and JavaFX native-library inspection
- GitHub repository/default-branch/visibility and workflow-run inspections
- numbered-log sequence, required-content, review-checkbox, and footer audits
- documentation marker/claim and tracked secret/generated-file scans
- `git diff --check`

## Actual build and test results

- Repository structure: all required items present; only the expected local-folder-name warning.
- Full Gradle quality gate: BUILD SUCCESSFUL.
- JUnit: 86 tests, 0 failures, 0 errors, 0 skipped.
- Checkstyle main and test tasks passed.
- Release checksum and ZIP integrity passed.
- JAR manifest names `constella.Launcher` and contains Windows `.dll`, Linux `.so`, and macOS `.dylib` JavaFX graphics natives.
- Public repository name and default `master` branch are correct.
- Latest Windows, Linux, and macOS CI jobs passed and produced byte-identical JARs before this audit.
- Logs `001` through `015` existed with no sequence gap; this audit adds `016`.

## Problems or limitations

- `docs/Reflections.md` still contained explicit personal-completion markers when the audit began; the user was working on it separately.
- Student review boxes remain unchecked, and several earlier logs have blank reviewer fields until the student completes their review.
- Windows and Linux GUI behavior still needs manual testing on those desktops.
- Intel macOS and ARM Windows/Linux native artifacts are not bundled.

## Changes made after verification

The initial clean build reported `releaseJar UP-TO-DATE` because `clean` no longer removed the checked-in release after the earlier multi-JAR design. The build was corrected so a clean release always regenerates the single submitted JAR.

## Outcome

The objective project, repository, documentation, logging, and release checks pass. The remaining submission work is explicitly personal or manual: finish reflections, personally review the logs, and complete outstanding cross-platform GUI checks where possible.

Suggested commit message: `chore: complete final submission audit`

## Student review

- [x] I confirmed that the original prompt is accurate.
- [x] I confirmed that the changed-file list is accurate.
- [x] I confirmed that recorded commands were actually executed.
- [x] I confirmed that build and test results are accurate.
- [x] I added any mistakes or disagreements omitted by the AI.

Reviewed by: A0273503L Lum Yi Ren Johannsen
Review date: 31 August 2026
