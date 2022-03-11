$(document).ready(function() {
	$(".linkVoteReview").on("click", function(e) {
		e.preventDefault();
		voteReview($(this));
	});
});

function voteReview(currentLink) {
	requestURL = currentLink.attr("href");

	$.ajax({
		type: "POST",
		url: requestURL,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
	}).done(function(voteResult) {
		console.log(voteResult);
		
		if (voteResult.successful) {
			$("#modalDialog").on("hide.bs.modal", function(e) {
				updateVoteCountAndIcons(currentLink, voteResult);
			});
		}
		
		showModalDialog("리뷰 추천", voteResult.message);
		
	}).fail(function() {
		showErrorModal("리뷰 추천 에러");
	});	
}

function updateVoteCountAndIcons(currentLink, voteResult) {
	reviewId = currentLink.attr("reviewId");
	voteUpLink = $("#linkVoteUp-" + reviewId);
	voteDownLink = $("#linkVoteDown-" + reviewId);
	
	$("#voteCount-" + reviewId).text("추천 " +voteResult.voteCount + "개");
	
	message = voteResult.message;
	
	if (message.includes("successfully voted up")) {
		highlightVoteUpIcon(currentLink, voteDownLink);
	} else if (message.includes("successfully voted down")) {
		highlightVoteDownIcon(currentLink, voteUpLink);
	} else if (message.includes("unvoted down")) {
		unhighlightVoteDownIcon(voteDownLink);
	} else if (message.includes("unvoted up")) {
		unhighlightVoteDownIcon(voteUpLink);
	}
}

function highlightVoteUpIcon(voteUpLink, voteDownLink) {
	voteUpLink.removeClass("far").addClass("fas");
	voteUpLink.attr("title", "이 리뷰의 추천을 취소했습니다.");
	voteDownLink.removeClass("fas").addClass("far");
}

function highlightVoteDownIcon(voteDownLink, voteUpLink) {
	voteDownLink.removeClass("far").addClass("fas");
	voteDownLink.attr("title", "이 리뷰의 비추천을 취소했습니다.");
	voteUpLink.removeClass("fas").addClass("far");
}

function unhighlightVoteDownIcon(voteDownLink) {
	voteDownLink.attr("title", "이 리뷰를 비추천했습니다.");
	voteDownLink.removeClass("fas").addClass("far");	
}

function unhighlightVoteUpIcon(voteUpLink) {
	voteUpLink.attr("title", "이 리뷰를 추천했습니다.");
	voteUpLink.removeClass("fas").addClass("far");	
}