package com.webelight.mqttpoc

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

class MyMqttClient {

    private val brokerUrl = "tcp://broker.hivemq.com:1883"
    private val clientId = "webelight_solution"
    private var mqttClient: MqttClient? = null

    init {
        try {
            mqttClient = MqttClient(brokerUrl, clientId, MemoryPersistence())
            mqttClient?.setCallback(object : MqttCallback {
                override fun connectionLost(cause: Throwable?) {

                }

                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    message?.let {
                        messageCallback?.invoke(topic ?: "", it.toString())
                    }
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {

                }
            })

            val options = MqttConnectOptions()
            options.isCleanSession = true
            mqttClient?.connect(options)

        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun publishMessage(topic: String, message: String) {
        try {
            val mqttMessage = MqttMessage(message.toByteArray())
            mqttClient?.publish(topic, mqttMessage)
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun subscribeToTopic(topic: String) {
        try {
            mqttClient?.subscribe(topic)
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    private var messageCallback: ((String, String) -> Unit)? = null

    fun setMessageCallback(callback: (String, String) -> Unit) {
        messageCallback = callback
    }
}
