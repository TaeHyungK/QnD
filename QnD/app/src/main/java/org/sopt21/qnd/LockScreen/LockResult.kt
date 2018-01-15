package org.sopt21.qnd.LockScreen

/**
 * Created by rkswl on 2018-01-09.
 */

class LockResult {
    var status: String? = null
    var msg: String? = null
    var data = LockResponseData()

    inner class LockResponseData {
        var servay_id: Int = 0   // 서베이 고유 id
        var servay_type: Int = 0 // 서베이 타입(0: 선택지 형, 1: 이미지 형, 2: 찬반형)
        var servay_title: String? = null    // 서베이 제목
        var servay_explanation: String? = null  // 설문지 설명

        // 타입 0 일 때
        var servay_q1: String? = null    // 질문 1
        var servay_q2: String? = null    // 질문 2
        var servay_q3: String? = null    // 질문 3
        var servay_q4: String? = null    // 질문 4
        var duple: Int = 0       // 중복 참여 가능 여부
        var q_count: Int = 0     // 질문 문항 수

        // 타입 1일 때
        var servay_a_txt: String? = null
        var servay_a_img: String? = null
        var servay_b_txt: String? = null
        var servay_b_img: String? = null
    }


}
