package comp1110.ass2;

public interface ShieldActionProvider {
    /**
     * Returns the special action chosen by the player based on the shield ID.
     * @param shieldId The identifier of the shield
     * @return The action chosen by the player ("a" or "b")
     */
    String getShieldAction(String shieldId);

    /**
     * Lets the player choose an ability track to advance.
     * @param availableTracks An array of available ability track colors
     * @return The chosen ability track color
     */
    String chooseAbilityTrack(String[] availableTracks);
}
