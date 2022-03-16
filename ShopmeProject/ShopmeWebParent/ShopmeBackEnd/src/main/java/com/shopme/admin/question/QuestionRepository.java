package com.shopme.admin.question;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.shopme.admin.paging.SearchRepository;
import com.shopme.common.entity.Question;

public interface QuestionRepository extends SearchRepository<Question, Integer>{

	// 질문 페이지에서 키워드 검색(질문 내용, 답변, 상품 이름, 질문자 이름)
	@Query("SELECT q FROM Question q WHERE q.questionContent LIKE %?1% OR "
			+ "q.answer LIKE %?1% OR q.product.name LIKE %?1% OR "
			+ "CONCAT(q.asker.firstName, ' ', q.asker.lastName) LIKE %?1%")
	public Page<Question> findAll(String keyword, Pageable pageable);

	// 질문 승인/미승인 상태 업데이트
	@Query("UPDATE Question p SET p.approved = ?2 WHERE p.id = ?1")
	@Modifying
	public void updateApprovalStatus(Integer id, boolean enabled);

}