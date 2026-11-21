package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserProfileEntity::class,
        PostEntity::class,
        CommentEntity::class,
        StoryEntity::class,
        DmThreadEntity::class,
        DmMessageEntity::class,
        NotificationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class LuminaDatabase : RoomDatabase() {
    abstract fun luminaDao(): LuminaDao

    companion object {
        @Volatile
        private var INSTANCE: LuminaDatabase? = null

        fun getInstance(context: Context): LuminaDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    LuminaDatabase::class.java,
                    "lumina_social.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}

object LuminaSeedData {
    // Bundled media keys resolved by LuminaMedia composable
    const val MEDIA_ARIA = "bundled:aria"
    const val MEDIA_TOKYO = "bundled:tokyo"
    const val MEDIA_CAFE = "bundled:cafe"
    const val MEDIA_COAST = "bundled:coast"
    const val MEDIA_AURORA = "art:aurora"
    const val MEDIA_DUSK = "art:dusk"
    const val MEDIA_ARCHITECT = "art:architect"
    const val MEDIA_BOTANICAL = "art:botanical"
    const val MEDIA_CERAMIC = "art:ceramic"

    const val AVATAR_ARIA = "bundled:aria"
    const val AVATAR_KAI = "avatar:kai"
    const val AVATAR_ELENA = "avatar:elena"
    const val AVATAR_SORA = "avatar:sora"
    const val AVATAR_MARCUS = "avatar:marcus"
    const val AVATAR_CHLOE = "avatar:chloe"

    val defaultProfile = UserProfileEntity(
        id = 1,
        displayName = "Aria Sterling",
        handle = "@ariasterling",
        bio = "Visual designer & editorial photographer capturing quiet mornings, neon rain, and coastal light.",
        website = "lumina.studio/aria",
        avatarUri = AVATAR_ARIA,
        followersCount = 14820,
        followingCount = 412
    )

    fun initialPosts(now: Long): List<PostEntity> = listOf(
        PostEntity(
            id = 1,
            authorName = "Sora Takahashi",
            authorHandle = "@sora.frames",
            authorAvatar = AVATAR_SORA,
            isAuthorVerified = true,
            isFollowingAuthor = true,
            location = "Shinjuku, Tokyo",
            caption = "Blue hour hits differently after a warm summer drizzle. Lantern glow bouncing off wet stone alleys never gets old. #tokyo #architecture #nightwalk",
            category = "Architecture",
            mediaUri = MEDIA_TOKYO,
            filterName = PhotoFilter.CLARENDON.name,
            likesCount = 2480,
            isLiked = true,
            isBookmarked = true,
            aspectRatio = 1.25f,
            isUserPost = false,
            createdAtMillis = now - 25 * 60_000L
        ),
        PostEntity(
            id = 2,
            authorName = "Aria Sterling",
            authorHandle = "@ariasterling",
            authorAvatar = AVATAR_ARIA,
            isAuthorVerified = true,
            isFollowingAuthor = false,
            location = "Copenhagen Studio",
            caption = "Slow Sunday ritual: speckled stoneware pour-over, flaky laminated pastry, and crisp morning shadows across travertine. #coffee #design #minimalism",
            category = "Coffee",
            mediaUri = MEDIA_CAFE,
            filterName = PhotoFilter.LARK.name,
            likesCount = 1842,
            isLiked = false,
            isBookmarked = false,
            aspectRatio = 1.25f,
            isUserPost = true,
            createdAtMillis = now - 2 * 3600_000L
        ),
        PostEntity(
            id = 3,
            authorName = "Elena Rossi",
            authorHandle = "@elenarossi.co",
            authorAvatar = AVATAR_ELENA,
            isAuthorVerified = true,
            isFollowingAuthor = true,
            location = "Positano, Amalfi Coast",
            caption = "Terracotta warmth, bougainvillea in full bloom, and that golden Mediterranean hush right before sunset. #travel #coastal #editorial",
            category = "Travel",
            mediaUri = MEDIA_COAST,
            filterName = PhotoFilter.WARM.name,
            likesCount = 5190,
            isLiked = false,
            isBookmarked = true,
            aspectRatio = 0.85f,
            isUserPost = false,
            createdAtMillis = now - 5 * 3600_000L
        ),
        PostEntity(
            id = 4,
            authorName = "Kai Mercer",
            authorHandle = "@kaimercer",
            authorAvatar = AVATAR_KAI,
            isAuthorVerified = false,
            isFollowingAuthor = false,
            location = "Reykjavik Gallery",
            caption = "Color study 09 — exploring rose-gold glass refraction and soft atmospheric gradients for the autumn identity system. #design #editorial #colorstudy",
            category = "Design",
            mediaUri = MEDIA_AURORA,
            filterName = PhotoFilter.NORMAL.name,
            likesCount = 934,
            isLiked = false,
            isBookmarked = false,
            aspectRatio = 1.0f,
            isUserPost = false,
            createdAtMillis = now - 9 * 3600_000L
        ),
        PostEntity(
            id = 5,
            authorName = "Aria Sterling",
            authorHandle = "@ariasterling",
            authorAvatar = AVATAR_ARIA,
            isAuthorVerified = true,
            isFollowingAuthor = false,
            location = "Kyoto Archive",
            caption = "Monochrome light study from last week's shoot. Stripping away color leaves pure geometry and quiet texture. #editorial #monochrome #architecture",
            category = "Editorial",
            mediaUri = MEDIA_TOKYO,
            filterName = PhotoFilter.MOON.name,
            likesCount = 1320,
            isLiked = false,
            isBookmarked = false,
            aspectRatio = 1.15f,
            isUserPost = true,
            createdAtMillis = now - 16 * 3600_000L
        ),
        PostEntity(
            id = 6,
            authorName = "Chloe Lindqvist",
            authorHandle = "@chloe.atelier",
            authorAvatar = AVATAR_CHLOE,
            isAuthorVerified = true,
            isFollowingAuthor = false,
            location = "Stockholm, Sweden",
            caption = "Sculptural terracotta curves and sunset ochre tones from our spatial lighting exhibition. #architecture #design #lighting",
            category = "Architecture",
            mediaUri = MEDIA_ARCHITECT,
            filterName = PhotoFilter.WARM.name,
            likesCount = 1675,
            isLiked = false,
            isBookmarked = false,
            aspectRatio = 0.9f,
            isUserPost = false,
            createdAtMillis = now - 22 * 3600_000L
        ),
        PostEntity(
            id = 7,
            authorName = "Marcus Vance",
            authorHandle = "@mvance.visuals",
            authorAvatar = AVATAR_MARCUS,
            isAuthorVerified = false,
            isFollowingAuthor = true,
            location = "Big Sur, California",
            caption = "Dusk horizon melting from magenta into deep obsidian over the Pacific cliffs. #travel #dusk #editorial",
            category = "Travel",
            mediaUri = MEDIA_DUSK,
            filterName = PhotoFilter.CLARENDON.name,
            likesCount = 2104,
            isLiked = true,
            isBookmarked = false,
            aspectRatio = 1.05f,
            isUserPost = false,
            createdAtMillis = now - 28 * 3600_000L
        ),
        PostEntity(
            id = 8,
            authorName = "Aria Sterling",
            authorHandle = "@ariasterling",
            authorAvatar = AVATAR_ARIA,
            isAuthorVerified = true,
            isFollowingAuthor = false,
            location = "Milan Design Week",
            caption = "Botanical silhouettes against warm linen plaster — collecting moodboard textures for our spring lookbook. #design #botanical #coffee",
            category = "Design",
            mediaUri = MEDIA_BOTANICAL,
            filterName = PhotoFilter.LARK.name,
            likesCount = 1495,
            isLiked = false,
            isBookmarked = false,
            aspectRatio = 0.92f,
            isUserPost = true,
            createdAtMillis = now - 36 * 3600_000L
        )
    )

    fun initialComments(now: Long): List<CommentEntity> = listOf(
        CommentEntity(
            postId = 1,
            authorName = "Elena Rossi",
            authorHandle = "@elenarossi.co",
            authorAvatar = AVATAR_ELENA,
            text = "The reflections on the cobblestones look like oil paint!",
            createdAtMillis = now - 18 * 60_000L
        ),
        CommentEntity(
            postId = 1,
            authorName = "Kai Mercer",
            authorHandle = "@kaimercer",
            authorAvatar = AVATAR_KAI,
            text = "Clarendon tones fit Shinjuku twilight so well.",
            createdAtMillis = now - 10 * 60_000L
        ),
        CommentEntity(
            postId = 2,
            authorName = "Chloe Lindqvist",
            authorHandle = "@chloe.atelier",
            authorAvatar = AVATAR_CHLOE,
            text = "Need that ceramic carafe for our studio kitchen immediately.",
            createdAtMillis = now - 90 * 60_000L
        ),
        CommentEntity(
            postId = 3,
            authorName = "Aria Sterling",
            authorHandle = "@ariasterling",
            authorAvatar = AVATAR_ARIA,
            text = "Pure golden hour magic. Saving this for my summer moodboard!",
            createdAtMillis = now - 4 * 3600_000L
        )
    )

    fun initialStories(now: Long): List<StoryEntity> = listOf(
        StoryEntity(
            authorName = "Sora",
            authorHandle = "@sora.frames",
            authorAvatar = AVATAR_SORA,
            mediaUri = MEDIA_TOKYO,
            caption = "Midnight ramen hunt in Omoide Yokocho",
            filterName = PhotoFilter.CLARENDON.name,
            isOwnStory = false,
            isViewed = false,
            createdAtMillis = now - 30 * 60_000L
        ),
        StoryEntity(
            authorName = "Elena",
            authorHandle = "@elenarossi.co",
            authorAvatar = AVATAR_ELENA,
            mediaUri = MEDIA_COAST,
            caption = "Aperitivo hour overlooking the Tyrrhenian Sea",
            filterName = PhotoFilter.WARM.name,
            isOwnStory = false,
            isViewed = false,
            createdAtMillis = now - 2 * 3600_000L
        ),
        StoryEntity(
            authorName = "Kai",
            authorHandle = "@kaimercer",
            authorAvatar = AVATAR_KAI,
            mediaUri = MEDIA_AURORA,
            caption = "New rose-amber shader tests for Lumina 2.0",
            filterName = PhotoFilter.NORMAL.name,
            isOwnStory = false,
            isViewed = false,
            createdAtMillis = now - 3 * 3600_000L
        ),
        StoryEntity(
            authorName = "Chloe",
            authorHandle = "@chloe.atelier",
            authorAvatar = AVATAR_CHLOE,
            mediaUri = MEDIA_CAFE,
            caption = "Morning espresso before gallery install",
            filterName = PhotoFilter.LARK.name,
            isOwnStory = false,
            isViewed = true,
            createdAtMillis = now - 5 * 3600_000L
        ),
        StoryEntity(
            authorName = "Marcus",
            authorHandle = "@mvance.visuals",
            authorAvatar = AVATAR_MARCUS,
            mediaUri = MEDIA_DUSK,
            caption = "Chasing twilight along Highway 1",
            filterName = PhotoFilter.NORMAL.name,
            isOwnStory = false,
            isViewed = false,
            createdAtMillis = now - 6 * 3600_000L
        )
    )

    fun initialDmThreads(now: Long): List<DmThreadEntity> = listOf(
        DmThreadEntity(
            id = "thread_sora",
            participantName = "Sora Takahashi",
            participantHandle = "@sora.frames",
            participantAvatar = AVATAR_SORA,
            isOnline = true,
            unreadCount = 2,
            lastMessagePreview = "Sending over the raw Tokyo twilight edits now!",
            updatedAtMillis = now - 5 * 60_000L
        ),
        DmThreadEntity(
            id = "thread_elena",
            participantName = "Elena Rossi",
            participantHandle = "@elenarossi.co",
            participantAvatar = AVATAR_ELENA,
            isOnline = true,
            unreadCount = 1,
            lastMessagePreview = "Are you still coming to the Milan editorial shoot?",
            updatedAtMillis = now - 42 * 60_000L
        ),
        DmThreadEntity(
            id = "thread_kai",
            participantName = "Kai Mercer",
            participantHandle = "@kaimercer",
            participantAvatar = AVATAR_KAI,
            isOnline = false,
            unreadCount = 0,
            lastMessagePreview = "Loved the travertine coffee composition!",
            updatedAtMillis = now - 4 * 3600_000L
        ),
        DmThreadEntity(
            id = "thread_chloe",
            participantName = "Chloe Lindqvist",
            participantHandle = "@chloe.atelier",
            participantAvatar = AVATAR_CHLOE,
            isOnline = true,
            unreadCount = 0,
            lastMessagePreview = "See you at the gallery opening on Friday",
            updatedAtMillis = now - 12 * 3600_000L
        )
    )

    fun initialDmMessages(now: Long): List<DmMessageEntity> = listOf(
        DmMessageEntity(
            threadId = "thread_sora",
            senderIsMe = false,
            text = "Hey Aria! Just wrapped our rainy night walk through Shinjuku.",
            createdAtMillis = now - 25 * 60_000L
        ),
        DmMessageEntity(
            threadId = "thread_sora",
            senderIsMe = true,
            text = "The lantern colors on the wet cobblestones look unreal!",
            createdAtMillis = now - 16 * 60_000L
        ),
        DmMessageEntity(
            threadId = "thread_sora",
            senderIsMe = false,
            text = "Sending over the raw Tokyo twilight edits now!",
            createdAtMillis = now - 5 * 60_000L
        ),
        DmMessageEntity(
            threadId = "thread_elena",
            senderIsMe = false,
            text = "Are you still coming to the Milan editorial shoot?",
            createdAtMillis = now - 42 * 60_000L
        ),
        DmMessageEntity(
            threadId = "thread_kai",
            senderIsMe = true,
            text = "Your new gradient study is pure cinema.",
            createdAtMillis = now - 5 * 3600_000L
        ),
        DmMessageEntity(
            threadId = "thread_kai",
            senderIsMe = false,
            text = "Loved the travertine coffee composition!",
            createdAtMillis = now - 4 * 3600_000L
        ),
        DmMessageEntity(
            threadId = "thread_chloe",
            senderIsMe = false,
            text = "See you at the gallery opening on Friday",
            createdAtMillis = now - 12 * 3600_000L
        )
    )

    fun initialNotifications(): List<NotificationEntity> = listOf(
        NotificationEntity(
            type = NotificationType.LIKE.name,
            actorName = "Sora Takahashi",
            actorHandle = "@sora.frames",
            actorAvatar = AVATAR_SORA,
            message = "liked your photo from Copenhagen Studio.",
            postThumbnailUri = MEDIA_CAFE,
            timeLabel = "4m",
            isToday = true,
            isFollowingActor = true,
            isUnread = true
        ),
        NotificationEntity(
            type = NotificationType.COMMENT.name,
            actorName = "Chloe Lindqvist",
            actorHandle = "@chloe.atelier",
            actorAvatar = AVATAR_CHLOE,
            message = "commented: \"Need that ceramic carafe for our studio!\"",
            postThumbnailUri = MEDIA_CAFE,
            timeLabel = "28m",
            isToday = true,
            isFollowingActor = false,
            isUnread = true
        ),
        NotificationEntity(
            type = NotificationType.FOLLOW.name,
            actorName = "Kai Mercer",
            actorHandle = "@kaimercer",
            actorAvatar = AVATAR_KAI,
            message = "started following you.",
            postThumbnailUri = null,
            timeLabel = "2h",
            isToday = true,
            isFollowingActor = false,
            isUnread = true
        ),
        NotificationEntity(
            type = NotificationType.MENTION.name,
            actorName = "Elena Rossi",
            actorHandle = "@elenarossi.co",
            actorAvatar = AVATAR_ELENA,
            message = "mentioned you in an Amalfi Coast story.",
            postThumbnailUri = MEDIA_COAST,
            timeLabel = "1d",
            isToday = false,
            isFollowingActor = true,
            isUnread = false
        ),
        NotificationEntity(
            type = NotificationType.LIKE.name,
            actorName = "Marcus Vance",
            actorHandle = "@mvance.visuals",
            actorAvatar = AVATAR_MARCUS,
            message = "liked your monochrome architecture study.",
            postThumbnailUri = MEDIA_TOKYO,
            timeLabel = "2d",
            isToday = false,
            isFollowingActor = true,
            isUnread = false
        )
    )

    val initialReels = listOf(
        ReelItem(
            id = "reel_1",
            creatorName = "Elena Rossi",
            creatorHandle = "@elenarossi.co",
            creatorAvatar = AVATAR_ELENA,
            isVerified = true,
            caption = "Golden hour breeze above Positano cliffs — turning sunlight into cinema. #amalfi #reels #travel",
            audioTitle = "Mediterranean Sunset (Lo-Fi Vinyl Mix) • Studio Sol",
            mediaUri = MEDIA_COAST,
            filterName = PhotoFilter.WARM.name,
            likesCount = 18420,
            commentsCount = 312,
            sharesCount = 940,
            isFollowing = true
        ),
        ReelItem(
            id = "reel_2",
            creatorName = "Sora Takahashi",
            creatorHandle = "@sora.frames",
            creatorAvatar = AVATAR_SORA,
            isVerified = true,
            caption = "Walking Shinjuku alleys at 23:45 in soft neon rain. Turn up the ambient sound. #tokyo #cinematography",
            audioTitle = "Shibuya Raindrops • Ambient Tape 04",
            mediaUri = MEDIA_TOKYO,
            filterName = PhotoFilter.CLARENDON.name,
            likesCount = 24610,
            commentsCount = 489,
            sharesCount = 1620,
            isFollowing = true
        ),
        ReelItem(
            id = "reel_3",
            creatorName = "Aria Sterling",
            creatorHandle = "@ariasterling",
            creatorAvatar = AVATAR_ARIA,
            isVerified = true,
            caption = "Morning light pouring over hand-thrown ceramics & single origin bloom. #coffee #slowliving #design",
            audioTitle = "Copenhagen Morning • Acoustic Rhodes",
            mediaUri = MEDIA_CAFE,
            filterName = PhotoFilter.LARK.name,
            likesCount = 12950,
            commentsCount = 214,
            sharesCount = 580,
            isFollowing = false
        ),
        ReelItem(
            id = "reel_4",
            creatorName = "Kai Mercer",
            creatorHandle = "@kaimercer",
            creatorAvatar = AVATAR_KAI,
            isVerified = false,
            caption = "Chromatic rose-amber prism refractions shot on 50mm macro lens. #visualart #motion #lumina",
            audioTitle = "Prism Drift • Analog Synth Waves",
            mediaUri = MEDIA_AURORA,
            filterName = PhotoFilter.NORMAL.name,
            likesCount = 9870,
            commentsCount = 148,
            sharesCount = 415,
            isFollowing = false
        )
    )

    val suggestedCreators = listOf(
        SuggestedCreator(
            name = "Kai Mercer",
            handle = "@kaimercer",
            subtitle = "Visual Artist • Reykjavik",
            avatarUri = AVATAR_KAI,
            isFollowing = false
        ),
        SuggestedCreator(
            name = "Chloe Lindqvist",
            handle = "@chloe.atelier",
            subtitle = "Spatial Design • Stockholm",
            avatarUri = AVATAR_CHLOE,
            isFollowing = false
        ),
        SuggestedCreator(
            name = "Sora Takahashi",
            handle = "@sora.frames",
            subtitle = "Street Cinema • Tokyo",
            avatarUri = AVATAR_SORA,
            isFollowing = true
        )
    )

    val exploreCategories = listOf(
        "For You",
        "Architecture",
        "Design",
        "Travel",
        "Coffee",
        "Editorial"
    )
}
