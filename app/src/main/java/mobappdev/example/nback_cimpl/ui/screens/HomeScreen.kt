package mobappdev.example.nback_cimpl.ui.screens

import android.content.res.Configuration
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mobappdev.example.nback_cimpl.data.db.ScoreEntity
import mobappdev.example.nback_cimpl.ui.viewmodels.GameType
import mobappdev.example.nback_cimpl.ui.viewmodels.GameViewModel
import mobappdev.example.nback_cimpl.util.SoundHapticHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    vm: GameViewModel,
    onNavigateToGame: () -> Unit = {},
    onNavigateToGameSettings: () -> Unit = {},
    onNavigateToThemeSettings: () -> Unit = {},
    onNavigateToScores: () -> Unit = {},
    onNavigateToTutorial: () -> Unit = {}
) {
    val highscore by vm.highscore.collectAsState()
    val gameState by vm.gameState.collectAsState()
    val scoresList by vm.scoresList.collectAsState()
    val view = LocalView.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🧠 N-Back Brain Game", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = {
                        SoundHapticHelper.playTapFeedback(view)
                        onNavigateToThemeSettings()
                    }) {
                        Icon(imageVector = Icons.Default.Palette, contentDescription = "Theme")
                    }
                    IconButton(onClick = {
                        SoundHapticHelper.playTapFeedback(view)
                        onNavigateToGameSettings()
                    }) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
                    }
                    IconButton(onClick = {
                        SoundHapticHelper.playTapFeedback(view)
                        onNavigateToScores()
                    }) {
                        Icon(imageVector = Icons.Default.Leaderboard, contentDescription = "Scores")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (isLandscape) {
            // Landscape Layout: Dual Scrollable Columns
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Column: Badges & Progress Graph (Scrollable)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Smaller High Score & Compact Mode Badge Cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Card(
                            onClick = {
                                SoundHapticHelper.playTapFeedback(view)
                                onNavigateToScores()
                            },
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.size(6.dp))
                                Column {
                                    Text("High Score", style = MaterialTheme.typography.labelSmall)
                                    Text("$highscore", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Card(
                            onClick = {
                                SoundHapticHelper.playTapFeedback(view)
                                onNavigateToGameSettings()
                            },
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Mode", style = MaterialTheme.typography.labelSmall)
                                Text("${gameState.nBack}-Back ⚙️", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Compact Progress Graph Card
                    Card(
                        onClick = {
                            SoundHapticHelper.playTapFeedback(view)
                            onNavigateToScores()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.TrendingUp,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.size(4.dp))
                                    Text("Score Progress", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                }
                                Text("Tap details", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                            }
                            ProgressGraph(scores = scoresList)
                        }
                    }
                }

                // Right Column: Challenge Mode Buttons (Scrollable)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "✨ CHOOSE YOUR CHALLENGE ✨",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        PlayfulModeButton(
                            title = "VISUAL N-BACK",
                            subtitle = "Track grid positions",
                            icon = Icons.Default.Visibility,
                            containerColor = MaterialTheme.colorScheme.primary,
                            displayScale = gameState.displayScale,
                            onClick = {
                                vm.setGameType(GameType.Visual)
                                vm.startGame()
                                onNavigateToGame()
                            }
                        )

                        PlayfulModeButton(
                            title = "AUDITORY N-BACK",
                            subtitle = "Listen to spoken letters",
                            icon = Icons.Default.VolumeUp,
                            containerColor = MaterialTheme.colorScheme.secondary,
                            displayScale = gameState.displayScale,
                            onClick = {
                                vm.setGameType(GameType.Audio)
                                vm.startGame()
                                onNavigateToGame()
                            }
                        )

                        PlayfulModeButton(
                            title = "DUAL N-BACK",
                            subtitle = "Visual & Auditory combined",
                            icon = Icons.Default.Psychology,
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            displayScale = gameState.displayScale,
                            onClick = {
                                vm.setGameType(GameType.AudioVisual)
                                vm.startGame()
                                onNavigateToGame()
                            }
                        )
                    }
                }
            }
        } else {
            // Portrait Layout: Single Column
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                // Smaller High Score & Compact Mode Badge Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        onClick = {
                            SoundHapticHelper.playTapFeedback(view)
                            onNavigateToScores()
                        },
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Column {
                                Text("High Score", style = MaterialTheme.typography.labelSmall)
                                Text("$highscore", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Card(
                        onClick = {
                            SoundHapticHelper.playTapFeedback(view)
                            onNavigateToGameSettings()
                        },
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Mode", style = MaterialTheme.typography.labelSmall)
                            Text("${gameState.nBack}-Back ⚙️", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Compact Progress Graph Card
                Card(
                    onClick = {
                        SoundHapticHelper.playTapFeedback(view)
                        onNavigateToScores()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.TrendingUp,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.size(6.dp))
                                Text("Score Progress Over Time", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            }
                            Text("Tap for details", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        }
                        ProgressGraph(scores = scoresList)
                    }
                }

                Text(
                    text = "✨ CHOOSE YOUR CHALLENGE ✨",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                // Playful Game Mode Buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PlayfulModeButton(
                        title = "VISUAL N-BACK",
                        subtitle = "Track grid positions",
                        icon = Icons.Default.Visibility,
                        containerColor = MaterialTheme.colorScheme.primary,
                        displayScale = gameState.displayScale,
                        onClick = {
                            vm.setGameType(GameType.Visual)
                            vm.startGame()
                            onNavigateToGame()
                        }
                    )

                    PlayfulModeButton(
                        title = "AUDITORY N-BACK",
                        subtitle = "Listen to spoken letters",
                        icon = Icons.Default.VolumeUp,
                        containerColor = MaterialTheme.colorScheme.secondary,
                        displayScale = gameState.displayScale,
                        onClick = {
                            vm.setGameType(GameType.Audio)
                            vm.startGame()
                            onNavigateToGame()
                        }
                    )

                    PlayfulModeButton(
                        title = "DUAL N-BACK",
                        subtitle = "Visual & Auditory combined",
                        icon = Icons.Default.Psychology,
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        displayScale = gameState.displayScale,
                        onClick = {
                            vm.setGameType(GameType.AudioVisual)
                            vm.startGame()
                            onNavigateToGame()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ProgressGraph(scores: List<ScoreEntity>, modifier: Modifier = Modifier) {
    if (scores.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(70.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Complete rounds to see your progress graph!",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    val sortedScores = scores.sortedBy { kotlin.math.abs(it.timestamp) }
    val maxScore = maxOf(50f, sortedScores.maxOf { it.score.toFloat() })
    val minScore = 0f
    val primaryColor = MaterialTheme.colorScheme.primary

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(75.dp)
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        val width = size.width
        val height = size.height
        val stepX = if (sortedScores.size > 1) width / (sortedScores.size - 1) else width

        val points = sortedScores.mapIndexed { index, scoreEntity ->
            val x = index * stepX
            val normalizedY = 1f - ((scoreEntity.score.toFloat() - minScore) / (maxScore - minScore))
            val y = normalizedY * (height - 10.dp.toPx()) + 5.dp.toPx()
            Offset(x, y)
        }

        if (points.size > 1) {
            val path = Path().apply {
                moveTo(points.first().x, points.first().y)
                for (i in 1 until points.size) {
                    lineTo(points[i].x, points[i].y)
                }
            }
            drawPath(
                path = path,
                color = primaryColor,
                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        points.forEach { point ->
            drawCircle(
                color = primaryColor,
                radius = 3.5.dp.toPx(),
                center = point
            )
        }
    }
}

@Composable
fun PlayfulModeButton(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    containerColor: androidx.compose.ui.graphics.Color,
    displayScale: String = "Normal",
    onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "scale"
    )
    val view = LocalView.current

    val buttonHeight = when (displayScale) {
        "XL" -> 72.dp
        "Large" -> 64.dp
        else -> 56.dp
    }

    Button(
        onClick = {
            pressed = true
            SoundHapticHelper.playTapFeedback(view)
            onClick()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(buttonHeight)
            .scale(scale),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.size(16.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
            }
        }
    }
}
