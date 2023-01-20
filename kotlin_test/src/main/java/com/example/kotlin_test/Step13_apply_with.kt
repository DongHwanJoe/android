package com.example.kotlin_test

class MachineGun{
    //총알을 발사하는 메소드
    fun fire():MachineGun{
        println("ㄷㄷㄷㄷㄷㄷ")
        return this
    }
}

class MyGun{
    fun fire(){
        println("eee")
    }
}

class YourWeapon{
    var name:String? = null
    fun prepare(){
        println("무기 준비")
    }
    fun move(){
        println("무기 이동")
    }
    fun use(){
        println("무기 사용")
    }
}

fun main(){
    val g1 = MachineGun()
    g1.fire().fire().fire()

    val g2 = MyGun()
    //특정 참조값을 블럭안에서 여러번 사용할 수 있다.
    with(g2){
        fire()
        fire()
        fire()
    }

    val w1 = YourWeapon()
    with(w1){
        name = "잠수함"
        prepare()
        move()
        use()
    }

    val w2 = YourWeapon().apply {
        name = "전투기"
        prepare()
        move()
    }

    w2.use()
}