package com.shopme.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.shopme.common.entity.order.OrderDetail;
import com.shopme.common.entity.order.OrderStatus;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer>{
	
	// 해당 상품ID와 회원ID의 주문 추적 상태가 'DELIVERED' 상태인 OrderDetail 개수 반환 -> 1개 이상이면 리뷰 작성 가능
	@Query("SELECT COUNT(d) FROM OrderDetail d JOIN OrderTrack t ON d.order.id = t.order.id"
			+ " WHERE d.product.id =?1 AND d.order.customer.id = ?2 AND"
			+ " t.status=?3")
	public Long countByProductAndCustomerAndOrderStatus(Integer productId, Integer CustomerId, OrderStatus status);
}
