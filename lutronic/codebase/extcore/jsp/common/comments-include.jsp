<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!-- 모달 등록 -->
<div class="modal fade" id="reply">
	<input type="hidden" name="roid" id="roid">
	<input type="hidden" name="depth" id="depth">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<h5 class="modal-title">
					<b>답글 등록</b>
				</h5>
			</div>
			<div class="modal-body" style="width: 100%; margin: 0 auto;">
				<textarea rows="10" name="data" style="width: 95%; box-sizing: border-box;"></textarea>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-success" onclick="_reply();">
					<b>등록</b>
				</button>
				<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
					<b>닫기</b>
				</button>
			</div>
		</div>
	</div>
</div>

<!-- 모달 수정 -->
<div class="modal fade" id="modify">
	<input type="hidden" name="moid" id="moid">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<h5 class="modal-title">
					<b>댓글 수정</b>
				</h5>
			</div>
			<div class="modal-body" style="width: 100%; margin: 0 auto;">
				<textarea rows="10" name="data" style="width: 95%; box-sizing: border-box;"></textarea>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-success" onclick="modify();">
					<b>수정</b>
				</button>
				<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
					<b>닫기</b>
				</button>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript">
	function _write(depth) {
		const oid = document.getElementById("oid").value;
		const comments = document.getElementById("comments");
		if (comments.value === "") {
			alert("댓글을 입력하세요.");
			comments.focus();
			return false;
		}
		if (!confirm("록 하시겠습니까?")) {
			return false;
		}
		const params = {
			comment : comments.value,
			oid : oid,
			depth : Number(depth)
		};
		const url = getCallUrl("/comments/create");
		openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				document.location.reload();
			}
			closeLayer();
		})
	}

	//답글 모달에 데이터 보냄
	function sendReply(oid, depth) {
		document.getElementById("roid").value = oid;
		document.getElementById("depth").value = Number(depth) + 1;
	}

	//수정 모달에 데이터 보냄
	function sendUpdate(oid, data) {
		document.getElementById("moid").value = oid;
		document.getElementsByName("data")[1].value = data;
	}

	// 답글달기
	function _reply() {
		const oid = document.getElementById("roid").value;
		const comment = document.getElementsByName("data")[0];
		const depth = document.getElementById("depth").value;
		if (comment.value === "") {
			alert("답글 내용을 입력하세요.");
			comment.focus();
			return false;
		}
		const params = {
			oid : oid,
			comment : comment.value,
			depth : depth
		}
		logger(params);
		if (!confirm("등록하시겠습니까?")) {
			return false;
		}

		openLayer();
		const url = getCallUrl("/comments/reply");
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				document.location.reload();
			}
			closeLayer();
		})
	}

	function modify() {
		const oid = document.getElementById("moid").value;
		const comment = document.getElementsByName("data")[1];
		if (comment.value === "") {
			alert("수정 내용을 입력하세요.");
			comment.focus();
			return false;
		}

		if (!confirm("수정하시겠습니까?")) {
			return false;
		}
		const params = {
			oid : oid,
			comment : comment.value
		}
		openLayer();
		const url = getCallUrl("/comments/modify");
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				document.location.reload();
			}
			closeLayer();
		})
	}

	//댓글 삭제
	function cmdel(oid) {
		if (!confirm("삭제 하시겠습니까?")) {
			return false;
		}
		const url = getCallUrl("/comments/delete?oid=" + oid);
		openLayer();
		call(url, null, function(data) {
			alert(data.msg);
			if (data.result) {
				document.location.reload();
			}
			closeLayer();
		}, "GET");
	}
</script>