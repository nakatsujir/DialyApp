package jp.rie.ijichi.diary

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    private lateinit var mCreateAccountListener: OnCompleteListener<AuthResult>
    private lateinit var mLoginListener: OnCompleteListener<AuthResult>
    private lateinit var mDatabaseReference: DatabaseReference

    private var mIsCreateAccount = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()
        mDatabaseReference = FirebaseDatabase.getInstance().reference

        title = "ログイン"

        // 新規作成のリスナー
        mCreateAccountListener = OnCompleteListener { task ->
            if (task.isSuccessful) {
                val email = login_email_text.text.toString()
                val password = login_password_edit.text.toString()
                login(email, password)
            } else {
                Toast.makeText(this, "アカウント作成に失敗しました", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            }
        }

        //　ログインのリスナー
        mLoginListener = OnCompleteListener { task ->
            if (task.isSuccessful) {
                // 成功した場合
                val user = mAuth.currentUser
                val userRef = mDatabaseReference.child(UsersPATH).child(user!!.uid)

                if (mIsCreateAccount) {
                    // アカウント作成の時は表示名をFirebaseに保存する
                    val name = login_name_text.text.toString()
                    val data = HashMap<String, String>()
                    data["name"] = name
                    userRef.setValue(data)

                    // 名前をPrefarenceに保存する
                    saveName(name)
                } else {
                    userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(p0: DataSnapshot) {
                            val data = p0.value as Map<*, *>?
                            if (data != null) {
                                saveName(data["name"] as String)
                            } else {
                                Log.d("data", "data_null")
                            }
                        }

                        override fun onCancelled(p0: DatabaseError) {
                        }
                    })
                }
                progressBar.visibility = View.GONE

                finish()

            } else {
                // 失敗した場合
                Toast.makeText(this, "ログインに失敗しました", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            }

        }


        login_create_button.setOnClickListener {
            closeKeyboard()
            val email = login_email_text.text.toString()
            val password = login_password_edit.text.toString()
            val name = login_name_text.text.toString()

            if (email.length != 0 && password.length >= 6 && name.length != 0) {
                mIsCreateAccount = true
                createAccount(email, password)
            } else {
                Toast.makeText(this, "正しく入力してください", Toast.LENGTH_SHORT).show()
            }
        }

        login_button.setOnClickListener {
            closeKeyboard()
            val email = login_email_text.text.toString()
            val password = login_password_edit.text.toString()
            if (email.length != 0 && password.length >= 6) {
                mIsCreateAccount = false
                login(email, password)
            } else {
                Toast.makeText(this, "正しく入力してください", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun saveName(name: String) {
        // Preferenceに名前保存する
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = sp.edit()
        editor.putString(NAME_KEY, name)
        editor.commit()
    }

    private fun login(email: String, password: String) {
        progressBar.visibility = View.VISIBLE
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(mLoginListener)
    }

    private fun createAccount(email: String, password: String) {
        progressBar.visibility = View.VISIBLE
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(mCreateAccountListener)
    }

    private fun closeKeyboard() {
        // キーボード閉じる
        val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        im.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }


}
