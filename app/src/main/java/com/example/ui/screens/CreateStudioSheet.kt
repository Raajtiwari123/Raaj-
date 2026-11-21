package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.LuminaSeedData
import com.example.data.PhotoFilter
import com.example.ui.components.LuminaMediaView
import com.example.ui.theme.BrandRose500
import com.example.ui.theme.LocalLuminaExtras
import com.example.viewmodel.CreateMode

private val studioPresets = listOf(
    "Tokyo" to LuminaSeedData.MEDIA_TOKYO,
    "Cafe" to LuminaSeedData.MEDIA_CAFE,
    "Amalfi" to LuminaSeedData.MEDIA_COAST,
    "Aurora" to LuminaSeedData.MEDIA_AURORA,
    "Dusk" to LuminaSeedData.MEDIA_DUSK,
    "Arch" to LuminaSeedData.MEDIA_ARCHITECT
)

@Composable
fun CreateStudioOverlay(
    mode: CreateMode,
    onPublishPost: (mediaUri: String, filter: PhotoFilter, caption: String, location: String, category: String) -> Unit,
    onPublishStory: (mediaUri: String, filter: PhotoFilter, caption: String) -> Unit,
    onDismiss: () -> Unit
) {
    BackHandler(onBack = onDismiss)
    val extras = LocalLuminaExtras.current

    var selectedMediaUri by remember { mutableStateOf(LuminaSeedData.MEDIA_COAST) }
    var selectedFilter by remember { mutableStateOf(PhotoFilter.NORMAL) }
    var showRuleOfThirdsGrid by remember { mutableStateOf(true) }
    var caption by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Editorial") }

    // Zero-permission Android Photo Picker (Google Play compliant)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedMediaUri = uri.toString()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Studio Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("create_close_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close studio"
                    )
                }
                Text(
                    text = if (mode == CreateMode.POST) {
                        stringResource(R.string.create_post_title)
                    } else {
                        stringResource(R.string.add_story_title)
                    },
                    style = MaterialTheme.typography.titleLarge
                )
                IconButton(
                    onClick = { showRuleOfThirdsGrid = !showRuleOfThirdsGrid },
                    modifier = Modifier.testTag("toggle_crop_grid_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.GridOn,
                        contentDescription = stringResource(R.string.rule_of_thirds),
                        tint = if (showRuleOfThirdsGrid) BrandRose500 else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            HorizontalDivider(color = extras.cardBorder)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .widthIn(max = 560.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Live Filtered Preview + Rule of Thirds Crop Grid
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .aspectRatio(if (mode == CreateMode.POST) 1.15f else 0.85f)
                        .border(1.dp, extras.cardBorder, RoundedCornerShape(24.dp))
                ) {
                    LuminaMediaView(
                        mediaUri = selectedMediaUri,
                        contentDescription = "Selected preview",
                        filter = selectedFilter,
                        modifier = Modifier.fillMaxSize()
                    )

                    if (showRuleOfThirdsGrid) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val strokeColor = Color.White.copy(alpha = 0.36f)
                            val w = size.width
                            val h = size.height
                            drawLine(strokeColor, Offset(w / 3f, 0f), Offset(w / 3f, h), strokeWidth = 1.5f)
                            drawLine(strokeColor, Offset(2f * w / 3f, 0f), Offset(2f * w / 3f, h), strokeWidth = 1.5f)
                            drawLine(strokeColor, Offset(0f, h / 3f), Offset(w, h / 3f), strokeWidth = 1.5f)
                            drawLine(strokeColor, Offset(0f, 2f * h / 3f), Offset(w, 2f * h / 3f), strokeWidth = 1.5f)
                        }
                    }

                    // Active filter badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.58f))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "Filter: ${selectedFilter.displayName}",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Gallery Photo Picker + Studio Presets
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Media Source",
                        style = MaterialTheme.typography.titleSmall
                    )
                    OutlinedButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, BrandRose500.copy(alpha = 0.5f)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("pick_gallery_photo_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PhotoLibrary,
                            contentDescription = null,
                            tint = BrandRose500,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.pick_photo),
                            style = MaterialTheme.typography.labelMedium,
                            color = BrandRose500
                        )
                    }
                }

                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(studioPresets) { (label, uri) ->
                        val isSelected = selectedMediaUri == uri
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.clickable { selectedMediaUri = uri }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(62.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(
                                        width = if (isSelected) 2.5.dp else 1.dp,
                                        color = if (isSelected) BrandRose500 else extras.cardBorder,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                            ) {
                                LuminaMediaView(
                                    mediaUri = uri,
                                    contentDescription = label,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) BrandRose500 else extras.mutedInk,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                // Live Filter Carousel (Normal, Clarendon, Moon, Lark, Warm)
                Text(
                    text = "Editorial Filters",
                    style = MaterialTheme.typography.titleSmall
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(PhotoFilter.entries) { filter ->
                        val active = filter == selectedFilter
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .clickable { selectedFilter = filter }
                                .testTag("filter_option_${filter.name.lowercase()}")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .border(
                                        width = if (active) 2.5.dp else 1.dp,
                                        color = if (active) BrandRose500 else extras.cardBorder,
                                        shape = RoundedCornerShape(18.dp)
                                    )
                            ) {
                                LuminaMediaView(
                                    mediaUri = selectedMediaUri,
                                    contentDescription = filter.displayName,
                                    filter = filter,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Text(
                                text = filter.displayName,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                                color = if (active) BrandRose500 else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Caption Input
                OutlinedTextField(
                    value = caption,
                    onValueChange = { caption = it },
                    placeholder = { Text(stringResource(R.string.write_caption_hint)) },
                    minLines = 3,
                    maxLines = 4,
                    shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandRose500,
                        unfocusedBorderColor = extras.cardBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("create_caption_input")
                )

                if (mode == CreateMode.POST) {
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        placeholder = { Text(stringResource(R.string.location_hint)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = null,
                                tint = BrandRose500
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(18.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandRose500,
                            unfocusedBorderColor = extras.cardBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("create_location_input")
                    )

                    // Category selector
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(LuminaSeedData.exploreCategories.drop(1)) { cat ->
                            val active = cat == selectedCategory
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (active) BrandRose500 else MaterialTheme.colorScheme.surface,
                                border = if (active) null else BorderStroke(1.dp, extras.cardBorder),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable { selectedCategory = cat }
                            ) {
                                Text(
                                    text = cat,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (active) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }

                // Primary Publish CTA
                Button(
                    onClick = {
                        if (mode == CreateMode.POST) {
                            onPublishPost(selectedMediaUri, selectedFilter, caption, location, selectedCategory)
                        } else {
                            onPublishStory(selectedMediaUri, selectedFilter, caption)
                        }
                    },
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("publish_cta_button")
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(extras.brandGradient, RoundedCornerShape(18.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (mode == CreateMode.POST) {
                                stringResource(R.string.publish_post)
                            } else {
                                stringResource(R.string.share_story)
                            },
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
