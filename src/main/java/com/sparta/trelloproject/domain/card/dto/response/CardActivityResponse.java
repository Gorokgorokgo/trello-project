package com.sparta.trelloproject.domain.card.dto.response;

import com.sparta.trelloproject.domain.card.entity.CardActivity;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CardActivityResponse {

    private String activity;

    private LocalDateTime timestamp;

    public CardActivityResponse(String activity, LocalDateTime timestamp){
        this.activity = activity;
        this.timestamp = timestamp;
    }

}
