package com.green.spring_board.controller;


import com.green.spring_board.dto.ApiResponse;
import com.green.spring_board.dto.BoardResponse;
import com.green.spring_board.dto.BoardUpdateRequest;
import com.green.spring_board.exceptions.AuthorizationFailureException;
import com.green.spring_board.dto.BoardCreateRequest;
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
    public ResponseEntity<ApiResponse<List<BoardResponse>>> getBoards() {
        return ResponseEntity.ok(
                ApiResponse.ok(boardService.getAllBoards())
        );
    }


    //특정게시글조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BoardResponse>> getBoardsById(@PathVariable int id) {

            BoardResponse boardResponse = boardService.getBoardDetail(id);
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
}