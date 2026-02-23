package com.appstudio.finmarka.ui.screens.common

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.appstudio.finmarka.ui.theme.FinmarkaTheme
import org.junit.Rule
import org.junit.Test

class ModuleScaffoldTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun moduleScaffold_rendersTitleSubtitleAndContent() {
        composeRule.setContent {
            FinmarkaTheme {
                ModuleScaffold(title = "Accounts", subtitle = "Manage accounts") {
                    SectionCard(title = "Overview", body = "Body text")
                }
            }
        }

        composeRule.onNodeWithText("Accounts").assertIsDisplayed()
        composeRule.onNodeWithText("Manage accounts").assertIsDisplayed()
        composeRule.onNodeWithText("Overview").assertIsDisplayed()
        composeRule.onNodeWithText("Body text").assertIsDisplayed()
    }

    @Test
    fun summaryRowAndBulletList_renderAllItems() {
        composeRule.setContent {
            FinmarkaTheme {
                SummaryRow("Income" to "$100", "Expense" to "$70")
                BulletList("Tips", listOf("Track daily", "Review weekly"))
            }
        }

        composeRule.onNodeWithText("Income").assertIsDisplayed()
        composeRule.onNodeWithText("$100").assertIsDisplayed()
        composeRule.onNodeWithText("Expense").assertIsDisplayed()
        composeRule.onNodeWithText("$70").assertIsDisplayed()
        composeRule.onNodeWithText("Tips").assertIsDisplayed()
        composeRule.onNodeWithText("Track daily").assertIsDisplayed()
        composeRule.onNodeWithText("Review weekly").assertIsDisplayed()
    }
}
