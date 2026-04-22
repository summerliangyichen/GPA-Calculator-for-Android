package com.summer.gpacalculator

import android.content.Intent
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.summer.gpacalculator.ui.UiTags
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    val composeRule = createEmptyComposeRule()

    @Test
    fun canNavigateAcrossEnabledReleasePresets() {
        val scenario = ActivityScenario.launch<MainActivity>(
            Intent(
                ApplicationProvider.getApplicationContext(),
                MainActivity::class.java,
            ).putExtra(MainActivity.EXTRA_CLEAR_STATE, true),
        )
        try {
            composeRule.waitForIdle()
            composeRule.onNodeWithText("Grade 10").assertIsDisplayed()
            composeRule.onNodeWithText("Customize").performClick()
            composeRule.onNodeWithText("Customize").assertIsDisplayed()
            composeRule.onAllNodesWithText("Take a Break").assertCountEquals(0)

            composeRule.onNodeWithContentDescription(UiTags.presetCard("stockshsidgrade11-ib"))
                .performScrollTo()
                .assertIsDisplayed()
            composeRule.onNodeWithContentDescription(UiTags.presetCard("stockshsidgrade11-ib-ee"))
                .performScrollTo()
                .performClick()
            composeRule.onNodeWithContentDescription("Back").performClick()
            composeRule.onNodeWithText("8 subjects").assertIsDisplayed()

            composeRule.onNodeWithText("Customize").performClick()
            composeRule.onNodeWithContentDescription(UiTags.presetCard("stockshsidgrade12-ib"))
                .performScrollTo()
                .performClick()
            composeRule.onAllNodesWithText("Preset Options").assertCountEquals(0)
            composeRule.onNodeWithContentDescription("Back").performClick()
            composeRule.onNodeWithText("7 subjects").assertIsDisplayed()
        } finally {
            scenario.close()
        }
    }
}
