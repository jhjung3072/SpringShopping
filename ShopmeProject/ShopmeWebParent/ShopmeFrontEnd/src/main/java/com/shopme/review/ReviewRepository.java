package com.shopme.review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.shopme.common.entity.Review;
import com.shopme.common.entity.product.Product;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
	
	// 회원 ID로 리뷰 리턴
	@Query("SELECT r FROM Review r WHERE r.customer.id = ?1")
	public Page<Review> findByCustomer(Integer customerId, Pageable pageable);
	
	// 리뷰 키워드 검색(리뷰 제목, 내용, 상품 이름)
	@Query("SELECT r FROM Review r WHERE r.customer.id = ?1 AND ("
			+ "r.headline LIKE %?2% OR r.comment LIKE %?2% OR "
			+ "r.product.name LIKE %?2%)")
	public Page<Review> findByCustomer(Integer customerId, String keyword, Pageable pageable);
	
	// 회원ID와 리뷰ID로 리뷰 리턴
	@Query("SELECT r FROM Review r WHERE r.customer.id = ?1 AND r.id = ?2")
	public Review findByCustomerAndId(Integer customerId, Integer reviewId);
	
	// 상품 객체로 리뷰 리턴
	public Page<Review> findByProduct(Product product, Pageable pageable);
	
	// 회원ID와 상품ID로 리뷰 개수 리턴
	@Query("SELECT COUNT(r.id) FROM Review r WHERE r.customer.id = ?1 AND "
			+ "r.product.id = ?2")
	public Long countByCustomerAndProduct(Integer customerId, Integer productId);
	
	// 리뷰 추천 수 업데이트 by 리뷰ID
	@Query("UPDATE Review r SET r.votes = COALESCE((SELECT SUM(v.votes) FROM ReviewVote v"
			+ " WHERE v.review.id=?1), 0) WHERE r.id = ?1")
	@Modifying
	public void updateVoteCount(Integer reviewId);
	
	// 리뷰 추천 수 리턴 by 리뷰ID
	@Query("SELECT r.votes FROM Review r WHERE r.id = ?1")
	public Integer getVoteCount(Integer reviewId);
}
