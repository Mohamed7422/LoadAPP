package com.udacity

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private lateinit var notificationManager: NotificationManager

    lateinit var loadingButton: LoadingButton
    lateinit var radioGroup: RadioGroup
    lateinit var radioButton: RadioButton
    private var completed = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        loadingButton = findViewById(R.id.custom_button)
        radioGroup = findViewById(R.id.radio_Container)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        loadingButton.setOnClickListener {

            //check first if no button selected
            //and then if one is selected then download
            if (radioGroup.checkedRadioButtonId == -1) {
                Toast.makeText(this, "Please Select the file to download", Toast.LENGTH_SHORT)
                    .show()
            } else {
                checkedBtn()

            }

        }

        notificationManager = ContextCompat.getSystemService(applicationContext, NotificationManager::class.java) as NotificationManager
        createChannel(
            getString(R.string.download_channel_ID),
            getString(R.string.download_channel_name)
        )

        if (completed) {
            loadingButton.hasCompletedDownload()
        }

    }


    private fun checkedBtn() {

        val radioId = radioGroup.checkedRadioButtonId
        radioButton = findViewById(radioId)
        when (radioButton) {
            glide ->download("https://github.com/bumptech/glide",radioButton.text.toString())
            LoadApp -> download("https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
                ,radioButton.text.toString())
            retrofit ->download("https://github.com/square/retrofit",radioButton.text.toString())
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val action = intent.action
            if(action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
            {
                if (id == downloadID ){
                    if(getDownloadStatus() == DownloadManager.STATUS_SUCCESSFUL){

                        notificationManager.sendNotification(applicationContext.getString(R.string.notification_description)
                            ,applicationContext,"Success")

                        completed = true

                    }else
                    {

                        notificationManager.sendNotification(applicationContext.getString(R.string.notification_description)
                            ,applicationContext,"Fail")

                    }
                }
            }


        }
    }

    private fun download(url:String,title:String) {


        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(title)
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        //a unique long ID which acts as an identifier for the download.
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.



    }

    private fun getDownloadStatus() : Int {
        val query = DownloadManager.Query()
        query.setFilterById(downloadID)
        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val cursor = downloadManager.query(query)
        if (cursor.moveToFirst()){
            val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
            val status = cursor.getInt(columnIndex)
            return status
        }
        return DownloadManager.ERROR_UNKNOWN
    }


    private fun createChannel(channelId: String, channelName: String) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(channelId, channelName,
                //set the importance
                NotificationManager.IMPORTANCE_HIGH
            )
            // Register the channel with the system
            val notificationManager = this.getSystemService(
                NotificationManager::class.java)

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    // Notification ID.
    private val NOTIFICATION_ID = 0
    @SuppressLint("WrongConstant")
    fun NotificationManager.sendNotification(
        messageBody: String,
        applicationContext: Context,
        status: String
    ){
        // Create the content intent for the notification, which launches
        // this activity

        val statue_intent = Intent(applicationContext,DetailActivity::class.java)
        statue_intent.putExtra("downloadID",downloadID)
        statue_intent.putExtra("status",status)

        // TODO 1: Pending intent
        val statuePendingIntent = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_ID,
            statue_intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )


        // Build the notification
        val builder = NotificationCompat.Builder(
            applicationContext,applicationContext.getString(R.string.download_channel_ID)
        )
            .setSmallIcon(R.drawable.ic_baseline_download)
            .setContentTitle(applicationContext.getString(R.string.notification_title))
            .setContentText(messageBody)
            .addAction(R.drawable.ic_baseline_download,
                "Check the statue",statuePendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)


        notify(NOTIFICATION_ID,builder.build())

    }

    //    companion object   {
//        private const val URL =
//            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
//        private const val CHANNEL_ID = "channelId"
//    }

}
