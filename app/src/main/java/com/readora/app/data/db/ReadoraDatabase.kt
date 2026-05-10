package com.readora.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        ComicEntity::class,
        ChapterEntity::class,
        ProgressEntity::class,
        DownloadJobEntity::class,
        MergeGroupEntity::class,
        SourceEntity::class,
        UpdateEntity::class,
        BookmarkEntity::class,
        ChapterNoteEntity::class,
        ReadingSessionEntity::class,
    ],
    version = 6,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class ReadoraDatabase : RoomDatabase() {
    abstract fun comicDao(): ComicDao
    abstract fun chapterDao(): ChapterDao
    abstract fun progressDao(): ProgressDao
    abstract fun downloadJobDao(): DownloadJobDao
    abstract fun mergeGroupDao(): MergeGroupDao
    abstract fun sourceDao(): SourceDao
    abstract fun updateDao(): UpdateDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun chapterNoteDao(): ChapterNoteDao
    abstract fun readingSessionDao(): ReadingSessionDao

    companion object {
        @Volatile
        var instance: ReadoraDatabase? = null

        fun getInstance(context: Context): ReadoraDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    ReadoraDatabase::class.java,
                    "readora.db",
                )
                    .fallbackToDestructiveMigration(false)
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                            super.onCreate(db)
                            db.execSQL("INSERT INTO sources (sourceId, name, baseUrl, enabled, version, language, category, iconUrl, lastCheckedAt, minAppVersion, parserType, parserDefinitionJson, repositoryId, trustLevel) VALUES ('mangadex', 'MangaDex', 'https://mangadex.org', 1, '1', 'en', 'Built-in', null, null, 1, null, null, null, 'official')")
                        }
                    })
                    .addMigrations(MIGRATION_1_2)
                    .addMigrations(MIGRATION_2_3)
                    .addMigrations(MIGRATION_3_4)
                    .addMigrations(MIGRATION_4_5)
                    .addMigrations(MIGRATION_5_6)
                    .build()
                    .also { instance = it }
            }

        private val MIGRATION_1_2 = object : androidx.room.migration.Migration(1, 2) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE sources ADD COLUMN minAppVersion INTEGER NOT NULL DEFAULT 1")
            }
        }

        private val MIGRATION_2_3 = object : androidx.room.migration.Migration(2, 3) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS updates (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        sourceId TEXT NOT NULL,
                        sourceName TEXT NOT NULL,
                        comicId TEXT NOT NULL,
                        comicTitle TEXT NOT NULL,
                        coverUrl TEXT,
                        chapterId TEXT NOT NULL,
                        chapterNumber TEXT NOT NULL,
                        chapterTitle TEXT NOT NULL,
                        foundAt INTEGER NOT NULL,
                        isRead INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_updates_sourceId_comicId_chapterId ON updates(sourceId, comicId, chapterId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_updates_foundAt ON updates(foundAt)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_updates_isRead ON updates(isRead)")
            }
        }

        private val MIGRATION_3_4 = object : androidx.room.migration.Migration(3, 4) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS bookmarks (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        sourceId TEXT NOT NULL,
                        comicId TEXT NOT NULL,
                        comicTitle TEXT NOT NULL,
                        chapterId TEXT NOT NULL,
                        chapterNumber TEXT NOT NULL,
                        chapterTitle TEXT NOT NULL,
                        pageIndex INTEGER NOT NULL,
                        note TEXT,
                        createdAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_bookmarks_sourceId_comicId_chapterId_pageIndex ON bookmarks(sourceId, comicId, chapterId, pageIndex)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_bookmarks_sourceId_comicId ON bookmarks(sourceId, comicId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_bookmarks_createdAt ON bookmarks(createdAt)")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS chapter_notes (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        sourceId TEXT NOT NULL,
                        comicId TEXT NOT NULL,
                        chapterId TEXT NOT NULL,
                        chapterNumber TEXT NOT NULL,
                        chapterTitle TEXT NOT NULL,
                        pageIndex INTEGER,
                        content TEXT NOT NULL,
                        createdAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_chapter_notes_sourceId_comicId_chapterId ON chapter_notes(sourceId, comicId, chapterId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_chapter_notes_createdAt ON chapter_notes(createdAt)")
            }
        }

        private val MIGRATION_4_5 = object : androidx.room.migration.Migration(4, 5) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS reading_sessions (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        sourceId TEXT NOT NULL,
                        comicId TEXT NOT NULL,
                        comicTitle TEXT NOT NULL,
                        chapterId TEXT,
                        chapterNumber TEXT,
                        startedAt INTEGER NOT NULL,
                        endedAt INTEGER NOT NULL,
                        durationMs INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_reading_sessions_sourceId_comicId ON reading_sessions(sourceId, comicId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_reading_sessions_startedAt ON reading_sessions(startedAt)")
            }
        }

        private val MIGRATION_5_6 = object : androidx.room.migration.Migration(5, 6) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE sources ADD COLUMN parserType TEXT")
                db.execSQL("ALTER TABLE sources ADD COLUMN parserDefinitionJson TEXT")
                db.execSQL("ALTER TABLE sources ADD COLUMN repositoryId TEXT")
                db.execSQL("ALTER TABLE sources ADD COLUMN trustLevel TEXT")
            }
        }
    }
}
