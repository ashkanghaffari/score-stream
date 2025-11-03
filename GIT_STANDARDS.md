# Git Standards

Lightweight, consistent rules for **branch names** and **commit messages**. Optimized for GitHub Issues and a clean history.

---

## Branch Naming

**Branch Types (use these)**
- `feature/` — new functionality or significant change
- `fix/` — bug fixes
- `docs/` — documentation-only changes
- `test/` — tests only (unit/integration/perf)
- `ci/` — GitHub Actions, build, or pipeline changes

**Rules**
- Use **lowercase**, **kebab-case** for the slug (`-` separators).
- Always include the **GitHub issue number** right after the type.
- Keep slugs short, action-oriented.

**Examples**
```text
feature/9-k6-catalog-smoke
fix/27-order-null-pointer
docs/45-update-readme-branch-standards
test/53-k6-load-stages
ci/61-cache-docker-layers
```


## Commit Messages (Conventional Commits)
**Format**
```aiexclude
<type>(scope?): short description

[optional body]

[footer for issue linkage]

```

**Types (use this slim set)**
- `feat` — new feature
- `fix` — bug fix
- `docs` — docs-only changes
- `test` — tests-only
- `ci` — CI/CD, build, tooling

**Examples**
```text
feat(k6): add catalog smoke and load test scripts
fix(order): handle missing payment reference in DTO
docs(readme): add branch/commit standards
test(k6): raise arrival-rate stages for load profile
ci(actions): add manual k6 runner workflow
```


---

## Linking Issues (Branches & Commits)

- **In the branch name:** include the **issue number** after the type.
    - `feature/9-k6-catalog-smoke` → targets Issue **#9**
- **In the commit footer:** close or reference the issue explicitly:
    - `Closes #9` (auto-closes on merge)
    - `Fixes #27`, `Relates to #53`, etc.

**Commit example with issue link**
```text
feat(k6): add catalog smoke and load test scripts

- VU-driven smoke + arrival-rate load
- Env-file overrides for URLs/hosts/thresholds
- Health precheck w/ retry/backoff
- JUnit/JSON/TXT summaries

Closes #9
```


---

## Pull Requests

- **Title** should follow the same conventional style:
    - `feat(k6): add catalog smoke and load test scripts (#9)`
- **Description**: include context, screenshots/logs if useful, and mention the issue:
    - `Closes #9`
- Prefer **squash & merge** to keep a clean, linear history.

---

## Quick Reference

**Allowed commit types:** `feat`, `fix`, `docs`, `test`, `ci`  
**Branch pattern:** `<type>/<issue-number>-<slug>`  
**Issue linking:** `Closes #<number>` in commit/PR body