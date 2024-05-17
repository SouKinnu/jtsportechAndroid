package com.cloudhearing.android.lib_common.network.exception

/**
 * Base class for all backend exceptions.
 */
open class BackendException(override val message: String = "") : Exception(message)
