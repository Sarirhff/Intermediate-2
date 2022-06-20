package com.example.mystoryapp.myadapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.mystoryapp.main.FragmentHome
import com.example.mystoryapp.main.FragmentMaps

class FragmentOptionsAdapter (activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = FragmentHome()
            1 -> fragment = FragmentMaps()
        }
        return fragment as Fragment
    }
}