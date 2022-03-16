package com.shopme.admin.question;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.paging.PagingAndSortingParam;
import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.common.entity.Question;
import com.shopme.common.exception.QuestionNotFoundException;

@Controller
public class QuestionController {
	private String defaultRedirectURL = "redirect:/questions/page/1?sortField=askTime&sortDir=desc";

	@Autowired private QuestionService service;

	// 질문 목록 GET
	@GetMapping("/questions")
	public String listFirstPage(Model model) {
		return defaultRedirectURL;
	}

	// 질문 목록 페이징 GET
	@GetMapping("/questions/page/{pageNum}")
	public String listByPage(
			@PagingAndSortingParam(listName = "listQuestions", moduleURL = "/questions") PagingAndSortingHelper helper,
						@PathVariable(name = "pageNum") int pageNum) {

		service.listByPage(pageNum, helper);

		return "question/questions";		
	}

	// 질문 상세 GET
	@GetMapping("/questions/detail/{id}")
	public String viewQuestion(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
		try {
			Question question = service.get(id);
			model.addAttribute("question", question);

			return "question/question_detail_modal";
		} catch (QuestionNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			return defaultRedirectURL;	
		}
	}

	// 질문 수정 폼 GET
	@GetMapping("/questions/edit/{id}")
	public String editQuestion(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
		try {
			Question question = service.get(id);
			model.addAttribute("question", question);
			model.addAttribute("pageTitle", "질문 수정 (ID: " + id + ")");

			return "question/question_form";
		} catch (QuestionNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			return defaultRedirectURL;
		}
	}

	// 질문 저장 POST
	@PostMapping("/questions/save")
	public String saveQuestion(Question question, RedirectAttributes ra,
			@AuthenticationPrincipal ShopmeUserDetails userDetails) {
		try {
			service.save(question, userDetails.getUser());
			ra.addFlashAttribute("message", "질문 ID " + question.getId() + " 가 성공적으로 저장되었습니다.");
		} catch (QuestionNotFoundException ex) {
			ra.addFlashAttribute("message", "질문 ID " + question.getId() + "을 찾을 수 없습니다.");
		}
		return defaultRedirectURL;
	}

	// 질문 승인 GET (승인된 질문만 회원들에게 보여줄 예정) 
	@GetMapping("/questions/{id}/approve")
	public String approveQuestion(@PathVariable("id") Integer id, RedirectAttributes ra) {
		service.approve(id);
		ra.addFlashAttribute("message", "질문 ID " + id + " 가 승인되었습니다");
		return defaultRedirectURL;
	}

	// 질문 미승인 GET
	@GetMapping("/questions/{id}/disapprove")
	public String disapproveQuestion(@PathVariable("id") Integer id, RedirectAttributes ra) {
		service.disapprove(id);
		ra.addFlashAttribute("message", "질문 ID " + id + " 가 미승인되었습니다.");
		return defaultRedirectURL;
	}

	// 질문삭제 GET
	@GetMapping("/questions/delete/{id}")
	public String deleteQuestion(@PathVariable(name = "id") Integer id, RedirectAttributes ra) throws IOException {

		try {
			service.delete(id);

			ra.addFlashAttribute("message", String.format("질문 ID %d 이 삭제되었습니다.", id));
		} catch (QuestionNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
		}

		return defaultRedirectURL;
	}
}