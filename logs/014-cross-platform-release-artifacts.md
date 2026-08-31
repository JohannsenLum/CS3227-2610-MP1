# 014 — Cross-platform release artifacts

Date: 31 August 2026

## Objective

Add the successful Linux and Windows CI builds to `release/`, preserve them across local clean builds, document how to run each platform artifact, and push the verified update.

## Original prompt (verbatim)

> The only script complaint is the [WARN] about the local folder being named MP1 — that's
> cosmetic. It checks your local directory name; the GitHub repo is named correctly, which
> is what actually gets pulled.
>
> **🔴 Reflections.md is still a template**
>
> This is the problem. It's **4.1 KB and every reflection section is unwritten** — it literally
> says:
>
> ▎ *"Complete every **Student reflection** section in your own words before submission."*
>
> Three ### Student reflection — complete personally sections and the ## Overall reflection
> — complete personally section are all just **lists of questions**, not answers. What's there
> is only the *factual evidence* half.
>
> **That's 25% of MP1 — tied with Code Quality as the largest component.** The scaffolding is
> genuinely good (three well-chosen candidates, each anchored to a specific log file), but a
> grader reading this sees prompts, not reflection.
>
> The good news: the hard part is done. Each section already names the concrete incident —
> the missing junit-platform-launcher dependency, the ambiguous duplicate-handling that got
> coalesced, the honesty of a platform-labelled JAR versus a claimed universal one. You just
> need to answer the questions in your own voice.
>
> **🟡 Minor: release/ holds only the macOS ARM64 jar**
>
> build.gradle:43 derives the platform from whatever machine runs releaseJar, so you shipped
> Constella-macos-arm64.jar. Your CI *does* build all three and your docs deliberately avoid
> claiming the jar is universal — which is defensible and honest. But a peer tester on
> Windows can't run what's in release/. Worth either downloading the Windows/Linux CI
> artifacts into release/, or making the UserGuide point clearly at the CI artifacts.
>
> ★ Insight ─────────────────────────────────────
> - This is exactly the gap I flagged when we read the script: it verifies **presence and
> **non-emptiness**, never content. docs/Reflections.md exists and is non-empty, so it returns
> [OK] — while being unfinished in the single most heavily weighted way possible. Passing
> the structure check means you avoided the structural penalty, nothing more.
> - Your build.gradle naming the artifact Constella-macos-arm64.jar rather than
> Constella.jar is a small piece of good engineering judgement — it refuses to imply
> portability the artifact doesn't have. That's itself worth a sentence in the Overall
> reflection.
>
> Can you update the release

## Response summary

Downloaded the successful Windows and Linux artifacts from GitHub Actions run `33377534350`, placed them beside the macOS artifact, recorded SHA-256 checksums, updated packaging behavior and CI artifact selection, and aligned the three project guides with the checked-in releases.

## Assumptions

- The user meant to include the already successful CI-built Windows and Linux JARs in the submitted repository.
- CI success verifies compilation, Checkstyle, tests, and packaging but not interactive JavaFX behavior on a physical Windows or Linux desktop.
- Personal reflection prose must remain the student's own work and was therefore not fabricated.

## Design decisions

- Kept distinct platform and architecture names because JavaFX native libraries are not portable between operating systems.
- Removed the custom deletion of `release/` from Gradle `clean`, preventing a macOS clean build from deleting checked-in Windows and Linux artifacts.
- Restricted each CI upload to its platform-specific filename pattern so every workflow artifact remains unambiguous.
- Added `SHA256SUMS.txt` for integrity verification.

## Files changed

- `.github/workflows/gradle.yml`
- `build.gradle`
- `README.md`
- `docs/UserGuide.md`
- `docs/DeveloperGuide.md`
- `docs/Reflections.md`
- `release/Constella-linux-x86_64.jar`
- `release/Constella-windows-x86_64.jar`
- `release/SHA256SUMS.txt`
- `logs/014-cross-platform-release-artifacts.md`

## Commands actually executed

- `gh run download 33377534350 --repo JohannsenLum/CS3227-2610-MP1 --name Constella-Windows ...`
- `gh run download 33377534350 --repo JohannsenLum/CS3227-2610-MP1 --name Constella-Linux ...`
- `unzip -t` for the downloaded and final JARs
- `./gradlew clean check releaseJar --no-daemon`
- SHA-256, archive-content, native-library, test-result, Git diff, and structure checks

## Actual build and test results

- `./gradlew clean check releaseJar --no-daemon` — BUILD SUCCESSFUL.
- JUnit result — 86 tests, 0 failures, 0 errors, 0 skipped.
- Checkstyle production and test checks passed.
- All three JARs passed ZIP archive-integrity checks.
- Linux JAR contains `libglass.so` and `libprism_es2.so`.
- Windows JAR contains `glass.dll` and `prism_d3d.dll`.

## Problems or limitations

- Windows and Linux interactive GUI behavior was not manually exercised on physical desktops.
- The macOS artifact targets Apple Silicon; Intel macOS is not included.
- `docs/Reflections.md` still requires the student's personal answers.

## Changes made after verification

The CI upload path was changed from the broad `release/*.jar` glob to a per-platform pattern because retaining all checked-in artifacts through `clean` would otherwise make every CI artifact contain all three JARs.

## Outcome

The repository now offers clearly labelled macOS ARM64, Windows x86-64, and Linux x86-64 JARs with recorded checksums while preserving honest platform verification claims.

Suggested commit message: `release: include Windows and Linux artifacts`

## Student review

- [x] I confirmed that the original prompt is accurate.
- [x] I confirmed that the changed-file list is accurate.
- [x] I confirmed that recorded commands were actually executed.
- [x] I confirmed that build and test results are accurate.
- [x] I added any mistakes or disagreements omitted by the AI.

Reviewed by: A0273503L Lum Yi Ren Johannsen
Review date: 31 August 2026
