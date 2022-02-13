package com.shopme.admin.order;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.paging.PagingAndSortingParam;
import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.admin.setting.SettingService;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.order.Order;
import com.shopme.common.entity.order.OrderDetail;
import com.shopme.common.entity.order.OrderStatus;
import com.shopme.common.entity.order.OrderTrack;
import com.shopme.common.entity.product.Product;
import com.shopme.common.entity.setting.Setting;
import com.shopme.common.exception.OrderNotFoundException;

@Controller
public class OrderController {
	private String defaultRedirectURL = "redirect:/orders/page/1?sortField=orderTime&sortDir=desc";
	
	@Autowired private OrderService orderService;
	@Autowired private SettingService settingService;

	// 주문 목록 첫 페이지
	@GetMapping("/orders")
	public String listFirstPage() {
		return defaultRedirectURL;
	}
	
	// 주문 목록 페이징
	@GetMapping("/orders/page/{pageNum}")
	public String listByPage(
			@PagingAndSortingParam(listName = "listOrders", moduleURL = "/orders") PagingAndSortingHelper helper,
			@PathVariable(name = "pageNum") int pageNum,
			HttpServletRequest request,
			@AuthenticationPrincipal ShopmeUserDetails loggedUser) {

		orderService.listByPage(pageNum, helper);
		loadCurrencySetting(request);
		
		// 배송관리자일 경우에만 허용
		if (!loggedUser.hasRole("운영자") && !loggedUser.hasRole("판매관리자") && loggedUser.hasRole("배송관리자")) {
			return "orders/orders_shipper";
		}
		
		return "orders/orders";
	}
	
	// 환율 설정 불러오기
	private void loadCurrencySetting(HttpServletRequest request) {
		List<Setting> currencySettings = settingService.getCurrencySettings();
		
		for (Setting setting : currencySettings) {
			request.setAttribute(setting.getKey(), setting.getValue());
		}	
	}	
	
	// 주문 상세 페이지 GET
	@GetMapping("/orders/detail/{id}")
	public String viewOrderDetails(@PathVariable("id") Integer id, Model model, 
			RedirectAttributes ra, HttpServletRequest request,
			@AuthenticationPrincipal ShopmeUserDetails loggedUser) {
		try {
			Order order = orderService.get(id);
			loadCurrencySetting(request);			
			
			boolean isVisibleForAdminOrSalesperson = false;
			
			// 운영자와 판매관리자에게만 보여짐
			if (loggedUser.hasRole("운영자") || loggedUser.hasRole("판매관리자")) {
				isVisibleForAdminOrSalesperson = true;
			}
			
			model.addAttribute("isVisibleForAdminOrSalesperson", isVisibleForAdminOrSalesperson);
			model.addAttribute("order", order);
			
			return "orders/order_details_modal";
		} catch (OrderNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			return defaultRedirectURL;
		}
		
	}
	
	// 주문내역 삭제 GET
	@GetMapping("/orders/delete/{id}")
	public String deleteOrder(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
		try {
			orderService.delete(id);;
			ra.addFlashAttribute("message", "주문 ID: " + id + " 를 삭제했습니다.");
		} catch (OrderNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
		}
		
		return defaultRedirectURL;
	}
	
	// 주문 수정 GET
	@GetMapping("/orders/edit/{id}")
	public String editOrder(@PathVariable("id") Integer id, Model model, RedirectAttributes ra,
			HttpServletRequest request) {
		try {
			Order order = orderService.get(id);;
			
			List<Country> listCountries = orderService.listAllCountries();
			
			model.addAttribute("pageTitle", "주문 수정 (ID: " + id + ")");
			model.addAttribute("order", order);
			model.addAttribute("listCountries", listCountries);
			
			return "orders/order_form";
			
		} catch (OrderNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			return defaultRedirectURL;
		}
		
	}	
	
	// 주문 저장 POST
	@PostMapping("/order/save")
	public String saveOrder(Order order, HttpServletRequest request, RedirectAttributes ra) {
		String countryName = request.getParameter("countryName");
		order.setCountry(countryName);
		
		updateProductDetails(order, request);
		updateOrderTracks(order, request);

		orderService.save(order);		
		
		ra.addFlashAttribute("message", "주문 ID: " + order.getId() + " 가 수정되었습니다.");
		
		return defaultRedirectURL;
	}

	// 주문 페이지내 주문 추적 수정
	// 주문 id 순서대로 상태, 날짜, 내용을 각 배열에 저장
	private void updateOrderTracks(Order order, HttpServletRequest request) {
		String[] trackIds = request.getParameterValues("trackId");
		String[] trackStatuses = request.getParameterValues("trackStatus");
		String[] trackDates = request.getParameterValues("trackDate");
		String[] trackNotes = request.getParameterValues("trackNotes");
		
		List<OrderTrack> orderTracks = order.getOrderTracks();
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
		
		// 주문 id 순서대로 상태, 날짜, 내용을 각 배열에 저장
 		for (int i = 0; i < trackIds.length; i++) {
			OrderTrack trackRecord = new OrderTrack();
			
			Integer trackId = Integer.parseInt(trackIds[i]);
			if (trackId > 0) {
				trackRecord.setId(trackId);
			}
			
			trackRecord.setOrder(order);
			trackRecord.setStatus(OrderStatus.valueOf(trackStatuses[i]));
			trackRecord.setNotes(trackNotes[i]);
			
			try {
				trackRecord.setUpdatedTime(dateFormatter.parse(trackDates[i]));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			orderTracks.add(trackRecord);
		}
	}

	// 주문페이지 내 상품 상세페이지 수정
	// 상세페이지 id 순서대로 상품ID, 비용, 수량, 가격을 각 배열에 저장
	private void updateProductDetails(Order order, HttpServletRequest request) {
		String[] detailIds = request.getParameterValues("detailId");
		String[] productIds = request.getParameterValues("productId");
		String[] productPrices = request.getParameterValues("productPrice");
		String[] productDetailCosts = request.getParameterValues("productDetailCost");
		String[] quantities = request.getParameterValues("quantity");
		String[] productSubtotals = request.getParameterValues("productSubtotal");
		String[] productShipCosts = request.getParameterValues("productShipCost");
		
		Set<OrderDetail> orderDetails = order.getOrderDetails();
		
		for (int i = 0; i < detailIds.length; i++) {
			System.out.println("Detail ID: " + detailIds[i]);
			System.out.println("\t Prodouct ID: " + productIds[i]);
			System.out.println("\t Cost: " + productDetailCosts[i]);
			System.out.println("\t Quantity: " + quantities[i]);
			System.out.println("\t Subtotal: " + productSubtotals[i]);
			System.out.println("\t Ship cost: " + productShipCosts[i]);
			
			OrderDetail orderDetail = new OrderDetail();
			Integer detailId = Integer.parseInt(detailIds[i]);
			if (detailId > 0) {
				orderDetail.setId(detailId);
			}
			
			orderDetail.setOrder(order);
			orderDetail.setProduct(new Product(Integer.parseInt(productIds[i])));
			orderDetail.setProductCost(Float.parseFloat(productDetailCosts[i]));
			orderDetail.setSubtotal(Float.parseFloat(productSubtotals[i]));
			orderDetail.setShippingCost(Float.parseFloat(productShipCosts[i]));
			orderDetail.setQuantity(Integer.parseInt(quantities[i]));
			orderDetail.setUnitPrice(Float.parseFloat(productPrices[i]));
			
			orderDetails.add(orderDetail);
			
		}
		
	}
	
}
