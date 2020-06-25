package com.example.picasso

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.image_item.view.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {
    val fileNames = mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val URL = "https://aws.random.cat/meow"
        progressBar.visibility = INVISIBLE
        val context = this
        getPicturesButton.setOnClickListener{
            progressBar.visibility = VISIBLE
            var picturesCounter = 0
            for (i in 1..5) {
                val client = OkHttpClient()
                val request = Request.Builder().url(URL).build()
                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        runOnUiThread {
                            Toast.makeText(context, "Error: $e", Toast.LENGTH_LONG).show()
                            progressBar.visibility = INVISIBLE
                        }
                    }

                    // {"file":"https:\/\/purr.objects-us-east-1.dream.io\/i\/020_-_JQSukVI.gif"}
                    override fun onResponse(call: Call, response: Response) {
                        val json = response.body?.string()
                        val fileName = (JSONObject(json).get("file").toString())
                        fileNames.add(fileName)
                        picturesCounter++
                        if (picturesCounter >=5){
                            runOnUiThread {
                                progressBar.visibility = INVISIBLE
                                recyclerView.adapter = PicturesAdapter(fileNames, context)
                            }
                        }

                    }

                })
            }
        }
    }
}

class PicturesAdapter(val fileNames: List<String>, val context: Context) : RecyclerView.Adapter<PicturesAdapter.PictureViewHolder>(){
    class PictureViewHolder(view: View): RecyclerView.ViewHolder(view){
        fun bind(fileName: String){
            Picasso.get()
                .load(fileName)
                .fit()
                .placeholder(R.drawable.ic_baseline_autorenew_24)
                .error(R.drawable.ic_baseline_block_24)
                .into(itemView.imageView)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictureViewHolder {
        return PictureViewHolder(LayoutInflater.from(context).inflate(R.layout.image_item, parent, false))
    }
    override fun getItemCount() = fileNames.size
    override fun onBindViewHolder(holder: PictureViewHolder, position: Int) {
        holder.bind(fileNames[position])
    }
}