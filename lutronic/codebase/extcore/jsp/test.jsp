<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>jQuery UI 동적 탭 예제</title>
    <!-- jQuery 및 jQuery UI를 로드합니다. -->
    <link rel="stylesheet" href="https://code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
    <style>
        /* 스타일링을 원하는 대로 수정하세요. */
        .tab-container {
            display: flex;
        }
        .tab-menu {
            flex: 1;
            padding: 10px;
            background-color: #f0f0f0;
        }
        .tab-content {
            flex: 4;
            border: 1px solid #ccc;
            padding: 10px;
        }
    </style>
</head>
<body>
    <div class="tab-container">
        <div class="tab-menu">
            <ul id="menu">
                <!-- 왼쪽 메뉴 항목을 추가할 때마다 아래와 같이 작성합니다. -->
                <li><a href="#tab-1">메뉴 1</a></li>
                <li><a href="#tab-2">메뉴 2</a></li>
                <!-- 필요한 만큼 왼쪽 메뉴를 추가하세요. -->
            </ul>
        </div>
        <div class="tab-content">
            <div id="tabs">
                <!-- 동적으로 추가되는 탭 컨텐츠가 여기에 표시됩니다. -->
            </div>
        </div>
    </div>

    <script>
        $(function() {
            // 왼쪽 메뉴의 클릭 이벤트 처리
            $("#menu a").click(function(event) {
                event.preventDefault();

                // 새로운 탭 아이디를 생성 (랜덤 문자열로)
                const tabId = 'tab-' + Math.random().toString(36).substr(2, 9);

                // 새로운 메뉴 항목을 추가
                const menu = $("#menu");
                menu.append('<li><a href="#' + tabId + '">메뉴 ' + (menu.children().length + 1) + '</a></li>');

                // 새로운 탭 컨텐츠를 생성하고 페이지를 로드
                const tabs = $("#tabs");
                tabs.append('<div id="' + tabId + '"><iframe src="새로운페이지.html" width="100%" height="400"></iframe></div>');

                // 새로운 탭을 활성화
                tabs.tabs("refresh");
                tabs.tabs("option", "active", tabs.find("div").length - 1);
            });

            // 초기 탭 위젯을 설정
            $("#tabs").tabs();
        });
    </script>
</body>
</html>
