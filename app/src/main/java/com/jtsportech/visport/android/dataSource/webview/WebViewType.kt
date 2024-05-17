package com.jtsportech.visport.android.dataSource.webview

/**
 * Author: BenChen
 * Date: 2023/12/26 11:36
 * Email:chenxiaobin@cloudhearing.cn
 */
enum class WebViewType(vararg _url: String) {
    USER_AGREEMENT(
        "https://visport.jtsportperformance.com/jtsport-static/user-agreement.html",
        "https://visport.jtsportperformance.com/jtsport-static/user-agreement-en.html"
    ),
    PRIVACY_POLICY(
        "https://visport.jtsportperformance.com/jtsport-static/privacy-policy.html",
        "https://visport.jtsportperformance.com/jtsport-static/privacy-policy-en.html"
    );

    val url = _url
}