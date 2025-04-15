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
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ke.co.coopbank.financialliteracy.R
import ke.co.coopbank.financialliteracy.databinding.ListItemQuestionViewBinding
import ke.co.coopbank.financialliteracy.model.Question
import ke.co.coopbank.financialliteracy.utilities.QuestionDiffCallback
import ke.co.coopbank.financialliteracy.utilities.isEqual
import timber.log.Timber
import java.util.*

class MarkQuizAdapter constructor(
    private val context: Context,
) :
    ListAdapter<Question, MarkQuizAdapter.QuestionViewHolder>(QuestionDiffCallback) {

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

        val answers = question.answers
        val userAnswers = question.userAnswers!!.split(",").map { it.trim() }
        val correct = isEqual(answers, userAnswers)

        holder.bind(question, position + 1, mark = correct)

        if (question.type!!.lowercase() == "BULLET".lowercase(Locale.getDefault())) {
            displayRadioButtons(question, holder, userAnswers)
        } else {
            displayCheckboxes(question, holder, userAnswers)
        }
    }


    private fun displayRadioButtons(
        question: Question,
        holder: QuestionViewHolder,
        userAnswers: List<String>?
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

            if (userAnswers != null) {
                if (userAnswers.contains(it.key)) {
                    radioButton.isChecked = true
                }
            }

            val colorFilter = PorterDuffColorFilter(
                ContextCompat.getColor(context, R.color.accent_200),
                PorterDuff.Mode.SRC_ATOP
            )

            CompoundButtonCompat.getButtonDrawable(radioButton)?.colorFilter = colorFilter

            radioButton.setPadding(5, 0, 5, 0)
            radioGroup.addView(radioButton)
        }

        holder.optionsLayout.addView(radioGroup)
    }

    private fun displayCheckboxes(
        question: Question,
        holder: QuestionViewHolder,
        userAnswers: List<String>?
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

            if (userAnswers != null) {
                if (userAnswers.contains(it.key)) {
                    checkBox.isChecked = true
                }
            }

            val colorFilter = PorterDuffColorFilter(
                ContextCompat.getColor(context, R.color.accent_200),
                PorterDuff.Mode.SRC_ATOP
            )

            CompoundButtonCompat.getButtonDrawable(checkBox)?.colorFilter = colorFilter
            holder.optionsLayout.addView(checkBox)
        }
    }

    inner class QuestionViewHolder(
        private val binding: ListItemQuestionViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        var optionsLayout: LinearLayout = binding.optionsLayout

        fun bind(item: Question, pos: Int, mark: Boolean) {
            binding.apply {
                question = item
                position = pos
                correct = mark
                showCorrectView = true
                executePendingBindings()
            }
        }
    }
}
