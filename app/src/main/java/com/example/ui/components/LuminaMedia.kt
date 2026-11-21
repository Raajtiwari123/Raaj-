package com.example.ui.components

import android.net.Uri
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.LuminaSeedData
import com.example.data.PhotoFilter
import com.example.ui.theme.BrandAmber400
import com.example.ui.theme.BrandPink500
import com.example.ui.theme.BrandRose400
import com.example.ui.theme.BrandRose500
import com.example.ui.theme.LocalLuminaExtras

/**
 * Renders bundled editorial photography drawables, procedural studio art backdrops,
 * or user-picked Android Photo Picker URIs via Coil, with live PhotoFilter ColorMatrix support.
 */
@Composable
fun LuminaMediaView(
    mediaUri: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
    filter: PhotoFilter = PhotoFilter.NORMAL,
    contentScale: ContentScale = ContentScale.Crop
) {
    val colorFilter: ColorFilter? = remember(filter) { filter.toColorFilter() }

    when (mediaUri) {
        LuminaSeedData.MEDIA_ARIA -> {
            Image(
                painter = painterResource(id = R.drawable.img_aria_avatar_1795266182532),
                contentDescription = contentDescription,
                contentScale = contentScale,
                colorFilter = colorFilter,
                modifier = modifier
            )
        }
        LuminaSeedData.MEDIA_TOKYO -> {
            Image(
                painter = painterResource(id = R.drawable.img_feed_tokyo_1795266186420),
                contentDescription = contentDescription,
                contentScale = contentScale,
                colorFilter = colorFilter,
                modifier = modifier
            )
        }
        LuminaSeedData.MEDIA_CAFE -> {
            Image(
                painter = painterResource(id = R.drawable.img_feed_cafe_1795266563399),
                contentDescription = contentDescription,
                contentScale = contentScale,
                colorFilter = colorFilter,
                modifier = modifier
            )
        }
        LuminaSeedData.MEDIA_COAST -> {
            Image(
                painter = painterResource(id = R.drawable.img_feed_coast_1795266172916),
                contentDescription = contentDescription,
                contentScale = contentScale,
                colorFilter = colorFilter,
                modifier = modifier
            )
        }
        LuminaSeedData.MEDIA_AURORA,
        LuminaSeedData.MEDIA_DUSK,
        LuminaSeedData.MEDIA_ARCHITECT,
        LuminaSeedData.MEDIA_BOTANICAL,
        LuminaSeedData.MEDIA_CERAMIC -> {
            StudioEditorialArtwork(
                artKey = mediaUri,
                filter = filter,
                modifier = modifier
            )
        }
        else -> {
            AsyncImage(
                model = Uri.parse(mediaUri),
                contentDescription = contentDescription,
                contentScale = contentScale,
                colorFilter = colorFilter,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun StudioEditorialArtwork(
    artKey: String,
    filter: PhotoFilter,
    modifier: Modifier = Modifier
) {
    val tintOverlay = when (filter) {
        PhotoFilter.NORMAL -> Color.Transparent
        PhotoFilter.CLARENDON -> Color(0x220EA5E9)
        PhotoFilter.MOON -> Color(0x88111111)
        PhotoFilter.LARK -> Color(0x28FFFBEB)
        PhotoFilter.WARM -> Color(0x33F59E0B)
    }

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            when (artKey) {
                LuminaSeedData.MEDIA_AURORA -> {
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF1A0B1E), Color(0xFF881337), Color(0xFFF43F5E), Color(0xFFFBBF24)),
                            start = Offset(0f, 0f),
                            end = Offset(w, h)
                        )
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xBBFDE68A), Color(0x44FB7185), Color.Transparent),
                            center = Offset(w * 0.72f, h * 0.28f),
                            radius = w * 0.65f
                        ),
                        radius = w * 0.65f,
                        center = Offset(w * 0.72f, h * 0.28f)
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xAAEC4899), Color(0x336366F1), Color.Transparent),
                            center = Offset(w * 0.25f, h * 0.75f),
                            radius = w * 0.55f
                        ),
                        radius = w * 0.55f,
                        center = Offset(w * 0.25f, h * 0.75f)
                    )
                    // Frosted geometric glass prism
                    drawRoundRect(
                        color = Color.White.copy(alpha = 0.14f),
                        topLeft = Offset(w * 0.18f, h * 0.22f),
                        size = Size(w * 0.64f, h * 0.56f),
                        cornerRadius = CornerRadius(48f, 48f)
                    )
                }
                LuminaSeedData.MEDIA_DUSK -> {
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF0F172A), Color(0xFF4C1D95), Color(0xFFBE185D), Color(0xFFFB923C))
                        )
                    )
                    // Glowing setting sun disc
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFFFFFBEB), Color(0xFFFDBA74), Color(0x00FB7185)),
                            center = Offset(w * 0.5f, h * 0.58f),
                            radius = w * 0.34f
                        ),
                        radius = w * 0.34f,
                        center = Offset(w * 0.5f, h * 0.58f)
                    )
                    // Coastal cliff silhouette
                    val cliff = Path().apply {
                        moveTo(0f, h * 0.72f)
                        quadraticTo(w * 0.28f, h * 0.60f, w * 0.55f, h * 0.78f)
                        quadraticTo(w * 0.80f, h * 0.66f, w, h * 0.74f)
                        lineTo(w, h)
                        lineTo(0f, h)
                        close()
                    }
                    drawPath(cliff, color = Color(0xFF090D16))
                }
                LuminaSeedData.MEDIA_ARCHITECT -> {
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFFF5E6D3), Color(0xFFE7C4A2), Color(0xFFC26D4A))
                        )
                    )
                    // Sculptural sunlit archways
                    drawRoundRect(
                        color = Color(0xFF9A3412).copy(alpha = 0.85f),
                        topLeft = Offset(w * 0.16f, h * 0.18f),
                        size = Size(w * 0.68f, h * 0.82f),
                        cornerRadius = CornerRadius(w * 0.34f, w * 0.34f)
                    )
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFFFED7AA), Color(0xFFFB923C), Color(0xFFEA580C))
                        ),
                        topLeft = Offset(w * 0.28f, h * 0.30f),
                        size = Size(w * 0.44f, h * 0.70f),
                        cornerRadius = CornerRadius(w * 0.22f, w * 0.22f)
                    )
                }
                else -> {
                    // Botanical warm linen editorial composition
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFFFAF5EE), Color(0xFFE9E2D0), Color(0xFFD6CEB8))
                        )
                    )
                    drawCircle(
                        color = Color(0xFF3F6212).copy(alpha = 0.18f),
                        radius = w * 0.38f,
                        center = Offset(w * 0.76f, h * 0.24f)
                    )
                    drawCircle(
                        color = Color(0xFF14532D).copy(alpha = 0.75f),
                        radius = w * 0.22f,
                        center = Offset(w * 0.34f, h * 0.48f)
                    )
                    drawCircle(
                        color = Color(0xFF365314).copy(alpha = 0.62f),
                        radius = w * 0.18f,
                        center = Offset(w * 0.54f, h * 0.62f)
                    )
                    drawRoundRect(
                        color = Color(0xFFA16207).copy(alpha = 0.3f),
                        topLeft = Offset(w * 0.24f, h * 0.76f),
                        size = Size(w * 0.52f, h * 0.12f),
                        cornerRadius = CornerRadius(24f, 24f)
                    )
                }
            }
        }
        if (tintOverlay != Color.Transparent) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(tintOverlay)
            )
        }
    }
}

/**
 * Creator circular avatar supporting Aria's bundled photo, gallery URIs, and stylish editorial monogram badges.
 */
@Composable
fun CreatorAvatar(
    avatarUri: String,
    name: String,
    size: Dp,
    modifier: Modifier = Modifier,
    showGradientRing: Boolean = false,
    isRingMuted: Boolean = false
) {
    val extras = LocalLuminaExtras.current
    val ringWidth = if (showGradientRing) 2.5.dp else 0.dp
    val innerPadding = if (showGradientRing) 2.5.dp else 0.dp

    val ringModifier = if (showGradientRing) {
        val ringBrush = if (isRingMuted) {
            Brush.linearGradient(listOf(extras.cardBorder, extras.mutedInk.copy(alpha = 0.5f)))
        } else {
            extras.storyRingGradient
        }
        modifier
            .size(size)
            .clip(CircleShape)
            .background(ringBrush)
            .padding(ringWidth)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .padding(innerPadding)
    } else {
        modifier.size(size)
    }

    Box(
        modifier = ringModifier
            .clip(CircleShape)
            .border(0.5.dp, extras.cardBorder, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        when {
            avatarUri == LuminaSeedData.AVATAR_ARIA -> {
                Image(
                    painter = painterResource(id = R.drawable.img_aria_avatar_1795266182532),
                    contentDescription = "$name avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            avatarUri.startsWith("avatar:") -> {
                val gradient = when (avatarUri) {
                    LuminaSeedData.AVATAR_SORA -> listOf(Color(0xFF0F172A), Color(0xFFF43F5E))
                    LuminaSeedData.AVATAR_ELENA -> listOf(Color(0xFFD97706), Color(0xFFEC4899))
                    LuminaSeedData.AVATAR_KAI -> listOf(Color(0xFF4F46E5), Color(0xFF06B6D4))
                    LuminaSeedData.AVATAR_CHLOE -> listOf(Color(0xFF059669), Color(0xFFF59E0B))
                    else -> listOf(Color(0xFF7C3AED), Color(0xFFF43F5E))
                }
                val initials = name.split(" ")
                    .mapNotNull { it.firstOrNull()?.uppercaseChar() }
                    .take(2)
                    .joinToString("")
                    .ifEmpty { "L" }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.linearGradient(gradient)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = (size.value * 0.34f).sp
                    )
                }
            }
            else -> {
                AsyncImage(
                    model = Uri.parse(avatarUri),
                    contentDescription = "$name avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

/**
 * Signature double-tap Heart Burst animation matching Lumina's CSS heartBurst keyframes.
 */
@Composable
fun HeartBurstOverlay(
    triggerCount: Int,
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }
    val rotation = remember { Animatable(-15f) }

    LaunchedEffect(triggerCount) {
        if (triggerCount > 0) {
            scale.snapTo(0f)
            alpha.snapTo(0f)
            rotation.snapTo(-15f)
            alpha.animateTo(1f, animationSpec = tween(140))
            scale.animateTo(1.28f, animationSpec = tween(220, easing = FastOutSlowInEasing))
            rotation.animateTo(4f, animationSpec = tween(140))
            scale.animateTo(1.05f, animationSpec = tween(110))
            scale.animateTo(1.45f, animationSpec = tween(220))
            alpha.animateTo(0f, animationSpec = tween(180))
        }
    }

    if (alpha.value > 0.01f) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = null,
                tint = BrandRose500,
                modifier = Modifier
                    .size(92.dp)
                    .graphicsLayer {
                        scaleX = scale.value
                        scaleY = scale.value
                        rotationZ = rotation.value
                        this.alpha = alpha.value
                    }
            )
        }
    }
}

/**
 * Signature 3-dot bouncing typing indicator for Direct Messages.
 */
@Composable
fun TypingDotsIndicator(
    modifier: Modifier = Modifier,
    dotColor: Color = BrandRose500
) {
    val transition = rememberInfiniteTransition(label = "typingDots")
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { index ->
            val scale by transition.animateFloat(
                initialValue = 0.35f,
                targetValue = 1.0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 650,
                        delayMillis = index * 160,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "dot_$index"
            )
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        alpha = 0.45f + (scale * 0.55f)
                    }
                    .clip(CircleShape)
                    .background(dotColor)
            )
        }
    }
}

/**
 * Signature 4-bar Audio Visualizer Equalizer for Reels.
 */
@Composable
fun AudioEqualizerBars(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    barColor: Color = Color.White
) {
    val transition = rememberInfiniteTransition(label = "eqBars")
    val heights = listOf(
        transition.animateFloat(4f, 18f, infiniteRepeatable(tween(520, easing = LinearEasing), RepeatMode.Reverse), label = "eq0"),
        transition.animateFloat(6f, 16f, infiniteRepeatable(tween(430, delayMillis = 120, easing = LinearEasing), RepeatMode.Reverse), label = "eq1"),
        transition.animateFloat(4f, 19f, infiniteRepeatable(tween(610, delayMillis = 70, easing = LinearEasing), RepeatMode.Reverse), label = "eq2"),
        transition.animateFloat(5f, 14f, infiniteRepeatable(tween(480, delayMillis = 180, easing = LinearEasing), RepeatMode.Reverse), label = "eq3")
    )

    Row(
        modifier = modifier.height(20.dp),
        horizontalArrangement = Arrangement.spacedBy(2.5.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        heights.forEach { anim ->
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(if (isPlaying) anim.value.dp else 5.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        Brush.verticalGradient(listOf(BrandAmber400, BrandPink500, barColor))
                    )
            )
        }
    }
}

fun formatCompactCount(count: Int): String = when {
    count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000f)
    count >= 1_000 -> String.format("%.1fK", count / 1_000f)
    else -> count.toString()
}

fun formatRelativeTime(createdAtMillis: Long): String {
    val diffMinutes = ((System.currentTimeMillis() - createdAtMillis) / 60_000L).coerceAtLeast(0L)
    return when {
        diffMinutes < 1 -> "Just now"
        diffMinutes < 60 -> "${diffMinutes}m ago"
        diffMinutes < 24 * 60 -> "${diffMinutes / 60}h ago"
        else -> "${diffMinutes / (24 * 60)}d ago"
    }
}
