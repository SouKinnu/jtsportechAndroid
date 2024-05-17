package com.jtsportech.visport.android.dataSource

import androidx.annotation.StringDef

/**
 * Author: BenChen
 * Date: 2024/01/08 16:23
 * Email:chenxiaobin@cloudhearing.cn
 */
@StringDef(
    UserRole.HEAD_COACH,
    UserRole.COACH,
    UserRole.MEMBER,
    UserRole.VISITOR,
    UserRole.GUARDER,
    UserRole.UNKOWN,
)
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class UserRole {

    companion object {
        /**
         * 主教练
         */
        const val HEAD_COACH = "HEAD_COACH"

        /**
         * 教练
         */
        const val COACH = "COACH"

        /**
         * 球员
         */
        const val MEMBER = "MEMBER"

        /**
         * 游客
         */
        const val VISITOR = "VISITOR"

        /**
         * 家长
         */
        const val GUARDER = "GUARDER"

        /**
         * 未知
         */
        const val UNKOWN = "UNKOWN"
    }
}
