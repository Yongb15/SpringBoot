package com.korea.board.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.korea.board.common.Common;
import com.korea.board.common.Common.Board;
import com.korea.board.dao.BoardDAO;
import com.korea.board.service.BoardService;
import com.korea.board.util.Paging;
import com.korea.board.vo.BoardVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board/*")
public class BoardController {

	private final BoardService boardService;
	
	@Autowired
	HttpSession session;
	
	@Autowired
	HttpServletRequest request;
	
	@GetMapping("board_list")
	public String list(Model model, @RequestParam(required=false, defaultValue="1") int page) {
		
		int start = (page-1) * Common.Board.BLOCKLIST+1;
		int end = start + Common.Board.BLOCKLIST-1;
		
		HashMap<String, Integer> map = new HashMap<>();
		map.put("start", start);
		map.put("end", end);
		
		//페이지 번호에 따른 전체 게시글 조회
		List<BoardVO> list = boardService.selectList(map);
		
		model.addAttribute("list",list);
		
		session.removeAttribute("show");
		
		//전체 게시물 수 구하기
		int rowTotal = boardService.getRowTotal();
		
		//페이지 메뉴 생성하기
		String pageMenu = Paging.getPaging("board_list", 
											page, 
											rowTotal, 
											Board.BLOCKLIST, 
											Board.BLOCKPAGE);
		
		model.addAttribute("pageMenu",pageMenu);
		
		return "/board/board_list";
	}
	
	@GetMapping("view")
	public String view(Model model,int idx, @RequestParam(name="page", required=false, defaultValue="1")int page) {
		BoardVO vo = boardService.selectOne(idx);
		
		model.addAttribute("vo",vo);
		
		//조회수 증가하기
		
		//1. 특정 key를 가진 세션을 호출
		String show = (String) session.getAttribute("show");
		//2. value가 null이면 넘어온 idx의 readhit를 1증가시키고
		//세션 key에 value를 세팅
		if(show == null) {
			int res = boardService.update_readhit(idx);
			session.setAttribute("show", "r");
		}
		return "/board/board_view";
	}
	
	@GetMapping("insert_form")
	public String insert_form(Model model, @RequestParam(name="page", required=false, defaultValue="1")int page)
	{
		model.addAttribute("vo", new BoardVO());

		// 반환값에다가 내가 보내줄 view의 경로
		return "/board/insert_form";
	}
	
	@PostMapping("insert")
	public RedirectView insert(BoardVO vo, @RequestParam(required=false, defaultValue="1")int page)
	{
		// 넘어온 데이터
		// 작성자, 제목, 내용, 비밀번호
		String ip = request.getRemoteAddr();
		
		vo.setIp(ip);
		
		int res = boardService.insert(vo);
		
		if(res > 0) {
			return new RedirectView("/board/board_list");
		}
		return null;
	}
	
	@PostMapping("del")
	@ResponseBody			// ajax일때 붙여야 함 @@ResponseBody / view에서 전달됨
	public String del(@RequestBody String body) {
		ObjectMapper om = new ObjectMapper();
		Map<String, String> data = null;
		
		try {					// 매개변수인 body를 넣음
			data = om.readValue(body, new TypeReference<Map<String, String>>() {});		// ? 왜 try-catch로 함?
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		int intIdx = Integer.parseInt(data.get("idx"));
		
		BoardVO vo = boardService.selectOne(intIdx);
		
		vo.setSubject("이미 삭제된 글입니다.");
		vo.setName("unknown");
		
		int res = boardService.del_update(vo);
		
		if(res > 0) {
			return "{\"param\":\"success\"}";
		}
		return "{\"param\":\"fail\"}";
	}
	
	@GetMapping("reply_form")
	public String reply_form(Model model, int idx)
	{
		model.addAttribute("vo", new BoardVO());
		model.addAttribute("idx", idx);
		
		// 반환값에다가 내가 보내줄 view의 경로
		return "/board/reply_form";
	}
	
	@PostMapping("reply")
	public RedirectView reply(BoardVO vo, @RequestParam(required=false, defaultValue="1")int page, int idx)
	{
		// 넘어온 데이터
		// 작성자, 제목, 내용, 비밀번호
		String ip = request.getRemoteAddr();
		
		vo.setIp(ip);
		
		
		
		
		int res = boardService.reply(vo);
		
		if(res > 0) {
			return new RedirectView("/board/board_list");
		}
		return null;
	}
}