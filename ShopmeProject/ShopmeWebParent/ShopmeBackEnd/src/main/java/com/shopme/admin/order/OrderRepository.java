package com.shopme.admin.order;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import com.shopme.admin.paging.SearchRepository;
import com.shopme.common.entity.order.Order;

public interface OrderRepository extends SearchRepository<Order, Integer> {
	
	// 주문 키워드 검색(id, 이름, 주소, 연락처, 우편번호, 도, 시, 국가, 결제방법, 상태, 회원이름) 및 페이징
	@Query("SELECT o FROM Order o WHERE CONCAT('#', o.id) LIKE %?1% OR "
			+ " CONCAT(o.firstName, ' ', o.lastName) LIKE %?1% OR"
			+ " o.firstName LIKE %?1% OR"
			+ " o.lastName LIKE %?1% OR o.phoneNumber LIKE %?1% OR"
			+ " o.addressLine1 LIKE %?1% OR o.addressLine2 LIKE %?1% OR"
			+ " o.postalCode LIKE %?1% OR o.city LIKE %?1% OR"
			+ " o.state LIKE %?1% OR o.country LIKE %?1% OR"
			+ " o.paymentMethod LIKE %?1% OR o.status LIKE %?1% OR"
			+ " o.customer.firstName LIKE %?1% OR"
			+ " o.customer.lastName LIKE %?1%")
	public Page<Order> findAll(String keyword, Pageable pageable);
	
	public Long countById(Integer id);
	
	// 기간 내에 주문목록 리스트
	// id, orderTime, productCost, subtotal, total 만 불러오기 위해 new 사용
	@Query("SELECT NEW com.shopme.common.entity.order.Order(o.id, o.orderTime, o.productCost,"
			+ "o.subtotal, o.total) FROM Order o WHERE"
			+ " o.orderTime BETWEEN ?1 and ?2 ORDER BY o.orderTime ASC")
	public List<Order> findByOrderTimeBetween(Date startTime, Date endTime);
}
