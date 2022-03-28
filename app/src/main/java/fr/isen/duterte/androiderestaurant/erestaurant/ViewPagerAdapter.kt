package fr.isen.duterte.androiderestaurant.erestaurant

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.squareup.picasso.Picasso
import fr.isen.duterte.androiderestaurant.R

class ViewPagerAdapter(var list: ArrayList<String>, var ctx: Context) : PagerAdapter() {

    lateinit var layoutInflater:LayoutInflater
    lateinit var context:Context

    override fun getCount(): Int {
        return list.size
    }


    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view.equals(`object`)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = LayoutInflater.from(ctx)

        var view = layoutInflater.inflate(R.layout.activity_view_pager_adapter,container,false)

        val img = view.findViewById<ImageView>(R.id.imageViewMain)

        Picasso.get().load(list[position].ifEmpty { null }).fit().centerCrop()
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_background)
            .into(img);

        container.addView(view,0)
        return view
    }



    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}