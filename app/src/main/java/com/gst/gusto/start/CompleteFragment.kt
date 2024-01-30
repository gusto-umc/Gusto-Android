package com.gst.gusto.start

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gst.gusto.R
import com.gst.gusto.Util.util
import com.gst.gusto.databinding.StartFragmentCompleteBinding
import com.gst.gusto.databinding.StartFragmentGenderBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CompleteFragment : Fragment() {

    lateinit var binding: StartFragmentCompleteBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = StartFragmentCompleteBinding.inflate(inflater, container, false)

        binding.btnNext.setOnClickListener {
            //findNavController().navigate(R.id.action_ageFragment_to_genderFragment)
            requireActivity().finish()
        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}