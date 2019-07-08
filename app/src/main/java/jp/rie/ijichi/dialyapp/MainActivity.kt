package jp.rie.ijichi.dialyapp

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.view.accessibility.AccessibilityEventCompat.setAction
import android.support.v7.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.FirebaseApp

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        const val DAY_TEXT = "day_text"
        const val TITLE_TEXT = "title_text"
    }

    private lateinit var adapter: DiaryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(main_toolbar)

        main_fab.setOnClickListener { view ->
            val intent = Intent(this,EditActivity::class.java)
            startActivity(intent)
        }

        adapter = DiaryAdapter(this)

        val diaryList = mutableListOf("aaa","bbb","ccc")
        adapter.diarylist = diaryList
        main_list_view.adapter = adapter
        adapter.notifyDataSetChanged()

        main_list_view.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(this,PreviewActivity::class.java)

            val dayText = diaryList[position]
            val titleText = diaryList[position]

            intent.putExtra(DAY_TEXT,dayText)
            intent.putExtra(TITLE_TEXT,titleText)
            startActivity(intent)
        }


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
