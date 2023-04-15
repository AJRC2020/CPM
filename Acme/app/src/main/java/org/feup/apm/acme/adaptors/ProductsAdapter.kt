package org.feup.apm.acme.adaptors

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import org.feup.apm.acme.R
import org.feup.apm.acme.convertToEuros
import org.feup.apm.acme.fragments.DialogWarningDelete
import org.feup.apm.acme.models.ProductAmount


class ProductsAdapter(private val dataSet: MutableList<ProductAmount>) :
    RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val amountField: TextView
        val productName: TextView
        val priceField: TextView
        val removeAllButton: ImageButton
        val removeOneButton: ImageButton

        init {
            amountField = view.findViewById(R.id.amountField)
            productName = view.findViewById(R.id.productName)
            priceField = view.findViewById(R.id.priceField)
            removeAllButton = view.findViewById(R.id.remove_all_button)
            removeOneButton = view.findViewById(R.id.remove_one_button)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.product_layout, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.amountField.text = dataSet[position].amount.toString()
        viewHolder.productName.text = dataSet[position].name
        viewHolder.priceField.text = convertToEuros(dataSet[position].price)

        viewHolder.removeAllButton.setOnClickListener{
            val delete = {dataSet[position].uuid?.let { it -> deleteProduct(it,position,viewHolder) }}
            val popupMenu = DialogWarningDelete(dataSet[position].name,dataSet[position].amount, delete)
            val manager = (viewHolder.itemView.context as FragmentActivity).supportFragmentManager
            popupMenu.show(manager,"PopUp")
        }

        viewHolder.removeOneButton.setOnClickListener{
            decreaseProductAmount(dataSet[position],position,viewHolder)
        }

    }


    private fun deleteProduct(uuid: String, position: Int, viewHolder: ViewHolder){
        val sharedPreference = viewHolder.itemView.context.getSharedPreferences("shopping_cart_prod_names", Context.MODE_PRIVATE)
        val sharedPreferencePrices =viewHolder.itemView.context.getSharedPreferences("shopping_cart_prod_prices", Context.MODE_PRIVATE)
        val sharedPreferenceAmount = viewHolder.itemView.context.getSharedPreferences("shopping_cart_prod_amount", Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        val editorPrices = sharedPreferencePrices.edit()
        val editorAmount = sharedPreferenceAmount.edit()
        editor.remove(uuid)
        editor.apply()
        editorPrices.remove(uuid)
        editorPrices.apply()
        editorAmount.remove(uuid)
        editorAmount.apply()

        dataSet.removeAt(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, dataSet.size);
    }

    private fun decreaseProductAmount(product: ProductAmount, position: Int, viewHolder: ViewHolder){

        val sharedPreferenceAmount = viewHolder.itemView.context.getSharedPreferences("shopping_cart_prod_amount", Context.MODE_PRIVATE)
        val amount = sharedPreferenceAmount.getInt(product.uuid,0)
        if (amount <= 1){
            product.uuid?.let { deleteProduct(it,position, viewHolder) }
        }else{
            val editorAmount = sharedPreferenceAmount.edit()
            editorAmount.putInt(product.uuid,amount-1)
            editorAmount.apply()

            product.amount -= 1

            notifyItemChanged(position);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}
