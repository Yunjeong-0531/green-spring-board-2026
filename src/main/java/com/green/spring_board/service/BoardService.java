package com.green.spring_board.service;

import com.green.spring_board.dto.BoardResponse;
import com.green.spring_board.dto.BoardUpdateRequest;
import com.green.spring_board.entity.User;
import com.green.spring_board.exceptions.ResourceNotFoundException;
import com.green.spring_board.dto.BoardCreateRequest;
import com.green.spring_board.entity.Board;
import com.green.spring_board.repository.BoardRepository;
import com.green.spring_board.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class BoardService {
    private BoardRepository boardRepository;
    private UserRepository userRepository;

    //전체 조회
    public List<BoardResponse> getAllBoards()
    {
        return boardRepository.findAll()
                .stream()
                .map(BoardResponse::from)
                .toList();
    }

    //상세 조회
    public BoardResponse getBoardDetail(int id) {
        Optional<Board> optionalBoards = boardRepository.findById(id);
        //잘못된 게시글 id
        if(optionalBoards.isEmpty()){
            throw new ResourceNotFoundException("요청한 게시글을 찾지 못했습니다.");
        }
            Board board = optionalBoards.get();

            board.setHits(board.getHits() + 1);
            boardRepository.save(board);

            return BoardResponse.from(board);
    }

    //생성
    public int createBoard(BoardCreateRequest boardCreateRequest, int id){
        System.out.println(boardCreateRequest.getTitle() +":"+ boardCreateRequest.getContent());

        Board board = new Board();
        board.setTitle(boardCreateRequest.getTitle());
        board.setContent(boardCreateRequest.getContent());
        Optional<User> userOptional = userRepository.findById(id);
        if(userOptional.isEmpty()){
            throw new ResourceNotFoundException("존재하지 않는 사용자 Id입니다.");
        }
        board.setUser(userOptional.get());

        Board savedBoard = boardRepository.save(board);
        return savedBoard.getId();
    }
    //수정
    public void updateBoard(int id, BoardUpdateRequest boardUpdateRequest){
        Optional<Board> optionalBoards = boardRepository.findById(id);
        //잘못된 게시글 id
        if(optionalBoards.isEmpty()){
            throw new ResourceNotFoundException("요청한 게시글을 찾지 못했습니다.");
        }
        Board board = optionalBoards.get();

        if (boardUpdateRequest.getTitle() != null && !boardUpdateRequest.getTitle().isBlank()) {
            board.setTitle(boardUpdateRequest.getTitle());
        }
        if (boardUpdateRequest.getContent() != null && !boardUpdateRequest.getContent().isBlank()) {
            board.setContent(boardUpdateRequest.getContent());
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

