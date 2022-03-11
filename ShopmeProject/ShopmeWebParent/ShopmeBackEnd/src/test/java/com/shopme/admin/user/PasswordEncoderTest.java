package com.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderTest {
	
	// 패스워드 암호화
	@Test
	public void testEncodePassword() {
		BCryptPasswordEncoder passwordEncoder=new BCryptPasswordEncoder();
		String rawPassword="1234";
		String encodedPassword=passwordEncoder.encode(rawPassword);
		
		System.out.println(encodedPassword);
		
		// 암호화된 패스워드와 기존 패스워드가 일치하는지 
		boolean matches=passwordEncoder.matches(rawPassword, encodedPassword);
		assertThat(matches).isTrue();
	}
}
