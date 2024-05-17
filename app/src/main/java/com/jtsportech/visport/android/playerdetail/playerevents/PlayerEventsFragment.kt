package com.jtsportech.visport.android.playerdetail.playerevents

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.cloudhearing.android.lib_base.base.BaseBindingVmFragment
import com.cloudhearing.android.lib_base.utils.observeEvent
import com.cloudhearing.android.lib_common.network.dataSource.player.playerevents.PlayerEventsEntityItem
import com.cloudhearing.android.lib_common.utils.DensityUtils
import com.jtsportech.visport.android.databinding.FragmentPlayerListBinding
import com.jtsportech.visport.android.playerdetail.simpleplay.SimplePlayActivity
import com.jtsportech.visport.android.racedetail.video.SpaceItemDecoration

class PlayerEventsFragment :
    BaseBindingVmFragment<FragmentPlayerListBinding, PlayerEventsViewModel>(
        FragmentPlayerListBinding::inflate
    ) {
    private lateinit var frontUserId: String
    private lateinit var matchType: String
    private val playerAdapter: PlayerEventsAdapter by lazy {
        PlayerEventsAdapter()
    }
    private var count: Int = 0

    companion object {
        private lateinit var mInstance: PlayerEventsFragment
        fun getInstance(frontUserId: String, matchType: String): PlayerEventsFragment {
            mInstance = PlayerEventsFragment()
            val bundle = Bundle()
            bundle.putString("frontUserId", frontUserId)
            bundle.putString("matchType", matchType)
            mInstance.arguments = bundle
            return mInstance
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            frontUserId = requireArguments().getString("frontUserId").toString()
            matchType = requireArguments().getString("matchType").toString()
        }
        count = if (DensityUtils.px2dp(resources.displayMetrics.widthPixels.toFloat()) >= 800) {
            4
        } else {
            2
        }
    }

    override fun initView() {
        viewModel.getPlayerEvents(frontUserId, matchType)
        playerAdapter.setOnItemClickListener(object : PlayerEventsAdapter.OnItemClickListener {
            override fun onItemClick(item: PlayerEventsEntityItem) {
                SimplePlayActivity.jump(
                    activity!!,
                    item.eventPlayerList[0].matchVideoEventId,
                    "MATCH_EVENT_VIDEO",
                    item.eventName + " - " + item.eventNum.toString()
                )
            }
        })
        viewModel.leagueFavoritesListStateFlow.observeEvent(this) {
            playerAdapter.submitList(it)
        }
        binding.RecyclerView.adapter = playerAdapter
        binding.RecyclerView.layoutManager = GridLayoutManager(context, count)
        binding.RecyclerView.addItemDecoration(SpaceItemDecoration(12, count))
    }


    override fun initData() {

    }

    override fun initEvent() {

    }
}