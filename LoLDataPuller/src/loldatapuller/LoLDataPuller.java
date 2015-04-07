/*
 *  LoLDataPuller.java
 *  This file pulls and stores a collection of League of Legends league data
 *  for use in a companion application.
 */
package loldatapuller;
import dto.FeaturedGames.FeaturedGames;
import java.util.Map;
import dto.League.League;
import dto.League.LeagueEntry;
import dto.FeaturedGames.Participant;
import dto.Summoner.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import main.java.riotapi.RiotApi;
import main.java.riotapi.RiotApiException;


class Player implements Serializable{
    public int wins, losses;
    public String name, league;
    Player(String name, String league, int wins, int losses) {
        this.name = name;
        this.league = league;
        this.wins = wins;
        this.losses = losses;
    }
}

public class LoLDataPuller {

    public static Map<String, ArrayList<Player>> leagueData;
    public static RiotApi api = new RiotApi("6c284814-e017-4df1-bee2-f373ff252136");
    
    public static void addData() throws RiotApiException {
        FeaturedGames featuredGames = api.getFeaturedGames();
        List<dto.FeaturedGames.CurrentGameInfo> games = featuredGames.getGameList();
        Iterator<dto.FeaturedGames.CurrentGameInfo> gamesIt = games.iterator();

        String idList = "";
        while(gamesIt.hasNext()) {
            dto.FeaturedGames.CurrentGameInfo gameInfo = gamesIt.next();
            Participant participant = gameInfo.getParticipants().iterator().next();
            Map<String, Summoner> map = api.getSummonerByName(participant.getSummonerName());
            Summoner summoner = (Summoner)(map.values().toArray()[0]);
            long ID = summoner.getId();
            idList += ID + ",";
        }

        Map<String, List<League>> data = api.getLeagueBySummoners(idList);
        for (Entry<String, List<League>> entry : data.entrySet()) {
        
            Iterator<League> leagueIt = entry.getValue().iterator();
            while(leagueIt.hasNext()) {
                League league = leagueIt.next();
                if (!league.getQueue().equals("RANKED_SOLO_5x5")) continue;

                String leagueName = league.getName();
                if (leagueData.containsKey(leagueName)) return;

                ArrayList<Player> playersInLeague = new ArrayList<>();
                Iterator<LeagueEntry> leagueEntryIt = league.getEntries().iterator();
                while(leagueEntryIt.hasNext()) {
                    LeagueEntry leagueEntry = leagueEntryIt.next();
                    String playerId = leagueEntry.getPlayerOrTeamId();
                    String name = leagueEntry.getPlayerOrTeamName();
                    int wins = leagueEntry.getWins();
                    int losses = leagueEntry.getLosses();

                    playersInLeague.add(new Player(name, leagueName, wins, losses));
                }
                leagueData.put(leagueName, playersInLeague);
            }
        }
    }
    
    public static void deserializeData() {
      try {
         FileInputStream fileIn = new FileInputStream("leagueData.ser");
         ObjectInputStream in = new ObjectInputStream(fileIn);
         leagueData = (HashMap<String, ArrayList<Player>>)in.readObject();
         in.close();
         fileIn.close();
         System.out.println("Serialized data loaded from leagueData.ser");
      }
      catch(Exception e) {
          leagueData = new HashMap<>();
          System.err.println(e.getMessage());
      }
    }
    
    public static void serializeData() {
      try {
         FileOutputStream fileOut = new FileOutputStream("leagueData.ser");
         ObjectOutputStream out = new ObjectOutputStream(fileOut);
         out.writeObject(leagueData);
         out.flush();
         out.close();
         fileOut.close();
         System.out.println("Serialized data saved to leagueData.ser with " + leagueData.size() + " entries.");
      }
      catch(IOException i) {
          System.err.println(i.getMessage());
      }
    }

    public static void main(String[] args) {        
        try {
            deserializeData();
            addData();
            serializeData();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        } 
    }
}

