package ke.co.coopbank.financialliteracy.ui.activity.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ke.co.coopbank.financialliteracy.databinding.ListItemNotificationViewBinding
import ke.co.coopbank.financialliteracy.model.Notification
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class NotificationAdapter :
    ListAdapter<Notification, NotificationAdapter.NotificationViewHolder>(NotificationDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder(
            ListItemNotificationViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val course = getItem(position)
        holder.bind(course)
    }

    inner class NotificationViewHolder(
        private val binding: ListItemNotificationViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Notification) {
            binding.apply {
                notification = item
                executePendingBindings()
            }
        }
    }
}

object NotificationDiffCallback : DiffUtil.ItemCallback<Notification>() {

    override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
        return oldItem == newItem
    }
}