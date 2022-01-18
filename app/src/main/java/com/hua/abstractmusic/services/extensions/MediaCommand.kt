package com.hua.abstractmusic.services.extensions

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.se.omapi.Session
import android.util.Log
import androidx.media2.session.MediaSession
import androidx.media2.session.SessionCommand
import androidx.media2.session.SessionCommandGroup
import androidx.media2.session.SessionResult
import com.google.android.exoplayer2.ext.media2.SessionCallbackBuilder
import com.hua.abstractmusic.other.Constant.CLEAR_PLAY_LIST
import com.hua.abstractmusic.services.PlayerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @author : huaweikai
 * @Date   : 2022/01/18
 * @Desc   : command
 */
class MediaCommand(
    private val service: PlayerService,
    private val scope: CoroutineScope
): SessionCallbackBuilder.CustomCommandProvider{
    override fun onCustomCommand(
        session: MediaSession,
        controllerInfo: MediaSession.ControllerInfo,
        customCommand: SessionCommand,
        args: Bundle?
    ): SessionResult {
        val handler = object :Handler(Looper.getMainLooper()){

        }
        return when(customCommand.customAction){
            CLEAR_PLAY_LIST ->{
                handler.post {
                    service.removeAllMusic()
                }
                SessionResult(SessionResult.RESULT_SUCCESS,null)

            }
            else-> {SessionResult(SessionResult.RESULT_ERROR_NOT_SUPPORTED,null)}
        }
    }

    override fun getCustomCommands(
        session: MediaSession,
        controllerInfo: MediaSession.ControllerInfo
    ): SessionCommandGroup {
        return SessionCommandGroup(
            listOf(SessionCommand(CLEAR_PLAY_LIST,null))
        )
    }
}