package com.example.mqtttestingtool

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mqtttestingtool.databinding.ActivityMainBinding
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mqttAndroidClient: MqttAndroidClient
    private lateinit var mqttHelper: mqttHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        binding.viewModel = MainViewModel()
        binding.lifecycleOwner = this
        setContentView(binding.root)

        with(binding){
            //初始化MqttAndroidClient

            //mqttAndroidClient = MqttAndroidClient(this@MainActivity, uri, clientId)
            val uri = "${viewModel?.ptc?.value}://${viewModel?.ip?.value}:${viewModel?.port?.value}"
            val clientId = MqttClient.generateClientId()
            mqttHelper = mqttHelper(this@MainActivity,uri,clientId)


            btnConn.setOnClickListener {
                val uri2 = "${viewModel?.ptc?.value}://${viewModel?.ip?.value}:${viewModel?.port?.value}"
                mqttHelper.connect(this@MainActivity, uri2, clientId)
            }

            btnSub.setOnClickListener {
                val connected = mqttHelper.isConnected()
                if (connected){
                    val topic = viewModel?.topic?.value?.trim()
                    val qos = viewModel?.qos?.value?.trim()

                    if (topic.isNullOrEmpty()){
                        txtTopic.error = "Topic不可為空"
                    }else if (qos.isNullOrEmpty()){
                        txtQos.error = "請輸入qos"
                    }else if (Integer.parseInt(qos)<0 || Integer.parseInt(qos)>2){
                        txtQos.error = "qos只能0~2"
                    }else{
                        mqttHelper.sub(topic,qos)
                        //Toast.makeText(this@MainActivity,"$topic 訂閱成功", Toast.LENGTH_LONG).show()
                    }
                }else{
                    Toast.makeText(this@MainActivity,"尚未連線or連線出錯", Toast.LENGTH_LONG).show()
                }

            }

            btnUnsub.setOnClickListener {
                val connected = mqttHelper.isConnected()
                val topic = viewModel?.topic?.value?.trim()
                if (connected){
                    if (topic.isNullOrEmpty()){
                        txtTopic.error = "Topic不可為空"
                    }else{
                        mqttHelper.unsub(topic)
                    }
                }else{
                    Toast.makeText(this@MainActivity,"尚未連線or連線有問題", Toast.LENGTH_LONG).show()
                }
            }

            btnPub.setOnClickListener {
                val connected = mqttHelper.isConnected()
                if (connected){
                    val topic = viewModel?.topic?.value?.trim()
                    val qos = viewModel?.qos?.value?.trim()
                    val msg = viewModel?.msg?.value?.trim()
                    val retain = viewModel?.retain?.value

                    if (topic?.trim().isNullOrEmpty()){
                        txtTopic.error = "Topic不可為空"
                        return@setOnClickListener
                    }else if (qos?.trim().isNullOrEmpty()){
                        txtQos.error = "請輸入qos"
                        return@setOnClickListener
                    }else if(msg?.trim().isNullOrEmpty()){
                        txtMsg.error = "請輸入msg"
                        return@setOnClickListener
                    }else{
                        mqttHelper.pub(topic, qos, msg, retain)

                    }
                }else{
                    Toast.makeText(this@MainActivity,"尚未連線/連線有問題", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

            }

            showMessage()

        }

    }


    private fun connect(){
        with(binding.viewModel){
            val uri = "${this?.ptc?.value}://${this?.ip?.value}:${this?.port?.value}"
            val clientId = MqttClient.generateClientId()
            mqttAndroidClient = MqttAndroidClient(this@MainActivity, uri, clientId)
            mqttAndroidClient.setCallback(object : MqttCallback{
                override fun connectionLost(cause: Throwable?) {
                }

                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    this@with?.result?.value = message.toString()
                    println("messageArrived= $message from topic= $topic")
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                }

            })

            val mqttConnectOptions = MqttConnectOptions()
            mqttConnectOptions.isCleanSession = false
            mqttAndroidClient.connect(mqttConnectOptions, object : IMqttActionListener{
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Toast.makeText(this@MainActivity,"連線成功", Toast.LENGTH_LONG).show()
                    println("isConnected? ${mqttAndroidClient.isConnected}")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    println("連線失敗: $exception")
                    Toast.makeText(this@MainActivity,"連線失敗: $exception", Toast.LENGTH_LONG).show()
                    println("isConnected? ${mqttAndroidClient.isConnected}")
                }

            })

        }

    }

//    private fun sub(){
//        with(binding.viewModel){
//            mqttAndroidClient.subscribe(this?.topic?.value, Integer.parseInt(this?.qos?.value!!),null, object : IMqttActionListener{
//                override fun onSuccess(asyncActionToken: IMqttToken?) {
//                    println("${this@with.topic.value}訂閱成功")
//                }
//
//                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
//                    println("${this@with.topic.value}訂閱失敗")
//                }
//            })
//        }
//
//    }

//    private fun unsub(){
//        with(binding.viewModel){
//            mqttAndroidClient.unsubscribe(this?.topic?.value, null, object : IMqttActionListener{
//                override fun onSuccess(asyncActionToken: IMqttToken?) {
//                    println("${this@with?.topic?.value}取消訂閱成功")
//                }
//
//                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
//                    println("${this@with?.topic?.value}取消訂閱失敗")
//                }
//
//            })
//        }
//    }

//    private fun pub(){
//        with(binding.viewModel){
//            val m = MqttMessage()
//            m.payload = this?.msg?.value?.toByteArray()
//            m.qos = Integer.parseInt(this?.qos?.value!!)
//            m.isRetained = this.retain.value!!
//            mqttAndroidClient.publish(this.topic.value, m, null, object :IMqttActionListener{
//                override fun onSuccess(asyncActionToken: IMqttToken?) {
//                    println("${this@with?.topic?.value}發布成功")
//                }
//
//                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
//                    println("${this@with?.topic?.value}發布失敗")
//                }
//
//            })
//        }
//    }

    private fun showMessage(){
        with(binding.viewModel){
            this?.result?.value = mqttHelper.receivedMsg()
        }

    }


}