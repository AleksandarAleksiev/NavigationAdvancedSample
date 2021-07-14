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
import com.example.android.navigationadvancedsample.navigation.NavEventHandler
import com.example.android.navigationadvancedsample.navigation.NavOptions
import com.example.android.navigationadvancedsample.navigation.NavigatorProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.flow.collect
import java.util.*

/**
 * An activity that inflates a layout that has a [BottomNavigationView].
 */
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    private lateinit var binding: ActivityMainBinding

    private val navHandler: NavEventHandler = NavigatorProvider

    private val savedState = TreeSet<String>()

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
                val selectedMenuItem = binding.bottomNav.menu.findItem(binding.bottomNav.selectedItemId)
                val selectedMenuItemRootFragment = selectedMenuItem.navOptions().fragmentClass.name
                val newSelectedMenuItemNavOptions = it.navOptions()
                savedState.add(selectedMenuItemRootFragment)
                supportFragmentManager.saveBackStack(selectedMenuItemRootFragment)
                if (savedState.remove(newSelectedMenuItemNavOptions.fragmentClass.name)) {
                    supportFragmentManager.restoreBackStack(newSelectedMenuItemNavOptions.fragmentClass.name)
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
        super.onBackPressed()
    }

    /**
     * Find the graph where the deeplink is defined
     * Switch the bottom nav to the tab of the deep link graph
     * Navigate to the deep link destination
     */
    private fun handleDeepLink(intent: Intent?) {
        navController.graph.matchDeepLink(NavDeepLinkRequest(intent!!))?.also { deepLink ->
            navController.findDestinationGraph(deepLink.destination.id)?.also { deepLinkGraph ->
                findViewById<BottomNavigationView>(R.id.bottom_nav).selectedItemId = deepLinkGraph.id
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
            replace(R.id.nav_host_container, fragmentNavOptions.fragmentClass, fragmentNavOptions.args, fragmentNavOptions.fragmentClass.name)
            addToBackStack(fragmentNavOptions.fragmentClass.name)
        }
    }

    private fun MenuItem.navOptions() = when (itemId) {
        R.id.home -> NavOptions.FragmentNavOptions(Title::class.java)
        R.id.list -> NavOptions.FragmentNavOptions(Leaderboard::class.java)
        else  -> NavOptions.FragmentNavOptions(Register::class.java)
    }
}