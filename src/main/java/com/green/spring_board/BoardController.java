package com.green.spring_board;


import lombok.AllArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/board")
@AllArgsConstructor
public class BoardController {
      private BoardRepository boardRepository;

    //조회
    @GetMapping
    public ResponseEntity<List<Boards>> getBoards(){
        return ResponseEntity.ok(
                boardRepository.findAll()
        );
    }



    @GetMapping("/{id}")
    public ResponseEntity<Boards> getBoardsById(@PathVariable int id){
        Optional<Boards> optionalBoards = boardRepository.findById(id);
        //비어있을 경우
        if(optionalBoards.isEmpty()){
            return ResponseEntity.notFound().build();
        } else {
            Boards board = optionalBoards.get();
            board.setHits(board.getHits() + 1);
            boardRepository.save(board);

            return ResponseEntity.ok(board);
        }
    }


    //삽입
    @PostMapping
    public ResponseEntity<Boards> createBoard(@RequestBody BoardCreateRequest boardCreateRequest){
        System.out.println(boardCreateRequest.getTitle() +":"+ boardCreateRequest.getContent());
        if(boardCreateRequest.getTitle() == null || boardCreateRequest.getTitle().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        if(boardCreateRequest.getContent() == null || boardCreateRequest.getContent().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Boards board = new Boards();
        board.setTitle(boardCreateRequest.getTitle());
        board.setContent(boardCreateRequest.getContent());
        Boards savedBoard = boardRepository.save(board);
        int newBoardId = savedBoard.getId();
        URI location = URI.create("api/board/"+newBoardId);

        return ResponseEntity.created(location).body(board);

    }
    //삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable int id){
        boolean isBoardExists = boardRepository.existsById(id);
        if(!isBoardExists){
            return ResponseEntity.notFound().build();
        }
        boardRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateBoard(@PathVariable int id, @RequestBody BoardCreateRequest boardCreateRequest){
        Optional<Boards> optionalBoards = boardRepository.findById(id);
        //비어있을 경우
        if(optionalBoards.isEmpty()){
            return ResponseEntity.notFound().build();
        }

            Boards board = optionalBoards.get();
            if ((boardCreateRequest.getTitle() == null || boardCreateRequest.getTitle().isBlank()) &&
                    (boardCreateRequest.getContent() == null || boardCreateRequest.getContent().isBlank())) {
                return ResponseEntity.badRequest().build();
            }
            if (boardCreateRequest.getTitle() != null && !boardCreateRequest.getTitle().isBlank()) {
                board.setTitle(boardCreateRequest.getTitle());
            }
            if (boardCreateRequest.getContent() != null && !boardCreateRequest.getContent().isBlank()) {
                board.setContent(boardCreateRequest.getContent());
            }
            boardRepository.save(board);
            return ResponseEntity.ok().build();
        
        }


}
