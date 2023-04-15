package org.feup.apm.acme.activities

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.feup.apm.acme.*
import org.feup.apm.acme.adaptors.ReceiptsAdapter
import org.feup.apm.acme.models.Receipt
import kotlin.concurrent.thread


class Receipts : AppCompatActivity() {
    private val backButton by lazy { findViewById<ImageButton>(R.id.receiptsBackButton)}

    private val progressBar by lazy {findViewById<ProgressBar>(R.id.progressBarReceipts)}
    private val mRecyclerView by lazy {findViewById<RecyclerView>(R.id.receiptsList)}
    private var mAdapter: RecyclerView.Adapter<*> = ReceiptsAdapter(listOf())
    private val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
    private val navbar by lazy { findViewById<BottomNavigationView>(R.id.navbar) }

    private var receipts = listOf<Receipt>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipts)
        mRecyclerView.layoutManager = mLayoutManager
        mRecyclerView.adapter = mAdapter

        backButton.setOnClickListener {
            finish()
        }

        loading(progressBar,listOf())
        val sharedPreference = this.getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val uuid = sharedPreference.getString("uuid","none")

        thread{
            receipts = uuid?.let {
                getPurchases(this,
                    it
                )
            }!!

            Log.d("res",receipts.toString())

            this.runOnUiThread {
                stopLoading(progressBar,listOf())
                addReceipts()
            }
        }
        navBarListeners(navbar,this)
    }


    private fun addReceipts(){
        mAdapter = ReceiptsAdapter(receipts)
        mRecyclerView.adapter = mAdapter
    }

}