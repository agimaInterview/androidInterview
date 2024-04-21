package com.agima.interviewproject.ui.dashboard

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.agima.interviewproject.R
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Url

class UniversitiesFragment : Fragment() {
    var adapter: RcAdapter? = null
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
        adapter = RcAdapter(emptyList())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        val retrofit = Retrofit.Builder().baseUrl("http://universities.hipolabs.com").addConverterFactory(
            GsonConverterFactory.create()).build()
        val api = retrofit.create<ApiUniversities>()

        GlobalScope.launch(Dispatchers.IO) {
            val response = api.getItems()
            withContext(Dispatchers.Main){
                adapter?.setList(response)
            }
        }
    }
}

interface ApiUniversities {
    @GET("/search?name=Middle")
    suspend fun getItems(): List<University>
}

class University(
    val country: String,
    val domain: String,
    val name: String,
    val web_page: String
)

class RcAdapter(private var dataList: List<University>) : RecyclerView.Adapter<RcAdapter.SimpleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.university_item, parent, false)
        return SimpleViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
        val dataItem = dataList[position]
        holder.bind(dataItem)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class SimpleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(dataItem: University) {
            itemView.findViewById<TextView>(R.id.textView).text = dataItem.name
            itemView.findViewById<TextView>(R.id.textView2).text = dataItem.country
            itemView.findViewById<TextView>(R.id.textView3).text = dataItem.web_page
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list:List<University>){
        dataList = list
        notifyDataSetChanged()
    }
}
