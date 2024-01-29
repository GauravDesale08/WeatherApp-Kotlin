package com.example.weatherapp

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.SearchView
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

           fetchWeatherData("mumbai")

        SearchCity()

    }

    private fun SearchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(cityName:String){
        val retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response = retrofit.getWeatherData(cityName, "66f69d287963e918135935ca1f90683a", "metric")
        response.enqueue(object : Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if(response.isSuccessful && responseBody != null){
                    val temperature = responseBody.main.temp.toInt()
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val Sunrise = responseBody.sys.sunrise
                    val Sunset = responseBody.sys.sunset
                    val seaLevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main?:"Unknown"
                    val maxTemp = responseBody.main.temp_max
                    val mintemp = responseBody.main.temp_min
                    binding.temp.text = "$temperature °C"
                    binding.weather.text = condition
                    binding.condition.text = condition
                    binding.maxTemp.text = "Max :$maxTemp °C"
                    binding.minTemp.text = "Max :$mintemp °C"
                    binding.humidity.text = "$humidity %"
                    binding.windSpeed.text = "$windSpeed m/s"
                    binding.sunRise.text = "$Sunrise"
                    binding.sunSet.text = "$Sunset"
                    binding.sea.text = "$seaLevel hpa"

                    binding.day.text = dayName(System.currentTimeMillis())
                    binding.date.text = date()
                    binding.cityName.text = "$cityName"

                    changeImagesAccTOConditions(condition)

                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun changeImagesAccTOConditions(condition:String) {
        when(condition){

            "Haze","CLouds", "Partly Clouds", "Overcast", "Mist", "Foggy" -> {
                binding.root.setBackgroundResource(R.drawable.cloudy_bg)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }

            "Clear Sky", "Sunny", "Clear" -> {
                binding.root.setBackgroundResource(R.drawable.sunny_bg)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }

            "Light Rain","Drizzle", "Moderate Rain", "Showers", "Heavy Rain" -> {
                binding.root.setBackgroundResource(R.drawable.rain_bg)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }

            "Light Snow","Moderate Snow", "Heavy Snow", "Blizzard"-> {
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }

        }
        binding.lottieAnimationView.playAnimation()
    }

    fun dayName(timestamp: Long): String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
    }

    fun date(): String{
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

}