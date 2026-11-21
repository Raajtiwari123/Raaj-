package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.PhotoFilter
import com.example.data.ReelItem
import com.example.ui.components.AudioEqualizerBars
import com.example.ui.components.CreatorAvatar
import com.example.ui.components.HeartBurstOverlay
import com.example.ui.components.LuminaMediaView
import com.example.ui.components.formatCompactCount
import com.example.ui.theme.BrandRose500

@Composable
fun ReelsScreen(
    reels: List<ReelItem>,
    onToggleLike: (String) -> Unit,
    onDoubleTapLike: (String) -> Unit,
    onToggleBookmark: (String) -> Unit,
    onToggleFollow: (String, Boolean) -> Unit,
    onShareReel: (ReelItem) -> Unit,
    onCommentReel: (ReelItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { reels.size })

    VerticalPager(
        state = pagerState,
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("reels_vertical_pager")
    ) { page ->
        val reel = reels[page]
        val isCurrentPage = pagerState.currentPage == page
        ReelCardPage(
            reel = reel,
            isActive = isCurrentPage,
            onToggleLike = { onToggleLike(reel.id) },
            onDoubleTapLike = { onDoubleTapLike(reel.id) },
            onToggleBookmark = { onToggleBookmark(reel.id) },
            onToggleFollow = { onToggleFollow(reel.creatorHandle, reel.isFollowing) },
            onShare = { onShareReel(reel) },
            onComment = { onCommentReel(reel) }
        )
    }
}

@Composable
private fun ReelCardPage(
    reel: ReelItem,
    isActive: Boolean,
    onToggleLike: () -> Unit,
    onDoubleTapLike: () -> Unit,
    onToggleBookmark: () -> Unit,
    onToggleFollow: () -> Unit,
    onShare: () -> Unit,
    onComment: () -> Unit
) {
    var isPaused by remember(reel.id) { mutableStateOf(false) }
    var heartTrigger by remember(reel.id) { mutableIntStateOf(0) }
    val isPlaying = isActive && !isPaused

    val motionTransition = rememberInfiniteTransition(label = "reelMotion")
    val kenBurnsScale by motionTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.11f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "kenBurns"
    )
    val discRotation by motionTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5500, easing = LinearEasing)
        ),
        label = "vinylSpin"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(reel.id) {
                detectTapGestures(
                    onTap = { isPaused = !isPaused },
                    onDoubleTap = {
                        heartTrigger++
                        onDoubleTapLike()
                    }
                )
            }
    ) {
        LuminaMediaView(
            mediaUri = reel.mediaUri,
            contentDescription = reel.caption,
            filter = PhotoFilter.fromName(reel.filterName),
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    val s = if (isPlaying) kenBurnsScale else 1.02f
                    scaleX = s
                    scaleY = s
                }
        )

        // Cinematic dark gradient overlays
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(
                    Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.5f), Color.Transparent))
                )
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(290.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.88f)))
                )
        )

        // Reels top badge
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Reels",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(BrandRose500.copy(alpha = 0.85f))
                    .padding(horizontal = 7.dp, vertical = 2.dp)
            ) {
                Text(
                    text = if (isPlaying) "LIVE MOTION" else "PAUSED",
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Play / Pause subtle indicator
        AnimatedVisibility(
            visible = isPaused,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.55f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Paused",
                    tint = Color.White,
                    modifier = Modifier.size(38.dp)
                )
            }
        }

        HeartBurstOverlay(triggerCount = heartTrigger)

        // Right-side floating action rail
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 14.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            ReelActionColumnButton(
                icon = if (reel.isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                tint = if (reel.isLiked) BrandRose500 else Color.White,
                label = formatCompactCount(reel.likesCount),
                onClick = {
                    if (!reel.isLiked) heartTrigger++
                    onToggleLike()
                },
                modifier = Modifier.testTag("reel_like_${reel.id}")
            )
            ReelActionColumnButton(
                icon = Icons.Outlined.ChatBubbleOutline,
                tint = Color.White,
                label = formatCompactCount(reel.commentsCount),
                onClick = onComment
            )
            ReelActionColumnButton(
                icon = Icons.AutoMirrored.Filled.Send,
                tint = Color.White,
                label = formatCompactCount(reel.sharesCount),
                onClick = onShare
            )
            ReelActionColumnButton(
                icon = if (reel.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                tint = if (reel.isBookmarked) BrandRose500 else Color.White,
                label = if (reel.isBookmarked) "Saved" else "Save",
                onClick = onToggleBookmark
            )

            // Spinning Audio Vinyl Disc
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .rotate(if (isPlaying) discRotation else 0f)
                    .clip(CircleShape)
                    .background(Color(0xFF18181B))
                    .border(2.dp, Color.White.copy(alpha = 0.7f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                CreatorAvatar(
                    avatarUri = reel.creatorAvatar,
                    name = reel.creatorName,
                    size = 24.dp
                )
            }
        }

        // Bottom-left creator metadata + animated 4-bar Audio Visualizer Equalizer
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.78f)
                .padding(start = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CreatorAvatar(
                    avatarUri = reel.creatorAvatar,
                    name = reel.creatorName,
                    size = 40.dp,
                    showGradientRing = true
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = reel.creatorHandle,
                        color = Color.White,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    if (reel.isVerified) {
                        Icon(
                            imageVector = Icons.Filled.Verified,
                            contentDescription = null,
                            tint = BrandRose500,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (reel.isFollowing) Color.White.copy(alpha = 0.22f) else BrandRose500)
                        .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .clickable(onClick = onToggleFollow)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (reel.isFollowing) stringResource(R.string.following) else stringResource(R.string.follow),
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = reel.caption,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Audio Equalizer Track Pill
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.Black.copy(alpha = 0.45f))
                    .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AudioEqualizerBars(isPlaying = isPlaying)
                Icon(
                    imageVector = Icons.Filled.MusicNote,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = reel.audioTitle,
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ReelActionColumnButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.36f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(25.dp)
            )
        }
        Text(
            text = label,
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}
