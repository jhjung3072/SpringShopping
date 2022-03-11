package com.shopme.review;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.shopme.common.entity.Review;
import com.shopme.common.entity.product.Product;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class ReviewRepositoryTests {
	
	@Autowired private ReviewRepository repo;
	
	// 해당 회원이 작성한 리뷰 목록
	@Test
	public void testFindByCustomerNoKeyword() {
		Integer customerId = 5;
		Pageable pageable = PageRequest.of(1, 5);
		
		Page<Review> page = repo.findByCustomer(customerId, pageable);
		long totalElements = page.getTotalElements();
		
		assertThat(totalElements).isGreaterThan(1);		
	}
	
	// 해당 회원이 작성한 리뷰 목록 키워드 검색
	@Test
	public void testFindByCustomerWithKeyword() {
		Integer customerId = 5;
		String keyword = "computer";
		Pageable pageable = PageRequest.of(1, 5);
		
		Page<Review> page = repo.findByCustomer(customerId, keyword, pageable);
		long totalElements = page.getTotalElements();
		
		assertThat(totalElements).isGreaterThan(0);		
	}
	
	// 리뷰 리턴 by 리뷰ID, 회원ID
	@Test
	public void testFindByCustomerAndId() {
		Integer customerId = 5;
		Integer reviewId = 4;
		
		Review review = repo.findByCustomerAndId(customerId, reviewId);
		assertThat(review).isNotNull();
	}
	
	// 해당 상품이 갖고있는 리뷰 목록 리턴
	@Test
	public void testFindByProduct() {
		Product product = new Product(23);
		Pageable pageable = PageRequest.of(0, 3);
		Page<Review> page = repo.findByProduct(product, pageable);
		
		assertThat(page.getTotalElements()).isGreaterThan(1);
		
		List<Review> content = page.getContent();
		content.forEach(System.out::println);
	}
	
	// 리뷰 개수 리턴 by 회원ID와 상품ID
	@Test
	public void testCountByCustomerAndProduct() {
		Integer customerId=5;
		Integer productId=1;
		Long count=repo.countByCustomerAndProduct(customerId, productId);
		
		assertThat(count).isEqualTo(1);
	}
	
	// 리뷰 추천 수 업데이트 by 리뷰ID
	@Test
	public void testUpadteCount() {
		Integer reviewId=4;
		repo.updateVoteCount(reviewId);
		Review review=repo.findById(reviewId).get();
		
		assertThat(review.getVotes()).isEqualTo(2);
	}
	
	// 리뷰 추천 수 get By ID
	@Test
	public void testGetVoteCount() {
		Integer reviewId=4;
		Integer voteCount=repo.getVoteCount(reviewId);
		
		assertThat(voteCount).isEqualTo(0);
	}
}
