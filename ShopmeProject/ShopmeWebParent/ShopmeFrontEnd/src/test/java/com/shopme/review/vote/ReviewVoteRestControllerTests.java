package com.shopme.review.vote;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopme.common.entity.Review;
import com.shopme.review.ReviewRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class ReviewVoteRestControllerTests {

	@Autowired private ReviewRepository reviewRepo;
	@Autowired private MockMvc mockMvc;
	@Autowired private ObjectMapper objectMapper;
	
   
	// 로그인을 하지 않은 사용자가 추천을 누르는 경우
	@Test
	public void testVoteNotLogin() throws Exception {
		String requestURL = "/vote_review/1/up";
		
		MvcResult mvcResult = mockMvc.perform(post(requestURL).with(csrf()))
			.andExpect(status().isOk())
			.andDo(print())
			.andReturn();
		
		String json = mvcResult.getResponse().getContentAsString();
		VoteResult voteResult = objectMapper.readValue(json, VoteResult.class);
		
		assertFalse(voteResult.isSuccessful());
		assertThat(voteResult.getMessage()).contains("로그인이 필요합니다.");
	}
	
	@Test
	@WithMockUser(username = "jaehooo@gmail.com", password = "q2648519")
	public void testVoteNonExistReview() throws Exception {
		String requestURL = "/vote_review/1233/up";
		
		MvcResult mvcResult = mockMvc.perform(post(requestURL).with(csrf()))
			.andExpect(status().isOk())
			.andDo(print())
			.andReturn();
		
		String json = mvcResult.getResponse().getContentAsString();
		VoteResult voteResult = objectMapper.readValue(json, VoteResult.class);
		
		assertFalse(voteResult.isSuccessful());
		assertThat(voteResult.getMessage()).contains("존재하지");
	}	
	
	@Test
	@WithMockUser(username = "jaehooo@gmail.com", password = "q2648519")
	public void testVoteUp() throws Exception {
		Integer reviewId = 21;
		String requestURL = "/vote_review/" + reviewId + "/up";
		
		Review review = reviewRepo.findById(reviewId).get();
		int voteCountBefore = review.getVotes();
		
		MvcResult mvcResult = mockMvc.perform(post(requestURL).with(csrf()))
			.andExpect(status().isOk())
			.andDo(print())
			.andReturn();
		
		String json = mvcResult.getResponse().getContentAsString();
		VoteResult voteResult = objectMapper.readValue(json, VoteResult.class);
		
		assertTrue(voteResult.isSuccessful());
		assertThat(voteResult.getMessage()).contains("추천했습니다.");
		
		int voteCountAfter = voteResult.getVoteCount();
		assertEquals(voteCountBefore + 1, voteCountAfter);
		
	}
	
	@Test
	@WithMockUser(username = "jaehooo@gmail.com", password = "q2648519")
	public void testUndoVoteUp() throws Exception {
		Integer reviewId = 20;
		String requestURL = "/vote_review/" + reviewId + "/up";
		
		Review review = reviewRepo.findById(reviewId).get();
		int voteCountBefore = review.getVotes();
		
		MvcResult mvcResult = mockMvc.perform(post(requestURL).with(csrf()))
			.andExpect(status().isOk())
			.andDo(print())
			.andReturn();
		
		String json = mvcResult.getResponse().getContentAsString();
		VoteResult voteResult = objectMapper.readValue(json, VoteResult.class);
		
		assertTrue(voteResult.isSuccessful());
		assertThat(voteResult.getMessage()).contains("추천을 취소했습니다.");
		
		int voteCountAfter = voteResult.getVoteCount();
		assertEquals(voteCountBefore - 1, voteCountAfter);
		
	}	
}
