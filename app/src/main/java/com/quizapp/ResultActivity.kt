package com.quizapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_result.*
import java.lang.StringBuilder

class ResultActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth

    private var mCorrectAnswers: Int = 0
    private var mTotal: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser

        var database = FirebaseDatabase.getInstance().reference

        // TODO (STEP 6: Hide the status bar and get the details from intent and set it to the UI. And also add a click event to the finish button.)
        // START
        // Hide the status bar.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        tv_name.text = currentUser?.displayName
        tv_id.text = currentUser.uid

        val totalQuestions = intent.getIntExtra(Constants.TOTAL_QUESTIONS, 0)
        val correctAnswers = intent.getIntExtra(Constants.CORRECT_ANSWERS, 0)

        mCorrectAnswers = correctAnswers
        mTotal = totalQuestions

        tv_score.text = "Your Score is $correctAnswers out of $totalQuestions."
        tv_score2.text = "$correctAnswers"

        var gamerId = tv_id.text.toString()
        var gamerName = tv_name.text.toString()
        var gamerResult = tv_score2.text.toString()
        database.child(gamerId).setValue(Gamer(gamerName, gamerResult))

        var getdata = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var sb = StringBuilder()
                for (i in snapshot.children) {
                    val gamerName1 = i.child ("gamerName").getValue()
                    val gamerResult1 = i.child ("gamerResult").getValue()
                    sb.append("$gamerName1 $gamerResult1\n")
                }
                textView.setText(sb)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }
        database.addValueEventListener(getdata)
        database.addListenerForSingleValueEvent(getdata)


        btn_finish.setOnClickListener {
            mAuth.signOut()
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }

        btn_statistic.setOnClickListener {
            val intent = Intent(this@ResultActivity, Statistic::class.java)
            val totalQuestions = intent.getIntExtra(Constants.TOTAL_QUESTIONS, mTotal)
            val correctAnswers = intent.getIntExtra(Constants.CORRECT_ANSWERS, mCorrectAnswers)
            intent.putExtra(Constants.TOTAL_QUESTIONS, totalQuestions)
            intent.putExtra(Constants.CORRECT_ANSWERS, correctAnswers)
            startActivity(intent)
        }

        }

        // END
    }



class SignInActivity {

}
