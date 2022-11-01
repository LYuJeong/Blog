package com.cos.blog.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder //빌더패턴 !
@Entity
public class Board {
    //하나의 게시글은 1명의 유저가 쓸 수 있다. 여러명이 하나의 게시글을 가진게 아님.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false,length = 100)
    private String title;

    @Lob //데용량 데이터
    private String content; //섬머노트 라이브러리 <HTML>태그가 섞여서 디자인이 됨.

    //@ColumnDefault("0")
    private int count; //조회수

    //FetchType.EAGER -> Board 테이블을 select 하면 무조건 user의 정보를 가지고 와야함
    @ManyToOne(fetch=FetchType.EAGER) // 연관관계를 설정.  Many =Board, User=One 한명의 유저는 여러개의 게시글을 쓸 수 있다.
    // 여러개의 게시글은 한명의 유저에 의해 쓰일 수 있다. 1대다
    //하나의 게시글은 여러명에 의해서 작성되는 것이 아니라 1명이 작성할 수 있다.
    @JoinColumn(name="userId")//실제로 DB에 만들어 질때는 userId칼럼으로 만들어지게 된다.
    private User user; //DB는 오브젝트를 저장할 수 없다. FK,자바는 오브젝트를 저장할 수 있다.
    //User user는 User테이블을 참조, 자동으로 FK가 생성됨. 참조하는 PK는 User테이블의 id

    //FetchType.LAZY -> 댓글이 필요하지 않을 수도 있음. 필요할때만 땡겨오는것. 만약에 댓글에 펼치기라는 버튼이 있다고 가정하면,
    //펼치기 버튼을 누르기 전에 댓글은 필요하지 않다. 하지만 펼치기 같은 기능이 없다면 EAGER을 사용헤야함.
    @OneToMany(mappedBy = "board",fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    //mappedBy 연관관계의 주인이 아니다.(난 FK가 아니다).DB에 컬럼을 만들지마라 mappedBy뒤에 오는 것은 Reply에 있는필드이름
    ////CascadeType.REMOVE -> 게시글을 지우면 댓글도 삭제되게
    @JsonIgnoreProperties({"board"}) //무한참조방지,이 보드객체로 리플리를 뽑을때는 Reply의 Board를 제이슨으로 파싱하지 않음.
    @OrderBy("id desc ") //내림차순 정렬
    //FK는 Board테이블에 있는것이 아니라 Reply테이블의 board이 FK이다.
    //하나의 게시글 =One , 여러개의 답변 = Reply
    //@JoinColumn(name="replyId")가 필요가 없게된다. 왜냐면 Board 테이블에 ReplyId라는 FK가 필요가 없고, 데이터베이스에 만들어지면 안된다
    //만약 FK로 replyId가 들어가게 되면 1정규화의 법칙(원자성->하나의 컬럼은 하나의 값을 가짐)이 깨지게 된다.
    private List<Reply> replys; //reply는 연관관계의 주인이 아니다. reply는 Board를 select 할 때 join문을 통해서 값을 얻기 위해 필요한 것
    //하나의 게시글의 답변은 하나가 될 수도 있고 여러개가 될 수도 있다.
    // 즉 join을 해서 가지고 오는 Reply 정보가 하나이면 안된다.
    //따라서 Reply는 List가 되어야 함.

    @CreationTimestamp
    private Timestamp createDate;
}
