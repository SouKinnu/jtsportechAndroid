package com.jtsportech.visport.android.playerdetail.playervideo

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.cloudhearing.android.lib_base.base.BaseBindingVmFragment
import com.cloudhearing.android.lib_base.utils.observeEvent
import com.cloudhearing.android.lib_common.network.dataSource.player.PlayerEntityItem
import com.cloudhearing.android.lib_common.utils.DensityUtils
import com.jtsportech.visport.android.databinding.FragmentPlayerVideoBinding
import com.jtsportech.visport.android.playerdetail.simpleplay.SimplePlayActivity
import com.jtsportech.visport.android.racedetail.video.SpaceItemDecoration


class PlayerVideoFragment : BaseBindingVmFragment<FragmentPlayerVideoBinding, PlayerVideoViewModel>(
    FragmentPlayerVideoBinding::inflate
) {
    private lateinit var frontUserId: String
    private var count: Int = 0

    companion object {
        private var mInstance: PlayerVideoFragment = PlayerVideoFragment()
        fun getInstance(frontUserId: String): PlayerVideoFragment {
            mInstance = PlayerVideoFragment()
            val bundle = Bundle()
            bundle.putString("frontUserId", frontUserId)
            mInstance.arguments = bundle
            return mInstance
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            frontUserId = requireArguments().getString("frontUserId").toString()
        }
        count = if (DensityUtils.px2dp(resources.displayMetrics.widthPixels.toFloat()) >= 800) {
            4
        } else {
            2
        }
    }

    override fun initView() {
        viewModel.getPlayerVideo(frontUserId)
        val playerAdapter = PlayerVideoAdapter()
        playerAdapter.setOnItemClickListener(object : PlayerVideoAdapter.OnItemClickListener {
            override fun onItemClick(item: PlayerEntityItem) {
                SimplePlayActivity.jump(
                    activity!!,
                    item.id,
                    "MATCH_PLAYER_VIDEO",
                    item.matchInfoName + "-" + item.playerFrontUserName
                )
            }

        })
        viewModel.leagueFavoritesListStateFlow.observeEvent(this) {
            playerAdapter.submitList(it)
        }
        binding.RecyclerView.adapter = playerAdapter
        binding.RecyclerView.layoutManager = GridLayoutManager(context, 2)
        binding.RecyclerView.addItemDecoration(SpaceItemDecoration(12, 2))
    }


    override fun initData() {

    }

    override fun initEvent() {

    }


}