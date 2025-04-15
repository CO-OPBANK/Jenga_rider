package ke.co.coopbank.financialliteracy.ui.activity.main.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ke.co.coopbank.financialliteracy.databinding.ListItemCoursesViewBinding
import ke.co.coopbank.financialliteracy.model.Course
import ke.co.coopbank.financialliteracy.ui.activity.course.CourseActivity
import ke.co.coopbank.financialliteracy.utilities.COURSE_ID
import ke.co.coopbank.financialliteracy.utilities.QUIZ_ID
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class CourseAdapter : ListAdapter<Course, CourseAdapter.CourseViewHolder>(CourseDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        return CourseViewHolder(
            ListItemCoursesViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = getItem(position)
        holder.bind(course)
    }

    inner class CourseViewHolder(
        private val binding: ListItemCoursesViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener { view ->
                binding.course?.let { course ->
                    val intent = Intent(view.context, CourseActivity::class.java)
                        .apply {
                            putExtra(COURSE_ID, course.id)
                            putExtra(QUIZ_ID,course.quizId)
                        }

                    view.context.startActivity(intent)
                }
            }
        }

        fun bind(item: Course) {
            binding.apply {
                course = item
                executePendingBindings()
            }
        }
    }
}

object CourseDiffCallback : DiffUtil.ItemCallback<Course>() {

    override fun areItemsTheSame(oldItem: Course, newItem: Course): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Course, newItem: Course): Boolean {
        return oldItem == newItem
    }
}