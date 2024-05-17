package com.cloudhearing.android.lib_common.network.repository

import com.cloudhearing.android.lib_common.network.dataSource.home.competition.Competition
import com.cloudhearing.android.lib_common.network.dataSource.home.competition.CompetitionList
import com.cloudhearing.android.lib_common.network.dataSource.homebanner.HomeBanner
import com.cloudhearing.android.lib_common.network.dataSource.messages.MessagesEntity
import com.cloudhearing.android.lib_common.network.dataSource.player.PlayerEntity
import com.cloudhearing.android.lib_common.network.dataSource.player.VideoPlayUrlEntity
import com.cloudhearing.android.lib_common.network.dataSource.player.playerevents.PlayerEventsEntity
import com.cloudhearing.android.lib_common.network.dataSource.racedetail.RaceDetailEntity
import com.cloudhearing.android.lib_common.network.dataSource.welcome.Welcome
import com.cloudhearing.android.lib_network.utils.ApiResponse

/**
 * Author: BenChen
 * Date: 2024/01/11 17:24
 * Email:chenxiaobin@cloudhearing.cn
 */
class CompetitionRepository : BaseRepository() {

    suspend fun getTrainBannerList(): ApiResponse<HomeBanner> {
        return executeHttp {
            mJTSportechService.getBannerList("TRAIN")
        }
    }

    suspend fun getMatchBannerList(): ApiResponse<HomeBanner> {
        return executeHttp {
            mJTSportechService.getBannerList("MATCH")
        }
    }

    suspend fun getLeagueBannerList(): ApiResponse<HomeBanner> {
        return executeHttp {
            mJTSportechService.getBannerList("LEAGUE")
        }
    }

    suspend fun getMatchHomeList(pageNum: Int, pageSize: Int): ApiResponse<CompetitionList> {
        return executeHttp {
            mJTSportechService.getCompetitionList("MATCH", pageNum, pageSize)
        }
    }

    suspend fun getTrainHomeList(pageNum: Int, pageSize: Int): ApiResponse<CompetitionList> {
        return executeHttp {
            mJTSportechService.getCompetitionList("TRAIN", pageNum, pageSize)
        }
    }

    suspend fun getLeagueHomeList(
        matchDate: String, matchType: String, pageNum: Int, pageSize: Int,
        name: String, leagueId: String
    ): ApiResponse<CompetitionList> {
        return executeHttp {
            mJTSportechService.getLeagueHomeList(
                matchDate = matchDate,
                matchType = matchType,
                pageNum = pageNum,
                pageSize = pageSize,
                name = name,
                leagueId = leagueId
            )
        }
    }

    suspend fun getSearchList(
        durationFrom: Long = -1L,
        durationTo: Long = -1L,
        leagueId: String = "",
        matchDate: String = "",
        matchType: String = "",
        name: String = "",
        pageNum: Int = 1,
        pageSize: Int = 1000,
        playerFrontUserId: String = "",
        publishEndTime: String = "",
        publishStartTime: String = "",
        subType: String = "",
    ): ApiResponse<CompetitionList> {
        val params = hashMapOf<String, Any>()

        if (durationFrom != -1L) {
            params["durationFrom"] = durationFrom
        }

        if (durationTo != -1L) {
            params["durationTo"] = durationTo
        }

        if (leagueId.isNotEmpty()) {
            params["leagueId"] = leagueId
        }

        if (matchDate.isNotEmpty()) {
            params["matchDate"] = matchDate
        }

        if (matchType.isNotEmpty()) {
            params["matchType"] = matchType
        }

        if (name.isNotEmpty()) {
            params["name"] = name
        }

        if (pageNum != -1) {
            params["pageNum"] = pageNum
        }

        if (pageSize != -1) {
            params["pageSize"] = pageSize
        }

        if (playerFrontUserId.isNotEmpty()) {
            params["playerFrontUserId"] = playerFrontUserId
        }

        if (publishEndTime.isNotEmpty()) {
            params["publishEndTime"] = publishEndTime
        }

        if (publishStartTime.isNotEmpty()) {
            params["publishStartTime"] = publishStartTime
        }

        if (subType.isNotEmpty()) {
            params["subType"] = subType
        }

        return executeHttp {
            mJTSportechService.getSearchList(params)
        }
    }

    suspend fun getHomeDialog(): ApiResponse<HomeBanner> {
        return executeHttp {
            mJTSportechService.getHomeDialog()
        }
    }

    suspend fun getLeagueList(): ApiResponse<List<Competition>> {
        return executeHttp {
            mJTSportechService.getLeagueList()
        }
    }

    suspend fun sendMatchBooking(matchInfoId: String): ApiResponse<Unit> {
        return executeHttp {
            mJTSportechService.sendMatchBooking(matchInfoId)
        }
    }

    suspend fun getTrainWatchHistory(pageNum: Int, pageSize: Int): ApiResponse<CompetitionList> {
        return executeHttp {
            mJTSportechService.getWatchHistory("TRAIN", pageNum, pageSize)
        }
    }

    suspend fun getMatchWatchHistory(pageNum: Int, pageSize: Int): ApiResponse<CompetitionList> {
        return executeHttp {
            mJTSportechService.getWatchHistory("MATCH", pageNum, pageSize)
        }
    }

    suspend fun getLeagueWatchHistory(pageNum: Int, pageSize: Int): ApiResponse<CompetitionList> {
        return executeHttp {
            mJTSportechService.getWatchHistory("LEAGUE", pageNum, pageSize)
        }
    }

    suspend fun getTrainFavorite(pageNum: Int, pageSize: Int): ApiResponse<CompetitionList> {
        return executeHttp {
            mJTSportechService.getFavorite("TRAIN", pageNum, pageSize)
        }
    }

    suspend fun getMatchFavorite(pageNum: Int, pageSize: Int): ApiResponse<CompetitionList> {
        return executeHttp {
            mJTSportechService.getFavorite("MATCH", pageNum, pageSize)
        }
    }

    suspend fun getLeagueFavorite(pageNum: Int, pageSize: Int): ApiResponse<CompetitionList> {
        return executeHttp {
            mJTSportechService.getFavorite("LEAGUE", pageNum, pageSize)
        }
    }

    suspend fun deleteFavorite(favoriteIds: String): ApiResponse<Unit> {
        return executeHttp {
            mJTSportechService.deleteFavorite(favoriteIds)
        }
    }

    suspend fun getRaceDetail(matchInfoId: String): ApiResponse<RaceDetailEntity> {
        return executeHttp {
            mJTSportechService.getRaceDetail(matchInfoId)
        }
    }

    suspend fun getPlayerVideo(frontUserId: String): ApiResponse<PlayerEntity> {
        return executeHttp {
            mJTSportechService.getPlayerVideo(frontUserId)
        }
    }

    suspend fun getPlayerEvents(
        frontUserId: String,
        matchType: String
    ): ApiResponse<PlayerEventsEntity> {
        return executeHttp {
            mJTSportechService.getPlayerEvents(frontUserId, matchType)
        }
    }

    suspend fun getVideoPlayUrl(
        videoSourceId: String,
        videoSourceType: String
    ): ApiResponse<VideoPlayUrlEntity> {
        return executeHttp {
            mJTSportechService.getVideoPlayUrl(videoSourceId, videoSourceType)
        }
    }

    suspend fun getMessages(matchInfoId: String, sortType: Int): ApiResponse<MessagesEntity> {
        return executeHttp {
            mJTSportechService.getMessages(matchInfoId, sortType)
        }
    }


    suspend fun getVideoDownLoadUrl(
        videoSourceId: String,
        videoSourceType: String
    ): ApiResponse<VideoPlayUrlEntity> {
        return executeHttp {
            mJTSportechService.getVideoDownLoadUrl(videoSourceId, videoSourceType)
        }
    }

    suspend fun getAddFavorite(
        eventName: String,
        favoriteType: String,
        matchInfoId: String
    ): ApiResponse<VideoPlayUrlEntity> {
        return executeHttp {
            mJTSportechService.getAddFavorite(eventName, favoriteType, matchInfoId)
        }
    }

    suspend fun getCancelFavorite(
        favoriteIds: String
    ): ApiResponse<VideoPlayUrlEntity> {
        return executeHttp {
            mJTSportechService.getCancelFavorite(favoriteIds)
        }
    }

    suspend fun getCriticize(
        audioDuration: Int,
        audioFileId: String,
        contentText: String,
        contentType: String,
        criticizeType: String,
        matchInfoId: String,
        replyCriticizeId: String
    ): ApiResponse<VideoPlayUrlEntity> {
        return executeHttp {
            mJTSportechService.getCriticize(
                audioDuration,
                audioFileId,
                contentText,
                contentType,
                criticizeType,
                matchInfoId,
                replyCriticizeId
            )
        }
    }


    suspend fun getEvaluate(
        matchEventId: String,
        evaluateResult: String
    ): ApiResponse<VideoPlayUrlEntity> {
        return executeHttp {
            mJTSportechService.getEvaluate(
                matchEventId,
                evaluateResult
            )
        }
    }

    suspend fun addWatchRecord(
        matchInfoId: String
    ): ApiResponse<VideoPlayUrlEntity> {
        return executeHttp {
            mJTSportechService.addWatchRecord(
                matchInfoId
            )
        }
    }

    suspend fun getWelcomeImg(
    ): ApiResponse<Welcome> {
        return executeHttp {
            mJTSportechService.getWelcomeImg()
        }
    }

    suspend fun getHomeBanner(
    ): ApiResponse<HomeBanner> {
        return executeHttp {
            mJTSportechService.getHomeBanner()
        }
    }

}