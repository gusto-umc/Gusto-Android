package com.gst.gusto.my.fragment

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.gst.gusto.R
import com.gst.gusto.databinding.BottomSheetEditImageProfileImageMyBinding

class MyProfileImageEditBottomSheet(
    private val onBasicProfileClickListener: () -> Unit,
    private val onProfileClickListener: () -> Unit
): BottomSheetDialogFragment(R.layout.bottom_sheet_edit_image_profile_image_my) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = BottomSheetEditImageProfileImageMyBinding.bind(view)

        initView(binding)
    }

    private fun initView(binding: BottomSheetEditImageProfileImageMyBinding){
        with(binding){
            basicProfileBtn.setOnClickListener {
                onBasicProfileClickListener()
                dismiss()
            }
            profileBtn.setOnClickListener {
                onProfileClickListener()
                dismiss()
            }
        }
    }

}