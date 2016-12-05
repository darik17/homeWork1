/**
 * Created by I on 29.11.2016.
 */


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;


public class Server {
    static Logger logger = Logger.getLogger(Client.class.getName());
    //static List<Worker> t = Collections.synchronizedList(new ArrayList<Worker>());
    static CopyOnWriteArrayList<Worker> t = new CopyOnWriteArrayList<Worker>();

    public static void main(String[] args) {
        try  { ServerSocket serverSocket = new ServerSocket(8888);
            logger.info("Server start");
            while (true) {
                Socket socket = serverSocket.accept();
                logger.info("client connected");
                Worker worker = new Worker(socket, t);
                t.add(worker);
                worker.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("client disconnected");
        }
    }
    static class Worker extends Thread {
        Socket socket;
        String name;
        CopyOnWriteArrayList<Worker> t;
        String str = "";
        BufferedReader reader = null;
        BufferedWriter writer = null;

        public Worker(Socket socket, CopyOnWriteArrayList<Worker> t) {
            this.socket = socket;
            this.t = t;
        }
        @Override
        public void run() {
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                writer.write("enter you username");
                writer.newLine();writer.flush();
                name = reader.readLine();
                writer.write("hi, " + name + "!");
                writer.newLine();writer.flush();
                while (true) {
                    str=reader.readLine();
                    if (str.equalsIgnoreCase("exit"))
                        break;
                    for (Worker work : t) {
                        work.writer.write(name + ": " + str);
                        work.writer.newLine(); work.writer.flush();
                    }
                }
                writer.write("goodbye, " + name + "!");
                writer.newLine();
                writer.flush();
            }
            catch (Exception e) {}
            finally {
                logger.info("client disconnected");
                try {
                    socket.close();
                    t.remove(this);
                } catch (Exception e) {}
            }
        }
    }
}
