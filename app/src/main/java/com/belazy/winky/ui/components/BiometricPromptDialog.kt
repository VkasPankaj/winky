package com.belazy.winky.ui.components

import android.content.Context
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.Composable
import androidx.fragment.app.FragmentActivity
import java.util.concurrent.Executor
import java.util.concurrent.Executors

fun showBiometricPrompt(
    context: Context,
    title: String,
    description: String,
    onSuccess: () -> Unit,
    onError: () -> Unit
) {
    val executor: Executor = Executors.newSingleThreadExecutor()
    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle(title)
        .setDescription(description)
        .setNegativeButtonText("Cancel")
        .build()

    val biometricPrompt = BiometricPrompt(
        context as FragmentActivity,
        executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                onError()
            }
        }
    )

    biometricPrompt.authenticate(promptInfo)
}
