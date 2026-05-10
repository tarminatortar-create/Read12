# Readora 100/100 Roadmap

## Phase 1 - Native Foundation [done]
- Kotlin + Jetpack Compose Android APK.
- Premium mobile UI shell: Home, Library, Discover, Merge, Settings, Details, Reader.
- Debug APK output at `Readora-debug.apk`.

## Phase 2 - Online Source Engine [done]
- Source interface: popular, search, details, chapters, pages.
- Built-in MangaDex connector.
- Live Discover, online Details, and online Reader.
- INTERNET permission and remote image display.

## Phase 3 - Persistence + Library [done]
- SharedPreferences storage layer.
- Persistent source repos, settings, last read, saved online library.
- Add/remove online titles from Library.
- Reopen saved Library titles into live details.
- Per-chapter reading progress and resume.

## Phase 4 - Offline Downloads [done]
- Chapter cache metadata.
- Download chapter page images to app storage.
- Offline availability badges.
- Reader prefers cached pages when available.
- Cache manager controls and storage stats.

## Phase 5 - Local Imports [done]
- Local source model for CBZ/ZIP/image folders.
- Android file picker.
- Persistent local library entries.
- Local CBZ/ZIP extraction to reader pages.
- Local image/document reader fallback.

## Phase 6 - Merge Engine [done]
- Persistent merge group model.
- Smart detection from online/local library entries.
- Merge Lab group display with linked entries.
- Next: manual merge/unmerge and source priority editing.

## Phase 7 - Polish + Release [in progress]
- Better navigation architecture.
- Image memory/cache improvements.
- Reader gestures and tap zones.
- Download queue UX and notifications.
- Backup/export/import.
- Signed release APK build.
- Debug/release build scripts.
- Offline cache stats and clear control.


## Phase 8 - Backup Center [done]
- Export/import Readora backup JSON.
- Backup includes settings, source repositories, online library, local library, merge groups, and last read state.
- Settings Backup Center uses Android document picker for safe user-controlled files.


## Phase 9 - Update Center [done]
- Library Update Center for saved online titles.
- Checks MangaDex chapter feed for latest chapter number.
- Saved online Library cards show up-to-date/latest chapter state after checking.


## Phase 10 - Offline Download UX [done]
- Online details has Save latest offline.
- Each online chapter row has per-chapter offline save control.
- Chapter rows show offline/downloading state.
- Downloads save titles to Library and cache pages for reader reuse.

## Phase 11 - Reader Defaults [done]
- Reader mode and page direction persist in settings/backups.
- Settings exposes default webtoon and right-to-left paging toggles.
- Demo reader updates saved defaults when changed from the toolbar.

## Phase 12 - Library Dashboard + Resume [done]
- Library shows online/local/demo counts, offline cache size, and last-read signal.
- Home promotes the real last online read entry above demo continue rows.
- Last-read data now keeps source/comic/chapter IDs so the title can be reopened.

## Phase 13 - Source Health [done]
- Settings can run a live MangaDex health check.
- Health result reports online/error state plus response timing/sample count.

## Phase 14 - About Polish [done]
- Settings includes an About Readora panel with current feature status.
- Roadmap reflects the larger batched development milestones.

## Phase 15 - Batch Build Verification [done]
- Rebuilt debug APK.
- Rebuilt signed release APK.

## Phase 16 - Download Queue [done]
- Persistent offline download queue model.
- Settings queue panel with run, retry failed, clear done, clear all, and remove actions.
- Online details can queue latest chapters or individual chapters for offline download.
- Queue processor updates job status/progress and stores completed chapters in offline cache.

## Phase 17 - Reader Tap Zones [done]
- Paged reader supports left/right tap zones.
- Direction-aware tap behavior for right-to-left manga paging.
- Reader hint added beside manual previous/next controls.

## Phase 18 - Manual Merge Controls [done]
- Merge Lab has a manual merge builder for saved online/local entries.
- Merge groups can be removed.
- Source priority can be reordered with Up/Down controls.

## Phase 19 - Local Archive Polish [done]
- CBZ/ZIP reader extracts pages in natural filename order.
- Nested archive entries are handled through sorted ZIP metadata.
- Local library and reader copy now reflect ready-to-read local archive support.

## Phase 20 - Build Verification [done]
- Rebuilt debug APK.
- Rebuilt signed release APK.

## Phase 21 - Room Database Foundation [done]
- Room dependencies are configured.
- Core entities/DAOs/database singleton exist.
- Application wires the database and repository container.

## Phase 22 - Preferences-To-Room Migration [done foundation]
- One-time migration helper imports saved online/local library, progress, merge groups, sources, and download queue jobs.
- SharedPreferences remain as fallback while the UI is migrated gradually.

## Phase 23 - Database Repository Layer [partial]
- Repository wrappers exist for library, progress, downloads, merge groups, sources, updates, bookmarks, notes, and reading sessions.
- Remaining: move every screen away from direct `ReadoraPreferences` access.

## Phase 24 - ViewModel Foundation [partial]
- ViewModels exist for Home, Library, Discover, Reader, Settings, Updates, and repositories.
- Remaining: move all network/storage side effects out of composables.

## Phase 25 - Compose Navigation Refactor [partial]
- Bottom-tab routes use Compose Navigation.
- Repository Manager and Source Migration routes exist.
- Remaining: route-based online details, demo details, online reader, and local reader.

## Phase 26 - State Restoration [partial]
- Important tab/settings state uses saveable state and persistent settings.
- Remaining: SavedStateHandle-based reader/details restoration after process death.

## Phase 27 - Error Model Foundation [done foundation]
- App error/logging types exist and Discover maps failures into app-level errors.
- Remaining: apply consistently across all screens/workers.

## Phase 28 - Diagnostics Foundation [done foundation]
- Readora logger and diagnostics panel exist.
- Remaining: export diagnostics and better debug-only filtering.

## Phase 29 - Typed Settings Model [done]
- Settings serializer replaces raw scattered preference keys for new settings.
- Settings ViewModel exposes typed updates for reader, source, privacy, and update options.

## Phase 30 - Architecture Cleanup [partial]
- Major screens/components were split into separate files.
- Remaining: clean package names/imports and remove copied mega-import blocks.

## Phase 31 - Source API V2 [done foundation]
- `OnlineSource` capability-based API exists for popular/latest/search/details/chapters/pages/health.

## Phase 32 - Source Registry Database [done foundation]
- Source rows persist in Room and can be enabled/disabled from Settings.

## Phase 33 - Repository Manifest Schema [done foundation]
- Repository manifest and source manifest entry models exist.
- Manifest parser validates schema version and reads source definitions.

## Phase 34 - Repository Manager UI [done foundation]
- Repository Manager screen can fetch a manifest URL and install listed sources.
- Remaining: persist repository manifests themselves and show update status.

## Phase 35 - Source Install/Update Flow [done foundation]
- Source installer writes manifest source entries into Room and supports update/remove.
- Audit fix: parser type, parser definition JSON, repository ID, and trust level now persist.

## Phase 36 - Safe Parser Strategy [done]
- Parser strategy document rejects arbitrary downloaded code and uses declarative parsing rules.

## Phase 37 - Declarative JSON Parser [done foundation]
- JSON parser/adapter exists.
- Audit fix: parser now handles list and single-object responses and preserves mapped fields.

## Phase 38 - Declarative HTML Parser [done foundation]
- HTML/CSS parser adapter exists for list/detail/chapter/page extraction.

## Phase 39 - Runtime Repository Source Wiring [done foundation]
- Audit fix: enabled Room `SourceEntity` rows with parser definitions can become runtime `OnlineSource` instances in Discover.

## Phase 40 - Source System QA [partial]
- Debug build passes after source/runtime wiring.
- Remaining: manifest fixtures, parser tests, broken-source handling, and release verification.

## Phase 41 - Notification Infrastructure [done]
- POST_NOTIFICATIONS and RECEIVE_BOOT_COMPLETED permissions added.
- BootReceiver reschedules background update worker after device reboot.
- NotificationPermissionHelper handles Android 13+ runtime permission.

## Phase 42 - Updates Tab Improvements [done]
- Unread count badge displayed in tab header.
- "Check for updates now" button with animated loading state.
- Improved swipe-to-dismiss backgrounds (green mark-read / red delete).
- Unread/All filter chips show live counts.
- Empty state offers direct check button.

## Phase 43 - Bookmarks & Notes Screen [done]
- Dedicated BookmarksScreen with tab switcher (Bookmarks / Notes).
- BookmarkDao and ChapterNoteDao extended with getAll() and deleteAll().
- BookmarksViewModel drives the screen from Room data.
- Delete individual items or clear all with confirmation dialog.
- Accessible from Home screen header and Settings.

## Phase 44 - Global Search Screen [done]
- SearchScreen searches local library and MangaDex online simultaneously.
- Debounced 400ms input for efficient network usage.
- Results sectioned into "In your library" and "Online results".
- Search icon added to Home screen header bar.

## Phase 45 - Download Manager [pending]
- Remaining: real progress bars, pause/resume, per-item retry.

## Phase 46 - Reader Polish [done]
- ReaderFooter now displays current page / total pages (e.g. "7 / 82").
- Chapter title/number shown in footer label.
- Page progress applies to both online and local readers.

## Phase 47 - Build Verification [done]
- Debug APK rebuilt: Readora-debug.apk (~20 MB).
- Release APK rebuilt: Readora-release.apk (~13 MB).

## Phase 48 - Reading History Screen [done]
- Dedicated ReadingHistoryScreen shows all reading sessions from Room.
- Each session row shows comic title, chapter number, date/time, and duration.
- Total session count and cumulative reading minutes shown in header.
- Accessible from Settings → Reading time → "View Reading History".

## Phase 49 - Download Manager Screen [done]
- Full DownloadManagerScreen replaces the compact Settings panel.
- Per-job progress bars with animated fill, status icons, and page counts.
- Per-job pause, resume, retry, and remove actions.
- Bulk "Start queue", "Pause", "Retry failed", "Clear done", "Clear all" controls.
- Filter tabs: All / Pending / Done / Failed.
- Entry point: Settings → Download queue → "Open Download Manager".

## Phase 50 - Discover Genre Filters [done]
- Genre/tag filter chips extracted from live search results in Discover.
- Tapping a chip filters the visible results to matching tagged titles.
- Active filter shown in section subtitle; clear-filter chip dismisses it.
- DiscoverViewModel tracks selectedTag and computes availableTags.

## Phase 51 - Build Verification [done]
- Debug APK rebuilt: Readora-debug.apk (~20 MB).
- Release APK rebuilt: Readora-release.apk (~13 MB).

## Phase 52 - Statistics Dashboard [done]
- Full StatsScreen with library, reading, bookmark, download, and update stats.
- All data sourced from Room DAOs (bookmarks, notes, sessions, updates, comics).
- BigStatCard tiles with icon + colored pill per section.
- Cache size formatted from bytes.
- Accessible from Settings → View Full Statistics.

## Phase 53 - Library Sort & Filter [done]
- Sort online library by: Added (newest first), Title (A-Z), Last Read.
- Filter by source name (filter row hidden when only one source present).
- Live result count label updates as filters change.
- LibrarySortFilterBar composable with FilterChip rows.

## Phase 54 - Merge Lab Improvements [done]
- SavedMergeGroupCard now shows stacked cover thumbnails from linked online entries.
- Each entry row displays a mini cover (online) or book icon (local).
- Priority badge (gold #1, blue rest) per entry.
- Source name pills displayed in group header.
- Up/Down reorder buttons use arrow characters for compactness.

## Phase 55 - Notification Permission [done]
- On first launch (Android 13+), app requests POST_NOTIFICATIONS permission.
- Single LaunchedEffect check — only prompts if permission not yet granted.
- Result handled silently; user can adjust in system settings.

## Phase 56 - App Polish [done]
- Discover loading state replaced with animated skeleton shimmer cards (3 placeholder cards).
- Skeleton uses infinite pulsing alpha animation (RepeatMode.Reverse).
- Existing empty states for Updates, History, Bookmarks, Search verified complete.

## Phase 57 - Build Verification [done]
- Debug APK rebuilt: Readora-debug.apk (~20 MB, May 9 15:29).
- Release packaging skipped: signing keystore not present on build machine (expected in CI).
- All Kotlin sources compiled and dexed without errors; only warnings (deprecated AutoMirrored icon aliases, benign).

## Phase 58 - Chapter Navigation in Reader [done]
- OnlineReaderScreen now accepts allChapters list and onNavigateChapter callback.
- Chapter navigation strip shown below the reader toolbar when a full chapter list is available.
- SkipPrevious / SkipNext buttons; previous/next chapter titles shown as breadcrumbs.
- Navigation strips current chapter label and adjacent chapter hints.
- OnlineDetailsScreen onRead callback updated to pass full chapter list.
- OnlineReaderRequest extended with allChapters field.

## Phase 59 - Online Details Chapter Load-More [done]
- Chapter display cap raised from 60 to configurable chapterLimit (default 60).
- "Load more chapters (N remaining)" TextButton appears when more chapters exist.
- Each tap adds 60 more chapters to the visible list.
- chapterLimit reset when switching to a different comic.

## Phase 60 - Home Library Strip [done]
- HomeScreen now loads savedOnlineLibrary from ReadoraPreferences.
- Horizontal LazyRow strip shows up to 12 saved online covers above Smart Shelves.
- OnlineLibraryStripCard: cover, title (2 lines), last chapter number.
- Hidden in incognito mode. Tapping opens OnlineDetailsScreen.

## Phase 61 - Cache Clear Polish [done]
- CacheStatsPanel now shows a clearing spinner and "Cache cleared" confirmation state.
- Progress bar + descriptive text when cache is non-empty.
- "No offline data cached" label when empty.
- Clear runs on IO dispatcher; button disabled while clearing.

## Phase 62 - Build Verification [done]
- Debug APK rebuilt and verified.

## Phase 63 - Online Reader Paged Mode [done]
- OnlineReaderScreen supports "webtoon" (scrolling) and "paged" (one page at a time) modes.
- Mode toggle chips in the second toolbar row: Webtoon / Paged.
- Direction toggle: LTR / RTL.
- Paged mode: single full-screen page with left/right tap zones and Prev/Next nav buttons.
- Reader respects `settings.readerMode` and `settings.readingDirection` as initial values.

## Phase 64 - Chapter Progress Rings [done]
- OnlineChapterRow chapter number box upgraded with a Canvas-based arc progress ring.
- Ring shows progress fraction for in-progress chapters.
- Completed chapters (≥ 97%) show a tinted background + CheckCircle icon overlay.
- Progress arc uses Sky color with `StrokeCap.Round` ends; background track is translucent.

## Phase 65 - Reader Settings Expansion [done]
- Settings "Reader hardware controls" panel extended with three new controls:
  - Keep screen on toggle (prevents display sleep while reading).
  - Auto-scroll speed slider (40–800 dp/s, visible only when auto-scroll is enabled).
  - Reader brightness slider (0–100%; "Reset" button to restore system default).
- `setDefaultBrightness` added to SettingsViewModel.
- OnlineReaderScreen wires `keepScreenOn` flag via `DisposableEffect` on the Activity window.
- Reader brightness applied to `WindowManager.LayoutParams.screenBrightness`; restored on exit.

## Phase 66 - Home Last Read from Room [done]
- HomeViewModel already exposes `lastRead: StateFlow<List<ComicEntity>>` (Room-backed).
- HomeScreen collects `roomLastRead` state from HomeViewModel.
- New `RecentlyReadPanel` composable shows up to 5 Room-sourced recently-read comics.
  - Per-row: cover thumbnail (remote image or icon fallback), title, source/status, relative date ("Today", "Yesterday", "Nd ago"), continue arrow.
- Panel shown preferentially over the old `DatabaseContinuePanel` when data is present.
- Hidden in incognito mode.

## Phase 67 - Build Verification [done]
- Debug APK rebuilt: Readora-debug.apk (~20 MB, May 9 15:53).
- All Kotlin compiled without errors; only deprecation warnings (AutoMirrored icon aliases).

## Phase 68 - Discover Latest Updates Tab [done]
- `DiscoverFeedTab` enum added to `DiscoverViewModel` (Popular / Latest).
- `DiscoverUiState` extended with `latestComics`, `latestLoading`, `latestError`, `feedTab`.
- `selectFeedTab()` and `refreshLatest()` added to DiscoverViewModel; `refreshLatest` calls `MangaDexSource.getLatest()`.
- DiscoverScreen shows Popular/Latest filter chip toggle when not searching.
- Latest tab displays `getLatest(page=1)` results with skeleton loading and error/retry states.
- Section title and subtitle update contextually based on active tab and query.

## Phase 69 - Library Long-Press Context Menu [done]
- `SavedOnlineLibraryRow` upgraded with `combinedClickable` (long-press opens context menu).
- Overflow menu replaces the single delete button; shows "Open details" and "Remove from library" options.
- `DropdownMenu` / `DropdownMenuItem` imported; `ExperimentalFoundationApi` opt-in added.
- Long-press OR three-dot icon both open the menu.

## Phase 70 - Details Similar Titles Section [done]
- `OnlineDetailsScreen` fetches similar titles after load using first comic tag via `MangaDexSource.search(tag, 8)`.
- "You might also like" `PremiumPanel` added above the chapter list.
- Shows skeleton placeholder boxes while loading; horizontal `LazyRow` of cover+title cards when ready.
- Panel only renders when loading or results are available (hidden if no tags or search fails silently).

## Phase 71 - Reader Double-Tap Zoom [done]
- Paged mode reader supports double-tap to zoom (1× ↔ 2.5×).
- `zoomLevel` state + `animateFloatAsState` for smooth scale animation.
- `Modifier.graphicsLayer { scaleX / scaleY }` applied to page `Box`.
- Three gesture zones (left/centre/right) all support double-tap; left/right also handle single-tap navigation.
- Zoom resets on page turn; "Double-tap to reset zoom" hint shown when zoomed in.

## Phase 72 - Home Screen Cleanup [done]
- Demo `ContinueRow` items only shown if the user has no real reading history (no Room lastRead, no SharedPrefs lastRead, no saved online library).
- "Get started" panel shown as onboarding when library is empty; includes Discover and Library `AssistChip` shortcuts.
- When the user has real data, demo rows are suppressed and the Room-backed `RecentlyReadPanel` takes precedence.

## Phase 73 - Build Verification [done]
- Debug APK rebuilt: Readora-debug.apk (~20 MB, May 9 16:05).
- All Kotlin compiled without errors; only deprecation warnings (AutoMirrored icon aliases).

## Phase 74 - Search History [done]
- Successful online search taps are persisted to `SettingsSerializer.savedSearches`.
- Recent searches displayed as `FlowRow` chips in SearchScreen when the query is blank.
- "Clear recent searches" TextButton wipes the list.
- Chips pre-fill the search field on tap for quick re-search.

## Phase 75 - Merge Lab Duplicate Suggestions [done]
- `ReadoraPreferences.suggestDuplicates()` uses Jaccard-like word-overlap scoring (threshold 0.4, min 2 words) to find fuzzy-duplicate saved online titles.
- Returns `List<Pair<SavedOnlineComic, SavedOnlineComic>>`.
- MergeLabScreen loads and displays a "Suggested merges" panel when duplicates are found.
- Each pair row shows both titles with a "Merge" button that calls `createManualMergeGroup`.

## Phase 76 - Updates Batch Actions [done]
- `UpdatesDao` extended with `deleteAllRead()` (`DELETE … WHERE isRead = 1`).
- `UpdatesRepository.deleteAllRead()` and `UpdatesViewModel.deleteAllRead()` wired through.
- UpdatesScreen batch-actions row now has three buttons: **Mark read** / **Del read** / **Clear all**.
- `UpdateRow` cover placeholder upgraded to a real `AsyncImage` thumbnail (falls back to `AutoStories` icon when `coverUrl` is null/blank).

## Phase 77 - Reader Webtoon Auto-Scroll [done]
- Auto-scroll `LaunchedEffect` rewritten to only activate in webtoon mode.
- Uses `animateScrollBy` with smooth per-frame pixel increments derived from `settings.autoScrollSpeed` (dp/s) via `LocalDensity`.
- Frame interval is 16 ms (~60 fps); speed range 40–800 dp/s matches the Settings slider.
- Auto-scroll toggle chip in reader toolbar remains the user control.

## Phase 78 - Details Screen Share Button [done]
- "Share" `TextButton` added to the OnlineDetailsScreen info panel below "Queue latest offline".
- Fires `Intent.ACTION_SEND` with `text/plain` MIME type, sharing `<title>\nhttps://mangadex.org/title/<id>`.
- Uses `Intent.createChooser` so the user can pick any sharing target.
- `IosShare` icon (already imported) used as button leading icon.

## Phase 79 - Build Verification [done]
- Debug APK rebuilt: Readora-debug.apk (May 9).
- All Kotlin compiled without errors; only deprecation warnings (AutoMirrored icon aliases).

## Phase 80 - Discover Pagination [done]
- `DiscoverUiState` extended with `popularPage`, `popularLoadingMore`, `latestPage`, `latestLoadingMore`.
- `loadMorePopular()` and `loadMoreLatest()` added to `DiscoverViewModel`; each fetches the next MangaDex page and appends distinct results.
- `refresh()` resets `popularPage = 1` on a clean reload.
- "Load more" `TextButton` (or `CircularProgressIndicator` while loading) appears at the bottom of the Popular and Latest feeds in `DiscoverScreen`.
- Load-more button hidden during active search queries.

## Phase 81 - Reader Page HUD [done]
- Added `hudVisible` + `hudHideJob` state to `OnlineReaderScreen`; `showHudBriefly()` shows the HUD for 3 s then auto-hides.
- Paged mode: centre tap zone upgraded to call `showHudBriefly()` on single tap; static overlay replaced with a conditional `if (hudVisible)` block.
- Webtoon mode: `LazyColumn` wrapped in a `Box` with `detectTapGestures` → `showHudBriefly()`; HUD text shows `"currentPage / totalPages"` anchored to bottom-centre.
- Both HUDs use a semi-transparent pill background for readability.

## Phase 82 - Library Text Search [done]
- Added `librarySearchQuery: String` state to `LibraryScreen` (persisted with `rememberSaveable`).
- `displayedOnline` derivation now filters by `it.title.contains(query, ignoreCase = true)` before applying sort.
- `OutlinedTextField` with `Search` leading icon and `Close` trailing icon (clears query) added above the sort/filter bar.
- Search field only visible when the saved online library is non-empty.

## Phase 83 - Accent Colour Picker [done]
- `Gold` (`#FFD166`) colour added to `ReadoraTheme.kt`.
- `accentColorFromKey(key)` helper maps stored key → `Color`; defaults to `Ember`.
- `LocalAccentColor` `CompositionLocal` exposes the resolved colour throughout the tree.
- `ReadoraTheme` now accepts `accentKey: String`; passes resolved colour to `LocalAccentColor` and overrides `primary` in `MaterialTheme.colorScheme`.
- `ReadoraAppWithTheme` composable reads `settings.themeAccentColor` from `SettingsViewModel` state and passes it to `ReadoraTheme` — live updates without restart.
- `setThemeAccentColor()` added to `SettingsViewModel`.
- Accent picker panel added to `SettingsScreen`: five coloured circle swatches (Ember / Mint / Sky / Coral / Gold) with a white border on the active selection.

## Phase 84 - Build Verification [done]
- Debug APK rebuilt: Readora-debug.apk (May 9).
- All Kotlin compiled without errors; only deprecation warnings (AutoMirrored icon aliases, statusBarColor).

## Phase 85 - Home Screen Reading Streak [done]
- `HomeViewModel` accepts `ReadingSessionRepository`; exposes `readingStreak: StateFlow<Int>`.
- Streak = consecutive days ending today with ≥ 1 reading session.
- `ReadoraViewModelFactory` passes `readingSessionRepository` to `HomeViewModel`.
- `HomeScreen` shows flame chip "🔥 Nd streak" in header when streak ≥ 1; `PremiumPanel` streak card when ≥ 2.

## Phase 86 - Details Chapter Read/Unread Toggle [done]
- `clearChapterProgress(sourceId, comicId, chapterId)` added to `ReadoraPreferences.kt`.
- `OnlineChapterRow` in `CommonComponents.kt` gains `onToggleRead` parameter.
- Toggle `IconButton` per row: `CheckCircle` (read) or `RadioButtonUnchecked` (unread).
- `OnlineDetailsScreen` tracks `manuallyReadIds` local state and wires toggle lambda.

## Phase 87 - Library Grid/List View Toggle [done]
- `var libraryGridView by rememberSaveable` added to `LibraryScreen`.
- Header row includes `ViewModule`/`ViewList` `IconButton` to switch modes.
- Grid mode: 3-column `LazyVerticalGrid` showing cover + title.
- List mode: existing `SavedOnlineLibraryRow` unchanged.

## Phase 88 - Reader Save Page to Gallery [done]
- `WRITE_EXTERNAL_STORAGE` (maxSdk 28) and `READ_MEDIA_IMAGES` (API 33+) permissions added to `AndroidManifest.xml`.
- `savePageToGallery(url, pageIndex)` suspend helper added to `OnlineReaderScreen`: downloads bitmap via `java.net.URL`, inserts into `MediaStore` under `Pictures/Readora`, shows Toast on success/failure.
- Webtoon mode: each page `Box` gets `pointerInput` with `onTap = showHudBriefly` and `onLongPress = savePageToGallery`.
- Paged mode: page `Box` gets `pointerInput` with `onLongPress = savePageToGallery`.

## Phase 89 - Build Verification [done]
- Debug APK rebuilt: Readora-debug.apk (May 9).
- All Kotlin compiled without errors; only deprecation warnings (AutoMirrored icon aliases).

## Phase 90 - Details Reading List Status [done]
- `saveReadingListStatus(comicId, status)` / `loadReadingListStatus(comicId)` added to `ReadoraPreferences` (key `rl_status_<id>`).
- `readingListStatus` state added to `OnlineDetailsScreen`, loaded from prefs on open.
- Three `FilterChip` items ("Plan to Read" / "Reading" / "Completed") shown below the Share button in the info panel.
- Tapping active chip deselects it (back to ""); selection persists immediately to SharedPreferences.

## Phase 91 - Discover Tag Filter Persistence [done]
- `discoverTagFilter: String` field added to `ReadoraSettings`, serializer load/save, and SettingsSerializer JSON.
- `DiscoverViewModel` initialises `selectedTag` from `settingsSerializer.load().discoverTagFilter`.
- `selectTag()` and `clearTagFilter()` now call `persistTagFilter()` which saves the new value via `settingsSerializer.save()`.
- Selected genre filter survives app restarts.

## Phase 92 - Library Swipe-to-Delete [done]
- `SwipeToDismissBox`, `SwipeToDismissBoxValue`, `rememberSwipeToDismissBoxState` imported in `LibraryScreen.kt`.
- List-mode `items(displayedOnline)` now uses `key = { it.id }` for stable animations.
- Each row wrapped in `SwipeToDismissBox` (end-to-start only); background shows a red delete icon.
- On full swipe, `preferences.removeOnlineLibrary()` is called and `savedOnline` is updated.

## Phase 93 - Build Verification [done]
- Debug APK rebuilt: Readora-debug.apk (May 9).
- All Kotlin compiled without errors; only deprecation warnings (AutoMirrored icon aliases).

## Phase 94 - Reader Bookmark Panel [done]
- `bookmarkPanelOpen` state and `comicBookmarks` flow (via `bookmarkRepository.getByComic`) added to `OnlineReaderScreen`.
- Bookmark `AssistChip` upgraded: shows count badge ("Bookmarks (N)"); tap toggles panel open/close.
- Separate `+` `IconButton` in toolbar row saves a bookmark at the current page with a Toast confirmation.
- Bookmark panel lists all bookmarks for the current comic: chapter, page, relative time, delete button, and tap-to-jump behaviour.

## Phase 95 - Home Quick Stats Row [done]
- `totalSessionCount: StateFlow<Int>` and `totalReadingMinutes: StateFlow<Long>` added to `HomeViewModel`.
- `HomeScreen` collects both states; shows a 3-tile stats row (Titles · Sessions · Read time) when library or sessions are non-empty.
- Tiles use Ember / Sky / Mint accent colours; hidden in incognito mode.

## Phase 96 - Chapter Download Progress Bar [done]
- `downloadProgress: Float?` parameter added to `OnlineChapterRow` in `CommonComponents.kt`.
- When `downloading = true` and a fraction is provided, a `LinearProgressIndicator` (Ember colour) with a percentage label appears below the reading-progress bar.
- When downloading but no fraction yet, an indeterminate bar is shown.
- `downloadProgressMap: Map<String, Float>` state added to `OnlineDetailsScreen`; per-page `onProgress` callback from `cacheManager.cacheChapter` updates it in real time.

## Phase 97 - Build Verification [done]
- Debug APK rebuilt: Readora-debug.apk (May 9).
- All Kotlin compiled without errors; only deprecation warnings (AutoMirrored icon aliases).

## Phase 98 - Discover Sort Order [done]
- `DiscoverSortOrder` enum added: `Default`, `TitleAZ`, `TitleZA`, `MostTags`.
- `sortOrder` field added to `DiscoverUiState`; `visibleComics` applies the sort after tag filtering.
- `selectSortOrder()` added to `DiscoverViewModel`.
- Four `FilterChip` items (Default / A→Z / Z→A / Most tags) shown as a horizontally scrolling strip in `DiscoverScreen`, always visible.

## Phase 99 - Reader Page Transition Animation [done]
- `pageTransition: String` field ("none"/"slide"/"fade", default "slide") added to `ReadoraSettings` + serializer load/save.
- `setPageTransition()` added to `SettingsViewModel`; three-chip picker added to SettingsScreen under "Page transition".
- Paged-mode reader: each page is wrapped in `AnimatedContent` keyed on the page URL.
  - **Slide**: `slideInHorizontally + fadeIn` ↔ `slideOutHorizontally + fadeOut` (220 ms).
  - **Fade**: `fadeIn` ↔ `fadeOut` (220 ms).
  - **None**: instant swap (0 ms tween).

## Phase 100 - Final Build Verification [done]
- Debug APK rebuilt: Readora-debug.apk (May 9) — all 100 phases complete.
- All Kotlin compiled without errors; only deprecation warnings (AutoMirrored icon aliases).
- Readora 100/100 roadmap complete.

## Phase 101 - Settings Content Language Filter [done]
- `preferredLanguages: List<String>` field already existed in `ReadoraSettings` (default: `["en"]`).
- `togglePreferredLanguage(lang)` helper added to `SettingsViewModel`; ensures at least one language always remains.
- "Content languages" `PremiumPanel` added to `SettingsScreen` with `FilterChip`s for: English, Japanese, Chinese, Korean, Spanish, French, Portuguese, German, Italian, Russian.
- `languageParams()` / `translatedLangParams()` helpers already existed in `MangaDexSource`; all `getPopular`, `getLatest`, `search`, and `getChapterList` overloads now accept a `langs` parameter.
- `DiscoverViewModel.refresh()`, `refreshLatest()`, `loadMorePopular()`, `loadMoreLatest()` all load `preferredLanguages` from settings and pass them to the language-aware MangaDex overloads.

## Phase 102 - Library New Chapter Badge [done]
- `getUnreadCountForComic(comicId)` DAO query added to `UpdateDao` (Room `COUNT(*)` query).
- `getUnreadCountForComic(comicId): Flow<Int>` helper added to `UpdatesRepository`.
- `LibraryScreen` collects all unread updates via `updatesRepository.getAll()` and derives a `newChapterCounts: Map<String, Int>` in a `remember` block.
- `SavedOnlineLibraryRow` extended with `newChapterCount: Int` parameter.
- When `newChapterCount > 0`, a filled Ember-coloured circular badge (showing count, or "9+" for 10+) overlays the top-right corner of the cover thumbnail.

## Phase 103 - Reader Left-Edge Swipe Brightness [done]
- `swipeBrightness: Float` (persisted via `rememberSaveable`) initialised from `settings.defaultBrightness` (or 0.5 if unset).
- `LaunchedEffect(swipeBrightness)` applies `window.attributes.screenBrightness` whenever the value changes.
- Transparent 20%-wide left-edge `Box` overlay uses `detectDragGestures`: vertical drag up/down adjusts brightness (delta = −dragY / screenHeight).
- `brightnessIndicatorVisible` state shown as a small pill on the left edge: sun icon + percentage label; auto-hides after 1.5 s.
- Works in both webtoon and paged reader modes.
- `fillMaxHeight` and `detectDragGestures` imports added to `ReaderScreen.kt`; `Icons.Rounded.Brightness4` icon added.

## Phase 104 - Build Verification [done]
- Debug APK rebuilt: Readora-debug.apk (May 9) — Phases 101-103 verified.
- All Kotlin compiled without errors; only deprecation warnings (AutoMirrored icon aliases).

## Phase 105 - Discover Infinite Scroll [done]
- `rememberLazyListState()` passed to the Discover `LazyColumn`.
- `LaunchedEffect` watches `layoutInfo.totalItemsCount` and last visible item index; auto-calls `loadMorePopular()` / `loadMoreLatest()` when within 4 items of the bottom.
- "Load more" `TextButton`s replaced with passive "Scroll for more" labels; spinner still appears while loading.
- Works for both Popular and Latest tabs; suppressed when a search query is active.

## Phase 106 - Reader Volume Key Navigation (improved) [done]
- Online reader `onPreviewKeyEvent` now distinguishes Up vs Down keys independently.
- **Paged mode**: Volume Up = left tap, Volume Down = right tap (or inverted if setting enabled); uses `leftTap()`/`rightTap()` helpers which respect RTL direction.
- **Webtoon mode**: Volume Up = scroll up, Volume Down = scroll down (600 px × 3 steps per press); direction also respects the "inverted" setting.
- Volume button navigation only fires if `settings.volumeButtonNavigation` is enabled.

## Phase 107 - Details Chapter Read/Unread Toggle [done]
- Already implemented in previous phases (`onToggleRead` lambda, `localReadIds` state, `clearChapterProgress`/`saveChapterProgress` calls).
- Verified correct: long-press on chapter number box or toggle icon marks chapter read/unread and persists immediately.

## Phase 108 - Library Grid View New-Chapter Badge [done]
- Grid-mode cover boxes now derive `gridBadge` from `newChapterCounts` map (same source as list mode).
- When `gridBadge > 0`: a filled Ember circular badge with count (capped "9+") appears in the top-right corner of each grid cover, offset by 4 dp padding.

## Phase 109 - Settings Reader Background Colour [done]
- `readerBackground: String` field ("dark"/"black"/"sepia"/"white", default "dark") added to `ReadoraSettings` + serializer load/save.
- `setReaderBackground()` added to `SettingsViewModel`.
- "Reader background" `PremiumPanel` with four `FilterChip`s (Dark / Black / Sepia / White) added to `SettingsScreen`.
- `readerBgColor` `remember` value derived from `settings.readerBackground`; applied to the main `Column` background and the paged-mode `Box` background in `OnlineReaderScreen`.

## Phase 110 - Build Verification [done]
- Debug APK rebuilt: Readora-debug.apk (May 9) — Phases 105-109 verified.
- All Kotlin compiled without errors; only deprecation warnings (AutoMirrored icon aliases).

## Phase 111 - Home Continue Reading Banner [done]
- `toOnlineChapter()` extension helper added to `LastOnlineRead` in `CommonComponents.kt`.
- `onOpenOnlineReader: (OnlineComicSummary, OnlineChapter) -> Unit` parameter added to `HomeScreen`.
- `LastReadPanel` upgraded with a prominent "Continue" banner button that opens the last-read chapter directly.
- Wired in `MainActivity` to set `onlineReaderChapter` state directly, bypassing the details screen.

## Phase 112 - Details Share Chapter Link [done]
- `onShare: (() -> Unit)?` parameter added to `OnlineChapterRow` in `CommonComponents.kt`.
- Share `IconButton` (IosShare icon) appears below the read/unread toggle per chapter row.
- In `DetailsScreen`: tapping share fires `Intent.ACTION_SEND` with MangaDex chapter URL `https://mangadex.org/chapter/<id>` via `Intent.createChooser`.

## Phase 113 - Reader Long-Press Page Info Popup [done]
- `pageInfoPopup: Pair<String, Int>?` state added to `OnlineReaderScreen`.
- Long-press on a page (both webtoon and paged modes) opens an `AlertDialog` instead of immediately saving.
- Dialog shows page number, chapter info, and full page URL; "Save to gallery" button triggers the existing gallery save flow.

## Phase 114 - Library Bulk Select & Delete [done]
- `bulkSelectMode: Boolean` and `selectedComicIds: Set<String>` states added to `LibraryScreen`.
- Bulk action bar (shown when `bulkSelectMode`) displays selected count, "Select All", and "Delete" button.
- List-mode items: long-press enters bulk mode; in bulk mode, tap toggles selection; swipe-to-dismiss disabled during bulk mode.
- `RadioButtonUnchecked` and `CheckCircle` icons used for selection indicators.
- `onBulkSelect: (() -> Unit)?` parameter added to `SavedOnlineLibraryRow`.

## Phase 115 - Discover Search Suggestions [done]
- `OnlineSearchPanel` extended with `savedSearches: List<String>` and `popularTags: List<String>` parameters.
- `isFocused` state via `Modifier.onFocusChanged` tracks whether the text field has focus.
- When focused, a horizontally scrolling `AssistChip` row appears below the field showing:
  - Recent saved searches that contain the current query (or all when blank).
  - Popular tags from current results that match the query (or all when blank).
- Suggestions capped at 8 items; each chip uses a `Restore` icon for saved searches and `Search` icon for tags.
- Tapping a suggestion chip calls `onQuickSearch` to immediately run that search.
- `onFocusChanged` import added to `DiscoverScreen.kt`.

## Phase 116 - Build Verification [done]
- Debug APK rebuilt: Readora-debug.apk (May 9) — Phases 111-115 verified.
- All Kotlin compiled without errors; only deprecation warnings (AutoMirrored icon aliases).

## Phase 117 - Details User Star Rating [done]
- `saveUserRating(comicId, stars)` / `loadUserRating(comicId)` added to `ReadoraPreferences`.
- `userRating` state added to `OnlineDetailsScreen`, loaded on open with `remember(comic.id)`.
- Row of 5 `IconButton`s in info panel: filled `Star` for rated, `StarOutline` for unrated stars.
- Tapping a star toggles it (tap same star = clear rating to 0); `"$rating/5"` label shown when > 0.
- Rating persisted immediately via SharedPreferences key `user_rating_<comicId>`.

## Phase 118 - Library Reading Status Filter [done]
- `libraryStatusFilter` state added to `LibraryScreen` (default `"All"`).
- All reading statuses pre-loaded into `allReadingStatuses: Map<String, String>` to avoid per-item I/O.
- `displayedOnline` derivation filters by status before applying sort.
- Five `FilterChip`s: All / Reading / Plan to Read / Completed / Untagged.
- Filter bar visible at all times when online library is non-empty.

## Phase 119 - Reader Chapter Notes [done]
- Already fully implemented and verified: `noteDraft` / `noteBoxOpen` states; "Note" `AssistChip` in toolbar.
- Inline `PremiumPanel` with `OutlinedTextField` + Save/Cancel; note saved via `chapterNoteRepository.add()`.

## Phase 120 - Home Daily Recommendation Card [done]
- `todaySeed` / `dailyPick` / `dailyPickSummary` computed at composable scope (above `LazyColumn`) using `YEAR * 1000 + DAY_OF_YEAR` seed.
- `dailyPickSummary` memoized once via `remember(dailyPick)` — single `toOnlineSummary()` allocation.
- `PremiumPanel` shows star icon header, cover, title, source name, latest chapter number, "Open" chip.
- Hidden in incognito mode or when library is empty.

## Phase 121 - Settings Notifications Preferences [done]
- Four new fields added to `ReadoraSettings`: `notifyNewChapters`, `notifyOnlyWifi`, `notifySoundEnabled`, `notifyVibrateEnabled`.
- Persisted through `SettingsSerializer` load/save (JSON keys match field names).
- Four setter functions added to `SettingsViewModel`.
- Dedicated `PremiumPanel` in `SettingsScreen` with: master toggle + collapsible sub-options (Wi-Fi only, Sound, Vibration), each with icon, description, and `Switch`.
- `_uiState` in both `SettingsViewModel` and `DiscoverViewModel` made `private`.

## Phase 122 - Build Verification [done]
- All 10 code-review fixes applied (star icon, LazyColumn crash risk, stale page counter, public MutableStateFlow, main-thread I/O, redundant allocations, duplicate imports, silent errors, orphaned coroutine, stale list on refresh).
- Pre-existing `navArgument` import path corrected for Navigation 2.9.x (`androidx.navigation.navArgument`).
- `findOnlineSource` return type fixed to `OnlineSource`; `source.details(summary)` updated to `source.getDetails(comicId)`.
- Missing `OnlineComicSource` import replaced with correct `OnlineSource` import.
- `initialComic` parameter name corrected at `OnlineDetailsScreen` call site in `MainActivity`.
- Debug APK rebuilt: Readora-debug.apk (May 10) — Phases 117-121 + all fixes verified.
- All Kotlin compiled without errors; only deprecation warnings (AutoMirrored icon aliases).

## Phase 123 - Updates Screen Group-by-Comic View [done]
- Added `groupByComic` state variable and `Group` FilterChip alongside Unread/All chips.
- When toggled, updates are grouped by `comicTitle`; each group renders a `GroupedComicHeader` with cover thumbnail, unread badge count, and expand/collapse toggle.
- Expanded groups show individual `DismissableUpdateRow` entries below the header.
- FQN `androidx.compose.ui.Modifier` references cleaned up to use the imported `Modifier`.

## Phase 124 - Reader Double-Tap Zoom (Paged Mode) [done]
- Confirmed already implemented: `zoomLevel` state variable, animated with `animateFloatAsState`, toggled between 1× and 2.5× on double-tap across all three tap zones (left, center, right) in paged mode.
- HUD hint "Double-tap to reset zoom" shown when `zoomLevel > 1f`.

## Phase 125 - Library Sort by User Rating [done]
- `allUserRatings` map loaded once via `remember(savedOnline)` calling `preferences.loadUserRating(it.id)`.
- `"Rating"` sort case added to `displayedOnline`: sorts descending by stored star rating.
- `"Rating"` FilterChip added to `LibrarySortFilterBar`.

## Phase 126 - Details Chapter Search/Filter Bar [done]
- `chapterSearchQuery` state variable (`rememberSaveable`) added to `OnlineDetailsScreen`.
- `OutlinedTextField` search bar inserted below "Live chapters" section title, only shown once chapters are loaded.
- Chapters filtered by `chapterSearchQuery` matching chapter number or title (case-insensitive).
- Clear (×) trailing icon resets the query; changing the query also resets `chapterLimit` to 60.
- `Icons.Rounded.Close` import added to `DetailsScreen.kt`.

## Phase 127 - Stats Weekly Reading Chart [done]
- `weekDayMinutes: LongArray(7)` state added; populated in `LaunchedEffect` bucketing sessions by calendar day (index 0 = 6 days ago, 6 = today).
- `WeeklyReadingChart` composable added with a `Canvas` drawing 7 rounded-rect bars scaled to the daily max.
- Day-of-week labels (Su/Mo/Tu…) + per-bar minute labels shown below.
- Empty bars use a dim background color; bars with data use Ember accent.
- Added imports: `Canvas`, `CornerRadius`, `Offset`, `Size`, `java.util.Calendar` to `StatsScreen.kt`.

## Phase 128 - Home Recently Updated Titles Strip [done]
- `recentlyUpdatedComicIds: StateFlow<List<String>>` added to `HomeViewModel`; groups unread updates by comicId, sorted by most recent update ID descending.
- `HomeViewModel` constructor updated to accept `UpdatesRepository`; `ReadoraViewModelFactory` updated accordingly.
- `HomeScreen` collects `recentlyUpdatedComicIds` and computes `recentlyUpdated` (cross-referenced with `savedOnlineLibrary`) above the `LazyColumn`.
- "Recently updated" `LazyRow` strip inserted after "My library" strip, hidden in incognito mode and when empty.

## Phase 129 - Build Verification [done]
- All Phases 123-128 compiled without errors; only deprecation warnings (AutoMirrored icon aliases).
- Debug APK rebuilt: Readora-debug.apk (May 10) — Phases 123-128 verified.

## Phase 130 - Reading History Search + Group-by-Comic [done]
- `searchQuery` state added; `OutlinedTextField` search bar with Search/Clear icons inserted below top bar.
- `visibleSessions` derived via `remember(sessions, searchQuery)`, filtering by `comicTitle` or `chapterNumber`.
- `groupByComic` toggle chip added (`FilterChip` row: "All (N)" | "Group by comic").
- Grouped view: collapsible per-comic headers showing cover icon, title, session count, total minutes; `expandedComics: Set<String>` tracks open groups.
- Imported: `Close`, `Search`, `KeyboardArrowDown/Up`, `FilterChip`, `OutlinedTextField`, `horizontalScroll`, `height`, `mutableStateOf/setValue`.

## Phase 131 - Bookmarks Search Bar [done]
- `searchQuery` state added to `BookmarksScreen`.
- `OutlinedTextField` with Search/Clear icons inserted between tab chips and the list.
- Bookmarks filtered by `comicTitle` or `chapterNumber`; notes filtered by `content`, `chapterTitle`, or `chapterNumber`.
- Imported: `Close`, `Search`, `OutlinedTextField`, `horizontalScroll`, `rememberScrollState`.

## Phase 132 - Downloads Swipe-to-Remove + Cancel Running Job [done]
- `DismissableDownloadJobRow` wrapper added: swipe EndToStart triggers `onRemove` via `SwipeToDismissBox`.
- Red delete background revealed on swipe; `SwipeToDismissBox`, `SwipeToDismissBoxValue`, `rememberSwipeToDismissBoxState`, `ExperimentalMaterial3Api` imported.
- `onCancel` callback added to `DownloadJobRow`; running jobs now show both Pause and Cancel (×) buttons.
- Queued jobs show Cancel (×) instead of Delete; cancelled jobs set to `Failed` status with "Cancelled" error message.

## Phase 133 - Discover Multi-Tag Genre Filter [done]
- `DiscoverUiState.selectedTag: String` replaced with `selectedTags: Set<String>`.
- `visibleComics` now filters using `all { }` — comics must match every selected tag simultaneously.
- `selectTag()` toggles a tag in/out of the set; `clearTagFilter()` clears the set.
- Persisting: first tag in set stored to `discoverTagFilter` setting for backward compatibility.
- UI: "✕ Clear all" chip shown when any tags active; "N active" count shown; multi-chip selection highlights correctly; subtitle shows all selected tags joined by " + ".

## Phase 134 - Reader Long-Strip Continuous Progress Bar [done]
- Right-edge thin (3dp wide) vertical progress bar added to webtoon/long-strip mode `Box`.
- Track: semi-transparent white `RoundedCornerShape`; fill: Ember-coloured bar scaled by `fillMaxHeight(animatedFraction)`.
- `animatedFraction` derived from `firstVisibleItemIndex / (pages.size - 1)` and animated via `animateFloatAsState`.
- Always visible in webtoon mode (not HUD-dependent); minimum fill height 3% to keep bar visible at top.

## Phase 135 - Build Verification [done]
- All Phases 130-134 compiled without errors; only deprecation warnings (AutoMirrored icon aliases).
- Debug APK rebuilt: Readora-debug.apk (May 10) — Phases 130-134 verified.

## Phase 136 - Stats Top-Read Comics Leaderboard [done]
- `topComics: List<Pair<String, Long>>` state added to `StatsScreen`; computed in `LaunchedEffect` by grouping sessions by `comicTitle` and summing `durationMs / 60_000L`.
- Leaderboard rendered in a `StatSection("Top read comics")` block: ranked rows with gold/silver/bronze icon for top 3 and rank number chips for the rest.
- Progress bar per comic scaled to the max value; total minutes shown on the right.
- Added imports: `fillMaxHeight`, `TextOverflow` to `StatsScreen.kt`.

## Phase 137 - Library Bulk Status Update [done]
- `bulkStatusMenuOpen: Boolean` state added when `selectedIds.isNotEmpty()`.
- "Set status" `IconButton` (CheckCircle icon) added to the bulk action bar.
- `DropdownMenu` with status options: Reading, Completed, On Hold, Plan to Read, Dropped — each calls `preferences.saveReadingListStatus(id, status)` for all selected IDs.
- `allReadingStatuses: Map<String, String>` computed from `savedOnline` in the library screen to reflect updates immediately.

## Phase 138 - Home Daily Reading Goal Widget [done]
- `todayMinutes: StateFlow<Long>` added to `HomeViewModel`; filters sessions by `startedAt >= todayStart` and sums `durationMs / 60_000L`.
- `HomeScreen` collects `todayMinutes`; `DailyGoalWidget` composable renders a circular `Canvas` progress ring (56 dp) with animated fill, plus a `LinearProgressIndicator` bar and label.
- Widget is hidden in incognito mode and when `dailyGoalMinutes == 0`; shown between "Recently updated" strip and "Smart shelves".
- `dailyGoalMinutes: Int = 30` field added to `ReadoraSettings`, serializer load/save updated.

## Phase 139 - Details Open Chapter in Browser + Copy URL [done]
- Two new optional lambdas added to `OnlineChapterRow`: `onOpenInBrowser` and `onCopyUrl`.
- `Icons.Rounded.OpenInBrowser` and `Icons.Rounded.ContentCopy` icon buttons shown in the chapter row action column.
- `DetailsScreen` wires `onOpenInBrowser` via `Intent.ACTION_VIEW` with the MangaDex chapter URL.
- `onCopyUrl` copies the URL to `ClipboardManager` and shows a short Toast.
- Added imports: `ClipData`, `ClipboardManager`, `Context` to `DetailsScreen.kt`; `ContentCopy`, `OpenInBrowser` to `CommonComponents.kt`.

## Phase 140 - Settings Daily Reading Goal Slider [done]
- `setDailyGoalMinutes(value: Int)` method added to `SettingsViewModel` (clamped 5–240 min).
- New `PremiumPanel` item added above "Reading time" in `SettingsScreen` with a `Slider` (range 5–240 min, 46 steps), live label showing current goal, and min/max endpoint labels (5 min / 4 hrs).

## Phase 141 - Build Verification [done]
- All Phases 136-140 compiled without errors; only deprecation warnings (AutoMirrored icon aliases).
- Debug APK rebuilt: Readora-debug.apk (May 10) — Phases 136-140 verified.

## Phase 142 - Reader Page-Jump Slider [done]
- `pageJumpVisible: Boolean` state added alongside `hudVisible`; `showHudBriefly()` resets it to false.
- Page counter pill in all three modes (paged_double, paged, webtoon) is now `clickable` — tapping toggles `pageJumpVisible`.
- When `pageJumpVisible` is true a `Slider` appears above the pill: range 1..pages.size, steps = pages.size-2, dragging calls `goToPage(it.toInt())`.
- `Slider` import added to `ReaderScreen.kt`.

## Phase 143 - Library Status Filter Counts + On Hold / Dropped [done]
- `statusCounts` map computed via `remember(savedOnline, allReadingStatuses)` — counts per status key.
- Filter chips updated: each label now includes the count in parentheses e.g. "Reading (3)".
- Two new chips added: "On Hold" (`on_hold`) and "Dropped" (`dropped`).
- Bulk status dropdown also updated to include "On Hold" and "Dropped" options.

## Phase 144 - Updates Per-Comic Mark-All-Read [done]
- `markAllReadForComic(comicId: String)` added to `UpdateDao` (`@Query UPDATE ... WHERE comicId = :comicId`), `UpdatesRepository`, and `UpdatesViewModel`.
- `GroupedComicHeader` gained optional `onMarkAllRead` callback; when set and `unreadCount > 0`, shows a small Mint `CheckCircle` `IconButton`.
- Callback wired in the grouped list: `comicId` extracted from the first entry in each group.

## Phase 145 - Details Chapter Count + Unread Badge [done]
- "Live chapters" `SectionTitle` replaced with a richer `Row` showing total count, read count, and unread count in the subtitle.
- An Ember "N new" badge box shown on the right when `unreadCount > 0`, computed as `totalChapters - readCount`.
- `readCount` derived from `localReadIds.intersect(details!!.chapters.map { it.id }.toSet()).size`.

## Phase 146 - Search Suggestion Chips [done]
- `suggestions` list computed via `remember(uiState.query, uiState.recentSearches)` — saved searches that contain the current query substring (excluding exact matches), capped at 6.
- Shown as a horizontally-scrollable `FilterChip` row between the search bar and loading indicator when query is non-blank.
- Tapping a chip calls `onQueryChanged(suggestion)`, filling the field and triggering a search.
- `remember` and `rememberScrollState` imports added to `SearchScreen.kt`.

## Phase 147 - Stats Reading Heatmap Calendar [done]
- `heatmapMinutes: LongArray(91)` state added; populated in `LaunchedEffect` from sessions in the last 91 days (index 0 = 90 days ago, 90 = today).
- `ReadingHeatmap` composable: 13-column × 7-row grid of 13dp cells aligned to current day-of-week; cells coloured by Ember intensity (4 levels + empty).
- Today aligns to its correct day-of-week row; future cells rendered as dim placeholders.
- Legend row at bottom with "Less → More" gradient swatches.
- `StatSection("Activity heatmap")` wrapper added. Item inserted after `WeeklyReadingChart`.

## Phase 148 - Build Verification [done]
- All Phases 142-147 compiled without errors; only deprecation warnings (AutoMirrored icon aliases).
- Fixed: `remember` import missing in `SearchScreen.kt`; unused variables removed from heatmap.
- Debug APK rebuilt: Readora-debug.apk (May 10) — Phases 142-147 verified.

## Phase 149 - Reader Live Session Timer [done]
- `elapsedSeconds` state added to `ReaderScreen.kt` with a `LaunchedEffect` that ticks every second while the composable is active.
- Timer pill displayed in the top-bar subtitle area as "▶ Xm Ys" when elapsed time > 0.
- Resets naturally on screen re-composition (new comic/chapter navigation).

## Phase 150 - Library Custom Shelves [done]
- `UserShelf(id, name, comicIds)` data class added to `ReadoraPreferences.kt`.
- `loadShelves()`, `saveShelves()`, `createShelf()`, `renameShelf()`, `deleteShelf()`, `toggleComicInShelf()` CRUD methods added.
- Shelves section in `LibraryScreen` LazyColumn: create new shelf (AlertDialog), rename/delete per shelf, per-shelf horizontally-scrollable comic strip.
- Comic-assignment picker panel: toggle comics in/out of any shelf via a `shelfPickerComicId` state.
- Fixed nested `LazyRow` inside `item {}` — replaced with `Row` + `horizontalScroll`.

## Phase 151 - Discover Genre Browse Grid [done]
- `GenreBrowseGrid` composable added to `DiscoverScreen.kt`.
- 12 genre tiles arranged in a 3-column grid (chunked rows of `Box` composables), each with an accent color and icon.
- Selected tiles highlight with full accent color + white text; inactive tiles show accent at 18% opacity.
- Tapping a tile calls `viewModel.selectTag(genre)` — wires into the existing tag filter pipeline.
- Panel shown only when `uiState.submittedQuery.isBlank()`; includes a "Clear" button when tags are active.
- `private data class GenreTile` + `private val genreTiles` list added as file-level declarations.

## Phase 152 - Settings Clear Reading History [done]
- `@Query("DELETE FROM reading_sessions")` `deleteAll()` added to `ReadingSessionDao`.
- `clearAll()` added to `ReadingSessionRepository`.
- `SettingsViewModel` gains optional `readingSessionRepository` parameter + `clearReadingHistory()` method.
- `ReadoraViewModelFactory` updated to pass `container.readingSessionRepository` to `SettingsViewModel`.
- `AlertDialog` import added to `SettingsScreen.kt`.
- "Reading history" `PremiumPanel` added with destructive red "Clear reading history" button → shows `AlertDialog` for confirmation → calls `settingsViewModel.clearReadingHistory()` + Toast on confirm.

## Phase 153 - Stats Session Insights [done]
- `longestSessionMs`, `avgSessionMs`, `longestSessionTitle` state variables added to `StatsScreen.kt`.
- Computed in `LaunchedEffect`: longest = `maxByOrNull { durationMs }`, average = total / count.
- "Session insights" `StatSection` added after the top-read leaderboard:
  - Largest red-tinted pill showing longest session duration + comic title.
  - Two-column row: "Avg session" (time) and "Est. pages/session" (~10 pg/min midpoint estimate).
- Section only shown when `sessionCount > 0`.

## Phase 154 - Home Onboarding Empty-State [done]
- Previous plain "Get started" `PremiumPanel` replaced with a rich `OnboardingPanel` composable.
- Welcome header with circular icon, app name and tagline.
- Three numbered step cards: Discover → Build library → Read & track (with icons and descriptions).
- Feature highlights list (5 items) with Mint icon bullets.
- Demo-comics note explaining the offline sample data below.
- `OnboardingPanel` extracted as a standalone `@Composable` at the bottom of `HomeScreen.kt`.

## Phase 155 - Details Scanlator Badge + Language Tag [done]
- `language: String? = null` field added to `OnlineChapter` data class in `OnlineModels.kt`.
- `translatedLanguage` parsed from MangaDex chapter feed response in `MangaDexSource.kt`.
- `OnlineChapterRow` in `CommonComponents.kt` updated:
  - New badge row shown when `scanlator` or `language` is non-blank.
  - Language: small Sky-tinted rounded pill displaying the language code in uppercase (e.g. "EN").
  - Scanlator: muted gray text beside the language pill, truncated to 1 line.
  - Subtitle text simplified: scanlator no longer duplicated in the `buildString` — now shown only in the badge row.

## Phase 156 - Build Verification [done]
- All Phases 149-155 compiled without errors; only deprecation warnings (AutoMirrored icon aliases).
- Fixed: `LazyRow` + `items` inside `item {}` in LibraryScreen replaced with `Row` + `horizontalScroll`.
- Debug APK rebuilt: Readora-debug.apk (May 10) — Phases 149-155 verified.

## Phase 157 - Reader Brightness Overlay Slider in HUD [done]
- `brightnessSliderVisible` state toggle via `Brightness4` icon button in HUD.
- `Slider` (0.01–1.0) synced with `swipeBrightness` state; added to all 3 reader mode HUDs.
- File: `ReaderScreen.kt`

## Phase 158 - Library Sort Options [done]
- Added `librarySortOrder` state: `"default"`, `"title_az"`, `"title_za"`, `"last_read"`, `"date_added"`.
- Sort chips row in library filter panel; `savedOnline` re-sorted via `remember`.
- File: `LibraryScreen.kt`

## Phase 159 - Discover Source Info Panel [done]
- `PremiumPanel` in DiscoverScreen listing connected sources from `SettingsViewModel.sources` StateFlow.
- Shows name, version, language, enabled/disabled badge, `SourceKind`.
- File: `DiscoverScreen.kt`

## Phase 160 - Details Reading Progress Resume Banner [done]
- Resume banner after description panel; shows first unread/in-progress chapter with PlayArrow button.
- `LinearProgressIndicator` + "Page X/Y" when partially read.
- File: `DetailsScreen.kt`

## Phase 161 - Updates Date-Grouped Sections [done]
- Three sections: "Today", "This week", "Earlier" with `DateSectionHeader` items.
- Bucketing uses `update.foundAt` timestamp; added `import java.util.Calendar`.
- File: `UpdatesScreen.kt`

## Phase 162 - Stats Per-Weekday Average Bar Chart [done]
- `WeekdayBarChart` composable with max-scaled coloured bars (Mon–Sun).
- Computed from all-time sessions: sum per weekday / occurrences for average minutes.
- Inserted in StatsScreen LazyColumn after the heatmap.
- File: `StatsScreen.kt`

## Phase 163 - Settings Reader Font Scale [done]
- `readerFontScale: Float = 1.0f` added to `ReadoraSettings`; persisted in JSON.
- `Slider` (0.75–1.5, 14 steps) in SettingsScreen; applied to reader top-bar title/subtitle font sizes.
- Files: `SettingsSerializer.kt`, `SettingsViewModel.kt`, `SettingsScreen.kt`, `ReaderScreen.kt`

## Phase 164 - Home Reading Streak Milestone Banner [done]
- `StreakMilestoneBanner` composable replaces simple streak panel.
- Seven milestone tiers: 3, 7, 14, 21, 30, 50, 100 days — each with unique emoji, label, subtitle, accent colour.
- Exact milestone day: gradient border highlight + tinted background; otherwise neutral dark card.
- Streak count shown in accent-coloured pill on the right.
- File: `HomeScreen.kt`

## Phase 165 - Build Verification [done]
- All Phases 157-164 compiled without errors; only pre-existing deprecation warnings.
- Debug APK rebuilt: Readora-debug.apk (May 10) — Phases 157-164 verified.

## Phase 166 - Reader Chapter Navigation Drawer [done]
- `FormatListBulleted` icon button in reader top bar (only shown when chapter list provided).
- Full-screen bottom-sheet overlay listing all chapters with status dots (current/read/in-progress/unread).
- Current chapter highlighted in Ember; reading progress shown inline; tapping navigates immediately.
- File: `ReaderScreen.kt`

## Phase 167 - Library Bulk Select + Multi-Delete [done]
- Already fully implemented: long-press card → bulk mode, checkboxes, delete/shelf actions in toolbar.
- Verified working — no additional code needed.

## Phase 168 - Discover Recently Viewed Titles Strip [done]
- `addRecentlyViewed` / `loadRecentlyViewed` (up to 20) added to `ReadoraPreferences`.
- Horizontal `LazyRow` strip of cover thumbnails shown above the search panel in DiscoverScreen.
- Opening any comic (search results or popular grid) calls `openOnlineComicTracked()` to track.
- Files: `ReadoraPreferences.kt`, `DiscoverScreen.kt`

## Phase 169 - Details Chapter Notes Preview Inline [done]
- `noteSnippet: String? = null` parameter added to `OnlineChapterRow`.
- Amber pill badge with Edit icon shows first note content snippet inline in the chapter row.
- DetailsScreen passes `notes.firstOrNull { it.chapterId == chapter.id }?.content`.
- Files: `CommonComponents.kt`, `DetailsScreen.kt`

## Phase 170 - Stats Daily Goal Progress Ring [done]
- `DailyGoalRing` composable: animated arc ring (fill fraction = today / goal).
- Colour: Mint when goal exceeded, Ember otherwise; "Goal crushed!" badge with surplus minutes.
- Reads `settings.dailyGoalMinutes` via `SettingsViewModel`; only shown when goal > 0.
- Files: `StatsScreen.kt`

## Phase 171 - Settings Accent Colour Picker Enhanced [done]
- Expanded from 5 to 8 swatches: added Violet, Rose, Ice.
- Two rows of 4 swatches, each with a named label below and a checkmark overlay when selected.
- File: `SettingsScreen.kt`

## Phase 172 - Home Continue Reading Carousel [done]
- `inProgressTitles` computed from `savedOnlineLibrary` filtered by partially-read last chapter.
- Horizontal `LazyRow` in a `PremiumPanel`, showing cover + progress bar overlay + chapter/page info.
- Shown in "In progress" section between streak banner and existing continue-reading items.
- File: `HomeScreen.kt`

## Phase 173 - Updates Mark All as Read [done]
- Already fully implemented: "Mark read", "Del read", "Clear all" buttons in UpdatesScreen toolbar.
- Verified working — no additional code needed.

## Phase 174 - Build Verification [done]
- All Phases 166-172 compiled without errors; only pre-existing AutoMirrored deprecation warnings.
- Debug APK rebuilt: Readora-debug.apk (May 10) — Phases 166-172 verified.

## Phase 175 - Reader Double-Tap to Bookmark Page [done]
- Double-tap gesture in paged and webtoon modes triggers `bookmarkCurrentPage()`.
- Haptic feedback (long-press vibration) + Toast confirmation on bookmark save.
- File: `ReaderScreen.kt`

## Phase 176 - Library Reading Progress Bar on Card [done]
- `readFraction: Float` parameter added to `SavedOnlineLibraryRow`.
- Slim `LinearProgressIndicator` (Ember, height 3.dp) shown below row when `readFraction > 0`.
- Computed from `preferences.loadChapterProgress()` across all chapters for each title.
- File: `LibraryScreen.kt`

## Phase 177 - Details Similar/Related Titles Navigation [done]
- "You might also like" LazyRow cards are tappable.
- Tapping updates the `comic` state in-place to navigate within the same DetailsScreen.
- File: `DetailsScreen.kt`

## Phase 178 - Home Personalized Greeting with Time of Day [done]
- Time-of-day greeting replaces static subtitle: "Good morning" / "Good afternoon" / "Good evening" / "Night owl mode".
- Uses `Calendar.HOUR_OF_DAY` at composable entry.
- File: `HomeScreen.kt`

## Phase 179 - Stats Total Pages Read Counter [done]
- `totalPagesEst` computed as `allTimeMinutes * 10` (rough 10 pages/min estimate).
- Second stats row in "Reading" section: "Est. pages" BigStatCard + "Avg/session" card.
- File: `StatsScreen.kt`

## Phase 180 - Settings Update Check Schedule Toggle [done]
- Already fully implemented with `autoUpdateLibrary` toggle and interval chips (6h/12h/24h/48h).
- Enhanced: 72h option added to interval chip row.
- Files: `SettingsScreen.kt`

## Phase 181 - Discover Tag Cloud Genre Drill-Down [done]
- Cloud mode toggle button (grid icon) in "Filter by genre" panel header.
- Cloud mode shows tags in wrapped rows with font-size variation (10–15sp) based on position.
- Selected tags get Ember accent; unselected get subdued style.
- File: `DiscoverScreen.kt`

## Phase 182 - Reader Sepia / Night Reading Tint Mode [done]
- `readingTintMode` state cycles: 0 (off) → 1 (sepia) → 2 (night).
- `tintOverlayColor`: sepia = warm amber at 18% alpha; night = deep navy at 28% alpha.
- Tint cycle `FilterChip` (Palette icon) in mode/direction strip: "☀️ Off" / "📜 Sepia" / "🌙 Night".
- Overlay `Box` with `Modifier.matchParentSize()` placed as last child of the outer brightness `Box` so it correctly layers over all page content in all three reader modes.
- File: `ReaderScreen.kt`

## Phase 183 - Build Verification [done]
- All Phases 175-182 compiled without errors; only pre-existing AutoMirrored deprecation warnings.
- Debug APK rebuilt: Readora-debug.apk (May 10) — Phases 175-182 verified.

## Phase 184 - Library Last-Read / Added Date Label on Card [done]
- `lastReadAt: Long? = null` parameter added to `SavedOnlineLibraryRow`.
- Small date text below tags row: "Read Xm/h/d ago" or "Last read MMM d" for read titles; "Added MMM d, yyyy" for unread ones.
- Relative formatting using `TimeUnit` helpers; fallback to absolute `SimpleDateFormat`.
- `lastReadAt` passed from `lastReadMap[comic.id]` at both list-mode call sites.
- Imports added: `SimpleDateFormat`, `Date`, `Locale`, `TimeUnit`.
- File: `LibraryScreen.kt`

## Phase 185 - Discover Comic Quick-Preview Dialog [done]
- Long-pressing any `OnlineDiscoveryCard` (popular, latest, search) opens a compact preview `AlertDialog`.
- Preview shows: cover thumbnail, title, source pill, status pill, description (5 lines), tags (5 max).
- "Open" button navigates to details; "Dismiss" closes without navigating.
- `OnlineDiscoveryCard` updated with optional `onLongClick` param using `combinedClickable`.
- `previewComic: OnlineComicSummary?` state added to `DiscoverScreen`.
- `AlertDialog` + `ExperimentalFoundationApi` imports added to `DiscoverScreen.kt`.
- File: `DiscoverScreen.kt`

## Phase 186 - Reader Pinch-to-Zoom in Webtoon Mode [done]
- `webtoonZoom: Float` state (1f default, range 0.75–3.0) added to `OnlineReaderScreen`.
- `animateFloatAsState` for smooth animated zoom transitions.
- `detectTransformGestures` on the webtoon outer `Box` processes pinch events.
- `graphicsLayer { scaleX/Y = animatedWebtoonZoom }` applied to the `LazyColumn`.
- `webtoonZoom` reset to 1f on chapter change via `LaunchedEffect(chapter.id, refreshKey)`.
- `detectTransformGestures` import added to `ReaderScreen.kt`.
- File: `ReaderScreen.kt`

## Phase 187 - Home Reading Goal Linear Progress Bar [done]
- Compact goal banner added between the streak banner and the in-progress carousel.
- Shows: Star icon (Mint when done, Ember otherwise), label, "Xm / Ym" counter, slim `LinearProgressIndicator`.
- Animated via `animateFloatAsState`; color transitions to Mint when goal reached.
- Only shown when `settings.dailyGoalMinutes > 0` and not in incognito mode.
- File: `HomeScreen.kt`

## Phase 188 - Details Chapter Count + Estimated Reading Time [done]
- Chapter section subtitle extended: "N chapters · X read · Y unread · ~Zh Mm to read".
- Estimated at 7 min/chapter; formatted as hours+minutes when ≥ 60 min.
- File: `DetailsScreen.kt`

## Phase 189 - Settings Data Export Summary Stats Panel [done]
- `exportSummary` state populated in `LaunchedEffect(Unit)`: library count, session count, bookmark count, note count.
- "What's in your backup" `PremiumPanel` shown below `BackupCenterPanel` when data loaded.
- Four tiles (2-column-style row): each shows count in Ember + label below.
- Reads from `preferences.loadOnlineLibrary()`, `readingSessionRepository.getAll()`, `bookmarkRepository.getAll()`, `chapterNoteRepository.getAll()`.
- File: `SettingsScreen.kt`

## Phase 190 - Build Verification [done]
- All Phases 184-189 compiled without errors; only pre-existing AutoMirrored deprecation warnings.
- Debug APK rebuilt: Readora-debug.apk (May 10) — Phases 184-189 verified.

## Phase 191 - Library Reading Status Color-Coded Left Border [done]
- `readingStatus: String = ""` parameter added to `SavedOnlineLibraryRow`.
- Vertical 4dp accent bar rendered on left edge of card inside the content Row.
- Status → color mapping: reading=Sky, completed=Mint, plan=Ember, on_hold=amber, dropped=muted red; unset=invisible.
- `readingStatus = allReadingStatuses[comic.id] ?: ""` passed at both list-mode call sites.
- File: `LibraryScreen.kt`

## Phase 192 - Home Library Stats Mini-Dashboard Strip [done]
- 4-tile stats row (Saved / Reading / Finished / Read time) replaces previous 3-tile strip.
- `readingCount` and `completedCount` computed from `preferences.loadReadingListStatus()` per title.
- Each tile: `Color(0xFF1A1B22)` card, bold count, muted label; compact 8dp gaps for 4-column fit.
- File: `HomeScreen.kt`

## Phase 193 - Reader Long-Strip Scroll-to-Top Button [done]
- Semi-transparent `Box` FAB appears top-right in webtoon mode when `firstVisibleItemIndex >= 3`.
- Tapping calls `listState.animateScrollToItem(0)` via coroutine scope.
- `KeyboardArrowUp` icon; `Icons.Rounded.KeyboardArrowUp` import added.
- File: `ReaderScreen.kt`

## Phase 194 - Discover Pinned Search Bookmarks Strip [done]
- `pinnedSearches: List<String>` field added to `ReadoraSettings`; load/save in `SettingsSerializer`.
- `togglePinnedSearch(query)` helper added to `SettingsViewModel` (add/remove, max 10).
- Pinned searches strip shown between saved searches panel and genre filter in `DiscoverScreen`.
- Each pinned chip taps to quick-search; "Pin current" / "Unpin" button appears when query active.
- "Pin this search" `TextButton` row also shown when no pinned searches yet but query is non-empty.
- Files: `SettingsSerializer.kt`, `SettingsViewModel.kt`, `DiscoverScreen.kt`

## Phase 195 - Stats Monthly Calendar Heatmap View [done]
- `monthCalMinutes: LongArray(31)`, `monthCalDaysInMonth`, `monthCalFirstDow` state added.
- Populated in `LaunchedEffect(Unit)` by bucketing current-month sessions per day-of-month.
- `MonthCalendarView` composable: 7-column calendar grid with day-of-week headers, day numbers inside cells.
- Cell colors: transparent (blank), `Color(0xFF1E2030)` (no reading), Ember 30/60/100% (low/mid/high), Sky (today).
- Legend row below grid; `aspectRatio` + `TextAlign` imports added.
- File: `StatsScreen.kt`

## Phase 196 - Settings Reader Background Visual Swatches [done]
- Text-only `FilterChip` row replaced with 4 colored swatch boxes (Dark/Black/Sepia/White).
- Each swatch uses the actual background color as its fill; Ember 2dp border when selected; CheckCircle icon overlay.
- Text color adapts to background (light text on dark, dark text on white).
- File: `SettingsScreen.kt`

## Phase 197 - Build Verification [done]
- All Phases 191-196 compiled without errors; only pre-existing AutoMirrored deprecation warnings.
- Debug APK rebuilt: Readora-debug.apk (May 10) — Phases 191-196 verified.

## Phase 198 - Details Comic Tags Drill-Down to Discover [done]
- `onTagSearch: ((String) -> Unit)? = null` parameter added to `OnlineDetailsScreen`.
- Tags in the info panel rendered as clickable `Pill`s with semi-transparent Ember highlight when `onTagSearch` is set.
- `pendingDiscoverTag: String?` state added to `ReadoraApp` in `MainActivity.kt`.
- `LaunchedEffect(pendingDiscoverTag)` navigates to Discover tab when a tag is set.
- `OnlineDetailsRoute` extended with `onTagSearch` param; wired at both call sites (overlay and NavHost Details route).
- Discover NavHost composable passes `initialTagFilter = pendingDiscoverTag`; clears it after passing.
- `DiscoverScreen` `initialTagFilter` already calls `viewModel.selectTag(tag)` on first render.
- Files: `DetailsScreen.kt`, `MainActivity.kt`, `DiscoverScreen.kt`

## Phase 199 - Library Quick-Shelf Move from Context Menu [done]
- `shelves: List<UserShelf> = emptyList()` and `onToggleShelf: ((String) -> Unit)? = null` parameters added to `SavedOnlineLibraryRow`.
- "Move to shelf" `DropdownMenuItem` added (only shown when shelves are non-empty); opens a nested `DropdownMenu` listing all user shelves.
- Each shelf row shows a `CheckCircle` (Mint) / `RadioButtonUnchecked` icon indicating whether the comic is currently on that shelf.
- Tapping a shelf row calls `preferences.toggleComicInShelf(shelfId, comic.id)` and refreshes `userShelves`.
- `Icons.Rounded.ChevronRight` import added; `userShelves` and toggle callback passed at the swipe-to-dismiss call site.
- File: `LibraryScreen.kt`

## Phase 200 - Reader Chapter End Summary Card [done]
- `atLastPage` derived state: `pages.isNotEmpty() && currentPage >= pages.size`.
- When on the last page and chapter list + navigation are available, a full-width summary card appears below the footer.
- Card shows: "Chapter N complete!" in Mint, page count + minutes read, and two buttons — "Next chapter" (navigates via `onNavigateChapter`) and "Back" (calls `onBack`).
- `OutlinedButton` import added; `elapsedSeconds` already tracked by the session timer.
- File: `ReaderScreen.kt`

## Phase 201 - Home Unread Updates Count Badge on Nav Bar [done]
- `updatesRepository.getUnreadCount()` collected as `unreadUpdatesCount: Int` state in `ReadoraApp`.
- `updatesCount: Int = 0` parameter added to `ReadoraBottomBar`.
- Updates tab icon wrapped in `BadgedBox` + `Badge` when `updatesCount > 0`; shows count (capped at "99+").
- `Badge`, `BadgedBox` imports added to `MainActivity.kt`.
- Files: `MainActivity.kt`

## Phase 202 - Stats Longest Reading Streak Counter [done]
- `currentStreak` and `longestStreak` state variables added to `StatsScreen`.
- Streak computed from reading sessions: unique midnight-aligned reading days → consecutive-day run algorithm.
- Current streak: counts backward from today (or yesterday as fallback anchor); longest streak: max consecutive run in full sorted day set.
- Two `BigStatCard` tiles shown in the Reading section when either streak > 0: "Current streak" (Ember, "Nd 🔥") and "Longest streak" (Mint, "Nd").
- File: `StatsScreen.kt`

## Phase 203 - Discover Sort by Release Year Filter [done]
- `year: Int? = null` field added to `OnlineComicSummary` data class.
- `attr.optInt("year", 0)` parsed in `MangaDexSource.parseMangaSummary()`.
- `DiscoverSortOrder` enum extended with `YearNewest` and `YearOldest`.
- `visibleComics` in `DiscoverUiState` applies the new sort cases (nulls sorted last / first respectively).
- Two new `FilterChip` items added to the sort strip: "Year ↓" (newest) and "Year ↑" (oldest).
- Year shown as a Sky-tinted `Pill` on `OnlineDiscoveryCard` tags row when available.
- Files: `OnlineModels.kt`, `MangaDexSource.kt`, `DiscoverViewModel.kt`, `DiscoverScreen.kt`

## Phase 204 - Build Verification [done]
- All Phases 198-203 compiled without errors; only pre-existing AutoMirrored deprecation warnings.
- Debug APK rebuilt: Readora-debug.apk (May 10) — Phases 198-203 verified.
