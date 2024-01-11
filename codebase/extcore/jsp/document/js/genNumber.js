const interalnumberId = "interalnumber";
const classType1Id = "classType1";
const classType2Id = "classType2";
const classType3Id = "classType3";
const preFixId = "preFix";
const suffixId = "suffix";
const modelId = "model";
const indexId = "index";
const requiredId = "required";
// 최초 화면 로드시 셀렉트 박스 변경
function toUI() {
	selectbox(classType1Id);
	selectbox(classType3Id);
	const numberTag = document.querySelector("#" + interalnumberId);
	const nameTag = document.querySelector("#" + preFixId);
	numberTag.setAttribute("readonly", "readonly");
	nameTag.setAttribute("readonly", "readonly");
	// 초기 사용 불가 상태
	$("#" + classType3Id).bindSelectDisabled(true);
	$("#" + modelId).bindSelectDisabled(true);
	productDisable();
}

function requiredAdd() {
	const primary = document.getElementById(requiredId);
	primary.classList.add("req"); // 클래스를 제거
}

function requiredRemove() {
	const primary = document.getElementById(requiredId);
	primary.classList.remove("req"); // 클래스를 제거
}

function first(obj) {
	const classType1 = obj.value;
	clearValue();
	if (classType1 !== "") {
		let checker = autoNumberChecker(classType1);
		// 채번 대상이다
		if (!checker) {
			if ("DEV" === classType1) {
				classType2(classType1);
				requiredAdd();
			} else if ("INSTRUCTION" === classType1) {
				setFirstNumber(classType1)
				classType2(classType1);
				requiredAdd();
			} else if ("REPORT" === classType1) {
				classType2(classType1);
				modelEnable();
				requiredRemove();
			} else if ("VALIDATION" === classType1) {
				setFirstNumber(classType1)
				classType2(classType1);
				modelEnable();
				requiredRemove();
			} else if ("MEETING" === classType1) {
				classType2(classType1);
				modelEnable();
				requiredRemove();
			}
		} else {
			// 채번 대상이 아닐경우
			modelEnable();
			removeReadOnly();
			requiredRemove();
		}
	}
}

// 번호 및 제목 태그 활성화
function removeReadOnly() {
	const numberTag = document.querySelector("#" + interalnumberId);
	const nameTag = document.querySelector("#" + suffixId);
	numberTag.value = "";
	numberTag.removeAttribute("readonly");
	nameTag.value = "";
	nameTag.removeAttribute("readonly");
	nameTag.focus();
}

// 클래스 2타입 가여조기
// 중분류 세팅
function classType2(classType1) {
	const classType2Tag = document.getElementById(classType2Id);
	classType2Tag.removeAttribute("readonly");
	axdom("#classType2").bindSelector({
		reserveKeys: {
			options: "list",
			optionValue: "oid",
			optionText: "name"
		},
		optionPrintLength: "all",
		onsearch: function(id, obj, callBack) {
			const value = document.getElementById(id).value;
			const params = new Object();
			const url = getCallUrl("/class/finder");
			params.value = value;
			params.classType = classType1;
			call(url, params, function(data) {
				callBack({
					options: data.list
				})
			})
		},
		onchange: function() {
			removeClazz();
			const el = document.getElementById("classType2");
			const clazzEl = document.createElement("input");
			clazzEl.setAttribute("data-oid", this.selectedOption.oid);
			clazzEl.type = "hidden";
			clazzEl.name = "clazz";
			clazzEl.id = "clazz";
			clazzEl.value = this.selectedOption.clazz;
			el.parentNode.insertBefore(clazzEl, el.nextSibling);
			second();
		},
	})
}

// 중분류 변경
function second() {
	const value = document.getElementById("clazz").getAttribute("data-oid");
	const clazz = document.getElementById("clazz").value;
	const classType1 = document.getElementById(classType1Id).value;
	const classType2 = document.getElementById(classType2Id).value;
	const tag = document.querySelector("#" + interalnumberId);
	const nameTag = document.querySelector("#" + preFixId);
	const modelreq = document.getElementById("modelreq");
	if (value !== "") {
		if ("INSTRUCTION" == classType1) {
			nameTag.value = "";
			tag.value = "";
			setFirstNumber("INSTRUCTION");
			tag.value += clazz + "-";
			modelEnable();
			modelreq.classList.add("req");
		} else if ("DEV" === classType1) {
			nameTag.value = "";
			tag.value = "";
			tag.value += clazz + "-";
			if (clazz === "DMR") {
				productEnable();
			} else {
				productDisable();
			}
			modelEnable();
			modelreq.classList.add("req");
		} else if ("REPORT" === classType1) {
			nameTag.value = "";
			tag.value = "";
			const currentDate = new Date();
			const year = currentDate.getFullYear() % 100; // 연도의 뒤 2자리
			const month = (currentDate.getMonth() + 1).toString().padStart(2, '0'); // 월을 2자리로 표현
			loadForm(clazz);
			tag.value += clazz + "-" + year + month + "-";
			classType3(classType1, value);
		} else if ("VALIDATION" === classType1) {
			nameTag.value = "";
			tag.value = "";
			setFirstNumber("VALIDATION");
			classType3(classType1, value);
		} else if ("MEETING" == classType1) {
			tag.value = "";
			tag.value += clazz + "-";
			suffixEnable();
			if (clazz !== "회의록") {
				nameTag.value = classType2 + "_"
			}
			loadForm(clazz);
			lastNumber(tag.value, classType1);
		}
	}
}

// 소분류 변경
function last() {
	const selectElement = document.getElementById(classType3Id);
	const selectedIndex = selectElement.selectedIndex;
	const value = selectElement.value;
	const clazz = selectElement.options[selectedIndex].getAttribute("data-clazz");
	const classType1 = document.getElementById(classType1Id).value;
	const numberTag = document.querySelector("#" + interalnumberId);
	const preFixTag = document.querySelector("#" + preFixId);
	if (value !== "") {
		numberTag.value = "";
		const classType2 = document.getElementById(classType2Id);
		const text = classType2.value;
		const clazz2 = document.getElementById("clazz").value;
		if ("REPORT" === classType1) {
			const currentDate = new Date();
			const year = currentDate.getFullYear() % 100; // 연도의 뒤 2자리
			const month = (currentDate.getMonth() + 1).toString().padStart(2, '0'); // 월을 2자리로 표현
			numberTag.value += clazz2 + "-" + year + month + "-" + clazz + "-";
			suffixEnable();
			preFixTag.value = text + "_"
			lastNumber(numberTag.value, classType1);
		} else if ("VALIDATION" === classType1) {
			setFirstNumber("VALIDATION");
			numberTag.value += clazz + "-";
			suffixEnable();
			preFixTag.value = text + "_"
			lastNumber(numberTag.value, classType1);
		}
	}
}

// 프로젝트 코드 변경
function preNumberCheck(obj) {
	const tag = document.querySelector("#" + interalnumberId);
	const value = obj.value;
	const preFixTag = document.querySelector("#" + preFixId);
	if (value !== "") {
		const classType1 = document.getElementById(classType1Id);
		const classType2 = document.getElementById(classType2Id);
		const text = classType2.value;
		const clazz2 = document.getElementById("clazz").value;
		if (classType1.value === "DEV") {
			tag.value = "";
			tag.value += clazz2 + "-" + value + "-";
			suffixEnable();
			preFixTag.value = text + "_" + value;
			lastNumber(tag.value, classType1.value);
		} else if (classType1.value === "INSTRUCTION") {
			tag.value = "";
			setFirstNumber("INSTRUCTION");
			tag.value += clazz2 + "-" + value + "-WI-";
			//			tag.value += value + "-WI-";
			suffixEnable();
			preFixTag.value = value + "_";
			lastNumber(tag.value, classType1.value);
		}
	}
}

// 소분류 세팅
function classType3(classType1, classType2) {
	const url = getCallUrl("/class/classType3?classType1=" + classType1 + "&classType2=" + classType2);
	call(url, null, function(data) {
		const classType3 = data.classType3;
		if (data.result) {
			document.querySelector("#classType3 option").remove();
			document.querySelector("#classType3").innerHTML = "<option value=\"\">선택</option>";
			for (let i = 0; i < classType3.length; i++) {
				const value = classType3[i].value;
				const clazz = classType3[i].clazz;
				const tag = "<option data-clazz=\"" + clazz + "\" value=\"" + value + "\">" + classType3[i].name + "</option>";
				document.querySelector("#classType3").innerHTML += tag;
			}
		}
	}, "GET", false);
	selectbox(classType3Id);
}


// 문서명 활성화
function suffixEnable() {
	const suffixTag = document.querySelector("#" + suffixId);
	suffixTag.value = "";
	suffixTag.removeAttribute("readonly");
}

// 프로젝트 코드 활성화
function modelEnable() {
	$("#" + modelId).bindSelectSetValue("");
	$("#" + modelId).bindSelectDisabled(false);
}

// 값 초기화 + 읽기전용처리
function clearValue() {
	const modelreq = document.getElementById("modelreq");
	const numberTag = document.querySelector("#" + interalnumberId);
	const preFixTag = document.querySelector("#" + preFixId);
	const suffixTag = document.querySelector("#" + suffixId);
	modelreq.classList.remove("req");
	numberTag.value = "";
	preFixTag.value = "";
	suffixTag.value = "";
	numberTag.setAttribute("readonly", "readonly");
	preFixTag.setAttribute("readonly", "readonly");
	suffixTag.setAttribute("readonly", "readonly");
	$("#" + classType3Id).bindSelectSetValue("");
	$("#" + modelId).bindSelectSetValue("");
	$("#" + classType3Id).bindSelectDisabled(true);
	$("#" + modelId).bindSelectDisabled(true);
	DEXT5.setBodyValue("", "content");

	const classType2Tag = document.getElementById(classType2Id);
	classType2Tag.setAttribute("readonly", "readonly");
	classType2Tag.value = "";
	removeClazz();
}


function removeClazz() {
	const el = document.getElementById("clazz");
	if (el) {
		el.parentNode.removeChild(el);
	}
}


// 시퀀시 따오기
function lastNumber(value, classType1) {
	const tag = document.querySelector("#" + interalnumberId);
	const url = getCallUrl("/doc/lastNumber?number=" + value + "&classType1=" + classType1);
	call(url, null, function(data) {
		if (data.result) {
			tag.value = "";
			tag.value = data.lastNumber;
		}
	}, "GET");
	const suffixTag = document.querySelector("#" + suffixId);
	suffixTag.focus();
}

// 채번할지 말지 여부 체크
function autoNumberChecker(classType1) {
	if ("RND" === classType1 || "ELEC" === classType1 || "SOURCE" === classType1 || "APPROVAL" === classType1 || "ETC" === classType1) {
		$("#" + classType3Id).bindSelectDisabled(true);
		return true;
	} else {
		$("#" + classType3Id).bindSelectDisabled(true);
		return false;
	}
}

// 기본 동일하게 적용되는 룰
function setFirstNumber(classType1) {
	if (classType1 === "INSTRUCTION" || classType1 === "REPORT" || classType1 === "VALIDATION") {
		const tag = document.querySelector("#" + interalnumberId);
		const selectElement = document.getElementById(classType1Id);
		const selectedIndex = selectElement.selectedIndex;
		const value = selectElement.options[selectedIndex].getAttribute("data-clazz");
		tag.value = value;
	}
}

// DEXT5 양식
function loadForm(classType2) {
	const url = getCallUrl("/form/getHtml?clazz=" + classType2);
	parent.openLayer();
	call(url, null, function(data) {
		if (data.result) {
			DEXT5.setBodyValue(data.html, "content");
		} else {
			alert(data.msg);
		}
		parent.closeLayer();
	}, "GET");
}

// 중분류 값 삭제
function clearSecond() {
	const classType1 = document.getElementById("classType1").value;
	document.getElementById("classType2").value = "";
	removeClazz();
	clearValue();
	productDisable();
	classType2(classType1);
}


// 제품명 코드 활성화
function productEnable() {
	const product = document.getElementById("product");
	const preq = document.getElementById("preq");
	product.removeAttribute("readonly");
	preq.classList.add("req"); // 클래스를 제거
}

// 제품명 코드 비활성화
function productDisable() {
	const product = document.getElementById("product");
	const preq = document.getElementById("preq");
	product.setAttribute("readonly", "readonly");
	preq.classList.remove("req"); // 클래스를 제거
}