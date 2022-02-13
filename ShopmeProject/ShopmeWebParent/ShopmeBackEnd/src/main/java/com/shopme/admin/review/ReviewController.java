package com.shopme.admin.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.paging.PagingAndSortingParam;
import com.shopme.common.entity.Review;
import com.shopme.common.exception.ReviewNotFoundException;

@Controller
public class ReviewController {
	private String defaultRedirectURL = "redirect:/reviews/page/1?sortField=reviewTime&sortDir=desc";
	
	@Autowired private ReviewService service;
	
	// 리뷰 목록 페이지 GET
	@GetMapping("/reviews")
	public String listFirstPage(Model model) {
		return defaultRedirectURL;
	}

	// 리뷰 목록 페이징 GET
	@GetMapping("/reviews/page/{pageNum}")
	public String listByPage(
			@PagingAndSortingParam(listName = "listReviews", moduleURL = "/reviews") PagingAndSortingHelper helper,
			@PathVariable(name = "pageNum") int pageNum) {

		service.listByPage(pageNum, helper);
		
		return "reviews/reviews";
	}
	
	// 리뷰 상세 GET
	@GetMapping("/reviews/detail/{id}")
	public String viewReview(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
		try {
			Review review = service.get(id);
			model.addAttribute("review", review);
			
			return "reviews/review_detail_modal";
		} catch (ReviewNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			return defaultRedirectURL;		
		}
	}
	
	// 리뷰 수정 폼 GET
	@GetMapping("/reviews/edit/{id}")
	public String editReview(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
		try {
			Review review = service.get(id);
			
			model.addAttribute("review", review);
			model.addAttribute("pageTitle", String.format("리뷰 수정 (ID: %d)", id));
			
			return "reviews/review_form";
		} catch (ReviewNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			return defaultRedirectURL;		
		}
	}	
	
	// 리뷰 저장 POST
	@PostMapping("/reviews/save")
	public String saveReview(Review reviewInForm, RedirectAttributes ra) {
		service.save(reviewInForm);		
		ra.addFlashAttribute("message", "리뷰 ID: " + reviewInForm.getId() + "가 수정되었습니다.");
		return defaultRedirectURL;		
	}
	
	// 리뷰 삭제 GET
	@GetMapping("/reviews/delete/{id}")
	public String deleteReview(@PathVariable("id") Integer id, RedirectAttributes ra) {
		try {
			service.delete(id);
			ra.addFlashAttribute("message", "리뷰 ID: " + id + "가 삭제되었습니다.");
		} catch (ReviewNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
		}
		
		return defaultRedirectURL;		
	}
}
