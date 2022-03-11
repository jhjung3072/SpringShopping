package com.shopme.review;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.ControllerHelper;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Review;
import com.shopme.common.entity.product.Product;
import com.shopme.common.exception.ProductNotFoundException;
import com.shopme.common.exception.ReviewNotFoundException;
import com.shopme.product.ProductService;
import com.shopme.review.vote.ReviewVoteService;

@Controller
public class ReviewController {
	private String defaultRedirectURL = "redirect:/reviews/page/1?sortField=reviewTime&sortDir=desc";
	
	@Autowired private ReviewService reviewService;
	@Autowired private ControllerHelper controllerHelper;
	@Autowired private ProductService productService;
	@Autowired private ReviewVoteService voteService;
	
	// 회원이 작성한 리뷰 목록 GET
	@GetMapping("/reviews")
	public String listFirstPage(Model model) {
		return defaultRedirectURL;
	}
	
	// 회원이 작성한 리뷰 목록 페이징 GET
	@GetMapping("/reviews/page/{pageNum}") 
	public String listReviewsByCustomerByPage(Model model, HttpServletRequest request,
							@PathVariable(name = "pageNum") int pageNum,
							String keyword, String sortField, String sortDir) {
		Customer customer = controllerHelper.getAuthenticatedCustomer(request);
		Page<Review> page = reviewService.listByCustomerByPage(customer, keyword, pageNum, sortField, sortDir);		
		List<Review> listReviews = page.getContent();
		
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		model.addAttribute("moduleURL", "/reviews");
		
		model.addAttribute("listReviews", listReviews);

		long startCount = (pageNum - 1) * ReviewService.REVIEWS_PER_PAGE + 1;
		model.addAttribute("startCount", startCount);
		
		long endCount = startCount + ReviewService.REVIEWS_PER_PAGE - 1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		model.addAttribute("endCount", endCount);
		
		return "reviews/reviews_customer";
	}
	
	// 회원이 작성한 리뷰 상세 GET
	@GetMapping("/reviews/detail/{id}")
	public String viewReview(@PathVariable("id") Integer id, Model model, 
			RedirectAttributes ra, HttpServletRequest request) {
		Customer customer = controllerHelper.getAuthenticatedCustomer(request);
		try {
			Review review = reviewService.getByCustomerAndId(customer, id);
			model.addAttribute("review", review);
			
			return "reviews/review_detail_modal";
		} catch (ReviewNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			return defaultRedirectURL;		
		}
	}
	
	// 상품 목록에서 리뷰 모두 보기 GET
	@GetMapping("/ratings/{productAlias}/page/{pageNum}") 
	public String listByProductByPage(Model model,
				@PathVariable(name = "productAlias") String productAlias,
				@PathVariable(name = "pageNum") int pageNum,
				String sortField, String sortDir, HttpServletRequest request) {
		
		Product product = null;
		
		try {
			product = productService.getProduct(productAlias);
		} catch (ProductNotFoundException ex) {
			return "error/404";
		}
		
		Page<Review> page = reviewService.listByProduct(product, pageNum, sortField, sortDir);
		List<Review> listReviews = page.getContent();
		
		Customer customer=controllerHelper.getAuthenticatedCustomer(request);
		
		if (customer != null) {
			// 해당 회원이 상품을 추천했는지 비추천했는지 색깔 구분
			voteService.markReviewsVotedForProductByCustomer(listReviews, product.getId(), customer.getId());
		}
		
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		
		model.addAttribute("listReviews", listReviews);
		model.addAttribute("product", product);

		long startCount = (pageNum - 1) * ReviewService.REVIEWS_PER_PAGE + 1;
		model.addAttribute("startCount", startCount);
		
		long endCount = startCount + ReviewService.REVIEWS_PER_PAGE - 1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		model.addAttribute("endCount", endCount);
		model.addAttribute("pageTitle", "리뷰: " + product.getShortName());
		
		return "reviews/reviews_product";
	}
	
	// 상품 목록에서 리뷰 모두 보기 페이징 GET
	@GetMapping("/ratings/{productAlias}")
	public String listByProductFirstPage(@PathVariable(name = "productAlias") String productAlias, Model model,
				HttpServletRequest request) {
		return listByProductByPage(model, productAlias, 1, "reviewTime", "desc",request);
	}	
	
	// 리뷰 작성 폼 GET
	@GetMapping("/write_review/product/{productId}")
	public String showViewForm(@PathVariable("productId") Integer productId, Model model,
			HttpServletRequest request) {
		
		Review review = new Review();
		
		Product product = null;
		
		try {
			product = productService.getProduct(productId);
		} catch (ProductNotFoundException ex) {
			return "error/404";
		}
		
		Customer customer = controllerHelper.getAuthenticatedCustomer(request);
		boolean customerReviewed = reviewService.didCustomerReviewProduct(customer, product.getId());
		
		if (customerReviewed) { // 이미 리뷰를 작성했다면,
			model.addAttribute("customerReviewed", customerReviewed);
		} else { // 리뷰를 작성하지 않았다면,
			boolean customerCanReview = reviewService.canCustomerReviewProduct(customer, product.getId());
			
			if (customerCanReview) {
				model.addAttribute("customerCanReview", customerCanReview);				
			} else { // 구매를 하지 않았거나 아직 상품을 수령하지 않았다면,
				model.addAttribute("NoReviewPermission", true);
			}
		}		
		
		model.addAttribute("product", product);
		model.addAttribute("review", review);
		
		return "reviews/review_form";
	}
	
	// 리뷰 작성 POST
	@PostMapping("/post_review")
	public String saveReview(Model model, Review review, Integer productId, HttpServletRequest request) {
		Customer customer = controllerHelper.getAuthenticatedCustomer(request);
		
		Product product = null;
		
		try {
			product = productService.getProduct(productId);
		} catch (ProductNotFoundException ex) {
			return "error/404";
		}
		
		review.setProduct(product);
		review.setCustomer(customer);
		
		Review savedReview = reviewService.save(review);
		
		model.addAttribute("review", savedReview);
		
		return "reviews/review_done";
	}
}
