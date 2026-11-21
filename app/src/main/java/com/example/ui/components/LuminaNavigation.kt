package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.LuminaTab
import com.example.data.UserProfileEntity
import com.example.ui.theme.BrandAmber500
import com.example.ui.theme.BrandIndigo400
import com.example.ui.theme.BrandRose500
import com.example.ui.theme.LocalLuminaExtras
import com.example.ui.theme.PlusJakartaSansFontFamily

@Composable
fun LuminaBrandLogo(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val extras = LocalLuminaExtras.current
    val badgeSize = if (compact) 34.dp else 40.dp
    val innerCorner = if (compact) 10.dp else 13.dp
    val outerCorner = if (compact) 12.dp else 15.dp

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(badgeSize)
                .clip(RoundedCornerShape(outerCorner))
                .background(extras.brandGradient)
                .padding(1.8.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(innerCorner))
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.AutoAwesome,
                    contentDescription = "Lumina logo",
                    tint = BrandRose500,
                    modifier = Modifier.size(if (compact) 18.dp else 20.dp)
                )
            }
        }
        Text(
            text = stringResource(R.string.app_name),
            style = TextStyle(
                fontFamily = PlusJakartaSansFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = if (compact) 21.sp else 24.sp,
                letterSpacing = (-0.6).sp,
                brush = extras.wordmarkGradient
            )
        )
    }
}

/**
 * Sticky mobile top bar with Lumina wordmark, Refresh button, prominent Light/OLED Dark toggle,
 * Notifications button with indicator dot, and always-visible Direct Messages button with unread badge.
 */
@Composable
fun LuminaMobileTopBar(
    isDarkMode: Boolean,
    isRefreshing: Boolean,
    unreadDmCount: Int,
    hasUnreadNotifications: Boolean,
    onLogoClick: () -> Unit,
    onRefreshClick: () -> Unit,
    onToggleTheme: () -> Unit,
    onNotificationsClick: () -> Unit,
    onDmClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val extras = LocalLuminaExtras.current
    val spinTransition = rememberInfiniteTransition(label = "refreshSpin")
    val spinAngle by spinTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(750, easing = LinearEasing)),
        label = "angle"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(extras.glassSurface)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LuminaBrandLogo(
                onClick = onLogoClick,
                compact = true,
                modifier = Modifier.testTag("top_bar_logo")
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // Manual Refresh Feed Button
                IconButton(
                    onClick = onRefreshClick,
                    modifier = Modifier.testTag("top_bar_refresh_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = stringResource(R.string.refresh_feed),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .size(22.dp)
                            .rotate(if (isRefreshing) spinAngle else 0f)
                    )
                }

                // Prominent Light / OLED Dark Mode Toggle
                IconButton(
                    onClick = onToggleTheme,
                    modifier = Modifier.testTag("top_bar_theme_toggle")
                ) {
                    Icon(
                        imageVector = if (isDarkMode) Icons.Filled.DarkMode else Icons.Filled.LightMode,
                        contentDescription = stringResource(R.string.toggle_theme),
                        tint = if (isDarkMode) BrandIndigo400 else BrandAmber500,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Notifications Button with rose indicator dot
                IconButton(
                    onClick = onNotificationsClick,
                    modifier = Modifier.testTag("top_bar_notifications_button")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.FavoriteBorder,
                            contentDescription = stringResource(R.string.nav_notifications),
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(23.dp)
                        )
                        if (hasUnreadNotifications) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 2.dp, y = (-2).dp)
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(BrandRose500)
                                    .border(1.5.dp, MaterialTheme.colorScheme.background, CircleShape)
                            )
                        }
                    }
                }

                // Direct Messages Button — always visible with live unread count badge
                IconButton(
                    onClick = onDmClick,
                    modifier = Modifier.testTag("top_bar_dm_button")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = stringResource(R.string.direct_messages),
                            tint = BrandRose500,
                            modifier = Modifier.size(22.dp)
                        )
                        if (unreadDmCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 6.dp, y = (-5).dp)
                                    .size(17.dp)
                                    .clip(CircleShape)
                                    .background(BrandRose500)
                                    .border(1.5.dp, MaterialTheme.colorScheme.background, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (unreadDmCount > 9) "9+" else unreadDmCount.toString(),
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
        HorizontalDivider(thickness = 0.8.dp, color = extras.cardBorder)
    }
}

/**
 * Compact mobile bottom navigation bar respecting WindowInsets.navigationBars.
 */
@Composable
fun LuminaBottomNavBar(
    activeTab: LuminaTab,
    userAvatarUri: String,
    userName: String,
    onSelectTab: (LuminaTab) -> Unit,
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val extras = LocalLuminaExtras.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(extras.glassSurface)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        HorizontalDivider(thickness = 0.8.dp, color = extras.cardBorder)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                label = stringResource(R.string.nav_home),
                selected = activeTab == LuminaTab.HOME,
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home,
                onClick = { onSelectTab(LuminaTab.HOME) },
                modifier = Modifier.testTag("bottom_nav_home")
            )
            BottomNavItem(
                label = stringResource(R.string.nav_explore),
                selected = activeTab == LuminaTab.EXPLORE,
                selectedIcon = Icons.Filled.Explore,
                unselectedIcon = Icons.Outlined.Explore,
                onClick = { onSelectTab(LuminaTab.EXPLORE) },
                modifier = Modifier.testTag("bottom_nav_explore")
            )
            // Elevated Create Post Gradient Pill
            Box(
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(extras.brandGradient)
                    .clickable(onClick = onCreateClick)
                    .padding(horizontal = 16.dp, vertical = 9.dp)
                    .testTag("bottom_nav_create"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.AddBox,
                    contentDescription = stringResource(R.string.nav_create),
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            BottomNavItem(
                label = stringResource(R.string.nav_reels),
                selected = activeTab == LuminaTab.REELS,
                selectedIcon = Icons.Filled.Movie,
                unselectedIcon = Icons.Outlined.Movie,
                onClick = { onSelectTab(LuminaTab.REELS) },
                modifier = Modifier.testTag("bottom_nav_reels")
            )
            // Profile Avatar Tab
            Column(
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onSelectTab(LuminaTab.PROFILE) }
                    .padding(horizontal = 10.dp, vertical = 4.dp)
                    .testTag("bottom_nav_profile"),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                CreatorAvatar(
                    avatarUri = userAvatarUri,
                    name = userName,
                    size = 26.dp,
                    showGradientRing = activeTab == LuminaTab.PROFILE
                )
                Text(
                    text = stringResource(R.string.nav_profile),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (activeTab == LuminaTab.PROFILE) FontWeight.Bold else FontWeight.Medium,
                    color = if (activeTab == LuminaTab.PROFILE) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    selected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contentColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    Column(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(
            imageVector = if (selected) selectedIcon else unselectedIcon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(23.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = contentColor
        )
    }
}

/**
 * Medium / Expanded Tablet Sidebar mirroring Lumina's desktop glassmorphic left rail.
 */
@Composable
fun LuminaSideNavigation(
    activeTab: LuminaTab,
    isDarkMode: Boolean,
    unreadDmCount: Int,
    hasUnreadNotifications: Boolean,
    profile: UserProfileEntity,
    onSelectTab: (LuminaTab) -> Unit,
    onOpenDm: () -> Unit,
    onOpenCreate: () -> Unit,
    onToggleTheme: () -> Unit,
    onEditProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val extras = LocalLuminaExtras.current

    Surface(
        modifier = modifier
            .width(248.dp)
            .fillMaxHeight(),
        color = extras.glassSurface
    ) {
        Row(modifier = Modifier.fillMaxHeight()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .statusBarsPadding()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 16.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    LuminaBrandLogo(
                        onClick = { onSelectTab(LuminaTab.HOME) },
                        modifier = Modifier.padding(bottom = 18.dp)
                    )

                    SideNavItem(
                        label = stringResource(R.string.nav_home),
                        icon = if (activeTab == LuminaTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                        selected = activeTab == LuminaTab.HOME,
                        onClick = { onSelectTab(LuminaTab.HOME) },
                        modifier = Modifier.testTag("side_nav_home")
                    )
                    SideNavItem(
                        label = stringResource(R.string.nav_explore),
                        icon = if (activeTab == LuminaTab.EXPLORE) Icons.Filled.Explore else Icons.Outlined.Explore,
                        selected = activeTab == LuminaTab.EXPLORE,
                        onClick = { onSelectTab(LuminaTab.EXPLORE) },
                        modifier = Modifier.testTag("side_nav_explore")
                    )
                    SideNavItem(
                        label = stringResource(R.string.nav_reels),
                        icon = if (activeTab == LuminaTab.REELS) Icons.Filled.Movie else Icons.Outlined.Movie,
                        selected = activeTab == LuminaTab.REELS,
                        onClick = { onSelectTab(LuminaTab.REELS) },
                        modifier = Modifier.testTag("side_nav_reels")
                    )
                    SideNavItem(
                        label = stringResource(R.string.nav_messages),
                        icon = Icons.AutoMirrored.Outlined.Send,
                        selected = false,
                        badgeCount = unreadDmCount,
                        onClick = onOpenDm,
                        modifier = Modifier.testTag("side_nav_messages")
                    )
                    SideNavItem(
                        label = stringResource(R.string.nav_notifications),
                        icon = if (activeTab == LuminaTab.NOTIFICATIONS) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        selected = activeTab == LuminaTab.NOTIFICATIONS,
                        showDot = hasUnreadNotifications,
                        onClick = { onSelectTab(LuminaTab.NOTIFICATIONS) },
                        modifier = Modifier.testTag("side_nav_notifications")
                    )
                    SideNavItem(
                        label = stringResource(R.string.nav_create),
                        icon = Icons.Outlined.AddBox,
                        selected = false,
                        onClick = onOpenCreate,
                        modifier = Modifier.testTag("side_nav_create")
                    )

                    // Profile row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (activeTab == LuminaTab.PROFILE) extras.subtleSurface else Color.Transparent
                            )
                            .clickable { onSelectTab(LuminaTab.PROFILE) }
                            .padding(horizontal = 14.dp, vertical = 11.dp)
                            .testTag("side_nav_profile"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        CreatorAvatar(
                            avatarUri = profile.avatarUri,
                            name = profile.displayName,
                            size = 24.dp,
                            showGradientRing = activeTab == LuminaTab.PROFILE
                        )
                        Text(
                            text = stringResource(R.string.nav_profile),
                            style = MaterialTheme.typography.titleMedium,
                            color = if (activeTab == LuminaTab.PROFILE) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Bottom Theme Switcher & Mini Profile Card
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    HorizontalDivider(color = extras.cardBorder)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(extras.subtleSurface)
                            .clickable(onClick = onToggleTheme)
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                            .testTag("side_nav_theme_toggle"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = if (isDarkMode) Icons.Filled.DarkMode else Icons.Filled.LightMode,
                                contentDescription = null,
                                tint = if (isDarkMode) BrandIndigo400 else BrandAmber500,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = if (isDarkMode) stringResource(R.string.oled_dark_mode) else stringResource(R.string.light_mode),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Box(
                            modifier = Modifier
                                .width(34.dp)
                                .height(18.dp)
                                .clip(CircleShape)
                                .background(if (isDarkMode) BrandRose500 else MaterialTheme.colorScheme.outlineVariant)
                                .padding(2.dp),
                            contentAlignment = if (isDarkMode) Alignment.CenterEnd else Alignment.CenterStart
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            CreatorAvatar(
                                avatarUri = profile.avatarUri,
                                name = profile.displayName,
                                size = 36.dp
                            )
                            Column {
                                Text(
                                    text = profile.displayName,
                                    style = MaterialTheme.typography.labelLarge,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = profile.handle,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = extras.mutedInk,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                        IconButton(onClick = onEditProfile) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = stringResource(R.string.edit_profile),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(0.8.dp)
                    .background(extras.cardBorder)
            )
        }
    }
}

@Composable
private fun SideNavItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badgeCount: Int = 0,
    showDot: Boolean = false
) {
    val extras = LocalLuminaExtras.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) extras.subtleSurface else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (badgeCount > 0) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(BrandRose500)
                    .padding(horizontal = 7.dp, vertical = 2.dp)
            ) {
                Text(
                    text = badgeCount.toString(),
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        } else if (showDot) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(BrandRose500)
            )
        }
    }
}
