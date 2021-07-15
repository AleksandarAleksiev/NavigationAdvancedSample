package com.example.android.navigationadvancedsample

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.android.navigationadvancedsample.listscreen.Leaderboard
import com.example.android.navigationadvancedsample.navigation.NavOptions
import com.example.android.navigationadvancedsample.navigation.NavigatorProvider

class BlankFragment : Fragment(R.layout.fragment_blank) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.nav_button).setOnClickListener {
            NavigatorProvider.navigate(NavOptions.FragmentPopBackNavOptions(Leaderboard::class.java))
        }
    }
}