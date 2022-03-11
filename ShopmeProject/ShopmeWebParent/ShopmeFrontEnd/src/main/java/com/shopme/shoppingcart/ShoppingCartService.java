package com.shopme.shoppingcart;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.product.Product;
import com.shopme.product.ProductRepository;

@Service
@Transactional
public class ShoppingCartService {

	@Autowired private CartItemRepository cartRepo;
	@Autowired private ProductRepository productRepo;
	
	// 상품 페이지에서 장바구니로 추가
	public Integer addProduct(Integer productId, Integer quantity, Customer customer) 
			throws ShoppingCartException {
		Integer updatedQuantity = quantity;
		Product product = new Product(productId);
		
		CartItem cartItem = cartRepo.findByCustomerAndProduct(customer, product);
		
		if (cartItem != null) { // 회원의 장바구니에 이미 있는 상품이라면,
			updatedQuantity = cartItem.getQuantity() + quantity; // 수량 업데이트
			
			if (updatedQuantity > 5) { // 최대 수량 5개
				throw new ShoppingCartException(quantity + "개의 상품을 추가할 수 없습니다."
						+ "회원님의 장바구니에 " + cartItem.getQuantity() + "개의 상품이 있습니다. "
						+ "최대 수량은 5개 입니다.");
			}
		} else { // 회원의 장바구니에 없는 상품이라면
			cartItem = new CartItem();
			cartItem.setCustomer(customer);
			cartItem.setProduct(product);
		}
		
		cartItem.setQuantity(updatedQuantity);
		
		cartRepo.save(cartItem);
		
		return updatedQuantity;
	}
	
	// 장바구니 목록
	public List<CartItem> listCartItems(Customer customer) {
		return cartRepo.findByCustomer(customer);
	}
	
	// 수량 및 가격 수정
	public float updateQuantity(Integer productId, Integer quantity, Customer customer) {
		cartRepo.updateQuantity(quantity, customer.getId(), productId);
		Product product = productRepo.findById(productId).get();
		float subtotal = product.getDiscountPrice() * quantity;
		return subtotal;
	}
	
	// 장바구니에서 상품 삭제
	public void removeProduct(Integer productId, Customer customer) {
		cartRepo.deleteByCustomerAndProduct(customer.getId(), productId);
	}
	
	// 장바구니 삭제
	public void deleteByCustomer(Customer customer) {
		cartRepo.deleteByCustomer(customer.getId());
	}
}
