# READORA — PHASE IMPLEMENTATION ROADMAP
# Top 10 Priority Phases (Post-Foundation)
## Synthesized from: READORA_ULTIMATE_MASTER_PLAN.md + MISSING_FEATURES_ANALYSIS.md + MASTER_PROMPT_DEVIN.md

**Last Updated:** May 9, 2026  
**Current Status:** Phases 1-40 Complete (Core Architecture + Basic Reader + MangaDex Source)  
**Next Phase to Start:** Phase 41 (See below)

---

## EXECUTIVE SUMMARY

The original 150-phase plan + GPT-5.5 framework is solid but missed 25+ critical features that separate a "good" app from a "10/10" app. This roadmap identifies the TOP 10 phases that will have maximum impact and user satisfaction.

**Philosophy:** Better to do 10 phases PERFECTLY than 50 phases half-baked.

**Estimated timeline:** 2-3 months at 2 phases per week (professional pace)

---

## PRIORITY RANKING METHODOLOGY

Each phase scored on:
- **User Impact** (1-10): How much users will notice/love this
- **Complexity** (1-10): How hard to implement  
- **Dependencies** (which phases must complete first)
- **Competitive Advantage** (vs Mihon, Kotatsu, Komikku)

Phases ranked by: `(User Impact × Competitive Advantage) / Complexity`

---

## TOP 10 PHASES

### PHASE 41 — Global Multi-Source Search (Kotatsu Killer Feature)

**Priority Score:** 9.8/10  
**User Impact:** 9/10 | **Complexity:** 6/10 | **Competitive Advantage:** 9/10  
**Dependencies:** Phase 35 (Source Registry complete)  
**Estimated Work:** 8-10 days

**What:** One search bar searches ALL enabled sources simultaneously. Results stream in as they load.

**Why:** Kotatsu's standout feature. Mihon searches one source at a time. This is THE differentiator for users who read across multiple sources (MangaDex, Mangakakalot, etc.).

**Deliverables:**
- SearchViewModel with multi-source query dispatch
- Parallel source API calls using async/await
- Result aggregation & deduplication (same title from 3 sources = 1 card with badges)
- StreamUI: "Loading from Source A..." / "5 results from Source B" / "Error in Source C"
- Dedicated GlobalSearchScreen in Discover tab
- Search history + saved searches
- "Merge all sources" quick action on deduplicated result

**Success Criteria:**
- Search 5 sources simultaneously, aggregate in <3 seconds
- Graceful handling of source failures (one source fails, others still show)
- UI reflects loading state per source
- APK size increase <2MB

---

### PHASE 42 — Tracker Integration (MAL + AniList) - CRITICAL TABLE STAKES

**Priority Score:** 9.5/10  
**User Impact:** 10/10 | **Complexity:** 7/10 | **Competitive Advantage:** 8/10  
**Dependencies:** Phase 24 (ViewModel foundation), Phase 36 (Error handling)  
**Estimated Work:** 12-15 days

**What:** MyAnimeList + AniList OAuth2 login, auto-sync reading progress, show status badges in Library.

**Why:** EVERY serious manga app has this. Its absence is Readora's biggest gap vs competition. Users expect their MAL list to stay in sync.

**Deliverables:**
- OAuth2 flow for MAL (com.myanimelist.app)
- OAuth2 flow for AniList (graphql.anilist.co)
- TrackerRepository in data/repository/
- Auto-mark chapter as read → update tracker
- Finished last chapter → prompt "Mark Complete?"
- Library status badge: Reading (green) / Completed (gray) / Planned (blue) / Dropped (red)
- Settings > Trackers: link/unlink accounts
- Sync status: "Last synced 2 hours ago" + manual sync button
- Two-way sync option: allow desktop MAL changes to reflect on phone

**Success Criteria:**
- Login flow completes in <10 seconds
- Reading progress syncs within 30 seconds of marking read
- Handles offline gracefully (queue syncs when online)
- APK size increase <3MB (+ 1MB per tracker)
- Zero crashes on sync failures

---

### PHASE 43 — Cross-Device Google Drive Sync

**Priority Score:** 9.3/10  
**User Impact:** 9/10 | **Complexity:** 8/10 | **Competitive Advantage:** 9/10  
**Dependencies:** Phase 21 (Room Database), Phase 23 (Repositories)  
**Estimated Work:** 14-18 days

**What:** Auto-backup library + reading position + history to Google Drive. Multi-device sync.

**Why:** THE #1 community complaint across all reader apps. Users want to switch from phone to tablet and continue seamlessly.

**Deliverables:**
- Google Play Services integration
- BackupRepository handles Drive API
- ScheduledWorker for auto-backup (6h / 24h / weekly, user configurable)
- Sync reading position: open on phone at page 50 → open on tablet → auto-jump to page 50
- Sync library state (add/remove from library syncs across devices)
- History sync (what you read on phone shows in history on tablet)
- Manual sync button in Settings
- Conflict resolution UI: "This device is newer / Other device is newer — which to keep?"
- Restore from Drive on first launch
- Optional self-hosted sync (WebDAV/Nextcloud for privacy users)

**Success Criteria:**
- Backup completes in <30 seconds for 100 titles
- Sync on app resume detects & resolves conflicts in <5 seconds
- Works offline (queues sync for when online)
- Handles network failures gracefully
- Zero data loss scenarios

---

### PHASE 44 — Automatic Webtoon/Manhwa Detection + Auto-Switch Reader Mode

**Priority Score:** 8.7/10  
**User Impact:** 8/10 | **Complexity:** 4/10 | **Competitive Advantage:** 8/10  
**Dependencies:** Phase 64 (Reader Upgrade)  
**Estimated Work:** 3-5 days

**What:** Open a webtoon chapter → automatically switch to vertical scroll mode. Override per-title if needed.

**Why:** TachiyomiSY pioneered this. Users hate manually toggling reader mode every time they open a manhwa. This feels intelligent.

**Deliverables:**
- ReaderViewModel detects first page aspect ratio (width/height)
- If height > 3× width → auto-switch to webtoon mode
- Toast: "Switched to Webtoon mode — tap to change"
- Source tags (manhwa/manhua sources) → default to webtoon
- Per-title override saved in database
- Remember user's override on subsequent visits

**Success Criteria:**
- Detection happens in <500ms on page load
- Override persists across app restarts
- No false positives (e.g., tall single-page art)

---

### PHASE 45 — Incognito / Private Reading Mode

**Priority Score:** 8.2/10  
**User Impact:** 7/10 | **Complexity:** 3/10 | **Competitive Advantage:** 7/10  
**Dependencies:** Phase 29 (Settings Storage)  
**Estimated Work:** 2-4 days

**What:** Toggle in reader ⋯ menu or quick settings. While active: no history saved, no progress tracked, visual indicator (amber status bar icon).

**Why:** Privacy feature for shared devices, mixed libraries (normal + adult content), or just keeping reading private.

**Deliverables:**
- ReadoraSettings.incognitoMode boolean flag
- Quick toggle in reader controls top bar
- Amber/orange tint on status bar + "Incognito" icon in toolbar
- While active: ProgressRepository.saveProgress() → no-op
- While active: ReadingSessionRepository → not called
- While active: no chapter added to history
- Auto-disable on app close (optional: auto-re-enable after lock screen)
- Category-level incognito: set category as "incognito only" in Settings

**Success Criteria:**
- Toggle works instantaneously
- Proves no data is saved by checking DB before/after reading in incognito mode
- Visual indicator always visible

---

### PHASE 46 — Updates Feed Screen (New Chapters at a Glance)

**Priority Score:** 8.5/10  
**User Impact:** 8/10 | **Complexity:** 5/10 | **Competitive Advantage:** 8/10  
**Dependencies:** Phase 99 (Background Update Checks)  
**Estimated Work:** 5-7 days

**What:** Dedicated tab showing "What's new today?" — all new chapters found in last library update, grouped by source, sorted by time.

**Why:** Kotatsu/Mihon have this. Readora should too. Users want a changelog of new chapters, not just progress updates.

**Deliverables:**
- UpdateEntity in database (already exists in schema) with: sourceId, comicId, chapterId, foundAt, isRead
- UpdatesScreen composable showing:
  - Date header "Today" / "Yesterday" / "May 8"
  - Chapter cards: cover + title + source badge + chapter number + "Mark Read" / "Read Now" buttons
  - Swipe to mark as read
  - Tap to open reader directly
  - "Clear all / Dismiss" button
- UpdatesViewModel pulling from UpdatesRepository
- Background worker logs new chapters to UpdateEntity on each library update
- Home screen shows "5 new chapters today" badge

**Success Criteria:**
- Screen shows all new chapters found today
- Sorting by time works correctly
- Marks read properly updates DB
- Opening reader from here jumps to correct chapter

---

### PHASE 47 — Source Migration UI (Batch Source Switching)

**Priority Score:** 8.0/10  
**User Impact:** 7/10 | **Complexity:** 7/10 | **Competitive Advantage:** 8/10  
**Dependencies:** Phase 35 (Source Install/Update), Phase 53 (Source Migration in DB)  
**Estimated Work:** 8-10 days

**What:** Dedicated screen: "Migrate all titles from MangaDex → Mangakakalot" in one batch. Per-title confirmation option for ambiguous matches.

**Why:** When a source goes down or you want to switch, you need this. TachiyomiSY has it. Readora will be better.

**Deliverables:**
- SourceMigrationScreen in Settings
- Migration flow:
  1. Pick source A (from) → source B (to)
  2. System attempts match: same title + author if available
  3. Show preview: "Title X matches Title X" (green checkmark) / "Title Y has no match" (yellow warning) / "Title Z matches 2 titles" (red ambiguous)
  4. User: confirm each ambiguous match or skip
  5. Confirm batch migration
- Post-migration: titles moved to new source, old source reference kept as history
- Unmerge available if desired
- Migration log showing what was moved, what failed

**Success Criteria:**
- Title matching accuracy >95% for exact title + author match
- UI handles 100+ titles migration
- Old source remains in library history (not deleted)
- Can undo migration within 24 hours

---

### PHASE 48 — Double-Page / Spread Mode (Landscape Two-Page View)

**Priority Score:** 7.8/10  
**User Impact:** 7/10 | **Complexity:** 6/10 | **Competitive Advantage:** 8/10  
**Dependencies:** Phase 65 (Paged Reader Upgrade)  
**Estimated Work:** 6-8 days

**What:** Landscape mode: show two pages side-by-side. Auto-detect spread pages and show full-width.

**Why:** Yokai (J2K fork) has this. Tachiyomi NEVER added it despite years of requests. Differentiator for tablet + large phone users.

**Deliverables:**
- ReaderViewModel.readerMode: PAGED_SINGLE / PAGED_DOUBLE / PAGED_AUTO
- Landscape orientation detection
- DoublePageReaderComposable showing pages[current] + pages[current+1] side-by-side
- Spread detection: if page width > 3× height → full-width override
- "First page solo" option: cover shown alone, then double-page from page 2
- Right-to-left spread support (mirror for RTL languages)
- Toggle in reader controls: Single / Double / Auto

**Success Criteria:**
- Double-page rendering is smooth (60fps)
- Spread detection doesn't break navigation
- RTL works correctly
- No layout glitches on rotation

---

### PHASE 49 — App Lock / Biometric Protection

**Priority Score:** 7.5/10  
**User Impact:** 6/10 | **Complexity:** 5/10 | **Competitive Advantage:** 7/10  
**Dependencies:** Phase 29 (Settings Storage)  
**Estimated Work:** 5-7 days

**What:** Optional PIN / pattern / biometric (fingerprint / face) to lock entire app or specific categories.

**Why:** Kotatsu has this. For users with adult content or shared devices. Privacy feature expected in modern apps.

**Deliverables:**
- ReadoraSettings.appLockEnabled, appLockType (NONE / PIN / PATTERN / BIOMETRIC)
- ReadoraSettings.appLockTrigger (ALWAYS / AFTER_5_MINUTES / ON_RESUME)
- BiometricPrompt on app resume if lock enabled
- PIN/pattern fallback if biometric fails
- Category-level lock: lock specific categories only (e.g., lock "18+" category)
- If locked: library shows blurred covers / placeholder icons instead of actual covers
- Timeout: lock re-activates after X minutes background
- Panic button: if biometric fails 3 times, falls back to PIN

**Success Criteria:**
- Biometric prompt appears within 1 second of app resume
- PIN entry works smoothly
- Covers are actually blurred (not just hidden)
- No bypass exploits

---

### PHASE 50 — Volume Button Page Navigation + Enhanced Reader Controls

**Priority Score:** 7.2/10  
**User Impact:** 6/10 | **Complexity:** 4/10 | **Competitive Advantage:** 7/10  
**Dependencies:** Phase 65 (Paged Reader Upgrade)  
**Estimated Work:** 3-5 days

**What:** Physical volume buttons turn pages. Configurable: Volume Up/Down = Prev/Next or reversed. Works in both paged and webtoon modes.

**Why:** Kotatsu supports this. Great for one-handed reading (commuting, lying in bed). Users love this feature.

**Deliverables:**
- KeyEvent handling in ReaderScreen
- ReadoraSettings.volumeButtonsEnabled boolean
- ReadoraSettings.volumeButtonsReversed boolean
- In paged mode: vol up → prev page, vol down → next page (or reversed)
- In webtoon mode: vol up → scroll up (configurable amount), vol down → scroll down
- Toast feedback: "Page 45 → 46"
- Works even with media volume controls present

**Success Criteria:**
- Volume buttons recognized within 100ms
- Configurable direction works correctly
- Doesn't interfere with system volume controls

---

## SUPPORTING PHASES (Not in Top 10, but necessary)

### PHASE 51 — Bulk Library Actions (Multi-Select Operations)
- Multi-select in library (long-press)
- Bulk: add to category, change reading status, delete downloads, mark read/unread
- Select all / invert selection

### PHASE 52 — Material You / Dynamic Color Theme
- Android 12+ dynamic color support
- Per-title cover-based theming (like Komikku)
- Smooth color transitions

### PHASE 53 — Page-Level Bookmarks
- Bookmark specific pages (not just chapters)
- View all bookmarks per title + globally
- Jump to bookmarked page

### PHASE 54 — Auto-Scroll for Webtoon Mode
- Auto-scroll toggle in webtoon reader
- Configurable scroll speed (slow/med/fast)
- Tap to pause/resume

### PHASE 55 — Tablet & Large Screen Optimization
- Two-panel layout on tablets (library + details side-by-side)
- Landscape-optimized library grid (more columns)
- Foldable screen hinge-awareness
- External keyboard shortcuts (Page Up/Down, arrow keys)

---

## PHASE EXECUTION STRATEGY

### Week 1-2: Phase 41 (Global Multi-Source Search)
- Maximum user impact for complexity
- Foundation for "omnichannel" discovery
- Clear success metrics

### Week 3-4: Phase 42 (MAL + AniList)
- Close the biggest gap vs competition
- Users will immediately notice
- ~3MB size increase (acceptable)

### Week 5-6: Phase 43 (Google Drive Sync)
- Differentiate on multi-device experience
- Addresses #1 community complaint
- Will take longest but most rewarding

### Week 7: Phase 44 (Webtoon Auto-Detect)
- Quick win, huge UX improvement
- Polish what we already have

### Week 8: Phase 45 (Incognito Mode)
- Another quick win
- Privacy feature rounds out the app

### Week 9: Phase 46 (Updates Feed)
- Fresh UI screen, feels like new release
- Completes the "feed" experience

### Week 10: Phase 47 (Source Migration)
- Power user feature
- Builds on source management

### Week 11-12: Phase 48 (Double-Page Mode)
- Technical deep-dive
- Great for tablets

### Week 13: Phase 49 (App Lock)
- Security/privacy polish
- Quick to implement

### Week 14: Phase 50 (Volume Buttons + Reader Polish)
- Quality-of-life feature
- Final reader polish

---

## SUCCESS METRICS (Per Phase)

- ✅ **Code Quality:** Zero warnings, follows Kotlin idioms
- ✅ **Testing:** All new code has unit tests (>80% coverage)
- ✅ **Performance:** APK size increase <5MB per phase
- ✅ **Stability:** Passes 5 manual test scenarios
- ✅ **User Impact:** Feature is discoverable and intuitive
- ✅ **Documentation:** Code comments + in-app help

---

## RISK MITIGATION

| Risk | Mitigation |
|------|-----------|
| Build failures during phase | Commit buildable code every day; never break main |
| Scope creep | Strict phase boundaries; anything else deferred to future phase |
| Performance regression | Profile before/after; APK size tracking |
| User confusion | UI copy is clear, in-app hints for new features |
| Data loss | Always have fallback; never destructive without confirmation |

---

## MEASURING SUCCESS

After each phase:
1. ✅ Feature is in app and reachable from UI
2. ✅ No new crashes introduced (0 baseline crashes per session)
3. ✅ Existing features still work (regression test all tabs)
4. ✅ APK built and signed for testing
5. ✅ Phase documented in code comments

---

## NEXT IMMEDIATE STEPS (Today)

1. Fix HomeScreen.kt build error (Compose DSL scope issue)
2. Build successfully
3. Create Phase 41 tickets in your project tracker
4. Begin Phase 41 implementation (Global Multi-Source Search)

---

## LONG-TERM VISION (After Phase 50)

At that point, Readora will be:
- ✅ Feature-complete vs Kotatsu/TachiyomiSY
- ✅ 10/10 on multi-source + tracking + sync
- ✅ Premium alternative to all free readers
- ✅ Ready for community release

Phase 51+ becomes "nice-to-haves" (widgets, e-ink mode, community features, etc.)

---

**Document Owner:** Arena AI Agent  
**Last Updated:** May 9, 2026  
**Status:** Ready for Implementation  
**Confidence Level:** 95% (based on original plan + gap analysis)
