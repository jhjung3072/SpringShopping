package com.shopme.admin.report;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.admin.order.OrderRepository;
import com.shopme.common.entity.order.Order;

@Service
public class MasterOrderReportService extends AbstractReportService{
	@Autowired private OrderRepository repo;
	

	
	protected List<ReportItem> getReportDataByDateRangeInternal(Date startTime, Date endTime, ReportType reportType) {
		// 기간 내에 주문목록 리스트를 불러옴
		List<Order> listOrders = repo.findByOrderTimeBetween(startTime, endTime);
		//콘솔에 주문정보 출력
		printRawData(listOrders);
		// 통계 시작 및 종료 날짜, 통계 타입을 입력받아 통계를 생성
		List<ReportItem> listReportItems = createReportData(startTime, endTime, reportType);
		
		System.out.println();
		// 통계에서 매출과 순이익, 주문횟수를 계산
		calculateSalesForReportData(listOrders, listReportItems);
		// 콘솔에 통계 데이터 출력
		printReportData(listReportItems);
		
		return listReportItems;
	}

	// 통계에서 매출과 순이익, 주문횟수를 계산하기 위한 함수
	private void calculateSalesForReportData(List<Order> listOrders, List<ReportItem> listReportItems) {
		for (Order order : listOrders) {
			String orderDateString = dateFormatter.format(order.getOrderTime());
			
			ReportItem reportItem = new ReportItem(orderDateString);
			
			int itemIndex = listReportItems.indexOf(reportItem);
			
			if (itemIndex >= 0) {
				reportItem = listReportItems.get(itemIndex);
				
				reportItem.addGrossSales(order.getTotal());
				reportItem.addNetSales(order.getSubtotal() - order.getProductCost());
				reportItem.increaseOrdersCount();
			}
		}
	}
	
	// 콘솔에서 통계의 매출과 순이익, 주문횟수를 출력
	private void printReportData(List<ReportItem> listReportItems) {
		listReportItems.forEach(item -> {
			System.out.printf("%s, %10.2f, %10.2f, %d \n", item.getIdentifier(), item.getGrossSales(),
					item.getNetSales(), item.getOrdersCount());
		});
		
	}

	// 통계 시작 및 종료 날짜, 통계 타입을 입력받아 통계를 생성하는 함수
	private List<ReportItem> createReportData(Date startTime, Date endTime, ReportType reportType) {
		List<ReportItem> listReportItems = new ArrayList<>();
		
		// 통계 시작 날짜 설정
		Calendar startDate = Calendar.getInstance();
		startDate.setTime(startTime);
		
		// 통계 종료 날짜 설정
		Calendar endDate = Calendar.getInstance();
		endDate.setTime(endTime);	
		
		Date currentDate = startDate.getTime();
		String dateString = dateFormatter.format(currentDate);
		
		listReportItems.add(new ReportItem(dateString));
		
		do {
			// 통계가 Day타입이면 시작 날짜를 기준으로 1일씩 add
			if (reportType.equals(ReportType.DAY)) {
				startDate.add(Calendar.DAY_OF_MONTH, 1);
			} else if (reportType.equals(ReportType.MONTH)) {//통계가 Month 타입이면 시작 날짜를 기준을 한달씩 add
				startDate.add(Calendar.MONTH, 1);
			}
			
			currentDate = startDate.getTime();
			dateString = dateFormatter.format(currentDate);	
			
			listReportItems.add(new ReportItem(dateString));
			
		} while (startDate.before(endDate)); // 시작날짜부터 종료날짜까지 반복
		
		return listReportItems;		
	}

	// 콘솔에 주문 시각 및 상품 정보 가격 출력
	private void printRawData(List<Order> listOrders) {
		listOrders.forEach(order -> {
			System.out.printf("%-3d | %s | %10.2f | %10.2f \n",
					order.getId(), order.getOrderTime(), order.getTotal(), order.getProductCost());
		});
	}

	
}
