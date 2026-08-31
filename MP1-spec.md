# CS3227 MP1 — AI-assisted SE

**Weight 15% · 100 points · Due Tue 1 Sep 2026, 2pm SGT · Locks 2 Sep 2pm · INDIVIDUAL**

Consolidated from: Canvas assignment 267103, announcements of 17 Aug & 25 Aug 2026,
and the course Grading page. No single Canvas file contains all of this.

Sources:
- Assignment: https://canvas.nus.edu.sg/courses/99226/assignments/267103
- AI Guidance / trimmed iP spec: https://nus-cs2103-ay2627-s1.github.io/website/projectDuke/cs3227.html
- Grading & late policy: https://canvas.nus.edu.sg/courses/99226/pages/grading
- GitHub username quiz: https://canvas.nus.edu.sg/courses/99226/modules/items/828452

---

## Project description

Develop a **personal utility app** while using LLMs and prompting as part of the
software development process. Expected level of work is similar to the CS2103/T iP.

You define the features. The **minimum bar for functionality and complexity is your
CS2103/T iP** — because AI tools should let you accomplish more for the same effort.

Jin strongly encourages re-doing the CS2103/T iP under the AI Guidance as a warm-up.

As you work, reflect on the use of AI and produce a reflection document.

### Reflection guiding questions (from the write-up)
- Why was the prompt formulated that way?
- What assumptions did the LLM make?
- What did it get wrong?
- How did I verify the result?
- How did the prompt evolve?
- When was prompting less effective than manual work?
- What engineering judgement was still required?
- What would I do differently next time?

---

## Restrictions

- **Individual project** — no team submissions.
- **Java desktop app**, as in CS2103/T iP. Default version **Java SE 25**.  [amended 25 Aug]
- Must **function properly on Windows, Linux AND macOS**. Ask classmates on the
  forum or TAs to test an OS you lack.                                        [amended 25 Aug]
- Focus on **LLM & prompting**; agentic dev features from the AI Guidance are allowed.
- **MAY NOT replicate CS2103/T iP or tP functionality** (a to-do manager with a chat
  interface). A chatbot doing something else entirely IS allowed — Jin's example is
  "a budget tracker with a chat interface".
- Violating the restrictions means **being asked to redo the project**.
- If unsure, clarify on the Ed forum.

---

## Submission — strict, penalised if not followed

Repo named exactly **`CS3227-2610-MP1`**, on your GitHub account, set to **public**.

| Path | Required contents |
|---|---|
| `src/…` | All source code. Reviewed for code quality. Format it nicely. |
| `release/…` | Latest jar built with Gradle (or similar) **with libraries bundled** (e.g. JavaFX). |
| `docs/UserGuide.md` | All current features + how to set up and test. Must match the product precisely — **inaccuracies are treated as bugs** by peer testers. |
| `docs/DeveloperGuide.md` | System design + SE process. Must match the latest release. **Include an acknowledgements section** citing all reused ideas/code/documentation. |
| `docs/Reflections.md` | Reflections on AI-assisted SE. **At least 3 interesting prompts, explained in detail.** |
| `logs/…` | Summaries of **all** prompts and interactions during development. Have AI generate them, then verify correctness. |

Also required:
- The **`master`** branch must be up to date — they pull `master` before the deadline.
  (NOT `main` — GitHub's default. Rename it.)
- Submit your **GitHub username via the Canvas quiz** by the same deadline.
- Repo must stay **accessible with no further changes after submission**.
- **No extensions.** Medical certificate only.

---

## Grading criteria (tentative)

| Component | Weight |
|---|---|
| Code Quality | 25% |
| Reflections on AI-assisted SE | 25% |
| Features | 20% |
| Basic SE Practices (project mgmt, design, testing) | 20% |
| Documentation Quality | 10% |

Assessed by **peer evaluators and tutors**. You will be required to formally
evaluate other students' submissions. Evaluation process details released later.

---

## Late penalties (Grading page)

| Late by | Penalty |
|---|---|
| within 1 hour | -10% |
| within 6 hours | -40% |
| within 12 hours | -70% |
| within 24 hours | -85% |
| after 24 hours | **zero** |

---

## Pre-submission checklist

- [ ] Repo named `CS3227-2610-MP1` and **public**
- [ ] Default/updated branch is **`master`**, pushed
- [ ] `src/` — formatted, no dead code
- [ ] `release/` — jar runs standalone with JavaFX bundled
- [ ] Tested on Windows / Linux / macOS
- [ ] `docs/UserGuide.md` matches product exactly
- [ ] `docs/DeveloperGuide.md` incl. acknowledgements section
- [ ] `docs/Reflections.md` with >= 3 detailed prompt examples
- [ ] `logs/` complete and verified
- [ ] Does NOT replicate iP/tP to-do-manager functionality
- [ ] GitHub username submitted via Canvas quiz
- [ ] No commits after submission
