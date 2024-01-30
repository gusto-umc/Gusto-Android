package com.gst.gusto.start

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gst.gusto.R
import com.gst.gusto.databinding.StartFragmentNameBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NameFragment : Fragment() {

    lateinit var binding: StartFragmentNameBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = StartFragmentNameBinding.inflate(inflater, container, false)

        binding.btnNext.setOnClickListener {
            findNavController().navigate(R.id.action_nameFragment_to_ageFragment)
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
    }

}