package org.sopt21.qnd.LockScreen

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import me.imid.swipebacklayout.lib.SwipeBackLayout
import me.imid.swipebacklayout.lib.app.SwipeBackActivity
import org.sopt21.qnd.Application.ApplicationController
import org.sopt21.qnd.Application.ApplicationController.Companion.context
import org.sopt21.qnd.Data.SurveyFinishData
import org.sopt21.qnd.Data.SurveyFinishResult
import org.sopt21.qnd.Network.NetworkService
import org.sopt21.qnd.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class LockScreenActivity : SwipeBackActivity() {

    private var lock_date: TextView? = null
    private var lock_time: TextView? = null
    private var lock_ap: TextView? = null
    private var mTimer: Timer? = null

    private var lock_survey_a: LinearLayout? = null
    private var lock_survey_b: LinearLayout? = null
    private var lock_survey_c: LinearLayout? = null

    private var lock_survey_a_title: TextView? = null
    private var lock_survey_b_title: TextView? = null
    private var lock_survey_c_title: TextView? = null
    private var lock_survey_a_explain: TextView? = null
    private var lock_survey_b_explain: TextView? = null
    private var lock_survey_c_explain: TextView? = null
    private var lock_survey_a_check: Button? = null
    private var lock_survey_b_check: Button? = null
    private var lock_survey_c_check: Button? = null

    private var lock_survey_a_op1: TextView? = null
    private var lock_survey_a_op2: TextView? = null
    private var lock_survey_a_op3: TextView? = null
    private var lock_survey_a_op4: TextView? = null

    private var lock_survey_b_op1: CircleImageView? = null
    private var lock_survey_b_op2: CircleImageView? = null
    private var lock_survey_b_img1: ImageView? = null
    private var lock_survey_b_img2: ImageView? = null
    private var lock_survey_b_txt1: TextView? = null
    private var lock_survey_b_txt2: TextView? = null

    private var lock_survey_c_op1: Button? = null
    private var lock_survey_c_op2: Button? = null

    internal var swipeBackLayout: SwipeBackLayout? = null

    internal lateinit var token: String

    internal lateinit var networkService: NetworkService

    internal lateinit var responseData: LockResult.LockResponseData

    private val mHandler = Handler()

    private val mUpdateTimeTask = Runnable {
        val nowTime = System.currentTimeMillis()
        val date = Date(nowTime)
        val formatTime = SimpleDateFormat("hh:mm")
        val formatDate = SimpleDateFormat("yyyy.MM.dd")
        val formatAP = SimpleDateFormat("a")
        val dateString = formatDate.format(date)
        val timeString = formatTime.format(nowTime)
        val apString = formatAP.format(date)
        lock_time!!.text = timeString
        lock_date!!.text = dateString
        lock_ap!!.text = apString
    }

    lateinit var loginInfo: SharedPreferences
    var user_id: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock_screen)

        loginInfo = this.getSharedPreferences("loginSetting", 0)

        Log.v("lock_user_id", loginInfo.toString())

        user_id = loginInfo.getString("user_id", "")//user_id: token값
        Log.v("lock_user_id", user_id)

        swipeBackLayout = getSwipeBackLayout()

        lock_date = findViewById(R.id.lock_date) as TextView
        lock_time = findViewById(R.id.lock_time) as TextView
        lock_ap = findViewById(R.id.lock_ap) as TextView

        lock_survey_a = findViewById(R.id.lock_survey_a) as LinearLayout
        lock_survey_a_title = findViewById(R.id.lock_survey_a_title) as TextView
        lock_survey_a_explain = findViewById(R.id.lock_survey_a_explain) as TextView
        lock_survey_a_op1 = findViewById(R.id.lock_survey_a_op1) as TextView
        lock_survey_a_op2 = findViewById(R.id.lock_survey_a_op2) as TextView
        lock_survey_a_op3 = findViewById(R.id.lock_survey_a_op3) as TextView
        lock_survey_a_op4 = findViewById(R.id.lock_survey_a_op4) as TextView
        lock_survey_a_check = findViewById(R.id.lock_survey_a_check) as Button

        lock_survey_b = findViewById(R.id.lock_survey_b) as LinearLayout
        lock_survey_b_title = findViewById(R.id.lock_survey_b_title) as TextView
        lock_survey_b_explain = findViewById(R.id.lock_survey_b_explain) as TextView
        lock_survey_b_op1 = findViewById(R.id.lock_survey_b_op1) as CircleImageView
        lock_survey_b_txt1 = findViewById(R.id.lock_survey_b_txt1) as TextView
        lock_survey_b_img1 = findViewById(R.id.lock_survey_b_img1) as ImageView
        lock_survey_b_op2 = findViewById(R.id.lock_survey_b_op2) as CircleImageView
        lock_survey_b_txt2 = findViewById(R.id.lock_survey_b_txt2) as TextView
        lock_survey_b_img2 = findViewById(R.id.lock_survey_b_img2) as ImageView
        lock_survey_b_check = findViewById(R.id.lock_survey_b_check) as Button

        lock_survey_c = findViewById(R.id.lock_survey_c) as LinearLayout
        lock_survey_c_title = findViewById(R.id.lock_survey_c_title) as TextView
        lock_survey_c_explain = findViewById(R.id.lock_survey_c_explain) as TextView
        lock_survey_c_op1 = findViewById(R.id.lock_survey_c_op1) as Button
        lock_survey_c_op2 = findViewById(R.id.lock_survey_c_op2) as Button
        lock_survey_c_check = findViewById(R.id.lock_survey_c_check) as Button

        lockScreenNetwork() // 실제통신시작

        // aType 설문
        lock_survey_a_op1!!.setOnClickListener {
            if (responseData.q_count == 2) {
                atypeBackground2(lock_survey_a_op1, lock_survey_a_op2)
            } else if (responseData.q_count == 3) {
                atypeBackground3(lock_survey_a_op1, lock_survey_a_op2, lock_survey_a_op3)
            } else if (responseData.q_count == 4) {
                atypeBackground4(lock_survey_a_op1, lock_survey_a_op2, lock_survey_a_op3, lock_survey_a_op4)
            }
        }

        lock_survey_a_op2!!.setOnClickListener {
            if (responseData.q_count == 2) {
                atypeBackground2(lock_survey_a_op2, lock_survey_a_op1)
            } else if (responseData.q_count == 3) {
                atypeBackground3(lock_survey_a_op2, lock_survey_a_op1, lock_survey_a_op3)
            } else if (responseData.q_count == 4) {
                atypeBackground4(lock_survey_a_op2, lock_survey_a_op1, lock_survey_a_op3, lock_survey_a_op4)
            }
        }

        lock_survey_a_op3!!.setOnClickListener {
            if (responseData.q_count == 3) {
                atypeBackground3(lock_survey_a_op3, lock_survey_a_op1, lock_survey_a_op2)
            } else if (responseData.q_count == 4) {
                atypeBackground4(lock_survey_a_op3, lock_survey_a_op1, lock_survey_a_op2, lock_survey_a_op4)
            }
        }

        lock_survey_a_op4!!.setOnClickListener {
            if (responseData.q_count == 4) {
                atypeBackground4(lock_survey_a_op4, lock_survey_a_op1, lock_survey_a_op2, lock_survey_a_op3)
            }
        }

        lock_survey_a_check!!.setOnClickListener {
            if (lock_survey_a_op1!!.background.constantState == resources.getDrawable(R.drawable.survey_atype_box1).constantState &&
                    lock_survey_a_op2!!.background.constantState == resources.getDrawable(R.drawable.survey_atype_box1).constantState &&
                    lock_survey_a_op3!!.background.constantState == resources.getDrawable(R.drawable.survey_atype_box1).constantState &&
                    lock_survey_a_op4!!.background.constantState == resources.getDrawable(R.drawable.survey_atype_box1).constantState) {
                Toast.makeText(this@LockScreenActivity, "선택이 되지 않았습니다.", Toast.LENGTH_SHORT).show()
            } else {
            }
        }

        // bType 설문
        lock_survey_b_op1!!.setOnClickListener { btypeBackground(lock_survey_b_img1, lock_survey_b_img2) }

        lock_survey_b_op2!!.setOnClickListener { btypeBackground(lock_survey_b_img2, lock_survey_b_img1) }

        lock_survey_b_check!!.setOnClickListener {
            if (lock_survey_b_op1!!.background.constantState == resources.getDrawable(R.drawable.survey_btype_circle1).constantState && lock_survey_b_op2!!.background.constantState == resources.getDrawable(R.drawable.survey_btype_circle1).constantState) {
                Toast.makeText(this@LockScreenActivity, "선택이 되지 않았습니다.", Toast.LENGTH_SHORT).show()
            } else {
            }
        }

        // cType 설문
        lock_survey_c_op1!!.setOnClickListener { ctypeBackground1(lock_survey_c_op1, lock_survey_c_op2) }

        lock_survey_c_op2!!.setOnClickListener { ctypeBackground2(lock_survey_c_op2, lock_survey_c_op1) }

        lock_survey_c_check!!.setOnClickListener {
            if (lock_survey_c_op1!!.background.constantState == resources.getDrawable(R.drawable.survey_ctype_circle1).constantState && lock_survey_c_op2!!.background.constantState == resources.getDrawable(R.drawable.survey_ctype_circle2).constantState) {
                Toast.makeText(this@LockScreenActivity, "선택이 되지 않았습니다.", Toast.LENGTH_SHORT).show()
            } else {
            }
        }


        val timerTask = MainTimerTask()
        mTimer = Timer()
        mTimer!!.schedule(timerTask, 500, 1000)

        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED   // 안드로이드 잠금화면보다 위에 띄움
                or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)  // 안드로이드 기본 잠금화면을 없앰

        swipeBackLayout!!.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT)

        swipeBackLayout!!.addSwipeListener(object : SwipeBackLayout.SwipeListener {
            override fun onScrollStateChange(state: Int, scrollPercent: Float) {
                // 스크롤 될 때
            }

            override fun onEdgeTouch(edgeFlag: Int) {
                // 설정된 모서리를 터치 했을 때
            }

            override fun onScrollOverThreshold() {
                // 창이 닫힐 정도로 스와이프 되었을 때
                Toast.makeText(applicationContext, "잠금해제", Toast.LENGTH_LONG).show()
            }
        })
    }

    internal inner class MainTimerTask : TimerTask() {
        override fun run() {
            mHandler.post(mUpdateTimeTask)
        }
    }

    override fun onBackPressed() {   // 뒤로가기 제어

    }

    // atype 보기 선택시 테두리 색깔 변경
    fun atypeBackground2(textView1: TextView?, textView2: TextView?) {
        if (responseData.duple == 0) {       //   중복체크 불가능
            if (textView1!!.background.constantState == textView2!!.background.constantState) {
                textView1.setBackgroundResource(R.drawable.survey_atype_box1)
            } else if (textView1.background.constantState == getResources().getDrawable(R.drawable.survey_atype_box2) &&
                    textView2.background.constantState == getResources().getDrawable(R.drawable.survey_atype_box1)) {
                textView1.setBackgroundResource(R.drawable.survey_atype_box1)
                textView2.setBackgroundResource(R.drawable.survey_atype_box2)
            } else if (textView1.background.constantState == resources.getDrawable(R.drawable.survey_atype_box1).constantState) {
                textView1.setBackgroundResource(R.drawable.survey_atype_box2)
            }
        } else if (responseData.duple == 1) {        // 중복체크 가능
            if (textView1!!.background.constantState == resources.getDrawable(R.drawable.survey_atype_box2).constantState) {
                textView1.setBackgroundResource(R.drawable.survey_atype_box1)
            } else if (textView1.background.constantState == resources.getDrawable(R.drawable.survey_atype_box1).constantState) {
                textView1.setBackgroundResource(R.drawable.survey_atype_box2)
            }
        }
    }

    fun atypeBackground3(textView1: TextView?, textView2: TextView?, textView3: TextView?) {
        if (responseData.duple == 0) {       //   중복체크 불가능
            if (textView1!!.background.constantState == textView2!!.background.constantState && textView1.background.constantState == textView3!!.background.constantState) {
                textView1.setBackgroundResource(R.drawable.survey_atype_box1)
            } else if (textView2.background.constantState == resources.getDrawable(R.drawable.survey_atype_box1).constantState) {
                textView1.setBackgroundResource(R.drawable.survey_atype_box1)
                textView2.setBackgroundResource(R.drawable.survey_atype_box2)
            } else if (textView3!!.background.constantState == resources.getDrawable(R.drawable.survey_atype_box1).constantState) {
                textView1.setBackgroundResource(R.drawable.survey_atype_box1)
                textView3.setBackgroundResource(R.drawable.survey_atype_box2)
            } else if (textView1.background.constantState == resources.getDrawable(R.drawable.survey_atype_box1).constantState) {
                textView1.setBackgroundResource(R.drawable.survey_atype_box2)
            }else {}
        } else if (responseData.duple == 1) {        // 중복체크 가능
            if (textView1!!.background.constantState == resources.getDrawable(R.drawable.survey_atype_box2).constantState) {
                textView1.setBackgroundResource(R.drawable.survey_atype_box1)
            } else if (textView1.background.constantState == resources.getDrawable(R.drawable.survey_atype_box1).constantState) {
                textView1.setBackgroundResource(R.drawable.survey_atype_box2)
            }
        }
    }

    fun atypeBackground4(textView1: TextView?, textView2: TextView?, textView3: TextView?, textView4: TextView?) {
        if (responseData.duple == 0) {       //   중복체크 불가능
            if (textView1!!.background.constantState == textView2!!.background.constantState &&
                    textView1.background.constantState == textView3!!.background.constantState &&
                    textView1.background.constantState == textView2.background.constantState) {
                textView1.setBackgroundResource(R.drawable.survey_atype_box1)
            } else if (textView2.background.constantState == resources.getDrawable(R.drawable.survey_atype_box1).constantState) {
                textView1.setBackgroundResource(R.drawable.survey_atype_box1)
                textView2.setBackgroundResource(R.drawable.survey_atype_box2)
            } else if (textView3!!.background.constantState == resources.getDrawable(R.drawable.survey_atype_box1).constantState) {
                textView1.setBackgroundResource(R.drawable.survey_atype_box1)
                textView3.setBackgroundResource(R.drawable.survey_atype_box2)
            } else if (textView4!!.background.constantState == resources.getDrawable(R.drawable.survey_atype_box1).constantState) {
                textView1.setBackgroundResource(R.drawable.survey_atype_box1)
                textView4.setBackgroundResource(R.drawable.survey_atype_box2)
            } else if (textView1.background.constantState == resources.getDrawable(R.drawable.survey_atype_box1).constantState) {
                textView1.setBackgroundResource(R.drawable.survey_atype_box2)
            }
        } else if (responseData.duple == 1) {        // 중복체크 가능
            if (textView1!!.background.constantState == resources.getDrawable(R.drawable.survey_atype_box2).constantState) {
                textView1.setBackgroundResource(R.drawable.survey_atype_box1)
            } else if (textView1.background.constantState == resources.getDrawable(R.drawable.survey_atype_box1).constantState) {
                textView1.setBackgroundResource(R.drawable.survey_atype_box2)
            }
        }
    }

    // btype 보기 선택시 테두리 색깔 변경
    fun btypeBackground(imageView1: ImageView?, imageView2: ImageView?) {
        if (imageView1!!.background.constantState == imageView2!!.background.constantState) {
            if (imageView1.background.constantState == resources.getDrawable(R.drawable.btype_circle1).constantState) {
                imageView1.setBackgroundResource(R.drawable.btype_circle2)
            } else if (imageView1.background.constantState == resources.getDrawable(R.drawable.btype_circle2).constantState) {
                imageView1.setBackgroundResource(R.drawable.btype_circle1)
            }
        } else if (imageView1.background.constantState == resources.getDrawable(R.drawable.btype_circle2).constantState) {
            imageView1.setBackgroundResource(R.drawable.btype_circle1)
        } else if (imageView1.background.constantState == resources.getDrawable(R.drawable.btype_circle1).constantState && imageView2.background.constantState == resources.getDrawable(R.drawable.btype_circle2).constantState) {
            imageView1.setBackgroundResource(R.drawable.btype_circle2)
            imageView2.setBackgroundResource(R.drawable.btype_circle1)
        }
    }

    // ctype 보기 선택시 테두리 색깔 변경
    fun ctypeBackground1(button1: Button?, button2: Button?) {
        if (button2!!.background.constantState == resources.getDrawable(R.drawable.survey_ctype_circle2).constantState) {
            if (button1!!.background.constantState == resources.getDrawable(R.drawable.survey_ctype_circle1).constantState) {
                button1.setBackgroundResource(R.drawable.survey_ctype_circle3)
            } else if (button1.background.constantState == resources.getDrawable(R.drawable.survey_ctype_circle3).constantState) {
                button1.setBackgroundResource(R.drawable.survey_ctype_circle1)
            }
        } else if (button1!!.background.constantState == resources.getDrawable(R.drawable.survey_ctype_circle3).constantState) {
            button1.setBackgroundResource(R.drawable.survey_ctype_circle1)
        } else if (button1.background.constantState == resources.getDrawable(R.drawable.survey_ctype_circle1).constantState && button2.background.constantState == resources.getDrawable(R.drawable.survey_ctype_circle4).constantState) {
            button1.setBackgroundResource(R.drawable.survey_ctype_circle3)
            button2.setBackgroundResource(R.drawable.survey_ctype_circle2)
        }
    }

    fun ctypeBackground2(button1: Button?, button2: Button?) {
        if (button2!!.background.constantState == resources.getDrawable(R.drawable.survey_ctype_circle1).constantState) {
            if (button1!!.background.constantState == resources.getDrawable(R.drawable.survey_ctype_circle2).constantState) {
                button1.setBackgroundResource(R.drawable.survey_ctype_circle4)
            } else if (button1.background.constantState == resources.getDrawable(R.drawable.survey_ctype_circle4).constantState) {
                button1.setBackgroundResource(R.drawable.survey_ctype_circle2)
            }
        } else if (button1!!.background.constantState == resources.getDrawable(R.drawable.survey_ctype_circle4).constantState) {
            button1.setBackgroundResource(R.drawable.survey_ctype_circle2)
        } else if (button1.background.constantState == resources.getDrawable(R.drawable.survey_ctype_circle2).constantState && button2.background.constantState == resources.getDrawable(R.drawable.survey_ctype_circle3).constantState) {
            button1.setBackgroundResource(R.drawable.survey_ctype_circle4)
            button2.setBackgroundResource(R.drawable.survey_ctype_circle1)
        }
    }

    //나머지는 알아서 구성

    fun lockScreenNetwork() {
        networkService = ApplicationController.instance!!.networkService!!

        val courseWriteTitle = networkService.getLockResult(user_id)
        courseWriteTitle.enqueue(object : Callback<LockResult> {
            override fun onResponse(call: Call<LockResult>, response: Response<LockResult>) { // 응답받았을때
                if (response.body().status == "Success") { // 리스폰스 성공적일때
                    responseData = response.body().data
                    Log.v("taehyung_lock_start", responseData.servay_type.toString())
                    if (responseData.servay_type == 0) {
                        lock_survey_a!!.visibility = View.VISIBLE
                        lock_survey_b!!.visibility = View.GONE
                        lock_survey_c!!.visibility = View.GONE

                        var select_choice1_noduple = -1 //다중선택 아닐때 보낼 값
                        var select_choice2_noduple = -1 //다중선택 아닐때 보낼 값
                        var select_choice3_noduple = -1 //다중선택 아닐때 보낼 값
                        var select_choice4_noduple = -1 //다중선택 아닐때 보낼 값

                        lock_survey_a!!.visibility = View.VISIBLE
                        if (responseData.q_count == 2) {
                            lock_survey_a_op1!!.text = responseData.servay_q1
                            lock_survey_a_op2!!.text = responseData.servay_q2

                            lock_survey_a_op1!!.setOnClickListener {
                                lock_survey_a_op1!!.setBackgroundResource(R.drawable.survey_atype_box1)
                                lock_survey_a_op2!!.setBackgroundResource(R.drawable.survey_atype_box2)

                                select_choice1_noduple = 1
                                select_choice2_noduple = 0

                            }
                            lock_survey_a_op2!!.setOnClickListener {
                                lock_survey_a_op1!!.setBackgroundResource(R.drawable.survey_atype_box2)
                                lock_survey_a_op2!!.setBackgroundResource(R.drawable.survey_atype_box1)

                                select_choice1_noduple = 0
                                select_choice2_noduple = 1
                            }
                        } else if (responseData.q_count == 3) {
                            lock_survey_a_op1!!.text = responseData.servay_q1
                            lock_survey_a_op2!!.text = responseData.servay_q2
                            lock_survey_a_op3!!.visibility = View.VISIBLE
                            lock_survey_a_op3!!.text = responseData.servay_q3

                            lock_survey_a_op1!!.setOnClickListener {
                                lock_survey_a_op1!!.setBackgroundResource(R.drawable.survey_atype_box1)
                                lock_survey_a_op2!!.setBackgroundResource(R.drawable.survey_atype_box2)
                                lock_survey_a_op3!!.setBackgroundResource(R.drawable.survey_atype_box2)

                                select_choice1_noduple = 1
                                select_choice2_noduple = 0
                                select_choice3_noduple = 0
                            }
                            lock_survey_a_op2!!.setOnClickListener {
                                lock_survey_a_op1!!.setBackgroundResource(R.drawable.survey_atype_box2)
                                lock_survey_a_op2!!.setBackgroundResource(R.drawable.survey_atype_box1)
                                lock_survey_a_op3!!.setBackgroundResource(R.drawable.survey_atype_box2)

                                select_choice1_noduple = 0
                                select_choice2_noduple = 1
                                select_choice3_noduple = 0
                            }
                            lock_survey_a_op3!!.setOnClickListener {
                                lock_survey_a_op1!!.setBackgroundResource(R.drawable.survey_atype_box2)
                                lock_survey_a_op2!!.setBackgroundResource(R.drawable.survey_atype_box2)
                                lock_survey_a_op3!!.setBackgroundResource(R.drawable.survey_atype_box1)

                                select_choice1_noduple = 0
                                select_choice2_noduple = 0
                                select_choice3_noduple = 1
                            }
                        } else if (responseData.q_count == 4) {
                            lock_survey_a_op1!!.text = responseData.servay_q1
                            lock_survey_a_op2!!.text = responseData.servay_q2
                            lock_survey_a_op3!!.visibility = View.VISIBLE
                            lock_survey_a_op3!!.text = responseData.servay_q3
                            lock_survey_a_op4!!.visibility = View.VISIBLE
                            lock_survey_a_op4!!.text = responseData.servay_q4

                            lock_survey_a_op1!!.setOnClickListener {
                                lock_survey_a_op1!!.setBackgroundResource(R.drawable.survey_atype_box1)
                                lock_survey_a_op2!!.setBackgroundResource(R.drawable.survey_atype_box2)
                                lock_survey_a_op3!!.setBackgroundResource(R.drawable.survey_atype_box2)
                                lock_survey_a_op4!!.setBackgroundResource(R.drawable.survey_atype_box2)

                                select_choice1_noduple = 1
                                select_choice2_noduple = 0
                                select_choice3_noduple = 0
                                select_choice4_noduple = 0
                            }
                            lock_survey_a_op2!!.setOnClickListener {
                                lock_survey_a_op1!!.setBackgroundResource(R.drawable.survey_atype_box2)
                                lock_survey_a_op2!!.setBackgroundResource(R.drawable.survey_atype_box1)
                                lock_survey_a_op3!!.setBackgroundResource(R.drawable.survey_atype_box2)
                                lock_survey_a_op4!!.setBackgroundResource(R.drawable.survey_atype_box2)

                                select_choice1_noduple = 0
                                select_choice2_noduple = 1
                                select_choice3_noduple = 0
                                select_choice4_noduple = 0
                            }
                            lock_survey_a_op3!!.setOnClickListener {
                                lock_survey_a_op1!!.setBackgroundResource(R.drawable.survey_atype_box2)
                                lock_survey_a_op2!!.setBackgroundResource(R.drawable.survey_atype_box2)
                                lock_survey_a_op3!!.setBackgroundResource(R.drawable.survey_atype_box1)
                                lock_survey_a_op4!!.setBackgroundResource(R.drawable.survey_atype_box2)

                                select_choice1_noduple = 0
                                select_choice2_noduple = 0
                                select_choice3_noduple = 1
                                select_choice4_noduple = 0
                            }
                            lock_survey_a_op4!!.setOnClickListener {
                                lock_survey_a_op1!!.setBackgroundResource(R.drawable.survey_atype_box2)
                                lock_survey_a_op2!!.setBackgroundResource(R.drawable.survey_atype_box2)
                                lock_survey_a_op3!!.setBackgroundResource(R.drawable.survey_atype_box2)
                                lock_survey_a_op4!!.setBackgroundResource(R.drawable.survey_atype_box1)

                                select_choice1_noduple = 0
                                select_choice2_noduple = 0
                                select_choice3_noduple = 0
                                select_choice4_noduple = 1
                            }
                        }

                        //완료버튼 통신
                        var lock_survey_a_check: Button = findViewById(R.id.lock_survey_a_check) as Button
                        lock_survey_a_check.setOnClickListener {
                            var surveyFinishData = SurveyFinishData()
                            surveyFinishData.servay_id = responseData.servay_id
                            surveyFinishData.selection_start_select_time = "no data"
                            surveyFinishData.selection_choice1 = select_choice1_noduple
                            surveyFinishData.selection_choice2 = select_choice2_noduple
                            surveyFinishData.selection_choice3 = select_choice3_noduple
                            surveyFinishData.selection_choice4 = select_choice4_noduple

                            surveyFinish(user_id, surveyFinishData)
                            finish()
                        }

                    } else if (responseData.servay_type == 1) {
                        lock_survey_a!!.visibility = View.GONE
                        lock_survey_b!!.visibility = View.VISIBLE
                        lock_survey_c!!.visibility = View.GONE

                        var select_choice1 = 0 //다중선택 아닐때 보낼 값
                        var select_choice2 = 0 //다중선택 아닐때 보낼 값

                        lock_survey_b!!.visibility = View.VISIBLE
                        if (responseData.servay_a_img == null || responseData.servay_b_img == null) {
                            lock_survey_b_txt1!!.text = responseData.servay_a_txt
                            lock_survey_b_txt2!!.text = responseData.servay_b_txt
                            Glide.with(this@LockScreenActivity).load(R.drawable.survey_ctype_circle1).into(lock_survey_b_op1!!)
                            Glide.with(this@LockScreenActivity).load(R.drawable.survey_ctype_circle2).into(lock_survey_b_op2!!)
                        } else if (responseData.servay_a_img != null || responseData.servay_b_img != null) {
                            lock_survey_b_txt1!!.text = responseData.servay_a_txt
                            lock_survey_b_txt2!!.text = responseData.servay_b_txt
                            Glide.with(this@LockScreenActivity).load(responseData.servay_a_img).into(lock_survey_b_op1!!)
                            Glide.with(this@LockScreenActivity).load(responseData.servay_b_img).into(lock_survey_b_op2!!)
                        }

                        lock_survey_b_op1!!.setOnClickListener {
                            lock_survey_b_img1!!.setImageResource(R.drawable.btype_circle2)
                            lock_survey_b_img2!!.setImageResource(R.drawable.btype_circle1)

                            select_choice1 = 1
                            select_choice2 = 0
                        }

                        lock_survey_b_op2!!.setOnClickListener {
                            lock_survey_b_img2!!.setImageResource(R.drawable.btype_circle2)
                            lock_survey_b_img1!!.setImageResource(R.drawable.btype_circle1)

                            select_choice1 = 0
                            select_choice2 = 1
                        }

                        var lock_survey_b_check = findViewById(R.id.lock_survey_b_check) as Button
                        Log.v("taehyung_lock_b", lock_survey_b_check.toString())
                        Log.v("taehyung_lock_b", responseData.servay_id.toString() + " / " + responseData.servay_id + " / " + select_choice1 + " / " + select_choice2)

                        lock_survey_b_check.setOnClickListener {
                            Log.v("taehyung_lock_b", responseData.servay_id.toString() + " / " + responseData.servay_id + " / " + select_choice1 + " / " + select_choice2)

                            var surveyFinishData = SurveyFinishData()
                            surveyFinishData.servay_id = responseData.servay_id
                            surveyFinishData.selection_start_select_time = "no data"
                            surveyFinishData.selection_choice1 = select_choice1
                            surveyFinishData.selection_choice2 = select_choice2
                            surveyFinish(user_id, surveyFinishData)

                            finish()
                        }




                    } else if (responseData.servay_type == 2) {
                        lock_survey_a!!.visibility = View.GONE
                        lock_survey_b!!.visibility = View.GONE
                        lock_survey_c!!.visibility = View.VISIBLE

                        var select_choice1 = 0 //다중선택 아닐때 보낼 값
                        var select_choice2 = 0 //다중선택 아닐때 보낼 값

                        lock_survey_c!!.visibility = View.VISIBLE
                        lock_survey_c_title!!.text = responseData.servay_title
                        lock_survey_c_explain!!.text = responseData.servay_explanation

                        lock_survey_c_op1!!.setOnClickListener {
                            lock_survey_c_op1!!.setBackgroundResource(R.drawable.survey_ctype_circle3)
                            lock_survey_c_op2!!.setBackgroundResource(R.drawable.survey_ctype_circle2)

                            select_choice1 = 1
                            select_choice2 = 0
                        }

                        lock_survey_c_op2!!.setOnClickListener {
                            lock_survey_c_op2!!.setBackgroundResource(R.drawable.survey_ctype_circle4)
                            lock_survey_c_op1!!.setBackgroundResource(R.drawable.survey_ctype_circle1)

                            select_choice1 = 0
                            select_choice2 = 1
                        }

                        var lock_survey_c_check = findViewById(R.id.lock_survey_c_check) as Button
                        Log.v("taehyung_lock_c", lock_survey_c_check.toString())
                        Log.v("taehyung_lock_c", responseData.servay_id.toString() + " / " + responseData.servay_id + " / " + select_choice1 + " / " + select_choice2)

                        lock_survey_c_check.setOnClickListener {
                            var surveyFinishData = SurveyFinishData()

                            Log.v("taehyung_lock_c", responseData.servay_id.toString() + " / " + responseData.servay_id + " / " + select_choice1 + " / " + select_choice2)

                            surveyFinishData.servay_id = responseData.servay_id
                            surveyFinishData.selection_start_select_time = "no data"
                            surveyFinishData.selection_choice1 = select_choice1
                            surveyFinishData.selection_choice2 = select_choice2

                            surveyFinish(user_id, surveyFinishData)
                            finish()
                        }
                    }

                } else { //리스폰스 데이터가 제대로 안들어왔을 때

                }
            }

            override fun onFailure(call: Call<LockResult>, t: Throwable) {
                //검색시 통신 실패
            }
        })
    }


    //설문 참여 완료 통신
    fun surveyFinish(token: String, surveyFinishData: SurveyFinishData) { //코인 정보 받아와서 안씀 삭제해도 되는거..?
        val finishResponse = networkService!!.surveyFinish(user_id, surveyFinishData)
        Log.v("taehyung_lock", surveyFinishData.servay_id.toString())

        finishResponse.enqueue(object : Callback<SurveyFinishResult> {
            override fun onResponse(call: Call<SurveyFinishResult>, response: Response<SurveyFinishResult>) =
                    if (response.isSuccessful) {
                        Toast.makeText(context, "통신 성공", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "통신 실패", Toast.LENGTH_SHORT).show()
                    }

            override fun onFailure(call: Call<SurveyFinishResult>, t: Throwable) {
                Toast.makeText(context, "네트워크가 원할하지 않습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
