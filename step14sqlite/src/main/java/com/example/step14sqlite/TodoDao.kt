package com.example.step14sqlite

import android.database.Cursor

/*
    TodoDao 클래스의 대표생성자(primary constructor)의 인자로 DBHelper 객체를 전달받아서
    필드에 넣어두고 메소드에서 활용하는 구조이다.

    insert, update, delete 작업을 할 때는
        var db:SQLiteDataBase = helper.getWritableDataBase()
        var db:SQLiteDataBase = helper.writableDataBase
    select 작업을 할 때는
        var db:SQLiteDataBase = helper.readableDataBase

    java의 JDBC에서 PreparedStatement 객체와 비슷한 기능을 하는 객체가 SQLiteDataBase 객체이다.
 */
class TodoDao(var helper:DBHelper) {

    //할 일 정보를 삭제하는 메소드
    fun delete(num:Int){
        val db = helper.writableDatabase
        val args = arrayOf<Any>(num)
        val sql = "DELETE FROM todo WHERE num = ?"
        db.execSQL(sql, args)
        db.close()
    }

    //할 일 정보 하나를 수정하는 메소드
    fun update(data: Todo){
        /*
        val db = helper.writableDatabase
        val args = arrayOf<Any>(data.num, data.content)
        val sql = "UPDATE todo SET content = ? WHERE num = ?"
        db.execSQL(sql, args)
        db.close()
        */
        with(helper.writableDatabase){
            execSQL("UPDATE todo SET content = ? WHERE num = ?", arrayOf<Any>(data.num, data.content))
            close()
        }
    }

    //할 일 정보를 저장하는 메소드
    fun insert(data:Todo){
        // java => SQLiteDataBase db = helper.getWritableDataBase()
        val db = helper.writableDatabase
        // ?에 바인딩 할 인자를 Any 배열로 얻어내기
        //java => Object[] args = { data.getContent() }
        val args = arrayOf<Any>(data.content)
        //실행할 sql문
        // SQLiteDB => datatime('now', 'localtime').  oracle = > SYSDATE
        val sql = "INSERT INTO todo (content, regdate)" +
                "VALUES(?, datetime('now', 'localtime'))"
        //sql 문 실행하기
        db.execSQL(sql, args)
        db.close() // close()를 호출해야 실제로 반영된다.
    }
    //할 일 목록을 리턴하는 함수
    fun getList():List<Todo>{
        //수정 가능한 Todo type을 담을 수 있는 list
        val list = mutableListOf<Todo>()
        //select 문을 수행해줄 객체
        val db = helper.readableDatabase

        /*
           SQLite DB 에서 날짜 format 만들기

           - strftime() 함수를 활용한다.
           year : %Y
           month : %m
           date : %d
           week of day 0 1 2 3 4 5 6 : %w
           hour : %H
           minute : %M

           substr('일월화수목금토', strftime('%w', regdate)+1, 1)
           substr('일요일월요일화요일수요일목요일금요일토요일', strftime('%w', regdate)*3+1, 3)
        */

        //실행할 select 문 구성
        val sql = "SELECT num, content, " +
                "strftime('%Y년 %m월 %d일 ', regdate)" +
                "|| substr('일월화수목금토', strftime('%w', regdate)+1, 1)" +
                "|| strftime(' %H시 %M분', regdate)" +
                "FROM todo ORDER BY num ASC"
        //query문을 수행하고 결과를 Cursor 객체로 얻어내기(selection 인자는 없다)
        val result:Cursor = db.rawQuery(sql, null)

        //Cursor 객체의 메소드를 이용해서 담긴 내용을 반복문 돌면서 추출한다.
        while (result.moveToNext()){
            //0번째는 num, 1번째는 content, 2번째는 regadate 다.
            val data = Todo(result.getInt(0), result.getString(1), result.getString(2))
            //할 일 정보가 담긴 Todo 객체를 List에 추가한다.
            list.add(data)
        }
        //할 일 목록을 리턴
        return list
    }
}