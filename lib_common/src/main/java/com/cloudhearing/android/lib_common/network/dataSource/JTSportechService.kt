package com.cloudhearing.android.lib_common.network.dataSource

import com.cloudhearing.android.lib_common.network.dataSource.home.competition.Competition
import com.cloudhearing.android.lib_common.network.dataSource.home.competition.CompetitionList
import com.cloudhearing.android.lib_common.network.dataSource.homebanner.HomeBanner
import com.cloudhearing.android.lib_common.network.dataSource.login.OpenLogin
import com.cloudhearing.android.lib_common.network.dataSource.messages.MessageNotice
import com.cloudhearing.android.lib_common.network.dataSource.messages.MessagesEntity
import com.cloudhearing.android.lib_common.network.dataSource.messages.Version
import com.cloudhearing.android.lib_common.network.dataSource.mine.Team
import com.cloudhearing.android.lib_common.network.dataSource.mine.TeamMembers
import com.cloudhearing.android.lib_common.network.dataSource.mine.User
import com.cloudhearing.android.lib_common.network.dataSource.mine.UserInfo
import com.cloudhearing.android.lib_common.network.dataSource.player.PlayerEntity
import com.cloudhearing.android.lib_common.network.dataSource.player.VideoPlayUrlEntity
import com.cloudhearing.android.lib_common.network.dataSource.player.playerevents.PlayerEventsEntity
import com.cloudhearing.android.lib_common.network.dataSource.racedetail.RaceDetailEntity
import com.cloudhearing.android.lib_common.network.dataSource.welcome.Welcome
import com.cloudhearing.android.lib_network.utils.ApiResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

/**
 * Author: BenChen
 * Date: 2023/05/24 10:38
 * Email:chenxiaobin@cloudhearing.cn
 */

/**
 * [匠体接口文档](https://test.sztcpay.com:10443/jtsport-front-web/doc.html#/home)
 *
 */
interface JTSportechService {

    @FormUrlEncoded
    @POST("/jtsport-front-web/auth/passwdlogin")
    suspend fun accountLogin(
        @Field("username") username: String,
        @Field("password") password: String
    ): ApiResponse<String>

    @FormUrlEncoded
    @POST("/jtsport-front-web/auth/smslogin-sendcode")
    suspend fun smsLoginSendCode(@Field("phoneNoEnc") phoneNoEnc: String): ApiResponse<String>

    @FormUrlEncoded
    @POST("/jtsport-front-web/auth/smslogin")
    suspend fun smsLogin(
        @Field("inviteCode") inviteCode: String,
        @Field("smsToken") smsToken: String,
        @Field("validCode") validCode: String
    ): ApiResponse<String>

    @FormUrlEncoded
    @POST("/jtsport-front-web/auth/open-login")
    suspend fun openLogin(
        @Field("openId") openId: String,
        @Field("openType") openType: String
    ): ApiResponse<String>

    @FormUrlEncoded
    @Headers("X-Auth-Token:true")
    @POST("/jtsport-front-web/user/open-bind")
    suspend fun openBind(
        @Field("openId") openId: String,
        @Field("openType") openType: String
    ): ApiResponse<String>


    @Headers("X-Auth-Token:true")
    @DELETE("/jtsport-front-web/user/open-unbind")
    suspend fun openUnbind(
        @Query("openId") openId: String,
        @Query("openType") openType: String
    ): ApiResponse<String>

    @FormUrlEncoded
    @Headers("X-Auth-Token:true")
    @POST("/jtsport-front-web/auth/mobile-login")
    suspend fun mobileLogin(
        @Field("umToken") umToken: String,
        @Field("inviteCode") inviteCode: String
    ): ApiResponse<String>

    @Headers("X-Auth-Token:true", "is-Data-Definitions:true")
    @GET("/jtsport-front-web/user/open-bind/list")
    suspend fun openBindList(): ApiResponse<List<OpenLogin>>

    @Headers("X-Auth-Token:true", "is-Data-Definitions:true")
    @GET("/jtsport-front-web/auth/user")
    suspend fun getUserInfo(): ApiResponse<UserInfo>

    @Headers("X-Auth-Token:true", "is-Data-Definitions:true")
    @GET("/jtsport-front-web/user/{id}")
    suspend fun getUserInfo(@Path("id") id: String): ApiResponse<User>

    @Headers("X-Auth-Token:true", "is-Data-Definitions:true")
    @GET("/jtsport-front-web/match/home/list")
    suspend fun getCompetitionList(
        @Query("matchType") matchType: String,
        @Query("pageNum") pageNum: Int,
        @Query("pageSize") pageSize: Int,
    ): ApiResponse<CompetitionList>

//    @Headers("X-Auth-Token:true", "is-Data-Definitions:true")
//    @GET("/jtsport-front-web/match/search/list")
//    suspend fun getSearchList(
//        @Query("durationFrom") durationFrom: Long,
//        @Query("durationTo") durationTo: Long,
//        @Query("leagueId") leagueId: String,
//        @Query("matchDate") matchDate: String,
//        @Query("matchType") matchType: String,
//        @Query("name") name: String,
//        @Query("pageNum") pageNum: Int,
//        @Query("pageSize") pageSize: Int,
//        @Query("playerFrontUserId") playerFrontUserId: String,
//        @Query("publishEndTime") publishEndTime: String,
//        @Query("publishStartTime") publishStartTime: String,
//        @Query("subType") subType: String
//    ): ApiResponse<Row<Search>>

    @Headers("X-Auth-Token:true", "is-Data-Definitions:true")
    @GET("/jtsport-front-web/match/search/list")
    suspend fun getSearchList(@QueryMap param: Map<String, @JvmSuppressWildcards Any>): ApiResponse<CompetitionList>

    @Headers("X-Auth-Token:true", "is-Data-Definitions:true")
    @GET("/jtsport-front-web/match/search/list")
    suspend fun getLeagueHomeList(
        @Query("matchDate") matchDate: String,
        @Query("matchType") matchType: String,
        @Query("pageNum") pageNum: Int,
        @Query("pageSize") pageSize: Int,
        @Query("name") name: String,
        @Query("leagueId") leagueId: String,
    ): ApiResponse<CompetitionList>

    @Headers("X-Auth-Token:true", "is-Data-Definitions:true")
    @GET("/jtsport-front-web/events/home-banner")
    suspend fun getBannerList(@Query("eventsType") eventsType: String): ApiResponse<HomeBanner>

    @Headers("X-Auth-Token:true", "is-Data-Definitions:true")
    @GET("/jtsport-front-web/events/home-dialog")
    suspend fun getHomeDialog(): ApiResponse<HomeBanner>

    @Headers("X-Auth-Token:true", "is-Data-Definitions:true")
    @GET("/jtsport-front-web/league/list")
    suspend fun getLeagueList(): ApiResponse<List<Competition>>

    @Headers("X-Auth-Token:true")
    @GET("/jtsport-front-web/match/booking/{matchInfoId}")
    suspend fun sendMatchBooking(@Path("matchInfoId") matchInfoId: String): ApiResponse<Unit>

    @Headers("X-Auth-Token:true")
    @GET("/jtsport-front-web/auth/invitecode")
    suspend fun getInvitecode(): ApiResponse<String>


    @Headers("X-Auth-Token:true")
    @POST("/jtsport-front-web/user/editpasswd-sendcode")
    suspend fun changePasswordToSendcode(): ApiResponse<String>

    @FormUrlEncoded
    @Headers("X-Auth-Token:true")
    @POST("/jtsport-front-web/user/editpasswd-checkcode")
    suspend fun changePasswordStepOne(
        @Field("smsToken") smsToken: String,
        @Field("validCode") validCode: String
    ): ApiResponse<String>

    @FormUrlEncoded
    @Headers("X-Auth-Token:true")
    @POST("/jtsport-front-web/user/editpasswd-finish")
    suspend fun changePasswordStepTwo(
        @Field("checkToken") checkToken: String,
        @Field("oldPasswd") oldPasswd: String,
        @Field("newPasswd") newPasswd: String,
        @Field("confirmPasswd") confirmPasswd: String
    ): ApiResponse<Unit>

    @Headers("X-Auth-Token:true")
    @PUT("/jtsport-front-web/auth/cancell")
    suspend fun cancellAccount(): ApiResponse<Unit>

    @Headers("X-Auth-Token:true")
    @POST("/jtsport-front-web/file/image")
    suspend fun uploadImage(@Body multipartBody: RequestBody): ApiResponse<String>

    @FormUrlEncoded
    @Headers("X-Auth-Token:true")
    @PUT("/jtsport-front-web/auth/modify")
    suspend fun modifyUserInfo(
        @Field("avatarImageFileId") avatarImageFileId: String
    ): ApiResponse<Unit>


    @Headers("X-Auth-Token:true")
    @POST("/jtsport-front-web/user/editphone-oldsendcode")
    suspend fun changePhoneNumberToStepOne(): ApiResponse<String>

    @FormUrlEncoded
    @Headers("X-Auth-Token:true")
    @POST("/jtsport-front-web/user/editphone-oldcheckvalidcode")
    suspend fun changePhoneNumberToStepTwo(
        @Field("smsToken") smsToken: String,
        @Field("validCode") validCode: String
    ): ApiResponse<String>

    @FormUrlEncoded
    @Headers("X-Auth-Token:true")
    @POST("/jtsport-front-web/user/editphone-newsendcode")
    suspend fun changePhoneNumberToStepThree(
        @Field("checkToken") checkToken: String,
        @Field("phoneNoEnc") phoneNoEnc: String
    ): ApiResponse<String>

    @FormUrlEncoded
    @Headers("X-Auth-Token:true")
    @POST("/jtsport-front-web/user/editphone-finish")
    suspend fun changePhoneNumberToStepFour(
        @Field("smsToken") smsToken: String,
        @Field("validCode") validCode: String
    ): ApiResponse<Unit>

    @Headers("X-Auth-Token:true", "is-Data-Definitions:true")
    @GET("/jtsport-front-web/user/my-orguser-list")
    suspend fun getTeamMembers(): ApiResponse<List<TeamMembers>>

    @Headers("X-Auth-Token:true", "is-Data-Definitions:true")
    @GET("/jtsport-front-web/user/my-org-list")
    suspend fun getTeam(): ApiResponse<List<Team>>

    @FormUrlEncoded
    @Headers("X-Auth-Token:true")
    @POST("/jtsport-front-web/auth/change-organization")
    suspend fun changeOrganization(@Field("organizationId") organizationId: String,@Field("groupId") groupId: String): ApiResponse<String>

    @Headers("X-Auth-Token:true", "is-Data-Definitions:true")
    @GET("/jtsport-front-web/match/watch-his/list-page")
    suspend fun getWatchHistory(
        @Query("matchType") matchType: String,
        @Query("pageNum") pageNum: Int,
        @Query("pageSize") pageSize: Int,
    ): ApiResponse<CompetitionList>

    @Headers("X-Auth-Token:true", "is-Data-Definitions:true")
    @GET("/jtsport-front-web/match/list-favorite-page")
    suspend fun getFavorite(
        @Query("matchType") matchType: String,
        @Query("pageNum") pageNum: Int,
        @Query("pageSize") pageSize: Int,
    ): ApiResponse<CompetitionList>

    @Headers("X-Auth-Token:true")
    @DELETE("/jtsport-front-web/match/favorite")
    suspend fun deleteFavorite(
        @Query("favoriteIds") favoriteIds: String
    ): ApiResponse<Unit>

    @Headers("X-Auth-Token:true", "is-Data-Definitions:true")
    @GET("/jtsport-front-web/match/{matchInfoId}")
    suspend fun getRaceDetail(
        @Path("matchInfoId") matchInfoId: String
    ): ApiResponse<RaceDetailEntity>

    @Headers("X-Auth-Token:true", "is-Data-Definitions:true")
    @GET("/jtsport-front-web/match/list-player-video")
    suspend fun getPlayerVideo(
        @Query("frontUserId") frontUserId: String,
    ): ApiResponse<PlayerEntity>

    @Headers("X-Auth-Token:true", "is-Data-Definitions:true")
    @GET("/jtsport-front-web/match/list-player-events")
    suspend fun getPlayerEvents(
        @Query("frontUserId") frontUserId: String,
        @Query("matchType") matchType: String,
    ): ApiResponse<PlayerEventsEntity>

    @Headers("X-Auth-Token:true", "is-Data-Definitions:true")
    @GET("/jtsport-front-web/match/video-play-url")
    suspend fun getVideoPlayUrl(
        @Query("videoSourceId") videoSourceId: String,
        @Query("videoSourceType") videoSourceType: String,
    ): ApiResponse<VideoPlayUrlEntity>

    @Headers("X-Auth-Token:true", "is-Data-Definitions:true")
    @GET("/jtsport-front-web/criticize/list/{matchInfoId}")
    suspend fun getMessages(
        @Path("matchInfoId") matchInfoId: String,
        @Query("sortType") sortType: Int,
    ): ApiResponse<MessagesEntity>

    @Headers("X-Auth-Token:true", "is-Data-Definitions:true")
    @POST("/jtsport-front-web/criticize")
    suspend fun getCriticize(
        @Query("audioDuration") audioDuration: Int,
        @Query("audioFileId") audioFileId: String,
        @Query("contentText") contentText: String,
        @Query("contentType") contentType: String,
        @Query("criticizeType") criticizeType: String,
        @Query("matchInfoId") matchInfoId: String,
        @Query("replyCriticizeId") replyCriticizeId: String,
    ): ApiResponse<VideoPlayUrlEntity>

    @Headers("X-Auth-Token:true")
    @POST("/jtsport-front-web/file/audio")
    suspend fun getAudio(@Body multipartBody: RequestBody): ApiResponse<String>

    @Headers("X-Auth-Token:true", "is-Data-Definitions:true")
    @GET("/jtsport-front-web/match/video-download-url")
    suspend fun getVideoDownLoadUrl(
        @Query("videoSourceId") videoSourceId: String,
        @Query("videoSourceType") videoSourceType: String,
    ): ApiResponse<VideoPlayUrlEntity>

    @Headers("X-Auth-Token:true", "is-Data-Definitions:true")
    @POST("/jtsport-front-web/match/favorite/{matchInfoId}")
    suspend fun getAddFavorite(
        @Query("eventName") eventName: String,
        @Query("favoriteType") favoriteType: String,
        @Query("matchInfoId") matchInfoId: String,
    ): ApiResponse<VideoPlayUrlEntity>

    @Headers("X-Auth-Token:true", "is-Data-Definitions:true")
    @DELETE("/jtsport-front-web/match/favorite")
    suspend fun getCancelFavorite(
        @Query("favoriteIds") favoriteIds: String,
    ): ApiResponse<VideoPlayUrlEntity>

    @Headers("X-Auth-Token:true", "is-Data-Definitions:true")
    @POST("/jtsport-front-web/match/evaluate/{matchEventId}")
    suspend fun getEvaluate(
        @Path("matchEventId") matchEventId: String,
        @Query("evaluateResult") evaluateResult: String,
    ): ApiResponse<VideoPlayUrlEntity>

    @Headers("X-Auth-Token:true", "is-Data-Definitions:true")
    @POST("/jtsport-front-web/match/watch-his/{matchInfoId}")
    suspend fun addWatchRecord(
        @Path("matchInfoId") matchInfoId: String,
    ): ApiResponse<VideoPlayUrlEntity>

    @Headers("X-Auth-Token:true", "is-Data-Definitions:true")
    @GET("/jtsport-front-web/msg-notice/list")
    suspend fun getMessageNotice(
        @Query("keyword") keyword: String,
        @Query("msgType") msgType: String,
    ): ApiResponse<List<MessageNotice>>

    @Headers("X-Auth-Token:true", "is-Data-Definitions:true")
    @GET("/jtsport-front-web/app/version")
    suspend fun getAppVersion(): ApiResponse<Version>

    @FormUrlEncoded
    @Headers("X-Auth-Token:true", "is-Data-Definitions:true")
    @PUT("/jtsport-front-web/msg-notice")
    suspend fun updateMsgNotice(
        @Field("ids") ids: String,
        @Field("msgStatus") msgStatus: String,
        @Field("msgType") msgType: String,
    ): ApiResponse<Unit>

    @FormUrlEncoded
    @Headers("X-Auth-Token:true", "is-Data-Definitions:true")
    @PUT("/jtsport-front-web/auth/alter-qrcode")
    suspend fun alterQrcode(
        @Field("id") id: String,
        @Field("loginStatus") loginStatus: String,
    ): ApiResponse<Unit>

    @Headers("X-Auth-Token:true", "is-Data-Definitions:true")
    @GET("/jtsport-front-web/user/group-member-list/{groupId}")
    suspend fun getGroupMemberList(@Path("groupId") groupId: String): ApiResponse<List<TeamMembers>>

    @Headers("X-Auth-Token:true", "is-Data-Definitions:true")
    @GET("/jtsport-front-web/app/welcome-img")
    suspend fun getWelcomeImg(): ApiResponse<Welcome>

    @Headers("X-Auth-Token:true", "is-Data-Definitions:true")
    @GET("/jtsport-front-web/events/home-banner")
    suspend fun getHomeBanner(): ApiResponse<HomeBanner>


}