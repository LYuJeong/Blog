package com.cos.blog.controller;

import com.cos.blog.config.auth.PrincipalDetail;
import com.cos.blog.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class BoardController {

    @Autowired
    private BoardService boardService;


    //컨트롤러에서 세션을 어떻게 찾는지?
    //@AuthenticationPrincipal PrincipalDetail principal
    // /WEB-INF/views/indes.jsp
    @GetMapping({"","/"})
    public String index(
            Model model,
            @PageableDefault(size=3,sort="id",direction = Sort.Direction.DESC) Pageable pageable
    ){

        int startPage = ((pageable.getPageNumber()-1) / 10) * 10 + 1;
        pageable.getPageSize();
        int endPage = startPage + 10 - 1  > pageable.getPageSize() ? pageable.getPageSize() : startPage + 10 - 1;
        model.addAttribute("startPageNo", startPage);
        model.addAttribute("endPageNo", endPage);
        model.addAttribute("boards",boardService.글목록(pageable));
        return "index"; //viewResolver 작동 해당 인덱스 페이지로 모델의 정보를 가지고 이동
    }

    @GetMapping("/board/{id}")
    public String findById(@PathVariable int id,Model model){
        model.addAttribute("board",boardService.글상세보기(id));
        return "board/detail";
    }

    @GetMapping("/board/{id}/updateForm")
    public String updateForm(@PathVariable int id, Model model){
        model.addAttribute("board",boardService.글상세보기(id));
        return "board/updateForm";
    }

    //USER 권한이 필요
    @GetMapping("/board/saveForm")
    public String saveForm(){
        return "board/saveForm";
    }

}
