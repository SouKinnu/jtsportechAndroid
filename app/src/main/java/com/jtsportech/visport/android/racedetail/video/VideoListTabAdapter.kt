package com.jtsportech.visport.android.racedetail.video

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.cloudhearing.android.lib_common.network.dataSource.racedetail.EventName
import com.jtsportech.visport.android.racedetail.video.videolist.VideoListFragment

class VideoListTabAdapter(
    fragment: Fragment,
    private var eventNameList: List<EventName>,
    var matchInfoId: String,
    var isTab: Boolean
) :
    FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return eventNameList.size + 1
    }

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            VideoListFragment.getInstance(matchInfoId, "null", isTab)
        } else {
            VideoListFragment.getInstance(matchInfoId, eventNameList[position - 1].name, isTab)
        }
    }
}