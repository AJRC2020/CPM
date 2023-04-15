package org.feup.apm.acme.activities
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.feup.apm.acme.*
import org.feup.apm.acme.adaptors.VouchersAdapter
import org.feup.apm.acme.models.Voucher
import kotlin.concurrent.thread


class Vouchers : AppCompatActivity() {
    private val backButton by lazy { findViewById<ImageButton>(R.id.vouchersBackButton) }
    private val navbar by lazy { findViewById<BottomNavigationView>(R.id.navbar) }
    private val mRecyclerView by lazy { findViewById<RecyclerView>(R.id.voucherList) }
    private var mAdapter: RecyclerView.Adapter<*> = VouchersAdapter(listOf())
    private val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
    private val progressBar by lazy { findViewById<ProgressBar>(R.id.progressBar2) }


    private var vouchers = listOf<Voucher>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vouchers)
        mRecyclerView.layoutManager = mLayoutManager
        mRecyclerView.adapter = mAdapter

        //Buttons
        backButton.setOnClickListener {
            finish()
        }

        loading(progressBar, listOf())
        val sharedPreference = this.getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val uuid = sharedPreference.getString("uuid", "none")

        thread {
            vouchers = uuid?.let {
                getVouchers(
                    this,
                    it
                )
            }!!

            Log.d("res", vouchers.toString())

            this.runOnUiThread {
                stopLoading(progressBar, listOf())
                addVouchers()
            }
        }

        navBarListeners(navbar, this)
    }

    private fun addVouchers() {
        mAdapter = VouchersAdapter(vouchers)
        mRecyclerView.adapter = mAdapter
    }
}
