package com.shopme.product;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.shopme.question.QuestionService;
import com.shopme.question.vote.QuestionVoteService;
import com.shopme.ControllerHelper;
import com.shopme.category.CategoryService;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Question;
import com.shopme.common.entity.Review;
import com.shopme.common.entity.product.Product;
import com.shopme.common.exception.CategoryNotFoundException;
import com.shopme.common.exception.ProductNotFoundException;
import com.shopme.review.ReviewService;
import com.shopme.review.vote.ReviewVoteService;

@Controller
public class ProductController {
	@Autowired private ProductService productService;
	@Autowired private CategoryService categoryService;
	@Autowired private ReviewService reviewService;	
	@Autowired private ControllerHelper controllerHelper;
	@Autowired private ReviewVoteService reviewVoteService;
	@Autowired private QuestionVoteService questionVoteService;
	@Autowired private QuestionService questionService;
	// 카테고리 목록 GET
	@GetMapping("/c/{category_alias}")
	public String viewCategoryFirstPage(@PathVariable("category_alias") String alias,
			Model model) {
		return viewCategoryByPage(alias, 1, model);
	}
	
	// 카테고리별 상품 목록 페이징 GET
	@GetMapping("/c/{category_alias}/page/{pageNum}")
	public String viewCategoryByPage(@PathVariable("category_alias") String alias,
			@PathVariable("pageNum") int pageNum,
			Model model) {
		try {
			Category category = categoryService.getCategory(alias);		
			List<Category> listCategoryParents = categoryService.getCategoryParents(category);
			
			Page<Product> pageProducts = productService.listByCategory(pageNum, category.getId());
			List<Product> listProducts = pageProducts.getContent();
			
			long startCount = (pageNum - 1) * ProductService.PRODUCTS_PER_PAGE + 1;
			long endCount = startCount + ProductService.PRODUCTS_PER_PAGE - 1;
			if (endCount > pageProducts.getTotalElements()) {
				endCount = pageProducts.getTotalElements();
			}
			
			
			model.addAttribute("currentPage", pageNum);
			model.addAttribute("totalPages", pageProducts.getTotalPages());
			model.addAttribute("startCount", startCount);
			model.addAttribute("endCount", endCount);
			model.addAttribute("totalItems", pageProducts.getTotalElements());
			model.addAttribute("pageTitle", category.getName());
			model.addAttribute("listCategoryParents", listCategoryParents);
			model.addAttribute("listProducts", listProducts);
			model.addAttribute("category", category);
			
			return "product/products_by_category";
		} catch (CategoryNotFoundException ex) {
			return "error/404";
		}
	}
	
	// 상품 상세 페이지 GET
	@GetMapping("/p/{product_alias}")
	public String viewProductDetail(@PathVariable("product_alias") String alias, Model model,
			HttpServletRequest request) {
		
		try {
			Product product = productService.getProduct(alias);
			List<Category> listCategoryParents = categoryService.getCategoryParents(product.getCategory());
			// 질문중 추천수가 가장 많은 3개
			List<Question> listQuestions = questionService.getTop3VotedQuestions(product.getId());
			// 리뷰중 가장 추천수가 많은 3개 상품
			Page<Review> listReviews = reviewService.list3MostVotedReviewsByProduct(product);
			
			Customer customer = controllerHelper.getAuthenticatedCustomer(request);
			
			if (customer != null) {
				boolean customerReviewed = reviewService.didCustomerReviewProduct(customer, product.getId());
				// 해당 회원이 리뷰를 추천했는지 비추천했는지 색깔 구분 
				reviewVoteService.markReviewsVotedForProductByCustomer(listReviews.getContent(), product.getId(), customer.getId());
				// 해당 회원이 질문을 추천했는지 비추천했는지 색깔 구분
				questionVoteService.markQuestionsVotedForProductByCustomer(listQuestions, product.getId(), customer.getId());
				if (customerReviewed) { // 리뷰 이미 작성
					model.addAttribute("customerReviewed", customerReviewed);
				} else { // 리뷰 작성 가능
					boolean customerCanReview = reviewService.canCustomerReviewProduct(customer, product.getId());
					model.addAttribute("customerCanReview", customerCanReview);
				}
			}
			// 질문 개수와 답변이 달린 질문 개수
			int numberOfQuestions = questionService.getNumberOfQuestions(product.getId());
			int numberOfAnsweredQuestions = questionService.getNumberOfAnsweredQuestions(product.getId());

			model.addAttribute("listQuestions", listQuestions);			
			model.addAttribute("numberOfQuestions", numberOfQuestions);
			model.addAttribute("numberOfAnsweredQuestions", numberOfAnsweredQuestions);
			model.addAttribute("listCategoryParents", listCategoryParents);
			model.addAttribute("product", product);
			model.addAttribute("listReviews", listReviews);
			model.addAttribute("pageTitle", product.getShortName());
			
			return "product/product_detail";
		} catch (ProductNotFoundException e) {
			return "error/404";
		}
	}
	
	// 키워드 검색
	@GetMapping("/search")
	public String searchFirstPage(String keyword, Model model) {
		return searchByPage(keyword, 1, model);
	}
	
	// 키워드 검색 페이징
	@GetMapping("/search/page/{pageNum}")
	public String searchByPage(String keyword,
			@PathVariable("pageNum") int pageNum,
			Model model) {
		Page<Product> pageProducts = productService.search(keyword, pageNum);
		List<Product> listResult = pageProducts.getContent();
		
		long startCount = (pageNum - 1) * ProductService.SEARCH_RESULTS_PER_PAGE + 1;
		long endCount = startCount + ProductService.SEARCH_RESULTS_PER_PAGE - 1;
		if (endCount > pageProducts.getTotalElements()) {
			endCount = pageProducts.getTotalElements();
		}
		
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", pageProducts.getTotalPages());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", pageProducts.getTotalElements());
		model.addAttribute("pageTitle", keyword + " - 검색 결과");
		
		model.addAttribute("keyword", keyword);
		model.addAttribute("searchKeyword", keyword);
		model.addAttribute("listResult", listResult);
		
		return "product/search_result";
	}		
}
