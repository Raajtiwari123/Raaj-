package com.example.data

import kotlinx.coroutines.flow.Flow

class LuminaRepository(
    private val dao: LuminaDao,
    private val themePreferences: ThemePreferences
) {
    val isOledDarkMode: Flow<Boolean> = themePreferences.isOledDarkMode
    val profile: Flow<UserProfileEntity?> = dao.observeProfile()
    val posts: Flow<List<PostEntity>> = dao.observeAllPosts()
    val comments: Flow<List<CommentEntity>> = dao.observeAllComments()
    val stories: Flow<List<StoryEntity>> = dao.observeStories()
    val dmThreads: Flow<List<DmThreadEntity>> = dao.observeDmThreads()
    val dmMessages: Flow<List<DmMessageEntity>> = dao.observeAllMessages()
    val notifications: Flow<List<NotificationEntity>> = dao.observeNotifications()

    suspend fun ensureSeeded() {
        if (dao.getProfileCount() == 0) {
            val now = System.currentTimeMillis()
            dao.upsertProfile(LuminaSeedData.defaultProfile)
            dao.insertPosts(LuminaSeedData.initialPosts(now))
            dao.insertComments(LuminaSeedData.initialComments(now))
            dao.insertStories(LuminaSeedData.initialStories(now))
            dao.insertDmThreads(LuminaSeedData.initialDmThreads(now))
            dao.insertMessages(LuminaSeedData.initialDmMessages(now))
            dao.insertNotifications(LuminaSeedData.initialNotifications())
        }
    }

    suspend fun setOledDarkMode(enabled: Boolean) {
        themePreferences.setOledDarkMode(enabled)
    }

    suspend fun updatePost(post: PostEntity) {
        dao.updatePost(post)
    }

    suspend fun createPost(post: PostEntity): Long {
        return dao.insertPost(post)
    }

    suspend fun addComment(comment: CommentEntity) {
        dao.insertComment(comment)
    }

    suspend fun addStory(story: StoryEntity) {
        dao.insertStory(story)
    }

    suspend fun markStoryViewed(storyId: Long) {
        dao.markStoryViewed(storyId)
    }

    suspend fun toggleFollowCreator(handle: String, nowFollowing: Boolean) {
        dao.updateFollowForAuthor(handle, nowFollowing)
        dao.updateNotificationFollow(handle, nowFollowing)
    }

    suspend fun sendMessage(threadId: String, text: String, senderIsMe: Boolean, incrementUnread: Boolean = false) {
        val now = System.currentTimeMillis()
        dao.insertMessage(
            DmMessageEntity(
                threadId = threadId,
                senderIsMe = senderIsMe,
                text = text,
                createdAtMillis = now
            )
        )
        dao.updateThreadSummary(
            threadId = threadId,
            preview = if (senderIsMe) "You: $text" else text,
            timestamp = now,
            unread = if (incrementUnread) 1 else 0
        )
    }

    suspend fun clearThreadUnread(threadId: String) {
        dao.clearThreadUnread(threadId)
    }

    suspend fun markAllNotificationsRead() {
        dao.markAllNotificationsRead()
    }

    suspend fun updateProfile(profile: UserProfileEntity) {
        dao.upsertProfile(profile)
        dao.syncUserPostsProfile(
            name = profile.displayName,
            handle = profile.handle,
            avatar = profile.avatarUri
        )
    }
}
