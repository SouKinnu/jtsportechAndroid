package com.jtsportech.visport.android.videoplay

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.cloudhearing.android.lib_common.network.dataSource.racedetail.EventName
import com.jtsportech.visport.android.videoplay.videolist.VideoListFragment

class VideoTabAdapter(
    private var eventNameList: List<EventName>,
    fragmentActivity: FragmentActivity
) :
    FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return eventNameList.size + 1
    }

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            VideoListFragment.getInstance("1765695381018542082", "null")
        } else {
            VideoListFragment.getInstance("1765695381018542082", eventNameList[position - 1].name)
        }
    }
}