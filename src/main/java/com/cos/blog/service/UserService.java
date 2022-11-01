package com.cos.blog.service;

import com.cos.blog.model.RoleType;
import com.cos.blog.model.User;
import com.cos.blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//스프링이 컴포넌트 스캔을 통해서 Bean에 등록을 해줌. IoC를 해준다.
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Transactional(readOnly = true)
    public User 회원찾기(String username){ //orElseGet() -> 만약에 회원을 찾았는데 없으면 빈객체를 리턴
        User user=userRepository.findByUsername(username).orElseGet(()->{
            return new User();
        });
        return user;
    }

    @Transactional
    public void 회원가입(User user){
        String rawPassword=user.getPassword(); //비밀번호 원문
        String encPassword=encoder.encode(rawPassword); // 비밀번호 해쉬화
            user.setPassword(encPassword);
            user.setRole(RoleType.USER);
           userRepository.save(user);
    }

    @Transactional
    public void 회원수정(User user){
        //수정시에는 영속성 컨텍스트에 User오브젝트를 영속화시키고, 영속화된 User 오브젝트를 수정
        //select를 해서 User 오브젝트를 DB로부터 가져오는 이유는 영속화를 하기 위해서!
        // 영속화된 오브젝트를 변경하면 자동으로 DB에 UPDATE문을 날려줌.
        User persistance=userRepository.findById(user.getId()).orElseThrow(()->{
            return new IllegalArgumentException("회원 찾기 실패");
        });

        //Valicate 체크 , 카카오 사용자는 패스워드가 고정되어야함. 비밀번호 바뀔 수 없음, 이메일도 막아준다
        if(persistance.getOauth()==null || persistance.getOauth().equals("")){ //카카오유저가 아닌 일반 유저라면
           String rawPassword=user.getPassword();
            String encPassword=encoder.encode(rawPassword);
            persistance.setPassword(encPassword); //패스워드 수정
            persistance.setEmail(user.getEmail()); //이메일 수정
        }



        //회원수정 함수 종료시 = 서비스 종료 = 트랜잭션 종료= commit이 자동으로 됨.
        //영속화된 persistance 객체의 변화가 감지되면 더티체킹이 되어 변화된것들의 update문을 날려줌
    }

/*    @Transactional(readOnly = true) //select 할때 트랜잭션 시작,서비스 종료시에 트랜잭션 종료(정합성 유지)
    public User 로그인(User user){
       return userRepository.findByUsernameAndPassword(user.getUsername(),user.getPassword());
    }*/

}
