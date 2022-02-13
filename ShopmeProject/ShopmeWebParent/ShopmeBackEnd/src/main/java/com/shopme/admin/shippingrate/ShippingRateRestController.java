package com.shopme.admin.shippingrate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShippingRateRestController {

	@Autowired private ShippingRateService service;
	
	// 주문 목록 수정에서 상품 추가시 배송비 계산 POST
	@PostMapping("/get_shipping_cost")
	public String getShippingCost(Integer productId, Integer countryId, String state) throws ShippingRateNotFoundException {
		float shippingCost=service.calculateShippingCost(productId, countryId, state);
		return String.valueOf(shippingCost);
	}
}
