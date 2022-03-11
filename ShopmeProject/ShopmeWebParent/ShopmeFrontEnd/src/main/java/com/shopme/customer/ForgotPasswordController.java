package com.shopme.customer;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.shopme.Utility;
import com.shopme.common.entity.Customer;
import com.shopme.common.exception.CustomerNotFoundException;
import com.shopme.setting.EmailSettingBag;
import com.shopme.setting.SettingService;

@Controller
public class ForgotPasswordController {
	@Autowired private CustomerService customerService;
	@Autowired private SettingService settingService;
	
	// 비밀번호 분실 폼 GET
	@GetMapping("/forgot_password")
	public String showRequestForm() {
		return "customer/forgot_password_form";
	}
	
	// 패스워드 초기화 요청 POST
	@PostMapping("/forgot_password")
	public String processRequestForm(HttpServletRequest request, Model model) {
		String email = request.getParameter("email");
		try {
			// 패스워드 리셋 토큰 및 링크 생성 후 발송
			String token = customerService.updateResetPasswordToken(email);
			String link = Utility.getSiteURL(request) + "/reset_password?token=" + token;
			sendEmail(link, email);
			
			model.addAttribute("message", "회원님의 이메일로 패스워드 재설정 링크를 보냈습니다."
					+ " 확인해주세요.");
		} catch (CustomerNotFoundException e) {
			model.addAttribute("error", e.getMessage());
		} catch (UnsupportedEncodingException | MessagingException e) {
			model.addAttribute("error", "이메일을 발송할 수 없습니다.");
		}
		
		return "customer/forgot_password_form";
	}
	
	// 패스워드 초기화 이메일 설정
	private void sendEmail(String link, String email) 
			throws UnsupportedEncodingException, MessagingException {
		EmailSettingBag emailSettings = settingService.getEmailSettings();
		JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);
		// 수신 및 제목 설정
		String toAddress = email;
		String subject = "패스워드 재설정 링크";
		// 이메일 내용 설정
		String content = "<p>안녕하세요,</p>"
				+ "<p>회원님의 패스워드 초기화 링크입니다</p>"
				+ "아래의 링크를 눌러 회원님의 패스워드를 변경하세요:</p>"
				+ "<p><a href=\"" + link + "\">패스워드 변경</a></p>"
				+ "<br>"
				+ "<p>만약 기존의 패스워드가 기억나신다면 변경하지 않으셔도 됩니다.<p>";
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		
		helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
		helper.setTo(toAddress);
		helper.setSubject(subject);		
		
		helper.setText(content, true);
		mailSender.send(message);
	}
	
	// 패스워드 수정 폼 GET
	@GetMapping("/reset_password")
	public String showResetForm(String token, Model model) {
		Customer customer = customerService.getByResetPasswordToken(token);
		if (customer != null) {
			model.addAttribute("token", token);
		} else {
			model.addAttribute("pageTitle", "Invalid Token");
			model.addAttribute("message", "Invalid Token");
			return "message";
		}
		
		return "customer/reset_password_form";
	}
	
	// 패스워드 수정 POST
	@PostMapping("/reset_password")
	public String processResetForm(HttpServletRequest request, Model model) {
		String token = request.getParameter("token");
		String password = request.getParameter("password");
		
		try {
			customerService.updatePassword(token, password);
			
			model.addAttribute("pageTitle", "패스워드 재설정");
			model.addAttribute("title", "패스워드 재설정");
			model.addAttribute("message", "패스워드를 재설정했습니다.");
			
		} catch (CustomerNotFoundException e) {
			model.addAttribute("pageTitle", "패스워드 토큰 에러");
			model.addAttribute("message", e.getMessage());
		}	

		return "message";		
	}
}
