package com.example.data

import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Live image filters matching Lumina's editorial photo filter presets.
 */
enum class PhotoFilter(val displayName: String) {
    NORMAL("Normal"),
    CLARENDON("Clarendon"),
    MOON("Moon"),
    LARK("Lark"),
    WARM("Warm");

    fun toColorFilter(): ColorFilter? = when (this) {
        NORMAL -> null
        CLARENDON -> {
            // High contrast, bright highlights, vivid saturation
            val sat = ColorMatrix().apply { setToSaturation(1.28f) }
            val contrast = ColorMatrix(
                floatArrayOf(
                    1.18f, 0f, 0f, 0f, 6f,
                    0f, 1.18f, 0f, 0f, 8f,
                    0f, 0f, 1.24f, 0f, 12f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            contrast.timesAssign(sat)
            ColorFilter.colorMatrix(contrast)
        }
        MOON -> {
            // Crisp high-contrast monochrome
            val gray = ColorMatrix().apply { setToSaturation(0f) }
            val lift = ColorMatrix(
                floatArrayOf(
                    1.14f, 0f, 0f, 0f, 8f,
                    0f, 1.14f, 0f, 0f, 8f,
                    0f, 0f, 1.14f, 0f, 8f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            lift.timesAssign(gray)
            ColorFilter.colorMatrix(lift)
        }
        LARK -> {
            // Airy lifted brightness with gentle saturation
            val sat = ColorMatrix().apply { setToSaturation(1.12f) }
            val airy = ColorMatrix(
                floatArrayOf(
                    0.96f, 0f, 0f, 0f, 18f,
                    0f, 1.02f, 0f, 0f, 16f,
                    0f, 0f, 1.04f, 0f, 18f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            airy.timesAssign(sat)
            ColorFilter.colorMatrix(airy)
        }
        WARM -> {
            // Golden sepia warmth and sunlit glow
            val warm = ColorMatrix(
                floatArrayOf(
                    1.14f, 0.14f, 0.04f, 0f, 8f,
                    0.06f, 1.04f, 0.04f, 0f, 4f,
                    0.02f, 0.06f, 0.84f, 0f, -4f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            ColorFilter.colorMatrix(warm)
        }
    }

    companion object {
        fun fromName(name: String): PhotoFilter =
            entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: NORMAL
    }
}

enum class LuminaTab {
    HOME,
    EXPLORE,
    REELS,
    NOTIFICATIONS,
    PROFILE
}

enum class NotificationType {
    LIKE,
    COMMENT,
    FOLLOW,
    MENTION
}

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val displayName: String,
    val handle: String,
    val bio: String,
    val website: String,
    val avatarUri: String,
    val followersCount: Int,
    val followingCount: Int
)

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val authorName: String,
    val authorHandle: String,
    val authorAvatar: String,
    val isAuthorVerified: Boolean = false,
    val isFollowingAuthor: Boolean = false,
    val location: String,
    val caption: String,
    val category: String,
    val mediaUri: String,
    val filterName: String = PhotoFilter.NORMAL.name,
    val likesCount: Int,
    val isLiked: Boolean = false,
    val isBookmarked: Boolean = false,
    val aspectRatio: Float = 1.2f,
    val isUserPost: Boolean = false,
    val createdAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val postId: Long,
    val authorName: String,
    val authorHandle: String,
    val authorAvatar: String,
    val text: String,
    val createdAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "stories")
data class StoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val authorName: String,
    val authorHandle: String,
    val authorAvatar: String,
    val mediaUri: String,
    val caption: String,
    val filterName: String = PhotoFilter.NORMAL.name,
    val isOwnStory: Boolean = false,
    val isViewed: Boolean = false,
    val createdAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "dm_threads")
data class DmThreadEntity(
    @PrimaryKey val id: String,
    val participantName: String,
    val participantHandle: String,
    val participantAvatar: String,
    val isOnline: Boolean,
    val unreadCount: Int,
    val lastMessagePreview: String,
    val updatedAtMillis: Long
)

@Entity(tableName = "dm_messages")
data class DmMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val threadId: String,
    val senderIsMe: Boolean,
    val text: String,
    val createdAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    val actorName: String,
    val actorHandle: String,
    val actorAvatar: String,
    val message: String,
    val postThumbnailUri: String?,
    val timeLabel: String,
    val isToday: Boolean,
    val isFollowingActor: Boolean,
    val isUnread: Boolean
)

data class ReelItem(
    val id: String,
    val creatorName: String,
    val creatorHandle: String,
    val creatorAvatar: String,
    val isVerified: Boolean,
    val caption: String,
    val audioTitle: String,
    val mediaUri: String,
    val filterName: String,
    val likesCount: Int,
    val commentsCount: Int,
    val sharesCount: Int,
    val isLiked: Boolean = false,
    val isBookmarked: Boolean = false,
    val isFollowing: Boolean = false
)

data class SuggestedCreator(
    val name: String,
    val handle: String,
    val subtitle: String,
    val avatarUri: String,
    val isFollowing: Boolean = false
)
