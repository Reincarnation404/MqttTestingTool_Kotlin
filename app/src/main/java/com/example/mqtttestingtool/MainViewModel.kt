package com.example.mqtttestingtool

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

//第一個字要大寫 不然會出現錯誤: Execution failed for task ':app:dataBindingGenBaseClassesDebug'. > couldn't make a guess for com.example.mqtttestingtool.viewModel
class MainViewModel(): ViewModel() {

    val ptc: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val ip: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val port: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val topic: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val msg: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val qos: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val result: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    //預設沒按toggleButton為false
    val retain: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>(false) }



//    fun subscribe(){
//
//        mqttAndroidClient.subscribe(topic.value, Integer.parseInt(qos.value!!),null, object : IMqttActionListener{
//            override fun onSuccess(asyncActionToken: IMqttToken?) {
//                println("${topic.value}訂閱成功")
//            }
//
//            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
//                println("${topic.value}訂閱失敗")
//            }
//        })
//    }

//    fun unsub(){
//        mqttAndroidClient.unsubscribe(topic.value, null, object : IMqttActionListener{
//            override fun onSuccess(asyncActionToken: IMqttToken?) {
//                println("${topic.value}取消訂閱成功")
//            }
//
//            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
//                println("${topic.value}取消訂閱失敗")
//            }
//
//        })
//    }

//    fun pub(){
//        val m = MqttMessage()
//        m.payload = msg.value?.toByteArray()
//        m.qos = Integer.parseInt(qos.value!!)
//        m.isRetained = retain.value!!
//        mqttAndroidClient.publish(topic.value, m, null, object :IMqttActionListener{
//            override fun onSuccess(asyncActionToken: IMqttToken?) {
//                println("${topic.value}發布成功")
//            }
//
//            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
//                println("${topic.value}發布失敗")
//            }
//
//        })
//    }

}