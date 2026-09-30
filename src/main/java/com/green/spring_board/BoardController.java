package com.green.spring_board;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/board")

public class BoardController {
    private BoardRepository boardRepository;

    public BoardController(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }
    //전체조회
    @GetMapping
    public List<Boards> getBoards(){
        return boardRepository.findAll();
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
}
