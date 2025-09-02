// FileViewScreen.kt - Floating tab bar wider with more roundness
package com.belazy.winky.ui.screens.fileview

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.belazy.winky.ui.screens.fileview.tabs.ImageGridScreen
import com.belazy.winky.ui.screens.fileview.tabs.VideoGridScreen

@Composable
fun FileViewScreen(navController: NavController) {
    val tabItems = listOf("Images", "Videos") // Text-only tabs
    var selectedTab by remember { mutableIntStateOf(0) }

    val density = LocalDensity.current

    // Bubble animation offset
    val bubbleOffsetX by animateFloatAsState(
        targetValue = if (selectedTab == 0) 0f else 56f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "bubble_offset"
    )

    // Bubble scale animation
    val bubbleScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = keyframes {
            durationMillis = 600
            1f at 0 with EaseInOutCubic
            1.3f at 150 with EaseOutBack
            0.9f at 300 with EaseInOut
            1.05f at 450 with EaseOutBack
            1f at 600 with EaseInOutCubic
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

        // Wider floating bottom navigation with more rounded corners
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(0.6f), // Make it wider
            shape = RoundedCornerShape(24.dp), // More roundness
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A1A1A).copy(alpha = 0.95f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Box(
                modifier = Modifier.padding(12.dp)
            ) {
                // Traveling Bubble Background
                Box(
                    modifier = Modifier
                        .offset(x = with(density) { bubbleOffsetX.dp })
                        .size(width = 48.dp, height = 48.dp)
                        .scale(bubbleScale)
                        .graphicsLayer {
                            shadowElevation = 8.dp.toPx()
                        }
                        .background(
                            color = Color(0xFF3B82F6),
                            shape = RoundedCornerShape(16.dp)
                        )
                )

                // Tab buttons row (text-only)
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    tabItems.forEachIndexed { index, title ->
                        val isSelected = selectedTab == index

                        // Text scale animation
                        val textScale by animateFloatAsState(
                            targetValue = if (isSelected) 1.1f else 1f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessHigh
                            ),
                            label = "text_scale_$index"
                        )

                        // Text color animation
                        val textColor by animateColorAsState(
                            targetValue = if (isSelected) Color.White else Color(0xFF9CA3AF),
                            animationSpec = tween(durationMillis = 300),
                            label = "text_color_$index"
                        )

                        Text(
                            text = title,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = textColor,
                            modifier = Modifier
                                .scale(textScale)
                                .clickable { selectedTab = index }
                        )
                    }
                }
            }
        }

        // Minimalistic dates section example (no background)
        /*
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val dates = listOf("1", "2", "3", "4", "5")
                dates.forEach { date ->
                    Text(
                        text = date,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
        */

        LaunchedEffect(selectedTab) {
            // Retriggers bubble animation on tab change
        }
    }
}
