package com.appstudio.finmarka.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Text
import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.appstudio.finmarka.ui.theme.FinmarkaTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class UiComponentsTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun primaryButton_clickAndEnabledState() {
        var clicked = 0
        composeRule.setContent {
            FinmarkaTheme {
                PrimaryButton(text = "Save", enabled = true) { clicked++ }
            }
        }

        composeRule.onNodeWithText("Save").assertIsDisplayed().assertIsEnabled().performClick()
        assertEquals(1, clicked)

        composeRule.setContent {
            FinmarkaTheme {
                PrimaryButton(text = "Disabled", enabled = false) { clicked++ }
            }
        }
        composeRule.onNodeWithText("Disabled").assertIsNotEnabled()
    }

    @Test
    fun appTextField_acceptsInput() {
        val valueState = androidx.compose.runtime.mutableStateOf("")
        composeRule.setContent {
            FinmarkaTheme {
                AppTextField(value = valueState.value, onValueChange = { valueState.value = it }, label = "Name")
            }
        }

        composeRule.onNodeWithText("Name").assertExists().performClick().performTextInput("John")
        composeRule.runOnIdle { assertEquals("John", valueState.value) }
    }

    @Test
    fun appActionTopBar_backAndActionCallbacks_andActionVisibility() {
        var back = 0
        var action = 0
        composeRule.setContent {
            FinmarkaTheme {
                AppActionTopBar(
                    title = "Edit",
                    onNavigationClick = { back++ },
                    actionText = "Done",
                    onActionClick = { action++ },
                    actionEnabled = true
                )
            }
        }

        composeRule.onNodeWithContentDescription("Back").assertExists().performClick()
        composeRule.onNodeWithText("Done").assertIsEnabled().performClick()
        assertEquals(1, back)
        assertEquals(1, action)

        composeRule.setContent {
            FinmarkaTheme {
                AppActionTopBar(title = "No Action", onNavigationClick = {}, actionText = null, onActionClick = null)
            }
        }
        composeRule.onNodeWithText("Done").assertDoesNotExist()
    }

    @Test
    fun bottomNavBar_navigatesWithSelectedItem() {
        var route: String? = null
        composeRule.setContent {
            FinmarkaTheme {
                BottomNavBar(
                    selectedRoute = "home",
                    items = listOf(
                        BottomNavItem("home", Icons.Default.Home, "Home"),
                        BottomNavItem("report", Icons.Default.Home, "Report")
                    ),
                    onNavigate = { route = it }
                )
            }
        }

        composeRule.onNodeWithContentDescription("Report").assertExists().performClick()
        composeRule.runOnIdle { assertEquals("report", route) }
    }

    @Test
    fun appCardAndListItem_renderContent() {
        composeRule.setContent {
            FinmarkaTheme {
                AppCard { Text("Card Body") }
                AppListItem(title = "Title", subtitle = "Subtitle", trailing = { Text("Trailing") })
            }
        }

        composeRule.onNodeWithText("Card Body").assertIsDisplayed()
        composeRule.onNodeWithText("Title").assertIsDisplayed()
        composeRule.onNodeWithText("Subtitle").assertIsDisplayed()
        composeRule.onNodeWithText("Trailing").assertIsDisplayed()
    }
}
