package com.gst.gusto.start

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.gst.gusto.api.LoginViewModel
import com.gst.gusto.R
import com.gst.gusto.databinding.StartFragmentNameBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NameFragment : Fragment() {

    lateinit var binding: StartFragmentNameBinding
    private val LoginViewModel : LoginViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = StartFragmentNameBinding.inflate(inflater, container, false)

        binding.btnNext.setOnClickListener {
            LoginViewModel.checkNickname(binding.etName.text.toString()) { resultCode ->
                when (resultCode) {
                    1 -> {
                        LoginViewModel.confirmNickname(binding.etName.text.toString()) { resultCode ->
                            when (resultCode) {
                                1 -> {
                                    LoginViewModel.setNickName(binding.etName.text.toString())
                                    findNavController().navigate(R.id.action_nameFragment_to_ageFragment)
                                }/*
                                2 -> {
                                    Toast.makeText(requireContext(), "이미 중복된 닉네임이 존재합니다", Toast.LENGTH_SHORT).show()
                                }
                                else -> {
                                    Toast.makeText(requireContext(), "오류 발생", Toast.LENGTH_SHORT).show()
                                }*/
                            }
                        }
                    }
                    2 -> {
                        Toast.makeText(requireContext(), "이미 중복된 닉네임이 존재합니다", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Toast.makeText(requireContext(), "오류 발생", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }
        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_nameFragment_to_loginFragment)
        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 이벤트 발생 전에 수행할 작업
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 텍스트가 변경될 때 수행할 작업
                binding.tvNum.text = "(${binding.etName.text.length}/13)"
            }

            override fun afterTextChanged(s: Editable?) {
                // 이벤트 발생 후에 수행할 작업
            }
        })

        val activity = requireActivity() as StartActivity
        activity.startTimer()
    }

}