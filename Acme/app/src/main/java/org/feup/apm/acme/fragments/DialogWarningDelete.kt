package org.feup.apm.acme.fragments

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import org.feup.apm.acme.R
import javax.security.auth.callback.Callback

class DialogWarningDelete(private val product: String,private val amount: Int, private val delete: () -> Unit? ): DialogFragment(){


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.popup_delete_confirmation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val yesButton = view.findViewById<Button>(R.id.yesButton)
        val cancelButton = view.findViewById<Button>(R.id.cancelButton)
        val closeButton = view.findViewById<ImageButton>(R.id.closePopUpButton)
        val content = view.findViewById<TextView>(R.id.contentView)

        closeButton.setOnClickListener{
            dismiss()
        }

        yesButton.setOnClickListener {
            delete()
            dismiss()
        }

        content.text = String.format(resources.getString(R.string.warning_delete_cart), amount, product)

        cancelButton.setOnClickListener{
            dismiss()
        }

    }

}