package com.shopme.review.vote;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.ControllerHelper;
import com.shopme.common.entity.Customer;

@RestController
public class VoteReviewRestController {

	@Autowired private ReviewVoteService service;
	@Autowired private ControllerHelper helper;
	
	// 리뷰 추천/비추천 POST
	@PostMapping("/vote_review/{id}/{type}")
	public VoteResult voteReview(@PathVariable(name = "id") Integer reviewId,
			@PathVariable(name = "type") String type,
			HttpServletRequest request) {
		
		// 승인된 회원 객체 가져오기
		Customer customer = helper.getAuthenticatedCustomer(request);
		
		// 로그인하지 않았다면, 추천/비추천 불가능
		if (customer == null) {
			return VoteResult.fail("로그인이 필요합니다.");
		}
		
		VoteType voteType = VoteType.valueOf(type.toUpperCase());
		return service.doVote(reviewId, customer, voteType);
	}
}
