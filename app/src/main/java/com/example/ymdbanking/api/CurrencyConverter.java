package com.example.ymdbanking.api;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CurrencyConverter extends AppCompatActivity
{
	public enum CURRENCIES
	{
		ISRAEL("ILS"),
		USA("USD"),
		AUSTRIA("EUR");

		String currency;

		CURRENCIES(String s)
		{
			currency = s;
		}

		public String getCurrency() {return currency;}
	}

	private double conversionValue;
	private Context context;

	public CurrencyConverter(Context context)
	{
		this.context = context;
	}

	public void getConversionRate(String convertFrom,String convertTo,Double amountToConvert) {
		RequestQueue queue = Volley.newRequestQueue(context);
		String url = "https://free.currconv.com/api/v7/convert?q="+convertFrom+"_"+convertTo+"&compact=ultra&apiKey=9de917fc5752ab3a4e57";
		StringRequest stringRequest = new StringRequest(Request.Method.GET, url,response ->
		{
			JSONObject jsonObject;
			try
			{
				jsonObject = new JSONObject(response);
				double conversionRateValue = round(((Double) jsonObject.get(convertFrom+"_"+convertTo)), 2);
				conversionValue = round((conversionRateValue*amountToConvert), 2);
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
		}, error ->
		{

		});
		queue.add(stringRequest);
	}

	public static double round(double value, int places)
	{
		if (places < 0)
			throw new IllegalArgumentException();
		BigDecimal bd = BigDecimal.valueOf(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	public double getConversionValue() {return conversionValue;}
}
