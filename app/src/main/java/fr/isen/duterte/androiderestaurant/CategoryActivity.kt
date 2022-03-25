package fr.isen.duterte.androiderestaurant

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import com.squareup.picasso.StatsSnapshot
import fr.isen.duterte.androiderestaurant.databinding.ActivityCategoryBinding

class CategoryActivity : AppCompatActivity(), CategoryAdapter.OnItemClickListener {
    private lateinit var monRecycler: RecyclerView
    private lateinit var binding: ActivityCategoryBinding
    private lateinit var category: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        category = intent.getStringExtra("categorie").toString()
        binding.titleCategory.text = category


        monRecycler = binding.recycleViewCategory
        monRecycler.layoutManager = LinearLayoutManager(this)
        monRecycler.adapter = CategoryAdapter(arrayListOf(),this)

        postVolley()
    }


    override fun onItemClick(item: APIItems) {
        val intent = Intent(this,ItemViewActivity::class.java)
        intent.putExtra("item",item)
        startActivity(intent)
    }


    private fun postVolley() {
        val queue = Volley.newRequestQueue(this)
        val url = "http://test.api.catering.bluecodegames.com/menu"

        val requestBody = JSONObject()
        requestBody.put("id_shop",1)
        val stringReq = JsonObjectRequest(
            Request.Method.POST, url, requestBody,
            { response ->
                // response
                val strResp = response.toString()
                val dataResult = Gson().fromJson(strResp, APIData::class.java)

                val items = dataResult.data.firstOrNull { it.name_fr == category }?.items ?: arrayListOf()

                monRecycler = binding.recycleViewCategory
                monRecycler.layoutManager = LinearLayoutManager(this)
                monRecycler.adapter = CategoryAdapter(items,this)

                binding.pBar.visibility = View.INVISIBLE

            },
            { error ->
                Log.d("API", "error => $error")
            })
        queue.add(stringReq)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)

        val sharedPreference =  this.getSharedPreferences("PANIER", Context.MODE_PRIVATE)
        val sharedNbItems = sharedPreference.getInt("nbItems", 0)

        var nb = menu.findItem(R.id.nbItems).actionView
        var nbText = nb.findViewById<TextView>(R.id.nbItemsText)
        nbText.text = sharedNbItems.toString()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.cardIcon -> {
                displayCard()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun displayCard() {
        val intent = Intent(this, PanierActivity::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()

        invalidateOptionsMenu()
    }
}