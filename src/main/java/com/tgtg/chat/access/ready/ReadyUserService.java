package com.tgtg.chat.access.ready;

public interface ReadyUserService {

    //회원이 준비버튼 클릭했을 때 readyUser 값 1 추가
    public void readyUser(int roomId);

    //회원이 준비버튼 취소했을 때 readyUser 값 1 감소
    public void unreadyUser(int roomId);

    //현재 준비한 회원 수
    public int getReady(int roomId);

    public void deleteReadyUser(int roomId);
}
