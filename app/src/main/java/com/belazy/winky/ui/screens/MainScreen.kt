package com.belazy.winky.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.belazy.winky.ui.screens.detail.GalleryViewModel
import com.belazy.winky.ui.screens.detail.VideoGridViewModel
import com.belazy.winky.ui.screens.fileview.FileViewScreen
import com.belazy.winky.ui.screens.folderview.FolderViewScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    videoViewModel: VideoGridViewModel = hiltViewModel(),
    imageViewModel: GalleryViewModel = hiltViewModel()
) {
    val tabItems = listOf("All Files", "Folder View")

    // 🔥 FIX: Use rememberSaveable so tab state persists across navigation
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var tabBarSize by remember { mutableStateOf(IntSize.Zero) }

    val density = LocalDensity.current

    LaunchedEffect(Unit) {
        videoViewModel.loadVideos()
        imageViewModel.loadImages()
    }

    val tabWidth = if (tabBarSize.width > 0) {
        with(density) { (tabBarSize.width / tabItems.size).toDp() }
    } else {
        150.dp
    }

    val bubbleOffsetX by animateFloatAsState(
        targetValue = selectedTab * tabWidth.value,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "bubble_offset"
    )

    val bubbleScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = keyframes {
            durationMillis = 900
            1f at 0 using EaseInOutCubic
            1.15f at 150 using EaseOutBack
            0.95f at 300 using EaseInOut
            1.03f at 450 using EaseOutBack
            1f at 900 using EaseInOutCubic
        },
        label = "bubble_scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A0A0A),
                        Color(0xFF1A1A1A)
                    )
                )
            )
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1A1A1A).copy(alpha = 0.95f),
                            Color(0xFF0A0A0A).copy(alpha = 0.9f)
                        )
                    )
                )
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Winky",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    ),
                    color = Color.White
                )
            }
        }

        // Tab Bar
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1E1E1E).copy(alpha = 0.95f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .padding(6.dp)
                    .onGloballyPositioned { coordinates ->
                        tabBarSize = coordinates.size
                    }
            ) {
                Box(
                    modifier = Modifier
                        .offset(x = with(density) { bubbleOffsetX.dp })
                        .width(tabWidth)
                        .height(48.dp)
                        .scale(bubbleScale)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF4F46E5),
                                    Color(0xFF1E3A8A)
                                )
                            )
                        )
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    tabItems.forEachIndexed { index, title ->
                        val isSelected = selectedTab == index

                        val textScale by animateFloatAsState(
                            targetValue = if (isSelected) 1.05f else 0.95f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessHigh
                            ),
                            label = "text_scale_$index"
                        )

                        val textColor by animateColorAsState(
                            targetValue = if (isSelected) Color.White else Color(0xFF9CA3AF),
                            animationSpec = tween(durationMillis = 300, easing = EaseInOutCubic),
                            label = "text_color_$index"
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    selectedTab = index
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                fontSize = 16.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = textColor,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .scale(textScale)
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(Color(0xFF0A0A0A))
        ) {
            Crossfade(
                targetState = selectedTab,
                animationSpec = tween(durationMillis = 400, easing = EaseInOutCubic),
                label = "content_crossfade"
            ) { tab ->
                when (tab) {
                    0 -> FileViewScreen(navController)
                    1 -> FolderViewScreen(navController)
                }
            }
        }
    }
}
