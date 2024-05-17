package com.jtsportech.visport.android.racedetail.player

import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.cloudhearing.android.lib_base.base.BaseBindingVmFragment
import com.cloudhearing.android.lib_base.utils.observeEvent
import com.cloudhearing.android.lib_base.utils.observeState
import com.cloudhearing.android.lib_base.utils.toDp
import com.cloudhearing.android.lib_common.image.loadRoundCornerImage
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.databinding.FragmentPlayerBinding
import com.jtsportech.visport.android.playerdetail.PlayerDetailActivity
import com.jtsportech.visport.android.racedetail.player.adapter.LineUpAdapter
import com.jtsportech.visport.android.racedetail.player.adapter.PlayerLeftAdapter
import com.jtsportech.visport.android.racedetail.player.adapter.PlayerRightAdapter
import com.jtsportech.visport.android.utils.img

class PlayerFragment :
    BaseBindingVmFragment<FragmentPlayerBinding, PlayerViewModel>(FragmentPlayerBinding::inflate) {
    private lateinit var matchInfoId: String
    private val playerLeftAdapter: PlayerLeftAdapter by lazy {
        PlayerLeftAdapter().apply {
            setOnClickListener {
                startActivity(Intent(context, PlayerDetailActivity::class.java).apply {
                    putExtra("teamPlayer", it)
                })
            }

        }
    }
    private val playerRightAdapter: PlayerRightAdapter by lazy {
        PlayerRightAdapter().apply {
//            setOnClickListener {
//                startActivity(Intent(context, PlayerDetailActivity::class.java).apply {
//                    putExtra("teamPlayer", it)
//                })
//            }
        }
    }
    private val lineUpRightAdapter: LineUpAdapter by lazy {
        LineUpAdapter(ContextCompat.getDrawable(requireContext(), R.drawable.circle_orange)!!)
    }
    private val lineUpLeftAdapter: LineUpAdapter by lazy {
        LineUpAdapter(ContextCompat.getDrawable(requireContext(), R.drawable.circle_green)!!)
    }

    companion object {
        fun getInstance(matchInfoId: String): PlayerFragment {
            val mInstance = PlayerFragment()
            val bundle = Bundle()
            bundle.putString("matchInfoId", matchInfoId)
            mInstance.arguments = bundle
            return mInstance
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            matchInfoId = requireArguments().getString("matchInfoId").toString()
        }
    }

    override fun initView() {
        viewModel.getRaceDetail(matchInfoId)
        viewModel.getRaceDetailMatchInfo(matchInfoId)
    }

    override fun initData() {
        viewModel.leagueFavoritesListStateFlow.observeState(this@PlayerFragment) {
            if (it.isNotEmpty()) {
                lineUpLeftAdapter.submitList(it)
                playerLeftAdapter.submitList(it)
            }
        }
        viewModel.leagueFavoritesListStateFlow2.observeState(this@PlayerFragment) {
            if (it.isNotEmpty()) {
                lineUpRightAdapter.submitList(it)
                playerRightAdapter.submitList(it)
            }
        }
        viewModel.leagueFavoritesListStateFlowMatchInfo.observeEvent(this@PlayerFragment) {
            binding.team1Name.text = it.team1OrganizationName
            binding.team2Name.text = it.team2OrganizationName
            binding.lineUp.loadRoundCornerImage(
                url = it.team1BackgroundImagePath.img(),
                radius = 6.toDp.toInt()
            )
        }
    }

    override fun initEvent() {
        binding.apply {
            list1.apply {
                adapter = playerLeftAdapter
                layoutManager = LinearLayoutManager(context)
            }
            list2.apply {
                adapter = playerRightAdapter
                layoutManager = LinearLayoutManager(context)
            }
            leftLineup.apply {
                adapter = lineUpLeftAdapter
                layoutManager = GridLayoutManager(context, 5, LinearLayoutManager.HORIZONTAL, true)
            }
            rightLineup.apply {
                adapter = lineUpRightAdapter
                layoutManager = GridLayoutManager(context, 5, LinearLayoutManager.HORIZONTAL, false)
            }
        }
    }
}