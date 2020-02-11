package by.bsuir.jsonexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import java.net.URL

/**
 * Finds all jobs for Java developers in London at GitHub jobs and displaying info (company + location + title) in Recycler view
 * */

class MainActivity : AppCompatActivity() {

    var jobsList : MutableList<Job> = mutableListOf<Job>()

    override fun onCreate(savedInstanceState: Bundle?) {

        /*only for debug: allow to start work with network in main thread*/
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
            .detectDiskReads()
            .detectDiskWrites()
            .detectNetwork()   // or .detectAll() for all detectable problems
            .penaltyLog()
            .build())
        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
            .detectLeakedSqlLiteObjects()
            .detectLeakedClosableObjects()
            .penaltyLog()
            .penaltyDeath()
            .build())

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*getting json data from GitHub API*/
        val jsonStringFromNet: String = URL("https://jobs.github.com/positions.json?&description=Java&location=London").readText()
        /*convert it to array where one element is one json object*/
        var jsonArray = JSONArray(jsonStringFromNet)
        for(jsonIndex in 0 until jsonArray.length()){
            /*getting data by tag*/
            val company = jsonArray.getJSONObject(jsonIndex).getString("company")
            val location = jsonArray.getJSONObject(jsonIndex).getString("location")
            val title = jsonArray.getJSONObject(jsonIndex).getString("title")
            /*adding data to Kotlin data class which described in Job.kt file*/
            val job  = Job (companyName = company, location = location, jobTitle = title)
            /*adding element to list*/
            jobsList.add(job)
        }

        /*set recycler view*/
        val recyclerView = findViewById<RecyclerView>(R.id.RecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = MyRecyclerViewAdapter()
        /*line between elements*/
        recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
    }

    /*ViewHolder for custom recycle view*/
    inner class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val company: TextView = itemView.findViewById<TextView>(R.id.company)
        val location : TextView  = itemView.findViewById<TextView>(R.id.location)
        val jobTitle : TextView = itemView.findViewById<TextView>(R.id.jobTitle)
    }

    /*Adapter for custom recycle view*/
    inner class MyRecyclerViewAdapter : RecyclerView.Adapter<MyViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_view_item, parent, false)
           return MyViewHolder(view)
        }

        override fun getItemCount(): Int {
            return jobsList.size
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            /*set each element in view*/
            val listItem = jobsList[position]
            holder.company.text = listItem.companyName
            holder.jobTitle.text = listItem.jobTitle
            holder.location.text = listItem.location
        }

    }
}
