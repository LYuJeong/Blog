package com.cos.blog.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.sql.Timestamp;




@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder //빌더패턴 !
//ORM -> Java(다른언어) Object -> 테이블로 매핑해주는 기술
//@DynamicInsert //insert 할 때 null 인 필드 제외시켜준다.
@Entity // User클래스가 MySql에 테이블이 생성이 된다.
public class User {
    @Id //primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) //프로젝트에서 연결된 DB의 넘버링 전략을 따라간다.
    private int id; //시퀀스

    @Column(nullable = false,length = 100, unique = true) //unique=true 이면 동일한 우저네임이 생성될 수 없음
    private String username; //아이디

    @Column(nullable = false,length = 100)
    private String password;

    @Column(nullable = false, length = 50)
    private String email;

   // @ColumnDefault("'user'") //ColumnDefault는 ''로 문자열이라는것을 알려주어야함
    //DB는 RoleType이라는게 없다.
    @Enumerated(EnumType.STRING)
    private RoleType role; //Enum을 쓰는게 좋다. //ADMIN,USER

    private String oauth; //kakao, google

    @CreationTimestamp // 시간이 자동입력
    private Timestamp createDate;
}
