import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server{
    
    // Creates new socket


    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }

    public void startServer(){

        try{

            // Runs until Socket closes
            while(!serverSocket.isClosed()){

                // Waiting for client to join
                
                Socket socket = serverSocket.accept();

                System.out.println("New Client Joined");

                // Each object from class will commnuncate with the client

                ClientHandler clientHandler = new ClientHandler(socket);


                Thread thread = new Thread(clientHandler);
                thread.start();


            }
        } catch(IOException e) {
        }
    }

    public void closeServerSocket(){

        try{
            if(serverSocket != null){
                serverSocket.close();
            }

        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(1234);

        Server server = new Server(serverSocket);

        server.startServer();
        
    }

    
}