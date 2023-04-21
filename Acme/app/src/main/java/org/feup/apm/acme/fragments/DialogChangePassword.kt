package org.feup.apm.acme.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import org.feup.apm.acme.*
import org.feup.apm.acme.activities.UserProfile
import kotlin.concurrent.thread

class DialogChangePassword(private val uuid: String, private val username: String,private val act: UserProfile): DialogFragment(){


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.popup_change_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val yesButton = view.findViewById<Button>(R.id.yesButton)
        val closeButton = view.findViewById<ImageButton>(R.id.closePopUpButton)
        val currentPassword = view.findViewById<EditText>(R.id.currentPasswordField)
        val newPassword = view.findViewById<EditText>(R.id.changePasswordField)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar4)
        val warningPassNew = view.findViewById<TextView>(R.id.newPasswordError)
        val warningPassCurrent = view.findViewById<TextView>(R.id.oldPasswordError)

        closeButton.setOnClickListener{
            dismiss()
        }

        yesButton.setOnClickListener {

            warningPassNew.visibility = View.GONE
            warningPassCurrent.visibility = View.GONE

            if (currentPassword.text.toString().trim().isEmpty()){
                showError(warningPassCurrent, "The current password field has be filled")
                return@setOnClickListener
            }


            if (newPassword.text.toString().trim().isEmpty()){
                showError(warningPassNew, "The new password field has be filled")
                return@setOnClickListener
            }


            val currPassword = currentPassword.text.toString()
            val nPassword = newPassword.text.toString()
            loading(progressBar, listOf(yesButton))
            closeButton.isEnabled = false

            thread {
                try{
                    changePassword(currPassword, nPassword, uuid, username)
                    act.runOnUiThread {
                        stopLoading(progressBar, listOf(yesButton))
                        closeButton.isEnabled = true
                        dismiss()
                        val dialog = DialogGeneric("Success", "Password was changed successfully")
                        dialog.show(act.supportFragmentManager, "success")
                    }
                }catch(e: Exception){
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