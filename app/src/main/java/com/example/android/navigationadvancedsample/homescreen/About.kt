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

package com.example.android.navigationadvancedsample.homescreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.android.navigationadvancedsample.R
import com.example.android.navigationadvancedsample.findDestinationGraph
import com.example.android.navigationadvancedsample.listscreen.MyAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Shows "About"
 */
class About : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.about_btn).setOnClickListener {
            //First we need to find the NavGraph of the destination
            //then switch the bottom nav bar to the correct tab
            //so the state of the nav graph back stack is restored
            //and we can navigate the user to the desired destination
            findNavController().findDestinationGraph(R.id.userProfile)?.let {
                requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav).selectedItemId = it.id
                val bundle = bundleOf(MyAdapter.USERNAME_KEY to "Jump user")
                findNavController().navigate(
                    R.id.action_leaderboard_to_userProfile,
                    bundle)
            }
        }
    }
}
