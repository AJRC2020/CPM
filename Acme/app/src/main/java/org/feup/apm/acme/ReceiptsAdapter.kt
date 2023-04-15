package org.feup.apm.acme

import android.app.ActionBar.LayoutParams
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.feup.apm.acme.models.Receipt
import org.json.JSONArray


//TODO: make cards more like mockup
class ReceiptsAdapter(private val dataSet: List<Receipt>) :
    RecyclerView.Adapter<ReceiptsAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val date: TextView
        val total: TextView
        val totalDetails: TextView
        val voucherLabel: TextView
        val voucherTick: ImageView
        val table: TableLayout
        val expandedReceipt: LinearLayout
        val expandButton: ImageButton

        init {
            date = view.findViewById(R.id.date)
            total = view.findViewById(R.id.total)
            totalDetails = view.findViewById(R.id.totalLableDetails)
            voucherLabel = view.findViewById(R.id.voucherLable)
            table = view.findViewById(R.id.table)
            expandedReceipt = view.findViewById(R.id.expandedReceipt)
            expandButton = view.findViewById(R.id.expandButton)
            voucherTick = view.findViewById(R.id.voucherUsed)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.card_receipt, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Expand button setup
        viewHolder.expandButton.setOnClickListener {
            if (viewHolder.expandedReceipt.visibility == View.VISIBLE) {
                viewHolder.expandedReceipt.visibility = View.GONE
                viewHolder.expandButton.rotation = 0F
            } else {
                viewHolder.expandedReceipt.visibility = View.VISIBLE
                viewHolder.expandButton.rotation = 180F
            }
        }

        // Get basic receipt info
        viewHolder.date.text = dataSet[position].date
        viewHolder.total.text = dataSet[position].price.toString()
        viewHolder.totalDetails.text = dataSet[position].price.toString()
        viewHolder.totalDetails.textSize = 24f

        if (dataSet[position].voucher != "null"){
            viewHolder.voucherLabel.text = dataSet[position].voucher
            viewHolder.voucherTick.setImageResource(R.drawable.tick)
        }

        // Get items
        val items = dataSet[position].items
        (0 until items.size).forEach {
            val item = items[it]

            val lp = TableRow.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,1f)
            val lpss = TableRow.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,0.1f)
            val lpb = TableRow.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,0.95f)
            val lps = TableRow.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,0.05f)

            // Create rows
            val row = TableRow(viewHolder.itemView.context)


            // Add amount
            val amount = TextView(viewHolder.itemView.context)
            amount.text = item.amount.toString()
            amount.setTextColor(Color.BLACK)
            amount.textSize = 20f
            amount.layoutParams = lpss

            // Add name
            val name = TextView(viewHolder.itemView.context)
            name.text = item.name
            name.setTextColor(Color.BLACK)
            name.textSize = 20f
            name.layoutParams = lp

            // Add price
            val price = TextView(viewHolder.itemView.context)
            price.text = item.price.toString()
            price.setTextColor(Color.BLACK)
            price.textSize = 20f
            price.layoutParams = lpb
            price.textAlignment = View.TEXT_ALIGNMENT_TEXT_END

            // Add € symbol
            val euroSymbol = TextView(viewHolder.itemView.context)
            euroSymbol.text = "€"
            euroSymbol.setTextColor(Color.BLACK)
            euroSymbol.textSize = 20f
            euroSymbol.layoutParams = lps
            euroSymbol.textAlignment = View.TEXT_ALIGNMENT_TEXT_END

            // Add product to row
            row.addView(amount)
            row.addView(name)
            row.addView(price)
            row.addView(euroSymbol)

            // Add row to table
            viewHolder.table.addView(row)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}
