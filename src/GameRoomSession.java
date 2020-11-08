import com.hangman.message.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameRoomSession implements Runnable{

    Boolean isRunning = true;
    Boolean showScore = true;

    private WordDatabase wordDatabase;

    //private ArrayList<ClientHandler> clients = new ArrayList<>();

    private List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<ClientHandler>());


    //constructor
    public GameRoomSession(){
        wordDatabase = new WordDatabase();
        wordDatabase.setString(wordDatabase.randomWord());
    }

    //Getter and setters

    //add client to this room
    public void addClientToRoom(ClientHandler client){
        this.clients.add(client);
    }

    //get clients
    public List<ClientHandler> getClients(){
        return this.clients;
    }

    public void writeToAllInRoom(Message message) throws IOException {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.writeMessageToUser(message);
            }
        }
    }


    //get clients info in this room
    public String getClientListNamesAndLives() {
        StringBuilder strUsernamesAndLives = new StringBuilder();

        for(ClientHandler client : clients){
            //System.out.println(client.getUserName());
            strUsernamesAndLives.append(client.getUserName() + " " + client.getLives() + " lives" + "\n");
        }
        return strUsernamesAndLives.toString();
    }


    @Override
    public void run() {

        while(isRunning){
            if(clients.size() == 3){
                try{
                    if(showScore){
                        writeToAllInRoom(new Message(getClientListNamesAndLives(), "updateusers"));
                        showScore = false;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                for(int i = 0; i < clients.size(); i++) {
                    //if(clients.get(i) != clients.get(2)) {
                        if (clients.get(i).getLives() == clients.get(2).getLives()) {
                            if (clients.get(i).getGuess() != null) {
                                try {
                                    wordDatabase.checkString(clients.get(i).getGuess(), wordDatabase);
                                    clients.get(i).setLives(clients.get(i).getLives()-wordDatabase.getMinusLives());
                                    wordDatabase.setMinusLives(0);
                                    System.out.println(clients.get(0).getLives());
                                    System.out.println(clients.get(1).getLives());
                                    System.out.println(clients.get(2).getLives());
                                    System.out.println(wordDatabase.getMinusLives());
                                    writeToAllInRoom(new Message(clients.get(i).getGuess(), "newguess"));
                                    clients.get(i).setGuess(null);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                }
            }
        }//end while isRunning
    } //end run
}


