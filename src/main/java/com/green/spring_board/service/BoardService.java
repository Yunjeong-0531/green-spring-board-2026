package com.green.spring_board.service;


import org.springframework.transaction.annotation.Transactional;

import com.green.spring_board.dto.BoardResponse;
import com.green.spring_board.dto.BoardUpdateRequest;
import com.green.spring_board.dto.LikeDetailResponse;
import com.green.spring_board.entity.Like;
import com.green.spring_board.entity.User;
import com.green.spring_board.exceptions.AuthorizationFailureException;
import com.green.spring_board.exceptions.InvalidStateException;
import com.green.spring_board.exceptions.ResourceNotFoundException;
import com.green.spring_board.dto.BoardCreateRequest;
import com.green.spring_board.entity.Board;
import com.green.spring_board.repository.BoardRepository;
import com.green.spring_board.repository.LikeRepository;
import com.green.spring_board.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;




@AllArgsConstructor
@Service
public class BoardService {

    private BoardRepository boardRepository;
    private UserRepository userRepository;
    private LikeRepository likeRepository;


    //전체 조회
    @Transactional(readOnly = true)
    public Page<BoardResponse> getAllBoards(int page, int size, String order)
    {   int userId = getUserIdFromToken();

        Pageable pageable = PageRequest.of(page,size,createSort(order));
        Page<BoardResponse> boardResponses = boardRepository.findByIsDeletedFalse(pageable)
                .map(board ->
                        BoardResponse.from(board,isLikedByMe(userId,board.getId())
                        )
                );

        return boardResponses;


    }


    private Sort createSort(String order){
        return switch (order){
            case "latest" -> Sort.by(Sort.Direction.DESC, "createdDatetime");
            case "likes"  -> Sort.by(Sort.Direction.DESC, "likeCount");
            case "views"  -> Sort.by(Sort.Direction.DESC, "hits");
            default -> throw new InvalidStateException("잘못된 정렬 옵션입니다.");
        };
   }

    //상세 조회(로그인)
    @Transactional(readOnly=true)
    public BoardResponse getBoardDetail( int id) {
        int userId = getUserIdFromToken();
        Optional<Board> optionalBoards = boardRepository.findById(id);
        //잘못된 게시글 id
        if(optionalBoards.isEmpty()){
            throw new ResourceNotFoundException("요청한 게시글을 찾지 못했습니다.");
        }
        Board board = optionalBoards.get();
        if(board.isDeleted()){
            throw  new ResourceNotFoundException("삭제된 게시글입니다.");
        }
        board.setHits(board.getHits() + 1);
        boardRepository.save(board);

        return BoardResponse.from(board, isLikedByMe(userId,id));
    }

    //생성
    @Transactional
    public int createBoard(BoardCreateRequest boardCreateRequest){
        int userId = getUserIdFromToken();

        Board board = new Board();
        board.setTitle(boardCreateRequest.getTitle());
        board.setContent(boardCreateRequest.getContent());
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isEmpty()){
            throw new ResourceNotFoundException("존재하지 않는 사용자 Id입니다.");
        }
        board.setUser(userOptional.get());

        Board savedBoard = boardRepository.save(board);
        return savedBoard.getId();
    }

    //수정
    @Transactional
    public void updateBoard(int id, BoardUpdateRequest boardUpdateRequest){

        int userId = getUserIdFromToken();

        Optional<Board> optionalBoards = boardRepository.findById(id);
        //잘못된 게시글 id
        if(optionalBoards.isEmpty()){
            throw new ResourceNotFoundException("요청한 게시글을 찾지 못했습니다.");
        }
        Board board = optionalBoards.get();
        if(board.isDeleted()){
            throw new ResourceNotFoundException("삭제된 게시글입니다.");
        }
        //요청자의 userId, 작성자의 userId비교
        if( board.getUser().getId() != userId){
            throw new AuthorizationFailureException("작성자만 수정이 가능합니다.");
        }


        if (boardUpdateRequest.getTitle() != null && !boardUpdateRequest.getTitle().isBlank()) {
            board.setTitle(boardUpdateRequest.getTitle());
        }
        if (boardUpdateRequest.getContent() != null && !boardUpdateRequest.getContent().isBlank()) {
            board.setContent(boardUpdateRequest.getContent());
        }

        boardRepository.save(board);
    }

    //삭제
    @Transactional
    public void deleteBoard(int id){
//        boolean isBoardExists = boardRepository.existsById(id);
//        if(!isBoardExists){
//            throw new ResourceNotFoundException("요청한 게시글을 찾지 못했습니다.");
//        }

        int userId = getUserIdFromToken();

        Optional<Board> optionalBoard = boardRepository.findById(id);
        if(optionalBoard.isEmpty()){
            throw new ResourceNotFoundException("요청한 게시글을 찾지 못했습니다.");
        }
        Board board = optionalBoard.get();
        if(board.getUser().getId()!=userId){

            ///유저가 없는 경우 if문 필요

            throw new AuthorizationFailureException("게시글 작성자만 삭제 가능합니다.");
        }
        board.setDeleted(true);
        boardRepository.save(board);
    }

    @Transactional
    public void pressLike(int id){

        int userId = getUserIdFromToken();
        //게시글 존재 여부 확인
        Optional<Board> optionalBoard = boardRepository.findById(id);
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalBoard.isEmpty()){
            throw  new ResourceNotFoundException("존재하지 않는 게시글입니다.");
        }
        if(optionalUser.isEmpty()){
            throw  new ResourceNotFoundException("존재하지 않는 사용자입니다.");
        }
        Board board = optionalBoard.get();
        User user = optionalUser.get();

        Optional<Like> likeOptional = likeRepository.findByUserIdAndBoardId(userId,id);
        if(likeOptional.isEmpty()) {
            Like like = new Like();
            like.setBoard(board);
            like.setUser(user);
            likeRepository.save(like);

            board.setLikeCount(board.getLikeCount()+1);
            boardRepository.save(board);
        } else{
            likeRepository.delete(likeOptional.get());

            board.setLikeCount(board.getLikeCount()-1);
            boardRepository.save(board);
        }


    }

    @Transactional
    public LikeDetailResponse viewLikeDetails(int id) {
        List<Like> likes = likeRepository.findByBoardId(id);

        List<String> likedUserNames = likes.stream()
                .map(like ->like.getUser().getNickname())
                .toList();

        return new LikeDetailResponse(likedUserNames);

    }

    @Transactional
    public List<BoardResponse> search(String keyword){
        List<Board> boards = boardRepository.searchByTitle(keyword);

//        return  boards.stream()
//                .map(board->BoardResponse.from(board, isLikedByMe(getUserIdFromToken(), board.getId())))
//                .toList();
        List<Integer> likeBoardIds = likeRepository.findLikedBoardIdsByUserId(getUserIdFromToken());
       return     boards.stream()
                .map(board ->BoardResponse.from(board,likeBoardIds.contains(board.getId())))
                .toList();

    }



    public boolean isLikedByMe(int userId, int boardId){
        return likeRepository.existsByUserIdAndBoardId(userId, boardId);

    }

    public int getUserIdFromToken(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username).getId();
    }

}

