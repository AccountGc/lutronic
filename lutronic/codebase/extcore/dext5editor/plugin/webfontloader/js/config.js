G_DEPlugin["webfontloader"].config = {
    fonts: [
        /*
         * fontFamilies: 추가할 폰트명
         * fontCssUrl: 웹 폰트 css url
         */
        {
            fontFamilies: ['폰트1', '폰트2'],
            fontCssUrl: '/config/webFont.css'
        }
        /*
         * 여러개의 웹 폰트 css 를 사용할 경우 위 형태와 동일하게 추가해주시면 됩니다.
        {
            fontFamilies: ['폰트3'],
            fontCssUrl: ''
        },
        {
            fontFamilies: ['폰트4'],
            fontCssUrl: ''
        },
        */
    ],
    /* 
     * timeout (단위: ms, 기본값: 3000 ms(3초))
     * 설정한 시간 동안 폰트를 불러오지 못하면 더 이상 불러오지 않습니다.
     */
    timeout: ''
}