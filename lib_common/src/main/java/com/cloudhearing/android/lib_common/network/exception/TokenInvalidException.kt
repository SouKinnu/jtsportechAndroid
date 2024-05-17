package com.cloudhearing.android.lib_common.network.exception

import java.io.IOException

/**
 * Author: BenChen
 * Date: 2021/11/26 17:38
 * Email:chenxiaobin@cloudhearing.cn
 */
class TokenInvalidException(msg: String = "token失效，请重新登录") : IOException(msg)
