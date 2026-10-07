package com.green.spring_board.controller;


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
    public ResponseEntity<ApiResponse<List<BoardResponse>>> getBoards(
            HttpServletRequest request
    ) {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
        List<BoardResponse> boardResponseList = boardService.getAllBoards();
        return ResponseEntity.ok().body(ApiResponse.ok(boardResponseList));
        }

        int userId = (int) session.getAttribute("userId");
        List<BoardResponse> boardResponseListLogin = boardService.getAllBoards(userId);
        return ResponseEntity.ok().body(ApiResponse.ok(boardResponseListLogin));




    }


    //특정게시글조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BoardResponse>> getBoardsById(
            HttpServletRequest request,
            @PathVariable int id) {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            BoardResponse boardResponse = boardService.getBoardDetail(id);
            return ResponseEntity.ok().body(ApiResponse.ok(boardResponse));
        }

        int userId = (int) session.getAttribute("userId");
            BoardResponse boardResponse = boardService.getBoardDetail(userId, id);
            return ResponseEntity.ok(ApiResponse.ok(boardResponse));

    }


    //삽입
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createBoard(
            @Valid @RequestBody BoardCreateRequest boardCreateRequest,
                                            HttpServletRequest request) {

            HttpSession session = request.getSession(false);
            if(session==null ||session.getAttribute("userId")==null){
                throw new AuthorizationFailureException("로그인이 필요합니다.") ;
            }
            int id = (int) session.getAttribute("userId");
            int newBoardId = boardService.createBoard(boardCreateRequest, id);
            URI location = URI.create("api/board/" + newBoardId);
            return ResponseEntity.created(location).body(ApiResponse.ok());


    }

    //수정
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateBoard(@PathVariable int id,
                                            @Valid @RequestBody BoardUpdateRequest boardUpdateRequest,
                                            HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            throw new AuthorizationFailureException("로그인이 필요합니다.");
        }
        int userId = (int) session.getAttribute("userId");
        boardService.updateBoard(id, boardUpdateRequest, userId);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    //삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBoard(@PathVariable int id,
        HttpServletRequest request) {

            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("userId") == null) {
                throw new AuthorizationFailureException("로그인이 필요합니다.");
            }
            int userId =(int) session.getAttribute("userId");
            boardService.deleteBoard(id, userId);
            return ResponseEntity.ok().body(ApiResponse.ok());
//            return ResponseEntity.noContent().build();
    }


    //좋아요
    @PostMapping("/like/{id}")
    public ResponseEntity<ApiResponse<Void>> likeBoard(
            HttpServletRequest request,
            @PathVariable int id
    ){
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            throw new AuthorizationFailureException("로그인이 필요합니다.");
        }
        int userId = (int) session.getAttribute("userId");

        boardService.pressLike(id, userId);
        return ResponseEntity.ok().body(ApiResponse.ok());

    }

    //좋아요 상세조회
    @GetMapping("/like/{id}")
    public ResponseEntity<ApiResponse<LikeDetailResponse>> viewLikeDetails(
            @PathVariable int id,
            HttpServletRequest request
    ){
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            throw new AuthorizationFailureException("로그인이 필요합니다.");
        }
        //유저들의 유저명 리스트 반환
        LikeDetailResponse likeDetailResponse =  boardService.viewLikeDetails(id);
        return ResponseEntity.ok().body(ApiResponse.ok(likeDetailResponse));
    }




}