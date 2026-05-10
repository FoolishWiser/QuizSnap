package com.example.quizhelper.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizhelper.R

data class QuizBankInfo(
    val name: String,
    val questionCount: Int,
    val createTime: Long
)

class QuizBankAdapter(
    private val quizBanks: List<QuizBankInfo>,
    private val onItemClick: (QuizBankInfo) -> Unit,
    private val onDeleteClick: (QuizBankInfo) -> Unit
) : RecyclerView.Adapter<QuizBankAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvBankName)
        val tvCount: TextView = itemView.findViewById(R.id.tvBankCount)
        val tvTime: TextView = itemView.findViewById(R.id.tvBankTime)
        val btnDelete: View = itemView.findViewById(R.id.btnDeleteBank)
        val cardView: CardView = itemView.findViewById(R.id.cardQuizBank)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_quiz_bank, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bank = quizBanks[position]
        holder.tvName.text = bank.name
        holder.tvCount.text = "${bank.questionCount} 道题"
        holder.tvTime.text = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
            .format(java.util.Date(bank.createTime))
        holder.cardView.setOnClickListener { onItemClick(bank) }
        holder.btnDelete.setOnClickListener { onDeleteClick(bank) }
    }

    override fun getItemCount(): Int = quizBanks.size
}