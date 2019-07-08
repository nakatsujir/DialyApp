package jp.rie.ijichi.dialyapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class DiaryAdapter(context: Context):BaseAdapter() {

    var diarylist = mutableListOf<String>()

    private val layoutInflater = LayoutInflater.from(context)


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view:View = convertView ?: layoutInflater.inflate(R.layout.diary_list,parent,false)

        val listImage = view.findViewById<ImageView>(R.id.list_image)
        val dayText = view.findViewById<TextView>(R.id.list_day_text)
        val titleText = view.findViewById<TextView>(R.id.list_title_text)

        // 後でdiaryクラスから情報を取得するように変更する
        dayText.text = diarylist[position]
        return view
    }

    override fun getItem(position: Int): Any {
        return diarylist[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return diarylist.size
    }
}