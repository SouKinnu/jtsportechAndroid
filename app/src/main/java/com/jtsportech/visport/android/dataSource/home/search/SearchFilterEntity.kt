package com.jtsportech.visport.android.dataSource.home.search

import androidx.annotation.IntDef

/**
 * Author: BenChen
 * Date: 2024/03/27 17:38
 * Email:chenxiaobin@cloudhearing.cn
 */
data class SearchFilterEntity(
    @SearchFilterLayoutType
    val type: Int,
    @SearchFilterTitleType
    val titleType: Int,
    val title: String,
    val subTitle: String,
    val isSelected: Boolean? = null,
    val publishStartTime: String? = null,
    val publishEndTime: String? = null,
    val durationFrom: Long? = null,
    val durationTo: Long? = null,
    val matchType: String? = null,
    val leagueId: String? = null,
    val isMore: Boolean? = null,
    val hasMoreView: Boolean? = null
)

@IntDef(
    SearchFilterLayoutType.TITLE,
    SearchFilterLayoutType.CONTENT,
)
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class SearchFilterLayoutType {

    companion object {
        /**
         * 标题
         */
        const val TITLE = 0

        /**
         * 内容
         */
        const val CONTENT = 1
    }
}

@IntDef(
    SearchFilterTitleType.PUBLISH_TIME,
    SearchFilterTitleType.DURATION_TIME,
    SearchFilterTitleType.MATCH_TYPE,
)
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class SearchFilterTitleType {

    companion object {
        /**
         * 发布时间
         */
        const val PUBLISH_TIME = 0

        /**
         * 视频时长
         */
        const val DURATION_TIME = 1

        /**
         * 检索范围
         */
        const val MATCH_TYPE = 2
    }
}
