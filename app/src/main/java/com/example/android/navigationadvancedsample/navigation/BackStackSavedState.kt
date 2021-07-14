package com.example.android.navigationadvancedsample.navigation

import androidx.annotation.IdRes

data class BackStackSavedState(
    val stateName: String,
    @IdRes
    val bottomNavId: Int
)