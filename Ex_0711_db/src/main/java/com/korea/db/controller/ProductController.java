package com.korea.db.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;

import com.korea.db.mapper.ProductMapper;
import com.korea.db.vo.ProductVO;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ProductController {
	
	final ProductMapper productMapper;
	
	// @RequestMapping (Get요청과 Post요청 둘 다 받을 수 있다.)
	
	@GetMapping("register")			// Get요청
	public String register(Model model) {
		model.addAttribute("productVO", new ProductVO());
		return "product-insert";			// -> templates의 product-insert로 넘어감
	}
	
	@PostMapping("register")
	public RedirectView register(ProductVO productVO) {
		// 메서드 호출시 선언부 쪽으로 가서 모든 명령을 수행하고 호출한 족으로 흐름이 돌아온다.
		productMapper.insert(productVO);
		return new RedirectView("list");	// 우리가 마치 클라이언트가 다시 요청을 하는 것처럼 Mapping을 다시 요청
	}
	
	@GetMapping("list")	// -> 다시 요청
	public String list(Model model) {
		
		// 전체 조회
		// 상품 테이블에 있는 모든 상품 조회
		List<ProductVO> list = productMapper.selectAll();			// 1 / List로 받아야하므로 List
		
		// model에 바인딩하기
		model.addAttribute("list", list);
		return "product-list";
	}
}
