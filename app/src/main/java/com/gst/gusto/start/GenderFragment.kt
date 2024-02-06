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
import com.gst.gusto.Util.util
import com.gst.gusto.databinding.StartFragmentGenderBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
            LoginViewModel.signUp { resultCode ->
                when (resultCode) {
                    1 -> {
                        findNavController().navigate(R.id.action_genderFragment_to_completeFragment)
                        println("회원가입 성공")
                    }
                    2 -> {
                        Toast.makeText(requireContext(), "회원가입 실패", Toast.LENGTH_SHORT).show()
                        println("회원가입 실패")
                    }
                    else -> {
                        println("알 수 없는 결과 코드")
                    }
                }
            }
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