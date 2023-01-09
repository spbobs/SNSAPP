package online.daliyq.ui.timeline

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import online.daliyq.databinding.ItemTimelineLoadStateBinding

class TimelineLoadStateAdapter(val retry: () -> Unit) : LoadStateAdapter<TimelineLoadStateViewHolder>(){
    override fun onBindViewHolder(holder: TimelineLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): TimelineLoadStateViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewHolder = ItemTimelineLoadStateBinding.inflate(layoutInflater, parent, false)

        return TimelineLoadStateViewHolder(viewHolder, retry)
    }
}