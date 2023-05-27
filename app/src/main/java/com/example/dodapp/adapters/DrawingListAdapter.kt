package com.example.dodapp.adapters

import android.graphics.BitmapFactory
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.dodapp.databinding.ItemDrawingListBinding
import com.example.dodapp.models.Drawing
import java.text.SimpleDateFormat
import java.util.*

class DrawingListAdapter(
    val listener : (Drawing) -> Unit
) : RecyclerView.Adapter<DrawingListAdapter.ViewHolder>(){

    inner class ViewHolder(val binding : ItemDrawingListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item : Drawing){
            binding.apply {
                val imageBitmap = BitmapFactory.decodeByteArray(item.imageBytes, 0, item.imageBytes.size)
                ivThumbNail.setImageBitmap(imageBitmap)
                tvDrawingName.text = item.name
                tvMarkers.text = item.markers.size.toString()
                tvAdditionTime.text = formatTimeInSocialFormat(item.creationTime)
            }
            binding.root.setOnClickListener{
                listener(item)
            }
        }
    }

    private val differCallBack = object : DiffUtil.ItemCallback<Drawing>() {
        override fun areItemsTheSame(oldItem: Drawing, newItem: Drawing): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Drawing, newItem: Drawing): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this,differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemDrawingListBinding.inflate(LayoutInflater.from(parent.context))
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.topMargin = 20
        layoutParams.marginStart = 10
        layoutParams.marginEnd = 10
        view.root.layoutParams = layoutParams
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun formatTimeWithAmPm(timeInMillis: Long): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        return dateFormat.format(calendar.time)
    }

    fun formatTimeInSocialFormat(creationTime: Long): String {
        val currentTime = System.currentTimeMillis()
        val timeDifference = currentTime - creationTime

        return when {
            timeDifference < DateUtils.MINUTE_IN_MILLIS -> {
                "Just now"
            }
            timeDifference < DateUtils.HOUR_IN_MILLIS -> {
                val minutes = (timeDifference / DateUtils.MINUTE_IN_MILLIS).toInt()
                "$minutes minutes ago"
            }
            timeDifference < DateUtils.DAY_IN_MILLIS -> {
                val hours = (timeDifference / DateUtils.HOUR_IN_MILLIS).toInt()
                "$hours hours ago"
            }
            timeDifference < DateUtils.WEEK_IN_MILLIS -> {
                val days = (timeDifference / DateUtils.DAY_IN_MILLIS).toInt()
                "$days days ago"
            }
            else -> {
                DateUtils.getRelativeTimeSpanString(creationTime, currentTime, DateUtils.WEEK_IN_MILLIS).toString()
            }
        }
    }
}