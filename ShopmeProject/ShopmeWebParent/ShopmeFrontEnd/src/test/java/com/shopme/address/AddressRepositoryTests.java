package com.shopme.address;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Address;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class AddressRepositoryTests {

	@Autowired private AddressRepository repo;
	
	// 배송지 추가
	@Test
	public void testAddNew() {
		Integer customerId = 40;
		Integer countryId = 234;
		
		Address newAddress = new Address();
		newAddress.setCustomer(new Customer(customerId));
		newAddress.setCountry(new Country(countryId));
		newAddress.setFirstName("aaa");
		newAddress.setLastName("sss");
		newAddress.setPhoneNumber("5554544");
		newAddress.setAddressLine1("경기도 구리시");
		newAddress.setAddressLine2("수택동");
		newAddress.setState("경기도");
		newAddress.setCity("구리시");
		newAddress.setPostalCode("4213");
		
		Address savedAddress = repo.save(newAddress);
		
		assertThat(savedAddress).isNotNull();
		assertThat(savedAddress.getId()).isGreaterThan(0);
	}
	
	// 배송지 리턴 by 회원ID
	@Test
	public void testFindByCustomer() {
		Integer customerId = 5;
		List<Address> listAddresses = repo.findByCustomer(new Customer(customerId));
		assertThat(listAddresses.size()).isGreaterThan(0);
		
		listAddresses.forEach(System.out::println);
	}
	
	// 배송지 리턴 by 배송지ID, 회원ID
	@Test
	public void testFindByIdAndCustomer() {
		Integer addressId = 1;
		Integer customerId = 5;
		
		Address address = repo.findByIdAndCustomer(addressId, customerId);
		
		assertThat(address).isNotNull();
		System.out.println(address);
	}
	
	// 배송지 정보 수정
	@Test
	public void testUpdate() {
		Integer addressId = 1;
		String phoneNumber = "01056546";
		
		Address address = repo.findById(addressId).get();
		address.setPhoneNumber(phoneNumber);

		Address updatedAddress = repo.save(address);
		assertThat(updatedAddress.getPhoneNumber()).isEqualTo(phoneNumber);
	}
	
	// 배송지 삭제
	@Test
	public void testDeleteByIdAndCustomer() {
		Integer addressId = 1;
		Integer customerId = 5;
		
		repo.deleteByIdAndCustomer(addressId, customerId);
		
		Address address = repo.findByIdAndCustomer(addressId, customerId);
		assertThat(address).isNull();
	}	
	
	// 해당 배송지 기본 배송지로 설정
	@Test
	public void testSetDefault() {
		Integer addressId = 4;
		repo.setDefaultAddress(addressId);
		
		Address address = repo.findById(addressId).get();
		assertThat(address.isDefaultForShipping()).isTrue();
	}
	
	// 기본 배송지를 제외한 나머지 배송지들을 defaultForShipping = false 
	@Test
	public void testSetNonDefaultAddresses() {
		Integer addressId = 8;
		Integer customerId = 5;
		repo.setNonDefaultForOthers(addressId, customerId);			
	}
	
	// 기본 배송지 리턴
	@Test
	public void testGetDefault() {
		Integer customerId = 5;
		Address address = repo.findDefaultByCustomer(customerId);
		assertThat(address).isNotNull();
		System.out.println(address);
	}
	
}
