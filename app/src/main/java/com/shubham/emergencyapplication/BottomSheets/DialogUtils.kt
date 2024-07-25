package com.shubham.emergencyapplication.BottomSheets

import android.content.Context
import androidx.fragment.app.FragmentManager

object DialogUtils {
    fun showSosBottomSheetDialog(context: Context?, fragmentManager: FragmentManager?) {
        val sosBottomSheetDialogFragment = AddmemberBottomSheetDialog()
        sosBottomSheetDialogFragment.show(fragmentManager!!, "SOSBottomSheetDialogFragment")
    }
}
