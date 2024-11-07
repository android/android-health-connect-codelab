package com.example.healthconnect.codelab.workers

import android.content.Context
import android.util.Log
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.coroutineScope
import java.time.LocalDateTime

/**
 *  Read total steps in last 24 hours
 */
class ReadStepWorker(context: Context, workerParams: WorkerParameters): CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result = coroutineScope {
        val context = applicationContext
        val healthConnectClient = HealthConnectClient.getOrCreate(context)

        val endTime = LocalDateTime.now()
        val startTime = endTime.minusHours(24)

        val request = AggregateRequest(
            metrics = setOf(StepsRecord.COUNT_TOTAL),
            timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
        )
        val response = healthConnectClient.aggregate(request)
        val stepsCount = (response[StepsRecord.COUNT_TOTAL]?: 0).toInt()

        Log.i("BackgroundReadWorker", "There are $stepsCount steps in Health Connect in the last 24 hours. ")

        return@coroutineScope Result.success()
    }
}