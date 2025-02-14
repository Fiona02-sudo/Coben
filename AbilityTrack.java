package comp1110.ass2;
import java.util.ArrayList;
import comp1110.ass2.gui.Colour;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbilityTrack {
    private Map<Colour, AbilityTrackEntry> tracks;
    private Board board;
    private Tile tile_new;

    public AbilityTrack(Board board) {
        tracks = new HashMap<>();
        this.board = board;

        // Initialize tracks for different abilities

        tracks.put(Colour.RED, new AbilityTrackEntry(Colour.RED, List.of(1,3), List.of(2,4,5,6)));
        tracks.put(Colour.BLUE, new AbilityTrackEntry(Colour.BLUE, List.of(1, 5), List.of(2,4,6)));
        tracks.put(Colour.GREEN, new AbilityTrackEntry(Colour.GREEN, List.of(1,5), List.of(4, 7,8)));
        tracks.put(Colour.YELLOW, new AbilityTrackEntry(Colour.GREEN, List.of(3, 6), List.of(5, 9)));
        tracks.put(Colour.PURPLE, new AbilityTrackEntry(Colour.GREEN, List.of(1, 4), List.of(3,6,7)));
    }


    public void markTrack(Colour color) {
        AbilityTrackEntry entry = tracks.get(color);
        if (entry != null) {
            entry.markOff();
        }
    }

    public List<Die> useAbility(Colour color, Tile tile, Game game, String changeColor, List<Die> dice) {
        AbilityTrackEntry entry = tracks.get(color);
        switch (color) {
            case YELLOW:
                if(entry.getAbilitiesAvailable()>0){
                    System.out.println("Player is reusing the large tile: " + tile.getName());
                    if (game.largeTileUsed.containsKey(tile.getName())) {
                        game.largeTileUsed.remove(tile.getName());
                        System.out.println("Tile reused.");
                        String tile_name = tile.getName();
                        tile_new = TileFactory.createTile(tile_name);
                        System.out.println("tile_new"+tile_new.getName());
                    } else{
                        String tile_name = tile.getName();
                        tile_new = TileFactory.createTile(tile_name);
                        System.out.println("tile_new"+tile_new.getName());
                    }

                    entry.useAbility();
                }
                break;

            case PURPLE:
                if(entry.getAbilitiesAvailable()>0){
                    entry.useAbility();
                }
                break;

            case GREEN:
                if(entry.getAbilitiesAvailable()>0){
                    System.out.println("Changing dice color to: " + changeColor);
                    for (Die die : dice) {
                        if (die.isCheck()==true) {
                            die.setColor(changeColor);
                            System.out.println(die.isCheck()+" Die change to: " + die.getColor());
                        }else{
                            System.out.println(die.isCheck()+" Die not changed " + die.getColor());
                        }
                    }
                    entry.useAbility();
                }else{
                    System.out.println("No abilities available.");
                }
                return dice;

            // In AbilityTrack's useAbility method
            case BLUE:
                System.out.println("Adding windows to the tile.");
                tile.addWindows(); // Update the tile's windows
                board.addWindowsToTile(tile); // Update the board's squares
                entry.useAbility();
                break;


            case RED:
                if(entry.getAbilitiesAvailable()>0){
                    System.out.println("Rerolling dice.");
                    for (Die die : dice) {
                        if (die.isCheck()==true) {
                            die.roll();
                            System.out.println(die.isCheck()+" Die rerolled to: " + die.getColor());
                        }else{
                            System.out.println(die.isCheck()+" Die not rerolled: " + die.getColor());
                        }
                    }
                    entry.useAbility();
                }else{
                    System.out.println("No abilities available.");
                }
                return dice;
        }

        return null;
    }

    public boolean useBonus(Colour color,List<Die> dice){
        AbilityTrackEntry entry = tracks.get(color);
        boolean using = entry.useBonus();
        String colorString = color.toString();
        System.out.println(colorString);
        Die die_new = new Die(colorString);
        dice.add(die_new);
        die_new.changeCheck();
        return using;
    }
    public int getAbilitiesAvailable(Colour color) {
        AbilityTrackEntry entry = tracks.get(color);
        if (entry != null) {
            return entry.getAbilitiesAvailable();
        }
        return 0;

    }

    public int getBonusesAvailable(Colour color) {
        AbilityTrackEntry entry = tracks.get(color);
        if (entry != null) {
            return entry.getBonusesAvailable();
        }
        return 0;
    }


    public int getMarkedOff(Colour color) {
        AbilityTrackEntry entry = tracks.get(color);
        if (entry != null) {
            return entry.getMarkedOff();
        }
        return 0;
    }
    public Tile getTile_new(){
        return tile_new;
    }

    public List<Colour> getCompletedTracks() {
        List<Colour> completedTracks = new ArrayList<>();
        for (Map.Entry<Colour, AbilityTrackEntry> entry : tracks.entrySet()) {
            AbilityTrackEntry trackEntry = entry.getValue();
            if (trackEntry.isCompleted() && !trackEntry.hasScoredForCompletion()) {
                completedTracks.add(entry.getKey());
                trackEntry.setScoredForCompletion(true);
            }
        }
        return completedTracks;
    }


    public Map<Colour, AbilityTrackEntry> getTracks() {
        return tracks;
    }

    public void advanceTrack(Colour color, int steps) {
        AbilityTrackEntry entry = tracks.get(color);
        if (entry != null) {
            for (int i = 0; i < steps; i++) {
                entry.markOff();
                System.out.println("xywyy"+entry.getMarkedOff()+" "+entry.getColor());
            }
        }
    }
    public int getNextAbility(Colour color) {
        AbilityTrackEntry entry = tracks.get(color);
        if (entry != null) {
            return entry.getNextAbility();
        }
        return 0;
    }
    public int getNextBonus(Colour color) {
        AbilityTrackEntry entry = tracks.get(color);
        if (entry != null) {
            return entry.getNextBonus();
        }
        return 0;
    }
    public AbilityTrackEntry getTrack(Colour color) {
        return tracks.get(color);
    }




}