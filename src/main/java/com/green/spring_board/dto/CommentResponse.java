package com.green.spring_board.dto;

import com.green.spring_board.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class CommentResponse {
    private int commentId;
    private String nickname;
    private String content;
    private LocalDateTime commentDate;

    public static CommentResponse from(Comment comment){

        return new CommentResponse(
        comment.getId(),
        comment.getUser().getNickname(),
        comment.getContent(),
        comment.getCreatedDatetime());

    }
}
