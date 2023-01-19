package com.example.step09gameview;

public class Enemy {
    public int x, y; //적기의 좌표
    public int type; //적기의 type ( 0 or 1 )
    public boolean isDead; //배열에서 제거 여부
    public int energy; //적기 Hp
    public int imageIndex; //이미지 인덱스(애니메이션 효과)
    public boolean isFall; // 현재 추락 하고 있는지 여부
    public int angle; //현재 회전각
    public int size; //현재 크기
}
