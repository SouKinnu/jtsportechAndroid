package com.jtsportech.visport.android.racedetail.graphanalysis

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding
import com.cloudhearing.android.lib_base.base.BaseListViewTypeAdapter
import com.cloudhearing.android.lib_common.image.loadRoundCornerImage
import com.cloudhearing.android.lib_common.network.dataSource.racedetail.ChartData
import com.cloudhearing.android.lib_common.network.dataSource.racedetail.ChartDataX
import com.cloudhearing.android.lib_common.utils.DensityUtils
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.databinding.ItemGraphBreBinding
import com.jtsportech.visport.android.databinding.ItemGraphLineBinding
import com.jtsportech.visport.android.databinding.ItemGraphPieBinding
import com.jtsportech.visport.android.databinding.ItemNullBinding


class ChartsAdapter :
    BaseListViewTypeAdapter<ChartData, ViewBinding>(ChartDiffCallback()) {

    lateinit var backgroundPath: String
    override fun getItemViewType(position: Int, item: ChartData): Int {
        return if (item.chartType == "BAR" && item.chartData.dataList.size > 0) 0
        else if (item.chartType == "LINE" && item.chartData.dataList.size > 0) 1
        else if (item.chartType == "PIE" && item.chartData.dataList.size > 0) 2
        else 3
    }

    override fun onCreateBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ViewBinding {
        return when (viewType) {
            0 -> ItemGraphBreBinding.inflate(inflater, parent, false)
            1 -> ItemGraphLineBinding.inflate(inflater, parent, false)
            2 -> ItemGraphPieBinding.inflate(inflater, parent, false)
            else -> ItemNullBinding.inflate(inflater, parent, false)
        }
    }

    override fun onBind(binding: ViewBinding, item: ChartData, position: Int) {
        when (binding) {
            is ItemGraphLineBinding -> {
                val context = binding.root.context
                val chartData = item.chartData
                binding.apply {
                    laiYuan.text = item.chartTitle
                    bac.loadRoundCornerImage(
                        url = backgroundPath,
                    )
                    LineChart.apply {
                        data = initLineChart(context, chartData)
                        legend.apply {
                            textColor = ContextCompat.getColor(context, R.color.dark_green)
                            horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                        }
                        setDrawBorders(false)
                        axisRight.isEnabled = false
                        setTouchEnabled(false)
                        description.isEnabled = false
                        axisLeft.apply {
                            setDrawAxisLine(false)
                            textSize = 8f
                            textColor = ContextCompat.getColor(context, R.color.dove_gray)
                            valueFormatter = object : ValueFormatter() {
                                override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                                    return "${(value).toInt()}%"
                                }
                            }
                        }
                        xAxis.apply {
                            setPosition(XAxis.XAxisPosition.BOTTOM)
                            setDrawGridLines(false)
                            textColor = ContextCompat.getColor(context, R.color.dove_gray)
                            labelRotationAngle = -45F
                            textSize = 8f
                            granularity = 1f
                            labelCount = chartData.titles.size
                            setDrawLabels(true)
                            valueFormatter = MyXAxisFormatter(chartData.titles)
                        }
                    }
                }

            }

            is ItemGraphBreBinding -> {
                val context = binding.root.context
                val chartData = item.chartData
                binding.apply {
                    laiYuan.text = item.chartTitle
                    bac.loadRoundCornerImage(
                        url = backgroundPath,
                    )
                    BarChart.apply {
                        data = initBarChart(context, chartData)
                        legend.apply {
                            textColor = ContextCompat.getColor(context, R.color.dark_green)
                            horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                            textSize = 10F
                        }
                        xAxis.apply {
                            textSize = 8f
                            setCenterAxisLabels(true)
                            axisMaximum = chartData.titles.size.toFloat()
                            axisMinimum = 0f
                            labelCount = chartData.titles.size
                            textColor = ContextCompat.getColor(context, R.color.dove_gray)
                            setPosition(XAxis.XAxisPosition.BOTTOM)
                            labelRotationAngle = -45F
                            setDrawGridLines(false)
                            granularity = 1f
                            setDrawLabels(true)
                            valueFormatter = MyXAxisFormatter(chartData.titles)
                        }
                        axisLeft.apply {
                            textColor = ContextCompat.getColor(context, R.color.dove_gray)
                            textSize = 8f
                            setDrawAxisLine(false)
                            valueFormatter = object : ValueFormatter() {
                                override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                                    return "${(value).toInt()}%"
                                }
                            }
                        }
                        description.isEnabled = false
                        axisRight.isEnabled = false
                        setTouchEnabled(false)
                        invalidate()
                    }
                }
            }

            is ItemGraphPieBinding -> {
                val context = binding.root.context
//                val itemList = ArrayList<String>()
//                item.chartData.dataList[0].data.forEach {
//                    if (it != "0") {
//                        itemList.add(it)
//                    }
//                }
                val pieData = setPieData(context, item.chartData.dataList[0].data.size, item).apply {
                    setValueTextColor(ContextCompat.getColor(context, R.color.white))
                    setValueFormatter(
                        PercentFormatter(binding.PieChart)
                    )
                }
                binding.PieChart.apply {
                    setHoleColor(ContextCompat.getColor(context, R.color.white))
                    transparentCircleRadius = 0f
                    holeRadius = 50f
                    setDrawCenterText(true)
                    isDrawHoleEnabled = true
                    setCenterTextColor(ContextCompat.getColor(context, R.color.dove_gray))
                    setCenterTextSize(DensityUtils.px2sp(context.resources.getDimension(R.dimen.sp_12)))
                    rotationAngle = 270F
                    isRotationEnabled = false
                    setUsePercentValues(true)
                    centerText = item.chartTitle
                    data = pieData
                    animateXY(1000, 1000)
                    description.text = ""
                    setDrawEntryLabels(false)
                    invalidate()
                    legend.apply {
                        form = Legend.LegendForm.SQUARE
                        xEntrySpace = 5f
                        formSize = 10f
                        textSize = 10f
                    }
                }
            }
        }
    }

    class ChartDiffCallback : DiffUtil.ItemCallback<ChartData>() {
        override fun areItemsTheSame(oldItem: ChartData, newItem: ChartData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ChartData, newItem: ChartData): Boolean {
            return oldItem.num == newItem.num
        }
    }

    private fun initLineChart(context: Context, chartDataX: ChartDataX): LineData {
        val entryList: ArrayList<Entry> = ArrayList()
        val entryList2: ArrayList<Entry> = ArrayList()
        for (i in 0..<chartDataX.dataList[0].data.size) {
            entryList.add(BarEntry(i.toFloat(), chartDataX.dataList[0].data[i].toFloat()))
            entryList2.add(BarEntry(i.toFloat(), chartDataX.dataList[1].data[i].toFloat()))
        }
        val lineDataSet = LineDataSet(entryList, chartDataX.dataList[0].name).apply {
            color = ContextCompat.getColor(context, R.color.purple_bar)
            setDrawCircles(false)
        }
        val lineDataSet2 = LineDataSet(entryList2, chartDataX.dataList[1].name).apply {
            color = ContextCompat.getColor(context, R.color.orange)
            setDrawCircles(false)
        }
        return LineData(lineDataSet).apply {
            addDataSet(lineDataSet2)
            setDrawValues(false)
        }
    }

    private fun initBarChart(
        context: Context, chartDataX: ChartDataX
    ): BarData {
        val list: ArrayList<BarEntry> = ArrayList()
        val list2: ArrayList<BarEntry> = ArrayList()

        for (i in 0..<chartDataX.dataList[0].data.size) {
            list.add(BarEntry(i.toFloat(), chartDataX.dataList[0].data[i].toFloat()))
            list2.add(BarEntry(i.toFloat(), chartDataX.dataList[1].data[i].toFloat()))
        }
        val barDataSet = BarDataSet(list, chartDataX.dataList[0].name).apply {
            color = ContextCompat.getColor(context, R.color.purple_bar)
        }
        val barDataSet2 = BarDataSet(list2, chartDataX.dataList[1].name).apply {
            color = ContextCompat.getColor(context, R.color.orange)
        }
        return BarData(barDataSet).apply {
            addDataSet(barDataSet2)
            barWidth = 0.2f
            groupBars(0f, 0.5f, 0.05f)
            setDrawValues(false)
        }
    }

    class MyXAxisFormatter(private var titles: List<String>) : ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return titles.getOrNull((value).toInt()) ?: value.toString()
        }
    }

    private fun setPieData(context: Context, count: Int, chartData: ChartData): PieData {
//        val itemList = ArrayList<String>()
//        chartData.chartData.dataList[0].data.forEach {
//            if (it != "0") {
//                itemList.add(it)
//            }
//        }

        val pieEntryList = ArrayList<PieEntry>()
        for (i in 0..<count) {
            pieEntryList.add(
                PieEntry(
                    chartData.chartData.dataList[0].data[i].toFloat(),
                    chartData.chartData.titles[i]
                )
            )
        }
        val pieDataSet = PieDataSet(pieEntryList, "").apply {
            sliceSpace = 2f
            colors = ArrayList<Int>().apply {
                add(ContextCompat.getColor(context, R.color.pie_1))
                add(ContextCompat.getColor(context, R.color.pie_2))
                add(ContextCompat.getColor(context, R.color.pie_3))
                add(ContextCompat.getColor(context, R.color.pie_4))
                add(ContextCompat.getColor(context, R.color.pie_5))
                add(ContextCompat.getColor(context, R.color.pie_6))
//                add(ContextCompat.getColor(context, R.color.chart_blue))
//                add(ContextCompat.getColor(context, R.color.chart_orange))
//                add(ContextCompat.getColor(context, R.color.chart_pink))
//                add(ContextCompat.getColor(context, R.color.chart_purple))
//                add(ContextCompat.getColor(context, R.color.chart_green))
//                add(ContextCompat.getColor(context, R.color.east_bay))
//                add(ContextCompat.getColor(context, R.color.texas_rose))
//                add(ContextCompat.getColor(context, R.color.supernova))
            }
            selectionShift = 5 * (context.resources.displayMetrics.densityDpi / 160f)
        }
        return PieData(pieDataSet)
    }
}
