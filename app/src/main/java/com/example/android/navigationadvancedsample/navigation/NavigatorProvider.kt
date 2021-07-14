package com.example.android.navigationadvancedsample.navigation

import android.os.Bundle
import androidx.fragment.app.Fragment
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class NavOptions {
    object Empty : NavOptions()
    data class FragmentNavOptions<T : Fragment>(val fragmentClass: Class<T>, val args: Bundle? = null) : NavOptions()
}

interface NavEventDispatcher {
    fun navigate(options: NavOptions)
}

interface NavEventHandler {
    val navEvent: StateFlow<NavOptions>
}

object NavigatorProvider : NavEventDispatcher, NavEventHandler {
    override val navEvent = MutableStateFlow<NavOptions>(NavOptions.Empty)
    override fun navigate(options: NavOptions) {
        MainScope().launch {
            navEvent.emit(options)
        }
    }
}