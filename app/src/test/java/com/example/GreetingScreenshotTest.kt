package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.TasbeehDua
import com.example.ui.TasbeehDuaCard
import com.example.ui.theme.HabitDeedTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    val sampleTasbeeh = TasbeehDua(
        dayNumber = 1,
        title = "Tasbeeh (Glory to Allah)",
        arabic = "سُبْحَانَ اللَّهِ",
        translation = "Glory be to Allah.",
        source = "Sahih al-Bukhari"
    )

    composeTestRule.setContent {
      HabitDeedTheme {
        TasbeehDuaCard(
            tasbeeh = sampleTasbeeh,
            daysSinceStart = 0,
            onAdvanceDay = {},
            onResetOffset = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
