package com.capgemini.workmanagerdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.capgemini.workmanagerdemo.databinding.ActivityMainBinding
import java.util.Timer
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.downloadB.setOnClickListener {
            scheduleDownload()
        }

        binding.uploadB.setOnClickListener {
            scheduleUpload()
        }
    }

    private fun scheduleUpload() {

        val wConstraints = Constraints.Builder()
            // .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val uploadReq = PeriodicWorkRequestBuilder<UploadWorker>(20,
                TimeUnit.MINUTES)
            .setConstraints(wConstraints)
            .build()

        val wManager = WorkManager.getInstance(this)
        wManager.enqueue(uploadReq)
        binding.statusT.text = "Upload is scheduled"

        wManager.getWorkInfoByIdLiveData(uploadReq.id).observe(this){
            val status = when(it.state){
                WorkInfo.State.ENQUEUED -> "ENQUEUED"
                WorkInfo.State.RUNNING -> "RUNNING"
                WorkInfo.State.SUCCEEDED -> "SUCCEEDED"
                WorkInfo.State.FAILED -> "FAILED"
                WorkInfo.State.BLOCKED -> "BLOCKED"
                WorkInfo.State.CANCELLED -> "CANCELLED"
            }
            binding.statusT.append("\n$status")
        }

        Timer().schedule(10_000){
            wManager.cancelWorkById(uploadReq.id)
        }


    }

    private fun scheduleDownload() {
        binding.statusT.text = "Download is scheduled"

        // create the download work request
        val wConstraints = Constraints.Builder()
           // .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val inpData = Data.Builder()
            .putString("url", "http://google.com")
            .build()

        val downloadReq = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setConstraints(wConstraints)
            .setInputData(inpData)
            .build()

        val wManager = WorkManager.getInstance(this)
        wManager.enqueue(downloadReq)

        // work chaining
//        wManager.beginWith(downloadReq)
//            .then(uploadReq)
//            .then(cacheReq)
//            .enqueue()

        wManager.getWorkInfoByIdLiveData(downloadReq.id)
            .observe(this){

                val status = when(it.state){
                    WorkInfo.State.ENQUEUED -> "ENQUEUED"
                    WorkInfo.State.RUNNING -> "RUNNING"
                    WorkInfo.State.SUCCEEDED -> {
                        val time = it.outputData.getString("finish_time")
                        "SUCCEEDED at $time"
                    }
                    WorkInfo.State.FAILED -> "FAILED"
                    WorkInfo.State.BLOCKED -> "BLOCKED"
                    WorkInfo.State.CANCELLED -> "CANCELLED"
                }

                binding.statusT.append("\n$status")
        }


    }
}