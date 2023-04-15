package org.feup.apm.acme.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.fragment.app.DialogFragment
import org.feup.apm.acme.*
import org.feup.apm.acme.activities.UserProfile
import kotlin.concurrent.thread

class DialogChangePayment(private val uuid: String, private val act: UserProfile): DialogFragment(){


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.popup_change_card_number, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val yesButton = view.findViewById<Button>(R.id.yesButton)
        val closeButton = view.findViewById<ImageButton>(R.id.closePopUpButton)
        val changeCardField = view.findViewById<EditText>(R.id.changeCardField)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar3)

        closeButton.setOnClickListener{
            dismiss()
        }

        yesButton.setOnClickListener {
            if (changeCardField.text.toString().trim().isNotEmpty()
            ) {
                val newCard = changeCardField.text.toString().toLong()
                loading(progressBar, listOf(yesButton))
                closeButton.isEnabled = false
                thread {
                    val result = changePaymentMethod(act, newCard, uuid)
                    act.runOnUiThread {
                        stopLoading(progressBar, listOf(yesButton))
                        closeButton.isEnabled = true
                        dismiss()
                        if (result) {
                            createSnackBar("Payment method change successfully", act)
                        } else {
                            createSnackBar("Error while changing payment method", act)
                        }
                    }
                }
            }
        }
    }

}