package org.multipaz.samples.securearea

import android.os.Build
import kotlinx.datetime.Instant
import kotlinx.io.bytestring.ByteString
import org.multipaz.context.applicationContext
import org.multipaz.crypto.Algorithm
import org.multipaz.securearea.AndroidKeystoreCreateKeySettings
import org.multipaz.securearea.AndroidKeystoreSecureArea
import org.multipaz.securearea.CreateKeySettings
import org.multipaz.securearea.SecureArea
import org.multipaz.securearea.UserAuthenticationType
import org.multipaz.storage.android.AndroidStorage
import java.io.File

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

private val androidStorage: AndroidStorage by lazy {
    AndroidStorage(
        File(applicationContext.dataDir.path, "storage.db").absolutePath
    )
}

actual suspend fun getPlatformSecureArea(): SecureArea {
    return AndroidKeystoreSecureArea.create(
        storage = androidStorage
    )
}

actual fun getPlatformCreateKeySettings(
    challenge: ByteString,
    algorithm: Algorithm,
    userAuthenticationRequired: Boolean,
    validFrom: Instant,
    validUntil: Instant
): CreateKeySettings {
    check(algorithm.fullySpecified)
    var timeoutMillis = 0L
    // Work around Android bug where ECDH keys don't work with timeout 0, see
    // AndroidKeystoreUnlockData.cryptoObjectForKeyAgreement for details.
    if (algorithm.isKeyAgreement) {
        timeoutMillis = 1000L
    }
    return AndroidKeystoreCreateKeySettings.Builder(challenge)
        .setAlgorithm(algorithm)
        .setUserAuthenticationRequired(
            required = userAuthenticationRequired,
            timeoutMillis = timeoutMillis,
            userAuthenticationTypes = setOf(UserAuthenticationType.LSKF, UserAuthenticationType.BIOMETRIC)
        )
        .setValidityPeriod(validFrom, validUntil)
        .build()
}
