package com.shopme;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.shopme.category.CategoryService;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.product.Product;
import com.shopme.product.ProductService;

@Controller
public class MainController {

	@Autowired private CategoryService categoryService;
	@Autowired private ProductService productService;
	
	// 메인페이지 GET
	// 하위 카테고리가 없는 카테고리 리스트
	@GetMapping("")
	public String viewHomePage(Model model) {
		List<Category> listCategories = categoryService.listNoChildrenCategories();
		Page<Product> listProducts= productService.list4MostDiscountedProduct();
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("listProducts", listProducts);
		return "index2";
	}
	
	// 인증받지 않은 사용자(비로그인 사용자)일 경우에 로그인 페이지 GET
	@GetMapping("/login")
	public String viewLoginPage() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return "login";
		}
		
		return "redirect:/";
	}	
}
