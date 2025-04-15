package ke.co.coopbank.financialliteracy.utilities

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import ke.co.coopbank.financialliteracy.model.Question

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T?) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}

fun <T> isEqual(first: List<T>, second: List<T>): Boolean {

    if (first.size != second.size) {
        return false
    }

    return first.zip(second).all { (x, y) -> x == y }
}

object QuestionDiffCallback : DiffUtil.ItemCallback<Question>() {

    override fun areItemsTheSame(oldItem: Question, newItem: Question): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Question, newItem: Question): Boolean {
        return oldItem == newItem
    }
}