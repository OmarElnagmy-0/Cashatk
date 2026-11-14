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
import com.example.cashatk.data.Category
import com.example.cashatk.data.Transaction
import java.text.NumberFormat
import java.util.Locale

class TransactionAdapter :
    ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder>(DiffCallback) {

    private var categoryMap: Map<Int, Category> = emptyMap()

    fun submitData(transactions: List<Transaction>, categories: List<Category>) {
        categoryMap = categories.associateBy { it.id }
        submitList(transactions)
    }

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        private val tvDate: TextView = itemView.findViewById(R.id.tv_date)
        private val tvAmount: TextView = itemView.findViewById(R.id.tv_amount)
        private val ivIcon: ImageView = itemView.findViewById(R.id.iv_icon)

        fun bind(item: Transaction) {
            val category = categoryMap[item.categoryId]
            tvTitle.text = item.description?.takeIf { it.isNotBlank() } ?: category?.name ?: "Expense"

            val isRecent = DateUtils.isToday(item.date)
            tvDate.text = if (isRecent) {
                "Today"
            } else if (DateUtils.isToday(item.date + DateUtils.DAY_IN_MILLIS)) {
                "Yesterday"
            } else {
                DateUtils.formatDateTime(
                    itemView.context,
                    item.date,
                    DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_ABBREV_MONTH
                )
            }

            val format = NumberFormat.getCurrencyInstance(Locale.US)
            val formattedAmount = format.format(item.amount)
            tvAmount.text = itemView.context.getString(R.string.expense_amount_format, formattedAmount)

            ivIcon.setImageResource(
                CategoryIconHelper.getDrawableRes(itemView.context, category?.icon)
            )
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
        private val DiffCallback = object : DiffUtil.ItemCallback<Transaction>() {
            override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
                return oldItem == newItem
            }
        }
    }
}
