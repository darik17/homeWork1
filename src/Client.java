

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by user on 29.11.16.
 */
public class Client {

    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 8888);
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

        try {
            System.out.println(reader.readLine());
            writer.write(consoleReader.readLine());
            writer.newLine();
            writer.flush();
            System.out.println(reader.readLine());
            Resender r = new Resender(socket);
            r.start();
            String str="";
            while (!(str.equalsIgnoreCase("exit"))){
                str=consoleReader.readLine();
                writer.write(str);
                writer.newLine();
                writer.flush();
            }
            r.stopped();

        } catch (Exception e) {
            e.printStackTrace();
        }  finally {
            writer.close();
            consoleReader.close();
            socket.close();
        }
    }
    static class Resender extends Thread{

        Socket socket;
        private boolean stop;
        public Resender(Socket socket) {
            this.socket = socket;
        }
        public void stopped(){
            stop=true;
        }
        public void run(){
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while (!stop) {
                    String str =reader.readLine();
                    System.out.println(str);
                }
            }catch (IOException e) {
                    System.err.println("Ошибка при получении сообщения.");
                    e.printStackTrace();
            }
        }
    }
}
