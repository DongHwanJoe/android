package com.example.kotlin_test

import java.io.BufferedReader
import java.io.FileReader
import java.io.InputStream

/*
    Kotlin 에서 입출력
 */
fun main() {
    //키보드와 연결된 InputStream
    var kbd : InputStream = System.`in`
    // c:\acorn202210\myFolder\memo.txt 파일에서 문자열을 읽어오려면
    // in java = > FileReader fr = new FileReader( File )
    val fr = FileReader("c:/acorn202210/myFolder/memo.txt")
    //BufferedReader
    val br = BufferedReader(fr)
    while (true){
        //한 줄씩 읽어오면서
        val line = br.readLine()
        //더이상 읽을 문자열이 없다면 반복문 탈출
        if(line == null)break
        println(line)
    }
}