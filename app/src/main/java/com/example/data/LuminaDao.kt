package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LuminaDao {
    // Profile
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun observeProfile(): Flow<UserProfileEntity?>

    @Query("SELECT COUNT(*) FROM user_profile")
    suspend fun getProfileCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProfile(profile: UserProfileEntity)

    // Posts
    @Query("SELECT * FROM posts ORDER BY createdAtMillis DESC")
    fun observeAllPosts(): Flow<List<PostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PostEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity): Long

    @Update
    suspend fun updatePost(post: PostEntity)

    @Query("UPDATE posts SET isFollowingAuthor = :isFollowing WHERE authorHandle = :handle")
    suspend fun updateFollowForAuthor(handle: String, isFollowing: Boolean)

    @Query("UPDATE posts SET authorName = :name, authorHandle = :handle, authorAvatar = :avatar WHERE isUserPost = 1")
    suspend fun syncUserPostsProfile(name: String, handle: String, avatar: String)

    // Comments
    @Query("SELECT * FROM comments ORDER BY createdAtMillis ASC")
    fun observeAllComments(): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComments(comments: List<CommentEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)

    // Stories
    @Query("SELECT * FROM stories ORDER BY isOwnStory DESC, isViewed ASC, createdAtMillis DESC")
    fun observeStories(): Flow<List<StoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(stories: List<StoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: StoryEntity)

    @Query("UPDATE stories SET isViewed = 1 WHERE id = :storyId")
    suspend fun markStoryViewed(storyId: Long)

    // Direct Messages
    @Query("SELECT * FROM dm_threads ORDER BY updatedAtMillis DESC")
    fun observeDmThreads(): Flow<List<DmThreadEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDmThreads(threads: List<DmThreadEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDmThread(thread: DmThreadEntity)

    @Query("UPDATE dm_threads SET unreadCount = 0 WHERE id = :threadId")
    suspend fun clearThreadUnread(threadId: String)

    @Query("UPDATE dm_threads SET lastMessagePreview = :preview, updatedAtMillis = :timestamp, unreadCount = :unread WHERE id = :threadId")
    suspend fun updateThreadSummary(threadId: String, preview: String, timestamp: Long, unread: Int)

    @Query("SELECT * FROM dm_messages ORDER BY createdAtMillis ASC")
    fun observeAllMessages(): Flow<List<DmMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<DmMessageEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: DmMessageEntity)

    // Notifications
    @Query("SELECT * FROM notifications ORDER BY isToday DESC, id ASC")
    fun observeNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Query("UPDATE notifications SET isUnread = 0")
    suspend fun markAllNotificationsRead()

    @Query("UPDATE notifications SET isFollowingActor = :isFollowing WHERE actorHandle = :handle")
    suspend fun updateNotificationFollow(handle: String, isFollowing: Boolean)
}
