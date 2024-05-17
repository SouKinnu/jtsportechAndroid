package com.jtsportech.visport.android.videoplay.videolist

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.cloudhearing.android.lib_base.base.BaseBindingVmFragment
import com.cloudhearing.android.lib_base.utils.observeEvent
import com.cloudhearing.android.lib_base.utils.observeState
import com.cloudhearing.android.lib_common.network.dataSource.racedetail.Event
import com.jtsportech.visport.android.databinding.FragmentVideoListBinding
import com.jtsportech.visport.android.racedetail.video.SpaceItemDecoration
import com.jtsportech.visport.android.videoplay.VideoPlayActivity

class VideoListFragment :
    BaseBindingVmFragment<FragmentVideoListBinding, VideoListViewModel>(FragmentVideoListBinding::inflate) {

    private lateinit var matchInfoId: String
    private lateinit var name: String
    private lateinit var events: ArrayList<Event>

    companion object {
        private var mInstance: VideoListFragment = VideoListFragment()
        fun getInstance(matchInfoId: String, name: String?): VideoListFragment {
            mInstance = VideoListFragment()
            val bundle = Bundle()
            bundle.putString("matchInfoId", matchInfoId)
            bundle.putString("name", name)
            mInstance.arguments = bundle
            return mInstance
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            matchInfoId = requireArguments().getString("matchInfoId").toString()
            name = requireArguments().getString("name").toString()
        }
    }

    override fun initView() {
        events = ArrayList()
    }

    override fun initData() {
        val videoListAdapter = VideoListAdapter(object : VideoListAdapter.OnCallBack {
            override fun onClickListener(eventName: String, id: String) {
                viewModel.apply {
                    getVideoPlayUrl(id, "MATCH_EVENT_VIDEO")
                    leagueFavoritesListStateFlowVideoPlayUrl.observeEvent(this@VideoListFragment) {
                        startActivity(Intent(context, VideoPlayActivity::class.java).apply {
                            putExtra("videoUrl", it.data)
                            putExtra("matchType", "EVENTS")
                            putExtra("eventName", eventName)
                            /*putExtra("hasFavorite", hasFavorite)
                            putExtra("favoriteId", favoriteId)*/
                            putExtra("matchInfoId", id)
                        })
                    }
                }
            }
        })
        viewModel.getFavorite(matchInfoId)
        viewModel.leagueFavoritesListStateFlow.observeState(this) {
            if (name == "null") {
                videoListAdapter.submitList(it)
            } else {
                for (i in it) {
                    if (i.eventName == name) {
                        events.add(i)
                        videoListAdapter.submitList(events)
                    }
                }
            }
        }
        binding.RecyclerView.adapter = videoListAdapter
        binding.RecyclerView.layoutManager = GridLayoutManager(context, 2)
        binding.RecyclerView.addItemDecoration(SpaceItemDecoration(20, 2))
    }

    override fun initEvent() {

    }

}