package com.shopme.admin.setting.country;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.common.entity.Country;

@RestController
public class CountryRestController {

	@Autowired private CountryRepository repo;
	
	// 국가 목록 리스트 GET
	@GetMapping("/countries/list")
	public List<Country> listAll() {
		return repo.findAllByOrderByNameAsc();
	}
	
	// 국가 리스트 저장 POST
	@PostMapping("/countries/save")
	public String save(@RequestBody Country country) {
		Country savedCountry = repo.save(country);
		return String.valueOf(savedCountry.getId());
	}
	
	// 국가 리스트 삭제 DELETE
	@DeleteMapping("/countries/delete/{id}")
	public void delete(@PathVariable("id") Integer id) {
		repo.deleteById(id);
	}
}
