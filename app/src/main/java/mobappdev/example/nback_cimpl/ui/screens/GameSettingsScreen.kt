package mobappdev.example.nback_cimpl.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mobappdev.example.nback_cimpl.ui.viewmodels.GameType
import mobappdev.example.nback_cimpl.ui.viewmodels.GameViewModel
import mobappdev.example.nback_cimpl.util.SoundHapticHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameSettingsScreen(
    vm: GameViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val gameState by vm.gameState.collectAsState()
    val view = LocalView.current

    var playerName by remember(gameState.playerName) { mutableStateOf(gameState.playerName) }
    var nBackVal by remember(gameState.nBack) { mutableStateOf(gameState.nBack) }
    var eventDurationSec by remember(gameState.eventDurationMs) { mutableStateOf(gameState.eventDurationMs / 1000f) }
    var eventsPerRoundVal by remember(gameState.eventsPerRound) { mutableStateOf(gameState.eventsPerRound) }
    var gridSizeVal by remember(gameState.gridSize) { mutableStateOf(gameState.gridSize) }
    var gameTypeVal by remember(gameState.gameType) { mutableStateOf(gameState.gameType) }

    fun saveChanges() {
        vm.updateSettings(
            nBack = nBackVal,
            eventDurationMs = (eventDurationSec * 1000).toLong(),
            eventsPerRound = eventsPerRoundVal,
            gridSize = gridSizeVal,
            gameType = gameTypeVal,
            playerName = playerName.ifBlank { "Player" },
            darkTheme = gameState.darkTheme,
            displayScale = gameState.displayScale
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Game Rules & Configuration", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {
                        SoundHapticHelper.playTapFeedback(view)
                        saveChanges()
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
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Player Name Field
            OutlinedTextField(
                value = playerName,
                onValueChange = {
                    playerName = it
                    saveChanges()
                },
                label = { Text("Player Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // N-Back Level Slider
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("N-Back Level: $nBackVal", fontWeight = FontWeight.Bold)
                    Slider(
                        value = nBackVal.toFloat(),
                        onValueChange = {
                            nBackVal = it.toInt()
                            saveChanges()
                        },
                        valueRange = 1f..5f,
                        steps = 3
                    )
                }
            }

            // Event Duration Slider (0.5s to 4.0s)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Event Duration: ${"%.1f".format(eventDurationSec)}s", fontWeight = FontWeight.Bold)
                    Slider(
                        value = eventDurationSec,
                        onValueChange = {
                            eventDurationSec = it
                            saveChanges()
                        },
                        valueRange = 0.5f..4.0f
                    )
                }
            }

            // Events Per Round Slider
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Events Per Round: $eventsPerRoundVal", fontWeight = FontWeight.Bold)
                    Slider(
                        value = eventsPerRoundVal.toFloat(),
                        onValueChange = {
                            eventsPerRoundVal = it.toInt()
                            saveChanges()
                        },
                        valueRange = 10f..30f,
                        steps = 3
                    )
                }
            }

            // Grid Size Selection
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Grid Size", fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(3, 4, 5).forEach { size ->
                            FilterChip(
                                selected = (gridSizeVal == size),
                                onClick = {
                                    SoundHapticHelper.playTapFeedback(view)
                                    gridSizeVal = size
                                    saveChanges()
                                },
                                label = { Text("${size}x${size}") }
                            )
                        }
                    }
                }
            }

            // Default Game Type Selection
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Default Stimulus Mode", fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        GameType.entries.forEach { type ->
                            FilterChip(
                                selected = (gameTypeVal == type),
                                onClick = {
                                    SoundHapticHelper.playTapFeedback(view)
                                    gameTypeVal = type
                                    saveChanges()
                                },
                                label = { Text(type.name) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Done Button
            Button(
                onClick = {
                    SoundHapticHelper.playTapFeedback(view)
                    saveChanges()
                    onNavigateBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.padding(6.dp))
                Text("DONE", fontWeight = FontWeight.Bold)
            }
        }
    }
}
