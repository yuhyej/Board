package com.example.boardproj.controller;


import com.example.boardproj.dto.BoardDTO;
import com.example.boardproj.dto.BoardImgDTO;
import com.example.boardproj.dto.PageRequestDTO;
import com.example.boardproj.dto.PageResponseDTO;
import com.example.boardproj.entity.Board;
import com.example.boardproj.service.BoardImgService;
import com.example.boardproj.service.BoardService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/board")
public class BoardController {

    private  final BoardService boardService;

    private final BoardImgService boardImgService;

    @GetMapping("/b")
    public @ResponseBody String bb(){

        return "홍길동";
    }


    //등록보기페이지
    @GetMapping("/register")
    public String registerGet(BoardDTO boardDTO){

        return "board/register";
    }


    //글을 디비에 등록하는 포스트
    @PostMapping("register")
    public String registerPost(@Valid BoardDTO boardDTO, BindingResult bindingResult,MultipartFile[] multipartFile){

        log.info("컨트롤러로 들어온값 : " + boardDTO);
//        log.info("컨트롤러로 들어온값 : " + multipartFile.getOriginalFilename());


        if (bindingResult.hasErrors()) {
            log.info("유효성 검삭나 문제가 있다 아래 로그는 모든 문제를 출력해준다.");
            log.info(bindingResult.getAllErrors());
            // 유효성검사간 문제가 있다면 다시 문제의 페이지로 돌려주고
            //사용자가 볼 수 있어야 한다. 문제를
            // model에 값을 할당하지 않아도 가지고 간다.

            return "board/register";

        }

       //boardService.register(boardDTO);
        //파일업로드 추가

        boardService.register(boardDTO, multipartFile);

        return "redirect:/board/list";

    }


    @GetMapping("/read")
    public String read(Long bno, Model model, PageRequestDTO pageRequestDTO) {
        log.info("컨트롤러 읽기로 들어온 게시글번호 : " + bno);
        log.info("컨트롤러 읽기로 들어온 페이징처리 : " + pageRequestDTO);

        if (bno == null || bno.equals("")) {
            log.info("들어온 bno가 이상함");

            return "redirect:/board/list";
        }


        BoardDTO boardDTO = new BoardDTO();
        try {
            //본문 데이터 // 아직 단방향중
            // 양방향 아님
            boardDTO = boardService.read(bno);

            List<BoardImgDTO> boardImgDTOList =
                    boardImgService.boardImgread(bno);
            log.info("컨트롤러에서 서비스 read() 불러온값 : ");
            log.info(boardDTO);
            model.addAttribute("boardDTO", boardDTO);
            model.addAttribute("boardImgDTOList", boardImgDTOList );

        } catch (EntityNotFoundException e) {
            log.info("bno로 값을 찾지 못함 ");
            log.info("pk값이 bno인 데이터가 없음 ");

            return "redirect:/board/list";
        }

        return "board/read";
    }


    @GetMapping("/list")
    public String list(Model model, PageRequestDTO pageRequestDTO){

        log.info(pageRequestDTO);

//        if(pageRequestDTO.getType().equals("t")){
//            log.info("제목에서 검색하세요");
//            log.info("키워드는 " + pageRequestDTO.getKeyword());
//        }
//
//        if(pageRequestDTO.getType().equals("c")){
//            log.info("내용에서 검색하세요");
//            log.info("키워드는 " + pageRequestDTO.getKeyword());
//        }


//        List<BoardDTO> boardDTOList =
//        boardService.list();

        PageResponseDTO<BoardDTO> pageResponseDTO =
//                boardService.pagelist(pageRequestDTO);
                boardService.pageListsearchdsl(pageRequestDTO);

        model.addAttribute("pageResponseDTO", pageResponseDTO);





        return "board/list";

    }


    @GetMapping("/update")
    public String update(Long bno, Model model, PageRequestDTO pageRequestDTO){

        if (bno == null || bno.equals("")) {
            log.info("들어온 bno가 이상함");

            return "redirect:/board/list?"+pageRequestDTO.getLink();
        }

        try {

            BoardDTO boardDTO = boardService.read(bno);

            List<BoardImgDTO> boardImgDTOList =
                    boardImgService.boardImgread(bno);

            model.addAttribute("boardDTO", boardDTO);

            model.addAttribute("boardImgDTOList", boardImgDTOList);

        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            log.info("pk로 검색한 값이 없음");
            return "redirect:/board/list?"+pageRequestDTO.getLink();
        }


        return "board/update";
    }


    @PostMapping("/update")
    public String updatePost(@Valid BoardDTO boardDTO, BindingResult bindingResult,
                             Long[] delino, PageRequestDTO pageRequestDTO, Model model, MultipartFile[] multipartFile){

        log.info("업데이트포스 " + boardDTO);
        log.info("업데이트포스 " + pageRequestDTO);


        if(bindingResult.hasErrors()){

            log.info("유효성검사 확인!!");
            log.info(bindingResult.getAllErrors()); //유효성 내용 콘솔창에 출력

            return "board/update";

        }


        try {
            boardService.update(boardDTO, multipartFile, delino);
//            if (multipartFile !=null && !multipartFile.getOriginalFilename().equals("")){
//                log.info("여기 사진 있어요");
//                log.info("업데이트포스 " + multipartFile.getOriginalFilename());
//
//                boardImgService.boardImgregister(boardDTO.getBno(), multipartFile);
//            }
//
//            if (delino !=null && !delino[0].equals("")){
//                log.info("업데이트포스" + Arrays.toString(delino));
//
//                //사진삭제
//                for (Long ino : delino){
//                    boardImgService.del(ino);
//                }
//
//            }

        }catch (EntityNotFoundException e){

            //model.addAttribute("msg", "현재 수정하려는 글번호가 옳바르지 않습니다.");

            //return  "board/update";
            return "redirect:/board/list?"+pageRequestDTO.getLink();

        }


        return "redirect:/board/list?"+pageRequestDTO.getLink();
    }

    @PostMapping("/del")
    public String del(Long bno){

        log.info(bno);

        boardService.del(bno);

        return "redirect:/board/list";
    }

}
