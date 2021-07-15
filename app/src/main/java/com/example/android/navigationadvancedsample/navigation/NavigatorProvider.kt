package com.example.android.navigationadvancedsample.navigation

import android.os.Bundle
import androidx.annotation.AnimRes
import androidx.fragment.app.Fragment
import com.example.android.navigationadvancedsample.R
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class Animations(
    @AnimRes
    val enterAnimation: Int,
    @AnimRes
    val exitAnimation: Int,
    @AnimRes
    val popEnterAnimation: Int,
    @AnimRes
    val popExitAnimation: Int,
) {
    companion object {
        fun bottomNavAnimation() = Animations(
            enterAnimation = R.anim.nav_default_enter_anim,
            exitAnimation = R.anim.nav_default_exit_anim,
            popEnterAnimation = R.anim.nav_default_pop_enter_anim,
            popExitAnimation = R.anim.nav_default_pop_exit_anim
        )

        fun screenTransitionAnimation() = Animations(
            enterAnimation = R.anim.fade_slide_in_bottom,
            exitAnimation = R.anim.slide_out_top,
            popEnterAnimation = R.anim.fade_slide_in_top,
            popExitAnimation = R.anim.fade_slide_out_bottom
        )
    }
}

sealed class NavOptions {
    object Empty : NavOptions()
    data class FragmentNavOptions<T : Fragment>(val fragmentClass: Class<T>, val args: Bundle? = null, val animations: Animations? = null) : NavOptions()
    data class FragmentPopBackNavOptions<T : Fragment>(val fragmentClass: Class<T>?) : NavOptions()
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