package com.shopme.review.vote;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Review;
import com.shopme.common.entity.ReviewVote;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ReviewVoteRepositoryTests {
	
	@Autowired private ReviewVoteRepository repo;
	
	// 해당 리뷰 추천 by 리뷰ID와 회원ID
	@Test
	public void testSaveVote() {
		Integer customerId = 1;
		Integer reviewId = 4;
		
		ReviewVote vote = new ReviewVote();
		vote.setCustomer(new Customer(customerId));
		vote.setReview(new Review(reviewId));
		vote.voteUp();
		
		ReviewVote savedVote = repo.save(vote);
		assertThat(savedVote.getId()).isGreaterThan(0);		
	}
	
	// 추천 객체 리턴 by 리뷰ID 와 회원ID
	@Test
	public void testFindByReviewAndCustomer() {
		Integer customerId = 1;
		Integer reviewId = 4;	
		
		ReviewVote vote = repo.findByReviewAndCustomer(reviewId, customerId);
		assertThat(vote).isNotNull();
		
		System.out.println(vote);
	}
	
	// 추천 리스트 리턴 by 상퓸ID 와 회원ID, 해당 상품에 추천/비추천을 여러개 한 경우
	@Test
	public void testFindByProductAndCustomer() {
		Integer customerId = 1;
		Integer productId = 1;
		
		List<ReviewVote> listVotes = repo.findByProductAndCustomer(productId, customerId);
		assertThat(listVotes.size()).isGreaterThan(0);
		
		listVotes.forEach(System.out::println);
	}
}
