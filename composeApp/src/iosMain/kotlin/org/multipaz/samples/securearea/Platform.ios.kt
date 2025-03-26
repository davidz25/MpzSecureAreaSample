package org.multipaz.samples.securearea

import kotlinx.datetime.Instant
import kotlinx.io.bytestring.ByteString
import org.multipaz.crypto.Algorithm
import org.multipaz.crypto.EcCurve
import org.multipaz.securearea.CreateKeySettings
import org.multipaz.securearea.SecureArea
import org.multipaz.securearea.SecureEnclaveCreateKeySettings
import org.multipaz.securearea.SecureEnclaveSecureArea
import org.multipaz.securearea.SecureEnclaveUserAuthType
import org.multipaz.storage.ephemeral.EphemeralStorage
import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

actual suspend fun getPlatformSecureArea(): SecureArea {
    return SecureEnclaveSecureArea.create(
        storage = EphemeralStorage()
    )
}

actual fun getPlatformCreateKeySettings(
    challenge: ByteString,
    algorithm: Algorithm,
    userAuthenticationRequired: Boolean,
    validFrom: Instant,
    validUntil: Instant
): CreateKeySettings {
    // Note: Since iOS Secure Enclave doesn't generate key attestations [validFrom] and [validUntil]
    // is not used. Neither is [challenge].
    require(algorithm.curve!! == EcCurve.P256)
    return SecureEnclaveCreateKeySettings.Builder()
        .setAlgorithm(algorithm)
        .setUserAuthenticationRequired(
            required = userAuthenticationRequired,
            userAuthenticationTypes = setOf(SecureEnclaveUserAuthType.USER_PRESENCE)
        )
        .build()
}