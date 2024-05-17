package com.jtsportech.visport.android.mine.myFavorites

import com.cloudhearing.android.lib_common.network.dataSource.home.competition.Competition

/**
 * Author: BenChen
 * Date: 2024/03/04 10:28
 * Email:chenxiaobin@cloudhearing.cn
 */
interface IEditModeViewModel {

    fun switchEditMode(isEditMode: Boolean)
    fun updateSelectedState(competition: Competition)
    fun checkAll(isCheckAll: Boolean)
}