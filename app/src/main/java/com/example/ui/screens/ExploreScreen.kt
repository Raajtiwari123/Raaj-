package com.example.ui.screens

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.CommentEntity
import com.example.data.LuminaSeedData
import com.example.data.PhotoFilter
import com.example.data.PostEntity
import com.example.ui.components.LuminaMediaView
import com.example.ui.components.formatCompactCount
import com.example.ui.theme.BrandRose500
import com.example.ui.theme.LocalLuminaExtras

@Composable
fun ExploreScreen(
    posts: List<PostEntity>,
    query: String,
    selectedCategory: String,
    isExpandedWidth: Boolean,
    onQueryChange: (String) -> Unit,
    onSelectCategory: (String) -> Unit,
    onPostClick: (PostEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val extras = LocalLuminaExtras.current
    val filteredPosts = remember(posts, query, selectedCategory) {
        posts.filter { post ->
            val matchesCategory = selectedCategory == "For You" ||
                post.category.equals(selectedCategory, ignoreCase = true)
            val q = query.trim().lowercase()
            val matchesQuery = q.isEmpty() ||
                post.caption.lowercase().contains(q) ||
                post.authorName.lowercase().contains(q) ||
                post.authorHandle.lowercase().contains(q) ||
                post.location.lowercase().contains(q) ||
                post.category.lowercase().contains(q)
            matchesCategory && matchesQuery
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 900.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = {
                    Text(
                        text = stringResource(R.string.search_explore_hint),
                        color = extras.mutedInk
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        tint = extras.mutedInk
                    )
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Clear search",
                                tint = extras.mutedInk
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = BrandRose500,
                    unfocusedBorderColor = extras.cardBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("explore_search_input")
            )

            // Category Pills
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 2.dp)
            ) {
                items(LuminaSeedData.exploreCategories) { category ->
                    val selected = category == selectedCategory
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (selected) BrandRose500 else MaterialTheme.colorScheme.surface,
                        border = if (selected) null else BorderStroke(1.dp, extras.cardBorder),
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onSelectCategory(category) }
                            .testTag("explore_category_$category")
                    ) {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp)
                        )
                    }
                }
            }

            if (filteredPosts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_search_results),
                        style = MaterialTheme.typography.bodyMedium,
                        color = extras.mutedInk,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(if (isExpandedWidth) 3 else 2),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalItemSpacing = 10.dp,
                    contentPadding = PaddingValues(bottom = 24.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(filteredPosts, key = { _, p -> p.id }) { index, post ->
                        val tileRatio = when (index % 4) {
                            0 -> 0.82f
                            1 -> 1.12f
                            2 -> 1.0f
                            else -> 0.78f
                        }
                        ExploreGridTile(
                            post = post,
                            aspectRatio = tileRatio,
                            onClick = { onPostClick(post) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExploreGridTile(
    post: PostEntity,
    aspectRatio: Float,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .aspectRatio(aspectRatio)
            .clickable(onClick = onClick)
            .testTag("explore_tile_${post.id}")
    ) {
        LuminaMediaView(
            mediaUri = post.mediaUri,
            contentDescription = post.caption,
            filter = PhotoFilter.fromName(post.filterName),
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.45f to Color.Transparent,
                        1.0f to Color.Black.copy(alpha = 0.72f)
                    )
                )
        )

        // Category badge top-left
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(10.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.Black.copy(alpha = 0.45f))
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Text(
                text = post.category,
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Creator & likes bottom overlay
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = post.authorHandle,
                color = Color.White,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = null,
                    tint = if (post.isLiked) BrandRose500 else Color.White,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = formatCompactCount(post.likesCount),
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun PostDetailDialog(
    post: PostEntity,
    comments: List<CommentEntity>,
    onToggleLike: () -> Unit,
    onDoubleTapLike: () -> Unit,
    onToggleBookmark: () -> Unit,
    onOpenComments: () -> Unit,
    onOpenShare: () -> Unit,
    onToggleFollow: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.widthIn(max = 520.dp),
                horizontalAlignment = Alignment.End
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .padding(bottom = 6.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.92f))
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close detail",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                FeedPostCard(
                    post = post,
                    commentsCount = comments.size,
                    latestComment = comments.lastOrNull(),
                    onToggleLike = onToggleLike,
                    onDoubleTapLike = onDoubleTapLike,
                    onToggleBookmark = onToggleBookmark,
                    onOpenComments = onOpenComments,
                    onOpenShare = onOpenShare,
                    onToggleFollow = onToggleFollow
                )
            }
        }
    }
}
