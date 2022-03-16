package com.shopme.admin.question;

import java.util.Date;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.common.entity.Question;
import com.shopme.common.entity.User;
import com.shopme.common.exception.QuestionNotFoundException;

@Service
@Transactional
public class QuestionService {

	public static final int QUESTIONS_PER_PAGE = 10;

	@Autowired
	private QuestionRepository repo;

	// 질문 목록 페이징 처리
	public void listByPage(int pageNum, PagingAndSortingHelper helper) {
		helper.listEntities(pageNum, QUESTIONS_PER_PAGE, repo);
	}	

	// 질문 객체 GET by ID
	public Question get(Integer id) throws QuestionNotFoundException {
		try {
			return repo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new QuestionNotFoundException("해당 질문을 찾을 수 없습니다 ID: " + id);
		}
	}

	// 질문 저장
	public void save(Question questionInForm, User user) throws QuestionNotFoundException {
		try {
			Question questionInDB = repo.findById(questionInForm.getId()).get();
			questionInDB.setQuestionContent(questionInForm.getQuestionContent());
			questionInDB.setAnswer(questionInForm.getAnswer());
			questionInDB.setApproved(questionInForm.isApproved());

			if (questionInDB.isAnswered()) { // 질문이 답변되었다면, 답변 시각과 답변단 직원 설정
				questionInDB.setAnswerTime(new Date());
				questionInDB.setAnswerer(user);
			}

			repo.save(questionInDB);

		} catch (NoSuchElementException ex) {
			throw new QuestionNotFoundException("해당 질문을 찾을 수 없습니다 ID: " + questionInForm.getId());
		}		
	}

	// 질문 상태 승인
	public void approve(Integer id) {
		repo.updateApprovalStatus(id, true);
	}

	// 질문 상태 미승인
	public void disapprove(Integer id) {
		repo.updateApprovalStatus(id, false);
	}

	// 질문 삭제
	public void delete(Integer id) throws QuestionNotFoundException {
		if (!repo.existsById(id)) {
			throw new QuestionNotFoundException("해당 질문을 찾을 수 없습니다 ID: " + id);
		}
		repo.deleteById(id);
	}	
}