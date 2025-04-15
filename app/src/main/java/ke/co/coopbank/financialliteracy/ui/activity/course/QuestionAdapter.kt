package ke.co.coopbank.financialliteracy.ui.activity.course

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.CompoundButtonCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ke.co.coopbank.financialliteracy.R
import ke.co.coopbank.financialliteracy.api.request.Result
import ke.co.coopbank.financialliteracy.databinding.ListItemQuestionViewBinding
import ke.co.coopbank.financialliteracy.model.Question
import ke.co.coopbank.financialliteracy.utilities.QuestionDiffCallback
import java.util.*

class QuestionAdapter constructor(
    private val context: Context,
    private val listener: (Result) -> Unit
) :
    ListAdapter<Question, QuestionAdapter.QuestionViewHolder>(QuestionDiffCallback) {

    private var results: MutableList<Result> = mutableListOf()

    private var typeFace: Typeface =
        Typeface.createFromAsset(context.assets, "fonts/avenir_medium.ttf")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        return QuestionViewHolder(
            ListItemQuestionViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        val question = getItem(position)
        holder.bind(question, position + 1)
        if (question.type!!.lowercase() == "BULLET".lowercase(Locale.getDefault())) {
            displayRadioButtons(question, holder)
        } else {
            displayCheckboxes(question, holder)
        }
    }

    fun clearResults() {
        results.clear()
    }

    fun getResults(): MutableList<Result> {
        return results
    }

    private fun displayRadioButtons(
        question: Question,
        holder: QuestionViewHolder
    ) {
        val radioGroup = RadioGroup(context)
        radioGroup.orientation = RadioGroup.VERTICAL

        question.choices?.entries?.forEach { it ->
            val radioButton = RadioButton(context)
            radioButton.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            radioButton.tag = it.key
            radioButton.text = it.value
            radioButton.typeface = typeFace
            radioButton.setTextColor(ContextCompat.getColor(context, R.color.text_color))

            val colorFilter = PorterDuffColorFilter(
                ContextCompat.getColor(context, R.color.accent_200),
                PorterDuff.Mode.SRC_ATOP
            )
            CompoundButtonCompat.getButtonDrawable(radioButton)?.colorFilter = colorFilter

            radioButton.setPadding(5, 0, 5, 0)
            radioGroup.addView(radioButton)
        }

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val radioButton: RadioButton = group.findViewById(checkedId)
            val answer = radioButton.tag.toString().toInt()

            val position = results.indexOf(
                Result(
                    id = question.id,
                    answers = mutableListOf()
                )
            )

            val result: Result

            if (radioButton.isChecked) {
                if (position > -1) {
                    result = results[position]
                    result.answers = mutableListOf(answer)
                    results[position] = result
                } else {
                    result = Result(
                        id = question.id,
                        answers = mutableListOf(answer)
                    )

                    results.add(result)
                }

                listener(result)
            }
        }

        holder.optionsLayout.addView(radioGroup)
    }

    private fun displayCheckboxes(
        question: Question,
        holder: QuestionViewHolder
    ) {
        question.choices?.entries?.forEach { it ->
            val checkBox = CheckBox(context)
            checkBox.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            checkBox.tag = it.key
            checkBox.text = it.value
            checkBox.typeface = typeFace
            checkBox.setTextColor(ContextCompat.getColor(context, R.color.text_color))

            val colorFilter = PorterDuffColorFilter(
                ContextCompat.getColor(context, R.color.accent_200),
                PorterDuff.Mode.SRC_ATOP
            )
            CompoundButtonCompat.getButtonDrawable(checkBox)?.colorFilter = colorFilter

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                val answer = it.key.toInt()

                val position = results.indexOf(
                    Result(
                        id = question.id,
                        answers = mutableListOf()
                    )
                )

                var result: Result? = null

                if (isChecked) {
                    if (position > -1) {
                        result = results[position]
                        val answers = result.answers

                        if (!answers.contains(answer)) {
                            answers.add(answer)
                        }
                        result.answers = answers
                        results[position] = result
                    } else {
                        result = Result(
                            id = question.id,
                            answers = mutableListOf(answer)
                        )
                        results.add(result)
                    }
                } else {
                    if (position > -1) {
                        result = results[position]
                        result.answers.remove(answer)
                        if (result.answers.isEmpty()) {
                            results.removeAt(position)
                        }
                    }
                }

                if (result != null) {
                    listener(result)
                }
            }

            holder.optionsLayout.addView(checkBox)
        }
    }

    inner class QuestionViewHolder(
        private val binding: ListItemQuestionViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        var optionsLayout: LinearLayout = binding.optionsLayout

        fun bind(item: Question, pos: Int) {
            binding.apply {
                question = item
                position = pos
                showCorrectView = false
                executePendingBindings()
            }
        }
    }
}

