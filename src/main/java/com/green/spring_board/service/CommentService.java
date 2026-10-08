package com.green.spring_board.service;

import com.green.spring_board.dto.CommentCreateRequest;
import com.green.spring_board.dto.CommentResponse;
import com.green.spring_board.entity.Board;
import com.green.spring_board.entity.Comment;
import com.green.spring_board.entity.User;
import com.green.spring_board.exceptions.AuthorizationFailureException;
import com.green.spring_board.exceptions.ResourceNotFoundException;
import com.green.spring_board.repository.BoardRepository;
import com.green.spring_board.repository.CommentRepository;
import com.green.spring_board.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.List;

@AllArgsConstructor
@Service
public class CommentService {
    private final  BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public void postComment(int boardId, int userId, CommentCreateRequest commentCreateRequest){
        Comment comment = new Comment();
        Board board =boardRepository.findById(boardId)
                        .orElseThrow(()-> new ResourceNotFoundException("존재하지 않는 게시글입니다."));
        User user = userRepository.findById(userId)
                        .orElseThrow(()->new ResourceNotFoundException("존재하지 않는 유저입니다."));
        comment.setBoard(board);
        comment.setUser(user);
        comment.setContent(commentCreateRequest.getContent());
        commentRepository.save(comment);
    }

    public List<CommentResponse> readComments(int boardId){
        if(!boardRepository.existsById(boardId)){
            throw new ResourceNotFoundException("존재하지 않는 게시글입니다.");
        }

        return commentRepository.findByBoardIdAndIsDeletedFalse(boardId)
                .stream()
                .map(CommentResponse::from)
                .toList();

    }

    public void updateComment(int id, int userId, CommentCreateRequest commentUpdateRequest) {
       Comment comment = commentRepository.findById(id)
               .orElseThrow(()-> new ResourceNotFoundException("존재하지 않는 댓글입니다."));

       if(comment.isDeleted()){
           throw  new ResourceNotFoundException("삭제된 댓글입니다.");
       }
       if(comment.getUser().getId()!=userId){
           throw new AuthorizationFailureException("작성자만 수정 가능합니다.");
       }

       comment.setContent(commentUpdateRequest.getContent());
       commentRepository.save(comment);
    }

    public void deleteComment(int id, int userId){
        Comment comment= commentRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("존재하지 않는 댓글입니다."));
        if(comment.getUser().getId()!=userId){
            throw new AuthorizationFailureException("작성자만 삭제 가능합니다.");
        }
        comment.setDeleted(true);
        commentRepository.save(comment);
    }
}
