/*
 *  LoLDataPuller.java
 *  This file pulls and stores a collection of League of Legends league data
 *  for use in a companion application.
 */
package loldatapuller;
import constant.Region;
import dto.FeaturedGames.FeaturedGames;
import java.util.Map;
import dto.League.League;
import dto.FeaturedGames.Participant;
import dto.League.LeagueEntry;
import dto.Stats.ChampionStats;
import dto.Stats.RankedStats;
import dto.Summoner.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import main.java.riotapi.RiotApi;
import main.java.riotapi.RiotApiException;


public class LoLDataPuller {

    public static Map<String, Set<League>> leagueData;
    public static Map<String, String> championData;
    public static RiotApi api = new RiotApi("6c284814-e017-4df1-bee2-f373ff252136");
    
    private static String getIdsFromFeaturedGames() throws RiotApiException{
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
        
        return idList;
    }
    
    public static void addData() throws RiotApiException {
        //String idList = getIdsFromFeaturedGames();
        String idList = "";
        
        String names[] = {"Omely", "Motroco", "Voryn", "LP Tanou", "Ywalsh"}; 
        
        for (String name : names) {
           long id = api.getSummonerByName(Region.EUW, name).values().iterator().next().getId();
           idList += id + ",";
        }

        Map<String, List<League>> data = api.getLeagueBySummoners(Region.EUW, idList);
        for (Entry<String, List<League>> entry : data.entrySet()) {
            
            Iterator<League> leagueIt = entry.getValue().iterator();
            while(leagueIt.hasNext()) {
                League league = leagueIt.next();
                if (!league.getQueue().equals("RANKED_SOLO_5x5")) continue;
                String tier = league.getTier();
                if (leagueData.containsKey(tier)) {
                    boolean contains = false;
                    for (League l : leagueData.get(tier)) {
                        String firstName = l.getEntries().iterator().next().getPlayerOrTeamName();
                        if (firstName.equals(league.getEntries().iterator().next().getPlayerOrTeamName())) contains = true;
                    }     
                    if (!contains) leagueData.get(tier).add(league); 
                }
                else {
                    Set<League> leagues = new HashSet<>();
                    leagues.add(league);
                    leagueData.put(tier, leagues);
                }
            }
        }
    }
    
    public static void deserializeData() {
      try {
         FileInputStream fileIn = new FileInputStream("leagueData.ser");
         ObjectInputStream in = new ObjectInputStream(fileIn);
         leagueData = (HashMap<String, Set<League>>)in.readObject();
         in.close();
         fileIn.close();
         System.out.println("Serialized data loaded from leagueData.ser");
         
         fileIn = new FileInputStream("championData.ser");
         in = new ObjectInputStream(fileIn);
         championData = (HashMap<String, String>)in.readObject();
         in.close();
         fileIn.close();
         System.out.println("Serialized data loaded from championData.ser");
      }
      catch(Exception e) {
          leagueData = new HashMap<>();
          championData = new HashMap<>();
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
         System.out.println("Serialized league data saved to leagueData.ser with " + leagueData.size() + " entries.");
         
         fileOut = new FileOutputStream("championData.ser");
         out = new ObjectOutputStream(fileOut);
         out.writeObject(championData);
         out.flush();
         out.close();
         fileOut.close();
         System.out.println("Serialized champion data saved to leagueData.ser with " + championData.size() + " entries.");
      }
      catch(IOException i) {
          System.err.println(i.getMessage());
      }
    }

    public static void main(String[] args) {        
        try {
            deserializeData();
            //addData();
            
            //Remove duplicates
       /*     HashSet<String> names = new HashSet<>();
            for (String tier : leagueData.keySet()) {
                 List<League> found = new ArrayList<>();
                 for (League league : leagueData.get(tier)) {
                    String name = league.getEntries().iterator().next().getPlayerOrTeamName();
                    if (names.contains(name)) found.add(league);
                    else names.add(name);
                }
                leagueData.get(tier).removeAll(found);
            } */
            
            
            for (String tier : leagueData.keySet()) {
                 for (League league : leagueData.get(tier)) {
                    for (LeagueEntry entry : league.getEntries()) {
                        String id = entry.getPlayerOrTeamId();
                        if (!championData.containsKey(id) || championData.get(id).equals("")) {
                            championData.put(id, getMostPlayedChampion(id));
                        }
                    }
                }
            }
            serializeData();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        } 
    }
    
    private static String getMostPlayedChampion(String id) throws RiotApiException {

        for (Region r : Region.values()) {
            try { 
                RankedStats stats = api.getRankedStats(r, Integer.parseInt(id));
                int max = 0; int maxId = -1;
                for (ChampionStats champ : stats.getChampions()){
                    if (champ.getId() == 0) continue;
                    int sessions = champ.getStats().getTotalSessionsPlayed();
                    if (sessions > max) {
                        max = sessions;
                        maxId = champ.getId();
                    }
                }
                return api.getDataChampion(maxId).getName();
            }
            catch(RiotApiException e) { }
        }
        System.out.println(id);
        return "";
    }
}

