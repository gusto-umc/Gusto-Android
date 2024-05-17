package com.gst.gusto.start

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.gst.gusto.api.LoginViewModel
import com.gst.gusto.R
import com.gst.gusto.util.util
import com.gst.gusto.databinding.StartFragmentGenderBinding

class GenderFragment : Fragment() {

    lateinit var binding: StartFragmentGenderBinding
    private val LoginViewModel : LoginViewModel by activityViewModels()
    var gender = "NONE"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = StartFragmentGenderBinding.inflate(inflater, container, false)

        binding.btnNext.setOnClickListener {
            LoginViewModel.setGender(gender)
            findNavController().navigate(R.id.action_genderFragment_to_profileFragment)
        }
        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_genderFragment_to_ageFragment)
        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvTitle.text = LoginViewModel.nickName + "님의\n성별을 선택해주세요."

        binding.etName.setOnClickListener {
            util.toggleLayout(true, binding.lyAges)
        }

        binding.btnWoman.setOnClickListener {
            binding.etName.text = binding.btnWoman.text
            gender = "FEMALE"
            util.toggleLayout(false, binding.lyAges)
        }
        binding.btnMan.setOnClickListener {
            binding.etName.text = binding.btnMan.text
            gender = "MALE"
            util.toggleLayout(false, binding.lyAges)
        }
        binding.btnNo.setOnClickListener {
            binding.etName.text = binding.btnNo.text
            gender = "NONE"
            util.toggleLayout(false, binding.lyAges)
        }
    }

}