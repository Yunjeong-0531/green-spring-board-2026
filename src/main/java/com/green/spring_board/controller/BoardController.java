package com.green.spring_board.controller;

import org.springframework.data.domain.Page;
import com.green.spring_board.dto.*;
import com.green.spring_board.exceptions.AuthorizationFailureException;
import com.green.spring_board.service.BoardService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.List;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("api/board")
@AllArgsConstructor

public class BoardController {

    private final BoardService boardService;

    //전체 조회
    @GetMapping
    public ResponseEntity<ApiResponse<Page<BoardResponse>>> getBoards(

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "latest") String order
    ) {



        Page<BoardResponse> boardResponseListLogin = boardService.getAllBoards(page, size, order);
        return ResponseEntity.ok().body(ApiResponse.ok(boardResponseListLogin));

    }


    //특정게시글조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BoardResponse>> getBoardsById(
            @PathVariable int id) {


            BoardResponse boardResponse = boardService.getBoardDetail( id);
            return ResponseEntity.ok(ApiResponse.ok(boardResponse));

    }


    //삽입
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createBoard(
            @Valid @RequestBody BoardCreateRequest boardCreateRequest) {


            int newBoardId = boardService.createBoard(boardCreateRequest);
            URI location = URI.create("api/board/" + newBoardId);
            return ResponseEntity.created(location).body(ApiResponse.ok());


    }

    //수정
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateBoard(@PathVariable int id,
                                            @Valid @RequestBody BoardUpdateRequest boardUpdateRequest) {

        boardService.updateBoard(id, boardUpdateRequest);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    //삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBoard(@PathVariable int id) {

            boardService.deleteBoard(id);
            return ResponseEntity.ok().body(ApiResponse.ok());
//            return ResponseEntity.noContent().build();
    }


    //좋아요
    @PostMapping("/like/{id}")
    public ResponseEntity<ApiResponse<Void>> likeBoard(
            @PathVariable int id
    ){

        boardService.pressLike(id);
        return ResponseEntity.ok().body(ApiResponse.ok());

    }

    //좋아요 상세조회
    @GetMapping("/like/{id}")
    public ResponseEntity<ApiResponse<LikeDetailResponse>> viewLikeDetails(
            @PathVariable int id
    ){
        //유저들의 유저명 리스트 반환
        LikeDetailResponse likeDetailResponse =  boardService.viewLikeDetails(id);
        return ResponseEntity.ok().body(ApiResponse.ok(likeDetailResponse));
    }

    //검색
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<BoardResponse>>> searchPosts(
            @RequestParam String keyword
    ){  List<BoardResponse> response = boardService.search(keyword);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }




}