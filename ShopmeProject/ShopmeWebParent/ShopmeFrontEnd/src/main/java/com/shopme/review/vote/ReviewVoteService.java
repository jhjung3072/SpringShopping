package com.shopme.review.vote;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Review;
import com.shopme.common.entity.ReviewVote;
import com.shopme.review.ReviewRepository;

@Service
@Transactional
public class ReviewVoteService {
	
	@Autowired private ReviewRepository reviewRepo;
	@Autowired private ReviewVoteRepository voteRepo;
	
	// 해당 리뷰 추천 취소
	public VoteResult undoVote(ReviewVote vote, Integer reviewId, VoteType voteType) {
		voteRepo.delete(vote);
		reviewRepo.updateVoteCount(reviewId);
		Integer voteCount = reviewRepo.getVoteCount(reviewId);
		
		return VoteResult.success(voteType + "을 취소했습니다.", voteCount);
	}
	
	// 해당 리뷰 추천하기
	public VoteResult doVote(Integer reviewId, Customer customer, VoteType voteType) {
		Review review = null;
		
		try {
			review = reviewRepo.findById(reviewId).get();
		} catch (NoSuchElementException ex) {
			return VoteResult.fail("해당 리뷰ID: " + reviewId + "가 존재하지 않습니다.");
		}
		
		ReviewVote vote = voteRepo.findByReviewAndCustomer(reviewId, customer.getId());
		
		if (vote != null) { // 해당 회원이 이미 추천 or 비추천을 누른 경우
			if (vote.isUpvoted() && voteType.equals(VoteType.UP) || // 해당 회원이 이미 추천을 눌렀고, 다시 추천을 누른다면 -> 추천 취소, 반대의 경우 비추천 취소
					vote.isDownvoted() && voteType.equals(VoteType.DOWN)) {
				return undoVote(vote, reviewId, voteType);
			} else if (vote.isUpvoted() && voteType.equals(VoteType.DOWN)) { //  해당 회원이 이미 추천을 눌렀고, 지금 비추천을 누른다면 -> 비추천
				vote.voteDown();
			} else if (vote.isDownvoted() && voteType.equals(VoteType.UP)) { // 해당 회원이 이미 비추천을 눌렀고, 지금 추천을 누른다면 -> 추천
				vote.voteUp();
			}
		} else { // 해당 회원이 추천 or 비추천을 하지 않은 경우
			vote = new ReviewVote();
			vote.setCustomer(customer);
			vote.setReview(review);
			
			if (voteType.equals(VoteType.UP)) { // 추천을 누르면
				vote.voteUp(); // 추천
			} else { // 비추천을 누르면
				vote.voteDown(); // 비추천
			}
		}
		
		voteRepo.save(vote);
		reviewRepo.updateVoteCount(reviewId);
		Integer voteCount = reviewRepo.getVoteCount(reviewId);
		
		return VoteResult.success(voteType + "했습니다.", voteCount);
	}
	
	// 해당 회원이 상품을 추천했는지 비추천했는지 색깔 구분
	public void markReviewsVotedForProductByCustomer(List<Review>listReviews, Integer productId,
				Integer customerId) {
		// 해당 회원이 상품에 대한 추천/비추천한 목록 리턴
		List<ReviewVote> listVotes=voteRepo.findByProductAndCustomer(productId, customerId);
		
		for (ReviewVote vote : listVotes) {
			Review votedReview=vote.getReview(); // 추천/비추천된 리뷰를 가져오고
			if (listReviews.contains(votedReview)) { // 리뷰 리스트에 추천/비추천된 리뷰가 있다면
				int index=listReviews.indexOf(votedReview);  // 리뷰 리스트에 해당 리뷰 인덱스 가져와서
				Review review = listReviews.get(index); // 리뷰 객체 리턴
				
				if (vote.isUpvoted()) { // 회원이 그 리뷰를 추천했으면
					review.setUpvotedByCurrentCustomer(true); // 추천된 상태 true 
				} else if (vote.isDownvoted()) { // 회원이 그 리뷰를 비추천 했으면
					review.setDownvotedByCurrentCustomer(true); // 비추천된 상태 true
				}
			}
		}
	}
}
