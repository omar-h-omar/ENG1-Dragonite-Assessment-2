package com.dragonboat.game;

import java.util.Arrays;
import java.util.Comparator;

public class Leaderboard {
    //attributes
    private Boat[] sortedBoats;
    static private Comparator<Boat> ascRaceTime;

    public Leaderboard(Boat[] boats){
        this.sortedBoats = boats;
    }

    //sorts order of boats
    public void UpdateOrder(){
        Arrays.sort(this.sortedBoats, ascRaceTime);
    }

    //gets top boats
    public Boat[] GetTopBoats(int places){
        Boat[] finalists = new Boat[places];
        for(int i = 0; i < places; i++){
            finalists[i] = sortedBoats[i];
        }

        return finalists;
    }

    //gets top 3 places
    public Boat[] GetPodium(){
        return GetTopBoats(3);
    }

    //defining comparators
    static{
        //allows boat times to be compared
        ascRaceTime = new Comparator<Boat>(){
            @Override
            public int compare(Boat boat1, Boat boat2){
                return Long.compare(boat1.GetFastestTime(), boat2.GetFastestTime());
            }
        };
    }

    //get names and times of top boats
    public String[] GetTimes(int places){
        String[] out = new String[places];
        for(int i = 0; i < places; i++){
            out[i] = sortedBoats[i].GetName() + sortedBoats[i].GetFastestTime();
        }

        return out;
    }
}
