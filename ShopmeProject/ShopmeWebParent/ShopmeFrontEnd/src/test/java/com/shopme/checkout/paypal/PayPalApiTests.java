package com.shopme.checkout.paypal;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class PayPalApiTests {
	private static final String BASE_URL="https://api.sandbox.paypal.com";
	private static final String GET_ORDER_API="/v2/checkout/orders/";
	private static final String CLIENT_ID="AeWX0U0KZ4E3uZzBU4EoKybZ-0sIy6TEgQmm20vnZlMH3s3MoZccqIUiP_j6rgCObq69Ey4ZYOyBorom";
	private static final String CLIENT_SECRET="EC410lng_BN-j36i2nWVT5AwfDCVDg-N63eTLWQqJtyB2qo5RLR0Yy6tUks_syK2nqvDhLcmZhp3d-DN";
	
	
	// 페이팔 주문ID 및 상태 get
	// 참고 : https://developer.paypal.com/api/orders/v2/
	@Test
	public void testGetOrderDetails() {
		String orderId="22D61778R1406735K"; // 페이팔 orderId
		String requestURL=BASE_URL + GET_ORDER_API +orderId;
		
		HttpHeaders headers=new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.add("Accept-Language", "en_US");
		headers.setBasicAuth(CLIENT_ID, CLIENT_SECRET);
		
		HttpEntity<MultiValueMap<String, String>> reuqest=new HttpEntity<>(headers);
		RestTemplate restTemplate = new RestTemplate();
		
		// requestURL, method, request Entity, Response Type
		ResponseEntity<PayPalOrderResponse > response=restTemplate.exchange(
				requestURL, HttpMethod.GET, reuqest, PayPalOrderResponse.class);
		
		PayPalOrderResponse orderResponse=response.getBody();
		
		System.out.println("Order Id: " +orderResponse.getId());
		System.out.println("Validated: " +orderResponse.validate(orderId));
	}
}
