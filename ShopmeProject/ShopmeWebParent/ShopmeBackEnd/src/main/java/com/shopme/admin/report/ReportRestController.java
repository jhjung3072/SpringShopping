package com.shopme.admin.report;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReportRestController {
	@Autowired private MasterOrderReportService masterOrderReportService;
	@Autowired private OrderDetailReportService orderDetailReportService;
	
	// 통계 기간을 설정하는 버튼을 누르면 호출되는 함수 Get
	@GetMapping("/reports/sales_by_date/{period}")
	public List<ReportItem> getReportDataByDatePeriod(@PathVariable("period") String period) {
		System.out.println("Report period: " + period);
		
		switch (period) {
			case "last_7_days":
				return masterOrderReportService.getReportDataLast7Days(ReportType.DAY);
				
			case "last_28_days":
				return masterOrderReportService.getReportDataLast28Days(ReportType.DAY);

			case "last_6_months":
				return masterOrderReportService.getReportDataLast6Months(ReportType.MONTH);

			case "last_year":
				return masterOrderReportService.getReportDataLastYear(ReportType.MONTH);
				
			default:
				return masterOrderReportService.getReportDataLast7Days(ReportType.DAY);
		}
		
	}
	
	// 사용자가 시작 날짜와 마지막 날짜를 지정하는 함수
	@GetMapping("/reports/sales_by_date/{startDate}/{endDate}")
	public List<ReportItem> getReportDataByDatePeriod(@PathVariable("startDate") String startDate,
			@PathVariable("endDate") String endDate) throws ParseException {
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		Date startTime = dateFormatter.parse(startDate);
		Date endTime = dateFormatter.parse(endDate);
		
		return masterOrderReportService.getReportDataByDateRange(startTime, endTime,ReportType.DAY);
	}
	
	// 사용자가 지정한 기간의 카테고리, 상품 별 통계를 보여주는 함수
	@GetMapping("/reports/{groupBy}/{startDate}/{endDate}")
	public List<ReportItem> getReportDataByCategoryOrProductDateRange(@PathVariable("groupBy") String groupBy,
			@PathVariable("startDate") String startDate,
			@PathVariable("endDate") String endDate) throws ParseException {
		ReportType reportType=ReportType.valueOf(groupBy.toUpperCase());
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		Date startTime = dateFormatter.parse(startDate);
		Date endTime = dateFormatter.parse(endDate);
		
		return orderDetailReportService.getReportDataByDateRange(startTime, endTime,reportType);
	}
	
	//  카테고리, 상품 별 통계를 보여주는 함수
	@GetMapping("/reports/{groupBy}/{period}")
	public List<ReportItem> getReportDataByCategoryOrProduct(@PathVariable("groupBy") String groupBy,
				@PathVariable("period") String period){
		ReportType reportType=ReportType.valueOf(groupBy.toUpperCase());
		
		switch (period) {
		case "last_7_days":
			return orderDetailReportService.getReportDataLast7Days(reportType);
			
		case "last_28_days":
			return orderDetailReportService.getReportDataLast28Days(reportType);

		case "last_6_months":
			return orderDetailReportService.getReportDataLast6Months(reportType);

		case "last_year":
			return orderDetailReportService.getReportDataLastYear(reportType);
			
		default:
			return orderDetailReportService.getReportDataLast7Days(reportType);
	}
	}
}
