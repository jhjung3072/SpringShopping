package com.shopme.order;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.Utility;
import com.shopme.common.entity.Customer;
import com.shopme.common.exception.CustomerNotFoundException;
import com.shopme.common.exception.OrderNotFoundException;
import com.shopme.customer.CustomerService;

@RestController
public class OrderRestController {
	
	@Autowired private OrderService orderService;
	@Autowired private CustomerService customerService;
	
	@PostMapping("/orders/return")
	public ResponseEntity<?> handleOrderReturnRequest(@RequestBody OrderReturnRequest returnRequest,
			HttpServletRequest servletRequest) {
		
		System.out.println("주문 ID: " + returnRequest.getOrderId());
		System.out.println("사유: " + returnRequest.getReason());
		System.out.println("내용: " + returnRequest.getNote());
		
		Customer customer = null;
		
		try {
			customer = getAuthenticatedCustomer(servletRequest);
		} catch (CustomerNotFoundException ex) {
			return new ResponseEntity<>("로그인이 필요합니다.", HttpStatus.BAD_REQUEST);
		}
		
		try {
			orderService.setOrderReturnRequested(returnRequest, customer);
		} catch (OrderNotFoundException ex) {
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<>(new OrderReturnResponse(returnRequest.getOrderId()), HttpStatus.OK);
	}
	
	private Customer getAuthenticatedCustomer(HttpServletRequest request) 
			throws CustomerNotFoundException {
		String email = Utility.getEmailOfAuthenticatedCustomer(request);
		if (email == null) {
			throw new CustomerNotFoundException("인증되지 않은 회원");
		}
				
		return customerService.getCustomerByEmail(email);
	}	
}
