package com.shopme;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import com.shopme.security.oauth.CustomerOAuth2User;
import com.shopme.setting.CurrencySettingBag;
import com.shopme.setting.EmailSettingBag;

public class Utility {
	
	// 현재 페이지의 url 중 ServletPath 지우고 반환(이메일 인증 코드와 패스워드 초기화 링크에 사용)
	public static String getSiteURL(HttpServletRequest request) {
		String siteURL = request.getRequestURL().toString();
		return siteURL.replace(request.getServletPath(), "");
	}
	
	// 메일전송을 하기 위한 세팅
	public static JavaMailSenderImpl prepareMailSender(EmailSettingBag settings) {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		
		mailSender.setHost(settings.getHost());
		mailSender.setPort(settings.getPort());
		mailSender.setUsername(settings.getUsername());
		mailSender.setPassword(settings.getPassword());
		
		Properties mailProperties = new Properties();
		mailProperties.setProperty("mail.smtp.auth", settings.getSmtpAuth());
		mailProperties.setProperty("mail.smtp.starttls.enable", settings.getSmtpSecured());
		
		mailSender.setJavaMailProperties(mailProperties);
		
		return mailSender;
	}
	
	// 승인된 회원 이메일 GET, 참고 https://velog.io/@ysb05222/%EB%A1%9C%EA%B7%B8%EC%9D%B8-%ED%95%98%EA%B8%B0
	public static String getEmailOfAuthenticatedCustomer(HttpServletRequest request) {
		Object principal = request.getUserPrincipal();
		if (principal == null) return null;
		
		String customerEmail = null;
		
		// 폼 로그인 회원이거나 쿠기로그인(로그인 기억) 회원일 경우 회원 이메일 리턴
		// 소셜 로그인일 경우 회원 이메일 리턴
		if (principal instanceof UsernamePasswordAuthenticationToken 
				|| principal instanceof RememberMeAuthenticationToken) {
			customerEmail = request.getUserPrincipal().getName();
		} else if (principal instanceof OAuth2AuthenticationToken) {
			OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) principal;
			CustomerOAuth2User oauth2User = (CustomerOAuth2User) oauth2Token.getPrincipal();
			customerEmail = oauth2User.getEmail();
		}
		
		return customerEmail;
	}	
	
	// 화폐 설정에 따른 가격의 점, 콤마 위치 설정
	public static String formatCurrency(float amount, CurrencySettingBag settings) {
		String symbol = settings.getSymbol();
		String symbolPosition = settings.getSymbolPosition();
		String decimalPointType = settings.getDecimalPointType();
		String thousandPointType = settings.getThousandPointType();
		int decimalDigits = settings.getDecimalDigits();
		
		// 가격 앞에 화폐가 표시될 경우 symbol + pattern 
		String pattern = symbolPosition.equals("Before price") ? symbol : "";
		pattern += "###,###";
		
		// 소수점의 오른쪽에 저장될 수 있는 최대 자릿수 설정
		if (decimalDigits > 0) {
			pattern += ".";
			for (int count = 1; count <= decimalDigits; count++) pattern += "#";
		}
		
		// 가격 뒤에 화폐가 표시될 경우 pattern + symbol
		pattern += symbolPosition.equals("After price") ? symbol : "";
		
		// 1000자리와 소수점 자리 구분 표시 설정
		char thousandSeparator = thousandPointType.equals("POINT") ? '.' : ',';
		char decimalSeparator = decimalPointType.equals("POINT") ? '.' : ',';
		
		DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance();
		decimalFormatSymbols.setDecimalSeparator(decimalSeparator);
		decimalFormatSymbols.setGroupingSeparator(thousandSeparator);
		
		DecimalFormat formatter = new DecimalFormat(pattern, decimalFormatSymbols);
		
		return formatter.format(amount);
	}
}
