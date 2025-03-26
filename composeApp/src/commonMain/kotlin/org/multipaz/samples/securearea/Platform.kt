package org.multipaz.samples.securearea

import kotlinx.datetime.Instant
import kotlinx.io.bytestring.ByteString
import org.multipaz.crypto.Algorithm
import org.multipaz.securearea.CreateKeySettings
import org.multipaz.securearea.SecureArea

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect suspend fun getPlatformSecureArea(): SecureArea

expect fun getPlatformCreateKeySettings(
    challenge: ByteString,
    algorithm: Algorithm,
    userAuthenticationRequired: Boolean,
    validFrom: Instant,
    validUntil: Instant
): CreateKeySettings
