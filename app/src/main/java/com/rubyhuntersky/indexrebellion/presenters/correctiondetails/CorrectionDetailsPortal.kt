package com.rubyhuntersky.indexrebellion.presenters.correctiondetails

import android.support.v4.app.FragmentActivity
import com.rubyhuntersky.indexrebellion.data.report.CorrectionDetails
import com.rubyhuntersky.interaction.core.Portal

class CorrectionDetailsPortal(private val getFragmentActivity: () -> FragmentActivity) :
    Portal<CorrectionDetails> {

    override fun jump(carry: CorrectionDetails) {
        CorrectionDetailsDialogFragment.newInstance(carry, getFragmentActivity)
            .show(
                getFragmentActivity().supportFragmentManager,
                CorrectionDetailsDialogFragment.newInstance(carry, getFragmentActivity).javaClass.simpleName
            )
    }
}
