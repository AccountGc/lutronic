const interalnumberId = "interalnumber";
const classType1Id = "classType1";
const classType2Id = "classType2";
const classType3Id = "classType3";
const nameId = "name";
const modelId = "model";
const indexId = "index";
// 최초 화면 로드시 셀렉트 박스 변경
function toUI() {
	selectbox(classType1Id);
	selectbox(classType2Id);
	selectbox(classType3Id);

	const numberTag = document.querySelector("#" + interalnumberId);
	const nameTag = document.querySelector("#" + nameId);
	numberTag.setAttribute("readonly", "readonly");
	nameTag.setAttribute("readonly", "readonly");
	// 초기 사용 불가 상태
	$("#" + classType2Id).bindSelectDisabled(true);
	$("#" + classType3Id).bindSelectDisabled(true);
	$("#" + nameId).bindSelectDisabled(true);
	$("#" + modelId).bindSelectDisabled(true);
}

function first(obj) {
	const classType1 = obj.value;
	clearValue();
	if (classType1 !== "") {
		const index = document.getElementById(indexId);
		const selectElement = document.getElementById(classType1Id);
		const selectedIndex = selectElement.selectedIndex;
		const text = selectElement.options[selectedIndex].text;
		//		index.value = text.length + 1;
		let checker = autoNumberChecker(classType1);
		// 채번 대상이다
		if (!checker) {

			if ("DEV" === classType1) {
				classType2(classType1);
			} else if ("INSTRUCTION" === classType1) {
				setFirstNumber(classType1)
				classType2(classType1);
			} else if ("REPORT" === classType1) {
				classType2(classType1);
				modelEnable();
			} else if ("VALIDATION" === classType1) {
				setFirstNumber(classType1)
				classType2(classType1);
				modelEnable();
			} else if ("MEETING" === classType1) {
				classType2(classType1);
				modelEnable();
			}
		} else {
			// 채번 대상이 아닐경우
			modelEnable();
			removeReadOnly();
		}
	}
}

// 번호 및 제목 태그 활성화
function removeReadOnly() {
	const numberTag = document.querySelector("#" + interalnumberId);
	const nameTag = document.querySelector("#" + nameId);
	numberTag.value = "";
	numberTag.removeAttribute("readonly");
	nameTag.value = "";
	nameTag.removeAttribute("readonly");
	nameTag.focus();
}

// 클래스 2타입 가여조기
// 중분류 세팅
function classType2(classType1) {
	const url = getCallUrl("/class/classType2?classType1=" + classType1);
	call(url, null, function(data) {
		const classType2 = data.classType2;
		if (data.result) {
			document.querySelector("#classType2 option").remove();
			document.querySelector("#classType2").innerHTML = "<option value=\"\">선택</option>";
			for (let i = 0; i < classType2.length; i++) {
				const value = classType2[i].value;
				const clazz = classType2[i].clazz;
				const tag = "<option data-clazz=\"" + clazz + "\" value=\"" + value + "\">" + classType2[i].name + "</option>";
				document.querySelector("#classType2").innerHTML += tag;
			}
		}
	}, "GET", false);
	selectbox(classType2Id);
}

// 중분류 변경
function second() {
	const selectElement = document.getElementById(classType2Id);
	const value = selectElement.value;
	const selectedIndex = selectElement.selectedIndex;
	const clazz = selectElement.options[selectedIndex].getAttribute("data-clazz");
	const text = selectElement.options[selectedIndex].text;
	const classType1 = document.getElementById(classType1Id).value;
	const tag = document.querySelector("#" + interalnumberId);
	const nameTag = document.querySelector("#" + nameId);
	if (value !== "") {
		if ("DEV" === classType1 || "INSTRUCTION" == classType1) {
			tag.value += clazz + "-";
			modelEnable();
		} else if ("REPORT" === classType1) {
			const currentDate = new Date();
			const year = currentDate.getFullYear() % 100; // 연도의 뒤 2자리
			const month = (currentDate.getMonth() + 1).toString().padStart(2, '0'); // 월을 2자리로 표현		
			tag.value += clazz + "-" + year + month + "-";
			classType3(classType1, value);
		} else if ("VALIDATION" === classType1) {
			classType3(classType1, value);
		} else if ("MEETING" === classType1) {
			tag.value += clazz + "-";
			nameEnable();
			nameTag.value = text + "-"
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
	const nameTag = document.querySelector("#" + nameId);
	if (value !== "") {
		const classType2 = document.getElementById(classType2Id);
		const index = classType2.selectedIndex;
		const text = classType2.options[index].text;
		if ("REPORT" === classType1) {
			numberTag.value += clazz + "-";
			nameEnable();
			nameTag.value = text + "-"
			lastNumber(numberTag.value, classType1);
		} else if ("VALIDATION" === classType1) {
			numberTag.value += clazz + "-";
			nameEnable();
			nameTag.value = text + "-"
			lastNumber(numberTag.value, classType1);
		}
	}
}

// 프로젝트 코드 변경
function preNumberCheck(obj) {
	const tag = document.querySelector("#" + interalnumberId);
	const value = obj.value;
	const nameTag = document.querySelector("#" + nameId);
	if (value !== "") {
		const classType1 = document.getElementById(classType1Id);
		const selectedIndex = classType1.selectedIndex;
		const text = classType1.options[selectedIndex].text;
		if (classType1.value === "DEV") {
			tag.value += value + "-";
			nameEnable();
			nameTag.value = text + "-";
			lastNumber(tag.value, classType1.value);
		} else if (classType1.value === "INSTRUCTION") {
			tag.value += value + "-WI-";
			nameEnable();
			nameTag.value = text + "-";
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


//문서명 앞부분 못건들게
function nameValidate(obj) {
	const value = obj.value;
	logger(value.indexOf("-"));
}

// 문서명 활성화
function nameEnable() {
	const nameTag = document.querySelector("#" + nameId);
	nameTag.value = "";
	nameTag.removeAttribute("readonly");
}

// 프로젝트 코드 활성화
function modelEnable() {
	$("#" + modelId).bindSelectDisabled(false);
}

// 값 초기화 + 읽기전용처리
function clearValue() {
	const numberTag = document.querySelector("#" + interalnumberId);
	const nameTag = document.querySelector("#" + nameId);
	numberTag.value = "";
	nameTag.value = "";
	numberTag.setAttribute("readonly", "readonly");
	nameTag.setAttribute("readonly", "readonly");
	$("#" + classType2Id).bindSelectSetValue("");
	$("#" + classType3Id).bindSelectSetValue("");
	$("#" + modelId).bindSelectSetValue("");

	$("#" + classType2Id).bindSelectDisabled(true);
	$("#" + classType3Id).bindSelectDisabled(true);
	$("#" + nameId).bindSelectDisabled(true);
	$("#" + modelId).bindSelectDisabled(true);
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
	const nameTag = document.querySelector("#" + nameId);
	nameTag.focus();
}

// 채번할지 말지 여부 체크
function autoNumberChecker(classType1) {
	if ("RND" === classType1 || "ELEC" === classType1 || "SOURCE" === classType1 || "APPROVAL" === classType1 || "ETC" === classType1) {
		$("#" + classType2Id).bindSelectDisabled(true);
		$("#" + classType3Id).bindSelectDisabled(true);
		return true;
	} else {
		$("#" + classType2Id).bindSelectDisabled(true);
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