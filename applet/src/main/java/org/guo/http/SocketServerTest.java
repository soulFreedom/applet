package org.guo.http;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * 模拟相关服务端，给客户端响应自定义报文
 * @author guohailong
 */
public class SocketServerTest {
	//端口
	private static final int port = 4201;

	public static void main(String[] args) throws Exception {
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			System.err.println(String.format("%s端口已经启动..", port));
			
			while (true) {
				Socket soketClient = serverSocket.accept();
				System.out.println("accept...");
				InputStream intputStream = soketClient.getInputStream();
				OutputStream outputStream = soketClient.getOutputStream();
				
				/**
				 * 在接收Client端的数据时，直接用InputStream的方式读取byte，与注释部分将InputStream封装成BufferedReader直接读取
				 * 一行的方式是不同的，读取一行默认以回车和换行作为一行的结束标识，假设客户端发送的数据中没有每一行加入回车换行
				 * 则 bufferedReader.readLine() 会一直阻塞。
				 * 
				 * 
				 */
				
				byte[] data = new byte[1024];
				intputStream.read(data);
				System.out.println(new String(data));

				/*
				 * BufferedReader bufferedReader = new BufferedReader(new
				 * InputStreamReader(intputStream)); StringBuilder reqMessage =
				 * new StringBuilder();
				 * 
				 * while ((readContent = bufferedReader.readLine()) != "-1") {
				 * System.out.println(readContent); }
				 * 
				 * System.out.println(String.format("reqMessage is :\n %s",
				 * reqMessage));
				 */
				
				/**
				 * 在给Client端返回数据时，要注意不能仅仅返回业务数据，http协议头的信息也要拼接上，才组成一个完整的响应，
				 * 否则，客户端在解析时会出错。
				 */
				
				BufferedOutputStream bis = new BufferedOutputStream(outputStream);
				BufferedWriter sbBuffer = new BufferedWriter(new OutputStreamWriter(bis));
				sbBuffer.write("HTTP/1.1 200 OK\n");
				sbBuffer.write("Content-Length: 363\n");
				sbBuffer.write("Content-Type: text/html;charset=utf8\n");
				sbBuffer.write("\n"); // 区分HEAD区和正文区
				sbBuffer.write(build_heilongjiang_resp());
				sbBuffer.flush();
				soketClient.close();
				System.err.print("write ok!");
			}
		} catch (IOException e) {
			System.out.println("连接出现异常");
			e.printStackTrace();
		}
	}

	public static String build_heilongjiang_resp() throws Exception {
		File f = new File("d:\\resp.txt");
		BufferedReader bReader = new BufferedReader(new FileReader(f));
		String text = null;
		StringBuilder resBoss = new StringBuilder();

		while ((text = bReader.readLine()) != null) {
			resBoss.append(text);
		}
		bReader.close();

		System.out.println("resBoss = " + resBoss);

		return resBoss.toString();
	}

}