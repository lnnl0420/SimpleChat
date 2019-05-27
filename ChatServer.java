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
    
    private static String[] badword = {"fuck", "crazy" , "씨발", "꺼져", "잠와" };
    // static 배열에 badwords를 만들어 저장했다.
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
            String line = null;
            while((line = br.readLine()) != null){
                    if(line.equals("/quit"))
                        break;
                    else if(line.indexOf("/to ") == 0){
                        sendmsg(line);
                    }
                    else if(line.equals("/userList")) {
                        send_userList();
                        //userlist 출력.
                    }
                    else if (line.contains("fuck")) {
                        badword();
                    }
                    else if (line.contains("crazy")) {
                        badword();
                    }
                    else if (line.contains("시발")) {
                        badword();
                    }
                    else if (line.contains("싫어")) {
                        badword();
                    }
                    else if (line.contains("fuxx")) {
                            badword();
                    }
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
            Iterator iter = collection.iterator();
            while(iter.hasNext()){
                PrintWriter pw = (PrintWriter)iter.next();
                if(pw != hm.get(id)){
                    // 해쉬맵에 저장돠어 있는 값(id)을 확인할 때, 확인된 값이 현재 유저의 아이디(자기자신)가 아니라면,
                    // 메시지를 출력하게 만듬.
                    pw.println(msg);
                    pw.flush();
                }
            }
        } // broadcast
    }
    
    public void send_userList() {
        synchronized(hm) {
            Set key = hm.keySet();
            for( Iterator iterator = key.iterator(); iterator.hasNext();) {
                // iterator method 로 키값을 받아온 후, 다음값이 없을때 까지 반복문을 돌린다.
                String id = (String) iterator.next();
                // string에 값을 저장한 후, 본인에게 출력 해준다.
                pw.print(id + " ");
                pw.pringln();
            }
        }
        int a = java.lang.Thread.activeCount()-1;
        //현재 사용하고 있는 thread의 값은 java.lang.Thread.activeCount()를 통해서 받아온다.
        //현재 가동되고 있는 서버를 제외해야 정확한 유저 수므로 -1 을 해주었다.
        pw.printfln("현재 총 접속자" + ": " + a);
        pw.flush();
    } //send_userlist
}

