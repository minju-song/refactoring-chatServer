package com.tgtg.chat.anonymous.service;

import com.tgtg.chat.anonymous.dto.AnonymousDTO;

public interface AnonymousService {

    //채팅방 입장 순으로 익명닉네임, 프로필 가져오기
    public AnonymousDTO getAnonymous(int count);

    //회원의 익명객체 생성 및 저장
    public AnonymousDTO createAnonymous(int roomId, String memberId);

    //회원의 익명객체 삭제
    public void deleteAnonymous(int roomId, int anonymousId);

    //해당 방 count삭제
    public void deleteCount(int roomId);

    //회원의 진짜 아이디 얻기
    public String findMemberId(int roomId,String anonymousId);

    //직렬화
    String serializeAnonymousDTO(AnonymousDTO anonymousDTO);

    //역직렬화
    AnonymousDTO deserializeToAnonymousDTO(String json);

}
