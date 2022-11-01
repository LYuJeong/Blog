package com.cos.blog.handler;

import com.cos.blog.dto.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice //어디에서든 Exception이 발생하면 이쪽으로 하기 위함. 모든 익셉션이 들어왔을때 이 요청을 받게됨
@RestController
public class GlobalExceptionHandler {

    //모든 익셉션이 발생했을때
    @ExceptionHandler(value = Exception.class)
    public ResponseDto<String> handleArgumentException(Exception e){ //이 익셉션에 대한 에러는 매개변수에 전달되서
        return new ResponseDto<String>(HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage());
        // 이 부분이 리턴됨, HttpStatus.INTERNAL_SERVER_ERROR는 500번 에러임
    }
}
