package com.green.spring_board;


import lombok.AllArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/board")
@AllArgsConstructor
public class BoardController {
      private BoardRepository boardRepository;

    //조회
    @GetMapping
    public List<Boards> getBoards(){
        return boardRepository.findAll();
    }
    @GetMapping("/{id}")
    public Boards getBoardsById(@PathVariable int id){
        Boards board = boardRepository.findById(id).get();
        board.setHits(board.getHits()+1);
        boardRepository.save(board);

        return board;
    }


    //삽입
    @PostMapping
    public void createBoard(@RequestBody BoardCreateRequest boardCreateRequest){
        System.out.println(boardCreateRequest.getTitle() +":"+ boardCreateRequest.getContent());

        Boards board = new Boards();
        board.setTitle(boardCreateRequest.getTitle());
        board.setContent(boardCreateRequest.getContent());
        boardRepository.save(board);

    }

    @DeleteMapping("/{id}")
    public void deleteBoard(@PathVariable int id){
        boardRepository.deleteById(id);
    }

    @PatchMapping("/{id}")
    public void updateBoard(int id, @RequestBody BoardCreateRequest boardCreateRequest){

        Boards board = boardRepository.findById(id).get();
        if(boardCreateRequest.getTitle()!= null) {
            board.setTitle(boardCreateRequest.getTitle());
        }
        if(boardCreateRequest.getContent()!=null) {
            board.setContent(boardCreateRequest.getContent());
        }
        boardRepository.save(board);
    }


}
