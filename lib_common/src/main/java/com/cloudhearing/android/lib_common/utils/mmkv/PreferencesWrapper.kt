package com.cloudhearing.android.lib_common.utils.mmkv

import androidx.annotation.StringDef

class PreferencesWrapper private constructor() {

    @StringDef(
        USE_MULTIPLE_TIMES_KEY,
        USB_LOCAL_KEY,
        ACCESSTOKEN_KEY,
        USERNAME_KEY,
        PHONE_NUMBER_KEY,
        ROLE_KEY,
        ORGANIZATION_NAME_KEY,
        ORGANIZATION_LOGO_IMAGE_FILE_PATH_KEY,
        USER_HEIGHT_KEY,
        USER_WEIGHT_KEY,
        GROUP_ID_KEY,
        GROUP_NAME_KEY,
        AVATAR_IMAGE_FILE_ID_KEY,
        AVATAR_IMAGE_FILE_PATH_KEY,
        HEAD_COACH_AVATAR_IMAGE_FILE_PATH_KEY,
        HEAD_COACH_FRONT_USER_NAME_KEY,
        LOGO_IMAGE_FILE_PATH_KEY,
        CURRENT_ORGANIZATION_ID_KEY,
        INVITER_FRONT_USER_ID_KEY,
        COMPETITION_SEARCH_LIST_KEY,
        CURRENT_WATCH_VIDEO_ID,
        PITCH_POSITION_KEY,
        PITCH_POSITION_NAME_KEY,
        UNIFORM_NO_KEY,
        BANNER_MATCH_DIALOG_KEY,
        BANNER_TRAIN_DIALOG_KEY,
        BANNER_LEAGUE_DIALOG_KEY
    )
    @Target(AnnotationTarget.VALUE_PARAMETER)
    @Retention(AnnotationRetention.SOURCE)
    annotation class PreferencesKey

    companion object {
        /**
         * 多次使用
         */
        const val USE_MULTIPLE_TIMES_KEY = "use_multiple_times_key"

        /**
         * 使用的语言
         */
        const val USB_LOCAL_KEY = "usb_local_key"

        /**
         * 访问 Token
         */
        const val ACCESSTOKEN_KEY = "accessToken_key"

        /**
         * 用户名
         */
        const val USERNAME_KEY = "username_key"

        /**
         * 手机号
         */
        const val PHONE_NUMBER_KEY = "phone_number_key"

        /**
         * 角色
         */
        const val ROLE_KEY = "role_key"

        /**
         * 组织名称
         */
        const val ORGANIZATION_NAME_KEY = "organization_name_key"

        /**
         * 组织头像文件路径
         */
        const val ORGANIZATION_LOGO_IMAGE_FILE_PATH_KEY = "organization_logo_image_file_path_key"

        /**
         * 身高
         */
        const val USER_HEIGHT_KEY = "user_height_key"

        /**
         * 体重
         */
        const val USER_WEIGHT_KEY = "user_weight_key"

        /**
         * 小组 id
         */
        const val GROUP_ID_KEY = "group_id_key"

        /**
         * 小组名称
         */
        const val GROUP_NAME_KEY = "group_name_key"

        /**
         * 头像图片路径
         */
        const val AVATAR_IMAGE_FILE_PATH_KEY = "avatar_image_file_path_key"

        /**
         * 头像图片 id
         */
        const val AVATAR_IMAGE_FILE_ID_KEY = "avatar_image_file_id_key"

        /**
         * 主教练头像图片文件路径
         */
        const val HEAD_COACH_AVATAR_IMAGE_FILE_PATH_KEY = "head_coach_avatar_image_file_path_key"

        /**
         * 主教练姓名
         */
        const val HEAD_COACH_FRONT_USER_NAME_KEY = "head_coach_front_user_name_key"

        /**
         * logo 图片文件路径
         */
        const val LOGO_IMAGE_FILE_PATH_KEY = "logo_image_file_path_key"

        /**
         * 当前组织 id
         */
        const val CURRENT_ORGANIZATION_ID_KEY = "current_organization_id_key"

        /**
         * 邀请人用户 ID
         */
        const val INVITER_FRONT_USER_ID_KEY = "inviter_front_user_id_key"

        /**
         * 比赛的搜索列表
         */
        const val COMPETITION_SEARCH_LIST_KEY = "competition_search_list_key"

        /**
         * 当前观看视频的ID
         */
        const val CURRENT_WATCH_VIDEO_ID = "current_watch_video_id"
        /**
         * 是否打开多个播放器
         */
        const val VIDEO_PLAYER_IS_LIVE = "video_player_is_live"

        /**
         * 球场位置
         */
        const val PITCH_POSITION_KEY = "pitch_position_key"

        /**
         * 球场位置名称
         */
        const val PITCH_POSITION_NAME_KEY = "pitch_position_name_key"

        /**
         * 球衣号码
         */
        const val UNIFORM_NO_KEY = "uniform_no_key"

        private val INSTANCE: PreferencesWrapper by lazy {
            PreferencesWrapper()
        }

        /**
         * 首页比赛 Banner弹窗
         * 首页训练 Banner弹窗
         * 首页赛事 Banner弹窗
         */
        const val BANNER_MATCH_DIALOG_KEY = "banner_match_dialog_key"
        const val BANNER_TRAIN_DIALOG_KEY = "banner_train_dialog_key"
        const val BANNER_LEAGUE_DIALOG_KEY = "banner_league_dialog_key"

        @JvmStatic
        fun get() = INSTANCE
    }

    /**
     * 设置首页比赛 Banner弹窗
     *
     * @param value
     */
    fun setBannerMatchDialog(value: Long) {
        PreferencesUtil.INSTANCE.encodeLong(BANNER_MATCH_DIALOG_KEY, value)
    }

    /**
     * 获取首页比赛 Banner弹窗
     *
     * @param value
     */
    fun getBannerMatchDialog(): Long {
        return PreferencesUtil.INSTANCE.decodeLong(BANNER_MATCH_DIALOG_KEY)
    }

    /**
     * 设置首页训练 Banner弹窗
     *
     * @param value
     */
    fun setBannerTrainDialog(value: Long) {
        PreferencesUtil.INSTANCE.encodeLong(BANNER_TRAIN_DIALOG_KEY, value)
    }

    /**
     * 获取首页比赛 Banner弹窗
     *
     * @param value
     */
    fun getBannerTrainDialog(): Long {
        return PreferencesUtil.INSTANCE.decodeLong(BANNER_TRAIN_DIALOG_KEY)
    }

    /**
     * 设置首页赛事 Banner弹窗
     *
     * @param value
     */
    fun setBannerLeagueDialog(value: Long) {
        PreferencesUtil.INSTANCE.encodeLong(BANNER_LEAGUE_DIALOG_KEY, value)
    }

    /**
     * 获取首页赛事 Banner弹窗
     *
     * @param value
     */
    fun getBannerLeagueDialog(): Long {
        return PreferencesUtil.INSTANCE.decodeLong(BANNER_LEAGUE_DIALOG_KEY)
    }

    /**
     * 设置多次使用
     *
     * @param value
     */
    fun setUseMultipleTimes(value: Boolean) {
        PreferencesUtil.INSTANCE.encodeBoolean(USE_MULTIPLE_TIMES_KEY, value)
    }

    /**
     * 获取多次使用
     *
     * @return
     */
    fun getUseMultipleTimes(): Boolean {
        return PreferencesUtil.INSTANCE.decodeBoolean(USE_MULTIPLE_TIMES_KEY)
    }

    /**
     * 设置使用的语言
     *
     * @param value
     */
    fun setUseLocal(value: String) {
        PreferencesUtil.INSTANCE.encodeString(USB_LOCAL_KEY, value)
    }

    /**
     * 获取设置的语言
     *
     * @return
     */
    fun getUseLocal(): String {
        return PreferencesUtil.INSTANCE.decodeString(USB_LOCAL_KEY)
    }


    /**
     * 设置访问 Token
     *
     * @param token
     */
    fun setAccessToken(token: String) {
        PreferencesUtil.INSTANCE.encodeString(ACCESSTOKEN_KEY, token)
    }

    /**
     * 获取访问地址
     *
     * @return
     */
    fun getAccessToken(): String {
        return PreferencesUtil.INSTANCE.decodeString(ACCESSTOKEN_KEY)
    }

    /**
     * 设置用户名
     *
     * @param username
     */
    fun setUsername(username: String) {
        PreferencesUtil.INSTANCE.encodeString(USERNAME_KEY, username)
    }

    /**
     * 获取用户名
     *
     * @return
     */
    fun getUsername(): String {
        return PreferencesUtil.INSTANCE.decodeString(USERNAME_KEY)
    }

    /**
     * 设置手机号码
     *
     * @param phone
     */
    fun setPhoneNumber(phone: String) {
        PreferencesUtil.INSTANCE.encodeString(PHONE_NUMBER_KEY, phone)
    }

    /**
     * 获取手机号码
     *
     * @return
     */
    fun getPhoneNumber(): String {
        return PreferencesUtil.INSTANCE.decodeString(PHONE_NUMBER_KEY)
    }

    /**
     * 设置角色
     *
     * @param role
     */
    fun setRole(role: String) {
        PreferencesUtil.INSTANCE.encodeString(ROLE_KEY, role)
    }

    /**
     * 获取角色
     *
     * @return
     */
    fun getRole(): String {
        return PreferencesUtil.INSTANCE.decodeString(ROLE_KEY)
    }

    /**
     * 设置组织名称
     *
     * @param organizationName
     */
    fun setOrganizationName(organizationName: String) {
        PreferencesUtil.INSTANCE.encodeString(ORGANIZATION_NAME_KEY, organizationName)
    }

    /**
     * 获取组织名称
     *
     * @return
     */
    fun getOrganizationName(): String {
        return PreferencesUtil.INSTANCE.decodeString(ORGANIZATION_NAME_KEY)
    }

    /**
     * 设置组织头像文件路径
     *
     * @param filePath
     */
    fun setOrganizationLogoImageFilePath(filePath: String) {
        PreferencesUtil.INSTANCE.encodeString(ORGANIZATION_LOGO_IMAGE_FILE_PATH_KEY, filePath)
    }

    /**
     * 获取组织头像文件路径
     *
     * @return
     */
    fun getOrganizationLogoImageFilePath(): String {
        return PreferencesUtil.INSTANCE.decodeString(ORGANIZATION_LOGO_IMAGE_FILE_PATH_KEY)
    }

    /**
     * 设置身高
     *
     * @param userHeight
     */
    fun setUserHeight(userHeight: Double) {
        PreferencesUtil.INSTANCE.encodeDouble(USER_HEIGHT_KEY, userHeight)
    }

    /**
     * 获取身高
     *
     * @return
     */
    fun getUserHeight(): Double {
        return PreferencesUtil.INSTANCE.decodeDouble(USER_HEIGHT_KEY)
    }

    /**
     * 设置体重
     *
     * @param userWeight
     */
    fun setUserWeight(userWeight: Double) {
        PreferencesUtil.INSTANCE.encodeDouble(USER_WEIGHT_KEY, userWeight)
    }

    /**
     * 获取体重
     *
     * @return
     */
    fun getUserWeight(): Double {
        return PreferencesUtil.INSTANCE.decodeDouble(USER_WEIGHT_KEY)
    }

    /**
     * 设置小组名称
     *
     * @param groupName
     */
    fun setGroupName(groupName: String) {
        PreferencesUtil.INSTANCE.encodeString(GROUP_NAME_KEY, groupName)
    }

    /**
     * 获取小组名称
     *
     * @return
     */
    fun getGroupName(): String {
        return PreferencesUtil.INSTANCE.decodeString(GROUP_NAME_KEY)
    }

    /**
     * 设置小组id
     *
     * @param id
     */
    fun setGroupId(id: String) {
        PreferencesUtil.INSTANCE.encodeString(GROUP_ID_KEY, id)
    }

    /**
     * 获取小组id
     *
     * @return
     */
    fun getGroupId(): String {
        return PreferencesUtil.INSTANCE.decodeString(GROUP_ID_KEY)
    }

    /**
     * 设置头像图片路径
     *
     * @param avatarImageFilePath
     */
    fun setAvatarImageFilePath(avatarImageFilePath: String) {
        PreferencesUtil.INSTANCE.encodeString(AVATAR_IMAGE_FILE_PATH_KEY, avatarImageFilePath)
    }

    /**
     * 获取头像图片路径
     *
     * @return
     */
    fun getAvatarImageFilePath(): String {
        return PreferencesUtil.INSTANCE.decodeString(AVATAR_IMAGE_FILE_PATH_KEY)
    }

    /**
     * 设置头像图片 id
     *
     * @param avatarImageFileId
     */
    fun setAvatarImageFileId(avatarImageFileId: String) {
        PreferencesUtil.INSTANCE.encodeString(AVATAR_IMAGE_FILE_ID_KEY, avatarImageFileId)
    }

    /**
     * 获取头像图片 id
     *
     * @return
     */
    fun getAvatarImageFileId(): String {
        return PreferencesUtil.INSTANCE.decodeString(AVATAR_IMAGE_FILE_ID_KEY)
    }

    /**
     * 设置主教练头像图片文件路径
     *
     * @param headCoachAvatarImageFilePath
     */
    fun setHeadCoachAvatarImageFilePath(headCoachAvatarImageFilePath: String) {
        PreferencesUtil.INSTANCE.encodeString(
            HEAD_COACH_AVATAR_IMAGE_FILE_PATH_KEY,
            headCoachAvatarImageFilePath
        )
    }

    /**
     * 获取主教练头像图片文件路径
     *
     * @return
     */
    fun getHeadCoachAvatarImageFilePath(): String {
        return PreferencesUtil.INSTANCE.decodeString(HEAD_COACH_AVATAR_IMAGE_FILE_PATH_KEY)
    }

    /**
     * 设置主教练姓名
     *
     * @param headCoachFrontUserName
     */
    fun setHeadCoachFrontUserName(headCoachFrontUserName: String) {
        PreferencesUtil.INSTANCE.encodeString(
            HEAD_COACH_FRONT_USER_NAME_KEY,
            headCoachFrontUserName
        )
    }

    /**
     * 获取主教练姓名
     *
     * @return
     */
    fun getHeadCoachFrontUserName(): String {
        return PreferencesUtil.INSTANCE.decodeString(HEAD_COACH_FRONT_USER_NAME_KEY)
    }

    /**
     * 设置 logo 图片文件路径
     *
     * @param logoImageFilePath
     */
    fun setLogoImageFilePath(logoImageFilePath: String) {
        PreferencesUtil.INSTANCE.encodeString(
            LOGO_IMAGE_FILE_PATH_KEY,
            logoImageFilePath
        )
    }

    /**
     * 获取 logo 图片文件路径
     *
     * @return
     */
    fun getLogoImageFilePath(): String {
        return PreferencesUtil.INSTANCE.decodeString(LOGO_IMAGE_FILE_PATH_KEY)
    }

    /**
     * 设置当前组织 id
     *
     * @param id
     */
    fun setCurrentOrganizationId(id: String) {
        PreferencesUtil.INSTANCE.encodeString(CURRENT_ORGANIZATION_ID_KEY, id)
    }

    /**
     * 获取当前组织 id
     *
     * @return
     */
    fun getCurrentOrganizationId(): String {
        return PreferencesUtil.INSTANCE.decodeString(CURRENT_ORGANIZATION_ID_KEY)
    }

    /**
     * 设置邀请人的 id
     *
     * @param id
     */
    fun setInviterFrontUserId(id: String) {
        PreferencesUtil.INSTANCE.encodeString(INVITER_FRONT_USER_ID_KEY, id)
    }

    /**
     * 获取邀请人的 id
     *
     * @return
     */
    fun getInviterFrontUserId(): String {
        return PreferencesUtil.INSTANCE.decodeString(INVITER_FRONT_USER_ID_KEY)
    }

    /**
     * 设置搜索列表的记录
     *
     * @param value
     */
    fun setCompetitionSearchList(value: String) {
        PreferencesUtil.INSTANCE.encodeString(COMPETITION_SEARCH_LIST_KEY, value)
    }

    /**
     * 获取搜索列表的记录
     *
     * @return
     */
    fun getCompetitionSearchList(): String {
        return PreferencesUtil.INSTANCE.decodeString(COMPETITION_SEARCH_LIST_KEY)
    }

    /**
     *
     *
     * @param id
     */
    fun setCurrentWatchVideoId(id: String) {
        PreferencesUtil.INSTANCE.encodeString(CURRENT_WATCH_VIDEO_ID, id)
    }

    /**
     *
     *
     * @return
     */
    fun getCurrentWatchVideoId(): String {
        return PreferencesUtil.INSTANCE.decodeString(CURRENT_WATCH_VIDEO_ID)
    }

    /**
     *
     *
     * @param isLive
     */
    fun setVideoPlayerIsLive(isLive: Boolean) {
        PreferencesUtil.INSTANCE.encodeBoolean(VIDEO_PLAYER_IS_LIVE, isLive)
    }

    /**
     *
     *
     * @return
     */
    fun getVideoPlayerIsLive(): Boolean {
        return PreferencesUtil.INSTANCE.decodeBoolean(VIDEO_PLAYER_IS_LIVE)
    }

    /**
     * 设置球场位置
     *
     * @param position
     */
    fun setPitchPosition(position: String) {
        PreferencesUtil.INSTANCE.encodeString(PITCH_POSITION_KEY, position)
    }

    /**
     * 获取球场位置
     *
     * @return
     */
    fun getPitchPosition(): String {
        return PreferencesUtil.INSTANCE.decodeString(PITCH_POSITION_KEY)
    }

    /**
     * 设置球场位置名称
     *
     * @param position
     */
    fun setPitchPositionName(position: String) {
        PreferencesUtil.INSTANCE.encodeString(PITCH_POSITION_NAME_KEY, position)
    }

    /**
     * 获取球场位置名称
     *
     * @return
     */
    fun getPitchPositionName(): String {
        return PreferencesUtil.INSTANCE.decodeString(PITCH_POSITION_NAME_KEY)
    }

    /**
     * 设置球员号码
     *
     * @param uniformNo
     */
    fun setUniformNo(uniformNo: Int) {
        PreferencesUtil.INSTANCE.encodeInt(UNIFORM_NO_KEY, uniformNo)
    }

    /**
     * 获取球衣号码
     *
     * @return
     */
    fun getUniformNo(): Int {
        return PreferencesUtil.INSTANCE.decodeInt(UNIFORM_NO_KEY)
    }
}