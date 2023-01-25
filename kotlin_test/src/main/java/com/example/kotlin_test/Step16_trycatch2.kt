package com.example.kotlin_test

fun main() {
    val isRun = true
    val result = if(isRun){
        "달"
    }else{
        "노달"
    }

    var str = "1000"
    var str2 = "천"

    var result2 = try{
        //예외가 발생하지 않을 경우 대입될 값
        Integer.parseInt(str2)
    }catch (nfe : java.lang.NumberFormatException){
        //예외가 발생하는 경우 대입될 값
        0
    }
    println("result2: ${result2}")
}