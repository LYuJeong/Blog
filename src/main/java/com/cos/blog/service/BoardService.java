package com.cos.blog.service;

import com.cos.blog.dto.ReplySaveRequestDto;
import com.cos.blog.model.Board;
import com.cos.blog.model.Reply;
import com.cos.blog.model.RoleType;
import com.cos.blog.model.User;
import com.cos.blog.repository.BoardRepository;
import com.cos.blog.repository.ReplyRepository;
import com.cos.blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class BoardService {
    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private ReplyRepository replyRepository;


    @Transactional
    public void 글쓰기(Board board,User user){// 글쓰기를 할때 title, content와 누가 적었는지 알아야하기 때문에 User 오브젝트가 필요함
        board.setCount(0); //조회수 0으로 셋팅
        board.setUser(user);
        boardRepository.save(board);
    }

    @Transactional(readOnly = true)
    public Page<Board> 글목록(Pageable pageable){
       return boardRepository.findAll(pageable); //페이지에 대하여 호출을 하면 리턴타입이 List가 아닌 Page가 됨
    }

    @Transactional(readOnly = true)
    public Board 글상세보기(int id){
        return boardRepository.findById(id)
                .orElseThrow(()->{
                    return new IllegalArgumentException("글 상세보기 실패: 아이디를 찾을 수 없습니다.");
                });
    }
    @Transactional
    public void 글삭제하기(int id){
         boardRepository.deleteById(id);
    }

    @Transactional
    public void 글수정하기(int id,Board requestBoard){
        Board board=boardRepository.findById(id)
                .orElseThrow(()->{
                    return new IllegalArgumentException("글수정 실패: 아이디를 찾을 수 없습니다.");
                }); // 영속성 컨텍스트에 board가 들어옴
        board.setTitle(requestBoard.getTitle());
        board.setContent(requestBoard.getContent());
        //해당 함수가 종료시에 트랜잭션이 Service가 종료될 때 트랜잭션이 종료됩니다. 이때 더티체킹 - 자동업데이트가 flush
    }

    @Transactional
    public void 댓글쓰기(ReplySaveRequestDto replySaveRequestDto){

/*        User user=userRepository.findById(replySaveRequestDto.getUserId())
                .orElseThrow(()->{
                    return new IllegalArgumentException("댓글 쓰기 실패: 유저 id를 찾을 수 없습니다.");
                });

       Board board=boardRepository.findById(replySaveRequestDto.getBoardId())
               .orElseThrow(()->{
                   return new IllegalArgumentException("댓글 쓰기 실패: 게시글 id를 찾을 수 없습니다.");
               });
       Reply reply=Reply.builder()
                        .user(user)
                        .board(board)
                        .content(replySaveRequestDto.getContent())
                        .build();

                     replyRepository.save(reply);
                        */
        int result = replyRepository.mSave(replySaveRequestDto.getUserId(), replySaveRequestDto.getBoardId(), replySaveRequestDto.getContent());
    }

    @Transactional
    public void 댓글삭제(int replyId){
        replyRepository.deleteById(replyId);
    }

}