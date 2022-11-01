package com.cos.blog.test;


import com.cos.blog.model.RoleType;
import com.cos.blog.model.User;
import com.cos.blog.repository.UserRepository;
import net.bytebuddy.TypeCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.PrintStream;
import java.util.List;
import java.util.function.Supplier;

//html파일이 아니라 data를 리턴해주는 controller = RestController
@RestController
public class DummyControllerTest {

    @Autowired //의존성 주입
    private UserRepository userRepository;

    @DeleteMapping("/dummy/user/{id}")
    public String delete(@PathVariable int id){
        try {
            userRepository.deleteById(id);
        }catch (EmptyResultDataAccessException e){
        return "삭제 실패하였습니다. 해당 id는 DB에 없습니다.";
        }
        return "삭제되었습니다 id: "+id;
    }


    //save함수는 id를 전달하지 않으면 insert를 해주고
    //save함수는 id를 전달하면 해당 id에 대한 데이터가 있으면 update를 해주고
    //save함수는 id를 전달하면 해당 id에 대한 데이터가 없으면 insert를 한다.
    //email, password
    @Transactional //함수 종료시에 자동 commit이 됨.
    @PutMapping("/dummy/user/{id}") //더티체킹을 이용한 update
    public User updateUser(@PathVariable int id, @RequestBody User requestUser){
        //json데이터를 요청=>java Object(MessageConverter의 Jackson라이브러리가 변환해서 받아줌)
        System.out.println("id: "+id);
        System.out.println("password:" + requestUser.getPassword());
        System.out.println("email:" + requestUser.getEmail());

        User user=userRepository.findById(id).orElseThrow(()->{ //영속화가 일어남
         return new IllegalArgumentException("수정에 실해하였습니다.");
        });

        user.setPassword(requestUser.getPassword());
        user.setEmail(requestUser.getEmail());
       // userRepository.save(user);

        //더티 체킹(변경을 감지하는것)
        return user;
    }

    //localhost8000/blog/dummy/user
    @GetMapping("/dummy/users")
    public List<User> list(){
        return userRepository.findAll();
    }

    //한페이지당 2건의 데이터를 리턴받아 볼 예정
    @GetMapping("/dummy/user")
    public Page<User> pageList(
                //2건씩 가져오고 정렬은 id로 , id는 최신순으로
            @PageableDefault(size=2,sort="id",direction = Sort.Direction.DESC) Pageable pageable){
        Page<User> pagingUser = userRepository.findAll(pageable);

        List<User> users=pagingUser.getContent();
        return pagingUser;
    }

    //{id}주소로 파라메터 전달 받을 수 있음
    //http://localhost:8000/blog/dummy/user/3
    @GetMapping("/dummy/user/{id}")
    public User detail(@PathVariable int id){
    // userRepository.findById(id)의 반환형이 User라면 user/없는 번호를 찾으면 데이터베이스에서 user가 null이 된다
    // 그럼 null 이 리턴이 되는데 그럼 NPE가 발생된다.
    //따라서 Optional로 User 객체를 감싸서 가져올테니 null인지 아닌지 판단해서 return 해야함

        //람다식
/*        User user = userRepository.findById(id).orElseThrow(() -> {
            return new IllegalArgumentException("해당 사용자는 없습니다");
        });*/
        User user = userRepository.findById(id).orElseThrow(new Supplier<IllegalArgumentException>() {
            @Override
            public IllegalArgumentException get() {
                return new IllegalArgumentException("해당 유저는 없습니다. id :" +id);
            }
        });
        //요청 : 웹브라우저
        //user 객체=자바 오브젝트
        //변환 (웹브라우저가 이해할 수 있는 데이터) -> json
        //스프링부트 =MessageConverter가 응답시에 자동 작동
        //만약에 자바 오브젝트를 리턴하게 되면 MessageConverter가 Jackson 라이브러리를 호출해서
        //user 오브젝트를 json으로 변환해서 브라우저에게 던져줍니다.
        return user;
    }

    //localhost:8000/blog/dummy/join (요청)
    //http의 body에 username,password,email 데이터를 가지고 (요청)
    @PostMapping("/dummy/join")
    public String join(User user){ //key=value(약속된 규칙)형태의 데이터를 받아줌
        System.out.println("id: "+user.getId());
        System.out.println("role :"+user.getRole());
        System.out.println("username: "+ user.getUsername());
        System.out.println("password: "+ user.getPassword());
        System.out.println("email: "+ user.getEmail());
        System.out.println("createDate: "+user.getCreateDate());
        user.setRole(RoleType.USER);
        userRepository.save(user);
        return "회원가입이 완료되었습니다";
    }
}
