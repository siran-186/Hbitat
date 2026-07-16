package com.example

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.data.AppDatabase
import com.example.data.HabitRepository
import com.example.ui.HabitHomeScreen
import com.example.ui.HabitViewModel
import com.example.ui.HabitViewModelFactory
import com.example.ui.theme.HabitDeedTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Initialize Room Database and Repository
        val database = AppDatabase.getDatabase(this)
        val repository = HabitRepository(database.habitDao())

        // 2. Initialize SharedPreferences for lightweight configs (simulation, timestamps)
        val sharedPrefs = getSharedPreferences("habit_deed_prefs", Context.MODE_PRIVATE)

        // 3. Instantiate ViewModel via Factory
        val viewModelFactory = HabitViewModelFactory(repository, sharedPrefs)
        val viewModel = ViewModelProvider(this, viewModelFactory)[HabitViewModel::class.java]

        setContent {
            HabitDeedTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // 4. Render main Home Screen component passing the inner padding
                    HabitHomeScreen(
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
