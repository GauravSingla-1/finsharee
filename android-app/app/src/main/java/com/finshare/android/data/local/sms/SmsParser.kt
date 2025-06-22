package com.finshare.android.data.local.sms

import java.math.BigDecimal
import java.util.regex.Pattern
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmsParser @Inject constructor() {

    private val bankPatterns = mapOf(
        // Common bank SMS patterns
        "HDFC" to listOf(
            "spent INR ([0-9,]+\\.?[0-9]*) on ([^\\n]+) at ([^\\n]+)",
            "debited for INR ([0-9,]+\\.?[0-9]*) on ([^\\n]+)"
        ),
        "ICICI" to listOf(
            "spent Rs\\.([0-9,]+\\.?[0-9]*) on ([^\\n]+) at ([^\\n]+)",
            "debited by Rs\\.([0-9,]+\\.?[0-9]*)"
        ),
        "SBI" to listOf(
            "Rs\\.([0-9,]+\\.?[0-9]*) spent on ([^\\n]+)",
            "debited Rs\\.([0-9,]+\\.?[0-9]*)"
        ),
        "AXIS" to listOf(
            "spent INR ([0-9,]+\\.?[0-9]*) at ([^\\n]+)",
            "debited for INR ([0-9,]+\\.?[0-9]*)"
        ),
        "KOTAK" to listOf(
            "spent Rs ([0-9,]+\\.?[0-9]*) at ([^\\n]+)",
            "debited Rs ([0-9,]+\\.?[0-9]*)"
        )
    )

    private val merchantPatterns = listOf(
        "at ([A-Za-z0-9\\s&.-]+)",
        "on ([A-Za-z0-9\\s&.-]+)",
        "from ([A-Za-z0-9\\s&.-]+)"
    )

    private val amountPatterns = listOf(
        "INR ([0-9,]+\\.?[0-9]*)",
        "Rs\\.?\\s*([0-9,]+\\.?[0-9]*)",
        "USD ([0-9,]+\\.?[0-9]*)",
        "\\$([0-9,]+\\.?[0-9]*)"
    )

    fun parseTransactionSms(sender: String, messageBody: String): TransactionInfo? {
        try {
            // Check if this is a transaction SMS
            if (!isTransactionSms(sender, messageBody)) {
                return null
            }

            val amount = extractAmount(messageBody)
            val merchant = extractMerchant(messageBody)
            val transactionType = determineTransactionType(messageBody)
            val timestamp = System.currentTimeMillis()

            if (amount != null && amount > BigDecimal.ZERO) {
                return TransactionInfo(
                    amount = amount,
                    merchant = merchant ?: "Unknown Merchant",
                    transactionType = transactionType,
                    timestamp = timestamp,
                    rawMessage = messageBody,
                    sender = sender,
                    confidence = calculateConfidence(amount, merchant, sender)
                )
            }
        } catch (e: Exception) {
            // Log error but don't crash
        }
        
        return null
    }

    private fun isTransactionSms(sender: String, message: String): Boolean {
        val transactionKeywords = listOf(
            "spent", "debited", "charged", "paid", "transaction", 
            "purchase", "withdraw", "debit", "credited"
        )
        
        val bankKeywords = listOf(
            "HDFC", "ICICI", "SBI", "AXIS", "KOTAK", "BANK", "CARD"
        )
        
        val messageUpper = message.uppercase()
        val senderUpper = sender.uppercase()
        
        return transactionKeywords.any { messageUpper.contains(it.uppercase()) } &&
               (bankKeywords.any { senderUpper.contains(it) } || 
                messageUpper.contains("CARD") || 
                messageUpper.contains("A/C"))
    }

    private fun extractAmount(message: String): BigDecimal? {
        for (pattern in amountPatterns) {
            val matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(message)
            if (matcher.find()) {
                val amountStr = matcher.group(1)?.replace(",", "")
                try {
                    return BigDecimal(amountStr)
                } catch (e: NumberFormatException) {
                    continue
                }
            }
        }
        return null
    }

    private fun extractMerchant(message: String): String? {
        for (pattern in merchantPatterns) {
            val matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(message)
            if (matcher.find()) {
                val merchant = matcher.group(1)?.trim()
                if (!merchant.isNullOrBlank() && merchant.length > 2) {
                    return cleanMerchantName(merchant)
                }
            }
        }
        
        // Try to extract from bank-specific patterns
        for ((bank, patterns) in bankPatterns) {
            for (pattern in patterns) {
                val matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(message)
                if (matcher.find() && matcher.groupCount() >= 3) {
                    val merchant = matcher.group(3)?.trim()
                    if (!merchant.isNullOrBlank()) {
                        return cleanMerchantName(merchant)
                    }
                }
            }
        }
        
        return null
    }

    private fun cleanMerchantName(merchant: String): String {
        return merchant
            .replace(Regex("[^a-zA-Z0-9\\s&.-]"), "")
            .replace(Regex("\\s+"), " ")
            .trim()
            .take(50) // Limit length
    }

    private fun determineTransactionType(message: String): String {
        val messageUpper = message.uppercase()
        return when {
            messageUpper.contains("CREDITED") || messageUpper.contains("REFUND") -> "CREDIT"
            else -> "DEBIT"
        }
    }

    private fun calculateConfidence(amount: BigDecimal?, merchant: String?, sender: String): Float {
        var confidence = 0f
        
        // Amount confidence
        if (amount != null && amount > BigDecimal.ZERO) {
            confidence += 0.4f
        }
        
        // Merchant confidence
        if (!merchant.isNullOrBlank()) {
            confidence += 0.3f
        }
        
        // Sender confidence (known bank)
        if (bankPatterns.keys.any { sender.uppercase().contains(it) }) {
            confidence += 0.3f
        }
        
        return confidence.coerceIn(0f, 1f)
    }
}

data class TransactionInfo(
    val amount: BigDecimal,
    val merchant: String,
    val transactionType: String,
    val timestamp: Long,
    val rawMessage: String,
    val sender: String,
    val confidence: Float
)