package com.jtsportech.visport.android.mine.myFavorites

/**
 * Author: BenChen
 * Date: 2024/03/04 10:00
 * Email:chenxiaobin@cloudhearing.cn
 */
interface IEditModeView {
    fun setEditMode(isEditMode: Boolean)

    fun setCheckAll(isCheckAll: Boolean)

    fun unFavorite()

    fun updateFavorites()
}