package com.gst.gusto.start

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gst.gusto.R
import com.gst.gusto.Util.util
import com.gst.gusto.Util.util.Companion.toggleLayout
import com.gst.gusto.databinding.StartFragmentAgeBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AgeFragment : Fragment() {

    lateinit var binding: StartFragmentAgeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = StartFragmentAgeBinding.inflate(inflater, container, false)

        binding.btnNext.setOnClickListener {
            findNavController().navigate(R.id.action_ageFragment_to_genderFragment)
        }
        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_ageFragment_to_nameFragment)
        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etName.setOnClickListener {
            util.toggleLayout(true, binding.lyAges)
        }

        binding.btn10.setOnClickListener {
            binding.etName.text = binding.btn10.text
            toggleLayout(false, binding.lyAges)
        }
        binding.btn20.setOnClickListener {
            binding.etName.text = binding.btn20.text
            toggleLayout(false, binding.lyAges)
        }
        binding.btn30.setOnClickListener {
            binding.etName.text = binding.btn30.text
            toggleLayout(false, binding.lyAges)
        }
        binding.btn40.setOnClickListener {
            binding.etName.text = binding.btn40.text
            toggleLayout(false, binding.lyAges)
        }
        binding.btn50.setOnClickListener {
            binding.etName.text = binding.btn50.text
            toggleLayout(false, binding.lyAges)
        }
        binding.btn60.setOnClickListener {
            binding.etName.text = binding.btn60.text
            toggleLayout(false, binding.lyAges)
        }
        binding.btnNo.setOnClickListener {
            binding.etName.text = binding.btnNo.text
            toggleLayout(false, binding.lyAges)
        }
    }

}