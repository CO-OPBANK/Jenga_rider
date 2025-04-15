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
import ke.co.coopbank.financialliteracy.model.County
import ke.co.coopbank.financialliteracy.model.Sacco
import ke.co.coopbank.financialliteracy.ui.dialog.SaccoDialog.SaccoAdapter.OnSaccoSelectedListener
import java.util.*

class CountyDialog : BottomSheetDialogFragment() {

    private var mListener: OnCountyListener? = null
    private var counties: List<County>? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parent = parentFragment
        mListener = if (parent != null) {
            parent as OnCountyListener?
        } else {
            context as OnCountyListener
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        counties = ArrayList()
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_counties, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            counties = requireArguments().getParcelableArrayList(KEY_COUNTIES)
        }
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.adapter = CountyAdapter(counties, object :
            CountyAdapter.OnCountySelectedListener {
            override fun onCountySelected(county: County?) {
                mListener!!.onCounty(county)
                dismiss()
            }
        })
    }

    interface OnCountyListener {
        fun onCounty(county: County?)
    }

    internal class CountyAdapter(
        private val countyList: List<County>?,
        private val listener: OnCountySelectedListener?
    ) : RecyclerView.Adapter<CountyAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_county_view, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val (_, name) = countyList!![position]
            holder.nameTextView.text = name
        }

        override fun getItemCount(): Int {
            return countyList!!.size
        }

        internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var nameTextView: TextView = itemView.findViewById(R.id.nameTextView)

            init {
                itemView.setOnClickListener {
                    listener?.onCountySelected(
                        countyList!![adapterPosition]
                    )
                }
            }
        }

        interface OnCountySelectedListener {
            fun onCountySelected(county: County?)
        }
    }

    companion object {
        const val TAG = "CountyDialog"
        private const val KEY_COUNTIES = "saccos"
        fun newInstance(countyList: List<County?>?): CountyDialog {
            val args = Bundle()
            args.putParcelableArrayList(KEY_COUNTIES, countyList as ArrayList<out Parcelable?>?)
            val fragment = CountyDialog()
            fragment.arguments = args
            return fragment
        }
    }
}