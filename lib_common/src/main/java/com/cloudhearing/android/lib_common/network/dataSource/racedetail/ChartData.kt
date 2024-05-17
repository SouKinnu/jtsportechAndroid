package com.cloudhearing.android.lib_common.network.dataSource.racedetail

data class ChartData(
    val chartData: ChartDataX,
    val chartTitle: String,
    val chartType: String,
    val chartUnit: String,
    val id: String,
    val num: Int
)