package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.NotificationEntity
import com.example.data.NotificationType
import com.example.ui.components.CreatorAvatar
import com.example.ui.components.LuminaMediaView
import com.example.ui.theme.BrandAmber500
import com.example.ui.theme.BrandIndigo400
import com.example.ui.theme.BrandRose500
import com.example.ui.theme.LocalLuminaExtras

@Composable
fun NotificationsScreen(
    notifications: List<NotificationEntity>,
    onToggleFollow: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val todayItems = remember(notifications) { notifications.filter { it.isToday } }
    val earlierItems = remember(notifications) { notifications.filter { !it.isToday } }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 580.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(key = "header") {
                Text(
                    text = stringResource(R.string.nav_notifications),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            if (todayItems.isNotEmpty()) {
                item(key = "today_label") {
                    Text(
                        text = "Today",
                        style = MaterialTheme.typography.titleSmall,
                        color = LocalLuminaExtras.current.mutedInk
                    )
                }
                items(todayItems, key = { it.id }) { item ->
                    NotificationRowCard(
                        item = item,
                        onToggleFollow = { onToggleFollow(item.actorHandle, item.isFollowingActor) }
                    )
                }
            }

            if (earlierItems.isNotEmpty()) {
                item(key = "earlier_label") {
                    Text(
                        text = "This Week",
                        style = MaterialTheme.typography.titleSmall,
                        color = LocalLuminaExtras.current.mutedInk,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                items(earlierItems, key = { it.id }) { item ->
                    NotificationRowCard(
                        item = item,
                        onToggleFollow = { onToggleFollow(item.actorHandle, item.isFollowingActor) }
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationRowCard(
    item: NotificationEntity,
    onToggleFollow: () -> Unit
) {
    val extras = LocalLuminaExtras.current
    val notifType = runCatching { NotificationType.valueOf(item.type) }.getOrDefault(NotificationType.LIKE)

    val (badgeIcon, badgeBg) = when (notifType) {
        NotificationType.LIKE -> Icons.Filled.Favorite to BrandRose500
        NotificationType.COMMENT -> Icons.Filled.ChatBubble to BrandIndigo400
        NotificationType.FOLLOW -> Icons.Filled.PersonAdd to BrandAmber500
        NotificationType.MENTION -> Icons.Filled.AlternateEmail to BrandRose500
    }

    Surface(
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, extras.cardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("notification_card_${item.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(modifier = Modifier.size(48.dp)) {
                CreatorAvatar(
                    avatarUri = item.actorAvatar,
                    name = item.actorName,
                    size = 46.dp
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(badgeBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = badgeIcon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(11.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(item.actorName)
                            append(" ")
                        }
                        append(item.message)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = item.timeLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = extras.mutedInk
                )
            }

            if (notifType == NotificationType.FOLLOW) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (item.isFollowingActor) extras.subtleSurface else BrandRose500,
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .clickable(onClick = onToggleFollow)
                        .testTag("notif_follow_button_${item.id}")
                ) {
                    Text(
                        text = if (item.isFollowingActor) stringResource(R.string.following) else stringResource(R.string.follow),
                        color = if (item.isFollowingActor) MaterialTheme.colorScheme.onSurfaceVariant else Color.White,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                    )
                }
            } else if (item.postThumbnailUri != null) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    LuminaMediaView(
                        mediaUri = item.postThumbnailUri,
                        contentDescription = "Post preview",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
