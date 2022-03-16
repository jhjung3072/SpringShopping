$(document).ready(function() {
	$("#buttonPostQuestion").on("click", function(e) {
		e.preventDefault();
		form = $("#formQuestion")[0];
		if (form.checkValidity()) {
			postQuestion();
		} else {
			form.reportValidity();
		}		
	});		
});

function postQuestion() {
	url = contextPath + "post_question/" + productId;
	question = $("#question").val();
	
	jsonData = {questionContent: question};
	
	$.ajax({
		type: 'POST',
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		},
		data: JSON.stringify(jsonData),
		contentType: 'application/json'
	}).done(function(response) {
		$("#modalDialog").on("hide.bs.modal", function(e) {
			$("#question").val("");	
		});
		
		showModalDialog("질문 등록", "회원님이 작성하신 질문은 관리자의 승인 후 등록됩니다.");
	}).fail(function() {
		showErrorModal("질문 등록에 실패했습니다... 다시 로그인해주세요");
	});			
}