package com.wei.vsclock.feature.times.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.wei.vsclock.core.data.repository.RefreshStateRepository
import com.wei.vsclock.core.data.repository.TimeRepository
import com.wei.vsclock.core.model.data.CurrentTime
import com.wei.vsclock.feature.times.lifecycle.MyLifecycleOwner
import com.wei.vsclock.feature.times.service.ui.FloatingTimeCard
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class FloatingTimeService : Service() {
    private lateinit var windowManager: WindowManager
    private lateinit var composeView: ComposeView

    // 用於 Service 全域操作的協程作用域
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // 用於 API 請求的協程作用域，每次刷新前會重新建立
    private var groupScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // 用於排程下一次刷新
    private var refreshJob: Job? = null

    private val currentTimeFlow = MutableStateFlow<CurrentTime?>(null)

    @Inject
    lateinit var refreshStateRepository: RefreshStateRepository

    @Inject
    lateinit var timeRepository: TimeRepository

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        startForegroundService()
        observeLastRefreshTimeState()
        showOverlay()
    }

    /**
     * 根據上次刷新時間訂閱計算下一次刷新時間，初次進入（lastRefreshTime == 0）則等待完整刷新間隔
     */
    private fun observeLastRefreshTimeState() {
        serviceScope.launch {
            refreshStateRepository.lastRefreshTime.collect { lastRefreshTime ->
                val currentTimeMillis = System.currentTimeMillis()
                val refreshRateMillis = TimeUnit.SECONDS.toMillis(
                    refreshStateRepository.refreshRate.first().second,
                )
                // 若尚未刷新過 (lastRefreshTime 為 0) 則延遲整個刷新間隔，
                // 否則計算剩餘的延遲時間，最少 0 毫秒
                val delayMillis = if (lastRefreshTime <= 0L) {
                    refreshRateMillis
                } else {
                    (refreshRateMillis - (currentTimeMillis - lastRefreshTime)).coerceAtLeast(0L)
                }
                Timber.d("Scheduling next refresh in $delayMillis ms, lastRefreshTime: $lastRefreshTime")
                refreshJob?.cancel()
                refreshJob = serviceScope.launch {
                    delay(delayMillis)
                    refreshCurrentTime()
                }
            }
        }
    }

    /**
     * 載入單筆時間資料：
     * 1. 取消舊的 API 請求作用域並建立新的 groupScope
     * 2. 更新刷新時間，排程下一次刷新
     * 3. 呼叫刷新 API
     */
    /**
     * 載入單筆時間資料：
     * 1. 取消舊的 API 請求作用域並建立新的 groupScope
     * 2. 取得當前 currentTimeFlow 的 timeZone，若為 null 則直接 return
     * 3. 更新刷新時間到 repository，並呼叫刷新 API
     */
    private fun refreshCurrentTime() {
        val currentTimeMillis = System.currentTimeMillis()
        Timber.d(
            "FloatingTimeService refreshCurrentTime() ${
                Instant.fromEpochMilliseconds(
                    currentTimeMillis,
                )
            }",
        )
        // 取消之前的請求作用域
        groupScope.cancel()
        groupScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        groupScope.launch {
            val timeZone = currentTimeFlow.value?.timeZone ?: return@launch
            // 更新刷新時間到 repository
            refreshStateRepository.updateLastRefreshTime(currentTimeMillis)
            // 呼叫 API 並取得最新資料
            timeRepository.refreshCurrentTime(timeZone = timeZone)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    /**
     * 顯示 Compose Overlay
     */
    private fun showOverlay() {
        val layoutFlag = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT,
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100
            y = 100
        }
        // 觀察 repository 中的 currentTime 並更新 currentTimeFlow
        serviceScope.launch {
            val currentFloatingTime = refreshStateRepository.currentFloatingTime.first()
            timeRepository.getCurrentTime(currentFloatingTime).collect { currentTime ->
                currentTimeFlow.value = currentTime
            }
        }

        composeView = ComposeView(this).apply {
            setContent {
                val currentTime by currentTimeFlow.collectAsState()
                FloatingTimeCard(
                    currentTime = currentTime,
                    onClick = {
                        val packageName = applicationContext.packageName
                        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
                        if (launchIntent != null) {
                            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(launchIntent)
                            stopSelf()
                        } else {
                            Timber.e("FloatingTimeService: Can't find launchIntent")
                        }
                    },
                    onDrag = { deltaX, deltaY ->
                        params.x += deltaX.toInt()
                        params.y += deltaY.toInt()
                        windowManager.updateViewLayout(composeView, params)
                    },
                )
            }
        }

        val lifecycleOwner = MyLifecycleOwner()
        lifecycleOwner.performRestore(null)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        composeView.setViewTreeLifecycleOwner(lifecycleOwner)
        composeView.setViewTreeSavedStateRegistryOwner(lifecycleOwner)

        windowManager.addView(composeView, params)
    }

    /**
     * 啟動前景服務，防止系統殺死
     */
    private fun startForegroundService() {
        val channelId = "floating_time_channel"
        val channelName = "Floating Time Service"

        val channel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_MIN)
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("VsClock 前台服務")
            .setContentText("點擊時鐘返回應用")
            .setOngoing(true)
            .build()

        startForeground(1, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        groupScope.cancel()
        refreshJob?.cancel()
        windowManager.removeView(composeView)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
