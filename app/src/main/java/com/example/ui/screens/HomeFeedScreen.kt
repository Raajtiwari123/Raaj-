package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.CommentEntity
import com.example.data.PhotoFilter
import com.example.data.PostEntity
import com.example.data.StoryEntity
import com.example.data.SuggestedCreator
import com.example.data.UserProfileEntity
import com.example.ui.components.CreatorAvatar
import com.example.ui.components.HeartBurstOverlay
import com.example.ui.components.LuminaMediaView
import com.example.ui.components.formatCompactCount
import com.example.ui.components.formatRelativeTime
import com.example.ui.theme.BrandRose500
import com.example.ui.theme.LocalLuminaExtras

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeFeedScreen(
    profile: UserProfileEntity,
    stories: List<StoryEntity>,
    posts: List<PostEntity>,
    commentsByPost: Map<Long, List<CommentEntity>>,
    suggestedCreators: List<SuggestedCreator>,
    isRefreshing: Boolean,
    showRightSidebar: Boolean,
    onRefresh: () -> Unit,
    onAddStoryClick: () -> Unit,
    onStoryClick: (Int) -> Unit,
    onToggleLike: (PostEntity) -> Unit,
    onDoubleTapLike: (PostEntity) -> Unit,
    onToggleBookmark: (PostEntity) -> Unit,
    onOpenComments: (Long) -> Unit,
    onOpenShare: (Long) -> Unit,
    onToggleFollow: (String, Boolean) -> Unit,
    onOpenEditProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.Center
    ) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier
                .weight(1f, fill = !showRightSidebar)
                .widthIn(max = 560.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                item(key = "stories_bar") {
                    StoriesBarCard(
                        profile = profile,
                        stories = stories,
                        onAddStoryClick = onAddStoryClick,
                        onStoryClick = onStoryClick
                    )
                }

                items(posts, key = { it.id }) { post ->
                    val postComments = commentsByPost[post.id].orEmpty()
                    FeedPostCard(
                        post = post,
                        commentsCount = postComments.size,
                        latestComment = postComments.lastOrNull(),
                        onToggleLike = { onToggleLike(post) },
                        onDoubleTapLike = { onDoubleTapLike(post) },
                        onToggleBookmark = { onToggleBookmark(post) },
                        onOpenComments = { onOpenComments(post.id) },
                        onOpenShare = { onOpenShare(post.id) },
                        onToggleFollow = { onToggleFollow(post.authorHandle, post.isFollowingAuthor) }
                    )
                }

                item(key = "feed_footer") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 18.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "You're all caught up on Lumina",
                            style = MaterialTheme.typography.labelMedium,
                            color = LocalLuminaExtras.current.mutedInk
                        )
                    }
                }
            }
        }

        if (showRightSidebar) {
            SuggestedSidebarPane(
                profile = profile,
                suggestedCreators = suggestedCreators,
                onToggleFollow = onToggleFollow,
                onOpenEditProfile = onOpenEditProfile,
                modifier = Modifier
                    .width(308.dp)
                    .padding(top = 14.dp, end = 20.dp, start = 14.dp)
            )
        }
    }
}

@Composable
private fun StoriesBarCard(
    profile: UserProfileEntity,
    stories: List<StoryEntity>,
    onAddStoryClick: () -> Unit,
    onStoryClick: (Int) -> Unit
) {
    val extras = LocalLuminaExtras.current

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, extras.cardBorder),
        shadowElevation = if (extras.isOledDark) 0.dp else 2.dp
    ) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 'Your Story' Add Circle
            item(key = "your_story_add") {
                Column(
                    modifier = Modifier
                        .width(68.dp)
                        .clickable(onClick = onAddStoryClick)
                        .testTag("your_story_add_button"),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(modifier = Modifier.size(64.dp)) {
                        CreatorAvatar(
                            avatarUri = profile.avatarUri,
                            name = profile.displayName,
                            size = 64.dp,
                            showGradientRing = false
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = 1.dp, y = 1.dp)
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(BrandRose500)
                                .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = stringResource(R.string.add_story_title),
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Text(
                        text = stringResource(R.string.your_story),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            itemsIndexed(stories, key = { _, item -> item.id }) { index, story ->
                Column(
                    modifier = Modifier
                        .width(68.dp)
                        .clickable { onStoryClick(index) }
                        .testTag("story_item_${story.id}"),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CreatorAvatar(
                        avatarUri = story.authorAvatar,
                        name = story.authorName,
                        size = 64.dp,
                        showGradientRing = true,
                        isRingMuted = story.isViewed
                    )
                    Text(
                        text = story.authorName,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (story.isViewed) FontWeight.Normal else FontWeight.SemiBold,
                        color = if (story.isViewed) extras.mutedInk else MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun FeedPostCard(
    post: PostEntity,
    commentsCount: Int,
    latestComment: CommentEntity?,
    onToggleLike: () -> Unit,
    onDoubleTapLike: () -> Unit,
    onToggleBookmark: () -> Unit,
    onOpenComments: () -> Unit,
    onOpenShare: () -> Unit,
    onToggleFollow: () -> Unit,
    modifier: Modifier = Modifier
) {
    val extras = LocalLuminaExtras.current
    var heartBurstCount by remember { mutableIntStateOf(0) }
    val likeScale by animateFloatAsState(
        targetValue = if (post.isLiked) 1.15f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "likeScale"
    )
    val likeTint by animateColorAsState(
        targetValue = if (post.isLiked) BrandRose500 else MaterialTheme.colorScheme.onSurface,
        label = "likeTint"
    )
    val filter = remember(post.filterName) { PhotoFilter.fromName(post.filterName) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("feed_post_card_${post.id}"),
        shape = RoundedCornerShape(26.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, extras.cardBorder),
        shadowElevation = if (extras.isOledDark) 0.dp else 3.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Post Author Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 13.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(11.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    CreatorAvatar(
                        avatarUri = post.authorAvatar,
                        name = post.authorName,
                        size = 42.dp,
                        showGradientRing = true
                    )
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = post.authorName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (post.isAuthorVerified) {
                                Icon(
                                    imageVector = Icons.Filled.Verified,
                                    contentDescription = "Verified creator",
                                    tint = BrandRose500,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                        Text(
                            text = "${post.location} • ${formatRelativeTime(post.createdAtMillis)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = extras.mutedInk,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                if (!post.isUserPost) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (post.isFollowingAuthor) extras.subtleSurface else BrandRose500.copy(alpha = 0.12f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable(onClick = onToggleFollow)
                            .testTag("follow_button_${post.id}")
                    ) {
                        Text(
                            text = if (post.isFollowingAuthor) stringResource(R.string.following) else stringResource(R.string.follow),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (post.isFollowingAuthor) MaterialTheme.colorScheme.onSurfaceVariant else BrandRose500,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Media Canvas with Double-Tap Heart Burst + Filter Badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .aspectRatio(post.aspectRatio.coerceIn(0.8f, 1.35f))
                    .pointerInput(post.id) {
                        detectTapGestures(
                            onDoubleTap = {
                                heartBurstCount++
                                onDoubleTapLike()
                            }
                        )
                    }
            ) {
                LuminaMediaView(
                    mediaUri = post.mediaUri,
                    contentDescription = post.caption,
                    filter = filter,
                    modifier = Modifier.fillMaxSize()
                )

                if (filter != PhotoFilter.NORMAL) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Black.copy(alpha = 0.55f))
                            .padding(horizontal = 9.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = filter.displayName,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                HeartBurstOverlay(triggerCount = heartBurstCount)
            }

            // Action Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    IconButton(
                        onClick = {
                            if (!post.isLiked) heartBurstCount++
                            onToggleLike()
                        },
                        modifier = Modifier.testTag("like_button_${post.id}")
                    ) {
                        Icon(
                            imageVector = if (post.isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Like post",
                            tint = likeTint,
                            modifier = Modifier
                                .size(24.dp)
                                .scale(likeScale)
                        )
                    }
                    Text(
                        text = formatCompactCount(post.likesCount),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = onOpenComments,
                        modifier = Modifier.testTag("comment_button_${post.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ChatBubbleOutline,
                            contentDescription = "Comments",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Text(
                        text = commentsCount.toString(),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = onOpenShare,
                        modifier = Modifier.testTag("share_button_${post.id}")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.Send,
                            contentDescription = "Share post",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(21.dp)
                        )
                    }
                }

                IconButton(
                    onClick = onToggleBookmark,
                    modifier = Modifier.testTag("bookmark_button_${post.id}")
                ) {
                    Icon(
                        imageVector = if (post.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Bookmark post",
                        tint = if (post.isBookmarked) BrandRose500 else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(23.dp)
                    )
                }
            }

            // Caption with highlighted hashtags
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val annotatedCaption = remember(post.authorHandle, post.caption) {
                    buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(post.authorHandle.removePrefix("@"))
                            append("  ")
                        }
                        post.caption.split(" ").forEachIndexed { idx, word ->
                            if (idx > 0) append(" ")
                            if (word.startsWith("#")) {
                                withStyle(SpanStyle(color = BrandRose500, fontWeight = FontWeight.SemiBold)) {
                                    append(word)
                                }
                            } else {
                                append(word)
                            }
                        }
                    }
                }
                Text(
                    text = annotatedCaption,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (latestComment != null) {
                    Text(
                        text = "${latestComment.authorName}: ${latestComment.text}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.clickable(onClick = onOpenComments)
                    )
                }

                if (commentsCount > 0) {
                    Text(
                        text = "View all $commentsCount comments",
                        style = MaterialTheme.typography.labelMedium,
                        color = extras.mutedInk,
                        modifier = Modifier.clickable(onClick = onOpenComments)
                    )
                }
            }
        }
    }
}

@Composable
private fun SuggestedSidebarPane(
    profile: UserProfileEntity,
    suggestedCreators: List<SuggestedCreator>,
    onToggleFollow: (String, Boolean) -> Unit,
    onOpenEditProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val extras = LocalLuminaExtras.current

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Mini Profile Card
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, extras.cardBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    CreatorAvatar(
                        avatarUri = profile.avatarUri,
                        name = profile.displayName,
                        size = 46.dp,
                        showGradientRing = true
                    )
                    Column {
                        Text(
                            text = profile.displayName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = profile.handle,
                            style = MaterialTheme.typography.labelSmall,
                            color = extras.mutedInk
                        )
                    }
                }
                Text(
                    text = "Edit",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = BrandRose500,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(onClick = onOpenEditProfile)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        // Suggested Creators Card
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, extras.cardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = stringResource(R.string.suggested_for_you),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                suggestedCreators.forEach { creator ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            CreatorAvatar(
                                avatarUri = creator.avatarUri,
                                name = creator.name,
                                size = 38.dp
                            )
                            Column {
                                Text(
                                    text = creator.name,
                                    style = MaterialTheme.typography.labelLarge,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = creator.subtitle,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = extras.mutedInk,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                        Text(
                            text = if (creator.isFollowing) stringResource(R.string.following) else stringResource(R.string.follow),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (creator.isFollowing) extras.mutedInk else BrandRose500,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onToggleFollow(creator.handle, creator.isFollowing) }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
