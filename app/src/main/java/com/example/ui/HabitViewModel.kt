package com.example.ui

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Habit
import com.example.data.HabitRepository
import com.example.data.TasbeehData
import com.example.data.TasbeehDua
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class HabitUiModel(
    val id: Int,
    val name: String,
    val durationDays: Int,
    val completedCount: Int,
    val isCompletedToday: Boolean,
    val progressPercent: Float,
    val currentChallengeDay: Int,
    val daysRemaining: Int,
    val isCompleted: Boolean,
    val isArchived: Boolean,
    val hasCelebrated: Boolean,
    val currentStreak: Int
)

data class MilestoneCelebration(
    val habitName: String,
    val dayNumber: Int,
    val message: String
)

data class FinalCompletionCelebration(
    val habitId: Int,
    val habitName: String,
    val durationDays: Int
)

class HabitViewModel(
    private val repository: HabitRepository,
    private val sharedPrefs: SharedPreferences
) : ViewModel() {

    private val _simulationOffset = MutableStateFlow(sharedPrefs.getInt("simulation_offset", 0))
    val simulationOffset: StateFlow<Int> = _simulationOffset

    private val _selectedDate = MutableStateFlow("")
    val selectedDate: StateFlow<String> = _selectedDate

    private val _milestoneCelebrationEvent = MutableStateFlow<MilestoneCelebration?>(null)
    val milestoneCelebrationEvent: StateFlow<MilestoneCelebration?> = _milestoneCelebrationEvent

    private val _finalCelebrationEvent = MutableStateFlow<FinalCompletionCelebration?>(null)
    val finalCelebrationEvent: StateFlow<FinalCompletionCelebration?> = _finalCelebrationEvent

    val encouragementMessages = listOf(
        "You're doing great! ✨",
        "Look at you go! 🌸",
        "So proud of your consistency! 🌱",
        "One step at a time, you're blooming! 🏡",
        "Every small effort adds up beautifully! ⭐",
        "You're creating such a lovely habit! ✨",
        "Keep flowing, you've got this! 🌊",
        "Your dedication is absolutely lovely! 💖",
        "Steady and beautiful progress! 🌿",
        "Look at your habitat grow! 🌳"
    )

    init {
        if (!sharedPrefs.contains("first_opened_timestamp")) {
            sharedPrefs.edit().putLong("first_opened_timestamp", System.currentTimeMillis()).apply()
        }
        updateSelectedDate()
    }

    private fun getCurrentDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun getFormattedDateForOffset(offset: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, offset)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(cal.time)
    }

    private fun updateSelectedDate() {
        _selectedDate.value = getFormattedDateForOffset(_simulationOffset.value)
    }

    fun getDaysSinceStart(): Int {
        val startMillis = sharedPrefs.getLong("first_opened_timestamp", System.currentTimeMillis())
        val currentMillis = System.currentTimeMillis()
        val elapsedDays = ((currentMillis - startMillis) / (24 * 60 * 60 * 1000)).toInt()
        val days = (if (elapsedDays >= 0) elapsedDays else 0) + _simulationOffset.value
        return days
    }

    fun calculateChallengeDay(createdAt: Long, selectedDateStr: String): Int {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val startDateStr = sdf.format(Date(createdAt))
        
        val startCal = Calendar.getInstance().apply {
            time = sdf.parse(startDateStr) ?: Date(createdAt)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val endCal = Calendar.getInstance().apply {
            time = sdf.parse(selectedDateStr) ?: Date()
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val diffMillis = endCal.timeInMillis - startCal.timeInMillis
        val daysElapsed = (diffMillis / (1000 * 60 * 60 * 24)).toInt()
        return (daysElapsed + 1).coerceAtLeast(1)
    }

    fun calculateCurrentStreak(completions: List<String>, todayStr: String): Int {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayDate = sdf.parse(todayStr) ?: return 0
        
        val completedToday = completions.contains(todayStr)
        
        val cal = Calendar.getInstance().apply { time = todayDate }
        cal.add(Calendar.DAY_OF_YEAR, -1)
        val yesterdayStr = sdf.format(cal.time)
        val completedYesterday = completions.contains(yesterdayStr)
        
        if (!completedToday && !completedYesterday) {
            return 0
        }
        
        var streak = 0
        val scanCal = Calendar.getInstance().apply { time = todayDate }
        if (!completedToday) {
            scanCal.add(Calendar.DAY_OF_YEAR, -1)
        }
        
        while (true) {
            val dateStr = sdf.format(scanCal.time)
            if (completions.contains(dateStr)) {
                streak++
                scanCal.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }
        return streak
    }

    val currentTasbeeh: StateFlow<TasbeehDua> = _simulationOffset.map { _ ->
        val days = getDaysSinceStart()
        val index = if (TasbeehData.items.isNotEmpty()) days % TasbeehData.items.size else 0
        TasbeehData.items[index]
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TasbeehData.items[0]
    )

    val habitsList: StateFlow<List<HabitUiModel>> = combine(
        repository.allHabits,
        repository.allCompletions,
        _selectedDate
    ) { habits, completions, selectedDateStr ->
        habits.map { habit ->
            val habitCompletions = completions.filter { it.habitId == habit.id }
            val completedCount = habitCompletions.size
            val isCompletedToday = habitCompletions.any { it.dateString == selectedDateStr }
            val currentChallengeDay = calculateChallengeDay(habit.createdAt, selectedDateStr)
            
            val autoCompleted = currentChallengeDay >= habit.durationDays
            
            // Auto transition to complete in database if duration exceeded or met
            if (autoCompleted && !habit.isCompleted && !habit.isArchived) {
                viewModelScope.launch {
                    repository.updateCompletionStatus(habit.id, true)
                }
            }

            // Auto trigger celebration if we haven't celebrated yet
            if (autoCompleted && !habit.hasCelebrated && !habit.isArchived && _finalCelebrationEvent.value?.habitId != habit.id) {
                _finalCelebrationEvent.value = FinalCompletionCelebration(
                    habitId = habit.id,
                    habitName = habit.name,
                    durationDays = habit.durationDays
                )
            }
            
            val currentStreak = calculateCurrentStreak(habitCompletions.map { it.dateString }, selectedDateStr)
            val progressPercent = (completedCount.toFloat() / habit.durationDays.toFloat()).coerceIn(0f, 1f)
            
            HabitUiModel(
                id = habit.id,
                name = habit.name,
                durationDays = habit.durationDays,
                completedCount = completedCount,
                isCompletedToday = isCompletedToday,
                progressPercent = progressPercent,
                currentChallengeDay = currentChallengeDay.coerceAtMost(habit.durationDays),
                daysRemaining = (habit.durationDays - currentChallengeDay).coerceAtLeast(0),
                isCompleted = habit.isCompleted || autoCompleted,
                isArchived = habit.isArchived,
                hasCelebrated = habit.hasCelebrated || (autoCompleted && habit.hasCelebrated),
                currentStreak = currentStreak
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val activeHabits: StateFlow<List<HabitUiModel>> = habitsList.map { list ->
        list.filter { !it.isCompleted && !it.isArchived }
            .sortedBy { it.daysRemaining }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val completedHabits: StateFlow<List<HabitUiModel>> = habitsList.map { list ->
        list.filter { it.isCompleted && !it.isArchived }
            .sortedByDescending { it.id }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun toggleHabitCompletion(habitId: Int) {
        viewModelScope.launch {
            val dateStr = _selectedDate.value
            if (dateStr.isEmpty()) return@launch
            val habit = habitsList.value.find { it.id == habitId } ?: return@launch
            
            if (habit.isCompletedToday) {
                repository.removeCompletion(habitId, dateStr)
            } else {
                repository.addCompletion(habitId, dateStr)
                
                val currentChallengeDay = habit.currentChallengeDay
                val nextChallengeDay = currentChallengeDay + 1 // since they checked in on currentChallengeDay
                
                if (nextChallengeDay >= habit.durationDays) {
                    // Trigger final celebration
                    _finalCelebrationEvent.value = FinalCompletionCelebration(
                        habitId = habit.id,
                        habitName = habit.name,
                        durationDays = habit.durationDays
                    )
                    repository.updateCompletionStatus(habit.id, true)
                } else {
                    // Check for milestones
                    val milestones = when (habit.durationDays) {
                        7 -> listOf(2, 5)
                        21 -> listOf(7, 14, 15, 20)
                        30 -> listOf(10, 20, 29)
                        40 -> listOf(13, 26, 39)
                        else -> listOf(habit.durationDays / 3, (2 * habit.durationDays) / 3)
                    }
                    if (currentChallengeDay in milestones) {
                        val msg = encouragementMessages.random()
                        _milestoneCelebrationEvent.value = MilestoneCelebration(
                            habitName = habit.name,
                            dayNumber = currentChallengeDay,
                            message = msg
                        )
                    }
                }
            }
        }
    }

    fun restartHabit(habitId: Int) {
        viewModelScope.launch {
            repository.resetHabitChallenge(habitId, System.currentTimeMillis())
        }
    }

    fun archiveHabit(habitId: Int) {
        viewModelScope.launch {
            repository.updateArchivedStatus(habitId, isArchived = true)
        }
    }

    fun clearMilestoneCelebration() {
        _milestoneCelebrationEvent.value = null
    }

    fun clearFinalCelebration(habitId: Int) {
        _finalCelebrationEvent.value = null
        viewModelScope.launch {
            repository.updateCelebratedStatus(habitId, true)
        }
    }

    fun addHabit(name: String, durationDays: Int) {
        viewModelScope.launch {
            repository.insertHabit(Habit(name = name, durationDays = durationDays))
        }
    }

    fun deleteHabit(habitId: Int) {
        viewModelScope.launch {
            repository.deleteHabit(habitId)
        }
    }

    fun advanceSimulationDay() {
        val newOffset = _simulationOffset.value + 1
        sharedPrefs.edit().putInt("simulation_offset", newOffset).apply()
        _simulationOffset.value = newOffset
        updateSelectedDate()
    }

    fun resetSimulationDay() {
        sharedPrefs.edit().putInt("simulation_offset", 0).apply()
        _simulationOffset.value = 0
        updateSelectedDate()
    }
}

class HabitViewModelFactory(
    private val repository: HabitRepository,
    private val sharedPrefs: SharedPreferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HabitViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HabitViewModel(repository, sharedPrefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
