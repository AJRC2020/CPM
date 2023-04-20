package org.feup.apm.terminalacme

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import java.util.*

class ResultActivity : AppCompatActivity() {

    private val retry by lazy {findViewById<Button>(R.id.retryBut)}
    private val title by lazy {findViewById<TextView>(R.id.titleRes)}
    private val content by lazy {findViewById<TextView>(R.id.descRes)}
    private val layout by lazy {findViewById<FrameLayout>(R.id.layoutRes)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.result_activity)

        val intent = intent
        val titleText = intent.getStringExtra("title") ?: "None"
        val descText = intent.getStringExtra("description") ?: "None"
        val error = intent.getBooleanExtra("error",false)

        title.text = titleText
        content.text = descText

        if (error){
            retry.visibility = View.VISIBLE
            layout.background = ResourcesCompat.getDrawable(resources, R.color.error_red, null);
        }

        retry.setOnClickListener {
            loadMain()
        }

        timeOut()
    }

    private fun timeOut(){
        val t = Timer()
        t.schedule(object : TimerTask() {
            override fun run() {
                loadMain()
            }
        }, 10000)
    }

    private fun loadMain(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

}