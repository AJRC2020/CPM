package org.feup.apm.acme.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

// To use
//val dialog = MyDialogFragment("Whatever title", "Whatever message")
//dialog.show(supportFragmentManager, "Whatever tag")


class DialogGeneric(private val title:String, private val message:String) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { _, _ ->
                dismiss()
            }
        return builder.create()
    }
}