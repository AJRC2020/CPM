package org.feup.apm.acme
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat


class Vouchers : AppCompatActivity() {
    private val backButton by lazy { findViewById<ImageButton>(R.id.vouchersBackButton)}
    private val scrollView by lazy {findViewById<LinearLayout>(R.id.vouchersScrollViewLayout)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vouchers)
        val inflater:LayoutInflater = LayoutInflater.from(this)


        //Buttons
        backButton.setOnClickListener {
            finish()
        }

        //I don't know what will be the format of voucher get requests, but assuming is something like {date, code, amount, validity

        fun addVoucher(date:String,code:String, amount:Int, valid:Boolean){
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



    }
}