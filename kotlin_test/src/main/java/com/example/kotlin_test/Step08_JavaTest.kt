package com.example.kotlin_test
import com.example.kotlin_test.java.Member
import com.example.kotlin_test.java.MemberDto

fun main(){
    // kotlin에서 java 클래스도 자유롭게 import해서 사용할 수 있다.
    val mem1 = Member()
    mem1.num = 1
    mem1.name = "kim"
    mem1.addr = "nrj"
    mem1.showInfo()

    val mem2 = MemberDto()
    //내부적으로 java의 setter 메소드가 호출된다.
    mem2.num=2;
    mem2.name="skul"
    mem2.addr="hsd"
}