package com.gst.clock.Fragment

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.gst.gusto.MainActivity
import com.gst.gusto.R
import com.gst.gusto.Util.util.Companion.dpToPixels
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentReviewAdd1Binding

class ReviewAdd1Fragment : Fragment() {

    lateinit var binding: FragmentReviewAdd1Binding
    lateinit var activity: MainActivity
    private val gustoViewModel : GustoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReviewAdd1Binding.inflate(inflater, container, false)

        activity = requireActivity() as MainActivity

        binding.btnExit.setOnClickListener {
            activity.hideBottomNavigation(false)
            findNavController().popBackStack()
        }

        binding.btnNext.setOnClickListener {
            gustoViewModel.progress = 0
            findNavController().navigate(R.id.action_reviewAdd1Fragment_to_reviewAdd2Fragment)
        }

        binding.btnSkip.setOnClickListener {

        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity.hideBottomNavigation(true)
        binding.lyRest.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                if (context != null) {
                    val width: Int = binding.lyRest.width
                    val height: Int = binding.lyRest.height
                    val marginInPixels = dpToPixels(8f, resources.displayMetrics)
                    val length =
                        if (width > height) (height - marginInPixels).toInt() else (width - marginInPixels).toInt()

                    val layoutParams = binding.ivRest.layoutParams
                    layoutParams.width = length
                    layoutParams.height = length
                    binding.ivRest.layoutParams = layoutParams
                }
                return true
            }
        })
    }
}