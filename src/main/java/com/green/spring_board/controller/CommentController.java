package com.green.spring_board.controller;

import com.green.spring_board.dto.ApiResponse;
import com.green.spring_board.dto.CommentCreateRequest;

import com.green.spring_board.dto.CommentResponse;
import com.green.spring_board.entity.Comment;
import com.green.spring_board.exceptions.UnauthenticatedException;
import com.green.spring_board.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import java.util.List;


@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class CommentController {
    CommentService commentService;

    @PostMapping("/board/{id}/comment")
    public ResponseEntity<ApiResponse<Void>> postComment(
            @PathVariable int id,
            @Valid @RequestBody CommentCreateRequest commentCreateRequest){

       commentService.postComment(id, commentCreateRequest);
       return ResponseEntity.ok().body(ApiResponse.ok());

    }

    @GetMapping("/board/{id}/comment")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> readComments(
            @PathVariable int id
    ){

        return ResponseEntity.ok().body(ApiResponse.ok(commentService.readComments(id)));

    }

    @PatchMapping("/comment/{id}")
    public ResponseEntity<ApiResponse<Void>> updateComment(
            @PathVariable int id,

            @Valid @RequestBody CommentCreateRequest commentUpdateRequest){


       commentService.updateComment(id, commentUpdateRequest);
       return ResponseEntity.ok().body(ApiResponse.ok());
    }

    @DeleteMapping("/comment/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable int id
    ){

        commentService.deleteComment(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.ok());
    }
}
