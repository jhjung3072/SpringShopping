package com.shopme.question;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Question;
import com.shopme.common.entity.product.Product;
import com.shopme.common.exception.ProductNotFoundException;
import com.shopme.product.ProductRepository;

@Service
public class QuestionService {
	public static final int QUESTIONS_PER_PAGE_FOR_PUBLIC_LISTING = 10;
	public static final int QUESTIONS_PER_PAGE_FOR_CUSTOMER = 4;

	@Autowired
	private QuestionRepository questionRepo;

	@Autowired
	private ProductRepository productRepo;

	// 새 질문 저장
	public void saveNewQuestion(Question question, Customer asker, 
			Integer productId) throws ProductNotFoundException {
		Optional<Product> productById = productRepo.findById(productId);
		if (!productById.isPresent()) {
			throw new ProductNotFoundException("해당 상품을 찾을 수 없습니다 ID: " + productId);
		}
		question.setAskTime(new Date());
		question.setProduct(productById.get());
		question.setAsker(asker);

		questionRepo.save(question);
	}

	// 질문 중 추천수가 가장 많은 3개
	public List<Question> getTop3VotedQuestions(Integer productId) {
		Pageable pageable = PageRequest.of(0, 3, Sort.by("votes").descending());
		Page<Question> result = questionRepo.findAll(productId, pageable);
		return result.getContent();
	}

	// 상품의 질문 목록
	public Page<Question> listQuestionsOfProduct(String alias, int pageNum, String sortField, String sortDir) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending(); 
		Pageable pageable = PageRequest.of(pageNum - 1, QUESTIONS_PER_PAGE_FOR_PUBLIC_LISTING, sort);
		return questionRepo.findByAlias(alias, pageable);
	}

	// 답변된 질문 개수
	public int getNumberOfAnsweredQuestions(Integer productId) {
		return questionRepo.countAnsweredQuestions(productId);
	}

	// 질문 개수
	public int getNumberOfQuestions(Integer productId) {
		return questionRepo.countApprovedQuestions(productId);
	}

	// 해당 질문의 추천수
	public int getVotesForQuestion(Integer questionId) {
		Question question = questionRepo.findById(questionId).get();
		return question.getVotes();
	}

	// 해당 회원의 질문 목록
	public Page<Question> listQuestionsByCustomer(Customer customer, String keyword, int pageNum, 
			String sortField, String sortDir) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

		Pageable pageable = PageRequest.of(pageNum - 1, QUESTIONS_PER_PAGE_FOR_CUSTOMER, sort);

		if (keyword != null) {
			return questionRepo.findByCustomer(customer.getId(), keyword, pageable);
		}

		return questionRepo.findByCustomer(customer.getId(), pageable);
	}	

	// 회원ID와 질문ID로 질문 객체 리턴
	public Question getByCustomerAndId(Customer customer, Integer questionId) {
		return questionRepo.findByCustomerAndId(customer.getId(), questionId);
	}
}