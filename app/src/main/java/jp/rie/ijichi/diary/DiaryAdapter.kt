package jp.rie.ijichi.diary

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class DiaryAdapter(context: Context) : BaseAdapter() {


    private val layoutInflater = LayoutInflater.from(context)

    private var diarylist = ArrayList<Diary>()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: layoutInflater.inflate(R.layout.diary_list, parent, false)

        val dayText = view.findViewById<TextView>(R.id.list_day_text) as TextView
        dayText.text = diarylist[position].day

        val titleText = view.findViewById<TextView>(R.id.list_title_text) as TextView
        titleText.text = diarylist[position].title

        val listImage = diarylist[position].imageBytes
        if (listImage.isNotEmpty()) {
            val image = BitmapFactory.decodeByteArray(listImage, 0, listImage.size).copy(Bitmap.Config.ARGB_8888, true)
            val imageView = view.findViewById<View>(R.id.list_image) as ImageView
            imageView.setImageBitmap(image)
        }
        return view
    }

    override fun getItem(position: Int): Any {
        return diarylist[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return diarylist.size
    }

    fun setDiaryArrayList(diaryArrayList:ArrayList<Diary>){
        diarylist = diaryArrayList

    }
}