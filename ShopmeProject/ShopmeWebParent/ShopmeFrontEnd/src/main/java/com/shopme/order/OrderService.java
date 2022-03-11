package com.shopme.order;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.checkout.CheckoutInfo;
import com.shopme.common.entity.Address;
import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.order.Order;
import com.shopme.common.entity.order.OrderDetail;
import com.shopme.common.entity.order.OrderStatus;
import com.shopme.common.entity.order.OrderTrack;
import com.shopme.common.entity.order.PaymentMethod;
import com.shopme.common.entity.product.Product;
import com.shopme.common.exception.OrderNotFoundException;

@Service
public class OrderService {
	public static final int ORDERS_PER_PAGE = 5;
	
	@Autowired private OrderRepository repo;
	
	// 주문 내역 생성
	public Order createOrder(Customer customer, Address address, List<CartItem> cartItems,
			PaymentMethod paymentMethod, CheckoutInfo checkoutInfo) {
		Order newOrder = new Order();
		newOrder.setOrderTime(new Date());
		
		// 결제방법이 페이팔이면 이미 결제한 것(PAID)
		if (paymentMethod.equals(PaymentMethod.PAYPAL)) {
			newOrder.setStatus(OrderStatus.PAID);
		} else { // 그렇지 않으면 NEW
			newOrder.setStatus(OrderStatus.NEW);
		}
		
		newOrder.setCustomer(customer);
		newOrder.setProductCost(checkoutInfo.getProductCost());
		newOrder.setSubtotal(checkoutInfo.getProductTotal());
		newOrder.setShippingCost(checkoutInfo.getShippingCostTotal());
		newOrder.setTax(0.0f);
		newOrder.setTotal(checkoutInfo.getPaymentTotal());
		newOrder.setPaymentMethod(paymentMethod);
		newOrder.setDeliverDays(checkoutInfo.getDeliverDays());
		newOrder.setDeliverDate(checkoutInfo.getDeliverDate());
		
		// 배송지 정보가 없으면 회원 정보에서 복사
		if (address == null) {
			newOrder.copyAddressFromCustomer();
		} else { // 배송지 정보가 있으면 그것을 사용
			newOrder.copyShippingAddress(address);
		}
		
		Set<OrderDetail> orderDetails = newOrder.getOrderDetails();
		
		for (CartItem cartItem : cartItems) {
			Product product = cartItem.getProduct();
			
			OrderDetail orderDetail = new OrderDetail();
			orderDetail.setOrder(newOrder);
			orderDetail.setProduct(product);
			orderDetail.setQuantity(cartItem.getQuantity());
			orderDetail.setUnitPrice(product.getDiscountPrice());
			orderDetail.setProductCost(product.getCost() * cartItem.getQuantity());
			orderDetail.setSubtotal(cartItem.getSubtotal());
			orderDetail.setShippingCost(cartItem.getShippingCost());
			
			orderDetails.add(orderDetail);
		}
		OrderTrack track= new OrderTrack();
		track.setOrder(newOrder);
		track.setStatus(OrderStatus.NEW);
		track.setNotes(OrderStatus.NEW.defaultDescription());
		track.setUpdatedTime(new Date());
		
		newOrder.getOrderTracks().add(track);
		
		return repo.save(newOrder);
	}
	
	// 주문 내역 목록 페이징
	public Page<Order> listForCustomerByPage(Customer customer, int pageNum, 
			String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum - 1, ORDERS_PER_PAGE, sort);
		
		if (keyword != null) {
			return repo.findAll(keyword, customer.getId(), pageable);
		}
		
		return repo.findAll(customer.getId(), pageable);
	}
	
	// 주문 객체 리턴 by ID and Customer
	public Order getOrder(Integer id, Customer customer) {
		return repo.findByIdAndCustomer(id, customer);
	}	
	
	// 주문 취소 설정
	public void setOrderReturnRequested(OrderReturnRequest request, Customer customer) 
			throws OrderNotFoundException {
		Order order = repo.findByIdAndCustomer(request.getOrderId(), customer);
		if (order == null) {
			throw new OrderNotFoundException("주문 ID: " + request.getOrderId() + " 를 찾을 수 없습니다.");
		}
		
		// 이미 주문 취소가 요청되었으면 무시
		if (order.isReturnRequested()) return;
		
		OrderTrack track = new OrderTrack();
		track.setOrder(order);
		track.setUpdatedTime(new Date());
		track.setStatus(OrderStatus.RETURN_REQUESTED);
		
		// 주문 취소 사유와 내용
		String notes = "사유: " + request.getReason();
		if (!"".equals(request.getNote())) {
			notes += ". " + request.getNote();
		}
		
		track.setNotes(notes);
		
		order.getOrderTracks().add(track);
		order.setStatus(OrderStatus.RETURN_REQUESTED);
		
		repo.save(order);
	}
}
