package com.finshare.android.data.local

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsMessage
import android.util.Log
import com.finshare.android.data.local.sms.SmsParser
import com.finshare.android.data.local.sms.TransactionInfo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SmsReceiver : BroadcastReceiver() {

    @Inject
    lateinit var smsParser: SmsParser

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            
            messages?.forEach { message ->
                processSmsMessage(message)
            }
        }
    }

    private fun processSmsMessage(message: SmsMessage) {
        scope.launch {
            try {
                val senderAddress = message.displayOriginatingAddress
                val messageBody = message.messageBody
                
                Log.d("SmsReceiver", "Received SMS from: $senderAddress")
                Log.d("SmsReceiver", "Message body: $messageBody")

                // Parse SMS for transaction information
                val transactionInfo = smsParser.parseTransactionSms(senderAddress, messageBody)
                
                if (transactionInfo != null) {
                    Log.d("SmsReceiver", "Parsed transaction: $transactionInfo")
                    
                    // Store transaction for user review
                    storePendingTransaction(transactionInfo)
                    
                    // Show notification to user
                    showTransactionNotification(transactionInfo)
                }
            } catch (e: Exception) {
                Log.e("SmsReceiver", "Error processing SMS", e)
            }
        }
    }

    private suspend fun storePendingTransaction(transactionInfo: TransactionInfo) {
        // Store in local database for user review
        // This will be shown in a "Pending Transactions" section
    }

    private fun showTransactionNotification(transactionInfo: TransactionInfo) {
        // Show notification asking user if they want to add this as an expense
    }
}