package com.example.mqtttestingtool

import android.content.Context
import android.widget.Toast
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

class mqttHelper(var mqttAndroidClient: MqttAndroidClient) {
    var m:String = ""





    fun connect(context: Context, uri:String){
        //mqttAndroidClient = MqttAndroidClient(context, uri, id)
//        mqttAndroidClient.setCallback(object : MqttCallback{
//            override fun connectionLost(cause: Throwable?) {
//            }
//
//            override fun messageArrived(topic: String?, message: MqttMessage?) {
//                println("$topic 的msg: ${message.toString()}")
//                m = message.toString()
//            }
//
//            override fun deliveryComplete(token: IMqttDeliveryToken?) {
//            }
//
//        })
        val mqttConnectOptions = MqttConnectOptions()
        mqttConnectOptions.isCleanSession = false
        mqttAndroidClient.connect(mqttConnectOptions, object : IMqttActionListener{
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                Toast.makeText(context,"連線成功", Toast.LENGTH_LONG).show()
                println("isConnected? ${mqttAndroidClient.isConnected}")
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                println("連線失敗: $exception")
                Toast.makeText(context,"連線失敗: $exception", Toast.LENGTH_LONG).show()
                println("isConnected? ${mqttAndroidClient.isConnected}")
            }

        })
    }
    fun sub(topic:String, qos:String) {
        mqttAndroidClient.subscribe(topic, Integer.parseInt(qos),null, object : IMqttActionListener{
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                println("${topic}訂閱成功")
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                println("${topic}訂閱失敗")
            }
        })

    }
    fun unsub(topic: String){
        mqttAndroidClient.unsubscribe(topic, null, object : IMqttActionListener{
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                println("${topic}取消訂閱成功")
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                println("${topic}取消訂閱失敗")
            }

        })
    }
    fun pub(topic: String?, qos: String?, msg:String?, retain:Boolean?){
        val m = MqttMessage()
        m.payload = msg?.toByteArray()
        m.qos = Integer.parseInt(qos!!)
        m.isRetained = retain!!
        mqttAndroidClient.publish(topic, m, null, object :IMqttActionListener{
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                println("$m / ${topic}發布成功")
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                println("${topic}發布失敗")
            }

        })
    }

    fun isConnected():Boolean{
        return mqttAndroidClient.isConnected
    }

    fun setCallBack(mqttCallback: MqttCallback) {
        mqttAndroidClient.setCallback(mqttCallback)
    }


}