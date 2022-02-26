package com.example.pencarianumkm

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.pencarianumkm.adapter.HighlightsAdapter
import com.example.pencarianumkm.adapter.ReviewAdapter
import com.example.pencarianumkm.model.ModelHighlights
import com.example.pencarianumkm.model.ModelMain
import com.example.pencarianumkm.model.ModelReview
import kotlinx.android.synthetic.main.activity_detail_resto.*
import kotlinx.android.synthetic.main.activity_detail_resto.toolbar
import kotlinx.android.synthetic.main.list_item_main_horizontal.*
import kotlinx.android.synthetic.main.toolbar_main.*
import org.json.JSONException
import org.json.JSONObject

class DetailRestoActivity : AppCompatActivity() {

    private var mProgressBar: ProgressDialog? = null
    private var highlightsAdapter: HighlightsAdapter? = null
    private var reviewAdapter: ReviewAdapter? = null
    private var modelHighlights: MutableList<ModelHighlights> = ArrayList()
    private var modelReview: MutableList<ModelReview> = ArrayList()

    var RatingUmkm = 0.0
    var IdUmkm: String? = null
    var ImageCover: String? = null
    var Title: String? = null
    var Rating: String? = null
    var UmkmName: String? = null
    var modelMain: ModelMain? = null

    @SuppressLint("Assert", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_resto)
        
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        if (Build.VERSION.SDK_INT >= 24){
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = resources.getColor(R.color.colorPrimary)
        }

        mProgressBar = ProgressDialog(this)
        mProgressBar?.setTitle("Mohon Tunggu")
        mProgressBar?.setCancelable(false)
        mProgressBar?.setMessage("Sedang Menampilkan Data...")

        toolbar.setTitle("")
        setSupportActionBar(toolbar)
        assert(supportActionBar != null)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        modelMain = intent.getSerializableExtra(DETAIL_RESTO) as ModelMain
        if (modelMain != null){
            IdUmkm = modelMain?.idUmkm
            ImageCover = modelMain?.thumbUmkm
            RatingUmkm = modelMain!!.aggregateRating
            Title = modelMain?.nameUmkm
            Rating = modelMain?. ratingText
            UmkmName = modelMain?.nameUmkm

            tvTitle.setText(Title)
            tv_UMKM_Name.setText(UmkmName)
            tvRating.setText("$RatingUmkm | $Rating")
            tvTitle.setSelected(true)
            val newValue = RatingUmkm.toFloat()

            ratingUMKM.setNumStars(5)
            ratingUMKM.setStepSize(0.5.toFloat())
            ratingUMKM.setRating(newValue)

            Glide.with(this)
                .load(ImageCover)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgCover)

            //Method Get Highlight
            showRecyclerViewList()

            //Method Get Detail
            getDetailUmkm()

            //Method Get Review
            getReviewUmkm()
        }
    }

    private fun showRecyclerViewList(){
        highlightsAdapter = HighlightsAdapter(modelHighlights)
        reviewAdapter = ReviewAdapter(this, modelReview)

        rvHighlights.setLayoutManager(LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false))
        rvHighlights.setHasFixedSize(true)
        rvHighlights.setAdapter(highlightsAdapter)

        rv_Review_UMKM.setLayoutManager(LinearLayoutManager(this))
        rv_Review_UMKM.setHasFixedSize(true)
        rv_Review_UMKM.setAdapter(reviewAdapter)
    }

    private fun getDetailUmkm(){
        mProgressBar?.show()
        AndroidNetworking.get(ApiEndopoint.BASEURL + ApiEndopoint.DetailUmkm + IdUmkm)
            .addHeaders("user-key", "81af0a0dade877c83f647a66163e6654")
            .setPriority(Priority.HIGH)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener{
                @SuppressLint("SetTextI18n")
                override fun onResponse(response: JSONObject) {
                    try{
                        mProgressBar?.dismiss()
                        val jsonArrayOne = response.getJSONArray("highlight")

                        for (i in 0 until jsonArrayOne.length()){
                            val dataApi = ModelHighlights()
                            val highlights = jsonArrayOne[i].toString()
                            dataApi.highlights = highlights
                            modelHighlights.add(dataApi)
                        }

                        val jsonObjectData = response.getJSONObject("location")
                        val jsonArrayTwo = response.getJSONArray("establishment")

                        for (x in 0 until jsonArrayTwo.length()){
                            val establishment = jsonArrayTwo[x].toString()
                            tvEstablishment.text = establishment
                        }

                        val AverageCost = response.getString("average_cost_for_two")
                        val PriceRange = response.getString("price_range")
                        val Currency = response.getString("currency")
                        val Timings = response.getString("timings")
                        val LocalityVerbose = jsonObjectData.getString("locality_verbose")
                        val Address = jsonObjectData.getString("address")
                        val Telepon = response.getString("phone_numbers")
                        val Website = response.getString("url")
                        val Latitude = jsonObjectData.getDouble("latitude")
                        val Longitude = jsonObjectData.getDouble("longitude")

                        tvLocalityVerbose.text = LocalityVerbose
                        tvAverageCost.text = "$Currency $AverageCost / $PriceRange orang"
                        tvAddress.text = Address
                        tvOpenTime.text = Timings

                        llRoute.setOnClickListener {
                            val intent = Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?daddr=$Latitude,$Longitude"))
                            startActivity(intent)
                        }

                        llTelpon.setOnClickListener {
                            val intent: Intent
                            intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$Telepon"))
                            startActivity(intent)
                        }

                        llWebsite.setOnClickListener {
                            val intent: Intent
                            intent = Intent(Intent.ACTION_VIEW, Uri.parse(Website))
                            startActivity(intent)
                        }
                        highlightsAdapter?.notifyDataSetChanged()
                    }
                    catch (e: JSONException){
                        e.printStackTrace()
                        Toast.makeText(this@DetailRestoActivity, "Gagal Menampilkan data!", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onError(anError: ANError?) {
                    mProgressBar?.dismiss()
                    Toast.makeText(this@DetailRestoActivity, "Tidak Ada Jaringan Internet", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun getReviewUmkm(){
        mProgressBar?.show()
        AndroidNetworking.get(ApiEndopoint.BASEURL + ApiEndopoint.ReviewUmkm + IdUmkm)
            .addHeaders("user-key", "81af0a0dade877c83f647a66163e6654")
            .setPriority(Priority.HIGH)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener{
                override fun onResponse(response: JSONObject) {
                    try {
                        mProgressBar?.dismiss()
                        val jsonArray = response.getJSONArray("user_review")

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val dataApi = ModelReview()
                            val jsonObjectDataOne = jsonObject.getJSONObject("review")
                            val jsonObjectDataTwo = jsonObjectDataOne.getJSONObject("user")
                            dataApi.ratingReview = jsonObjectDataOne.getDouble("rating")
                            dataApi.reviewText = jsonObjectDataOne.getString("review_text")
                            dataApi.reviewTime = jsonObjectDataOne.getString("review_time_friendly")
                            dataApi.nameUser = jsonObjectDataTwo.getString("name")
                            dataApi.profileImage = jsonObjectDataTwo.getString("profile_image")
                            modelReview.add(dataApi)
                        }
                        reviewAdapter?.notifyDataSetChanged()
                    }
                    catch (e: JSONException){
                        e.printStackTrace()
                        Toast.makeText(this@DetailRestoActivity, "gagal Menampilkan Data!", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onError(anError: ANError?) {
                    mProgressBar?.dismiss()
                    Toast.makeText(this@DetailRestoActivity, "Tidak Ada Jaringan Internet!", Toast.LENGTH_SHORT).show()
                }

            })
    }

    companion object{
        const val DETAIL_RESTO = "detailResto"
        fun setWindowFlag(activity: Activity, bits: Int, on: Boolean){
            val window =  activity.window
            val layoutParams = window.attributes
            if (on){
                layoutParams.flags = layoutParams.flags or bits
            }
            else{
                layoutParams.flags = layoutParams.flags and bits.inv()
            }
            window.attributes = layoutParams
        }
    }
}