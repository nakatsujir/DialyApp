package jp.rie.ijichi.dialyapp

import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_preview.*

class PreviewActivity : AppCompatActivity() {


    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabaseReference: DatabaseReference

    private lateinit var mDiary: Diary

    private lateinit var day: String
    private lateinit var title: String
    private lateinit var text: String

    private val isEdit = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)

        mDatabaseReference = FirebaseDatabase.getInstance().reference

        mAuth = FirebaseAuth.getInstance()

        val extras = intent.extras
        mDiary = extras.get("diary") as Diary

        mDiary.let {
            day = it.day
            title = it.title
            text = it.text
            preview_day_text.text = day
            preview_title_edit.text = title
            preview_text_edit.text = text
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_preview, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_preview_edit -> {

                PreferenceManager.getDefaultSharedPreferences(this).edit().apply {
                    putBoolean(EDIT_TYPE, isEdit)
                    commit()
                }

                Intent(this, EditActivity::class.java).apply {
                    putExtra(KEY_DAY, day)
                    putExtra(KEY_TITLE, title)
                    putExtra(KEY_TEXT, text)
                    putExtra("diary",mDiary)
                    startActivity(this)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
