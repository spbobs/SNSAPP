package online.daliyq.ui.today

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import coil.load
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import online.daliyq.R
import online.daliyq.api.response.Question
import online.daliyq.databinding.FragmentTodayBinding
import online.daliyq.ui.base.BaseFragment
import online.daliyq.ui.image.ImageViewerActivity
import online.daliyq.ui.write.WriteActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TodayFragment : BaseFragment() {
    var _binding: FragmentTodayBinding? = null
    val binding get() = _binding!!

    var question: Question? = null

    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            lifecycleScope.launch {
                setupAnswer()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTodayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.writeButton.setOnClickListener {
            startForResult.launch(Intent(requireContext(), WriteActivity::class.java).apply {
                putExtra(WriteActivity.EXTRA_QID, question!!.id)
                putExtra(WriteActivity.EXTRA_MODE, WriteActivity.Mode.WRITE)
            })
        }

        binding.editButton.setOnClickListener {
            startForResult.launch(Intent(requireContext(), WriteActivity::class.java).apply {
                putExtra(WriteActivity.EXTRA_QID, question!!.id)
                putExtra(WriteActivity.EXTRA_MODE, WriteActivity.Mode.EDIT)
            })
        }

        binding.deleteButton.setOnClickListener {
            showDeleteConfirmDialog()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val questionResponse = api.getQuestion(LocalDate.now())
            if (questionResponse.isSuccessful) {
                question = questionResponse.body()!!

                val dateFormatter = DateTimeFormatter.ofPattern("yyyy. M. d.")
                binding.date.text = dateFormatter.format(question!!.id)
                binding.question.text = question!!.text

                setupAnswer()
            }
        }

//        Thread{
//            val url = URL("http://10.0.2.2:5000/v1/hello-world")
//
//            val conn = url.openConnection() as HttpURLConnection
//            conn.connectTimeout = 5000
//            conn.readTimeout = 5000
//            conn.requestMethod = "GET"
//            conn.setRequestProperty("Accept","application/json")
//
//            conn.connect()
//
//            val reader = BufferedReader(InputStreamReader(conn.inputStream))
//            val body = reader.readText()
//            reader.close()
//            conn.disconnect()
//
////            val json = JSONObject(body)
////            val date = json.getString("date")
////            val message = json.getString("message")
//
//            val gson = Gson()
//            val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.KOREA)
//            val helloWorld = gson.fromJson(body, HelloWorld::class.java)
//
//            activity?.runOnUiThread{
//                binding.date.text = dateFormat.format(helloWorld.date)
//                binding.question.text = helloWorld.message
//            }
//        }.start()
    }

    fun showDeleteConfirmDialog() {
        MaterialAlertDialogBuilder(requireContext()).setMessage(R.string.dialog_msg_are_you_sure_to_delete)
            .setPositiveButton(R.string.ok) { dialog, which ->
                lifecycleScope.launch {
                    val deleteResponse = api.deleteAnswer(question!!.id)
                    if (deleteResponse.isSuccessful) {
                        binding.answerArea.isVisible = false
                        binding.writeButton.isVisible = true
                    }
                }
            }.setNegativeButton(R.string.cancel) { dialog, which ->

            }.show()
    }

    suspend fun setupAnswer() {
        val question = question ?: return

        val answer = api.getAnswer(question.id).body()
        binding.answerArea.isVisible = answer != null
        binding.textAnswer.text = answer?.text

        binding.writeButton.isVisible = answer == null

        binding.photoAnswer.isVisible = !answer?.photo.isNullOrEmpty()
        answer?.photo?.let {
            binding.photoAnswer.load(it) {
                placeholder(R.drawable.ph_image)
            }
            binding.photoAnswer.setOnClickListener {
                startActivity(Intent(
                    requireContext(), ImageViewerActivity::class.java
                ).apply {
                    putExtra(ImageViewerActivity.EXTRA_URL, answer.photo)
                })
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}