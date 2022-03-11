package com.shopme.customer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.AuthenticationType;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CustomerRepositoryTests {

	@Autowired private CustomerRepository repo;
	@Autowired private TestEntityManager entityManager;
	
	// 회원 생성
	@Test
	public void testCreateCustomer1() {
		Integer countryId = 234; // USA
		Country country = entityManager.find(Country.class, countryId);
		
		Customer customer = new Customer();
		customer.setCountry(country);
		customer.setFirstName("han");
		customer.setLastName("pizza");
		customer.setPassword("password123");
		customer.setEmail("sadadasd@gmail.com");
		customer.setPhoneNumber("1325646");
		customer.setAddressLine1("sadsadjalskd");
		customer.setCity("asdasds");
		customer.setState("asdadew");
		customer.setPostalCode("124123");
		customer.setCreatedTime(new Date());
		
		Customer savedCustomer = repo.save(customer);
		
		assertThat(savedCustomer).isNotNull();
		assertThat(savedCustomer.getId()).isGreaterThan(0);
	}
	
	// 회원 생성
	@Test
	public void testCreateCustomer2() {
		Integer countryId = 106; // India
		Country country = entityManager.find(Country.class, countryId);
		
		Customer customer = new Customer();
		customer.setCountry(country);
		customer.setFirstName("kim");
		customer.setLastName("ha");
		customer.setPassword("password456");
		customer.setEmail("asdgrew@gmail.com");
		customer.setPhoneNumber("41512321");
		customer.setAddressLine1("fdgsgasd");
		customer.setAddressLine2("adsada");
		customer.setCity("dsfdsfas");
		customer.setState("fdghrtwerw");
		customer.setPostalCode("15646");
		customer.setCreatedTime(new Date());
		
		Customer savedCustomer = repo.save(customer);
		
		assertThat(savedCustomer).isNotNull();
		assertThat(savedCustomer.getId()).isGreaterThan(0);
	}	
	
	// 회원 목록 리스트 출력
	@Test
	public void testListCustomers() {
		Iterable<Customer> customers = repo.findAll();
		customers.forEach(System.out::println);
		
		assertThat(customers).hasSizeGreaterThan(1);
	}
	
	// 회원 정보 수정
	@Test
	public void testUpdateCustomer() {
		Integer customerId = 1;
		String lastName = "kimmm";
		
		Customer customer = repo.findById(customerId).get();
		customer.setLastName(lastName);
		customer.setEnabled(true);
		
		Customer updatedCustomer = repo.save(customer);
		assertThat(updatedCustomer.getLastName()).isEqualTo(lastName);
	}
	
	// 회원 get By ID
	@Test
	public void testGetCustomer() {
		Integer customerId = 2;
		Optional<Customer> findById = repo.findById(customerId);
		
		assertThat(findById).isPresent();
		
		Customer customer = findById.get();
		System.out.println(customer);
	}
	
	// 회원 삭제
	@Test
	public void testDeleteCustomer() {
		Integer customerId = 2;
		repo.deleteById(customerId);
		
		Optional<Customer> findById = repo.findById(customerId);		
		assertThat(findById).isNotPresent();		
	}
	
	// 회원 리턴 by 이메일
	@Test
	public void testFindByEmail() {
		String email = "asdgrew@gmail.com";
		Customer customer = repo.findByEmail(email);
		
		assertThat(customer).isNotNull();
		System.out.println(customer);		
	}
	
	// 회원 리턴 by 회원 인증 코드
	@Test
	public void testFindByVerificationCode() {
		String code = "code_123";
		Customer customer = repo.findByVerificationCode(code);
		
		assertThat(customer).isNotNull();
		System.out.println(customer);		
	}
	
	// 회원 활성화
	@Test
	public void testEnableCustomer() {
		Integer customerId = 1;
		repo.enable(customerId);
		
		Customer customer = repo.findById(customerId).get();
		assertThat(customer.isEnabled()).isTrue();
	}
	
	// 회원 인증 타입 수정
	@Test
	public void testUpdateAythenticationType() {
		Integer id=1;
		repo.updateAuthenticationType(id, AuthenticationType.DATABASE);
		
		Customer customer=repo.findById(id).get();
		assertThat(customer.getAuthenticationType()).isEqualTo(AuthenticationType.DATABASE);
	}
	
}
