package com.sparta.trelloproject.domain.comment.service;

import com.sparta.trelloproject.common.exception.CustomException;
import com.sparta.trelloproject.common.exception.ErrorCode;
import com.sparta.trelloproject.domain.auth.entity.AuthUser;
import com.sparta.trelloproject.domain.card.entity.Card;
import com.sparta.trelloproject.domain.card.repository.CardRepository;
import com.sparta.trelloproject.domain.comment.dto.request.CommentRequest;
import com.sparta.trelloproject.domain.comment.dto.response.CommentResponse;
import com.sparta.trelloproject.domain.comment.entity.Comment;
import com.sparta.trelloproject.domain.comment.repository.CommentRepository;

import com.sparta.trelloproject.domain.notification.service.NotificationService;
import com.sparta.trelloproject.domain.user.entity.User;
import com.sparta.trelloproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final CardRepository cardRepository;

//    텍스트와 이모티콘 조건
    private final String textRegex = "^[a-zA-Z0-9가-힣\\p{Punct}\\s]+$";
    private final String emojiRegex = "[\\uD83C-\\uDBFF\\uDC00-\\uDFFF]+";

//    댓글 작성
    @Transactional
    public CommentResponse saveComment(AuthUser authUser, Long cardId, CommentRequest commentRequest) {

        // userId로 User 객체 조회
        User user = userRepository.findById(
                authUser.getUserId()).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        // 카드 조회 (cardId로 Card 객체 조회)
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CustomException(ErrorCode.CARD_NOT_FOUND, "해당 카드를 찾을 수 없습니다."));

//      예외처리 ( 읽기 전용 유저 )
//        if (authUser.isReadOnly()) {
//            throw new CustomException(ErrorCode.Comment_FORBIDDEN, "댓글 작성 권한이 없습니다.");
//        }
        // 텍스트와 이모지 유효성 검증
        validateText(commentRequest.getText());
        validateEmoji(commentRequest.getEmoji());

        Comment comment = new Comment(
                user,
                commentRequest.getText(),
                commentRequest.getEmoji(),
                card);

        commentRepository.save(comment);

//        댓글 작성 후에 알림 전송
        sendNotification(user.getEmail(),cardId, commentRequest.getText());

        return new CommentResponse(
                comment.getCommentId(),
                comment.getText(),
                comment.getEmoji(),
                comment.getUser().getEmail());
    }

    //    댓글 수정
    @Transactional
    public CommentResponse updateComment(AuthUser authUser, Long commentId, CommentRequest commentRequest) {
        // userId로 User 객체 조회
        User user = userRepository.findById(
                        authUser.getUserId()).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));
        Comment comment = commentRepository.findById(
                commentId).orElseThrow(
                        () -> new CustomException(ErrorCode.Comment_NOT_FOUND, "댓글을 찾을 수 없습니다"));

//        작성자 확인
        if (!user.equals(comment.getUser())){
        throw new CustomException(ErrorCode.Comment_AUTH_FORBIDDEN, "작성자가 아니므로 수정/삭제가 불가능합니다.");
        }

        validateText(commentRequest.getText());
        validateEmoji(commentRequest.getEmoji());

        comment.update(commentRequest.getText(), commentRequest.getEmoji());

        return new CommentResponse(
                comment.getCommentId(),
                comment.getText(),
                comment.getEmoji(),
                comment.getUser().getEmail());
    }

    //    댓글 삭제
    @Transactional
    public void deleteComment(AuthUser authUser, Long commentId) {
        // userId로 User 객체 조회
        User user = userRepository.findById(
                authUser.getUserId()).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));
        Comment comment = commentRepository.findById(
                commentId).orElseThrow(
                () -> new CustomException(ErrorCode.Comment_NOT_FOUND, "댓글을 찾을 수 없습니다"));
//        작성자 확인
        if (!user.equals(comment.getUser())){
            throw new CustomException(ErrorCode.Comment_AUTH_FORBIDDEN, "작성자가 아니므로 수정/삭제가 불가능합니다.");
        }
        commentRepository.delete(comment);
    }


    //    텍스트와 이모티콘 검증
    private void validateText(String text) {
        if (text == null || !Pattern.matches(textRegex, text)) {
            throw new CustomException(ErrorCode.Comment_BAD_REQUEST, "잘못된 형식의 텍스트/이모지입니다.");
        }
    }
    private void validateEmoji(String emoji) {
        if (emoji == null || !Pattern.matches(emojiRegex, emoji)) {
            throw new CustomException(ErrorCode.Comment_BAD_REQUEST, "잘못된 형식의 텍스트/이모지입니다.");
        }
    }

//    알림 전송
    private void sendNotification(String email, Long cardId, String commentText) {
//        알림 메시지 생성
        notificationService.sendSlackNotification(email, cardId.toString(), commentText);
        notificationService.sendDiscordNotification(email, cardId.toString(), commentText);
    }
}
