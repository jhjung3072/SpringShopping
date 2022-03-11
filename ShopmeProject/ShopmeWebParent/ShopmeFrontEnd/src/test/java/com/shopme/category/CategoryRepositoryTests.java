package com.shopme.category;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.shopme.common.entity.Category;

@DataJpaTest
@AutoConfigureTestDatabase(replace=Replace.NONE)
public class CategoryRepositoryTests {
	
	@Autowired
	private CategoryRepository repo;
	
	// 활성화된 카테고리 리턴 by 줄임말
	@Test
	public void testFindCategoryByAlias() {
		String alias="전자기기";
		Category category=repo.findByAliasEnabled(alias);
		
		assertThat(category).isNotNull();
	}
}
