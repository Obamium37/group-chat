import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;


public class Client {
    

    private Socket socket;

    private BufferedReader bufferedReader;

    private BufferedWriter bufferedWriter;

    private String username;


    public Client(Socket socket, String username){


        try{
            this.socket = socket;

            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            this.username = username;

        } catch(IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }


    public void sendMessage(){

        try{

            bufferedWriter.write(username);

            bufferedWriter.newLine();

            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);

            while(socket.isConnected()){

                // Taking msg and writing to server to dispay
                String msgToSend = scanner.nextLine();

                bufferedWriter.write(username + ": " + msgToSend);
                
                bufferedWriter.newLine();

                bufferedWriter.flush();
            }
        } catch(IOException e){

            closeEverything(socket, bufferedReader, bufferedWriter);

        }
    }


    public void listenForMessage(){
        // New thread so then you can listen for messages and also send them 

        new Thread(new Runnable() {

            @Override
            public void run() {

                String msgFromChat;

                while(socket.isConnected()){

                    
                    try{

                        msgFromChat = bufferedReader.readLine();

                        System.out.println(msgFromChat);
                    } catch(IOException e){

                        closeEverything(socket, bufferedReader, bufferedWriter);
                        
                    }
                }


                
            }

            
        }).start();


    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){

        try{
            // Closing all processes
            if(bufferedReader != null){

                bufferedReader.close();
            } 
            if(bufferedWriter != null){

                bufferedWriter.close();
            }

            if(socket != null){

                socket.close();
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter Username: ");

        String username = scanner.nextLine();

        Socket socket = new Socket("localhost", 1234);

        Client client = new Client(socket, username);

        client.listenForMessage();

        client.sendMessage();

    }
}
