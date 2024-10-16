package com.sparta.trelloproject.domain.member.dto;

import com.sparta.trelloproject.domain.member.entity.Member;
import lombok.Getter;

@Getter
public class MemberSaveResponse {
    private final Long id;
    private final String msg;

    public MemberSaveResponse(Member member) {
        this.id = member.getId();
        this.msg = "멤버 추가가 성공적으로 완료되었습니다.";
    }
}
