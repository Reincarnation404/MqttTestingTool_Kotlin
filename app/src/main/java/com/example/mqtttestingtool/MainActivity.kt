package com.example.mqtttestingtool

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        binding.viewModel = MainViewModel()
        binding.lifecycleOwner = this
        setContentView(binding.root)

        with(binding){

            btnConn.setOnClickListener {


                connect(this@MainActivity)

            }

            btnSub.setOnClickListener {

                if (mqttAndroidClient.isConnected){

                    sub()
                }else{
                    Toast.makeText(this@MainActivity,"尚未連線or連線有問題", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
            }

            btnUnsub.setOnClickListener {
                if (mqttAndroidClient.isConnected){
                    val topic = viewModel?.topic?.value
                    if (topic?.trim().isNullOrEmpty()){
                        txtTopic.error = "Topic不可為空"
                        return@setOnClickListener
                    }else{
                        unsub()
                    }
                }else{
                    Toast.makeText(this@MainActivity,"尚未連線or連線有問題", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
            }

            btnPub.setOnClickListener {
                pub()
            }

        }

    }


    private fun connect(context: Context){
        with(binding.viewModel){
            val uri = "${this?.ptc?.value}://${this?.ip?.value}:${this?.port?.value}"
            val clientId = MqttClient.generateClientId()
            mqttAndroidClient = MqttAndroidClient(context, uri, clientId)
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
                    println("$uri 連線成功")
                    println("isConnected? ${mqttAndroidClient.isConnected}")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    println("連線失敗: $exception")
                    println("isConnected? ${mqttAndroidClient.isConnected}")
                }

            })

        }

    }

    private fun sub(){
        with(binding.viewModel){
            try {
                mqttAndroidClient.subscribe(this?.topic?.value, Integer.parseInt(this?.qos?.value!!),null, object : IMqttActionListener{
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        println("${this@with.topic.value}訂閱成功")
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        println("${this@with.topic.value}訂閱失敗")
                    }
                })
            }catch (e: MqttException){
                println("${this?.topic?.value} 訂閱出錯: $e")
                throw RuntimeException(e)
            }

        }

    }

    private fun unsub(){
        with(binding.viewModel){
            mqttAndroidClient.unsubscribe(this?.topic?.value, null, object : IMqttActionListener{
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    println("${this@with?.topic?.value}取消訂閱成功")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    println("${this@with?.topic?.value}取消訂閱失敗")
                }

            })
        }
    }

    private fun pub(){
        with(binding.viewModel){
            val m = MqttMessage()
            m.payload = this?.msg?.value?.toByteArray()
            m.qos = Integer.parseInt(this?.qos?.value!!)
            m.isRetained = retain.value!!
            mqttAndroidClient.publish(this.topic.value, m, null, object :IMqttActionListener{
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    println("${this@with?.topic?.value}發布成功")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    println("${this@with?.topic?.value}發布失敗")
                }

            })
        }
    }
}