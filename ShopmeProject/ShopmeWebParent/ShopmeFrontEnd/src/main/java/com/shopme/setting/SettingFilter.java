package com.shopme.setting;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.shopme.common.Constants;
import com.shopme.common.entity.setting.Setting;

//설정 정보, S3_BASE_URI 중복 코드 처리
// 설정 정보 콘솔에 출력
//controller 마다 중복코드를 피하고자, SettingFilter에서 처리
@Component
public class SettingFilter implements Filter {

	@Autowired
	private SettingService service; 
	
	// 요청/응답 쌍이 체인을 통해 전달될 때마다 컨테이너에 의해 호출.
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest servletRequest = (HttpServletRequest) request;
		String url = servletRequest.getRequestURL().toString();
		
		// static 요청일 경우 return
		if (url.endsWith(".css") || url.endsWith(".js") || url.endsWith(".png") ||
				url.endsWith(".jpg")) {
			chain.doFilter(request, response);
			return;
		}
		
		List<Setting> generalSettings = service.getGeneralSettings();

		// 설정 정보 setAttribute
		generalSettings.forEach(setting -> {
			request.setAttribute(setting.getKey(), setting.getValue());
			System.out.println(setting.getKey() + " == > " + setting.getValue());
		});
		
		request.setAttribute("S3_BASE_URI", Constants.S3_BASE_URI);
		chain.doFilter(request, response);

	}

}
