package com.shopme.address;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Address;
import com.shopme.common.entity.Customer;

@Service
@Transactional
public class AddressService {
	
	@Autowired private AddressRepository repo;
	
	// 회원의 배송지 목록 리스트
	public List<Address> listAddressBook(Customer customer) {
		return repo.findByCustomer(customer);
	}

	// 배송지 저장
	public void save(Address address) {
		repo.save(address);
	}
	
	// 배송지 리턴
	public Address get(Integer addressId, Integer customerId) {
		return repo.findByIdAndCustomer(addressId, customerId);
	}
	
	// 배송지 삭제
	public void delete(Integer addressId, Integer customerId) {
		repo.deleteByIdAndCustomer(addressId, customerId);
	}
	
	// 기본 배송지로 설정
	public void setDefaultAddress(Integer defaultAddressId, Integer customerId) {
		// 해당 배송지의 기본 배송지 id값이 0이상이라면, 해당 배송지를 기본 배송지로 설정
		if (defaultAddressId > 0) {
			repo.setDefaultAddress(defaultAddressId);
		}
		// 기본 배송지를 제외한 다른 배송지들을 false로 설정
		repo.setNonDefaultForOthers(defaultAddressId, customerId);
	}
	
	// 기본 배송지를 리턴
	public Address getDefaultAddress(Customer customer) {
		return repo.findDefaultByCustomer(customer.getId());
	}
}
