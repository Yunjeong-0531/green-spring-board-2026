package com.green.spring_board.service;

import com.green.spring_board.exceptions.ResourceNotFoundException;
import com.green.spring_board.exceptions.UserRequestException;
import com.green.spring_board.dto.BoardCreateRequest;
import com.green.spring_board.entity.Board;
import com.green.spring_board.repository.BoardRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class BoardService {
    private BoardRepository boardRepository;

    //전체 조회
    public List<Board> getAllBoards(){
        return boardRepository.findAll();
    }

    //상세 조회
    public Board getBoardDetail(int id) {
        Optional<Board> optionalBoards = boardRepository.findById(id);
        //잘못된 게시글 id
        if(optionalBoards.isEmpty()){
            throw new ResourceNotFoundException("요청한 게시글을 찾지 못했습니다.");
        }
            Board board = optionalBoards.get();
            board.setHits(board.getHits() + 1);
            boardRepository.save(board);

            return board;
    }

    //생성
    public int createBoard(BoardCreateRequest boardCreateRequest, int id){
        System.out.println(boardCreateRequest.getTitle() +":"+ boardCreateRequest.getContent());
        if(boardCreateRequest.getTitle() == null || boardCreateRequest.getTitle().isBlank()) {
            throw new UserRequestException("잘못된 입력값입니다.");
        }
        if(boardCreateRequest.getContent() == null || boardCreateRequest.getContent().isBlank()) {
            throw new UserRequestException("잘못된 입력값입니다.");
        }
        Board board = new Board();
        board.setTitle(boardCreateRequest.getTitle());
        board.setContent(boardCreateRequest.getContent());


        Board savedBoard = boardRepository.save(board);
        return savedBoard.getId();
    }
    //수정
    public void updateBoard(int id, BoardCreateRequest boardCreateRequest){
        Optional<Board> optionalBoards = boardRepository.findById(id);
        //잘못된 게시글 id
        if(optionalBoards.isEmpty()){
            throw new ResourceNotFoundException("요청한 게시글을 찾지 못했습니다.");
        }

        Board board = optionalBoards.get();

        //제목 내용이 비었을 때
        if ((boardCreateRequest.getTitle() == null || boardCreateRequest.getTitle().isBlank()) &&
                (boardCreateRequest.getContent() == null || boardCreateRequest.getContent().isBlank())) {
            throw new UserRequestException("잘못된 입력값입니다.");
        }
        if (boardCreateRequest.getTitle() != null && !boardCreateRequest.getTitle().isBlank()) {
            board.setTitle(boardCreateRequest.getTitle());
        }
        if (boardCreateRequest.getContent() != null && !boardCreateRequest.getContent().isBlank()) {
            board.setContent(boardCreateRequest.getContent());
        }

        boardRepository.save(board);


    }
    //삭제
    public void deleteBoard(int id){
        boolean isBoardExists = boardRepository.existsById(id);
        if(!isBoardExists){
            throw new ResourceNotFoundException("요청한 게시글을 찾지 못했습니다.");
        }
        boardRepository.deleteById(id);
    }




}

