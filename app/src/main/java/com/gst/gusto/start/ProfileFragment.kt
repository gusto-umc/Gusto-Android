package com.gst.gusto.start

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.gst.gusto.api.LoginViewModel
import com.gst.gusto.R
import com.gst.gusto.util.util
import com.gst.gusto.databinding.StartFragmentGenderBinding
import com.gst.gusto.databinding.StartFragmentProfileBinding
import com.gst.gusto.util.util.Companion.setImage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ProfileFragment : Fragment() {

    lateinit var binding: StartFragmentProfileBinding
    private val LoginViewModel : LoginViewModel by activityViewModels()
    var gender = "NONE"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = StartFragmentProfileBinding.inflate(inflater, container, false)

        binding.btnNext.setOnClickListener {
            binding.btnNext.isEnabled = false
            val bitmap = (binding.ivProfile.drawable as BitmapDrawable).bitmap

            val profileFile = saveBitmapToFile(requireContext(),bitmap,"profileImage.png")
            LoginViewModel.profileImg = profileFile

            LoginViewModel.signUp { resultCode ->
                binding.btnNext.isEnabled = true
                when (resultCode) {
                    1 -> {
                        findNavController().navigate(R.id.action_profileFragment_to_completeFragment)
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
            findNavController().navigate(R.id.action_profileFragment_to_genderFragment)
        }

        val pickMedia1 = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                setImage(binding.ivProfile, uri.toString(), requireContext())

            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
        binding.btnProfileEdit.setOnClickListener {
            pickMedia1.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))


        }


        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvTitle.text = LoginViewModel.nickName + "님의\n성별을 선택해주세요."

        setImage(binding.ivProfile,LoginViewModel.profileUrl,requireContext())
    }
    fun saveBitmapToFile(context: Context, bitmap: Bitmap, fileName: String): File? {
        // 외부 저장소의 Pictures 디렉터리 경로 가져오기
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File(storageDir, fileName)

        try {
            // 파일에 비트맵 저장
            val outputStream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            return imageFile
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

}