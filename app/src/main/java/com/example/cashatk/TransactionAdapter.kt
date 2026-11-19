package com.example.cashatk

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cashatk.data.TransactionWithCategory
import java.text.NumberFormat
import java.util.Locale

class TransactionAdapter :
    ListAdapter<TransactionWithCategory, TransactionAdapter.TransactionViewHolder>(DiffCallback) {

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        private val tvDate: TextView = itemView.findViewById(R.id.tv_date)
        private val tvAmount: TextView = itemView.findViewById(R.id.tv_amount)
        private val ivIcon: ImageView = itemView.findViewById(R.id.iv_icon)

        fun bind(item: TransactionWithCategory) {
            tvTitle.text = item.transaction.description ?: item.category.name

            val isRecent = DateUtils.isToday(item.transaction.date)
            tvDate.text = if (isRecent) {
                "Today"
            } else if (DateUtils.isToday(item.transaction.date + DateUtils.DAY_IN_MILLIS)) {
                "Yesterday"
            } else {
                DateUtils.formatDateTime(
                    itemView.context,
                    item.transaction.date,
                    DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_ABBREV_MONTH
                )
            }

            val format = NumberFormat.getCurrencyInstance(Locale.US)
            val formattedAmount = format.format(item.transaction.amount)
            tvAmount.text = itemView.context.getString(R.string.expense_amount_format, formattedAmount)

            // Set icon placeholder logic or load real icon
            // ivIcon.setImageResource(...)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<TransactionWithCategory>() {
            override fun areItemsTheSame(
                oldItem: TransactionWithCategory,
                newItem: TransactionWithCategory
            ): Boolean {
                return oldItem.transaction.id == newItem.transaction.id
            }

            override fun areContentsTheSame(
                oldItem: TransactionWithCategory,
                newItem: TransactionWithCategory
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
