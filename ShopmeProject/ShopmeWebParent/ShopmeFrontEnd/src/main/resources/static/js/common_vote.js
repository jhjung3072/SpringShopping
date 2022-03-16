function doVote(currentLink, entityName) {
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
				updateVoteCountAndIcons(currentLink, voteResult, entityName);
			});
		}
		
		showModalDialog("추천 ", voteResult.message);
		
	}).fail(function() {
		showErrorModal("추천 에러 " + entityName);
	});	
}

function updateVoteCountAndIcons(currentLink, voteResult, entityName) {
	itemId = currentLink.attr(entityName + "Id");
	voteUpLink = $("#linkVoteUp-" + entityName + "-" + itemId);
	voteDownLink = $("#linkVoteDown-" + entityName + "-" + itemId);
	
	$("#voteCount-" + entityName + "-" + itemId).text("추천 " +voteResult.voteCount + "개");
	
	message = voteResult.message;
	
	if (message.includes("좋아요를 눌렀습니다.")) {
		highlightVoteUpIcon(currentLink, voteDownLink, entityName);
	} else if (message.includes("싫어요를 눌렀습니다.")) {
		highlightVoteDownIcon(currentLink, voteUpLink, entityName);
	} else if (message.includes("싫어요를 취소했습니다.")) {
		unhighlightVoteDownIcon(voteDownLink, entityName);
	} else if (message.includes("좋아요를 취소했습니다.")) {
		unhighlightVoteDownIcon(voteUpLink, entityName);
	}
}

function highlightVoteUpIcon(voteUpLink, voteDownLink, entityName) {
	voteUpLink.removeClass("far").addClass("fas");
	voteUpLink.attr("title", "Undo vote up this " + entityName);
	voteDownLink.removeClass("fas").addClass("far");
}

function highlightVoteDownIcon(voteDownLink, voteUpLink, entityName) {
	voteDownLink.removeClass("far").addClass("fas");
	voteDownLink.attr("title", "Undo vote down this" + entityName);
	voteUpLink.removeClass("fas").addClass("far");
}

function unhighlightVoteDownIcon(voteDownLink, entityName) {
	voteDownLink.attr("title", "Vote down this " + entityName);
	voteDownLink.removeClass("fas").addClass("far");	
}

function unhighlightVoteUpIcon(voteUpLink, entityName) {
	voteUpLink.attr("title", "Vote up this " + entityName);
	voteUpLink.removeClass("fas").addClass("far");	
}