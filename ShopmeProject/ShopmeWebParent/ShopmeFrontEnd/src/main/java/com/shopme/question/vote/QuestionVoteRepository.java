package com.shopme.question.vote;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.shopme.common.entity.QuestionVote;

@Repository
public interface QuestionVoteRepository extends CrudRepository<QuestionVote, Integer> {

	// 질문ID와 회원ID로 QuestionVote 객체 리턴
	@Query("SELECT qv FROM QuestionVote qv WHERE qv.question.id = ?1 AND qv.customer.id = ?2")
	public QuestionVote findByQuestionAndCustomer(Integer questionId, Integer customerId);

	// 상품ID와 회원ID 로 QuestionVote 리스트 리턴
	@Query("SELECT qv FROM QuestionVote qv WHERE qv.question.product.id = ?1 AND qv.customer.id = ?2")
	public List<QuestionVote> findByProductAndCustomer(Integer productId, Integer customerId);

}