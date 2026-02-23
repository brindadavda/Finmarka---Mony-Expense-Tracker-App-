package com.appstudio.finmarka.ui.screens.splash

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.appstudio.finmarka.ui.theme.FinmarkaTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class SplashScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun splashScreen_navigatesToLock_whenShouldShowLock() {
        var lockCount = 0
        var mainCount = 0
        composeRule.mainClock.autoAdvance = false

        composeRule.setContent {
            FinmarkaTheme {
                SplashScreen(
                    onNavigateToLock = { lockCount++ },
                    onNavigateToMain = { mainCount++ },
                    shouldShowLock = true
                )
            }
        }

        composeRule.onNodeWithText("Finmarka").assertIsDisplayed()
        composeRule.mainClock.advanceTimeBy(1500)
        composeRule.runOnIdle {
            assertEquals(1, lockCount)
            assertEquals(0, mainCount)
        }
    }

    @Test
    fun splashScreen_navigatesToMain_whenShouldNotShowLock() {
        var lockCount = 0
        var mainCount = 0
        composeRule.mainClock.autoAdvance = false

        composeRule.setContent {
            FinmarkaTheme {
                SplashScreen(
                    onNavigateToLock = { lockCount++ },
                    onNavigateToMain = { mainCount++ },
                    shouldShowLock = false
                )
            }
        }

        composeRule.mainClock.advanceTimeBy(1500)
        composeRule.runOnIdle {
            assertEquals(0, lockCount)
            assertEquals(1, mainCount)
        }
    }
}
