package com.sparta.trelloproject.domain.comment.dto.response;

import lombok.Getter;

@Getter
public class CommentResponse {
    private final Long commentId;
    private final String text;
    private final String emoji;
    private final String userEmail;

    public CommentResponse(Long commentId, String text, String emoji, String userEmail) {
        this.commentId = commentId;
        this.text = text;
        this.emoji = emoji;
        this.userEmail = userEmail;
    }
}