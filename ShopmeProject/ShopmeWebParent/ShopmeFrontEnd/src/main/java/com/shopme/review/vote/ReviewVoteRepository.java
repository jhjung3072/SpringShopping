package com.shopme.review.vote;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.shopme.common.entity.ReviewVote;

public interface ReviewVoteRepository extends JpaRepository<ReviewVote, Integer> {

	// 리뷰ID 와 회원ID로 추천 객체 리턴
	@Query("SELECT v FROM ReviewVote v WHERE v.review.id = ?1 AND v.customer.id = ?2")
	public ReviewVote findByReviewAndCustomer(Integer reviewId, Integer customerId);
	
	// 상퓸ID 와 회원ID로 추천 리스트 리턴, 해당 상품에 추천/비추천을 여러개 한 경우
	@Query("SELECT v FROM ReviewVote v WHERE v.review.product.id = ?1 AND v.customer.id = ?2")
	public List<ReviewVote> findByProductAndCustomer(Integer productId, Integer customerId);	
}
