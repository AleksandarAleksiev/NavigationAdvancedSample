/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.navigationadvancedsample

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import com.example.android.navigationadvancedsample.databinding.ActivityMainBinding
import com.example.android.navigationadvancedsample.formscreen.Register
import com.example.android.navigationadvancedsample.homescreen.Title
import com.example.android.navigationadvancedsample.listscreen.Leaderboard
import com.example.android.navigationadvancedsample.navigation.BackStackSavedState
import com.example.android.navigationadvancedsample.navigation.NavEventHandler
import com.example.android.navigationadvancedsample.navigation.NavOptions
import com.example.android.navigationadvancedsample.navigation.NavigatorProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.flow.collect

/**
 * An activity that inflates a layout that has a [BottomNavigationView].
 */
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    private lateinit var binding: ActivityMainBinding

    private val navHandler: NavEventHandler = NavigatorProvider

    private val savedState = HashSet<BackStackSavedState>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        handleNavEvents()
        if (savedInstanceState == null) {
            val selectedMenuItem = binding.bottomNav.menu.findItem(binding.bottomNav.selectedItemId)
            NavigatorProvider.navigate(selectedMenuItem.navOptions())
        }
        binding.bottomNav.setOnItemSelectedListener {
            if (it.itemId != binding.bottomNav.selectedItemId) {
                val previouslySelectedMenuItem =
                    binding.bottomNav.menu.findItem(binding.bottomNav.selectedItemId)
                val previouslySelectedMenuItemBackStackState = BackStackSavedState(
                    stateName = previouslySelectedMenuItem.navOptions().fragmentClass.name,
                    bottomNavId = previouslySelectedMenuItem.itemId
                )
                val newSelectedMenuItemNavOptions = it.navOptions()
                val newSelectedMenuBackStackState = BackStackSavedState(
                    stateName = newSelectedMenuItemNavOptions.fragmentClass.name,
                    bottomNavId = it.itemId
                )
                savedState.add(previouslySelectedMenuItemBackStackState)
                supportFragmentManager.saveBackStack(previouslySelectedMenuItemBackStackState.stateName)
                if (savedState.remove(newSelectedMenuBackStackState)) {
                    // for some reason if we do not pop back the current stack before restoring the selected tab stack
                    // then weird things happen on back navigation
                    supportFragmentManager.popBackStack()
                    supportFragmentManager.restoreBackStack(newSelectedMenuBackStackState.stateName)
                } else {
                    NavigatorProvider.navigate(newSelectedMenuItemNavOptions)
                }
            }
            true
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }

    override fun onBackPressed() {
        when {
            supportFragmentManager.backStackEntryCount == 1 && savedState.isEmpty() -> finish()
            supportFragmentManager.backStackEntryCount > 1 || savedState.isEmpty() -> super.onBackPressed()
            else -> {
                savedState.last().also {
                    savedState.remove(it)
                    //should not set the selected menu item trough binding.bottomNav.selectedItemId
                    //because it invokes the logic in OnItemSelectedListener and things get messy
                    binding.bottomNav.menu.findItem(it.bottomNavId).isChecked = true
                    supportFragmentManager.popBackStack()
                    supportFragmentManager.restoreBackStack(it.stateName)
                }
            }
        }
    }

    private fun handleDeepLink(intent: Intent?) {
        navController.graph.matchDeepLink(NavDeepLinkRequest(intent!!))?.also { deepLink ->
            navController.findDestinationGraph(deepLink.destination.id)?.also { deepLinkGraph ->
                findViewById<BottomNavigationView>(R.id.bottom_nav).selectedItemId =
                    deepLinkGraph.id
                navController.navigate(deepLink.destination.id, null)
            }
        }
    }

    private fun handleNavEvents() {
        lifecycleScope.launchWhenStarted {
            navHandler.navEvent.collect {
                when (it) {
                    is NavOptions.FragmentNavOptions<*> -> handleFragmentNavigation(it)
                    else -> {
                        // DO NOTHING YET
                    }
                }
            }
        }
    }

    private fun handleFragmentNavigation(fragmentNavOptions: NavOptions.FragmentNavOptions<*>) {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(
                R.id.nav_host_container,
                fragmentNavOptions.fragmentClass,
                fragmentNavOptions.args,
                fragmentNavOptions.fragmentClass.name
            )
            addToBackStack(fragmentNavOptions.fragmentClass.name)
        }
    }

    private fun MenuItem.navOptions() = when (itemId) {
        R.id.home -> NavOptions.FragmentNavOptions(Title::class.java)
        R.id.list -> NavOptions.FragmentNavOptions(Leaderboard::class.java)
        else -> NavOptions.FragmentNavOptions(Register::class.java)
    }
}