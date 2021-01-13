package com.udacity.asteroidradar.main

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.databinding.AsteroidListItemBinding

class AsteroidAdapter(private val onclickListener: OnclickListener) :
        ListAdapter<Asteroid, AsteroidAdapter.AsteroidViewHolder>(DiffCallback) {

    class AsteroidViewHolder(private var binding: AsteroidListItemBinding) :
            RecyclerView.ViewHolder(binding.root) {
        fun bind(asteroid: Asteroid) {
            binding.asteroidItem = asteroid
            binding.executePendingBindings()
        }
    }

    class OnclickListener(val clickListener: (asteroid: Asteroid) -> Unit) {
        fun onClick(asteroid: Asteroid) = clickListener(asteroid)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Asteroid>() {
        override fun areItemsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): AsteroidAdapter.AsteroidViewHolder {
        var view = AsteroidListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AsteroidViewHolder(view)
    }

    override fun onBindViewHolder(holder: AsteroidAdapter.AsteroidViewHolder, position: Int) {
        val asteroid = getItem(position)
        holder.itemView.setOnClickListener {
            onclickListener.onClick(asteroid)
        }
        holder.bind(asteroid)
    }
}

