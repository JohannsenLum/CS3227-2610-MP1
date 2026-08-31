# Reflections on AI-assisted Software Engineering

This reflection discusses three prompts that changed how I used AI during the development of Constella. I selected examples where verification or ambiguity required me to go beyond accepting generated output and exercise my own engineering judgement.

## Candidate 1 — Project scaffolding and Gradle/JUnit compatibility

*Excerpt from the original prompt (`logs/001-project-scaffolding.md`):*

> For this increment, perform only the following work:
>
> 1. Create a maintainable Gradle project using Java 25, JavaFX, and
>    JUnit 5.
>
> [...]
>
> 5. Add at least one meaningful automated test that does not require
>    launching the JavaFX interface.
>
> [...]
>
> After implementation:
>
> 1. Run the complete automated test suite.
> 2. Run a clean Gradle build.

My first prompt asked the AI to scaffold a Java 25 Gradle project, create a minimal JavaFX window and add a UI-independent test. I deliberately required the exact commands and results to be recorded because a generated project structure can look convincing without actually compiling. I also requested a UI-independent test because I wanted an automated check that did not depend on whether JavaFX could open a graphical window on the testing machine. At that stage, I was using the AI not only to generate files but also to establish a repeatable verification process for later increments.

The first test run failed because the generated build configuration did not include the JUnit Platform launcher required by Gradle 9. The AI had assumed that declaring JUnit Jupiter was sufficient for test execution. This showed me that an AI can produce a conventional-looking build file while overlooking a compatibility detail involving the specific Gradle and JUnit versions in use. The omission was small, but it would have remained hidden if I had accepted the source files without running the wrapper command.

After the failure, the launcher was added as a test runtime dependency. I independently verified the correction by running the tests again, then performing a clean build and launching the JavaFX application. I considered the diagnosis efficient because it responded to the actual Gradle error instead of changing unrelated dependencies. However, the successful correction did not change the fact that the original output was incomplete.

This experience changed my later prompts. I began asking for a baseline result before editing, focused tests after each change, and a complete quality gate at the end. I also required failures and corrections to be written into the interaction logs instead of reporting only the successful final state. The engineering judgement still remained with me: I had to decide what level of verification was credible and whether a passing unit test was enough for a graphical application. In a future scaffold prompt, I would name the exact Java, Gradle and JUnit versions, request a clean build immediately, and explicitly require the runtime test launcher rather than relying on conventional defaults. See `logs/001-project-scaffolding.md`.

## Candidate 2 — Memory normalization and identity

*Excerpt from the original prompt (`logs/002-memory-domain-model.md`):*

> ## Design requirements
>
> - Keep all domain code independent of JavaFX.
> - Keep all domain code independent of JSON and persistence libraries.
> - Clearly define whether `Memory` is immutable or mutable and justify
>   the choice.
> - Clearly define equality and identity semantics.
> - Prevent callers from mutating internal collections.
> - Reject null or invalid mandatory values.
> - Normalize titles, tags, people, and location consistently where
>   appropriate.
> - Reject blank tags and blank people names.
> - Prevent duplicate tags after normalization.
> - Prevent duplicate people after normalization.

The second prompt focused on the `Memory` domain model. I asked for immutability, validation and explicit identity rules because these decisions would affect persistence, editing, filtering and constellation membership throughout the project. Some requirements were technical, such as defensive copies and non-null values, but several were product decisions. Lowercasing tags makes filtering predictable, while preserving the first spelling of a person's name keeps the display more natural. Treating blank descriptions and locations as absent also avoids storing values that are technically present but contain no useful information.

The prompt did not specify what should happen when normalized duplicates occurred. The AI chose to coalesce duplicate tags and people rather than reject the memory. I agree with that decision for this application because a journal should be forgiving when a user enters `Travel` and `travel`, or repeats a person's name with different capitalization. Rejecting the entire memory would create friction without protecting important data. Nevertheless, this was still an assumption made by the AI rather than a conclusion forced by my prompt. The same prompt also left future dates unrestricted. These cases taught me that normalization rules are part of user-facing behaviour, even when they are implemented inside a domain class.

Tests helped make those assumptions visible. They checked trimmed titles, absent optional text, normalized collections, invalid importance values, defensive copying and UUID-based equality. Reviewing these cases forced me to decide whether the implemented behaviour matched the intended product rather than merely whether the code passed. UUID-only equality was especially important: editing a title or mood should not turn an existing memory into a different entity. If equality included every field, replacements, collection membership and constellation references would become more fragile whenever a memory was edited.

My later prompts became more example-driven because of this ambiguity. Instead of saying only “normalize duplicates,” I would now provide input-output examples and state whether invalid input should be rejected, corrected or preserved. The AI was useful for enumerating invariants and producing boundary tests, but I was still responsible for deciding what was friendly and meaningful for a journal user. In a future project, I would settle identity, normalization and date policies before persistence or UI work begins and record them as product decisions in the developer documentation. See `logs/002-memory-domain-model.md`.

## Candidate 3 — Safe persistence and cross-platform release

*Excerpt from the master prompt (`logs/003-journal-service.md`):*

> ## Persistence requirements
>
> Use a local, human-readable format such as JSON.
>
> Requirements:
>
> - Store data in an appropriate per-user application-data directory
>   rather than inside the installed application directory.
> - Keep persistence paths cross-platform.
> - Handle a missing data file as an empty journal.
> - Validate loaded data.
> - Do not silently overwrite malformed data.
> - Save atomically using a temporary file followed by replacement where
>   supported.
> - Use UTF-8.
>
> [...]
>
> ## Release preparation
>
> [...]
>
> - Do not claim one JAR is universally compatible if it contains
>   platform-specific JavaFX native libraries.
> - Document exactly what was built and on which platform.
> - Document how Windows and Linux artifacts must be built or verified.

The MVP prompt required local JSON persistence that did not silently destroy malformed data. I formulated it this way because journal entries are user data, so recovering from an error by overwriting the original file would be worse than refusing to save. This requirement led to UTF-8 storage, validated reconstruction, a same-directory temporary file and atomic replacement where supported. Tests covered missing files, malformed JSON, Unicode, round trips, stale constellation memberships and failed writes. The important lesson was that persistence quality is not demonstrated only by a successful save-and-load example; failure behaviour is part of the design.

The release work exposed a different type of assumption. The AI initially treated JavaFX native libraries as a reason to keep separate macOS, Windows and Linux JARs. That was technically defensible, but I later checked the exact submission wording, which repeatedly referred to one latest JAR while also requiring the application to function on all three operating systems. I decided that the safer interpretation was one `Constella.jar` containing the native libraries required by Windows x86-64, Linux x86-64 and Apple Silicon macOS.

Producing that JAR required more than changing its filename. The first Gradle attempt placed mutually exclusive JavaFX variants in one configuration and failed with a capability conflict. Separate configurations fixed that issue, but comparing CI artifacts revealed that the three runners still produced different hashes. The first difference came from host-selected JavaFX artifacts being expanded before the fixed universal set. After correcting the order, Windows still differed because Git had converted the bundled CSS to CRLF. Adding repository line-ending rules finally produced the same SHA-256 hash on all three CI runners. This sequence is a good example of the AI being useful for implementation and diagnosis while still requiring me to question its packaging assumptions and verify the actual binary output.

The final CI results prove that the project builds, passes Checkstyle and passes its 86 automated tests on all three hosted operating systems. They do not prove every GUI interaction on physical Windows and Linux desktops, so the User Guide keeps those checks manual instead of claiming more evidence than I have. In a future project, I would interpret the release requirement before designing packaging, define the exact supported architectures, and compare artifacts from CI much earlier. I would also retain the malformed-data tests because they verify a safety property that visual inspection cannot provide. See `logs/004-json-persistence.md`, `logs/008-release-preparation.md`, `logs/014-cross-platform-release-artifacts.md` and `logs/015-single-cross-platform-jar.md`.

## Overall reflection

Prompting was most effective when I could express a bounded outcome with observable acceptance criteria. Requirements such as keeping domain logic independent of JavaFX, running focused tests and recording exact command results gave the AI useful constraints and made its work easier to review. The numbered increments also prevented the project from becoming one large generated change whose assumptions would be difficult to trace.

Prompting was less effective for subjective visual quality and ambiguous requirements. The initial welcome screen was functional but visually simple, and the first 2.5D graph did not create the spatial experience I wanted. I had to compare the running application with my reference, reject results that technically met the prompt but looked wrong, and refine the request toward genuine JavaFX 3D. Manual inspection was also essential for dialog sizing, graph interaction, screenshots and confirming that selected memories dim unrelated nodes correctly. These are areas where passing model or geometry tests does not guarantee a convincing interface.

Reviewing the logs showed several occasions where the AI was plausible but not fully correct: the missing JUnit launcher, an unstated duplicate policy, the initial platform-specific release interpretation, Gradle variant conflicts and non-identical CI artifacts. The common lesson is that confident output is not evidence. My role was to interpret the specification, decide product behaviour, run the official checks, inspect failures and keep documentation aligned with what was actually verified.

For a future project, I would start by converting the specification into a traceable checklist and resolving packaging constraints before feature development. I would use smaller prompts with concrete examples for ambiguous behaviour, require a baseline and post-change verification for every increment, and introduce multi-platform CI earlier. I would also schedule manual UI reviews throughout development rather than treating visual inspection as final polish. AI substantially increased the amount I could build, but the quality of the result depended on how carefully I constrained, tested and challenged its output.
