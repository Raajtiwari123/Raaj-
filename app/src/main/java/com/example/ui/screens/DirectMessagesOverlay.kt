package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.DmMessageEntity
import com.example.data.DmThreadEntity
import com.example.ui.components.CreatorAvatar
import com.example.ui.components.TypingDotsIndicator
import com.example.ui.components.formatRelativeTime
import com.example.ui.theme.BrandEmerald500
import com.example.ui.theme.BrandRose500
import com.example.ui.theme.LocalLuminaExtras

@Composable
fun DirectMessagesOverlay(
    threads: List<DmThreadEntity>,
    activeThreadId: String?,
    messagesByThread: Map<String, List<DmMessageEntity>>,
    typingThreadIds: Set<String>,
    onSelectThread: (String) -> Unit,
    onBackFromThread: () -> Unit,
    onSendMessage: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    BackHandler {
        if (activeThreadId != null) onBackFromThread() else onDismiss()
    }

    val activeThread = threads.firstOrNull { it.id == activeThreadId }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (activeThread != null) {
            DmChatThreadView(
                thread = activeThread,
                messages = messagesByThread[activeThread.id].orEmpty(),
                isTyping = typingThreadIds.contains(activeThread.id),
                onBack = onBackFromThread,
                onSendMessage = { text -> onSendMessage(activeThread.id, text) }
            )
        } else {
            DmInboxListView(
                threads = threads,
                onSelectThread = onSelectThread,
                onDismiss = onDismiss
            )
        }
    }
}

@Composable
private fun DmInboxListView(
    threads: List<DmThreadEntity>,
    onSelectThread: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val extras = LocalLuminaExtras.current
    var searchQuery by remember { mutableStateOf("") }
    val filteredThreads = remember(threads, searchQuery) {
        val q = searchQuery.trim().lowercase()
        if (q.isEmpty()) threads else threads.filter {
            it.participantName.lowercase().contains(q) ||
                it.participantHandle.lowercase().contains(q) ||
                it.lastMessagePreview.lowercase().contains(q)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("dm_close_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close messages"
                    )
                }
                Text(
                    text = stringResource(R.string.direct_messages),
                    style = MaterialTheme.typography.titleLarge
                )
            }
            val totalUnread = threads.sumOf { it.unreadCount }
            if (totalUnread > 0) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(BrandRose500.copy(alpha = 0.14f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$totalUnread unread",
                        color = BrandRose500,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        HorizontalDivider(color = extras.cardBorder)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 600.dp)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search messages...", color = extras.mutedInk) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        tint = extras.mutedInk
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = BrandRose500,
                    unfocusedBorderColor = extras.cardBorder
                ),
                modifier = Modifier.fillMaxWidth()
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredThreads, key = { it.id }) { thread ->
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, extras.cardBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { onSelectThread(thread.id) }
                            .testTag("dm_thread_${thread.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(modifier = Modifier.size(50.dp)) {
                                CreatorAvatar(
                                    avatarUri = thread.participantAvatar,
                                    name = thread.participantName,
                                    size = 50.dp
                                )
                                if (thread.isOnline) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .size(13.dp)
                                            .clip(CircleShape)
                                            .background(BrandEmerald500)
                                            .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                                    )
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = thread.participantName,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = formatRelativeTime(thread.updatedAtMillis),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = extras.mutedInk
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = thread.lastMessagePreview,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (thread.unreadCount > 0) MaterialTheme.colorScheme.onSurface else extras.mutedInk,
                                    fontWeight = if (thread.unreadCount > 0) FontWeight.SemiBold else FontWeight.Normal,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            if (thread.unreadCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(BrandRose500),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = thread.unreadCount.toString(),
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DmChatThreadView(
    thread: DmThreadEntity,
    messages: List<DmMessageEntity>,
    isTyping: Boolean,
    onBack: () -> Unit,
    onSendMessage: (String) -> Unit
) {
    val extras = LocalLuminaExtras.current
    var draft by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size, isTyping) {
        val targetIndex = (messages.size + if (isTyping) 1 else 0) - 1
        if (targetIndex >= 0) {
            listState.animateScrollToItem(targetIndex)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Chat Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("dm_chat_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to inbox"
                )
            }
            Box(modifier = Modifier.size(40.dp)) {
                CreatorAvatar(
                    avatarUri = thread.participantAvatar,
                    name = thread.participantName,
                    size = 40.dp
                )
                if (thread.isOnline) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(11.dp)
                            .clip(CircleShape)
                            .background(BrandEmerald500)
                            .border(2.dp, MaterialTheme.colorScheme.background, CircleShape)
                    )
                }
            }
            Column {
                Text(
                    text = thread.participantName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isTyping) "Typing..." else if (thread.isOnline) "Active now" else thread.participantHandle,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isTyping || thread.isOnline) BrandEmerald500 else extras.mutedInk
                )
            }
        }
        HorizontalDivider(color = extras.cardBorder)

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .widthIn(max = 600.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (msg.senderIsMe) Arrangement.End else Arrangement.Start
                ) {
                    val bubbleShape = RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomStart = if (msg.senderIsMe) 20.dp else 6.dp,
                        bottomEnd = if (msg.senderIsMe) 6.dp else 20.dp
                    )
                    if (msg.senderIsMe) {
                        Box(
                            modifier = Modifier
                                .widthIn(max = 290.dp)
                                .clip(bubbleShape)
                                .background(extras.brandGradient)
                                .padding(horizontal = 15.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = msg.text,
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
                        Surface(
                            shape = bubbleShape,
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, extras.cardBorder),
                            modifier = Modifier.widthIn(max = 290.dp)
                        ) {
                            Text(
                                text = msg.text,
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp)
                            )
                        }
                    }
                }
            }

            if (isTyping) {
                item(key = "typing_indicator") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, extras.cardBorder)
                        ) {
                            TypingDotsIndicator(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                            )
                        }
                    }
                }
            }
        }

        HorizontalDivider(color = extras.cardBorder)

        // Message Composer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = draft,
                onValueChange = { draft = it },
                placeholder = { Text(stringResource(R.string.message_hint), color = extras.mutedInk) },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = BrandRose500,
                    unfocusedBorderColor = extras.cardBorder
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("dm_message_input")
            )
            IconButton(
                onClick = {
                    if (draft.isNotBlank()) {
                        onSendMessage(draft)
                        draft = ""
                    } else {
                        onSendMessage("❤️")
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(extras.brandGradient)
                    .testTag("dm_send_button")
            ) {
                Icon(
                    imageVector = if (draft.isNotBlank()) Icons.AutoMirrored.Filled.Send else Icons.Filled.Favorite,
                    contentDescription = "Send message",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
