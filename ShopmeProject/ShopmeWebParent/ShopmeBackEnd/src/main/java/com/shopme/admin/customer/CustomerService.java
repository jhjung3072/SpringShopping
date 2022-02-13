package com.shopme.admin.customer;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.setting.country.CountryRepository;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.common.exception.CustomerNotFoundException;

@Service
@Transactional
public class CustomerService {
	public static final int CUSTOMERS_PER_PAGE = 10;
	
	@Autowired private CustomerRepository customerRepo;
	@Autowired private CountryRepository countryRepo;	
	@Autowired private PasswordEncoder passwordEncoder;
	
	// 페이징
	public void listByPage(int pageNum, PagingAndSortingHelper helper) {
		helper.listEntities(pageNum, CUSTOMERS_PER_PAGE, customerRepo);
	}
	
	// 회원 활성화
	public void updateCustomerEnabledStatus(Integer id, boolean enabled) {
		customerRepo.updateEnabledStatus(id, enabled);
	}
	
	// 회원 get by ID
	public Customer get(Integer id) throws CustomerNotFoundException {
		try {
			return customerRepo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new CustomerNotFoundException("회원 ID: " + id +"를 찾을 수 없습니다.");
		}
	}

	// 국가 리스트 목록
	public List<Country> listAllCountries() {
		return countryRepo.findAllByOrderByNameAsc();
	}		
	
	// 이메일 중복 여부
	// true or false
	public boolean isEmailUnique(Integer id, String email) {
		Customer existCustomer = customerRepo.findByEmail(email);

		if (existCustomer != null && existCustomer.getId() != id) {
			// 같은 이메일이 이미 존재한다면 false
			return false;
		}
		
		return true;
	}
	
	// 회원 저장
	// passwordEncoder -> 암호화 적용
	public void save(Customer customerInForm) {
		Customer customerInDB = customerRepo.findById(customerInForm.getId()).get();
		
		// 패스워드 칸이 작성되었다면
		if (!customerInForm.getPassword().isEmpty()) {
			// 패스워드 암호화
			String encodedPassword = passwordEncoder.encode(customerInForm.getPassword());
			customerInForm.setPassword(encodedPassword);			
		} else {
			// 패스워드 칸이 비어져있다면 -> 회원 수정 시
			customerInForm.setPassword(customerInDB.getPassword());
		}		
		
		customerInForm.setEnabled(customerInDB.isEnabled());
		customerInForm.setCreatedTime(customerInDB.getCreatedTime());
		customerInForm.setVerificationCode(customerInDB.getVerificationCode());
		customerInForm.setAuthenticationType(customerInDB.getAuthenticationType());
		customerInForm.setResetPasswordToken(customerInDB.getResetPasswordToken());
		
		customerRepo.save(customerInForm);
	}
	
	// 회원 삭제
	public void delete(Integer id) throws CustomerNotFoundException {
		Long count = customerRepo.countById(id);
		if (count == null || count == 0) {
			throw new CustomerNotFoundException("회원 ID: " + id +"를 찾을 수 없습니다.");
		}
		
		customerRepo.deleteById(id);
	}
	
}
