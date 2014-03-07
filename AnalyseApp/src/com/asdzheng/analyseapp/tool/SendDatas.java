package com.asdzheng.analyseapp.tool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.util.Log;

public class SendDatas {

	public static int sendAppDatas(String json) {
		URL url;
		HttpURLConnection httpURLConnection = null;
		//使用键值对的方式发送数据
		json = "json=" + json ;
	
		try {
			byte[] data = json.getBytes();

			url = new URL("http://10.0.2.2:8080/AnalyseServer");
			httpURLConnection = (HttpURLConnection) url.openConnection();

			httpURLConnection.setConnectTimeout(3000); // 设置连接超时时间
			httpURLConnection.setDoInput(true); // 打开输入流，以便从服务器获取数据
			httpURLConnection.setDoOutput(true); // 打开输出流，以便向服务器提交数据
			httpURLConnection.setRequestMethod("POST"); // 设置以Post方式提交数据
			httpURLConnection.setUseCaches(false); // 使用Post方式不能使用缓存
			// 设置请求体的类型是文本类型
			httpURLConnection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			// 设置请求体的长度
			httpURLConnection.setRequestProperty("Content-Length",
					String.valueOf(data.length));
			// 获得输出流，向服务器写入数据
			OutputStream outputStream = httpURLConnection.getOutputStream();
			outputStream.write(data);
			outputStream.flush();
			outputStream.close();

			int response = httpURLConnection.getResponseCode(); // 获得服务器的响应码
			if (response == HttpURLConnection.HTTP_OK) {
				InputStream inputStream = httpURLConnection.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
				String responseStr = "";
				String test1 = "";
				while (( test1 = reader.readLine()) != null) {
					responseStr += test1;
				}
				Log.i("response : ", "responsenStr === " + responseStr);
				
			} else {
				return 0;
			}


		} catch (MalformedURLException e) {
			e.printStackTrace();
			return 0;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		} finally {
			httpURLConnection.disconnect();
		}

		return 1;

	}

}
