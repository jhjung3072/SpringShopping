package com.shopme.admin.brand;

// 브랜드별 카테고리 목록을 보여줄 떄 사용
public class CategoryDTO {
	private Integer id;
	private String name;

	public CategoryDTO() {
	}

	public CategoryDTO(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
