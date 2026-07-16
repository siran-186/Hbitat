package com.example.data

import kotlinx.coroutines.flow.Flow

class HabitRepository(private val habitDao: HabitDao) {
    val allHabits: Flow<List<Habit>> = habitDao.getAllHabits()
    val allCompletions: Flow<List<HabitCompletion>> = habitDao.getAllCompletions()

    suspend fun getHabitById(habitId: Int): Habit? = habitDao.getHabitById(habitId)

    suspend fun updateCompletionStatus(habitId: Int, isCompleted: Boolean) {
        habitDao.updateCompletionStatus(habitId, isCompleted)
    }

    suspend fun updateArchivedStatus(habitId: Int, isArchived: Boolean) {
        habitDao.updateArchivedStatus(habitId, isArchived)
    }

    suspend fun updateCelebratedStatus(habitId: Int, hasCelebrated: Boolean) {
        habitDao.updateCelebratedStatus(habitId, hasCelebrated)
    }

    suspend fun resetHabitChallenge(habitId: Int, createdAt: Long) {
        habitDao.resetHabitChallenge(habitId, createdAt)
        habitDao.deleteCompletionsForHabit(habitId)
    }

    suspend fun insertHabit(habit: Habit): Long {
        return habitDao.insertHabit(habit)
    }

    suspend fun deleteHabit(habitId: Int) {
        habitDao.deleteHabitById(habitId)
        habitDao.deleteCompletionsForHabit(habitId)
    }

    suspend fun addCompletion(habitId: Int, dateString: String) {
        habitDao.insertCompletion(HabitCompletion(habitId, dateString))
    }

    suspend fun removeCompletion(habitId: Int, dateString: String) {
        habitDao.deleteCompletion(habitId, dateString)
    }
}
