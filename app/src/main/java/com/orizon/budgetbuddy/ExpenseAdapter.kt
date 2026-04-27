package com.orizon.budgetbuddy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ExpenseAdapter(private var expenses: List<Expense>) :
    RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    class ExpenseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDescription: TextView = view.findViewById(R.id.tvItemDescription)
        val tvAmount: TextView = view.findViewById(R.id.tvItemAmount)
        val tvCategory: TextView = view.findViewById(R.id.tvItemCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]
        holder.tvDescription.text = expense.description
        holder.tvCategory.text = expense.category
        holder.tvAmount.text = "R${String.format("%.2f", expense.amount)}"
    }

    override fun getItemCount() = expenses.size

    fun updateData(newExpenses: List<Expense>) {
        this.expenses = newExpenses
        notifyDataSetChanged()
    }

    fun getExpenseAt(position: Int): Expense = expenses[position]
}