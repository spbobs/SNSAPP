package online.daliyq.ui.timeline

import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import online.daliyq.databinding.ItemTimelineLoadStateBinding

class TimelineLoadStateViewHolder(val binding: ItemTimelineLoadStateBinding, val retry:() -> Unit) : ViewHolder(binding.root) {
    init {
        binding.retry.setOnClickListener {
            retry()
        }
    }

    fun bind(loadState: LoadState){
        binding.progress.isVisible = loadState is LoadState.Loading
        binding.retry.isVisible = loadState is LoadState.Error
    }
}