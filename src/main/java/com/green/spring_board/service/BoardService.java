package com.green.spring_board.service;

import com.green.spring_board.dto.BoardCreateRequest;
import com.green.spring_board.entity.Boards;
import com.green.spring_board.repository.BoardRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class BoardService {
    private BoardRepository boardRepository;

    //전체 조회
    public List<Boards> getAllBoards(){
        return boardRepository.findAll();
    }

    //상세 조회
    public Boards getBoardDetail(int id){
        Optional<Boards> optionalBoards = boardRepository.findById(id);
        //잘못된 게시글 id
        if(optionalBoards.isEmpty()){
            return null;
        }
            Boards board = optionalBoards.get();
            board.setHits(board.getHits() + 1);
            boardRepository.save(board);

            return board;
    }

    //생성
    public int createBoard(BoardCreateRequest boardCreateRequest){
        System.out.println(boardCreateRequest.getTitle() +":"+ boardCreateRequest.getContent());
        if(boardCreateRequest.getTitle() == null || boardCreateRequest.getTitle().isBlank()) {
            return -1;
        }
        if(boardCreateRequest.getContent() == null || boardCreateRequest.getContent().isBlank()) {
            return -1;
        }
        Boards board = new Boards();
        board.setTitle(boardCreateRequest.getTitle());
        board.setContent(boardCreateRequest.getContent());
        Boards savedBoard = boardRepository.save(board);
        return savedBoard.getId();
    }
    //수정
    public int updateBoard(int id, BoardCreateRequest boardCreateRequest){
        Optional<Boards> optionalBoards = boardRepository.findById(id);
        //잘못된 게시글 id
        if(optionalBoards.isEmpty()){
            return -1;
        }

        Boards board = optionalBoards.get();

        //제목 내용이 비었을 때
        if ((boardCreateRequest.getTitle() == null || boardCreateRequest.getTitle().isBlank()) &&
                (boardCreateRequest.getContent() == null || boardCreateRequest.getContent().isBlank())) {
            return -2;
        }
        if (boardCreateRequest.getTitle() != null && !boardCreateRequest.getTitle().isBlank()) {
            board.setTitle(boardCreateRequest.getTitle());
        }
        if (boardCreateRequest.getContent() != null && !boardCreateRequest.getContent().isBlank()) {
            board.setContent(boardCreateRequest.getContent());
        }
        boardRepository.save(board);
        return 0;


    }
    //삭제
    public int deleteBoard(int id){
        boolean isBoardExists = boardRepository.existsById(id);
        if(!isBoardExists){
            return -1;
        }
        boardRepository.deleteById(id);
        return 0;
    }




}

