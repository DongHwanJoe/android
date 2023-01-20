package com.example.kotlin_test

fun main(){

    var selected = "gun"

    when(selected){
        "gun" -> println("총")
        "sword" -> println("검")
        else -> println("주먹")
    }
}