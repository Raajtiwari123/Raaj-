package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.PhotoFilter
import com.example.data.PostEntity
import com.example.data.UserProfileEntity
import com.example.ui.components.CreatorAvatar
import com.example.ui.components.LuminaMediaView
import com.example.ui.components.formatCompactCount
import com.example.ui.theme.BrandRose500
import com.example.ui.theme.LocalLuminaExtras

@Composable
fun ProfileScreen(
    profile: UserProfileEntity,
    userPosts: List<PostEntity>,
    savedPosts: List<PostEntity>,
    onEditProfileClick: () -> Unit,
    onShareProfileClick: () -> Unit,
    onPostClick: (PostEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val extras = LocalLuminaExtras.current
    var selectedSegment by remember { mutableIntStateOf(0) } // 0 = Posts, 1 = Saved
    val displayedPosts = if (selectedSegment == 0) userPosts else savedPosts

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 620.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Hero Card
            item(key = "profile_hero") {
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, extras.cardBorder),
                    shadowElevation = if (extras.isOledDark) 0.dp else 3.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CreatorAvatar(
                            avatarUri = profile.avatarUri,
                            name = profile.displayName,
                            size = 92.dp,
                            showGradientRing = true
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Text(
                                    text = profile.displayName,
                                    style = MaterialTheme.typography.headlineSmall
                                )
                                Icon(
                                    imageVector = Icons.Filled.Verified,
                                    contentDescription = "Verified",
                                    tint = BrandRose500,
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                            Text(
                                text = profile.handle,
                                style = MaterialTheme.typography.labelLarge,
                                color = extras.mutedInk
                            )
                        }

                        if (profile.bio.isNotBlank()) {
                            Text(
                                text = profile.bio,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }

                        if (profile.website.isNotBlank()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Link,
                                    contentDescription = null,
                                    tint = BrandRose500,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = profile.website,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = BrandRose500
                                )
                            }
                        }

                        // Stats Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(extras.subtleSurface)
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ProfileStatColumn(value = userPosts.size.toString(), label = "Posts")
                            ProfileStatColumn(value = formatCompactCount(profile.followersCount), label = "Followers")
                            ProfileStatColumn(value = formatCompactCount(profile.followingCount), label = "Following")
                        }

                        // Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = onEditProfileClick,
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                contentPadding = PaddingValues(),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .testTag("edit_profile_button")
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(extras.brandGradient, RoundedCornerShape(16.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Edit,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = stringResource(R.string.edit_profile),
                                            color = Color.White,
                                            style = MaterialTheme.typography.labelLarge
                                        )
                                    }
                                }
                            }

                            OutlinedButton(
                                onClick = onShareProfileClick,
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, extras.cardBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .testTag("share_profile_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Share,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = stringResource(R.string.share_profile),
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }
                }
            }

            // Segmented Tabs: Posts / Saved
            item(key = "profile_segments") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ProfileSegmentTab(
                        label = "${stringResource(R.string.posts_tab)} (${userPosts.size})",
                        icon = Icons.Filled.GridView,
                        selected = selectedSegment == 0,
                        onClick = { selectedSegment = 0 },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("profile_tab_posts")
                    )
                    ProfileSegmentTab(
                        label = "${stringResource(R.string.saved_tab)} (${savedPosts.size})",
                        icon = Icons.Filled.Bookmark,
                        selected = selectedSegment == 1,
                        onClick = { selectedSegment = 1 },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("profile_tab_saved")
                    )
                }
            }

            // Grid of Posts or Saved
            if (displayedPosts.isEmpty()) {
                item(key = "empty_grid") {
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, extras.cardBorder)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.no_saved_posts),
                                style = MaterialTheme.typography.bodyMedium,
                                color = extras.mutedInk,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                val rows = displayedPosts.chunked(3)
                items(rows.size, key = { "grid_row_$selectedSegment-$it" }) { rowIndex ->
                    val rowItems = rows[rowIndex]
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowItems.forEach { post ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable { onPostClick(post) }
                            ) {
                                LuminaMediaView(
                                    mediaUri = post.mediaUri,
                                    contentDescription = post.caption,
                                    filter = PhotoFilter.fromName(post.filterName),
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                        repeat(3 - rowItems.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileStatColumn(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = LocalLuminaExtras.current.mutedInk
        )
    }
}

@Composable
private fun ProfileSegmentTab(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val extras = LocalLuminaExtras.current
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) extras.subtleSurface else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected) BrandRose500 else extras.mutedInk,
            modifier = Modifier.size(17.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) MaterialTheme.colorScheme.onSurface else extras.mutedInk,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun EditProfileDialog(
    profile: UserProfileEntity,
    onSave: (displayName: String, handle: String, bio: String, website: String, avatarUri: String) -> Unit,
    onDismiss: () -> Unit
) {
    val extras = LocalLuminaExtras.current
    var name by remember { mutableStateOf(profile.displayName) }
    var handle by remember { mutableStateOf(profile.handle) }
    var bio by remember { mutableStateOf(profile.bio) }
    var website by remember { mutableStateOf(profile.website) }
    var avatarUri by remember { mutableStateOf(profile.avatarUri) }

    val avatarPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) avatarUri = uri.toString()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(26.dp),
        title = {
            Text(
                text = stringResource(R.string.edit_profile),
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clickable {
                            avatarPicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                ) {
                    CreatorAvatar(
                        avatarUri = avatarUri,
                        name = name,
                        size = 80.dp,
                        showGradientRing = true
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(BrandRose500),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CameraAlt,
                            contentDescription = "Change avatar",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Display Name") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandRose500,
                        unfocusedBorderColor = extras.cardBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = handle,
                    onValueChange = { handle = it },
                    label = { Text("Handle") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandRose500,
                        unfocusedBorderColor = extras.cardBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Bio") },
                    minLines = 2,
                    maxLines = 3,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandRose500,
                        unfocusedBorderColor = extras.cardBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = website,
                    onValueChange = { website = it },
                    label = { Text("Website") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandRose500,
                        unfocusedBorderColor = extras.cardBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name, handle, bio, website, avatarUri) },
                colors = ButtonDefaults.buttonColors(containerColor = BrandRose500),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.testTag("save_profile_button")
            ) {
                Text("Save", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
