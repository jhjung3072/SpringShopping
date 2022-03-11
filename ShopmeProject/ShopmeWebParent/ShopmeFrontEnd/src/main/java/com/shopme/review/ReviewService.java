package com.shopme.review;

import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Review;
import com.shopme.common.entity.order.OrderStatus;
import com.shopme.common.entity.product.Product;
import com.shopme.common.exception.ReviewNotFoundException;
import com.shopme.order.OrderDetailRepository;
import com.shopme.product.ProductRepository;

@Service
@Transactional
public class ReviewService {
	public static final int REVIEWS_PER_PAGE = 5;
	
	@Autowired private ReviewRepository reviewRepo;
	@Autowired private OrderDetailRepository orderDetailRepo;
	@Autowired private ProductRepository productRepo;

	// 해당 회원이 작성한 리뷰 목록 페이징
	public Page<Review> listByCustomerByPage(Customer customer, String keyword, int pageNum, 
			String sortField, String sortDir) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum - 1, REVIEWS_PER_PAGE, sort);
		
		if (keyword != null) {
			return reviewRepo.findByCustomer(customer.getId(), keyword, pageable);
		}
		
		return reviewRepo.findByCustomer(customer.getId(), pageable);
	}
	
	// 회원ID와 리뷰ID로 리뷰 리턴
	public Review getByCustomerAndId(Customer customer, Integer reviewId) throws ReviewNotFoundException {
		Review review = reviewRepo.findByCustomerAndId(customer.getId(), reviewId);
		if (review == null) 
			throw new ReviewNotFoundException("회원이 해당 리뷰ID를 갖고 있지 않습니다. " + reviewId);
		
		return review;
	}
	
	// 가장 추천을 많이 받은 3개의 리뷰 리스트 리턴
	public Page<Review> list3MostVotedReviewsByProduct(Product product) {
		Sort sort = Sort.by("votes").descending();
		Pageable pageable = PageRequest.of(0, 3, sort);
		
		return reviewRepo.findByProduct(product, pageable);		
	}
	
	// 상품 객체로 리뷰 리턴
	public Page<Review> listByProduct(Product product, int pageNum, String sortField, String sortDir) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending(); 
		Pageable pageable = PageRequest.of(pageNum - 1, REVIEWS_PER_PAGE, sort);
		
		return reviewRepo.findByProduct(product, pageable);
	}

	// 회원ID와 해당 리뷰의 상품ID로 리뷰 개수가 0 이상이면 이미 리뷰를 작성한 상태
	public boolean didCustomerReviewProduct(Customer customer, Integer productId) {
		Long count = reviewRepo.countByCustomerAndProduct(customer.getId(), productId);
		return count > 0;
	}
	
	// 해당 상품ID와 회원ID의 주문 추적 상태가 'DELIVERED' 상태면 리뷰 작성 가능
	public boolean canCustomerReviewProduct(Customer customer, Integer productId) {
		Long count = orderDetailRepo.countByProductAndCustomerAndOrderStatus(productId, customer.getId(), OrderStatus.DELIVERED);
		return count > 0;
	}
	
	// 리뷰 저장
	// 상품 평균 평점과 리뷰 개수 업데이트
	public Review save(Review review) {
		review.setReviewTime(new Date());
		Review savedReview = reviewRepo.save(review);
		
		Integer productId = savedReview.getProduct().getId();		
		
		productRepo.updateReviewCountAndAverageRating(productId);
		
		return savedReview;
	}
}
