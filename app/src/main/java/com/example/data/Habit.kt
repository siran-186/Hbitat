package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val durationDays: Int, // 7, 21, 30, 40
    val createdAt: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false,
    val isArchived: Boolean = false,
    val hasCelebrated: Boolean = false
)

@Entity(tableName = "habit_completions", primaryKeys = ["habitId", "dateString"])
data class HabitCompletion(
    val habitId: Int,
    val dateString: String // "YYYY-MM-DD"
)

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits ORDER BY createdAt DESC")
    fun getAllHabits(): Flow<List<Habit>>

    @Query("SELECT * FROM habit_completions")
    fun getAllCompletions(): Flow<List<HabitCompletion>>

    @Query("SELECT * FROM habits WHERE id = :habitId")
    suspend fun getHabitById(habitId: Int): Habit?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit): Long

    @Query("UPDATE habits SET isCompleted = :isCompleted WHERE id = :habitId")
    suspend fun updateCompletionStatus(habitId: Int, isCompleted: Boolean)

    @Query("UPDATE habits SET isArchived = :isArchived WHERE id = :habitId")
    suspend fun updateArchivedStatus(habitId: Int, isArchived: Boolean)

    @Query("UPDATE habits SET hasCelebrated = :hasCelebrated WHERE id = :habitId")
    suspend fun updateCelebratedStatus(habitId: Int, hasCelebrated: Boolean)

    @Query("UPDATE habits SET createdAt = :createdAt, isCompleted = 0, isArchived = 0, hasCelebrated = 0 WHERE id = :habitId")
    suspend fun resetHabitChallenge(habitId: Int, createdAt: Long)

    @Query("DELETE FROM habits WHERE id = :habitId")
    suspend fun deleteHabitById(habitId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletion(completion: HabitCompletion)

    @Query("DELETE FROM habit_completions WHERE habitId = :habitId AND dateString = :dateString")
    suspend fun deleteCompletion(habitId: Int, dateString: String)

    @Query("DELETE FROM habit_completions WHERE habitId = :habitId")
    suspend fun deleteCompletionsForHabit(habitId: Int)
}
