package com.finshare.android.data.sync

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.work.*
import com.finshare.android.data.security.SecureStorageManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class SyncService : Service() {

    @Inject
    lateinit var secureStorageManager: SecureStorageManager

    @Inject
    lateinit var workManager: WorkManager

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_SYNC_NOW -> scheduleSyncWork()
            ACTION_ENABLE_AUTO_SYNC -> enableAutoSync()
            ACTION_DISABLE_AUTO_SYNC -> disableAutoSync()
        }
        return START_NOT_STICKY
    }

    private fun scheduleSyncWork() {
        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        workManager.enqueue(syncRequest)
    }

    private fun enableAutoSync() {
        val periodicSyncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            15, TimeUnit.MINUTES
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
            .build()

        workManager.enqueueUniquePeriodicWork(
            SYNC_WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            periodicSyncRequest
        )
    }

    private fun disableAutoSync() {
        workManager.cancelUniqueWork(SYNC_WORK_NAME)
    }

    companion object {
        const val ACTION_SYNC_NOW = "com.finshare.android.SYNC_NOW"
        const val ACTION_ENABLE_AUTO_SYNC = "com.finshare.android.ENABLE_AUTO_SYNC"
        const val ACTION_DISABLE_AUTO_SYNC = "com.finshare.android.DISABLE_AUTO_SYNC"
        private const val SYNC_WORK_NAME = "periodic_sync_work"
    }
}

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: android.content.Context,
    @Assisted workerParams: WorkerParameters,
    private val syncManager: DataSyncManager
) : CoroutineWorker(context, workerParams) {

    @AssistedFactory
    interface Factory {
        fun create(context: android.content.Context, params: WorkerParameters): SyncWorker
    }

    override suspend fun doWork(): Result {
        return try {
            syncManager.performFullSync()
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}