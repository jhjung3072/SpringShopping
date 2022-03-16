package com.shopme.question.vote;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Question;
import com.shopme.common.entity.QuestionVote;
import com.shopme.question.QuestionRepository;
import com.shopme.vote.VoteResult;
import com.shopme.vote.VoteType;

@Service
@Transactional
public class QuestionVoteService {

	@Autowired
	private QuestionVoteRepository voteRepo;
	
	@Autowired
	private QuestionRepository questionRepo;
	
	// 질문 추천하기
	public VoteResult doVote(Integer questionId, Customer customer, VoteType voteType) {
		Question question = null;
		
		try {
			question = questionRepo.findById(questionId).get();
		} catch (NoSuchElementException ex) {
			return VoteResult.fail("질문 ID: " + questionId + " 가 존재하지 않습니다.");
		}
		
		QuestionVote vote = voteRepo.findByQuestionAndCustomer(questionId, customer.getId());
		
		if (vote != null) { // 해당 회원이 이미 추천 or 비추천을 주른 경우
			if (vote.isUpvoted() && voteType.equals(VoteType.UP) ||  // 회원이 이미 추천을 눌렀고, 다시 추천을 누른다면 -> 추천 취소, 반대의 경우 비추천 취소
					vote.isDownvoted() && voteType.equals(VoteType.DOWN)) {
				return undoVote(vote, questionId, voteType);
			} else if (vote.isUpvoted() && voteType.equals(VoteType.DOWN)) { //  해당 회원이 이미 추천을 눌렀고, 지금 비추천을 누른다면 -> 비추천
				vote.voteDown();
			} else if (vote.isDownvoted() && voteType.equals(VoteType.UP)) { // 해당 회원이 이미 비추천을 눌렀고, 지금 추천을 누른다면 -> 추천
				vote.voteUp();
			}			
		} else { // 해당 회원이 추천 or 비추천을 하지 않은 경우
			vote = new QuestionVote();
			vote.setQuestion(question);
			vote.setCustomer(customer);
			
			if (voteType.equals(VoteType.UP)) { // 추천을 누르면
				vote.voteUp(); // 추천
			} else { // 비추천을 누르면
				vote.voteDown(); // 비추천
			}			
		}
		
		voteRepo.save(vote);
		questionRepo.updateVoteCount(questionId);
		Integer voteCount = questionRepo.getVoteCount(questionId);
		
		return VoteResult.success(voteType + "를 눌렀습니다." , voteCount);
	}
	
	// 해당 질문 추천 취소
	public VoteResult undoVote(QuestionVote vote, Integer questionId, VoteType voteType) {
		voteRepo.delete(vote);
		questionRepo.updateVoteCount(questionId);
		Integer voteCount = questionRepo.getVoteCount(questionId);
		
		return VoteResult.success(voteType + "를 취소했습니다.", voteCount);
	}
	
	// 해당 회원이 상품을 추천했는지 비추천했는지 색깔 구분
	public void markQuestionsVotedForProductByCustomer(List<Question> listQuestions, Integer productId, Integer customerId) {
		List<QuestionVote> listVotes = voteRepo.findByProductAndCustomer(productId, customerId);
		
		// 해당 회원이 질문에 대한 추천/비추천한 목록 리턴
		for (QuestionVote aVote : listVotes) {
			Question votedQuestion = aVote.getQuestion(); // 추천/비추천된 질문을 가져오고
			if (listQuestions.contains(votedQuestion)) { // 질문 리스트에 추천/비추천된 리뷰가 있다면
				int index = listQuestions.indexOf(votedQuestion); // 질문 리스트에 해당 질문 인덱스 가져와서
				Question question = listQuestions.get(index); //질문 객체 리턴
				
				if (aVote.isUpvoted()) { // 회원이 그 질문을 추천했으면
					question.setUpvotedByCurrentCustomer(true); // 추천된 상태 true 
				} else if (aVote.isDownvoted()) { // 회원이 그 리뷰를 비추천 했으면
					question.setDownvotedByCurrentCustomer(true); // 비추천된 상태 true
				}
			}
		}
	}
	
}