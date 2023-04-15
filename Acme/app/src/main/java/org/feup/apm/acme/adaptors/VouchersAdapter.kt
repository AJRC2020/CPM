package org.feup.apm.acme.adaptors

import android.app.ActionBar.LayoutParams
import android.graphics.Color
import android.provider.Settings.Global.getString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.feup.apm.acme.R
import org.feup.apm.acme.models.Receipt
import org.feup.apm.acme.models.Voucher


//TODO: make cards more like mockup
class VouchersAdapter(private val dataSet: List<Voucher>) :
    RecyclerView.Adapter<VouchersAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateTextView: TextView
        val codeTextView: TextView
        val voucherValidImage: ImageView

        init {
            dateTextView = view.findViewById(R.id.voucherDateText)
            codeTextView = view.findViewById(R.id.voucherCodeText)
            voucherValidImage = view.findViewById(R.id.voucherValidImage)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.voucher_layout, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        viewHolder.dateTextView.text =  dataSet[position].date
        viewHolder.codeTextView.text =  dataSet[position].uuid

        if (!dataSet[position].used){
            viewHolder.voucherValidImage.setImageDrawable(ContextCompat.getDrawable(viewHolder.itemView.context,R.drawable.tick_icon))
        }else{
            viewHolder.voucherValidImage.setImageDrawable(ContextCompat.getDrawable(viewHolder.itemView.context,R.drawable.cross_icon))
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}
