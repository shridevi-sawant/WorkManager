package com.capgemini.workmanagerdemo

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay

class UploadWorker(appContext: Context, params: WorkerParameters)
    : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        // TODO Uploading task

        delay(10_000)
        return Result.success()
    }


}