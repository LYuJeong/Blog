package com.cos.blog.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Reply { //답변
    @Id //primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) //프로젝트에서 연결된 DB의 넘버링 전략을 따라간다.
    private int id; //시퀀스

    @Column(nullable = false,length = 200)
    private String content;

    @ManyToOne
    //하나의 유저는 여러개의 답변을 달 수 있다.
    @JoinColumn(name="userId")
    private User user; //답변을 누가 걸었는지도 알아야함

    @ManyToOne // Many=Reply, One=Board 하나의 게시글에는 여러개의 답변이 달릴 수 있다.
    @JoinColumn(name="boardId") //FK=boardId
    private Board board;


    @CreationTimestamp
    private Timestamp createDate;

    @Override
    public String toString() {
        return "Reply{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", user=" + user +
                ", board=" + board +
                ", createDate=" + createDate +
                '}';
    }
}
