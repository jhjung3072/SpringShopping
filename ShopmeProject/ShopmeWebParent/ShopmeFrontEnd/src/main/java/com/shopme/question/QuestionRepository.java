package com.shopme.question;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.shopme.common.entity.Question;

public interface QuestionRepository extends JpaRepository<Question, Integer> {

	// 상품 페이지에서 승인된 질문 불러오기
	@Query("SELECT q FROM Question q WHERE q.approved = true AND q.product.id = ?1")
	Page<Question> findAll(Integer productId, Pageable pageable);

	// 상품 줄일말로 승인된 질문 불러오기
	@Query("SELECT q FROM Question q WHERE q.approved = true AND q.product.alias = ?1")
	Page<Question> findByAlias(String alias, Pageable pageable);

	// 승인된 질문 중 답변된 질문 개수
	@Query("SELECT COUNT (q) FROM Question q WHERE q.approved = true AND q.answer IS NOT NULL and q.product.id =?1")
	int countAnsweredQuestions(Integer productId);

	// 승인된 질문 개수
	@Query("SELECT COUNT (q) FROM Question q WHERE q.approved = true and q.product.id =?1")
	int countApprovedQuestions(Integer productId);

	// 질문 추천수 업데이트
	@Query("UPDATE Question q SET q.votes = COALESCE((SELECT SUM(v.votes) FROM QuestionVote v"
			+ " WHERE v.question.id=?1), 0) WHERE q.id = ?1")
	@Modifying
	public void updateVoteCount(Integer questionId);

	// 질문 추천수 리턴
	@Query("SELECT q.votes FROM Question q WHERE q.id = ?1")
	public Integer getVoteCount(Integer questionId);	

	// 해당 회원의 질문 목록 리턴
	@Query("SELECT q FROM Question q WHERE q.asker.id = ?1")
	Page<Question> findByCustomer(Integer customerId, Pageable pageable);

	// 해당 회원의 질문 목록 키워드 검색
	@Query("SELECT q FROM Question q WHERE q.asker.id = ?1 AND ("
			+ "q.questionContent LIKE %?2% OR "
			+ "q.answer LIKE %?2% OR q.product.name LIKE %?2%)")
	Page<Question> findByCustomer(Integer customerId, String keyword, Pageable pageable);

	// 회원ID와 질문ID로 질문 객체 리턴
	@Query("SELECT q FROM Question q WHERE q.asker.id = ?1 AND q.id = ?2")
	Question findByCustomerAndId(Integer customerId, Integer questionId);

}