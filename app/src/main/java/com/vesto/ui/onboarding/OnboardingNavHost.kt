package com.vesto.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.vesto.viewmodel.OnboardingViewModel

/**
 * Onboarding Navigation Host
 * Manages navigation between budget and currency screens
 */
@Composable
fun OnboardingNavHost(
    viewModel: OnboardingViewModel,
    onOnboardingComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var currentStep by remember { mutableIntStateOf(0) }
    
    AnimatedContent(
        targetState = currentStep,
        transitionSpec = {
            if (targetState > initialState) {
                slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
            } else {
                slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
            }
        },
        label = "onboarding_animation",
        modifier = modifier
    ) { step ->
        when (step) {
            0 -> BudgetSetupScreen(
                uiState = uiState,
                onBudgetChange = viewModel::setBudget,
                onNext = {
                    if (viewModel.validateBudget()) {
                        currentStep = 1
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
            
            1 -> CurrencySelectionScreen(
                uiState = uiState,
                onCurrencySelect = viewModel::selectCurrency,
                onComplete = {
                    viewModel.completeOnboarding(onOnboardingComplete)
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
