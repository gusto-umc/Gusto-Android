package com.gst.clock.Fragment

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gst.gusto.R
import com.gst.gusto.Util.util.Companion.dpToPixels
import com.gst.gusto.databinding.FragmentReviewAdd1Binding

class ReviewAdd1Fragment : Fragment() {

    lateinit var binding: FragmentReviewAdd1Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReviewAdd1Binding.inflate(inflater, container, false)

        binding.btnExit.setOnClickListener {
            findNavController().navigate(R.id.action_reviewAdd1Fragment_to_myFragment)
        }

        binding.btnNext.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("progress", 0)
            }
            findNavController().navigate(R.id.action_reviewAdd1Fragment_to_reviewAdd2Fragment,bundle)
        }

        binding.btnSkip.setOnClickListener {

        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lyRest.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                val width: Int = binding.lyRest.width
                val height: Int = binding.lyRest.height
                val marginInPixels = dpToPixels(8f,resources.displayMetrics)
                val length =
                    if (width > height) (height - marginInPixels).toInt() else (width - marginInPixels).toInt()

                val layoutParams = binding.ivRest.layoutParams
                layoutParams.width = length
                layoutParams.height = length
                binding.ivRest.layoutParams = layoutParams
                return true
            }
        })


    }


}