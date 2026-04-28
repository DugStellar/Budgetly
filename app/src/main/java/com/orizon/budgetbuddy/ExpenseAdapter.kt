package com.orizon.budgetbuddy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ExpenseAdapter(private val onDeleteClick: (Expense) -> Unit) : ListAdapter<Expense, ExpenseAdapter.ExpenseViewHolder>(ExpenseDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view, onDeleteClick)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = getItem(position)
        holder.bind(expense)
    }

    class ExpenseViewHolder(itemView: View, private val onDeleteClick: (Expense) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val tvDescription: TextView = itemView.findViewById(R.id.tvItemDescription)
        private val tvCategory: TextView = itemView.findViewById(R.id.tvItemCategory)
        private val tvAmount: TextView = itemView.findViewById(R.id.tvItemAmount)
        private val tvDate: TextView = itemView.findViewById(R.id.tvItemDate)
        private val btnDelete: android.widget.ImageButton = itemView.findViewById(R.id.btnDeleteItem)
        private val btnViewPhoto: android.widget.ImageButton = itemView.findViewById(R.id.btnViewPhoto)
        private val viewCategoryColor: View = itemView.findViewById(R.id.viewCategoryColor)

        fun bind(expense: Expense) {
            tvDescription.text = expense.description
            tvCategory.text = expense.category
            
            val timeStr = if (expense.startTime.isNotEmpty()) " (${expense.startTime} - ${expense.endTime})" else ""
            tvDate.text = "${expense.date}$timeStr"

            val formattedAmount = "-R${String.format("%.2f", expense.amount)}"
            tvAmount.text = formattedAmount

            btnDelete.setOnClickListener { onDeleteClick(expense) }
            
            if (expense.photoUri != null) {
                btnViewPhoto.visibility = View.VISIBLE
                btnViewPhoto.setOnClickListener {
                    showPhotoDialog(expense.photoUri)
                }
            } else {
                btnViewPhoto.visibility = View.GONE
            }
            
            // Set color based on category
            val color = when(expense.category) {
                "Groceries" -> "#4285F4"
                "Transport" -> "#FBBC04"
                "Rent" -> "#EA4335"
                "Utilities" -> "#34A853"
                else -> "#1A73E8"
            }
            viewCategoryColor.setBackgroundColor(android.graphics.Color.parseColor(color))
        }

        private fun showPhotoDialog(uriString: String) {
            val context = itemView.context
            try {
                val builder = android.app.AlertDialog.Builder(context)
                val imageView = android.widget.ImageView(context)
                val uri = android.net.Uri.parse(uriString)
                
                // Add padding to image view
                val padding = (16 * context.resources.displayMetrics.density).toInt()
                imageView.setPadding(padding, padding, padding, padding)
                imageView.adjustViewBounds = true
                
                imageView.setImageURI(uri)
                builder.setView(imageView)
                builder.setPositiveButton("Close") { d, _ -> d.dismiss() }
                builder.show()
            } catch (e: Exception) {
                android.widget.Toast.makeText(context, "Error loading photo", android.widget.Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    class ExpenseDiffCallback : DiffUtil.ItemCallback<Expense>() {
        override fun areItemsTheSame(oldItem: Expense, newItem: Expense): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Expense, newItem: Expense): Boolean {
            return oldItem == newItem
        }
    }
}