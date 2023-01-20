package com.example.kotlin_test

/*
    - 비교 연산자
    == 와 === 의 구분
    == 는 값이 같은지 비교
    === 는 참조값이 같은지 비교

    != 는 값이 다른지 비교
    !== 는 참조값이 다른지 비교

    - 삼항 연산자가 없다
      대신 대체할 수 있는 문법이 있다.

    - Elvis 연산자
      ?:
 */

fun main() {
    /*
        names와 names2 는 참조값은 다르지만 안에 저장된 값은 같다
     */
    val names = listOf("kim", "lee", "park")
    val names2 = listOf("kim", "lee", "park")

    println("names === names2: ${names === names2 }")
    println("names == names2: ${names == names2 }")

    val a = "kim"
    val b = "kim"

    println("a === b : ${a === b}")
    println("a == b : ${a == b}")

    var isRun = true
    var result = if(isRun) "달려요" else "달리지 않아요"

    var result2 = if(isRun){
        println("qwe")
        println("asd")
        "zxczxc"
    }else{
        "ddd"
    }

    var myName:String? = null

    if(myName === null){
        myName = "기본이름"
        println("이름: "+myName)
    }else{
        println("이름: "+ myName)
    }

    // ?: 연산자의 좌흑에 있는 값이 null이면 ?: 연산자의 우측에 있는 값이 남는다.
    // 이 값을 알 수 없으면(null이면) ?: 이 값을 써라
    var result3 = myName ?: "기본이름"

    println("이름: ${myName ?: "기본이름"}")
}