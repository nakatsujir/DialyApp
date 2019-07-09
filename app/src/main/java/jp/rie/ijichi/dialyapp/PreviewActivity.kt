package jp.rie.ijichi.dialyapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_preview.*

class PreviewActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabaseReference: DatabaseReference

    private lateinit var mDialy:Diary

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)

        mDatabaseReference = FirebaseDatabase.getInstance().reference

        mAuth = FirebaseAuth.getInstance()

        val extras = intent.extras
        mDialy = extras.get("diary") as Diary

        preview_day_text.text = mDialy.day
        preview_title_edit.text = mDialy.title
        preview_text_edit.text = mDialy.text





    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_preview,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.menu_preview_edit -> {
                val intent = Intent(this,EditActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
