package com.example.randomchat.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.randomchat.fragments.CallsFragment
import com.example.randomchat.fragments.ChatsFragment
import com.example.randomchat.fragments.StatusFragment

class FragmentDataAdapter(fragmentManager: FragmentManager) :
    FragmentPagerAdapter(fragmentManager) {
    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> ChatsFragment()
            1 -> CallsFragment()
            else -> ChatsFragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "CHATS"
            1 -> "CALLS"
            else -> "CHATS"
        }
    }
}