package ke.co.coopbank.financialliteracy.ui.dialog

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ke.co.coopbank.financialliteracy.R
import ke.co.coopbank.financialliteracy.model.Sacco
import ke.co.coopbank.financialliteracy.ui.dialog.SaccoDialog.SaccoAdapter.OnSaccoSelectedListener
import java.util.*

class SaccoDialog : BottomSheetDialogFragment() {

    private var mListener: OnSaccoListener? = null
    private var saccos: List<Sacco>? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parent = parentFragment
        mListener = if (parent != null) {
            parent as OnSaccoListener?
        } else {
            context as OnSaccoListener
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        saccos = ArrayList()
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_saccos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            saccos = requireArguments().getParcelableArrayList(KEY_SACCOS)
        }
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.adapter = SaccoAdapter(saccos, object : OnSaccoSelectedListener {
            override fun onSaccoSelected(sacco: Sacco?) {
                mListener!!.onSacco(sacco)
                dismiss()
            }
        })
    }

    interface OnSaccoListener {
        fun onSacco(sacco: Sacco?)
    }

    internal class SaccoAdapter(
        private val saccoList: List<Sacco>?,
        private val listener: OnSaccoSelectedListener?
    ) : RecyclerView.Adapter<SaccoAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_sacco_view, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val (_, name) = saccoList!![position]
            holder.nameTextView.text = name
        }

        override fun getItemCount(): Int {
            return saccoList!!.size
        }

        internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var nameTextView: TextView = itemView.findViewById(R.id.nameTextView)

            init {
                itemView.setOnClickListener {
                    listener?.onSaccoSelected(
                        saccoList!![adapterPosition]
                    )
                }
            }
        }

        interface OnSaccoSelectedListener {
            fun onSaccoSelected(sacco: Sacco?)
        }
    }

    companion object {
        const val TAG = "SaccoDialog"
        private const val KEY_SACCOS = "saccos"
        fun newInstance(saccoList: List<Sacco?>?): SaccoDialog {
            val args = Bundle()
            args.putParcelableArrayList(KEY_SACCOS, saccoList as ArrayList<out Parcelable?>?)
            val fragment = SaccoDialog()
            fragment.arguments = args
            return fragment
        }
    }
}