package com.shopme.admin.review;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Review;
import com.shopme.common.entity.product.Product;
import com.sun.el.stream.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ReviewRepositoryTests {
	@Autowired private ReviewRepository repo;
	
	// 리뷰 작성
	@Test
	public void testCreateReview() {
		Integer productId=1;
		Product product=new Product(productId);
		
		Integer customerId=5;
		Customer customer=new Customer(customerId);
		
		Review review=new Review();
		review.setProduct(product);
		review.setCustomer(customer);
		review.setReviewTime(new Date());
		review.setHeadline("너무 좋아요");
		review.setComment("배송도 빠르고 성능도 좋아요");
		review.setRating(5);
		
		Review savedReview=repo.save(review);
		
		assertThat(savedReview).isNotNull();
		assertThat(savedReview.getId()).isGreaterThan(0);
	}
	
	// 리뷰 목록 리스트
	@Test
	public void testListReview() {
		List<Review> listReviews=repo.findAll();
		assertThat(listReviews.size()).isGreaterThan(0);
		listReviews.forEach(System.out::println);
	}
	
	// 리뷰 Get By ID
	@Test
	public void testGetReview() {
		Integer id=1;
		Review review=repo.findById(id).get();
		assertThat(review).isNotNull();
		System.out.println(review);
	}
	
	// 리뷰 수정
	@Test
	public void testUpdateReview() {
		Integer id=1;
		String headline="진짜 안좋은 제품이네요";
		String comment="배송도 늦게오고, 성능도 최악";
		Review review=repo.findById(id).get();
		
		review.setHeadline(headline);
		review.setComment(comment);
		
		Review updatedReview=repo.save(review);
		
		assertThat(updatedReview.getHeadline()).isEqualTo(headline);
		assertThat(updatedReview.getComment()).isEqualTo(comment);
	}
	
	// 리뷰 삭제
	@Test
	public void testDeleteReview() {
		Integer id=1;
		repo.deleteById(id);
		
		java.util.Optional<Review> findById=repo.findById(id);
		
		assertThat(findById).isNotPresent();
	}
	
}
