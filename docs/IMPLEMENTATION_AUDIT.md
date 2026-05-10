# Readora Implementation Audit

## Current Verified State
- Debug build passes after the user-added architecture/source work.
- Room, repository classes, ViewModels, Compose Navigation tab routes, WorkManager workers, parser models, and repository-manager screens exist.
- The app is still hybrid: several production paths still use `ReadoraPreferences` directly while Room is being introduced.

## Correctly Implemented Foundations
- Room database with comics, chapters, progress, download jobs, merge groups, sources, updates, bookmarks, notes, and reading sessions.
- One-time SharedPreferences-to-Room migration helper.
- Repository wrappers for major database areas.
- Typed settings serializer and settings ViewModel.
- Bottom-tab navigation via Compose Navigation.
- Source manifest parser, source installer, and repository manager UI.
- Safe parser strategy document: declarative definitions instead of arbitrary downloaded code.

## Issues Found And Fixed In This Audit
- Repository-installed sources were saved to Room but could not become runtime `OnlineSource` instances for Discover.
- Source manifest entries did not preserve parser definition JSON, repository ID, or trust level.
- Room `SourceEntity` lacked columns needed to recreate repository-installed parser sources.
- `SourceInstaller` error messages escaped Kotlin interpolation and showed literal `${...}` text.
- JSON parser engine could not parse single-object detail responses and only preserved top-level maps.

## Remaining Gaps In The New Work
- Library/Home/Reader still rely partly on `ReadoraPreferences`; repository/ViewModel adoption is incomplete.
- Details and Reader routes are still mostly driven by in-memory state instead of full route arguments.
- Repository manager stores repository manifests only in memory; installed sources persist, but the repository list itself should persist.
- Parser definitions are now persisted and can be turned into runtime sources, but manifest validation and source QA are still early.
- UI files are split physically but still use root package/import-heavy copied files; cleanup remains.

## Next Recommended Direction
- Finish the hybrid Room transition before piling on more user-facing features.
- Persist repository manifests, not only installed source rows.
- Finish route-based details/reader navigation.
- Add parser manifest validation and sample fixtures.
- Then move into download/background/source QA phases.
