package com.example.quizhelper.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.quizhelper.R
import com.example.quizhelper.model.Question
import com.example.quizhelper.utils.QuestionMatcher

class QuestionAdapter(
    private val onItemClick: (Question) -> Unit = {}
) : ListAdapter<Question, QuestionAdapter.QuestionViewHolder>(DiffCallback()) {

    class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvType: TextView = itemView.findViewById(R.id.tvType)
        val tvContent: TextView = itemView.findViewById(R.id.tvContent)
        val tvOptions: TextView = itemView.findViewById(R.id.tvOptions)
        val tvAnswer: TextView = itemView.findViewById(R.id.tvAnswer)
        val tvEditHint: TextView = itemView.findViewById(R.id.tvEditHint)
        val cardView: com.google.android.material.card.MaterialCardView = itemView.findViewById(R.id.cardQuestion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_question, parent, false)
        return QuestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        val question = getItem(position)

        holder.tvType.text = QuestionMatcher.getTypeDisplayText(question.type)
        holder.tvContent.text = question.content

        // 显示选项
        if (question.options.isNotEmpty()) {
            val options = question.options.split("|")
            val optionsText = options.mapIndexed { index, option ->
                "${'A' + index}: $option"
            }.joinToString("\n")
            holder.tvOptions.text = optionsText
            holder.tvOptions.visibility = View.VISIBLE
        } else {
            holder.tvOptions.visibility = View.GONE
        }

        // 显示答案
        holder.tvAnswer.text = "答案: ${QuestionMatcher.getAnswerDisplayText(question)}"

        // 点击编辑
        holder.cardView.setOnClickListener {
            onItemClick(question)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Question>() {
        override fun areItemsTheSame(oldItem: Question, newItem: Question): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Question, newItem: Question): Boolean {
            return oldItem == newItem
        }
    }
}
