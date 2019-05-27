//나의 깃주소 : https://github.com/lnnl0420/SimpleChat.git

import java.net.*;
import java.io.*;
import java.util.*;

public class ChatServer {
    
    public static void main(String[] args) {
        try{
            ServerSocket server = new ServerSocket(10001);
            System.out.println("Waiting connection...");
            HashMap hm = new HashMap();
            while(true){
                Socket sock = server.accept();
                ChatThread chatthread = new ChatThread(sock, hm);
                chatthread.start();
            } // while
        }catch(Exception e){
            System.out.println(e);
        }
    } // main
}

class ChatThread extends Thread{
    private Socket sock;
    private String id;
    private BufferedReader br;
    private HashMap hm;
    private boolean initFlag = false;
    public ChatThread(Socket sock, HashMap hm){
        this.sock = sock;
        this.hm = hm;
        try{
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));
            br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            id = br.readLine();
            broadcast(id + " entered.");
            System.out.println("[Server] User (" + id + ") entered.");
            synchronized(hm){
                hm.put(this.id, pw);
            }
            initFlag = true;
        }catch(Exception ex){
            System.out.println(ex);
        }
    } // construcor
    public void run(){
        try{
            String bad = "fuck crazy shit holy fuxx";
            // badwords 를 설정해 주었다.
            String line = null;
            while((line = br.readLine()) != null){
                String line1 = line.toLowerCase();
                if(line1.contains("fuck") || line1.contains("crazy") || line1.contains("shit") || line1.contains("holy") || line1.contains("fuxx"))
                    badword();
                    else if(line.equals("/quit"))
                        break;
                    else if(line.indexOf("/to ") == 0)
                        sendmsg(line);
                    else if(line.equals("/userList"))
                        send_userList();
                        //userlist 출력.
                    else
                    broadcast(id + " = " + line);
                }
        }catch(Exception ex){
            System.out.println(ex);
        }finally{
            synchronized(hm){
                hm.remove(id);
            }
            broadcast(id + " exited.");
            try{
                if(sock != null)
                    sock.close();
            }catch(Exception ex){}
        }
    } // run
    // line 으로 읽어들이는 문자에 금지어가 포함되어 있는지 'contains' 메소드(함수)로 확인을 하고, 금지어가 포함되어
    // 있다면 자신의 창에 경고문이 띄어지고, 만약 없다면 정상적인 출력 기능을 하게 하였다.
    public void sendmsg(String msg){
        int start = msg.indexOf(" ") +1;
        int end = msg.indexOf(" ", start);
        if(end != -1){
            String to = msg.substring(start, end);
            String msg2 = msg.substring(end+1);
            Object obj = hm.get(to);
            if(obj != null){
                PrintWriter pw = (PrintWriter)obj;
                pw.println(id + " whisphered. : " + msg2);
                pw.flush();
            } // if
        }
    } // sendmsg
    public void broadcast(String msg){
        synchronized(hm){
            Collection collection = hm.values();
            //collection 을 통해서 hm.values 값을 가져온다.
            Iterator iter = collection.iterator();
            PrintWriter pw2 = (PrintWriter)hm.get(id);
            // 현재 id의 value를 pw2에 저장한다.
            while(iter.hasNext()){
                //반복문을 통해 value값에 있는것을 pw 에 저장
                    PrintWriter pw = (PrintWriter)iter.next();
                    if(pw!=pw2 ){
                    // 저장한 pw와 pw2의 value가 일치하지 않으면, msg 출력
                    // 일치하면 자신이라는 뜻이므로, 메시지를 출력하지 않음.
                    pw.println(msg);
                    pw.flush();
                    }
                    else;
            }
        }
    }
    
    public void send_userList() {
        synchronized(hm) {
            Object obj = hm.get(id); // id의 pw 의 값을 얻는다
            if(obj != null) {
                PrintWriter pw (PrintWriter)obj;
                pw.println("유저의 수는 " + hm.size() + " : " +hm.keySet());
                // hm.size와 hm.keyset으로 얼마나 많은 유저가 들어있는지 출력.
                pw.flush();
            }
        }
    } //send_userlist

    public void badword() {
        synchronized(hm)    {
                Object obj = hm.get(id);
                // id 의 값을 얻음.
            if(obj != null) {
                PrintWriter pw = (PrintWriter)obj;
                    pw.println("don't use badwords!!");
                    pw.flush();
                        }
                }
            }
}
