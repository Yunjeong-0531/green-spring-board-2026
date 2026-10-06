package com.green.spring_board.dto;

import com.green.spring_board.entity.Board;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BoardResponse {
    int id;
    String title;
    String content;
    int hits;
    Integer authorId;//작성자 ID
    String authorNickName; //작성자 닉네임

    LocalDateTime createdDateTime;
    LocalDateTime updatedDateTime;

    public static BoardResponse from(Board board){

        Integer authorId = null;
        String authorNickName = null;

        if(board.getUser()!=null){
            authorId = board.getUser().getId();
            authorNickName = board.getUser().getNickname();
        }

        return new BoardResponse(
                board.getId(),
                board.getTitle(),
                board.getContent(),
                board.getHits(),
                authorId,
                authorNickName,
                board.getCreatedDatetime(),
                board.getUpdatedDatetime());
    }

}
