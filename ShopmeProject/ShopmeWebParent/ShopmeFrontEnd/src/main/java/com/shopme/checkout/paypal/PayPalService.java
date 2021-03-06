package com.shopme.checkout.paypal;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.shopme.setting.PaymentSettingBag;
import com.shopme.setting.SettingService;

// 페이팔 모듈 설정을 위한 서비스 클래스
@Component
public class PayPalService {
	private static final String GET_ORDER_API = "/v2/checkout/orders/";
	
	@Autowired private SettingService settingService;
	
	// 페이팔 onApprove
	public boolean validateOrder(String orderId) throws PayPalApiException {
		PayPalOrderResponse orderResponse = getOrderDetails(orderId);
		
		return orderResponse.validate(orderId);
	}

	// 주문 내역 GET
	// 상태가 OK 가 아니면 예외 처리
	private PayPalOrderResponse getOrderDetails(String orderId) throws PayPalApiException {
		ResponseEntity<PayPalOrderResponse> response = makeRequest(orderId);
		
		HttpStatus statusCode = response.getStatusCode();
		
		if (!statusCode.equals(HttpStatus.OK)) {
			throwExceptionForNonOKResponse(statusCode);
		}
		
		return response.getBody();
	}

	// 페이팔 요청 URL, ID, 비밀키  설정
	private ResponseEntity<PayPalOrderResponse> makeRequest(String orderId) {
		PaymentSettingBag paymentSettings = settingService.getPaymentSettings();
		String baseURL = paymentSettings.getURL();
		String requestURL = baseURL + GET_ORDER_API + orderId;
		String clientId = paymentSettings.getClientID();
		String clientSecret = paymentSettings.getClientSecret();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.add("Accept-Language", "en_US");
		headers.setBasicAuth(clientId, clientSecret);
		
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
		RestTemplate restTemplate = new RestTemplate();
		
		return restTemplate.exchange(
				requestURL, HttpMethod.GET, request, PayPalOrderResponse.class);
	}

	private void throwExceptionForNonOKResponse(HttpStatus statusCode) throws PayPalApiException {
		String message = null;
		
		switch (statusCode) {
		case NOT_FOUND: 
			message = "주문ID를 찾지 못했습니다.";
			
		case BAD_REQUEST:
			message = "잘못된 요청: 페이팔 결제 API";
			
		case INTERNAL_SERVER_ERROR:
			message = "페이팔 서버 에러";
			
		default:
			message = "페이팔 에러: 올바르지 않은 상태코드가 반환되었습니다.";
		}
		
		throw new PayPalApiException(message);
	}
}
