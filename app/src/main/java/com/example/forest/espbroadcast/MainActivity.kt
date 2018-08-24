package com.example.forest.espbroadcast

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.os.StrictMode
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.example.forest.espbroadcast.Adapter.DeviceAdapter
import com.example.forest.espbroadcast.TouchListener.RecyclerViewItemTouchListener
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.*
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class MainActivity : AppCompatActivity() {

    private var clientSocket = DatagramSocket()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        device_recycler_view.layoutManager = LinearLayoutManager(this)
        device_recycler_view.addOnItemTouchListener(RecyclerViewItemTouchListener(this,
                device_recycler_view,
                object : RecyclerViewItemTouchListener.ClickListener {
                    override fun onClick(view: View, position: Int) {
                        println("Press on $position")

                        Toast.makeText(this@MainActivity, "$position", Toast.LENGTH_SHORT).show()

                    }

                    override fun onLongClick(view: View, position: Int) {
                        addAlertDialog()
                        println("Long press on $position")
                    }
                }
        ))

        try {
            //Open a random port to send the package
            clientSocket.broadcast = true
            startListenForUDPBroadcast()
        } catch (ex: IOException) {
            Log.e("Error", "IOException: $ex")
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.refresh, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if (item!!.itemId == R.id.refresh_btn) {
            sendBroadcast("The is Broadcast")
        }

        return true
    }

    private fun listenAndWaitAndThrowIntent(broadcastIP: InetAddress, port: Int) {

        val receiveBuf = ByteArray(15000)

        if (clientSocket == null || clientSocket.isClosed) {
            clientSocket = DatagramSocket(port, broadcastIP)
            clientSocket.broadcast = true
        }

        val receivePacket = DatagramPacket(receiveBuf, receiveBuf.size)

        Log.e("UDP", "Waiting for UDP broadcast")

        clientSocket.receive(receivePacket)

        var senderIP = receivePacket.address
        var message = String(receivePacket.data).trim{it <= '?'}

        println("IP: " + senderIP.toString())
//        println("message: $message")

        var gsonInstance = GsonBuilder().create()
        var response = gsonInstance.fromJson(message, JsonDataModel::class.java)

        var array: ArrayList<JsonDataModel> = ArrayList()
        array.addAll(listOf(response))

        println("response: "+ response.data)

        runOnUiThread {
            device_recycler_view.adapter = DeviceAdapter(array)
        }


    }

    // Send Package
    private fun sendBroadcast(msg: String) {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val sendData = msg.toByteArray(Charsets.UTF_8)
        val address = InetAddress.getByName("255.255.255.255")
        val sendPackage = DatagramPacket(sendData, sendData.size, address, 1234)

        clientSocket.send(sendPackage)

        println("Broadcast sent to: " + address.toString())
    }

    private val shouldRestartSocketListen: Boolean = true

    //Listen for Broadcast
    private fun startListenForUDPBroadcast() {

        var thread = Thread(Runnable {
            kotlin.run {
                Looper.prepare()
                try {
                    val broadcastIP = InetAddress.getByName("0.0.0.0")
                    val port = 11111

                    while (shouldRestartSocketListen) {
                        listenAndWaitAndThrowIntent(broadcastIP, port)
                    }

                } catch (e: Exception) {
                    Log.i("UDP", "no longer listening for UDP broadcasts cause of error "
                            + e.message)
                }
            }
        })
        thread.start()
    }

    private fun addAlertDialog () {
        alert ("Long press") {

            yesButton {}
            noButton {}
        }.show()
    }
}

data class JsonDataModel(
        @SerializedName("data")
        var data: Data
)

data class Data (
        @SerializedName("ip")
        var ip: String,
        @SerializedName("port")
        var port: Int,
        @SerializedName("name")
        var name: String,
        @SerializedName("chipId")
        var chipId: String,
        @SerializedName("flashId")
        var flashId: String
)