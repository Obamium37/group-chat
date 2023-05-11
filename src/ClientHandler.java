import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{

    // Keeps track of all clients, so then when a Client sends a msg, we loop through clients and send joined message to all of them
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<ClientHandler>();

    private Socket socket;

    // Reads msgs sent from client
    private BufferedReader bufferedReader;
    // Sends msgs to client 
    private BufferedWriter bufferedWriter;


    private String clientUsername;


    public ClientHandler(Socket socket){

        try{

            this.socket = socket;

            // Getting sockets output stream for buffered reader and writer
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            // Adding Client username from info given from the Writer
            this.clientUsername = bufferedReader.readLine();

            clientHandlers.add(this);

            broadcastMessage("SERVER " + clientUsername + " has joined the chat");


        } catch(IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);

        }


    }


    @Override
    public void run() {

            // Thread waiting for msgs, and another one working with rest of program


            String msgFromClient;

            while(socket.isConnected()){
                try{
                    msgFromClient = bufferedReader.readLine();

                    broadcastMessage(msgFromClient);


                } catch(IOException e) {

                    closeEverything(socket, bufferedReader, bufferedWriter);

                    break;

                }
            }     
        
    }


    public void broadcastMessage(String messageToSend){

        for(ClientHandler clientHandler: clientHandlers){

            try{

                // Broadcasting message to everyone but the person who send it
                if(!clientHandler.equals(clientUsername)){
                    clientHandler.bufferedWriter.write(messageToSend);

                    clientHandler.bufferedWriter.newLine();


                    // Cool fact: Things buffer because it is waiting for the buffer array to be filled
                    clientHandler.bufferedWriter.flush();
                }


            } catch(IOException e){
                
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void removeClientHandler(){

        // Remove clients from server
        clientHandlers.remove(this);

        broadcastMessage("SERVER " + clientUsername + " has left the chat");

    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){

        removeClientHandler();

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


}




