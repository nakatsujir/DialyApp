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
import android.view.View
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
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
            val name = map["name"] ?: ""
            val uid = map["uid"] ?: ""
            val bytes = if (imageString.isNotEmpty()) {
                Base64.decode(imageString, Base64.DEFAULT)
            } else {
                byteArrayOf()
            }

            val diary = Diary(title, text, day, p0.key ?: "", name, uid, bytes)
            mDiaryArrayList.add(diary)
            adapter.notifyDataSetChanged()

            main_message_text.visibility = View.GONE
            main_list_view.visibility = View.VISIBLE
        }

        override fun onChildChanged(p0: DataSnapshot, p1: String?) {
        }

        override fun onChildRemoved(p0: DataSnapshot) {
        }

        override fun onCancelled(p0: DatabaseError) {
        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        main_fab.setOnClickListener { view ->
            val user = FirebaseAuth.getInstance().currentUser
            if (user == null) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            } else {
                PreferenceManager.getDefaultSharedPreferences(this).edit().apply {
                    putBoolean(EDIT_TYPE, isEdit)
                    commit()
                }

                Intent(this, EditActivity::class.java).apply {
                    startActivity(this)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mDatabaseReference = FirebaseDatabase.getInstance().reference

        mAuth = FirebaseAuth.getInstance()
        // ListView
        adapter = DiaryAdapter(this)
        mDiaryArrayList = ArrayList<Diary>()
        main_list_view.adapter = adapter
        adapter.notifyDataSetChanged()

        adapter.setDiaryArrayList(mDiaryArrayList)
        main_list_view.adapter = adapter

        //取得
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val diaryRef = mDatabaseReference.child(DiaryPATH).child(user.uid)
            diaryRef.addChildEventListener(mEventListener)
        }

        main_list_view.setOnItemClickListener { parent, view, position, id ->
            val user = FirebaseAuth.getInstance().currentUser
            if (user == null) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            } else {
                Intent(this, PreviewActivity::class.java).apply {
                    putExtra("diary", mDiaryArrayList[position])
                    startActivity(this)
                }
            }

        }

        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val name = sp.getString(NAME_KEY, "")
        main_user_name_text.text = String.format("${name}さんのDiary")

        main_message_text.visibility = View.VISIBLE
        main_list_view.visibility = View.GONE
    }


}
