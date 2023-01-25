package com.example.kotlin_test

fun main() {
    var names = listOf("kim", "lee", "park", "cho", "seo")
    for(i in 0 .. 4){
        // i 는 0에서부터 4까지 순서대로 참조가 된다.
        //val tmp = names[i]
        val tmp = names.get(i)
        println(tmp)
    }
    println("-----")

    //java의 확장 for문 처럼 item만 순서대로 참조할 수 있다.
    for(item in names){
        println(item)
    }
    println("-----")

    for(i in 0 until names.size){
        val tmp = names[i]
        println(tmp)
    }
    println("-----")

    for(num in 0 .. 10 step 2){
        println(num)
    }
    println("-----")

    for(num in 10 downTo 0){
        println(num)
    }
    println("-----")

    for(num in 10 downTo 0 step 2){
        println(num)
    }
    println("-----")
    //names 배열의 아이템을 역순으로 참조하기
    for(i in names.size-1 downTo 0){
        var tmp = names[i]
        println(tmp)
    }
    println("-----")

    names.forEach{
        println(it)
    }
}