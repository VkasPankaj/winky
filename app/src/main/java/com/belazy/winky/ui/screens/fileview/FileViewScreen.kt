// FileViewScreen.kt - Fixed with tab state preservation
package com.belazy.winky.ui.screens.fileview

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.navigation.NavController
import com.belazy.winky.ui.screens.fileview.tabs.ImageGridScreen
import com.belazy.winky.ui.screens.fileview.tabs.VideoGridScreen

@Composable
fun FileViewScreen(navController: NavController) {
    val tabItems = listOf("Images", "Videos")

    // KEY FIX: Use rememberSaveable to preserve tab state across navigation
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var tabBarSize by remember { mutableStateOf(IntSize.Zero) }

    val density = LocalDensity.current

    // Calculate responsive bubble positioning
    val tabWidth = if (tabBarSize.width > 0) {
        with(density) { (tabBarSize.width / tabItems.size).toDp() }
    } else {
        100.dp // Default fallback
    }

    val bubbleOffsetX by animateFloatAsState(
        targetValue = selectedTab * tabWidth.value,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "bubble_offset"
    )

    // Bubble scale animation with more pronounced effect
    val bubbleScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = keyframes {
            durationMillis = 900
            1f at 0 using EaseInOutCubic
            1.2f at 150 using EaseOutBack
            0.95f at 300 using EaseInOut
            1.05f at 450 using EaseOutBack
            1f at 900 using EaseInOutCubic
        },
        label = "bubble_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
    ) {
        // Content area with crossfade animation
        Crossfade(
            targetState = selectedTab,
            animationSpec = tween(durationMillis = 400, easing = EaseInOutCubic),
            label = "content_crossfade"
        ) { tab ->
            when (tab) {
                0 -> ImageGridScreen(navController)
                1 -> VideoGridScreen(navController)
            }
        }

        // Compact floating bottom navigation
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .wrapContentWidth() // Responsive width
                .widthIn(min = 200.dp, max = 300.dp), // Reasonable bounds
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A1A1A).copy(alpha = 0.95f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .onGloballyPositioned { coordinates ->
                        tabBarSize = coordinates.size
                    }
            ) {
                // Traveling Bubble Background - positioned behind text
                Box(
                    modifier = Modifier
                        .offset(x = with(density) { bubbleOffsetX.dp })
                        .width(tabWidth)
                        .height(44.dp)
                        .scale(bubbleScale)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF4F46E5),
                                    Color(0xFF1E3A8A)
                                )
                            )
                        )
                )

                // Tab buttons row
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    tabItems.forEachIndexed { index, title ->
                        val isSelected = selectedTab == index

                        // Text scale animation
                        val textScale by animateFloatAsState(
                            targetValue = if (isSelected) 1.05f else 0.95f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessHigh
                            ),
                            label = "text_scale_$index"
                        )

                        // Text color animation with better contrast
                        val textColor by animateColorAsState(
                            targetValue = if (isSelected) Color.White else Color(0xFF9CA3AF),
                            animationSpec = tween(durationMillis = 300, easing = EaseInOutCubic),
                            label = "text_color_$index"
                        )

                        // Individual tab container
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(20.dp))
                                .clickable(
                                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                                    indication = null // Remove ripple effect for cleaner look
                                ) {
                                    selectedTab = index
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
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

        // Trigger bubble animation on tab change
        LaunchedEffect(selectedTab) {
            // Animation trigger
        }
    }
}