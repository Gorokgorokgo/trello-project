package com.sparta.trelloproject.domain.board.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "boards")
public class Board {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long boardId;

    @Column(name = "workspace_id", nullable = false)
    private Long workspaceId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "background")
    private String background;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    public Board(Long workspaceId, Long userId, String title, String background) {
        this.workspaceId = workspaceId;
        this.userId = userId;
        this.title = title;
        this.background = background;
        this.createdAt = LocalDateTime.now(); // 현재 시간으로 생성
        this.modifiedAt = LocalDateTime.now(); // 현재 시간으로 수정
    }

    // 보드 수정
    public void update(String title, String background) {
        this.title = title;
        this.background = background;
        this.modifiedAt = LocalDateTime.now();
    }
}
