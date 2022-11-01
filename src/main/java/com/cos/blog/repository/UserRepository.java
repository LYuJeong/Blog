package com.cos.blog.repository;

import com.cos.blog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

//DAO
//자동으로 bean등록이 된다
//@Repository 어노테이션 생략가능하다
public interface UserRepository extends JpaRepository<User,Integer> { //User테이블이 관리하는 레파지토리,프라이머리키는 Integer
    //SELECT * FROM user WHERE username=1?; 해당쿼리가 실행이됨 꼭 카멜케이스로
    Optional<User> findByUsername(String username);
}

//JPA Naming 쿼리
//SELECT * FROM user WHERE username=? AND password=?;
//User findByUsernameAndPassword(String username, String password);

/*    @Query(value="SELECT * FROM user WHERE username=?1 AND password=?2", nativeQuery = true)
    User login(String username, String password);*/
