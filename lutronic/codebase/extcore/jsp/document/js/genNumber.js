const interalnumberId = "interalnumber";
const classType1Id = "classType1";
const classType2Id = "classType2";
const classType3Id = "classType3";
// 최초 화면 로드시 셀렉트 박스 변경
function toUI() {
	selectbox(classType1Id);
	selectbox(classType2Id);
	selectbox(classType3Id);

	// 초기 사용 불가 상태
	$("#" + classType2Id).bindSelectDisabled(true);
	$("#" + classType3Id).bindSelectDisabled(true);
}

// 화면 로드시 최초 호출 되는함수
function genNumber(obj) {
	const classType1 = obj.value;
	if (classType1 !== "") {
		// 채번 아닌 대상 구분
		let checker = autoNumberChecker(classType1);
		// 채번 대상이다
		if (!checker) {
			// 값 초기화
			clearValue();
			// 설변 경우
			if (classType1 === "DEV") {
				setReadOnly();
				setCommonNumber(classType1);
			} else if (classType1 === "CHANGE") {
				classType2(classType1);
				// 지침서
			} else if (classType1 === "INSTRUCTION") {
				setReadOnly();
				setCommonNumber(classType1);
				classType2(classType1);
			} else if ("REPORT" === classType1) {
				classType2(classType1);
			}
		} else {
			// 채번 대상이 아닐경우
			removeReadOnly();
		}
	}
}

// 값 초기화
function clearValue() {
	const tag = document.querySelector("#" + interalnumberId);
	tag.value = "";
}


// 기본 동일하게 적용되는 룰
function setCommonNumber(classType1) {
	if (classType1 === "DEV" || classType1 === "INSTRUCTION" || classType1 === "REPORT" || classType1 === "VAILDATION") {
		const tag = document.querySelector("#" + interalnumberId);
		const selectElement = document.getElementById(classType1Id);
		const selectedIndex = selectElement.selectedIndex;
		const value = selectElement.options[selectedIndex].getAttribute("data-clazz");
		tag.value = value;
	}
}

// 프로젝트 코드 선택전 체크
function preNumberCheck(obj) {
	const tag = document.querySelector("#" + interalnumberId);
	const value = obj.value;
	if (value !== "") {
		const classType1 = document.getElementById(classType1Id);
		if (classType1.value === "DEV") {
			tag.value += value + "-";
			classType2(classType1.value); // 증분류 활성화
		} else if (classType1.value === "INSTRUCTION") { // 지침서
			tag.value += value + "-WI-";
			lastNumber(tag.value, classType1.value);
		}
	}
}

// 중분류 값 세팅
function middleNumber() {
	const selectElement = document.getElementById(classType2Id);
	const selectedIndex = selectElement.selectedIndex;
	const value = selectElement.options[selectedIndex].getAttribute("data-clazz");

	const classType1 = document.getElementById(classType1Id).value;
	const tag = document.querySelector("#" + interalnumberId);
	if ("DEV" === classType1) {
		tag.value += value + "-";
		lastNumber(tag.value, classType1);
	} else if ("CHANGE" === classType1) {
		tag.value += value + "-";
		lastNumber(tag.value, classType1);
	} else if ("INSTRUCTION" === classType1) {
		tag.value += value + "-";
	} else if ("REPORT" === classType1) {
		tag.value += value + "-";
		lastNumber(tag.value, classType1);
	}
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
	selectbox("classType2");
}

// 내부 문서 인풋박스 읽기전용 속성제거
function removeReadOnly() {
	const tag = document.querySelector("#" + interalnumberId);
	tag.removeAttribute("readonly");
}

// 내부 문서 인풋박스 읽기전용 속성추가
function setReadOnly() {
	const tag = document.querySelector("#" + interalnumberId);
	tag.setAttribute("readonly", "readonly");
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
}
