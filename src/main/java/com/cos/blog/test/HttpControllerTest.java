package com.cos.blog.test;

import org.springframework.web.bind.annotation.*;

//사용자가 요청 ->응답(HTML 파일) ->Controller
//사용자가 요청 -> 응답(Data)
@RestController
public class HttpControllerTest {

    private static final String TAG="HttpControllerTest: ";

    @GetMapping("/http/lombok")
    public String lombokTest(){
        Member m1=Member.builder().username("ssar").password("123").email("ssar@nate.com").build();
        System.out.println(TAG+"getter"+m1.getUsername());
        m1.setUsername("cos");
        System.out.println(TAG+"setter"+m1.getUsername());
        return "lombok test 완료";
    }

    //인터넷 브라우저 요청은 무조건 get요청밖에 할 수 없다
    //http://localhost:8080/http/get(selet)
    @GetMapping("/http/get")
    public String getTest(Member m){

        return  "get 요청 : "+m.getId()+","+m.getUsername()+","+m.getPassword()+","+m.getEmail();
    }

    //http://localhost:8080/http/post(insert)
    @PostMapping("/http/post") //text/plain, application/json
    public String postTest(@RequestBody Member m){ //MessageConverter(스프링부트)
        return  "post 요청 : "+m.getId()+","+m.getUsername()+","+m.getPassword()+","+m.getEmail();
    }

    //http://localhost:8080/http/put(update)
    @PutMapping("/http/put")
    public String putTest(@RequestBody Member m){ //RequestBody를 이용하면 object로 매핑해서 받을 수 있다
        return  "put 요청"+m.getId()+","+m.getUsername()+","+m.getPassword()+","+m.getEmail();
    }

    //http://localhost:8080/http/delete(delete)
    @DeleteMapping("/http/delete")
    public String deleteTest(){
        return  "delete 요청";
    }
}
