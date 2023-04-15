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
import org.feup.apm.acme.adaptors.ReceiptsAdapter
import org.feup.apm.acme.adaptors.VouchersAdapter
import org.feup.apm.acme.models.Receipt
import org.feup.apm.acme.models.Voucher
import kotlin.concurrent.thread


class Vouchers : AppCompatActivity() {
    private val backButton by lazy { findViewById<ImageButton>(R.id.vouchersBackButton) }
    private val navbar by lazy { findViewById<BottomNavigationView>(R.id.navbar) }
    private val mRecyclerView by lazy {findViewById<RecyclerView>(R.id.voucherList)}
    private var mAdapter: RecyclerView.Adapter<*> = VouchersAdapter(listOf())
    private val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
    private val progressBar by lazy {findViewById<ProgressBar>(R.id.progressBar2)}

    var vouchers = listOf<Voucher>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vouchers)
        mRecyclerView.layoutManager = mLayoutManager;
        mRecyclerView.adapter = mAdapter;

        //Buttons
        backButton.setOnClickListener {
            finish()
        }

        loading(progressBar,listOf())
        val sharedPreference = this.getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val uuid = sharedPreference.getString("uuid","none")

        thread{
            vouchers = uuid?.let {
                getVouchers(this,
                    it
                )
            }!!

            Log.d("res",vouchers.toString())

            this.runOnUiThread {
                stopLoading(progressBar,listOf())
                addVouchers()
            }
        }

        navBarListeners(navbar,this)
    }

    private fun addVouchers(){
        mAdapter = VouchersAdapter(vouchers)
        mRecyclerView.adapter = mAdapter;
    }



        //I don't know what will be the format of voucher get requests, but assuming is something like {date, code, amount, validity

        /*fun addVoucher(date:String,code:String, amount:Int, valid:Boolean){
            val voucherLayout: View = inflater.inflate(R.layout.voucher_layout, scrollView, false)

            // Populate the views with the information from the item

            // Populate the views with the information from the item
            val dateTextView: TextView = voucherLayout.findViewById(R.id.voucherDateText)
            dateTextView.text = getString(R.string.voucher_code_format,code)

            val codeTextView: TextView = voucherLayout.findViewById(R.id.voucherCodeText)
            codeTextView.text = getString(R.string.voucher_date_format,date)

            val percentageTextView: TextView = voucherLayout.findViewById(R.id.voucherPercentageText)
            percentageTextView.text = getString(R.string.voucher_percentage_format)

            val voucherValidImage: ImageView = voucherLayout.findViewById(R.id.voucherValidImage)
            if (valid){
                voucherValidImage.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.tick_icon))
            }else{
                voucherValidImage.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.cross_icon))
            }

            // Add the frame view to the scroll view

            // Add the frame view to the scroll view
            scrollView.addView(voucherLayout)
        }

        addVoucher("10-05-2024","2901-dadd-3214",15,true)
        addVoucher("13-06-2024","3jkd-32dd-mmmm",15,false)
        addVoucher("13-06-2024","3jkd-32dd-mmmm",15,false)
        addVoucher("13-06-2024","3jkd-32dd-mmmm",15,false)
        addVoucher("13-06-2024","3jkd-32dd-mmmm",15,false)



    }*/
}
