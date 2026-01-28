package com.vesto.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vesto.data.local.entity.CategoryExpenseSummary
import kotlin.math.cos
import kotlin.math.sin

/**
 * Simple Pie Chart for expense categories
 * Shows category distribution with animated segments
 */
@Composable
fun SimplePieChart(
    data: List<CategoryExpenseSummary>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) {
        // Empty state
        Box(
            modifier = modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No expenses yet",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }
    
    val total = data.sumOf { it.totalAmount }
    var animatedProgress by remember { mutableFloatStateOf(0f) }
    
    LaunchedEffect(data) {
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        ) { value, _ ->
            animatedProgress = value
        }
    }
    
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.size(200.dp)) {
            val canvasSize = size.minDimension
            val radius = canvasSize / 2f
            val center = Offset(size.width / 2f, size.height / 2f)
            
            var startAngle = -90f // Start from top
            
            data.forEach { category ->
                val sweepAngle = (category.totalAmount / total * 360f * animatedProgress).toFloat()
                val color = parseColor(category.categoryColor) ?: Color.Gray
                
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    topLeft = Offset(
                        (size.width - canvasSize) / 2f,
                        (size.height - canvasSize) / 2f
                    ),
                    size = Size(canvasSize, canvasSize)
                )
                
                startAngle += sweepAngle
            }
            
            // Draw white circle in center for donut effect
            drawCircle(
                color = Color.White,
                radius = radius * 0.5f,
                center = center
            )
        }
    }
}

/**
 * Parse hex color string to Color
 */
private fun parseColor(hexColor: String): Color? {
    return try {
        Color(android.graphics.Color.parseColor(hexColor))
    } catch (e: Exception) {
        null
    }
}

/**
 * Animated Linear Progress Indicator for budget
 */
@Composable
fun AnimatedBudgetProgress(
    progress: Float,
    isOverBudget: Boolean,
    modifier: Modifier = Modifier
) {
    var animatedProgress by remember { mutableFloatStateOf(0f) }
    
    LaunchedEffect(progress) {
        animate(
            initialValue = animatedProgress,
            targetValue = progress.coerceIn(0f, 1f),
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        ) { value, _ ->
            animatedProgress = value
        }
    }
    
    val progressColor = if (isOverBudget) {
        MaterialTheme.colorScheme.error
    } else when {
        animatedProgress < 0.5f -> Color(0xFF4CAF50) // Green
        animatedProgress < 0.8f -> Color(0xFFFFA726) // Orange
        else -> Color(0xFFF44336) // Red
    }
    
    Canvas(modifier = modifier.height(12.dp)) {
        // Background track
        drawRoundRect(
            color = Color(0xFFE0E0E0),
            size = Size(size.width, size.height),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(size.height / 2f)
        )
        
        // Progress indicator
        if (animatedProgress > 0f) {
            drawRoundRect(
                color = progressColor,
                size = Size(size.width * animatedProgress.coerceIn(0f, 1f), size.height),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(size.height / 2f)
            )
        }
    }
}
