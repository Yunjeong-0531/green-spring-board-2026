package com.green.spring_board.controller;


import com.green.spring_board.dto.BoardResponse;
import com.green.spring_board.dto.BoardUpdateRequest;
import com.green.spring_board.exceptions.ResourceNotFoundException;
import com.green.spring_board.exceptions.UserRequestException;
import com.green.spring_board.dto.BoardCreateRequest;
import com.green.spring_board.service.BoardService;
import com.green.spring_board.entity.Board;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.List;
import jakarta.servlet.http.HttpSession;


@RestController
@RequestMapping("api/board")
@AllArgsConstructor
public class BoardController {

    private final BoardService boardService;

    //전체 조회
    @GetMapping
    public ResponseEntity<List<BoardResponse>> getBoards() {
        return ResponseEntity.ok(
                boardService.getAllBoards()
        );
    }


    //특정게시글조회
    @GetMapping("/{id}")
    public ResponseEntity<BoardResponse> getBoardsById(@PathVariable int id) {
        try {
            BoardResponse boardResponse = boardService.getBoardDetail(id);
            return ResponseEntity.ok(boardResponse);
        } catch (ResourceNotFoundException e) {
            //게시글 못 찾았을 때
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            //서버 에러
            return ResponseEntity.internalServerError().build();
        }
    }


    //삽입
    @PostMapping
    public ResponseEntity<Void> createBoard(
            @Valid @RequestBody BoardCreateRequest boardCreateRequest,
                                            HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            if(session==null ||session.getAttribute("userId")==null){
                return ResponseEntity.status(401).build();
            }
            int id = (int) session.getAttribute("userId");
            int newBoardId = boardService.createBoard(boardCreateRequest, id);
            URI location = URI.create("api/board/" + newBoardId);
            return ResponseEntity.created(location).build();

        } catch (UserRequestException e) {
            return ResponseEntity.badRequest().build();
        }catch (ResourceNotFoundException e){
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    //수정
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateBoard(@PathVariable int id,
                                            @Valid @RequestBody BoardUpdateRequest boardUpdateRequest) {
        try {
            boardService.updateBoard(id, boardUpdateRequest);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UserRequestException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    //삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable int id) {
        try {
            boardService.deleteBoard(id);
            return ResponseEntity.noContent().build();

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }

    }
}