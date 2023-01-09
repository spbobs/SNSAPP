package online.daliyq.messaging

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import online.daliyq.Notifier

class MessagingService: FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        message.data.let {
            when(it["type"]){
                "follow" -> {
                    Notifier.showFollowNotification(this, it["username"]!!)
                }
                "answer" -> {
                    Notifier.showAnswerNotification(this, it["username"]!!)
                }
            }
        }
    }
}