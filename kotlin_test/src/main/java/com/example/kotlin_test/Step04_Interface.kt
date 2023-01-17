package com.example.kotlin_test

interface Remocon{
    fun up()
    fun down()
}
/*
    java => implements Remocon
    kotlin => : Remocon
 */
class MyRemocon : Remocon{
    override fun up() {
        println("무언가 올려요")
    }

    override fun down() {
        println("무언가 내려요")
    }
}

fun main(){
    val r1 = MyRemocon()
    r1.up()
    r1.down()
    /*
        java
        Remocon r = new Remocon(){
            @override
            public void up(){}
            @override
            public void down(){}
        };

        kotlin
        var r = object : Remocon{
            override fun up(){}
            override fun down(){}
        }
     */

    /*
        object : 클래스정의 (인터페이스를 구현하거나, 클래스를 상속받은)
        정의된 클래스로 객체를 생성해서 object에 담는 느낌

        var object = 객체생성
        var r2 = object
     */
    var r2 = object : Remocon{
        override fun up() {
            println("무언가 올려요")
        }

        override fun down() {
            println("무언가 내려요")
        }
    }

    r2.up()
    r2.down()
}