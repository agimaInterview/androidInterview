package com.agima.interviewproject.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.agima.interviewproject.R
import com.agima.interviewproject.ui.dashboard.ApiUniversities
import com.agima.interviewproject.ui.dashboard.RcAdapter
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Query

class FragmentNews : Fragment() {

    var adapter: RcAdapterNews? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        adapter = RcAdapterNews(null, requireContext())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        val retrofit = Retrofit.Builder().baseUrl("https://newsapi.org").addConverterFactory(
            GsonConverterFactory.create()).build()
        val api = retrofit.create<NewsApi>()

        GlobalScope.launch(Dispatchers.IO) {
            val response = api.getNews()
            withContext(Dispatchers.Main){
                adapter?.setData(response)
            }
        }
    }
}

interface NewsApi {
    @GET("https://newsapi.org/v2/everything?apiKey=8f0047a52975437e90a58918c4fa2dc2")
    suspend fun getNews(@Query("q") query: String= "bitcoin"): News
}

data class News(
    val status: String? = "",
    val totalResults: Int? = 0,
    val articles: ArrayList<NewsItem> = ArrayList<NewsItem>(),
)

data class NewsItem(
    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val source: Source,
    val title: String?,
    val url: String?,
    val urlToImage: String?
)

data class Source(
    val id: String?,
    val name: String?
)

class RcAdapterNews(private var data: News?, val context: Context) : RecyclerView.Adapter<RcAdapterNews.SimpleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return SimpleViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
        val dataItem = data?.articles?.get(position)
        holder.bind(dataItem, context)
    }

    override fun getItemCount(): Int {
        return data?.articles?.size?:0
    }

    class SimpleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(dataItem: NewsItem?, context: Context) {
            Glide.with(context)
                .load(dataItem?.urlToImage)
                .into(itemView.findViewById(R.id.imageView2))
            itemView.findViewById<TextView>(R.id.title).text = dataItem?.title
            itemView.findViewById<TextView>(R.id.description).text = dataItem?.content

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: News){
        this.data = data
        notifyDataSetChanged()
    }
}