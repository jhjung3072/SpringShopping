package com.shopme.address;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.Utility;
import com.shopme.common.entity.Address;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.customer.CustomerService;

@Controller
public class AddressController {

	@Autowired private AddressService addressService;
	@Autowired private CustomerService customerService;	
	
	// 배송지 목록 GET
	@GetMapping("/address_book")
	public String showAddressBook(Model model, HttpServletRequest request) {
		Customer customer = getAuthenticatedCustomer(request);
		List<Address> listAddresses = addressService.listAddressBook(customer);
		
		// 기본 배송지를 제외한 배송지에 '기본 배송지로 설정' 버튼을 보여줌
		boolean usePrimaryAddressAsDefault = true;
		for (Address address : listAddresses) {
			if (address.isDefaultForShipping()) {
				usePrimaryAddressAsDefault = false;
				break;
			}
		}
		
		model.addAttribute("listAddresses", listAddresses);
		model.addAttribute("customer", customer);
		model.addAttribute("usePrimaryAddressAsDefault", usePrimaryAddressAsDefault);
		
		return "address_book/addresses";
	}
	
	// 승인된 회원의 객체 리턴
	private Customer getAuthenticatedCustomer(HttpServletRequest request) {
		String email = Utility.getEmailOfAuthenticatedCustomer(request);				
		return customerService.getCustomerByEmail(email);
	}		
	
	@GetMapping("/address_book/new")
	public String newAddress(Model model) {
		List<Country> listCountries = customerService.listAllCountries();
		
		model.addAttribute("listCountries", listCountries);
		model.addAttribute("address", new Address());
		model.addAttribute("pageTitle", "새 주소 추가하기");
		
		return "address_book/address_form";
	}
	
	// 배송지 저장 POST
	@PostMapping("/address_book/save")
	public String saveAddress(Address address, HttpServletRequest request, RedirectAttributes ra) {
		Customer customer = getAuthenticatedCustomer(request);
		
		address.setCustomer(customer);
		addressService.save(address);
		
		String redirectOption = request.getParameter("redirect");
		String redirectURL = "redirect:/address_book";
		
		// 결제단계에서 redirect 되었으면 배송지 저장 후 결졔단계로 이동
		if ("checkout".equals(redirectOption)) {
			redirectURL += "?redirect=checkout";
		}
		
		ra.addFlashAttribute("message", "배송지가 성공적으로 저장되었습니다.");
		// redirect값에 checkout 이 없다면 그냥 배송지 목록으로 이동
		return redirectURL;
	}
	
	// 배송지 목록 수정 GET
	@GetMapping("/address_book/edit/{id}")
	public String editAddress(@PathVariable("id") Integer addressId, Model model,
			HttpServletRequest request) {
		Customer customer = getAuthenticatedCustomer(request);
		List<Country> listCountries = customerService.listAllCountries();
		
		Address address = addressService.get(addressId, customer.getId());

		model.addAttribute("address", address);
		model.addAttribute("listCountries", listCountries);
		model.addAttribute("pageTitle", "배송지 수정 (ID: " + addressId + ")");
		
		return "address_book/address_form";
	}
	
	// 배송지 삭제 GET
	@GetMapping("/address_book/delete/{id}")
	public String deleteAddress(@PathVariable("id") Integer addressId, RedirectAttributes ra,
			HttpServletRequest request) {
		Customer customer = getAuthenticatedCustomer(request);
		addressService.delete(addressId, customer.getId());
		
		ra.addFlashAttribute("message", "배송지 ID: " + addressId + " 가 삭제되었습니다");
		
		return "redirect:/address_book";
	}
	
	// 기본 배송지로 설정 GET
	@GetMapping("/address_book/default/{id}")
	public String setDefaultAddress(@PathVariable("id") Integer addressId,
			HttpServletRequest request) {
		Customer customer = getAuthenticatedCustomer(request);
		addressService.setDefaultAddress(addressId, customer.getId());
		
		String redirectOption = request.getParameter("redirect");
		String redirectURL = "redirect:/address_book";
		
		// 장바구니에서 redirect 되었으면 기본 배송지로 설정 후 장바구니로 이동
		if ("cart".equals(redirectOption)) {
			redirectURL = "redirect:/cart";
		} else if ("checkout".equals(redirectOption)) { // 결제 단계에서 redirect 되었으면 기본 배송지로 설정 후 결제 단계로 이동
			redirectURL = "redirect:/checkout";
		}
		
		return redirectURL; 
	}
}
