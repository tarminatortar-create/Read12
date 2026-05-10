package com.readora.app.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ComicDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(comic: ComicEntity): Long

    @Update
    suspend fun update(comic: ComicEntity)

    @Delete
    suspend fun delete(comic: ComicEntity)

    @Query("DELETE FROM comics WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM comics ORDER BY COALESCE(lastReadAt, addedAt) DESC")
    fun getAll(): Flow<List<ComicEntity>>

    @Query("SELECT * FROM comics WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): ComicEntity?

    @Query("SELECT * FROM comics WHERE sourceId = :sourceId ORDER BY title COLLATE NOCASE")
    fun getBySource(sourceId: String): Flow<List<ComicEntity>>

    @Query("SELECT * FROM comics WHERE sourceId = :sourceId ORDER BY title COLLATE NOCASE")
    suspend fun getBySourceOnce(sourceId: String): List<ComicEntity>

    /** Phase 53 — migrate all comics from one source to another. Returns updated row count. */
    @Query("UPDATE comics SET sourceId = :toSourceId WHERE sourceId = :fromSourceId")
    suspend fun migrateSource(fromSourceId: String, toSourceId: String): Int

    @Query("SELECT * FROM comics WHERE title LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%' ORDER BY title COLLATE NOCASE")
    fun search(query: String): Flow<List<ComicEntity>>
}

@Dao
interface ChapterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(chapter: ChapterEntity): Long

    @Update
    suspend fun update(chapter: ChapterEntity)

    @Delete
    suspend fun delete(chapter: ChapterEntity)

    @Query("SELECT * FROM chapters WHERE comicId = :comicId ORDER BY number + 0 DESC, title COLLATE NOCASE")
    fun getByComic(comicId: String): Flow<List<ChapterEntity>>

    @Query("SELECT * FROM chapters WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): ChapterEntity?

    @Query("SELECT * FROM chapters WHERE isDownloaded = 1 ORDER BY readAt DESC")
    fun getDownloaded(): Flow<List<ChapterEntity>>
}

@Dao
interface ProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(progress: ProgressEntity): Long

    @Query("SELECT * FROM progress WHERE comicId = :comicId ORDER BY updatedAt DESC")
    fun getByComic(comicId: String): Flow<List<ProgressEntity>>

    @Query("SELECT * FROM progress WHERE chapterId = :chapterId LIMIT 1")
    suspend fun getByChapter(chapterId: String): ProgressEntity?

    @Query("SELECT * FROM progress WHERE comicId = :comicId AND chapterId = :chapterId LIMIT 1")
    suspend fun getByComicAndChapter(comicId: String, chapterId: String): ProgressEntity?

    @Query("SELECT * FROM progress WHERE comicId = :comicId ORDER BY updatedAt DESC LIMIT 1")
    suspend fun getLastRead(comicId: String): ProgressEntity?

    @Query("DELETE FROM progress WHERE comicId = :comicId")
    suspend fun deleteByComic(comicId: String)
}

@Dao
interface DownloadJobDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(job: DownloadJobEntity): Long

    @Update
    suspend fun update(job: DownloadJobEntity)

    @Delete
    suspend fun delete(job: DownloadJobEntity)

    @Query("SELECT * FROM download_jobs ORDER BY queuedAt DESC")
    fun getAll(): Flow<List<DownloadJobEntity>>

    @Query("SELECT * FROM download_jobs WHERE status = :status ORDER BY queuedAt")
    suspend fun getByStatus(status: String): List<DownloadJobEntity>

    @Query("SELECT * FROM download_jobs WHERE comicId = :comicId ORDER BY queuedAt DESC")
    fun getByComic(comicId: String): Flow<List<DownloadJobEntity>>
}

@Dao
interface MergeGroupDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(group: MergeGroupEntity): Long

    @Update
    suspend fun update(group: MergeGroupEntity)

    @Delete
    suspend fun delete(group: MergeGroupEntity)

    @Query("DELETE FROM merge_groups WHERE groupId = :groupId")
    suspend fun deleteById(groupId: String)

    @Query("SELECT * FROM merge_groups ORDER BY priority DESC, title COLLATE NOCASE")
    fun getAll(): Flow<List<MergeGroupEntity>>

    @Query("SELECT * FROM merge_groups WHERE groupId = :groupId LIMIT 1")
    suspend fun getById(groupId: String): MergeGroupEntity?
}

@Dao
interface SourceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(source: SourceEntity): Long

    @Update
    suspend fun update(source: SourceEntity)

    @Delete
    suspend fun delete(source: SourceEntity)

    @Query("SELECT * FROM sources ORDER BY enabled DESC, name COLLATE NOCASE")
    fun getAll(): Flow<List<SourceEntity>>

    @Query("SELECT * FROM sources WHERE enabled = 1 ORDER BY name COLLATE NOCASE")
    suspend fun getEnabled(): List<SourceEntity>

    @Query("SELECT * FROM sources WHERE sourceId = :sourceId LIMIT 1")
    suspend fun getById(sourceId: String): SourceEntity?

    @Query("UPDATE sources SET enabled = :enabled WHERE sourceId = :sourceId")
    suspend fun setEnabled(sourceId: String, enabled: Boolean)
}

@Dao
interface UpdateDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(update: UpdateEntity): Long

    @Query("SELECT * FROM updates ORDER BY foundAt DESC")
    fun getAll(): Flow<List<UpdateEntity>>

    @Query("SELECT * FROM updates WHERE isRead = 0 ORDER BY foundAt DESC")
    fun getUnread(): Flow<List<UpdateEntity>>

    @Query("SELECT COUNT(*) FROM updates WHERE comicId = :comicId AND isRead = 0")
    fun getUnreadCountForComic(comicId: String): Flow<Int>

    @Query("UPDATE updates SET isRead = 1 WHERE id = :id")
    suspend fun markRead(id: Long)

    @Query("UPDATE updates SET isRead = 1")
    suspend fun markAllRead()

    @Query("UPDATE updates SET isRead = 1 WHERE comicId = :comicId")
    suspend fun markAllReadForComic(comicId: String)

    @Query("DELETE FROM updates")
    suspend fun clearAll()

    @Query("DELETE FROM updates WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM updates WHERE isRead = 1")
    suspend fun deleteAllRead()
}

@Dao
interface BookmarkDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(bookmark: BookmarkEntity): Long

    @Query("SELECT * FROM bookmarks ORDER BY createdAt DESC")
    fun getAll(): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks WHERE sourceId = :sourceId AND comicId = :comicId ORDER BY createdAt DESC")
    fun getByComic(sourceId: String, comicId: String): Flow<List<BookmarkEntity>>

    @Query("DELETE FROM bookmarks WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM bookmarks")
    suspend fun deleteAll()
}

@Dao
interface ChapterNoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: ChapterNoteEntity): Long

    @Query("SELECT * FROM chapter_notes ORDER BY createdAt DESC")
    fun getAll(): Flow<List<ChapterNoteEntity>>

    @Query("SELECT * FROM chapter_notes WHERE sourceId = :sourceId AND comicId = :comicId ORDER BY createdAt DESC")
    fun getByComic(sourceId: String, comicId: String): Flow<List<ChapterNoteEntity>>

    @Query("DELETE FROM chapter_notes WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM chapter_notes")
    suspend fun deleteAll()
}

@Dao
interface ReadingSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: ReadingSessionEntity): Long

    @Query("SELECT * FROM reading_sessions ORDER BY startedAt DESC")
    fun getAll(): Flow<List<ReadingSessionEntity>>

    @Query("SELECT SUM(durationMs) FROM reading_sessions WHERE startedAt >= :since")
    suspend fun totalDurationSince(since: Long): Long?

    @Query("DELETE FROM reading_sessions")
    suspend fun deleteAll()
}
