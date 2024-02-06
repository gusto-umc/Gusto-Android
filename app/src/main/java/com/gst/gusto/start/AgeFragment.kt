package com.gst.gusto.start

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.gst.api.LoginViewModel
import com.gst.gusto.R
import com.gst.gusto.Util.util
import com.gst.gusto.Util.util.Companion.toggleLayout
import com.gst.gusto.databinding.StartFragmentAgeBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AgeFragment : Fragment() {

    lateinit var binding: StartFragmentAgeBinding
    private val LoginViewModel : LoginViewModel by activityViewModels()
    var age = "NONE"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = StartFragmentAgeBinding.inflate(inflater, container, false)


        binding.btnNext.setOnClickListener {
            LoginViewModel.setAge(age)
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
            age ="TEEN"
            toggleLayout(false, binding.lyAges)
        }
        binding.btn20.setOnClickListener {
            binding.etName.text = binding.btn20.text
            age ="TWENTIES"
            toggleLayout(false, binding.lyAges)
        }
        binding.btn30.setOnClickListener {
            binding.etName.text = binding.btn30.text
            age ="THIRTIES"
            toggleLayout(false, binding.lyAges)
        }
        binding.btn40.setOnClickListener {
            binding.etName.text = binding.btn40.text
            age ="FOURTIES"
            toggleLayout(false, binding.lyAges)
        }
        binding.btn50.setOnClickListener {
            binding.etName.text = binding.btn50.text
            age ="FIFTIES"
            toggleLayout(false, binding.lyAges)
        }
        binding.btn60.setOnClickListener {
            binding.etName.text = binding.btn60.text
            age ="OLDER"
            toggleLayout(false, binding.lyAges)
        }
        binding.btnNo.setOnClickListener {
            binding.etName.text = binding.btnNo.text
            age ="NONE"
            toggleLayout(false, binding.lyAges)
        }
    }

}