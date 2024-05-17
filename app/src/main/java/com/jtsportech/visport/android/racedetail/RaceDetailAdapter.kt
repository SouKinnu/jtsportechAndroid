package com.jtsportech.visport.android.racedetail

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jtsportech.visport.android.racedetail.comprehensivedata.ComprehensiveDataFragment
import com.jtsportech.visport.android.racedetail.graphanalysis.GraphAnalysisFragment
import com.jtsportech.visport.android.racedetail.player.PlayerFragment
import com.jtsportech.visport.android.racedetail.video.VideoFragment

class RaceDetailAdapter(fragmentActivity: FragmentActivity, var matchInfoId: String) :
    FragmentStateAdapter(fragmentActivity) {
    private val comprehensiveDataFragment: ComprehensiveDataFragment by lazy {
        ComprehensiveDataFragment.getInstance(matchInfoId)
    }
    private val videoFragment: VideoFragment by lazy {
        VideoFragment.getInstance(matchInfoId)
    }
    private val graphAnalysisFragment: GraphAnalysisFragment by lazy {
        GraphAnalysisFragment.getInstance(matchInfoId)
    }
    private val playerFragment: PlayerFragment by lazy {
        PlayerFragment.getInstance(matchInfoId)
    }

    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> comprehensiveDataFragment
            1 -> videoFragment
            2 -> graphAnalysisFragment
            3 -> playerFragment
            else -> comprehensiveDataFragment
        }

    }

}