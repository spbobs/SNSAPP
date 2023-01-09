package online.daliyq.ui.timeline

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import online.daliyq.R
import online.daliyq.databinding.ItemTimelineCardBinding
import online.daliyq.db.entity.QuestionEntity
import online.daliyq.ui.details.DetailsActivity
import java.time.format.DateTimeFormatter

class TimelineCardViewHolder(val binding: ItemTimelineCardBinding) : ViewHolder(binding.root) {
    companion object{
        val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy. M. d.")
    }

    fun bind(question: QuestionEntity){
        binding.date.text = DATE_FORMATTER.format(question.id)
        binding.question.text = question.text

        binding.answerCount.text = if(question.answerCount > 0){
            binding.root.context.getString(R.string.answer_count_format, question.answerCount)
        } else {
            binding.root.context.getString(R.string.no_answer_yet)
        }

        binding.card.setOnClickListener {
            val context = binding.root.context

            context.startActivity(Intent(context,
            DetailsActivity::class.java).apply {
                putExtra(DetailsActivity.EXTRA_QID, question.id)
            })
        }
    }
}