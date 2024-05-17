package com.jtsportech.visport.android.playerdetail

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jtsportech.visport.android.playerdetail.playerevents.PlayerEventsFragment
import com.jtsportech.visport.android.playerdetail.playervideo.PlayerVideoFragment

class PlayerAdapter(fragmentActivity: FragmentActivity, private var frontUserId: String) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 5
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return PlayerEventsFragment.getInstance(frontUserId, "")
            1 -> return PlayerEventsFragment.getInstance(frontUserId, "MATCH")
            2 -> return PlayerEventsFragment.getInstance(frontUserId, "LEAGUE")
            3 -> return PlayerEventsFragment.getInstance(frontUserId, "TRAIN")
            4 -> return PlayerVideoFragment.getInstance(frontUserId)
        }
        return PlayerEventsFragment.getInstance(frontUserId, "")
    }
}