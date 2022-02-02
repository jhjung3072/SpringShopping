dropdownBrands = $("#brand");
dropdownCategories = $("#category");

$(document).ready(function() {
	
	$("#shortDescription").richText();
	$("#fullDescription").richText();
	
	dropdownBrands.change(function() {
		dropdownCategories.empty();
		getCategories();
	});	
	
	getCategoriesForNewForm();

});

function getCategoriesForNewForm() {
	catIdField = $("#categoryId");
	editMode = false;
	
	if (catIdField.length) {
		editMode = true;
	}
	
	if (!editMode) getCategories();
}

function getCategories() {
	brandId = dropdownBrands.val(); 
	url = brandModuleURL + "/" + brandId + "/categories";
	
	$.get(url, function(responseJson) {
		$.each(responseJson, function(index, category) {
			$("<option>").val(category.id).text(category.name).appendTo(dropdownCategories);
		});			
	});
}

function checkUnique(form) {
	productId = $("#id").val();
	productName = $("#name").val();
	
	csrfValue = $("input[name='_csrf']").val();
	
	params = {id: productId, name: productName, _csrf: csrfValue};
	
	$.post(checkUniqueUrl, params, function(response) {
		if (response == "OK") {
			form.submit();
		} else if (response == "Duplicate") {
			showWarningModal(productName+"해당 이름의 상품이 이미 존재합니다");	
		} else {
			showErrorModal("알 수 없는 서버응답입니다.");
		}
		
	}).fail(function() {
		showErrorModal("서버와 연결할 수 없습니다.");
	});
	
	return false;
}	