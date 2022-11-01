package com.cos.blog.controller;

import com.cos.blog.config.auth.PrincipalDetail;
import com.cos.blog.model.KakaoProfile;
import com.cos.blog.model.OAuthToken;
import com.cos.blog.model.User;
import com.cos.blog.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

// 인증이 안된 사용자들이 출입할 수 있는 경로를 /auth/** 허용
//그냥 주소가 /이면 index.jps 허용
// static 이하에 있는 /js/**, /css/**, /image/** 허용

@Controller
public class UserController {

    //실제로는 절대 노출되면 안되는 값.노출되면 모든 OAuth로그인이 뚫린다고 보면된다. 절대 노출x
    @Value("{cos.key}")
    private String cosKey;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @GetMapping("/auth/joinForm") //인증이 필요없는 부분엔 auth를 붙힘
    public String joinForm(){

    return "user/joinForm";
    }

    @GetMapping("/auth/loginForm")
    public String loginForm(){

        return "user/loginForm";
    }


    @GetMapping("/user/updateForm")
    public String updateForm(@AuthenticationPrincipal PrincipalDetail principal){
        return "user/updateForm";
    }

    @GetMapping("/auth/kakao/callback")
    public  String kakaoCallback(String code){ //@ResponseBody를 붙히면 Data를 리턴해준다.
        //POST방식으로 key=value 데이터를 요청 (카카오쪽으로)
        //사용되는 라이브러리 종류 Retrofit2 , OkHttp, RestTemplate
        RestTemplate rt=new RestTemplate();

        //HttpHeader 오브젝트 생성
       HttpHeaders headers=new HttpHeaders();
        headers.add("Content-type","application/x-www-form-urlencoded;charset=utf-8");

        //HttpBody 오브젝트 생성
        //헤더에 컨텐트 타입을 담아야함.내가 전송할 http body 데이터가 key=value 형태의 데이터라고 알려주는것
        MultiValueMap<String,String> params=new LinkedMultiValueMap<>();
        params.add("grant_type","authorization_code");
        params.add("client_id","1103ccf0f5a57bd599bbf1fdc078e6b9");
        params.add("redirect_uri","http://localhost:8000/auth/kakao/callback");
        params.add("code",code);

        //HttpHeader와 HttpBody를 하나의 오브젝트에 담기, 왜 넣냐면 exchange메서드가 HttpEntity를 넣게 되어있기때문에.
        HttpEntity<MultiValueMap<String,String>> kakaoTokenRequest=
                new HttpEntity<>(params,headers);

        //Http 요청하기 - Post방식으로 - 그리고 response 변수의 응답 받음.
        ResponseEntity<String> response=rt.exchange(
          "https://kauth.kakao.com/oauth/token", //토큰발급주소
                HttpMethod.POST, //요청메서드가 무엇인지
                kakaoTokenRequest, //헤더값,바디값이 들어가있는 데이터
                String.class //응답을 받을 클래스
        );
        //Gson, Json Simple, ObjectMapper
        ObjectMapper objectMapper=new ObjectMapper();
        OAuthToken oauthToken=null;
        try {
            oauthToken=objectMapper.readValue(response.getBody(),OAuthToken.class);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        }catch (JsonProcessingException e){
            e.printStackTrace();
        }

        System.out.println("카카오 액세스 토큰 : "+oauthToken.getAccess_token());

        RestTemplate rt2=new RestTemplate();

        //HttpHeader 오브젝트 생성
        HttpHeaders headers2=new HttpHeaders();
        headers2.add("Authorization","Bearer "+oauthToken.getAccess_token());
        headers2.add("Content-type","application/x-www-form-urlencoded;charset=utf-8");

        //HttpHeader와 HttpBody를 하나의 오브젝트에 담기, 왜 넣냐면 exchange메서드가 HttpEntity를 넣게 되어있기때문에.
        HttpEntity<MultiValueMap<String,String>> kakaoProfileRequest2=
                new HttpEntity<>(headers2);

        //Http 요청하기 - Post방식으로 - 그리고 response 변수의 응답 받음.
        ResponseEntity<String> response2=rt2.exchange(
                "https://kapi.kakao.com//v2/user/me",
                HttpMethod.POST, //요청메서드가 무엇인지
                kakaoProfileRequest2, //헤더값,바디값이 들어가있는 데이터
                String.class //응답을 받을 클래스
        );

        ObjectMapper objectMapper2=new ObjectMapper();
        KakaoProfile kakaoProfile=null;
        try {
           kakaoProfile=objectMapper2.readValue(response2.getBody(),KakaoProfile.class);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        }catch (JsonProcessingException e){
            e.printStackTrace();
        }

        //User 오브젝트 : username, password,email
        System.out.println("카카오 아이디(번호) : "+kakaoProfile.getId());
        System.out.println("카카오 이메일 : "+ kakaoProfile.getKakao_account().getEmail());

        System.out.println("블로그 유저네임: "+kakaoProfile.getKakao_account().getEmail()+"_"+kakaoProfile.getId());
        System.out.println("블로그서버 이메일: "+kakaoProfile.getKakao_account().getEmail());
       // UUID garbagePassword=UUID.randomUUID();
        //UUID란 -> 중복되지 않는 어떤 특정 값을 만들어내는 알고리즘
        System.out.println("블로그서버 패스워드: "+cosKey);

        User kakaoUser=User.builder()
                        .username(kakaoProfile.getKakao_account().getEmail()+"_"+kakaoProfile.getId())
                        .password(cosKey)
                        .email(kakaoProfile.getKakao_account().getEmail())
                        .oauth("kakao")
                        .build();
        System.out.println("카카오 유저 : "+kakaoUser);
        // 가입자 혹은 비가입자 체크해서 처리
        User originUser = userService.회원찾기(kakaoUser.getUsername());
        if (originUser.getUsername()==null) { //찾기를해서 회원이 없으면 이때 회원가입
            System.out.println("기존 회원이 아니기에 자동 회원가입을 진행합니다.");
            userService.회원가입(kakaoUser);
        }
        System.out.println("자동 로그인을 진행합니다.");
        //로그인 처리
        Authentication authentication=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(kakaoUser.getUsername(),cosKey));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return "redirect:/";
    }
}
