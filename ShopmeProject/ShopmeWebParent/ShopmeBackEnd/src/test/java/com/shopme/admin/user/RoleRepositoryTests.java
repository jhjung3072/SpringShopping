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
	
	@Test
	public void testCreateFirstRole() {
		Role roleAdmin=new Role("Admin","manage everything");
		Role savedRole= repo.save(roleAdmin);
		assertThat(savedRole.getId()).isGreaterThan(0);
	}

	@Test
	public void testCreateRestRole() {
		Role roleSaleperson=new Role("Sales","manage product price,customers,shipping");
		Role roleEditor=new Role("Editor","manage product price,customers,shipping");
		Role roleShipper=new Role("Shipper","manage product price,customers,shipping");
		Role roleAssistant=new Role("Assistant","manage product price,customers,shipping");

		repo.saveAll(List.of(roleSaleperson,roleEditor,roleShipper,roleAssistant));
		
	}
}
