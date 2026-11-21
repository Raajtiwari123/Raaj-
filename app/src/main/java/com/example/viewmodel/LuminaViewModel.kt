package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CommentEntity
import com.example.data.DmMessageEntity
import com.example.data.DmThreadEntity
import com.example.data.LuminaDatabase
import com.example.data.LuminaRepository
import com.example.data.LuminaSeedData
import com.example.data.LuminaTab
import com.example.data.NotificationEntity
import com.example.data.PhotoFilter
import com.example.data.PostEntity
import com.example.data.ReelItem
import com.example.data.StoryEntity
import com.example.data.SuggestedCreator
import com.example.data.ThemePreferences
import com.example.data.UserProfileEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class CreateMode {
    POST,
    STORY
}

data class LuminaUiOverlays(
    val activeTab: LuminaTab = LuminaTab.HOME,
    val isRefreshing: Boolean = false,
    val showDmOverlay: Boolean = false,
    val activeDmThreadId: String? = null,
    val typingThreadIds: Set<String> = emptySet(),
    val createMode: CreateMode? = null,
    val activeCommentsPostId: Long? = null,
    val activeSharePostId: Long? = null,
    val activeDetailPostId: Long? = null,
    val activeStoryIndex: Int? = null,
    val showEditProfileDialog: Boolean = false,
    val exploreQuery: String = "",
    val selectedExploreCategory: String = "For You",
    val snackbarMessage: String? = null
)

class LuminaViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: LuminaRepository

    init {
        val db = LuminaDatabase.getInstance(application)
        val prefs = ThemePreferences(application)
        repository = LuminaRepository(db.luminaDao(), prefs)
        viewModelScope.launch {
            repository.ensureSeeded()
        }
    }

    val isOledDarkMode: StateFlow<Boolean> = repository.isOledDarkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val profile: StateFlow<UserProfileEntity> = repository.profile
        .map { it ?: LuminaSeedData.defaultProfile }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LuminaSeedData.defaultProfile)

    val posts: StateFlow<List<PostEntity>> = repository.posts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val commentsByPost: StateFlow<Map<Long, List<CommentEntity>>> = repository.comments
        .map { list -> list.groupBy { it.postId } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())

    val stories: StateFlow<List<StoryEntity>> = repository.stories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val dmThreads: StateFlow<List<DmThreadEntity>> = repository.dmThreads
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val dmMessagesByThread: StateFlow<Map<String, List<DmMessageEntity>>> = repository.dmMessages
        .map { list -> list.groupBy { it.threadId } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())

    val notifications: StateFlow<List<NotificationEntity>> = repository.notifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val totalUnreadDmCount: StateFlow<Int> = dmThreads
        .map { threads -> threads.sumOf { it.unreadCount } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 3)

    val hasUnreadNotifications: StateFlow<Boolean> = notifications
        .map { list -> list.any { it.isUnread } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    private val _reels = MutableStateFlow(LuminaSeedData.initialReels)
    val reels: StateFlow<List<ReelItem>> = _reels.asStateFlow()

    private val _suggestedCreators = MutableStateFlow(LuminaSeedData.suggestedCreators)
    val suggestedCreators: StateFlow<List<SuggestedCreator>> = _suggestedCreators.asStateFlow()

    private val _overlays = MutableStateFlow(LuminaUiOverlays())
    val overlays: StateFlow<LuminaUiOverlays> = _overlays.asStateFlow()

    fun toggleTheme() {
        viewModelScope.launch {
            repository.setOledDarkMode(!isOledDarkMode.value)
        }
    }

    fun selectTab(tab: LuminaTab) {
        _overlays.update { it.copy(activeTab = tab, showDmOverlay = false, activeDmThreadId = null) }
        if (tab == LuminaTab.NOTIFICATIONS) {
            viewModelScope.launch {
                repository.markAllNotificationsRead()
            }
        }
    }

    fun refreshFeed() {
        if (_overlays.value.isRefreshing) return
        viewModelScope.launch {
            _overlays.update { it.copy(isRefreshing = true) }
            delay(850L)
            _overlays.update {
                it.copy(
                    isRefreshing = false,
                    snackbarMessage = "Feed updated with fresh inspiration"
                )
            }
        }
    }

    fun toggleLike(post: PostEntity) {
        viewModelScope.launch {
            val nowLiked = !post.isLiked
            val delta = if (nowLiked) 1 else -1
            repository.updatePost(
                post.copy(
                    isLiked = nowLiked,
                    likesCount = (post.likesCount + delta).coerceAtLeast(0)
                )
            )
        }
    }

    fun doubleTapLike(post: PostEntity) {
        if (!post.isLiked) {
            toggleLike(post)
        }
    }

    fun toggleBookmark(post: PostEntity) {
        viewModelScope.launch {
            val nowSaved = !post.isBookmarked
            repository.updatePost(post.copy(isBookmarked = nowSaved))
            showMessage(if (nowSaved) "Saved to your collection" else "Removed from saved")
        }
    }

    fun toggleFollowCreator(handle: String, currentlyFollowing: Boolean) {
        val nowFollowing = !currentlyFollowing
        viewModelScope.launch {
            repository.toggleFollowCreator(handle, nowFollowing)
            val currentProfile = profile.value
            val delta = if (nowFollowing) 1 else -1
            repository.updateProfile(
                currentProfile.copy(followingCount = (currentProfile.followingCount + delta).coerceAtLeast(0))
            )
        }
        _suggestedCreators.update { list ->
            list.map { if (it.handle == handle) it.copy(isFollowing = nowFollowing) else it }
        }
        _reels.update { list ->
            list.map { if (it.creatorHandle == handle) it.copy(isFollowing = nowFollowing) else it }
        }
    }

    fun openComments(postId: Long) {
        _overlays.update { it.copy(activeCommentsPostId = postId) }
    }

    fun closeComments() {
        _overlays.update { it.copy(activeCommentsPostId = null) }
    }

    fun addComment(postId: Long, text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return
        val current = profile.value
        viewModelScope.launch {
            repository.addComment(
                CommentEntity(
                    postId = postId,
                    authorName = current.displayName,
                    authorHandle = current.handle,
                    authorAvatar = current.avatarUri,
                    text = trimmed
                )
            )
        }
    }

    fun openShareSheet(postId: Long) {
        _overlays.update { it.copy(activeSharePostId = postId) }
    }

    fun closeShareSheet() {
        _overlays.update { it.copy(activeSharePostId = null) }
    }

    fun sharePostToThread(post: PostEntity, thread: DmThreadEntity) {
        viewModelScope.launch {
            val snippet = post.caption.take(64).trim()
            repository.sendMessage(
                threadId = thread.id,
                text = "Shared ${post.authorHandle}'s post: \"$snippet\"",
                senderIsMe = true
            )
            _overlays.update {
                it.copy(
                    activeSharePostId = null,
                    snackbarMessage = "Sent to ${thread.participantName}"
                )
            }
        }
    }

    fun openPostDetail(postId: Long) {
        _overlays.update { it.copy(activeDetailPostId = postId) }
    }

    fun closePostDetail() {
        _overlays.update { it.copy(activeDetailPostId = null) }
    }

    fun openCreateStudio(mode: CreateMode) {
        _overlays.update { it.copy(createMode = mode) }
    }

    fun closeCreateStudio() {
        _overlays.update { it.copy(createMode = null) }
    }

    fun publishPost(
        mediaUri: String,
        filter: PhotoFilter,
        caption: String,
        location: String,
        category: String
    ) {
        val current = profile.value
        val finalCaption = caption.trim().ifEmpty { "Captured a new moment on #lumina" }
        val finalLocation = location.trim().ifEmpty { "Studio Light" }
        viewModelScope.launch {
            repository.createPost(
                PostEntity(
                    authorName = current.displayName,
                    authorHandle = current.handle,
                    authorAvatar = current.avatarUri,
                    isAuthorVerified = true,
                    isFollowingAuthor = false,
                    location = finalLocation,
                    caption = finalCaption,
                    category = category,
                    mediaUri = mediaUri,
                    filterName = filter.name,
                    likesCount = 1,
                    isLiked = true,
                    isBookmarked = false,
                    aspectRatio = 1.1f,
                    isUserPost = true,
                    createdAtMillis = System.currentTimeMillis()
                )
            )
            _overlays.update {
                it.copy(
                    createMode = null,
                    activeTab = LuminaTab.HOME,
                    snackbarMessage = "Published to your Lumina feed"
                )
            }
        }
    }

    fun publishStory(
        mediaUri: String,
        filter: PhotoFilter,
        caption: String
    ) {
        val current = profile.value
        viewModelScope.launch {
            repository.addStory(
                StoryEntity(
                    authorName = "Your Story",
                    authorHandle = current.handle,
                    authorAvatar = current.avatarUri,
                    mediaUri = mediaUri,
                    caption = caption.trim().ifEmpty { "New moment just shared" },
                    filterName = filter.name,
                    isOwnStory = true,
                    isViewed = false,
                    createdAtMillis = System.currentTimeMillis()
                )
            )
            _overlays.update {
                it.copy(
                    createMode = null,
                    snackbarMessage = "Added to Your Story"
                )
            }
        }
    }

    fun openStoryViewer(index: Int) {
        val storyList = stories.value
        if (index in storyList.indices) {
            _overlays.update { it.copy(activeStoryIndex = index) }
            viewModelScope.launch {
                repository.markStoryViewed(storyList[index].id)
            }
        }
    }

    fun navigateStory(newIndex: Int) {
        val storyList = stories.value
        if (newIndex in storyList.indices) {
            _overlays.update { it.copy(activeStoryIndex = newIndex) }
            viewModelScope.launch {
                repository.markStoryViewed(storyList[newIndex].id)
            }
        } else {
            closeStoryViewer()
        }
    }

    fun closeStoryViewer() {
        _overlays.update { it.copy(activeStoryIndex = null) }
    }

    fun replyToStory(story: StoryEntity, replyText: String) {
        val trimmed = replyText.trim()
        if (trimmed.isEmpty()) return
        val matchingThread = dmThreads.value.firstOrNull {
            it.participantHandle.equals(story.authorHandle, ignoreCase = true)
        } ?: dmThreads.value.firstOrNull()

        if (matchingThread != null) {
            viewModelScope.launch {
                repository.sendMessage(
                    threadId = matchingThread.id,
                    text = "Replied to story: $trimmed",
                    senderIsMe = true
                )
                showMessage("Reply sent to ${story.authorName}")
            }
        }
    }

    // Direct Messages
    fun openDmOverlay() {
        _overlays.update { it.copy(showDmOverlay = true) }
    }

    fun closeDmOverlay() {
        _overlays.update { it.copy(showDmOverlay = false, activeDmThreadId = null) }
    }

    fun openDmThread(threadId: String) {
        _overlays.update { it.copy(showDmOverlay = true, activeDmThreadId = threadId) }
        viewModelScope.launch {
            repository.clearThreadUnread(threadId)
        }
    }

    fun closeDmThread() {
        _overlays.update { it.copy(activeDmThreadId = null) }
    }

    fun sendDmMessage(threadId: String, text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return
        viewModelScope.launch {
            repository.sendMessage(threadId = threadId, text = trimmed, senderIsMe = true)
            // Trigger realistic creator typing dots and contextual reply
            _overlays.update { it.copy(typingThreadIds = it.typingThreadIds + threadId) }
            delay(1600L)
            _overlays.update { it.copy(typingThreadIds = it.typingThreadIds - threadId) }
            val reply = contextualReply(trimmed)
            val isCurrentlyViewing = _overlays.value.showDmOverlay && _overlays.value.activeDmThreadId == threadId
            repository.sendMessage(
                threadId = threadId,
                text = reply,
                senderIsMe = false,
                incrementUnread = !isCurrentlyViewing
            )
        }
    }

    private fun contextualReply(userMessage: String): String {
        val lower = userMessage.lowercase()
        return when {
            lower.contains("photo") || lower.contains("shoot") || lower.contains("camera") ->
                "Love that idea! Let's shoot during golden hour when the light softens."
            lower.contains("coffee") || lower.contains("cafe") || lower.contains("meet") ->
                "Count me in! That new Scandinavian roastery on 4th has great morning light."
            lower.contains("heart") || lower == "❤️" ->
                "Sending good vibes right back!"
            lower.contains("filter") || lower.contains("edit") ->
                "Try Clarendon or Warm at subtle intensity — it makes highlights glow."
            else ->
                "Totally agree! Can't wait to see what you share next on Lumina."
        }
    }

    // Explore
    fun updateExploreQuery(query: String) {
        _overlays.update { it.copy(exploreQuery = query) }
    }

    fun selectExploreCategory(category: String) {
        _overlays.update { it.copy(selectedExploreCategory = category) }
    }

    // Reels
    fun toggleReelLike(reelId: String) {
        _reels.update { list ->
            list.map { reel ->
                if (reel.id == reelId) {
                    val nowLiked = !reel.isLiked
                    reel.copy(
                        isLiked = nowLiked,
                        likesCount = reel.likesCount + if (nowLiked) 1 else -1
                    )
                } else reel
            }
        }
    }

    fun doubleTapReelLike(reelId: String) {
        val target = _reels.value.firstOrNull { it.id == reelId } ?: return
        if (!target.isLiked) toggleReelLike(reelId)
    }

    fun toggleReelBookmark(reelId: String) {
        _reels.update { list ->
            list.map { reel ->
                if (reel.id == reelId) {
                    val nowSaved = !reel.isBookmarked
                    showMessage(if (nowSaved) "Reel saved to archive" else "Reel removed from archive")
                    reel.copy(isBookmarked = nowSaved)
                } else reel
            }
        }
    }

    // Profile Editing
    fun openEditProfile() {
        _overlays.update { it.copy(showEditProfileDialog = true) }
    }

    fun closeEditProfile() {
        _overlays.update { it.copy(showEditProfileDialog = false) }
    }

    fun saveProfileEdits(
        displayName: String,
        handle: String,
        bio: String,
        website: String,
        avatarUri: String
    ) {
        val current = profile.value
        val cleanName = displayName.trim().ifEmpty { current.displayName }
        val rawHandle = handle.trim().ifEmpty { current.handle }
        val cleanHandle = if (rawHandle.startsWith("@")) rawHandle else "@$rawHandle"
        viewModelScope.launch {
            repository.updateProfile(
                current.copy(
                    displayName = cleanName,
                    handle = cleanHandle,
                    bio = bio.trim(),
                    website = website.trim(),
                    avatarUri = avatarUri
                )
            )
            _overlays.update {
                it.copy(
                    showEditProfileDialog = false,
                    snackbarMessage = "Profile updated"
                )
            }
        }
    }

    fun showMessage(message: String) {
        _overlays.update { it.copy(snackbarMessage = message) }
    }

    fun consumeSnackbar() {
        _overlays.update { it.copy(snackbarMessage = null) }
    }
}
