package com.example.findparcel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val trackingNo = findViewById<EditText>(R.id.etTrackingNumber)
        val status = findViewById<EditText>(R.id.etStatus)

        val b1 = findViewById<Button>(R.id.btnSearch)

        b1.setOnClickListener {

            var url: String =
                "http://192.168.100.23/findParcelAndroid/findParcel.php?trackingNumber="+trackingNo.text.toString()

            var rq: RequestQueue = Volley.newRequestQueue(this)
            var json =
                JsonObjectRequest(Request.Method.GET, url, null, Response.Listener { response ->

                    val s = response.getString("trackingNumber")
                    status.setText(s)

                }, Response.ErrorListener { error ->
                    Log.e("Volley Error", error.toString())
                })
            rq.add(json)
        }
    }
}