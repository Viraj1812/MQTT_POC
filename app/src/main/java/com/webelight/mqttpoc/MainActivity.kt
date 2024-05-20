package com.webelight.mqttpoc

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage

class MainActivity : AppCompatActivity() {



    private lateinit var myMqttClient: MyMqttClient
    private lateinit var messageTextView: TextView
    private lateinit var messageEditText: EditText
    private lateinit var publishButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        messageTextView = findViewById(R.id.messageTextView)
        messageEditText = findViewById(R.id.messageEditText)
        publishButton = findViewById(R.id.publishButton)

        myMqttClient = MyMqttClient()

        myMqttClient.subscribeToTopic("test/topic")

        publishButton.setOnClickListener {
            val message = messageEditText.text.toString()
            if (message.isNotEmpty()) {
                myMqttClient.publishMessage("test/topic", message)
            }
        }

        myMqttClient.setMessageCallback { topic, message ->
            runOnUiThread {
                messageTextView.text = message
            }
        }
    }
}