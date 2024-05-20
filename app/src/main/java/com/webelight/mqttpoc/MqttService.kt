package com.webelight.mqttpoc

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

class MqttService : Service() {

    private lateinit var mqttClient: MqttAndroidClient
    private val serverUri = "tcp://YOUR_BROKER_URL:1883"
    private val clientId = "ExampleAndroidClient"
    private val subscriptionTopic = "test/topic"
    private val publishTopic = "test/topic"
    private val publishMessage = "Hello from Android"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mqttClient = MqttAndroidClient(applicationContext, serverUri, clientId)

        mqttClient.setCallback(object : MqttCallback {
            override fun connectionLost(cause: Throwable?) {
                Log.d("MqttService", "Connection lost")
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                Log.d("MqttService", "Message arrived: ${message.toString()}")
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                Log.d("MqttService", "Delivery complete")
            }
        })

        connect()

        return START_STICKY
    }

    private fun connect() {
        val options = MqttConnectOptions()
        options.isAutomaticReconnect = true
        options.isCleanSession = false

        try {
            mqttClient.connect(options, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d("MqttService", "Connected successfully")
                    subscribe()
                    publish()
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d("MqttService", "Failed to connect")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    private fun subscribe() {
        try {
            mqttClient.subscribe(subscriptionTopic, 1, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d("MqttService", "Subscribed successfully")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d("MqttService", "Failed to subscribe")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    private fun publish() {
        try {
            val message = MqttMessage()
            message.payload = publishMessage.toByteArray()
            mqttClient.publish(publishTopic, message)
            Log.d("MqttService", "Message published")
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
