package se.nsritbygg.myapplication

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : AppCompatActivity() {
    var etCity: EditText? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
          etCity = findViewById<EditText>(R.id.edtxtcity)
    }

    fun btnget(view: View) {
        //Define the variables and tolls
        //Yahoo Weather API URL and return JSon
        val url =
            "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22" + etCity?.text + "%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys"
        //Execute Async Task
          MyAsyncTask().execute(url)


    }

    inner class MyAsyncTask:AsyncTask<String,String,String>(){
        // First before run on background

        override fun onPreExecute() {

        }
        // Implement the method and can not access UI

        override fun doInBackground(vararg params: String?): String {
            try {

                //define the url we have to connect with
                val url = URL(params[0])
                //make connect with url and send request
                val urlConnection = url.openConnection() as HttpURLConnection
                //waiting for 7000ms for response
                urlConnection.connectTimeout = 700 //set timeout to 5 seconds
                try {
                    //getting the response data
                    val  InpuStream = convertStreamToString(urlConnection.inputStream)
                    publishProgress(InpuStream)
                } finally {
                    //end connection
                    urlConnection.disconnect()
                }
            } catch (ex: Exception) {
            }
            return ""
        }
        // Access UI

         @SuppressLint("SetTextI18n")
         override fun onProgressUpdate(vararg values: String?) {
             try {
                 val json = JSONObject(values[0].toString())
                 val query = json.getJSONObject("query")
                 val results = query.getJSONObject("results")
                 val channel = results.getJSONObject("channel")
                 val astronomy = channel.getJSONObject("astronomy")
                 val sunset = astronomy.getString("sunset")
                 val sunrise = astronomy.getString("sunrise")
                 val tv = findViewById<TextView>(R.id.txtviewresult)
                 //display response data
             tv.text = "sunset:$sunset,sunrise:$sunrise"



             } catch (ex: Exception) {
             }

        }
        //Last when procssen done!

        override fun onPostExecute(result: String?) {

        }
        // Help function to convert Stream To String
        fun convertStreamToString (inputStream: InputStream): String {

            val bureader = BufferedReader(InputStreamReader(inputStream))
            var line: String?
            var linereultcal: String? = ""

            try {
                while (bureader.readLine().also { line = it } != null) {
                    linereultcal += line
                }
                inputStream.close()
            } catch (ex: java.lang.Exception) {
            }

            return linereultcal!!
        }

    }

}