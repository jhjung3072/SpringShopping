package com.shopme.admin.setting;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Currency;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CurrencyRepositoryTests {

	@Autowired
	private CurrencyRepository repo;
	
	// 환율 생성
	@Test
	public void testCreateCurrencies() {
		List<Currency> listCurrencies = Arrays.asList(
			new Currency("미국 달러", "$", "USD"),
			new Currency("영국 파운드", "£", "GPB"),
			new Currency("일본 엔", "¥", "JPY"),
			new Currency("유로", "€", "EUR"),
			new Currency("러시아 루블", "₽", "RUB"),
			new Currency("대한민국 원", "₩", "KRW"),
			new Currency("중국 위안", "¥", "CNY"),
			new Currency("브라질 헤알", "R$", "BRL"),
			new Currency("호주 달러", "$", "AUD"),
			new Currency("캐나다 달러", "$", "CAD"),
			new Currency("베트남 동 ", "₫", "VND"),
			new Currency("인도 루피", "₹", "INR")
		);
		
		repo.saveAll(listCurrencies);
		
		Iterable<Currency> iterable = repo.findAll();
		
		assertThat(iterable).size().isEqualTo(12);
	}
	
	// 환율 리스트 오름차순
	@Test
	public void testListAllOrderByNameAsc() {
		List<Currency> currencies = repo.findAllByOrderByNameAsc();
		
		currencies.forEach(System.out::println);
		
		assertThat(currencies.size()).isGreaterThan(0);
	}
}
