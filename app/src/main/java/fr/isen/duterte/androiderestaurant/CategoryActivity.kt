package fr.isen.duterte.androiderestaurant

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import fr.isen.duterte.androiderestaurant.databinding.ActivityCategoryBinding

class CategoryActivity : AppCompatActivity(), CategoryAdapter.OnItemClickListener {
    private lateinit var monRecycler: RecyclerView
    private lateinit var items: ArrayList<Item>
    private lateinit var binding: ActivityCategoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.titleCategory.text = intent.getStringExtra("categorie").toString()

        postVolley()

        items = fillItems()

        monRecycler = binding.recycleViewCategory
        monRecycler.layoutManager = LinearLayoutManager(this)
        monRecycler.adapter = CategoryAdapter(items,this)



    }

    fun fillItems() : ArrayList<Item> {
        val items = ArrayList<Item>()
        items.add(Item("item1",getString(R.string.image)))
        items.add(Item("item2",getString(R.string.image)))
        return items
    }

    override fun onItemClick(item: Item) {
        val intent = Intent(this,ItemViewActivity::class.java)
        Toast.makeText(this@CategoryActivity, "you clicked on ${item.name_fr}",Toast.LENGTH_SHORT).show()
        intent.putExtra("item",item)
        startActivity(intent)
    }


    fun postVolley() {
        val queue = Volley.newRequestQueue(this)
        val url = "http://test.api.catering.bluecodegames.com/menu"

        val requestBody = JSONObject()
        requestBody.put("id_shop",1)
        val stringReq = JsonObjectRequest(
            Request.Method.POST, url, requestBody,
            { response ->
                // response
                var strResp = response.toString()
                var item = Gson().fromJson(strResp, APIData::class.java)
                Log.d("API",item.data[0].name_fr)

                Log.d("API", strResp)




            },
            { error ->
                Log.d("API", "error => $error")
            })
        queue.add(stringReq)
    }


}