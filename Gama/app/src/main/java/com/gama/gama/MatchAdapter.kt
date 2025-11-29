package com.gama.gama

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.gama.gama.models.Match
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MatchAdapter(
    private var matches: List<Match>,
    private val onItemClick: (Match) -> Unit,
    private val onJoinClick: (Match) -> Unit
) : RecyclerView.Adapter<MatchAdapter.MatchViewHolder>() {

    inner class MatchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvMatchTitle)
        private val tvGameType: TextView = itemView.findViewById(R.id.tvGameType)
        private val tvEntryFee: TextView = itemView.findViewById(R.id.tvEntryFee)
        private val tvPrizePool: TextView = itemView.findViewById(R.id.tvPrizePool)
        private val tvParticipants: TextView = itemView.findViewById(R.id.tvParticipants)
        private val btnJoin: Button = itemView.findViewById(R.id.btnJoin)

        fun bind(match: Match) {
            tvTitle.text = match.title
            tvGameType.text = match.gameType
            tvEntryFee.text = "Entry: ৳${match.entryFee}"
            tvPrizePool.text = "Prize: ৳${match.prizePool}"
            tvParticipants.text = "${match.currentParticipants}/${match.maxParticipants} Players"

            // Show join button only if match is not full
            if (match.currentParticipants < match.maxParticipants) {
                btnJoin.visibility = View.VISIBLE
                btnJoin.setOnClickListener {
                    onJoinClick(match)
                }
            } else {
                btnJoin.visibility = View.GONE
            }

            // Make entire item clickable
            itemView.setOnClickListener {
                onItemClick(match)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_match, parent, false)
        return MatchViewHolder(view)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        holder.bind(matches[position])
    }

    override fun getItemCount(): Int = matches.size

    fun updateMatches(newMatches: List<Match>) {
        matches = newMatches
        notifyDataSetChanged()
    }
}