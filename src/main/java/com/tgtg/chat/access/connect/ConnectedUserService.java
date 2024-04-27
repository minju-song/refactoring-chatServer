package com.tgtg.chat.access.connect;

import com.tgtg.chat.anonymous.dto.AnonymousDTO;

import java.time.LocalTime;
import java.util.Set;

public interface ConnectedUserService {

    //회원이 접속했을 때
    public void userEntered(int roomId, AnonymousDTO anonymous);

    //현재 시간 보냄
    public LocalTime gameStartUser(int roomId);

    //게임투표 count
    public void gameVoteCount(int roomId, int gameSelect);

    //시간끝난 인원수 체크
    public boolean getZeroCount(int roomId);

    //투표 결과 리턴
    public String getVoteResult(int roomId);

    //회원이 퇴장했을 때
    public void userLeft(int roomId, AnonymousDTO anonymous);

    //현재 접속한 회원 수
    public Long getConnectedUserCount(int roomId);

    //특정 방의 모든 회원 가져오기
    public Set<AnonymousDTO> getAllMembersInRoom(int roomId);

    //회원 역할
    public Set<AnonymousDTO> setRole(int roomId);

    public void saveResult(int roomId, String result);

    public void deleteRoom(int roomId);

}
