package com.example.kotlin_test

fun main(){
    //수정 불가능한 Map
    val mem = mapOf<String, Any>("num" to 1, "name" to "kim", "isMan" to true)

    //Map에 저장된 데이터 참조하는 방법1
    val num = mem.get("num")
    val name = mem.get("name")
    val isMan = mem.get("isMan")

    //Map에 저장된 데이터 참조하는 방법2
    val num2 = mem["num"]
    val name2 = mem["name"]
    val isMan2 = mem["isMan"]

    //수정 가능한 Map
    val mem2 = mutableMapOf<String, Any>()
    //빈 map에 데이터 넣기
    mem2.put("num", 2)
    mem2.put("name", "skul")
    mem2.put("isMan", false)

    val mem3:MutableMap<String, Any> = mutableMapOf()
    //빈 Map에 데이터 넣기2
    mem3["num"] = 3
    mem3["name"] = "monkey"
    mem3["isMan"] = true
}