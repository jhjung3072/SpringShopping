package com.shopme.shoppingcart;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.Utility;
import com.shopme.common.entity.Customer;
import com.shopme.common.exception.CustomerNotFoundException;
import com.shopme.customer.CustomerService;

@RestController
public class ShoppingCartRestController {
	@Autowired private ShoppingCartService cartService;
	@Autowired private CustomerService customerService;
	
	// 해당 상품 장바구니에 추가(장바구니에 해당 상품이 있다면 거기에 더하기)
	@PostMapping("/cart/add/{productId}/{quantity}")
	public String addProductToCart(@PathVariable("productId") Integer productId,
			@PathVariable("quantity") Integer quantity, HttpServletRequest request) {
		
		try { 
			Customer customer = getAuthenticatedCustomer(request);
			Integer updatedQuantity = cartService.addProduct(productId, quantity, customer); // 수량 추가
			
			return updatedQuantity + "개의 상품이 장바구니에 담겼습니다.";
		} catch (CustomerNotFoundException ex) {
			return "장바구니에 담기위해서 로그인이 필요합니다.";
		} catch (ShoppingCartException ex) {
			return ex.getMessage();
		}
		
	}
	
	// 승인된 회원 객체 리턴
	private Customer getAuthenticatedCustomer(HttpServletRequest request) 
			throws CustomerNotFoundException {
		String email = Utility.getEmailOfAuthenticatedCustomer(request);
		if (email == null) {
			throw new CustomerNotFoundException("승인되지 않은 회원입니다.");
		}
				
		return customerService.getCustomerByEmail(email);
	}
	
	// 장바구니에서 상품 수량 업데이트 POST
	@PostMapping("/cart/update/{productId}/{quantity}")
	public String updateQuantity(@PathVariable("productId") Integer productId,
			@PathVariable("quantity") Integer quantity, HttpServletRequest request) {
		try {
			Customer customer = getAuthenticatedCustomer(request);
			float subtotal = cartService.updateQuantity(productId, quantity, customer);
			
			return String.valueOf(subtotal);
		} catch (CustomerNotFoundException ex) {
			return "상품의 수량을 변경하기 위해서 로그인이 필요합니다.";
		}	
	}
	
	// 장바구니에서 상품 삭제 DELETE
	@DeleteMapping("/cart/remove/{productId}")
	public String removeProduct(@PathVariable("productId") Integer productId,
			HttpServletRequest request) {
		try {
			Customer customer = getAuthenticatedCustomer(request);
			cartService.removeProduct(productId, customer);
			
			return "해당 상품이 장바구니에서 삭제되었습니다.";
			
		} catch (CustomerNotFoundException e) {
			return "상품을 장바구니에서 삭제하기 위해서는 로그인이 필요합니다.";
		}
	}
}
