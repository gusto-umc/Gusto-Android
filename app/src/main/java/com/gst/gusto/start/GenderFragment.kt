package com.gst.gusto.start

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gst.gusto.R
import com.gst.gusto.Util.util
import com.gst.gusto.databinding.StartFragmentGenderBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GenderFragment : Fragment() {

    lateinit var binding: StartFragmentGenderBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = StartFragmentGenderBinding.inflate(inflater, container, false)

        binding.btnNext.setOnClickListener {
            findNavController().navigate(R.id.action_genderFragment_to_completeFragment)
        }
        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_genderFragment_to_ageFragment)
        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etName.setOnClickListener {
            util.toggleLayout(true, binding.lyAges)
        }

        binding.btnWoman.setOnClickListener {
            binding.etName.text = binding.btnWoman.text
            util.toggleLayout(false, binding.lyAges)
        }
        binding.btnMan.setOnClickListener {
            binding.etName.text = binding.btnMan.text
            util.toggleLayout(false, binding.lyAges)
        }
        binding.btnNo.setOnClickListener {
            binding.etName.text = binding.btnNo.text
            util.toggleLayout(false, binding.lyAges)
        }
    }

}