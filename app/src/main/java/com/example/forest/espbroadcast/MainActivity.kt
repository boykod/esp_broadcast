package com.example.forest.espbroadcast

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.os.StrictMode
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Layout
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
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

        try {
            //Open a random port to send the package
            clientSocket.broadcast
            startListenForUDPBroadcast()
        } catch (ex: IOException) {
            Log.e("Error", "IOException: $ex")
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.refresh,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?):Boolean  {

        if (item!!.itemId == R.id.refresh_btn){
            sendBroadcast("The is Broadcast")
        }

        return true
    }

    // Send Package
    private fun sendBroadcast (msg: String) {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val sendData = msg.toByteArray(Charsets.UTF_8)
        val address = InetAddress.getByName("255.255.255.255")
        val sendPackage = DatagramPacket(sendData, sendData.size, address, 1234)

        clientSocket.send(sendPackage)

        println("Broadcast sent to: " + address.toString())
    }

    //Listen for Broadcast
    private fun startListenForUDPBroadcast() {

        runOnUiThread {
            try {
                val broadcastIP = InetAddress.getByName("0.0.0.0")
                val port = 11111

                while (shouldRestartSocketListen) {
                    println("while($shouldRestartSocketListen)")
                    listenAndWaitAndThrowIntent(broadcastIP, port)
                }

            } catch (e: Exception){
                Log.i("UDP", "no longer listening for UDP broadcasts cause of error "
                        + e.message)
            }
        }
    }

    private val shouldRestartSocketListen: Boolean = true

    private fun listenAndWaitAndThrowIntent(broadcastIP: InetAddress, port: Int) {

        val receiveBuf = ByteArray(15000)

        if (clientSocket == null || clientSocket.isClosed) {
            clientSocket = DatagramSocket(port, broadcastIP)
            clientSocket.broadcast
        }

        val receivePacket = DatagramPacket(receiveBuf, receiveBuf.size)

        Log.e("UDP", "Waiting for UDP broadcast")

        clientSocket.receive(receivePacket)

        println("receive")



        var senderIP = receivePacket.address.hostName
        var message = String(receivePacket.data).trim()

        println("IP: " + senderIP.toString())
        println("message: $message")

    }
}
