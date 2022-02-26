package com.example.pencarianumkm.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.pencarianumkm.R
import com.example.pencarianumkm.model.ModelMain
import com.example.pencarianumkm.utils.OnItemClickCallback
import kotlinx.android.synthetic.main.list_item_main.view.*

class MainAdapter(private val mContext: Context, private val items: List<ModelMain>) : RecyclerView.Adapter<MainAdapter.ViewHolder>(){

    private var Rating = 0.0
    private var onItemClickCallback: OnItemClickCallback? = null
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback?){
        this.onItemClickCallback = onItemClickCallback
    }

    //Class Holder
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cvListMain: CardView
        var imgUmkm: ImageView
        var tvNameUmkm: TextView
        var tvAddress: TextView
        var tvRating: TextView
        var ratingUmkm: RatingBar

        init {
            cvListMain = itemView.cvListMain
            imgUmkm = itemView.img_UMKM
            tvNameUmkm = itemView.tv_Name_UMKM
            tvAddress = itemView.tvAddress
            tvRating = itemView.tvRating
            ratingUmkm = itemView.ratting_UMKM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_main, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = items[position]

        Rating = data.aggregateRating

        Glide.with(mContext)
            .load(data.thumbUmkm)
            .transform(CenterCrop(), RoundedCorners(25))
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.imgUmkm)

        val newValue = Rating.toFloat()
        holder.ratingUmkm.numStars = 5
        holder.ratingUmkm.stepSize = 0.5.toFloat()
        holder.ratingUmkm.rating = newValue

        holder.tvNameUmkm.text = data.nameUmkm
        holder.tvAddress.text = data.addressUmkm
        holder.tvRating.text = " | " + data.aggregateRating + " " + data.ratingText
        holder.cvListMain.setOnClickListener {
            onItemClickCallback?.onItemMainClicked(data)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}