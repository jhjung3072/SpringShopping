package com.shopme.shoppingcart;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.product.Product;

public interface CartItemRepository extends CrudRepository<CartItem, Integer> {
	public List<CartItem> findByCustomer(Customer customer);
	
	// 상품과 회원으로 장바구니 리턴
	public CartItem findByCustomerAndProduct(Customer customer, Product product);
	
	// 회원의 장바구니에 있는 상품 수량 업데이트
	@Modifying
	@Query("UPDATE CartItem c SET c.quantity = ?1 WHERE c.customer.id = ?2 AND c.product.id = ?3")
	public void updateQuantity(Integer quantity, Integer customerId, Integer productId);
	
	// 회원의 장바구니에 있는 상품 삭제
	@Modifying
	@Query("DELETE FROM CartItem c WHERE c.customer.id = ?1 AND c.product.id = ?2")
	public void deleteByCustomerAndProduct(Integer customerId, Integer productId);
	
	// 회원의 장바구니 삭제
	@Modifying
	@Query("DELETE CartItem c WHERE c.customer.id = ?1")
	public void deleteByCustomer(Integer customerId);
}
