package com.example

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.LuminaTab
import com.example.ui.components.CommentsBottomSheet
import com.example.ui.components.LuminaBottomNavBar
import com.example.ui.components.LuminaMobileTopBar
import com.example.ui.components.LuminaSideNavigation
import com.example.ui.components.SharePostSheet
import com.example.ui.components.StoryViewerOverlay
import com.example.ui.screens.CreateStudioOverlay
import com.example.ui.screens.DirectMessagesOverlay
import com.example.ui.screens.EditProfileDialog
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.HomeFeedScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.PostDetailDialog
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ReelsScreen
import com.example.ui.theme.LuminaTheme
import com.example.viewmodel.CreateMode
import com.example.viewmodel.LuminaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: LuminaViewModel = viewModel()
            val isDarkMode by viewModel.isOledDarkMode.collectAsStateWithLifecycle()

            LaunchedEffect(isDarkMode) {
                enableEdgeToEdge(
                    statusBarStyle = if (isDarkMode) {
                        SystemBarStyle.dark(AndroidColor.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT)
                    },
                    navigationBarStyle = if (isDarkMode) {
                        SystemBarStyle.dark(AndroidColor.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT)
                    }
                )
            }

            LuminaTheme(darkTheme = isDarkMode) {
                LuminaAppRoot(viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun LuminaAppRoot(viewModel: LuminaViewModel) {
    val isDarkMode by viewModel.isOledDarkMode.collectAsStateWithLifecycle()
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val posts by viewModel.posts.collectAsStateWithLifecycle()
    val commentsByPost by viewModel.commentsByPost.collectAsStateWithLifecycle()
    val stories by viewModel.stories.collectAsStateWithLifecycle()
    val reels by viewModel.reels.collectAsStateWithLifecycle()
    val dmThreads by viewModel.dmThreads.collectAsStateWithLifecycle()
    val dmMessagesByThread by viewModel.dmMessagesByThread.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val suggestedCreators by viewModel.suggestedCreators.collectAsStateWithLifecycle()
    val unreadDmCount by viewModel.totalUnreadDmCount.collectAsStateWithLifecycle()
    val hasUnreadNotifications by viewModel.hasUnreadNotifications.collectAsStateWithLifecycle()
    val overlays by viewModel.overlays.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(overlays.snackbarMessage) {
        val message = overlays.snackbarMessage
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            viewModel.consumeSnackbar()
        }
    }

    // Back press on secondary tabs returns cleanly to Home Feed
    BackHandler(enabled = overlays.activeTab != LuminaTab.HOME) {
        viewModel.selectTab(LuminaTab.HOME)
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isCompact = maxWidth < 700.dp
        val showRightSidebar = maxWidth >= 1040.dp

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                if (isCompact) {
                    LuminaMobileTopBar(
                        isDarkMode = isDarkMode,
                        isRefreshing = overlays.isRefreshing,
                        unreadDmCount = unreadDmCount,
                        hasUnreadNotifications = hasUnreadNotifications,
                        onLogoClick = { viewModel.selectTab(LuminaTab.HOME) },
                        onRefreshClick = { viewModel.refreshFeed() },
                        onToggleTheme = { viewModel.toggleTheme() },
                        onNotificationsClick = { viewModel.selectTab(LuminaTab.NOTIFICATIONS) },
                        onDmClick = { viewModel.openDmOverlay() }
                    )
                }
            },
            bottomBar = {
                if (isCompact) {
                    LuminaBottomNavBar(
                        activeTab = overlays.activeTab,
                        userAvatarUri = profile.avatarUri,
                        userName = profile.displayName,
                        onSelectTab = { viewModel.selectTab(it) },
                        onCreateClick = { viewModel.openCreateStudio(CreateMode.POST) }
                    )
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (!isCompact) {
                    LuminaSideNavigation(
                        activeTab = overlays.activeTab,
                        isDarkMode = isDarkMode,
                        unreadDmCount = unreadDmCount,
                        hasUnreadNotifications = hasUnreadNotifications,
                        profile = profile,
                        onSelectTab = { viewModel.selectTab(it) },
                        onOpenDm = { viewModel.openDmOverlay() },
                        onOpenCreate = { viewModel.openCreateStudio(CreateMode.POST) },
                        onToggleTheme = { viewModel.toggleTheme() },
                        onEditProfile = { viewModel.openEditProfile() }
                    )
                }

                val contentModifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .let { if (!isCompact && overlays.activeTab != LuminaTab.REELS) it.statusBarsPadding() else it }

                Box(modifier = contentModifier) {
                    when (overlays.activeTab) {
                        LuminaTab.HOME -> {
                            HomeFeedScreen(
                                profile = profile,
                                stories = stories,
                                posts = posts,
                                commentsByPost = commentsByPost,
                                suggestedCreators = suggestedCreators,
                                isRefreshing = overlays.isRefreshing,
                                showRightSidebar = showRightSidebar,
                                onRefresh = { viewModel.refreshFeed() },
                                onAddStoryClick = { viewModel.openCreateStudio(CreateMode.STORY) },
                                onStoryClick = { index -> viewModel.openStoryViewer(index) },
                                onToggleLike = { viewModel.toggleLike(it) },
                                onDoubleTapLike = { viewModel.doubleTapLike(it) },
                                onToggleBookmark = { viewModel.toggleBookmark(it) },
                                onOpenComments = { viewModel.openComments(it) },
                                onOpenShare = { viewModel.openShareSheet(it) },
                                onToggleFollow = { handle, following ->
                                    viewModel.toggleFollowCreator(handle, following)
                                },
                                onOpenEditProfile = { viewModel.openEditProfile() }
                            )
                        }
                        LuminaTab.EXPLORE -> {
                            ExploreScreen(
                                posts = posts,
                                query = overlays.exploreQuery,
                                selectedCategory = overlays.selectedExploreCategory,
                                isExpandedWidth = !isCompact,
                                onQueryChange = { viewModel.updateExploreQuery(it) },
                                onSelectCategory = { viewModel.selectExploreCategory(it) },
                                onPostClick = { viewModel.openPostDetail(it.id) }
                            )
                        }
                        LuminaTab.REELS -> {
                            ReelsScreen(
                                reels = reels,
                                onToggleLike = { viewModel.toggleReelLike(it) },
                                onDoubleTapLike = { viewModel.doubleTapReelLike(it) },
                                onToggleBookmark = { viewModel.toggleReelBookmark(it) },
                                onToggleFollow = { handle, following ->
                                    viewModel.toggleFollowCreator(handle, following)
                                },
                                onShareReel = { reel ->
                                    viewModel.showMessage("Shared ${reel.creatorHandle}'s reel link")
                                },
                                onCommentReel = { reel ->
                                    val firstPost = posts.firstOrNull()
                                    if (firstPost != null) viewModel.openComments(firstPost.id)
                                    else viewModel.showMessage("${reel.commentsCount} comments on this reel")
                                }
                            )
                        }
                        LuminaTab.NOTIFICATIONS -> {
                            NotificationsScreen(
                                notifications = notifications,
                                onToggleFollow = { handle, following ->
                                    viewModel.toggleFollowCreator(handle, following)
                                }
                            )
                        }
                        LuminaTab.PROFILE -> {
                            val userPosts = remember(posts) { posts.filter { it.isUserPost } }
                            val savedPosts = remember(posts) { posts.filter { it.isBookmarked } }
                            ProfileScreen(
                                profile = profile,
                                userPosts = userPosts,
                                savedPosts = savedPosts,
                                onEditProfileClick = { viewModel.openEditProfile() },
                                onShareProfileClick = {
                                    viewModel.showMessage("Copied ${profile.handle} profile link")
                                },
                                onPostClick = { viewModel.openPostDetail(it.id) }
                            )
                        }
                    }
                }
            }
        }

        // Full-Screen Direct Messages Overlay
        AnimatedVisibility(
            visible = overlays.showDmOverlay,
            enter = slideInVertically(initialOffsetY = { it / 6 }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it / 6 }) + fadeOut()
        ) {
            DirectMessagesOverlay(
                threads = dmThreads,
                activeThreadId = overlays.activeDmThreadId,
                messagesByThread = dmMessagesByThread,
                typingThreadIds = overlays.typingThreadIds,
                onSelectThread = { viewModel.openDmThread(it) },
                onBackFromThread = { viewModel.closeDmThread() },
                onSendMessage = { threadId, text -> viewModel.sendDmMessage(threadId, text) },
                onDismiss = { viewModel.closeDmOverlay() }
            )
        }

        // Full-Screen Create Post / Add Story Studio Overlay
        val activeCreateMode = overlays.createMode
        AnimatedVisibility(
            visible = activeCreateMode != null,
            enter = slideInVertically(initialOffsetY = { it / 5 }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it / 5 }) + fadeOut()
        ) {
            if (activeCreateMode != null) {
                CreateStudioOverlay(
                    mode = activeCreateMode,
                    onPublishPost = { mediaUri, filter, caption, location, category ->
                        viewModel.publishPost(mediaUri, filter, caption, location, category)
                    },
                    onPublishStory = { mediaUri, filter, caption ->
                        viewModel.publishStory(mediaUri, filter, caption)
                    },
                    onDismiss = { viewModel.closeCreateStudio() }
                )
            }
        }

        // Full-Screen Story Viewer Overlay
        val storyIndex = overlays.activeStoryIndex
        if (storyIndex != null) {
            StoryViewerOverlay(
                stories = stories,
                currentIndex = storyIndex,
                onNavigateIndex = { viewModel.navigateStory(it) },
                onSendReply = { story, reply -> viewModel.replyToStory(story, reply) },
                onDismiss = { viewModel.closeStoryViewer() }
            )
        }

        // Post Detail Modal (from Explore or Profile grids)
        val detailPost = overlays.activeDetailPostId?.let { id -> posts.firstOrNull { it.id == id } }
        if (detailPost != null) {
            PostDetailDialog(
                post = detailPost,
                comments = commentsByPost[detailPost.id].orEmpty(),
                onToggleLike = { viewModel.toggleLike(detailPost) },
                onDoubleTapLike = { viewModel.doubleTapLike(detailPost) },
                onToggleBookmark = { viewModel.toggleBookmark(detailPost) },
                onOpenComments = { viewModel.openComments(detailPost.id) },
                onOpenShare = { viewModel.openShareSheet(detailPost.id) },
                onToggleFollow = {
                    viewModel.toggleFollowCreator(detailPost.authorHandle, detailPost.isFollowingAuthor)
                },
                onDismiss = { viewModel.closePostDetail() }
            )
        }

        // Comments Bottom Sheet
        val commentsPost = overlays.activeCommentsPostId?.let { id -> posts.firstOrNull { it.id == id } }
        if (commentsPost != null) {
            CommentsBottomSheet(
                post = commentsPost,
                comments = commentsByPost[commentsPost.id].orEmpty(),
                userAvatarUri = profile.avatarUri,
                userName = profile.displayName,
                onAddComment = { text -> viewModel.addComment(commentsPost.id, text) },
                onDismiss = { viewModel.closeComments() }
            )
        }

        // Share Post to DM Sheet
        val sharePost = overlays.activeSharePostId?.let { id -> posts.firstOrNull { it.id == id } }
        if (sharePost != null) {
            SharePostSheet(
                post = sharePost,
                threads = dmThreads,
                onShareToThread = { thread -> viewModel.sharePostToThread(sharePost, thread) },
                onDismiss = { viewModel.closeShareSheet() }
            )
        }

        // Edit Profile Dialog
        if (overlays.showEditProfileDialog) {
            EditProfileDialog(
                profile = profile,
                onSave = { name, handle, bio, website, avatarUri ->
                    viewModel.saveProfileEdits(name, handle, bio, website, avatarUri)
                },
                onDismiss = { viewModel.closeEditProfile() }
            )
        }
    }
}
