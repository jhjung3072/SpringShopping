package com.shopme.customer;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.Utility;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.security.CustomerUserDetails;
import com.shopme.security.oauth.CustomerOAuth2User;
import com.shopme.setting.EmailSettingBag;
import com.shopme.setting.SettingService;

@Controller
public class CustomerController {
	@Autowired private CustomerService customerService;
	@Autowired private SettingService settingService;
	
	// 회원 가입 폼 GET
	@GetMapping("/register")
	public String showRegisterForm(Model model) {
		// 국가 리스트 불러오기
		List<Country> listCountries = customerService.listAllCountries();
		
		model.addAttribute("listCountries", listCountries);
		model.addAttribute("pageTitle", "회원가입");
		model.addAttribute("customer", new Customer());
		
		return "register/register_form";
	}
	
	// 회원 가입 POST
	@PostMapping("/create_customer")
	public String createCustomer(Customer customer, Model model,
			HttpServletRequest request) throws UnsupportedEncodingException, MessagingException {
		// 회원 가입
		customerService.registerCustomer(customer);
		// 인증 이메일 발송
		sendVerificationEmail(request, customer);
		
		model.addAttribute("pageTitle", "회원가입이 완료되었습니다.");
		
		return "/register/register_success";
	}

	// 회원 가입 인증 이메일 발송
	private void sendVerificationEmail(HttpServletRequest request, Customer customer) 
			throws UnsupportedEncodingException, MessagingException {
		EmailSettingBag emailSettings = settingService.getEmailSettings();
		JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);
		
		// 이메일 수신자, 제목, 내용 설정
		String toAddress = customer.getEmail();
		String subject = emailSettings.getCustomerVerifySubject();
		String content = emailSettings.getCustomerVerifyContent();
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		
		// 발신자, 수신자, 제목
		helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
		helper.setTo(toAddress);
		helper.setSubject(subject);
		
		// name 을 회원이름으로 대입
		content = content.replace("[[name]]", customer.getFullName());
		// 인증 링크 생성
		String verifyURL = Utility.getSiteURL(request) + "/verify?code=" + customer.getVerificationCode();
		//URL을 인증 링크로 대입
		content = content.replace("[[URL]]", verifyURL);
		// 이메일 본문 생성
		helper.setText(content, true);
		// 발송
		mailSender.send(message);
		
		System.out.println("수신자: " + toAddress);
		System.out.println("인증 링크: " + verifyURL);
	}	
	
	// 회원 인증 여부 페이지 GET
	@GetMapping("/verify")
	public String verifyAccount(String code, Model model) {
		boolean verified = customerService.verify(code);
		
		return "register/" + (verified ? "verify_success" : "verify_fail");
	}
	
	// 회원 계정 상세 페이지 GET
	@GetMapping("/account_details")
	public String viewAccountDetails(Model model, HttpServletRequest request) {
		//승인된 회원 이메일 불러오기
		String email = Utility.getEmailOfAuthenticatedCustomer(request);
		Customer customer = customerService.getCustomerByEmail(email);
		List<Country> listCountries = customerService.listAllCountries();
		
		model.addAttribute("customer", customer);
		model.addAttribute("listCountries", listCountries);
		
		return "customer/account_form";
	}
	
	// 회원 계정 정보 수정 POST
	@PostMapping("/update_account_details")
	public String updateAccountDetails(Model model, Customer customer, RedirectAttributes ra,
			HttpServletRequest request) {
		customerService.update(customer);
		ra.addFlashAttribute("message", "계정정보가 수정되었습니다.");
		
		// 인증된 회원 이름 수정
		updateNameForAuthenticatedCustomer(customer, request);
		
		String redirectOption = request.getParameter("redirect");
		String redirectURL = "redirect:/account_details";
		
		// 수정이 완료되었으면 리다이렉트된 곳으로 페이지 이동
		if ("address_book".equals(redirectOption)) {
			redirectURL = "redirect:/address_book";
		} else if ("cart".equals(redirectOption)) {
			redirectURL = "redirect:/cart";
		} else if ("checkout".equals(redirectOption)) {
			redirectURL = "redirect:/address_book?redirect=checkout";
		}
		
		return redirectURL;
	}

	// 인증된 회원 이름 수정, 가입 유형마다 방법 다름
	private void updateNameForAuthenticatedCustomer(Customer customer, HttpServletRequest request) {
		Object principal = request.getUserPrincipal();
		
		// 폼 로그인 회원이거나 쿠기로그인(로그인 기억) 회원일 경우
		if (principal instanceof UsernamePasswordAuthenticationToken 
				|| principal instanceof RememberMeAuthenticationToken) {
			CustomerUserDetails userDetails = getCustomerUserDetailsObject(principal);
			Customer authenticatedCustomer = userDetails.getCustomer();
			authenticatedCustomer.setFirstName(customer.getFirstName());
			authenticatedCustomer.setLastName(customer.getLastName());
			
		} else if (principal instanceof OAuth2AuthenticationToken) { // 소셜 로그인일 경우 회원 이메일 리턴
			OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) principal;
			CustomerOAuth2User oauth2User = (CustomerOAuth2User) oauth2Token.getPrincipal();
			String fullName = customer.getFirstName() + " " + customer.getLastName();
			oauth2User.setFullName(fullName);
		}		
	}
	
	// 폼 로그인 회원이거나 쿠기로그인(로그인 기억) 회원의 UserDetails 가져오기
	private CustomerUserDetails getCustomerUserDetailsObject(Object principal) {
		CustomerUserDetails userDetails = null;
		if (principal instanceof UsernamePasswordAuthenticationToken) {
			UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
			userDetails = (CustomerUserDetails) token.getPrincipal();
		} else if (principal instanceof RememberMeAuthenticationToken) {
			RememberMeAuthenticationToken token = (RememberMeAuthenticationToken) principal;
			userDetails = (CustomerUserDetails) token.getPrincipal();
		}
		
		return userDetails;
	}
}
