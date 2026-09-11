# TaskTide Release

## Overview

**Release:** `<release name / description>`  
**Date:** `<YYYY-MM-DD>`  
**Release type:** `<feature | bugfix | breaking | maintenance>`

### Summary

<Brief description of what this release accomplishes and why it is being released.>

### Highlights

- <Highlight>
- <Highlight>
- <Highlight>

---

## Release Versions

| Module | Version | Released? |
|---|---:|:---:|
| `mutex` | `<version>` | ☐ |
| `parser` | `<version>` | ☐ |
| `itemstore` | `<version>` | ☐ |
| `core` | `<version>` | ☐ |
| `engine` | `<version>` | ☐ |
| `api` | `<version>` | ☐ |
| `tasktide` | `<version>` | ☐ |

> Only modules whose versions change need to be released. Unchanged modules should retain their existing versions.

---

## Relevant Modules

List the modules affected by this release and briefly explain their involvement.

- `mutex` — <why this module is relevant, or "No changes">
- `parser` — <description>
- `itemstore` — <description>
- `core` — <description>
- `engine` — <description>
- `api` — <description>
- `tasktide` — <description>

---

## Module Releases

### `mutex`

**Version:** `<version>`  
**Previous version:** `<version>`

#### Changes

- <Change>
- <Change>

#### Breaking Changes

- <None / description>

#### Migration Notes

<Migration instructions, if applicable.>

---

### `parser`

**Version:** `<version>`  
**Previous version:** `<version>`

#### Changes

- <Change>
- <Change>

#### Breaking Changes

- <None / description>

#### Migration Notes

<Migration instructions, if applicable.>

---

### `itemstore`

**Version:** `<version>`  
**Previous version:** `<version>`

#### Changes

- <Change>
- <Change>

#### Breaking Changes

- <None / description>

#### Migration Notes

<Migration instructions, if applicable.>

---

### `core`

**Version:** `<version>`  
**Previous version:** `<version>`

#### Changes

- <Change>
- <Change>

#### Breaking Changes

- <None / description>

#### Migration Notes

<Migration instructions, if applicable.>

---

### `engine`

**Version:** `<version>`  
**Previous version:** `<version>`

#### Changes

- <Change>
- <Change>

#### Breaking Changes

- <None / description>

#### Migration Notes

<Migration instructions, if applicable.>

---

### `api`

**Version:** `<version>`  
**Previous version:** `<version>`

#### Changes

- <Change>
- <Change>

#### Breaking Changes

- <None / description>

#### Migration Notes

<Migration instructions, if applicable.>

---

### `tasktide`

**Version:** `<version>`  
**Previous version:** `<version>`

#### Changes

- <Change>
- <Change>

#### Breaking Changes

- <None / description>

#### Migration Notes

<Migration instructions, if applicable.>

---

## Compatibility / Dependencies

Document important version relationships between components.

| Component | Depends On | Required Version |
|---|---|---:|
| `parser` | `mutex` | `<version>` |
| `itemstore` | `core` | `<version>` |
| `engine` | `core` | `<version>` |
| `api` | `core` | `<version>` |
| `tasktide` | `core`, `engine`, `api`, etc. | `<versions>` |

### Compatibility Notes

<Describe any compatibility requirements or intentionally supported combinations.>

---

## Breaking Changes

List breaking changes across the release.

- **`<module>`:** <description>
- **`<module>`:** <description>

If there are no breaking changes:

> No breaking changes in this release.

---

## Migration Notes

<Describe anything users or downstream projects need to change when upgrading.>

If no migration is required:

> No migration required.

---

## Verification

### Build

- [ ] Clean build completed
- [ ] All tests pass
- [ ] Javadocs generated successfully
- [ ] No unexpected dependency resolution changes
- [ ] Published artifacts verified locally

### Module Verification

- [ ] `mutex`
- [ ] `parser`
- [ ] `itemstore`
- [ ] `core`
- [ ] `engine`
- [ ] `api`
- [ ] `tasktide`

### Runtime / Integration Verification

- [ ] CLI/application starts successfully
- [ ] Core workflows verified
- [ ] API verified
- [ ] Relevant integration scenarios verified

---

## Publication

### Pre-release

- [ ] Versions updated in `gradle.properties`
- [ ] Release notes completed
- [ ] Working tree clean
- [ ] Release commit created
- [ ] Git tag prepared

### Publish

- [ ] Component artifacts published
- [ ] Aggregate `tasktide` artifact published
- [ ] POM dependencies verified
- [ ] Signatures verified
- [ ] Published artifacts available from Maven Central

### Post-release

- [ ] Git tag pushed
- [ ] GitHub release created
- [ ] Documentation updated
- [ ] Next development versions/configuration prepared

---

## Release Notes

<Final user-facing release notes.>

### Added

- <Item>

### Changed

- <Item>

### Fixed

- <Item>

### Removed

- <Item>

### Breaking

- <Item>
