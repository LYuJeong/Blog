package com.cos.blog.repository;

import com.cos.blog.dto.ReplySaveRequestDto;
import com.cos.blog.model.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;


public interface ReplyRepository extends JpaRepository<Reply,Integer> {

    //네이티브 쿼리를 사용하면 영속화 시키는 작업을 하지 않아도 된다.
    @Modifying
    @Query(value="INSERT INTO reply(userId, boardId, content, createDate) VALUES(?1,?2,?3,now())",nativeQuery = true)
    int mSave(int userId, int boardId, String content); //업데이트된 행의 개수를 리턴해줌.
}
