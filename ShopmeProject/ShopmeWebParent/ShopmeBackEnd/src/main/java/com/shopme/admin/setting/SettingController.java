package com.shopme.admin.setting;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.AmazonS3Util;
import com.shopme.admin.FileUploadUtil;
import com.shopme.common.Constants;
import com.shopme.common.entity.Currency;
import com.shopme.common.entity.setting.Setting;

@Controller
public class SettingController {

	@Autowired private SettingService service;
	
	@Autowired private CurrencyRepository currencyRepo;
	
	// 설정 목록 리스트 GET
	@GetMapping("/settings")
	public String listAll(Model model) {
		List<Setting> listSettings = service.listAllSettings(); // 설정 목록
		List<Currency> listCurrencies = currencyRepo.findAllByOrderByNameAsc(); // 환율 목록
		
		model.addAttribute("listCurrencies", listCurrencies);
		
		for (Setting setting : listSettings) {
			model.addAttribute(setting.getKey(), setting.getValue());
		}
		
		model.addAttribute("S3_BASE_URL", Constants.S3_BASE_URI); // uri 설정
		
		return "settings/settings";
	}
	
	// 일반 설정 POST
	@PostMapping("/settings/save_general")
	public String saveGeneralSettings(@RequestParam("fileImage") MultipartFile multipartFile,
			HttpServletRequest request, RedirectAttributes ra) throws IOException {
		GeneralSettingBag settingBag = service.getGeneralSettings();
		
		saveSiteLogo(multipartFile, settingBag); // 사이트 로고
		saveCurrencySymbol(request, settingBag); // 환율 표시
		
		updateSettingValuesFromForm(request, settingBag.list());
		
		ra.addFlashAttribute("message", "일반 설정이 저장되었습니다.");
		
		return "redirect:/settings";
	}

	// 사이트 로고 저장
	private void saveSiteLogo(MultipartFile multipartFile, GeneralSettingBag settingBag) throws IOException {
		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			String value = "/site-logo/" + fileName;
			settingBag.updateSiteLogo(value);
			
			String uploadDir = "site-logo";
			AmazonS3Util.removeFolder(uploadDir); // 기존의 폴더 제거 후 재업로드
			AmazonS3Util.uploadFile(uploadDir, fileName, multipartFile.getInputStream());
		}
	}
	
	// 환율 표시 저장
	private void saveCurrencySymbol(HttpServletRequest request, GeneralSettingBag settingBag) {
		Integer currencyId = Integer.parseInt(request.getParameter("CURRENCY_ID"));
		Optional<Currency> findByIdResult = currencyRepo.findById(currencyId);
		
		if (findByIdResult.isPresent()) {
			Currency currency = findByIdResult.get();
			settingBag.updateCurrencySymbol(currency.getSymbol());
		}
	}
	
	// 설정 필드 값 저장
	private void updateSettingValuesFromForm(HttpServletRequest request, List<Setting> listSettings) {
		for (Setting setting : listSettings) {
			String value = request.getParameter(setting.getKey());
			if (value != null) {
				setting.setValue(value);
			}
		}
		
		service.saveAll(listSettings);
	}
	
	// 메일 서버 저장 POST
	@PostMapping("/settings/save_mail_server")
	public String saveMailServerSetttings(HttpServletRequest request, RedirectAttributes ra) {
		List<Setting> mailServerSettings = service.getMailServerSettings();
		updateSettingValuesFromForm(request, mailServerSettings);
		
		ra.addFlashAttribute("message", "메일서버 설정이 저장되었습니다.");
		
		return "redirect:/settings#mailServer";
	}
	
	// 메일 양식 저장 POST
	@PostMapping("/settings/save_mail_templates")
	public String saveMailTemplateSetttings(HttpServletRequest request, RedirectAttributes ra) {
		List<Setting> mailTemplateSettings = service.getMailTemplateSettings();
		updateSettingValuesFromForm(request, mailTemplateSettings);
		
		ra.addFlashAttribute("message", "메일양식이 저장되었습니다. ");
		
		return "redirect:/settings#mailTemplates";
	}
	
	// 결제 API 저장
	@PostMapping("/settings/save_payment")
	public String savePaymentSetttings(HttpServletRequest request, RedirectAttributes ra) {
		List<Setting> paymentSettings = service.getPaymentSettings();
		updateSettingValuesFromForm(request, paymentSettings);
		
		ra.addFlashAttribute("message", "결제API가 저장되었습니다.");
		
		return "redirect:/settings#payment";
	}		
}
