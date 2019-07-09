package jp.rie.ijichi.dialyapp

import android.arch.lifecycle.Transformations.map
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat.startActivity
import android.support.v4.view.accessibility.AccessibilityEventCompat.setAction
import android.support.v7.app.AppCompatActivity;
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabaseReference: DatabaseReference

    private lateinit var adapter: DiaryAdapter

    private lateinit var mDiaryArrayList: ArrayList<Diary>

    private val isEdit = false

    private val mEventListener = object : ChildEventListener {
        override fun onChildAdded(p0: DataSnapshot, p1: String?) {
            val map = p0.value as Map<String, String>
            val title = map["title"] ?: ""
            val text = map["text"] ?: ""
            val day = map["day"] ?: ""
            val imageString = map["image"] ?: ""
            val bytes = if (imageString.isNotEmpty()) {
                Base64.decode(imageString, Base64.DEFAULT)
            } else {
                byteArrayOf()
            }

            val diary = Diary(title, text, day, bytes)
            mDiaryArrayList.add(diary)
            adapter.notifyDataSetChanged()
        }

        override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onChildRemoved(p0: DataSnapshot) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onCancelled(p0: DatabaseError) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }





    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mDatabaseReference = FirebaseDatabase.getInstance().reference

        mAuth = FirebaseAuth.getInstance()

        main_fab.setOnClickListener { view ->
            PreferenceManager.getDefaultSharedPreferences(this).edit().apply {
                putBoolean(EDIT_TYPE, isEdit)
                commit()
            }

            Intent(this, EditActivity::class.java).apply {
                startActivity(this)
            }

        }

        // ListView
        adapter = DiaryAdapter(this)
        mDiaryArrayList = ArrayList<Diary>()
        main_list_view.adapter = adapter
        adapter.notifyDataSetChanged()

        adapter.setDiaryArrayList(mDiaryArrayList)
        main_list_view.adapter = adapter

        //取得
        val diaryRef = mDatabaseReference.child(DiaryPATH)
        diaryRef.addChildEventListener(mEventListener)

        main_list_view.setOnItemClickListener { parent, view, position, id ->
            Intent(this, PreviewActivity::class.java).apply {
                putExtra("diary", mDiaryArrayList[position])
                startActivity(this)
            }
        }


    }

    override fun onResume() {
        //取得
        super.onResume()
    }


}
