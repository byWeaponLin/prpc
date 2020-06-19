package com.github.weaponlin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        try {
            Socket s = new Socket("127.0.0.1",8888);

            //构建IO
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();



            Scanner scanner = new Scanner(System.in);

            while(scanner.hasNextLine()) {
                final OutputStreamWriter out = new OutputStreamWriter(os);
                BufferedWriter bw = new BufferedWriter(out);
                //向服务器端发送一条消息
                bw.write(scanner.nextLine() + "\n");
                bw.flush();


                //读取服务器返回的消息
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String mess = br.readLine();
                System.out.println("服务器："+mess);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
