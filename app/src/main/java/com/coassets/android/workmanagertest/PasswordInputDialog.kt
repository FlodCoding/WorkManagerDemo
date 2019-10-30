package com.coassets.android.workmanagertest

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.dialog_password_input.view.*


/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2019-10-30
 * UseDes:
 */
class PasswordInputDialog constructor(private val onPasswordInputListener: OnPasswordInputListener) :
    DialogFragment() {

    interface OnPasswordInputListener {
        fun onSubmit(password: String)
        fun onCancel()
    }


    override fun onStart() {
        super.onStart()
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.dialog_password_input, container)

        val fabDone = root.fabDone
        val etInput = root.etInput
        
        fabDone.setOnClickListener {
            onPasswordInputListener.onSubmit(etInput.text.toString())
            dismiss()
        }


        etInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s ?: return

                if (s.length > 3) {
                    fabDone.show()
                } else {
                    fabDone.hide()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        return root
    }


    fun show(@NonNull manager: FragmentManager) {
        val remain = manager.findFragmentByTag("PasswordInputDialog")
        if (remain != null) return
        try {
            showNow(manager, "PasswordInputDialog")
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onCancel(dialog: DialogInterface?) {
        super.onCancel(dialog)
        onPasswordInputListener.onCancel()
    }


}
