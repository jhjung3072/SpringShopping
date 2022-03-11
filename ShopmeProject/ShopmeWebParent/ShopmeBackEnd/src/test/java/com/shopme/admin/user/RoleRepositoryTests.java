package com.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Role;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class RoleRepositoryTests {
	
	@Autowired
	private RoleRepository repo;
	
	// 운영자 생성
	@Test
	public void testCreateFirstRole() {
		Role roleAdmin=new Role("운영자","모든 권한");
		Role savedRole= repo.save(roleAdmin);
		assertThat(savedRole.getId()).isGreaterThan(0);
	}

	// 직원 역할 생성
	@Test
	public void testCreateRestRole() {
		Role roleSaleperson=new Role("판매관리자","상품 가격, 회원, 배송, 주문 및 판매 통계");
		Role roleEditor=new Role("편집자","카테고리,브랜드,상품,기사 및 메뉴");
		Role roleShipper=new Role("배송관리자","주문 내역 및 배송 추적 상태 변경");
		Role roleAssistant=new Role("Q/A담당자","질문과 답변, 리뷰관리");

		repo.saveAll(List.of(roleSaleperson,roleEditor,roleShipper,roleAssistant));
		
	}
}
