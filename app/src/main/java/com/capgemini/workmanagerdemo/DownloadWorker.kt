package com.capgemini.workmanagerdemo

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.Calendar

class DownloadWorker(ctx: Context, workerParams: WorkerParameters)
    : Worker(ctx, workerParams) {

    // executed on background thread
    override fun doWork(): Result {
        // read input data
        val downloadUrl = inputData.getString("url")
        // TODO -Download
        Log.d("DownloadWorker", "work started $downloadUrl")
        Thread.sleep(10_000)
        Log.d("DownloadWorker", "work finished")

        val outputData = Data.Builder()
            .putString("finish_time", Calendar.getInstance().time.toString())
            .build()

        return Result.success(outputData)
    }

    // executed when signalled to stop the work
    override fun onStopped() {
        super.onStopped()
    }

}