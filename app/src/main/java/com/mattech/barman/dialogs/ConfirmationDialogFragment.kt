package com.mattech.barman.dialogs

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.mattech.barman.R

class ConfirmationDialogFragment : DialogFragment() {
    var listener: ConfirmActionListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? ConfirmActionListener
    }

    interface ConfirmActionListener {
        fun onConfirm()
        fun onReject()
        fun onDialogDismissed()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?) = AlertDialog.Builder(requireContext())
            .setMessage(arguments?.getString(MESSAGE_KEY))
            .setPositiveButton(R.string.yes) { _, _ ->
                listener?.onConfirm()
            }
            .setNegativeButton(R.string.no) { _, _ ->
                listener?.onReject()
            }
            .create()

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener?.onDialogDismissed()
    }

    companion object {
        const val MESSAGE_KEY = "message"

        fun newInstance(message: String) = ConfirmationDialogFragment().apply {
            arguments = Bundle().apply {
                putString(MESSAGE_KEY, message)
            }
        }
    }
}