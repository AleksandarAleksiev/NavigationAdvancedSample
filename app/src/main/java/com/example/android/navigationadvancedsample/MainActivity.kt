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
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.*
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

/**
 * An activity that inflates a layout that has a [BottomNavigationView].
 */
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_container
        ) as NavHostFragment
        navController = navHostFragment.navController

        // Setup the bottom navigation view with navController
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNavigationView.setupWithNavController(navController)

        // Setup the ActionBar with navController and 3 top level destinations
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.titleScreen, R.id.leaderboard, R.id.register)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
//        navController.handleDeepLink(intent)
        handleDeepLink(intent)
    }

    override fun onBackPressed() {
        /**
         * Need to handle somehow the back button to switch between tabs when we are at the root of the current tab
         * otherwise the default back button behaviour is to always switch to the first bottom nav tab
         * and navigate the user to its root screen
         * With the ugly code below it switches again to the first bottom nav but at least
         * restore the previous state of that tab
         */
        val prevDestinationParent = navController.previousBackStackEntry?.destination?.parent
        val currentDestinationParent = navController.currentDestination?.parent
        val isOnSameGraph = currentDestinationParent != null && currentDestinationParent == prevDestinationParent
        val bottomView = findViewById<BottomNavigationView>(R.id.bottom_nav)
        if (isOnSameGraph || prevDestinationParent == null || bottomView.selectedItemId == prevDestinationParent.id) {
            super.onBackPressed()
        } else {
            bottomView.selectedItemId = prevDestinationParent.id
        }
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
}