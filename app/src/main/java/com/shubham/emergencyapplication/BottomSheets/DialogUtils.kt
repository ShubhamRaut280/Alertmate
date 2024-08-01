package com.shubham.emergencyapplication.BottomSheets

import android.app.Activity
import android.content.Context
import androidx.fragment.app.FragmentManager
import com.shubham.emergencyapplication.Models.User

object DialogUtils {
    fun showSosBottomSheetDialog(context: Context?, fragmentManager: FragmentManager?) {
        val sosBottomSheetDialogFragment = AddmemberBottomSheetDialog()
        sosBottomSheetDialogFragment.show(fragmentManager!!, "SOSBottomSheetDialogFragment")
    }
    fun showUserDetailsSheet(userid : User, context: Context?, fragmentManager: FragmentManager?) {
        val sosBottomSheetDialogFragment = UserDetailsBottomSheet(userid)
        sosBottomSheetDialogFragment.show(fragmentManager!!, "SOSBottomSheetDialogFragment")
    }

}
