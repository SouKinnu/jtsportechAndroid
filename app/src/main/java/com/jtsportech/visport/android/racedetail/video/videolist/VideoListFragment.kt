package com.jtsportech.visport.android.racedetail.video.videolist

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.cloudhearing.android.lib_base.base.BaseBindingVmFragment
import com.cloudhearing.android.lib_base.utils.CURRENT_PLAY_INFO
import com.cloudhearing.android.lib_base.utils.CUT_VIDEO_ID
import com.cloudhearing.android.lib_base.utils.CUT_VIDEO_TYPE
import com.cloudhearing.android.lib_base.utils.observeEvent
import com.cloudhearing.android.lib_common.network.dataSource.racedetail.Event
import com.cloudhearing.android.lib_common.network.dataSource.racedetail.RaceDetailEntity
import com.cloudhearing.android.lib_common.utils.DensityUtils
import com.jeremyliao.liveeventbus.LiveEventBus
import com.jtsportech.visport.android.databinding.FragmentVideoListBinding
import com.jtsportech.visport.android.racedetail.video.SpaceItemDecoration

class VideoListFragment :
    BaseBindingVmFragment<FragmentVideoListBinding, VideoListViewModel>(FragmentVideoListBinding::inflate) {

    private lateinit var matchInfoId: String
    private lateinit var name: String
    private var isTab: Boolean = false
    private lateinit var events: ArrayList<Event>
    private var count: Int = 0
    private var selectIndex = -1
    private lateinit var videoListAdapter2: VideoList2Adapter
    private lateinit var videoListAdapter3: VideoList3Adapter
    private lateinit var raceEntity: RaceDetailEntity

    companion object {
        private var mInstance: VideoListFragment = VideoListFragment()
        fun getInstance(matchInfoId: String, name: String?, isTab: Boolean): VideoListFragment {
            mInstance = VideoListFragment()
            val bundle = Bundle()
            bundle.putString("matchInfoId", matchInfoId)
            bundle.putString("name", name)
            bundle.putBoolean("isTab", isTab)
            mInstance.arguments = bundle
            return mInstance
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            matchInfoId = requireArguments().getString("matchInfoId").toString()
            name = requireArguments().getString("name").toString()
            isTab = requireArguments().getBoolean("isTab")
        }
        count =
            if (isTab && DensityUtils.px2dp(resources.displayMetrics.widthPixels.toFloat()) >= 800) {
                4
            } else {
                2
            }
    }

    override fun initView() {
        events = ArrayList()
        viewModel.getFavorite(matchInfoId)
    }

    override fun initData() {

        viewModel.leagueFavoritesListStateFlow.observeEvent(this) {
            raceEntity = it
            if (name == "null") {
                videoListAdapter3 = VideoList3Adapter(isTab)
                videoListAdapter3.submitList(it.videoList)
                videoListAdapter3.setOnClickListener {
                    LiveEventBus.get<String>(CUT_VIDEO_TYPE).post("MATCH_VIDEO")
                }
                binding.RecyclerView.adapter = videoListAdapter3
            } else {
                videoListAdapter2 = VideoList2Adapter(isTab)
                for (i in it.eventList) {
                    if (i.eventName == name) {
                        events.add(i)
                        videoListAdapter2.submitList(events)
                    }
                }
                videoListAdapter2.setOnClickListener {
                    LiveEventBus.get<String>(CUT_VIDEO_ID).post(it)
                }
                binding.RecyclerView.adapter = videoListAdapter2
            }
            binding.RecyclerView.layoutManager = GridLayoutManager(context, count)
            binding.RecyclerView.addItemDecoration(SpaceItemDecoration(10, count))
        }
        LiveEventBus.get<String>(CURRENT_PLAY_INFO).observe(this) {
            if (!::raceEntity.isInitialized) {
                return@observe
            }
            val split = it.split("+")
            if (name == split[0]) {
                if (name == "null") {
                    selectIndex = split[1].toInt()
                    videoListAdapter3.setSelectIndex(selectIndex)
                } else {
                    events.forEachIndexed { index, event ->
                        if (event.id == split[1]) {
                            selectIndex = index
                            videoListAdapter2.setSelectIndex(selectIndex)
                            return@forEachIndexed
                        }
                    }
                }
            } else {
                if (selectIndex != -1) {
                    if (name == "null") {
                        videoListAdapter3.setUnSelectIndex(selectIndex)
                    } else {
                        videoListAdapter2.setUnSelectIndex(selectIndex)
                    }
                    selectIndex = -1
                }
            }
        }
    }

    override fun initEvent() {

    }

}