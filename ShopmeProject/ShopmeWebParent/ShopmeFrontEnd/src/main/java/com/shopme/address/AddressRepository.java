package com.shopme.address;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.shopme.common.entity.Address;
import com.shopme.common.entity.Customer;

public interface AddressRepository extends CrudRepository<Address, Integer> {
	
	public List<Address> findByCustomer(Customer customer);
	
	// 배송지ID 와 회원ID로 배송지 리턴
	@Query("SELECT a FROM Address a WHERE a.id = ?1 AND a.customer.id = ?2")
	public Address findByIdAndCustomer(Integer addressId, Integer customerId);
	
	// 배송지ID 와 회원ID로 배송지 삭제
	@Query("DELETE FROM Address a WHERE a.id = ?1 AND a.customer.id = ?2")
	@Modifying
	public void deleteByIdAndCustomer(Integer addressId, Integer customerId);
	
	// 해당 배송지를 기본 배송지로 업데이트
	@Query("UPDATE Address a SET a.defaultForShipping = true WHERE a.id = ?1")
	@Modifying
	public void setDefaultAddress(Integer id);
	
	// 기본 배송지를 제외한 나머지 배송지를 false로 업데이트
	@Query("UPDATE Address a SET a.defaultForShipping = false "
			+ "WHERE a.id != ?1 AND a.customer.id = ?2")
	@Modifying
	public void setNonDefaultForOthers(Integer defaultAddressId, Integer customerId);
	
	// 회원ID로 기본 배송지 리턴
	@Query("SELECT a FROM Address a WHERE a.customer.id = ?1 AND a.defaultForShipping = true")
	public Address findDefaultByCustomer(Integer customerId);
}
