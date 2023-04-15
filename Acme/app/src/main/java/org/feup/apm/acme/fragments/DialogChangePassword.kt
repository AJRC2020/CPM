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

        closeButton.setOnClickListener{
            dismiss()
        }

        yesButton.setOnClickListener {
            if (currentPassword.text.toString().trim().isNotEmpty() &&
                newPassword.text.toString().trim().isNotEmpty()
            ) {
                val currPassword = currentPassword.text.toString()
                val nPassword = newPassword.text.toString()
                loading(progressBar, listOf(yesButton))
                closeButton.isEnabled = false
                thread {
                    val result = changePassword(act, currPassword, nPassword, uuid, username)
                    act.runOnUiThread {
                        stopLoading(progressBar, listOf(yesButton))
                        closeButton.isEnabled = true
                        dismiss()
                        if (result) {
                            createSnackBar("Password change successfully", act)
                        } else {
                            createSnackBar("Error while changing password", act)
                        }
                    }
                }
            }
        }
    }

}