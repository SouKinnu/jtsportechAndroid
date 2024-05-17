package com.jtsportech.visport.android.racedetail.graphanalysis

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.cloudhearing.android.lib_base.base.BaseBindingVmFragment
import com.cloudhearing.android.lib_base.utils.observeEvent
import com.cloudhearing.android.lib_base.utils.observeState
import com.cloudhearing.android.lib_common.network.dataSource.racedetail.ChartData
import com.cloudhearing.android.lib_common.network.dataSource.racedetail.RaceDetailEntity
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.databinding.FragmentGraphAnalysisBinding
import com.jtsportech.visport.android.utils.img


class GraphAnalysisFragment :
    BaseBindingVmFragment<FragmentGraphAnalysisBinding, GraphAnalysisViewModel>(
        FragmentGraphAnalysisBinding::inflate
    ) {
    private lateinit var matchInfoId: String
    private lateinit var graphAnalysisAdapter: GraphAnalysisAdapter
    private val chartsAdapter: ChartsAdapter by lazy {
        ChartsAdapter()
    }
    private val pieAdapter: ChartsAdapter by lazy {
        ChartsAdapter()
    }
    private val chartData = ArrayList<ChartData>()
    private val pieData = ArrayList<ChartData>()


    companion object {
        fun getInstance(matchInfoId: String): GraphAnalysisFragment {
            val mInstance = GraphAnalysisFragment()
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
    }

    override fun initData() {
        viewModel.leagueFavoritesListStateMatchInfo.observeEvent(this@GraphAnalysisFragment) {
            chartsAdapter.backgroundPath = it.img()
        }
        viewModel.leagueFavoritesListStateFlow.observeEvent(this@GraphAnalysisFragment) {
            if (it.chartDataList.isNotEmpty()) {
//                chartsAdapter.submitList(it.chartDataList)
                for (i in it.chartDataList) when (i.chartType) {
                    "PIE" -> {
//                        initPieChart(binding.PieChart, i)
                        pieData.add(i)
                    }
                    "BAR" -> {
                        chartData.add(i)
                    }
                }
                chartsAdapter.submitList(chartData)
                pieAdapter.submitList(pieData)

                graphAnalysisAdapter = GraphAnalysisAdapter().apply {
                    submitList(it.dataList)
                }
                binding.RecyclerView.apply {
                    adapter = graphAnalysisAdapter
                    setHasFixedSize(true)
                    isNestedScrollingEnabled = false
                    layoutManager = object : LinearLayoutManager(context) {
                        override fun canScrollVertically(): Boolean {
                            return false
                        }
                    }
                }
            }
            binding.RecyclerViewPieChart.apply {
                adapter = pieAdapter
                setHasFixedSize(true)
                isNestedScrollingEnabled = false
                layoutManager = object : LinearLayoutManager(context) {
                    override fun canScrollVertically(): Boolean {
                        return false
                    }
                }
            }
            binding.RecyclerViewCharts.apply {
                adapter = chartsAdapter
                setHasFixedSize(true)
                isNestedScrollingEnabled = false
                layoutManager = object : LinearLayoutManager(context) {
                    override fun canScrollVertically(): Boolean {
                        return false
                    }
                }
            }
            binding.RecyclerViewCharts.layoutManager
        }
    }

    override fun initEvent() {

    }

//    private fun initPieChart(pieChart: PieChart, chartData: ChartData): PieChart {
//        val pieData = setPieData(chartData.chartData.dataList[0].data.size, chartData).apply {
//            setValueTextColor(ContextCompat.getColor(requireContext(), R.color.white))
//            setValueFormatter(
//                PercentFormatter(pieChart)
//            )
//        }
//        return pieChart.apply {
//            setHoleColor(ContextCompat.getColor(context, R.color.white))
//            transparentCircleRadius = 0f
//            holeRadius = 50f
//            setDrawCenterText(true)
//            isDrawHoleEnabled = true
//            setCenterTextColor(ContextCompat.getColor(context, R.color.dove_gray))
//            setCenterTextSize(16f)
//            rotationAngle = 90F
//            isRotationEnabled = false
//            setUsePercentValues(true)
//            centerText = chartData.chartTitle
//            data = pieData
//            animateXY(1000, 1000)
//            description.text = ""
//            setDrawEntryLabels(false)
//            invalidate()
//            legend.apply {
//                form = Legend.LegendForm.SQUARE
//                xEntrySpace = 5f
//                formSize = 10f
//                textSize = 10f
//            }
//        }
//    }
//
//    private fun setPieData(count: Int, chartData: ChartData): PieData {
//        val pieEntryList = ArrayList<PieEntry>()
//        for (i in 0..<count) {
//            pieEntryList.add(
//                PieEntry(
//                    chartData.chartData.dataList[0].data[i].toFloat(),
//                    chartData.chartData.titles[i]
//                )
//            )
//        }
//        val pieDataSet = PieDataSet(pieEntryList, "").apply {
//            sliceSpace = 2f
//            colors = ArrayList<Int>().apply {
//                add(ContextCompat.getColor(requireContext(), R.color.chart_blue))
//                add(ContextCompat.getColor(requireContext(), R.color.chart_orange))
//                add(ContextCompat.getColor(requireContext(), R.color.chart_pink))
//                add(ContextCompat.getColor(requireContext(), R.color.chart_purple))
//                add(ContextCompat.getColor(requireContext(), R.color.chart_green))
//                add(ContextCompat.getColor(requireContext(), R.color.east_bay))
//                add(ContextCompat.getColor(requireContext(), R.color.texas_rose))
//                add(ContextCompat.getColor(requireContext(), R.color.supernova))
//            }
//            selectionShift = 5 * (resources.displayMetrics.densityDpi / 160f)
//        }
//        return PieData(pieDataSet)
//    }
}