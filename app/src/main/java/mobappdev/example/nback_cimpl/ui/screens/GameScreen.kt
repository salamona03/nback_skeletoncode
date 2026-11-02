package mobappdev.example.nback_cimpl.ui.screens

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import mobappdev.example.nback_cimpl.ui.viewmodels.GameStatus
import mobappdev.example.nback_cimpl.ui.viewmodels.GameType
import mobappdev.example.nback_cimpl.ui.viewmodels.GameViewModel
import mobappdev.example.nback_cimpl.util.SoundHapticHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    vm: GameViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val gameState by vm.gameState.collectAsState()
    val view = LocalView.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Scaffold(
        topBar = {
            if (isLandscape) {
                // Thinner compact top bar in landscape (44dp)
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${gameState.gameType.name} (${gameState.nBack}-Back)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = {
                                SoundHapticHelper.playTapFeedback(view)
                                vm.stopGame()
                                onNavigateBack()
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", modifier = Modifier.size(20.dp))
                        }
                    }
                }
            } else {
                TopAppBar(
                    title = {
                        Text(
                            text = "${gameState.gameType.name} (${gameState.nBack}-Back)",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            SoundHapticHelper.playTapFeedback(view)
                            vm.stopGame()
                            onNavigateBack()
                        }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }
    ) { innerPadding ->
        if (isLandscape) {
            // Landscape Layout: Dual Scrollable Columns with perfectly sized game square
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Column: HUD & Grid/Audio (Scrollable)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // HUD Summary Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("EVENT", style = MaterialTheme.typography.labelSmall)
                                val currentEv = if (gameState.currentEventIndex < 0) 0 else minOf(gameState.currentEventIndex + 1, gameState.totalEvents)
                                Text("$currentEv / ${gameState.totalEvents}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("SCORE", style = MaterialTheme.typography.labelSmall)
                                Text("${gameState.score}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("HITS", style = MaterialTheme.typography.labelSmall)
                                Text("${gameState.hits}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                            }
                        }
                    }

                    // Visual Grid / Audio Display (Resized to fit screen in landscape)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (gameState.gameType == GameType.Audio || gameState.gameType == GameType.AudioVisual) {
                            Text(
                                text = if (gameState.currentAudioLetter.isNotBlank()) "Letter: ${gameState.currentAudioLetter}" else "Listening...",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                        if (gameState.gameType == GameType.Visual || gameState.gameType == GameType.AudioVisual) {
                            val gridSize = gameState.gridSize
                            val numCells = gridSize * gridSize
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(gridSize),
                                modifier = Modifier
                                    .size(150.dp)
                                    .aspectRatio(1f)
                                    .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(3.dp),
                                verticalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                items(numCells) { cellIndex ->
                                    val isHighlighted = (gameState.currentVisualValue == cellIndex)
                                    CenterFadeGridCell(
                                        isHighlighted = isHighlighted,
                                        cornerRadius = 4.dp
                                    )
                                }
                            }
                        }
                    }
                }

                // Right Column: Match Controls (Scrollable)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MatchButtonsSection(vm = vm, gameState = gameState, view = view)
                }
            }
        } else {
            // Portrait Layout: Standard Vertical Column
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // HUD Summary Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("EVENT", style = MaterialTheme.typography.labelSmall)
                            val currentEv = if (gameState.currentEventIndex < 0) 0 else minOf(gameState.currentEventIndex + 1, gameState.totalEvents)
                            Text("$currentEv / ${gameState.totalEvents}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("SCORE", style = MaterialTheme.typography.labelSmall)
                            Text("${gameState.score}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("HITS", style = MaterialTheme.typography.labelSmall)
                            Text("${gameState.hits}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                        }
                    }
                }

                // Visual Stimuli Grid / Audio Stimuli Display Area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (gameState.gameType == GameType.Audio || gameState.gameType == GameType.AudioVisual) {
                            Box(
                                modifier = Modifier
                                    .padding(bottom = 12.dp)
                                    .background(
                                        color = if (gameState.currentAudioLetter.isNotBlank()) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .padding(horizontal = 24.dp, vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (gameState.currentAudioLetter.isNotBlank()) "Letter: ${gameState.currentAudioLetter}" else "Listening...",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }

                        if (gameState.gameType == GameType.Visual || gameState.gameType == GameType.AudioVisual) {
                            val gridSize = gameState.gridSize
                            val numCells = gridSize * gridSize

                            LazyVerticalGrid(
                                columns = GridCells.Fixed(gridSize),
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .aspectRatio(1f)
                                    .border(
                                        width = 2.dp,
                                        color = MaterialTheme.colorScheme.outline,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(numCells) { cellIndex ->
                                    val isHighlighted = (gameState.currentVisualValue == cellIndex)
                                    CenterFadeGridCell(
                                        isHighlighted = isHighlighted,
                                        cornerRadius = 8.dp
                                    )
                                }
                            }
                        }
                    }
                }

                // Controls & Match Buttons
                MatchButtonsSection(vm = vm, gameState = gameState, view = view)
            }
        }
    }

    // Game Finished Dialog
    if (gameState.status == GameStatus.FINISHED) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("ROUND COMPLETED!", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Final Score: ${gameState.score}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("Hits (Correct): ${gameState.hits}")
                    Text("False Alarms (Errors): ${gameState.falseAlarms}")
                    Text("Mode: ${gameState.gameType.name} (${gameState.nBack}-Back)")
                }
            },
            confirmButton = {
                Button(onClick = {
                    SoundHapticHelper.playTapFeedback(view)
                    vm.startGame()
                }) {
                    Text("PLAY AGAIN")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    SoundHapticHelper.playTapFeedback(view)
                    vm.stopGame()
                    onNavigateBack()
                }) {
                    Text("RETURN HOME")
                }
            }
        )
    }
}

@Composable
fun CenterFadeGridCell(
    isHighlighted: Boolean,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 8.dp
) {
    val progress by animateFloatAsState(
        targetValue = if (isHighlighted) 1.0f else 0.0f,
        animationSpec = if (isHighlighted) {
            tween(durationMillis = 180, easing = FastOutSlowInEasing)
        } else {
            tween(durationMillis = 250, easing = FastOutSlowInEasing)
        },
        label = "fadeProgress"
    )

    val primaryColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(cornerRadius)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (progress > 0.01f) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(cornerRadius))
            ) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val maxRadius = kotlin.math.sqrt((size.width * size.width + size.height * size.height).toDouble()).toFloat() / 2f
                val currentRadius = maxRadius * progress.coerceIn(0.2f, 1.0f)

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = progress),
                            primaryColor.copy(alpha = progress * 0.9f),
                            primaryColor.copy(alpha = progress * 0.6f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = currentRadius
                    ),
                    center = center,
                    radius = currentRadius
                )

                if (progress > 0.85f) {
                    drawRect(
                        color = primaryColor.copy(alpha = (progress - 0.85f) / 0.15f)
                    )
                }
            }
        }
    }
}

@Composable
fun MatchButtonsSection(
    vm: GameViewModel,
    gameState: mobappdev.example.nback_cimpl.ui.viewmodels.GameState,
    view: android.view.View
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (gameState.gameType == GameType.Visual || gameState.gameType == GameType.AudioVisual) {
            val isVisualError = gameState.visualFeedbackError
            val buttonColor by animateColorAsState(
                targetValue = if (isVisualError) MaterialTheme.colorScheme.error else if (gameState.visualMatchPressed) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                label = "btnColor"
            )
            val buttonScale by animateFloatAsState(
                targetValue = if (isVisualError) 0.92f else 1.0f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy),
                label = "btnScale"
            )

            Button(
                onClick = {
                    SoundHapticHelper.playTapFeedback(view)
                    vm.checkVisualMatch()
                },
                enabled = gameState.status == GameStatus.RUNNING,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .scale(buttonScale),
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(imageVector = Icons.Default.Visibility, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (gameState.visualMatchPressed) "VISUAL MATCH RECORDED" else "MATCH VISUAL",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (gameState.gameType == GameType.Audio || gameState.gameType == GameType.AudioVisual) {
            val isAudioError = gameState.audioFeedbackError
            val buttonColor by animateColorAsState(
                targetValue = if (isAudioError) MaterialTheme.colorScheme.error else if (gameState.audioMatchPressed) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                label = "btnColor"
            )
            val buttonScale by animateFloatAsState(
                targetValue = if (isAudioError) 0.92f else 1.0f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy),
                label = "btnScale"
            )

            Button(
                onClick = {
                    SoundHapticHelper.playTapFeedback(view)
                    vm.checkAudioMatch()
                },
                enabled = gameState.status == GameStatus.RUNNING,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .scale(buttonScale),
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (gameState.audioMatchPressed) "AUDIO MATCH RECORDED" else "MATCH AUDIO",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
