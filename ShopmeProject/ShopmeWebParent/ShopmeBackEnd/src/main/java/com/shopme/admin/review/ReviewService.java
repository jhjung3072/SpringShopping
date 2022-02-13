package com.shopme.admin.review;

import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.product.ProductRepository;
import com.shopme.common.entity.Review;
import com.shopme.common.exception.ReviewNotFoundException;

@Service
@Transactional
public class ReviewService {
	public static final int REVIEWS_PER_PAGE = 5;
	
	@Autowired private ReviewRepository reviewRepo;
	@Autowired private ProductRepository productRepo;
	
	// 리뷰 목록 페이징
	public void listByPage(int pageNum, PagingAndSortingHelper helper) {
		helper.listEntities(pageNum, REVIEWS_PER_PAGE, reviewRepo);
	}
	
	// 리뷰 GET by ID
	public Review get(Integer id) throws ReviewNotFoundException {
		try {
			return reviewRepo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new ReviewNotFoundException("해당 리뷰ID를 찾을 수 없습니다.");
		}
	}
	
	// 리뷰 저장 및 상품 리뷰 갯수와 평점 갱신
	public void save(Review reviewInForm) {
		Review reviewInDB = reviewRepo.findById(reviewInForm.getId()).get();
		reviewInDB.setHeadline(reviewInForm.getHeadline());
		reviewInDB.setComment(reviewInForm.getComment());
		
		reviewRepo.save(reviewInDB);
		productRepo.updateReviewCountAndAverageRating(reviewInDB.getProduct().getId());
	}
	
	// 리뷰 삭제
	public void delete(Integer id) throws ReviewNotFoundException {
		if (!reviewRepo.existsById(id)) {
			throw new ReviewNotFoundException("해당 리뷰ID를 찾을 수 없습니다.");
		}
		
		reviewRepo.deleteById(id);
	}
}
