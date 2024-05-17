package com.jtsportech.visport.android.racedetail.comprehensivedata

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.cloudhearing.android.lib_base.base.BaseBindingVmFragment
import com.cloudhearing.android.lib_base.utils.TAB_INTO_TAB
import com.cloudhearing.android.lib_base.utils.observeState
import com.jeremyliao.liveeventbus.LiveEventBus
import com.jtsportech.visport.android.databinding.FragmentComprehensiveDataBinding

class ComprehensiveDataFragment :
    BaseBindingVmFragment<FragmentComprehensiveDataBinding, ComprehensiveDataViewModel>(
        FragmentComprehensiveDataBinding::inflate
    ) {
    private lateinit var matchInfoId: String

    companion object {
        fun getInstance(matchInfoId: String): ComprehensiveDataFragment {
            val mInstance = ComprehensiveDataFragment()
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
        val comprehensiveDataAdapter = ComprehensiveDataAdapter()
        comprehensiveDataAdapter.setCallBack(object : ComprehensiveDataAdapter.OnItemClickListener {
            override fun callBack(eventName: String) {
                LiveEventBus.get<String>(TAB_INTO_TAB).post(eventName)
            }
        })
        viewModel.leagueFavoritesListStateFlow.observeState(this) {
            comprehensiveDataAdapter.submitList(it)
        }
        binding.RecyclerView.adapter = comprehensiveDataAdapter
        binding.RecyclerView.layoutManager = LinearLayoutManager(context)
    }

    override fun initData() {

    }

    override fun initEvent() {

    }

}