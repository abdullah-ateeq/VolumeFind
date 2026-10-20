package com.example.volumefind.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.volumefind.data.repository.AppSettings
import com.example.volumefind.ui.components.rememberHapticHelper
import com.example.volumefind.ui.home.VolumeFindViewModel
import java.math.RoundingMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: VolumeFindViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settingsFlow.collectAsState()
    val haptic = rememberHapticHelper(settings.hapticFeedback)

    var showClearConfirmDialog by remember { mutableStateOf(false) }
    var showResetSettingsDialog by remember { mutableStateOf(false) }

    var themeMenuExpanded by remember { mutableStateOf(false) }
    var decimalsMenuExpanded by remember { mutableStateOf(false) }
    var roundingMenuExpanded by remember { mutableStateOf(false) }

    val decimalOptions = listOf(0, 1, 2, 3, 4, 6, 8, 10, 12)
    val roundingOptions = listOf(
        RoundingMode.HALF_UP to "Half Up",
        RoundingMode.HALF_DOWN to "Half Down",
        RoundingMode.HALF_EVEN to "Half Even",
        RoundingMode.UP to "Up",
        RoundingMode.DOWN to "Down",
        RoundingMode.CEILING to "Ceiling",
        RoundingMode.FLOOR to "Floor"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Appearance
            SettingsSectionHeader(title = "APPEARANCE")
            Card(
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Theme selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { themeMenuExpanded = true }
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Theme", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                            Text(text = settings.themeMode, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        }
                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Select Theme")

                        DropdownMenu(
                            expanded = themeMenuExpanded,
                            onDismissRequest = { themeMenuExpanded = false }
                        ) {
                            listOf(AppSettings.THEME_SYSTEM, AppSettings.THEME_LIGHT, AppSettings.THEME_DARK).forEach { mode ->
                                DropdownMenuItem(
                                    text = { Text(mode) },
                                    onClick = {
                                        haptic.lightClick()
                                        viewModel.setTheme(mode)
                                        themeMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    // Dynamic Color
                    SettingsToggleRow(
                        title = "Dynamic Color",
                        subtitle = "Use colors derived from wallpaper (Android 12+)",
                        checked = settings.dynamicColor,
                        onCheckedChange = {
                            haptic.lightClick()
                            viewModel.setDynamicColor(it)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Behavior
            SettingsSectionHeader(title = "BEHAVIOR")
            Card(
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SettingsToggleRow(
                        title = "Haptic Feedback",
                        subtitle = "Vibrate subtly on actions and selections",
                        checked = settings.hapticFeedback,
                        onCheckedChange = {
                            viewModel.setHaptics(it)
                            if (it) haptic.lightClick()
                        }
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    SettingsToggleRow(
                        title = "Animations",
                        subtitle = "Smooth transitions for state changes",
                        checked = settings.animations,
                        onCheckedChange = {
                            haptic.lightClick()
                            viewModel.setAnimations(it)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Number Display
            SettingsSectionHeader(title = "NUMBER DISPLAY")
            Card(
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Decimal places
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { decimalsMenuExpanded = true }
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Decimal Places", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                            Text(text = "${settings.decimalPlaces} decimal places", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        }
                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Select Decimals")

                        DropdownMenu(
                            expanded = decimalsMenuExpanded,
                            onDismissRequest = { decimalsMenuExpanded = false }
                        ) {
                            decimalOptions.forEach { decimals ->
                                DropdownMenuItem(
                                    text = { Text("$decimals") },
                                    onClick = {
                                        haptic.lightClick()
                                        viewModel.setDecimalPlaces(decimals)
                                        decimalsMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    // Rounding mode
                    val currentRoundingName = roundingOptions.find { it.first == settings.roundingMode }?.second ?: "Half Up"
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { roundingMenuExpanded = true }
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Rounding Mode", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                            Text(text = currentRoundingName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        }
                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Select Rounding Mode")

                        DropdownMenu(
                            expanded = roundingMenuExpanded,
                            onDismissRequest = { roundingMenuExpanded = false }
                        ) {
                            roundingOptions.forEach { (mode, label) ->
                                DropdownMenuItem(
                                    text = { Text(label) },
                                    onClick = {
                                        haptic.lightClick()
                                        viewModel.setRoundingMode(mode)
                                        roundingMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Display
            SettingsSectionHeader(title = "DISPLAY")
            Card(
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SettingsToggleRow(
                        title = "Show Formula",
                        subtitle = "Display formula card for active shape",
                        checked = settings.showFormula,
                        onCheckedChange = {
                            haptic.lightClick()
                            viewModel.setShowFormula(it)
                        }
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    SettingsToggleRow(
                        title = "Show Calculation Steps",
                        subtitle = "Expandable step-by-step math derivation",
                        checked = settings.showCalculationSteps,
                        onCheckedChange = {
                            haptic.lightClick()
                            viewModel.setShowCalculationSteps(it)
                        }
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    SettingsToggleRow(
                        title = "Show Shape Diagram",
                        subtitle = "Geometric vector diagram with dimension labels",
                        checked = settings.showShapeDiagram,
                        onCheckedChange = {
                            haptic.lightClick()
                            viewModel.setShowShapeDiagram(it)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // History & Data
            SettingsSectionHeader(title = "HISTORY & DATA")
            Card(
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SettingsToggleRow(
                        title = "Confirm History Delete",
                        subtitle = "Prompt before deleting single history items",
                        checked = settings.confirmHistoryDelete,
                        onCheckedChange = {
                            haptic.lightClick()
                            viewModel.setConfirmHistoryDelete(it)
                        }
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    SettingsToggleRow(
                        title = "Preserve Last Calculation",
                        subtitle = "Keep inputs and selected shape across launches",
                        checked = settings.preserveLastCalculation,
                        onCheckedChange = {
                            haptic.lightClick()
                            viewModel.setPreserveLastCalculation(it)
                        }
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                haptic.lightClick()
                                showClearConfirmDialog = true
                            }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.DeleteSweep, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "Clear History", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.SemiBold)
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                haptic.lightClick()
                                showResetSettingsDialog = true
                            }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "Reset Settings", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }

        // Clear History Confirmation Dialog
        if (showClearConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showClearConfirmDialog = false },
                title = { Text("Clear History") },
                text = { Text("Clear all recent volume calculations?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            haptic.confirm()
                            viewModel.clearAllHistory()
                            showClearConfirmDialog = false
                        }
                    ) {
                        Text("Clear", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearConfirmDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Reset Settings Confirmation Dialog
        if (showResetSettingsDialog) {
            AlertDialog(
                onDismissRequest = { showResetSettingsDialog = false },
                title = { Text("Reset Settings") },
                text = { Text("Reset all settings to default values?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            haptic.confirm()
                            viewModel.resetSettings()
                            showResetSettingsDialog = false
                        }
                    ) {
                        Text("Reset", color = MaterialTheme.colorScheme.primary)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetSettingsDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun SettingsToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 8.dp)
            .semantics {
                contentDescription = "$title, $subtitle. Currently ${if (checked) "On" else "Off"}"
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
