package com.shopme.customer;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.AuthenticationType;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.common.exception.CustomerNotFoundException;
import com.shopme.setting.CountryRepository;

import net.bytebuddy.utility.RandomString;

@Service
@Transactional
public class CustomerService {

	@Autowired private CountryRepository countryRepo;
	@Autowired private CustomerRepository customerRepo;
	@Autowired PasswordEncoder passwordEncoder;
	
	// 국가 리스트 목록
	public List<Country> listAllCountries() {
		return countryRepo.findAllByOrderByNameAsc();
	}
	
	// 이메일 중복 체크
	public boolean isEmailUnique(String email) {
		Customer customer = customerRepo.findByEmail(email);
		return customer == null;
	}
	
	// 회원 가입 등록
	public void registerCustomer(Customer customer) {
		// 패스워드 암호화
		encodePassword(customer);
		customer.setEnabled(false);
		customer.setCreatedTime(new Date());
		// 가입 유형은 DATABASE 로 설정
		customer.setAuthenticationType(AuthenticationType.DATABASE);
		
		// 회원 인증 코드 생성 및 등록
		String randomCode = RandomString.make(64);
		customer.setVerificationCode(randomCode);
		
		customerRepo.save(customer);
		
	}
	
	// 이메일로 회원 객체 리턴
	public Customer getCustomerByEmail(String email) {
		return customerRepo.findByEmail(email);
	}

	// 패스워드 암호화
	private void encodePassword(Customer customer) {
		String encodedPassword = passwordEncoder.encode(customer.getPassword());
		customer.setPassword(encodedPassword);
	}
	
	// 회원 인증 활성화 by 인증 코드
	public boolean verify(String verificationCode) {
		// 해당 인증 코드로 회원 객체 리턴
		Customer customer = customerRepo.findByVerificationCode(verificationCode);
		
		// 해당 인증 코드로 회원을 찾을 수 없거나 이미 인증된 회원이라면 false
		if (customer == null || customer.isEnabled()) {
			return false;
		} else { // 회원 활성화 true
			customerRepo.enable(customer.getId());
			return true;
		}
	}
	
	// 회원 계정 유형 업데이트
	public void updateAuthenticationType(Customer customer, AuthenticationType type) {
		if (!customer.getAuthenticationType().equals(type)) {
			customerRepo.updateAuthenticationType(customer.getId(), type);
		}
	}
	
	// 소셜 회원가입 시(구글, 페이스북) 회원 정보 기본값 설정 
	public void addNewCustomerUponOAuthLogin(String name, String email, String countryCode,
			AuthenticationType authenticationType) {
		Customer customer = new Customer();
		customer.setEmail(email);
		setName(name, customer);
		
		customer.setEnabled(true);
		customer.setCreatedTime(new Date());
		customer.setAuthenticationType(authenticationType);
		customer.setPassword("");
		customer.setAddressLine1("");
		customer.setCountry(countryRepo.findByCode(countryCode));
		customer.setState("");
		customer.setCity("");
		customer.setPostalCode("");
		customer.setPhoneNumber("");
		customerRepo.save(customer);
	}	
	
	// 소설 회원가입 시 이름 설정
	private void setName(String name, Customer customer) {
		// 공백으로 이름 분류
		String[] nameArray = name.split(" ");
		// 공백이 없으면 이름만 설정
		if (nameArray.length < 2) {
			customer.setFirstName(name);
			customer.setLastName("");
		} else { // 공백이 있으면 첫번째 부분은 이름, 두번째 부분은 성으로 설정
			String firstName = nameArray[0];
			customer.setFirstName(firstName);
			// name에서 firtName을 지우면 lastName
			String lastName = name.replaceFirst(firstName + " ", "");
			customer.setLastName(lastName);
		}
	}
	
	// 회원 수정
	public void update(Customer customerInForm) {
		Customer customerInDB = customerRepo.findById(customerInForm.getId()).get();
		
		// 회원 유형이 db 타입이고
		if (customerInDB.getAuthenticationType().equals(AuthenticationType.DATABASE)) {
			if (!customerInForm.getPassword().isEmpty()) { // 폼에 패스워드가 채워져있으면 패스워드 암호화 후 저장
				String encodedPassword = passwordEncoder.encode(customerInForm.getPassword());
				customerInForm.setPassword(encodedPassword);			
			} else { // 폼에 패스워드가 채워져있지 않으면 기존의 패스워드 사용
				customerInForm.setPassword(customerInDB.getPassword());
			}		
		} else { // 회원 유형이 소셜 회원이면 패스워드 그대로 사용
			customerInForm.setPassword(customerInDB.getPassword());
		}
		
		customerInForm.setEnabled(customerInDB.isEnabled());
		customerInForm.setCreatedTime(customerInDB.getCreatedTime());
		customerInForm.setVerificationCode(customerInDB.getVerificationCode());
		customerInForm.setAuthenticationType(customerInDB.getAuthenticationType());
		customerInForm.setResetPasswordToken(customerInDB.getResetPasswordToken());
		
		customerRepo.save(customerInForm);
	}

	// 패스워드 초기화 토큰 수정
	public String updateResetPasswordToken(String email) throws CustomerNotFoundException {
		Customer customer = customerRepo.findByEmail(email);
		
		// 해당 이메일을 가진 회원이 있다면 랜덤 토큰 생성 후 저장
		if (customer != null) {
			String token = RandomString.make(30);
			customer.setResetPasswordToken(token);
			customerRepo.save(customer);
			return token;
		} else {
			throw new CustomerNotFoundException("해당 이메일을 가진 회원을 찾을 수 없습니다: " + email);
		}
	}	
	
	// 패스워드 초기화 토큰으로 회원 객체 리턴
	public Customer getByResetPasswordToken(String token) {
		return customerRepo.findByResetPasswordToken(token);
	}
	
	// 패스워드 수정
	public void updatePassword(String token, String newPassword) throws CustomerNotFoundException {
		// 패스워드 초기화 토큰으로 회원 객체 리턴
		Customer customer = customerRepo.findByResetPasswordToken(token);
		if (customer == null) {
			throw new CustomerNotFoundException("회원을 찾을 수 없습니다.: 잘못된 패스워드 토큰입니다.");
		}
		
		// 새 패스워드 저장 후 암호화, 패스워드 토큰 값 null
		customer.setPassword(newPassword);
		customer.setResetPasswordToken(null);
		encodePassword(customer);
		
		customerRepo.save(customer);
	}
}
