package org.feup.apm.acme.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import org.feup.apm.acme.*
import org.feup.apm.acme.activities.UserProfile
import kotlin.concurrent.thread

class DialogChangePayment(private val uuid: String, private val username: String, private val act: UserProfile): DialogFragment(){


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
        val warning = view.findViewById<TextView>(R.id.cardChangeError)

        closeButton.setOnClickListener{
            dismiss()
        }

        yesButton.setOnClickListener {

            warning.visibility = View.GONE

            if (changeCardField.text.toString().trim().isEmpty()){
                showError(warning, "The new card field has be filled")
                return@setOnClickListener
            }

            val newCard = changeCardField.text.toString().toLong()
            loading(progressBar, listOf(yesButton))
            closeButton.isEnabled = false

            thread {
                try{
                    changePaymentMethod(newCard, uuid, username)
                    act.runOnUiThread {
                        stopLoading(progressBar, listOf(yesButton))
                        closeButton.isEnabled = true
                        dismiss()
                        val dialog = DialogGeneric("Success", "Password was changed successfully")
                        dialog.show(act.supportFragmentManager, "success")
                    }
                }catch (e: Exception){
                    act.runOnUiThread {
                        stopLoading(progressBar, listOf(yesButton))
                        dismiss()
                        val dialog = e.message?.let { DialogGeneric("Error", it) }
                        dialog?.show(act.supportFragmentManager, "error")
                    }
                }
            }

        }
    }

}