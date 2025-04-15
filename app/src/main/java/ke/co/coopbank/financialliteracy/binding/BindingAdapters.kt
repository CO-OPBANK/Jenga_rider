package ke.co.coopbank.financialliteracy.binding

import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import ke.co.coopbank.financialliteracy.R
import ke.co.coopbank.financialliteracy.utilities.AUTH_BASE_URL

@BindingAdapter("visibleGone")
fun showHide(view: View, show: Boolean) {
    view.visibility = if (show) View.VISIBLE else View.GONE
}

@BindingAdapter("profilePhoto")
fun bindProfilePhoto(view: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        Glide.with(view.context)
            .load("$AUTH_BASE_URL$imageUrl")
            .into(view)
    }
}

@BindingAdapter("imageUrl")
fun bindImageUrl(view: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        Glide.with(view.context)
            .load(imageUrl)
            .into(view)
    }
}

@BindingAdapter("renderHtml")
fun bindRenderHtml(view: TextView, description: String?) {
    if (description != null) {
        view.text = HtmlCompat.fromHtml(description, HtmlCompat.FROM_HTML_MODE_COMPACT)
        view.movementMethod = LinkMovementMethod.getInstance()
    } else {
        view.text = ""
    }
}

@BindingAdapter("progressIcon")
fun progressIcon(textView: TextView, complete: Boolean) {
    if (complete) {
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.trophy_gold, 0, 0, 0)
    } else {
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_trophy_line, 0, 0, 0)
    }
}
