package com.example.auth

import android.content.Context
import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import org.json.JSONObject

object MobileAuthStore {
  private const val PREFS_NAME = "fleet_crm_mobile_auth"
  private const val KEY_ACCOUNTS_JSON = "accounts_json"
  private const val KEY_ACTIVE_MOBILE = "active_mobile"
  private const val KEY_IS_LOGGED_IN = "is_logged_in"

  private fun prefs(context: Context) =
    context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

  fun normalizeMobile(raw: String): String {
    val digits = raw.filter { it.isDigit() }
    return if (digits.length == 12 && digits.startsWith("91")) {
      digits.substring(2)
    } else if (digits.length > 10 && digits.startsWith("0")) {
      digits.trimStart('0').takeLast(10)
    } else {
      digits
    }
  }

  private fun readAccountsMap(context: Context): JSONObject {
    val raw = prefs(context).getString(KEY_ACCOUNTS_JSON, null) ?: return JSONObject()
    return try {
      JSONObject(raw)
    } catch (_: Exception) {
      JSONObject()
    }
  }

  private fun writeAccountsMap(context: Context, json: JSONObject) {
    prefs(context).edit().putString(KEY_ACCOUNTS_JSON, json.toString()).apply()
  }

  private fun generateSalt(): String {
    val bytes = ByteArray(16)
    SecureRandom().nextBytes(bytes)
    return Base64.encodeToString(bytes, Base64.NO_WRAP)
  }

  private fun hashPassword(salt: String, password: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val bytes = digest.digest("$salt:$password".toByteArray(Charsets.UTF_8))
    return bytes.joinToString("") { "%02x".format(it) }
  }

  fun hasRegisteredAccount(context: Context): Boolean {
    return readAccountsMap(context).length() > 0
  }

  fun getLastMobile(context: Context): String {
    val saved = prefs(context).getString(KEY_ACTIVE_MOBILE, "").orEmpty()
    if (saved.isNotBlank()) return saved
    val accounts = readAccountsMap(context)
    val keys = accounts.keys()
    return if (keys.hasNext()) keys.next() else ""
  }

  fun isLoggedIn(context: Context): Boolean {
    return prefs(context).getBoolean(KEY_IS_LOGGED_IN, false)
  }

  fun markSessionLoggedIn(context: Context, mobile: String) {
    val normalized = normalizeMobile(mobile)
    prefs(context).edit()
      .putBoolean(KEY_IS_LOGGED_IN, true)
      .putString(KEY_ACTIVE_MOBILE, normalized)
      .apply()
  }

  fun markGoogleSessionLoggedIn(context: Context) {
    prefs(context).edit()
      .putBoolean(KEY_IS_LOGGED_IN, true)
      .apply()
  }

  fun logout(context: Context) {
    prefs(context).edit()
      .putBoolean(KEY_IS_LOGGED_IN, false)
      .apply()
  }

  fun registerAccount(
    context: Context,
    rawMobile: String,
    password: String,
    confirmPassword: String,
  ): Result<String> {
    val mobile = normalizeMobile(rawMobile)
    if (mobile.length != 10) {
      return Result.failure(IllegalArgumentException("Enter a valid 10-digit mobile number."))
    }
    if (password.length < 4) {
      return Result.failure(IllegalArgumentException("Password must be at least 4 characters."))
    }
    if (password != confirmPassword) {
      return Result.failure(IllegalArgumentException("Passwords do not match."))
    }
    val salt = generateSalt()
    val hash = hashPassword(salt, password)
    val accounts = readAccountsMap(context)
    val entry = JSONObject().apply {
      put("salt", salt)
      put("hash", hash)
    }
    accounts.put(mobile, entry)
    writeAccountsMap(context, accounts)
    return Result.success(mobile)
  }

  fun verifyCredentials(
    context: Context,
    rawMobile: String,
    password: String,
  ): Result<String> {
    val mobile = normalizeMobile(rawMobile)
    if (mobile.length != 10) {
      return Result.failure(IllegalArgumentException("Enter a valid 10-digit mobile number."))
    }
    if (password.isEmpty()) {
      return Result.failure(IllegalArgumentException("Enter your password."))
    }
    val accounts = readAccountsMap(context)
    if (accounts.length() == 0) {
      return Result.failure(IllegalStateException("No account registered yet. Please register first."))
    }
    val entry = accounts.optJSONObject(mobile)
      ?: return Result.failure(IllegalArgumentException("Mobile number not registered. Tap Register to create login."))
    val salt = entry.optString("salt", "")
    val expectedHash = entry.optString("hash", "")
    val actualHash = hashPassword(salt, password)
    if (salt.isBlank() || expectedHash.isBlank() || !MessageDigest.isEqual(
        expectedHash.toByteArray(Charsets.UTF_8),
        actualHash.toByteArray(Charsets.UTF_8)
      )
    ) {
      return Result.failure(IllegalArgumentException("Incorrect mobile number or password."))
    }
    return Result.success(mobile)
  }

  fun resetPassword(
    context: Context,
    rawMobile: String,
    newPassword: String,
    confirmPassword: String,
  ): Result<String> {
    val mobile = normalizeMobile(rawMobile)
    if (mobile.length != 10) {
      return Result.failure(IllegalArgumentException("Enter a valid 10-digit mobile number."))
    }
    if (newPassword.length < 4) {
      return Result.failure(IllegalArgumentException("Password must be at least 4 characters."))
    }
    if (newPassword != confirmPassword) {
      return Result.failure(IllegalArgumentException("Passwords do not match."))
    }
    val accounts = readAccountsMap(context)
    if (!accounts.has(mobile)) {
      return Result.failure(IllegalArgumentException("Mobile number is not registered."))
    }
    val salt = generateSalt()
    val hash = hashPassword(salt, newPassword)
    accounts.put(
      mobile,
      JSONObject().apply {
        put("salt", salt)
        put("hash", hash)
      }
    )
    writeAccountsMap(context, accounts)
    return Result.success(mobile)
  }
}
