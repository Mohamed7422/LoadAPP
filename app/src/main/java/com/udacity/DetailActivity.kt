package com.udacity

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.view.*

class DetailActivity : AppCompatActivity() {

    lateinit var button: Button
    lateinit var filetext: TextView
    lateinit var statusState:TextView
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        button  = findViewById(R.id.ok)
        filetext = findViewById(R.id.file_description)
        statusState = findViewById(R.id.status_state)

        val downloadID = intent.getLongExtra("downloadID", 0L)
        fun getDownloadTitle():String {
            val query = DownloadManager.Query()
            query.setFilterById(downloadID)
            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            val cursor = downloadManager.query(query)
            if (cursor.moveToFirst()){
                val title =  cursor.getString(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TITLE))
                return title
            }
            cursor.close()
            return DownloadManager.ERROR_UNKNOWN.toString()

        }

        val status = intent.getStringExtra("status")


        filetext.text = getDownloadTitle()
        statusState.text = status



        button.setOnClickListener{
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

    }

}
