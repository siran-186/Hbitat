package com.example.ui

import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.TasbeehData
import com.example.data.TasbeehDua

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitHomeScreen(
    viewModel: HabitViewModel,
    modifier: Modifier = Modifier
) {
    val activeHabits by viewModel.activeHabits.collectAsStateWithLifecycle()
    val completedHabits by viewModel.completedHabits.collectAsStateWithLifecycle()
    val tasbeeh by viewModel.currentTasbeeh.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val milestoneEvent by viewModel.milestoneCelebrationEvent.collectAsStateWithLifecycle()
    val finalCelebrationEvent by viewModel.finalCelebrationEvent.collectAsStateWithLifecycle()
    val daysSinceStart = viewModel.getDaysSinceStart()

    var showAddHabitDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) } // 0 = Active, 1 = Completed

    val currentList = if (selectedTab == 0) activeHabits else completedHabits
    val activeCompletionsToday = activeHabits.count { it.isCompletedToday }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        Column(modifier = Modifier.padding(start = 4.dp)) {
                            Text(
                                text = "habitat 🏠",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = (-0.5).sp,
                                    color = MaterialTheme.colorScheme.primary // Deep forest green
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = selectedDate.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp
                                )
                            )
                        }
                    },
                    actions = {
                        Box(
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Checklist,
                                contentDescription = "habitat Status",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            },
            floatingActionButton = {
                if (selectedTab == 0) {
                    FloatingActionButton(
                        onClick = { showAddHabitDialog = true },
                        containerColor = MaterialTheme.colorScheme.primary, // Forest green
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        shape = CircleShape,
                        modifier = Modifier
                            .testTag("add_habit_fab")
                            .padding(bottom = 8.dp)
                            .size(56.dp)
                            .border(4.dp, MaterialTheme.colorScheme.background, CircleShape),
                        elevation = FloatingActionButtonDefaults.elevation(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Habit",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            },
            floatingActionButtonPosition = FabPosition.Center
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. Daily Rotating Tasbeeh/Dua Card
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    TasbeehDuaCard(
                        tasbeeh = tasbeeh,
                        daysSinceStart = daysSinceStart,
                        onAdvanceDay = { viewModel.advanceSimulationDay() },
                        onResetOffset = { viewModel.resetSimulationDay() }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // 2. Custom Tabs Row
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        listOf("Active", "Completed").forEachIndexed { index, label ->
                            val isSelected = selectedTab == index
                            val count = if (index == 0) activeHabits.size else completedHabits.size
                            
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable { selectedTab = index }
                                    .testTag("tab_$label"),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                                    }
                                ),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = if (isSelected) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                    }
                                )
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) {
                                                MaterialTheme.colorScheme.onPrimary
                                            } else {
                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                            }
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(
                                                if (isSelected) {
                                                    MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
                                                } else {
                                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                                }
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = count.toString(),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) {
                                                MaterialTheme.colorScheme.onPrimary
                                            } else {
                                                MaterialTheme.colorScheme.primary
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 3. Section Header: Active checklist metadata
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (selectedTab == 0) "TODAY'S INTENTIONS" else "COMPLETED CHALLENGES",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                letterSpacing = 1.sp
                            )
                        )
                        if (selectedTab == 0 && activeHabits.isNotEmpty()) {
                            Text(
                                text = "$activeCompletionsToday of ${activeHabits.size} Complete Today",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }

                // 4. Habits Checklist List
                if (currentList.isEmpty()) {
                    item {
                        EmptyHabitsPlaceholder(selectedTab)
                    }
                } else {
                    items(currentList, key = { it.id }) { habit ->
                        AnimatedVisibility(
                            visible = true,
                            exit = shrinkVertically(animationSpec = spring()) + fadeOut()
                        ) {
                            if (selectedTab == 0) {
                                HabitPollItem(
                                    habit = habit,
                                    onToggle = { viewModel.toggleHabitCompletion(habit.id) },
                                    onDelete = { viewModel.deleteHabit(habit.id) }
                                )
                            } else {
                                CompletedHabitItem(
                                    habit = habit,
                                    onRestart = { viewModel.restartHabit(habit.id) },
                                    onArchive = { viewModel.archiveHabit(habit.id) }
                                )
                            }
                        }
                    }
                }

                // Padding at the bottom to prevent FAB covering list items
                item {
                    Spacer(modifier = Modifier.height(96.dp))
                }
            }
        }

        // Falling leaves overlay shown during final-day completion celebration!
        if (finalCelebrationEvent != null) {
            FallingLeavesOverlay(modifier = Modifier.fillMaxSize())
        }
    }

    if (showAddHabitDialog) {
        AddHabitDialog(
            onDismiss = { showAddHabitDialog = false },
            onConfirm = { name, duration ->
                viewModel.addHabit(name, duration)
                showAddHabitDialog = false
            }
        )
    }

    milestoneEvent?.let { event ->
        MilestoneDialog(
            habitName = event.habitName,
            dayNumber = event.dayNumber,
            message = event.message,
            onDismiss = { viewModel.clearMilestoneCelebration() }
        )
    }

    finalCelebrationEvent?.let { event ->
        FinalCompletionDialog(
            habitName = event.habitName,
            durationDays = event.durationDays,
            onRestart = {
                viewModel.restartHabit(event.habitId)
                viewModel.clearFinalCelebration(event.habitId)
            },
            onDone = {
                viewModel.archiveHabit(event.habitId)
                viewModel.clearFinalCelebration(event.habitId)
            }
        )
    }
}

@Composable
fun TasbeehDuaCard(
    tasbeeh: TasbeehDua,
    daysSinceStart: Int,
    onAdvanceDay: () -> Unit,
    onResetOffset: () -> Unit
) {
    val totalItems = if (TasbeehData.items.isNotEmpty()) TasbeehData.items.size else 17
    val dayIndex = (daysSinceStart % totalItems) + 1
    val cycleIndex = ((daysSinceStart / totalItems) % 100) + 1

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("tasbeeh_card")
            .clip(RoundedCornerShape(32.dp)),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    drawCircle(
                        color = Color.White.copy(alpha = 0.08f),
                        radius = 120.dp.toPx(),
                        center = androidx.compose.ui.geometry.Offset(-20.dp.toPx(), -20.dp.toPx())
                    )
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "DAY $dayIndex • SUPPLICATION",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp,
                                fontSize = 10.sp
                            )
                        )
                    }

                    Text(
                        text = "CYCLE $cycleIndex/$totalItems",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Text(
                        text = tasbeeh.arabic,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 42.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = tasbeeh.translation,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.95f),
                        lineHeight = 22.sp,
                        fontSize = 14.sp
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "— ${tasbeeh.source}",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f))

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onResetOffset,
                        modifier = Modifier.size(36.dp),
                        enabled = daysSinceStart > 0
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Day",
                            tint = if (daysSinceStart > 0) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Button(
                        onClick = onAdvanceDay,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f),
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(
                            text = "Next Day",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HabitPollItem(
    habit: HabitUiModel,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    var showConfirmDelete by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("habit_card_${habit.id}")
            .clip(RoundedCornerShape(16.dp))
            .clickable { onToggle() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (habit.isCompletedToday) {
                MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.outline
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            val fillFraction = habit.progressPercent
            if (fillFraction > 0f) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = if (habit.isCompletedToday) {
                                    listOf(
                                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f),
                                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.15f)
                                    )
                                } else {
                                    listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.03f)
                                    )
                                }
                            )
                        )
                        .fillMaxHeight()
                        .fillMaxWidth(fillFraction)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(
                            if (habit.isCompletedToday) {
                                MaterialTheme.colorScheme.secondary
                            } else {
                                Color.Transparent
                            }
                        )
                        .border(
                            width = 2.dp,
                            color = if (habit.isCompletedToday) {
                                MaterialTheme.colorScheme.secondary
                            } else {
                                MaterialTheme.colorScheme.outline
                            },
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (habit.isCompletedToday) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Checked",
                            tint = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = habit.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 15.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (habit.isCompletedToday) {
                            "Day ${habit.currentChallengeDay} of ${habit.durationDays} • Completed Today ✨"
                        } else {
                            "Day ${habit.currentChallengeDay} of ${habit.durationDays} • Streak: ${habit.currentStreak} days 🔥"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (habit.isCompletedToday) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                }

                IconButton(
                    onClick = { showConfirmDelete = true },
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("delete_habit_${habit.id}")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Delete Habit",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }

    if (showConfirmDelete) {
        AlertDialog(
            onDismissRequest = { showConfirmDelete = false },
            title = { Text("Delete Habit?") },
            text = { Text("This will permanently remove '${habit.name}' and its completion history.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showConfirmDelete = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDelete = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun CompletedHabitItem(
    habit: HabitUiModel,
    onRestart: () -> Unit,
    onArchive: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("completed_habit_${habit.id}")
            .clip(RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🏆", fontSize = 22.sp)
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = habit.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 16.sp
                        )
                    )
                    Text(
                        text = "Successfully completed the ${habit.durationDays}-Day Challenge!",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onRestart,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1.4f).height(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Restart Habit",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Restart", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onArchive,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Complete Habit",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Done", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun EmptyHabitsPlaceholder(selectedTab: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Checklist,
                    contentDescription = "No Habits",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = if (selectedTab == 0) "Nurture Daily Growth" else "No Completed Challenges",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (selectedTab == 0) {
                    "Establish mindful habits. Tap the plus button below to create your first personal challenge."
                } else {
                    "Your completed milestones will be preserved here so you can restart them or celebrate them anytime!"
                },
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall.copy(
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            )
        }
    }
}

@Composable
fun AddHabitDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Int) -> Unit
) {
    var habitName by remember { mutableStateOf("") }
    val durations = listOf(7, 21, 30, 40)
    var selectedDuration by remember { mutableStateOf(21) }
    var errorText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Create Habit Challenge",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = habitName,
                    onValueChange = {
                        habitName = it
                        if (errorText.isNotEmpty() && it.isNotBlank()) {
                            errorText = ""
                        }
                    },
                    label = { Text("Habit Name") },
                    placeholder = { Text("e.g. Read Quran, Drink Water") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("habit_name_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                if (errorText.isNotEmpty()) {
                    Text(
                        text = errorText,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Challenge Duration",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        durations.forEach { days ->
                            val isSelected = selectedDuration == days
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { selectedDuration = days }
                                    .testTag("duration_chip_$days"),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        Color.Transparent
                                    }
                                ),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = if (isSelected) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.outline
                                    }
                                )
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$days Days",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) {
                                                MaterialTheme.colorScheme.onPrimary
                                            } else {
                                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                            }
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (habitName.isBlank()) {
                        errorText = "Please enter a habit name"
                    } else {
                        onConfirm(habitName.trim(), selectedDuration)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.testTag("confirm_add_habit"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Start Challenge")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            ) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
fun MilestoneDialog(
    habitName: String,
    dayNumber: Int,
    message: String,
    onDismiss: () -> Unit
) {
    LaunchedEffect(Unit) {
        try {
            val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
            toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 100)
        } catch (e: Exception) {
            // Safely fail-silent if not supported
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Thank you! 💕", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
            }
        },
        title = {
            Text(
                text = "✨ Milestone Reached! ✨",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🌸", fontSize = 32.sp)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Day $dayNumber of your '$habitName' challenge!",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)),
                    textAlign = TextAlign.Center
                )
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
fun FinalCompletionDialog(
    habitName: String,
    durationDays: Int,
    onRestart: () -> Unit,
    onDone: () -> Unit
) {
    LaunchedEffect(Unit) {
        try {
            val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
            toneGen.startTone(ToneGenerator.TONE_CDMA_PIP, 150)
        } catch (e: Exception) {
            // Safely fail-silent if not supported
        }
    }

    AlertDialog(
        onDismissRequest = onDone,
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onRestart,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1.3f).height(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Restart",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Restart", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onDone,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Done",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Done", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        title = null,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🏡", fontSize = 48.sp)
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Text(
                    text = "🎉 $durationDays Days Done!",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                val congratsText = "Oh my goodness! You did it! You've successfully completed the full $durationDays-day challenge for '$habitName'. You are officially a habit superstar! Your cute habitat is flourishing. 🌱 We are so incredibly proud of your dedication! Keep up the amazing work! 💖"
                
                Text(
                    text = congratsText,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        lineHeight = 22.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("✨", fontSize = 18.sp)
                    Text("⭐", fontSize = 18.sp)
                    Text("✨", fontSize = 18.sp)
                    Text("⭐", fontSize = 18.sp)
                    Text("✨", fontSize = 18.sp)
                }
            }
        },
        shape = RoundedCornerShape(28.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        properties = androidx.compose.ui.window.DialogProperties(
            usePlatformDefaultWidth = true
        )
    )
}

@Composable
fun FallingLeavesOverlay(
    modifier: Modifier = Modifier,
    durationMillis: Long = 7000L,
    onAnimationEnd: () -> Unit = {}
) {
    val leafCount = 28
    val leaves = remember {
        List(leafCount) {
            val startX = (0..1000).random().toFloat() / 1000f
            val startY = -(100..800).random().toFloat()
            val speed = kotlin.random.Random.nextFloat() * (6.5f - 2.5f) + 2.5f
            val rotation = (0..360).random().toFloat()
            val rotationSpeed = kotlin.random.Random.nextFloat() * (2.5f - (-2.5f)) + (-2.5f)
            val size = kotlin.random.Random.nextFloat() * (36f - 16f) + 16f
            val windPhase = (0..360).random().toFloat()
            val windSpeed = kotlin.random.Random.nextFloat() * (0.025f - 0.01f) + 0.01f
            val greenHues = listOf(
                Color(0xFF1B4332), // Forest green
                Color(0xFF2D6A4F), // Medium forest green
                Color(0xFF40916C), // Soft forest green
                Color(0xFF52B788), // Sage green
                Color(0xFF74C69D)  // Pale green mint
            )
            LeafState(
                xFraction = startX,
                y = startY,
                speed = speed,
                rotation = rotation,
                rotationSpeed = rotationSpeed,
                size = size,
                windPhase = windPhase,
                windSpeed = windSpeed,
                color = greenHues.random()
            )
        }
    }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(durationMillis)
        onAnimationEnd()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "falling_leaves")
    val frame by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "frame"
    )

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        // Read frame state to trigger recomposition draw
        val triggerDraw = frame

        leaves.forEach { leaf ->
            leaf.y += leaf.speed
            if (leaf.y > height) {
                leaf.y = -(kotlin.random.Random.nextFloat() * (250f - 50f) + 50f)
                leaf.xFraction = (0..1000).random().toFloat() / 1000f
            }

            leaf.rotation += leaf.rotationSpeed
            leaf.windPhase += leaf.windSpeed
            val windOffset = kotlin.math.sin(leaf.windPhase) * 22f
            val x = (leaf.xFraction * width) + windOffset

            drawContext.canvas.save()
            drawContext.canvas.translate(x, leaf.y)
            drawContext.canvas.rotate(leaf.rotation)

            val path = androidx.compose.ui.graphics.Path().apply {
                moveTo(0f, -leaf.size)
                quadraticTo(leaf.size / 2f, 0f, 0f, leaf.size)
                quadraticTo(-leaf.size / 2f, 0f, 0f, -leaf.size)
                close()
            }
            drawPath(path = path, color = leaf.color.copy(alpha = 0.85f))
            drawContext.canvas.restore()
        }
    }
}

private class LeafState(
    var xFraction: Float,
    var y: Float,
    val speed: Float,
    var rotation: Float,
    val rotationSpeed: Float,
    val size: Float,
    var windPhase: Float,
    val windSpeed: Float,
    val color: Color
)
