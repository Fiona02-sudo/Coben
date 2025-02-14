# Test Plan

## Main Classes to Test:
1. **AbilityTrack**
2. **AbilityTrackEntry**
3. **Board**
4. **Dice**
5. **Die**
6. **Game**
7. **Player**
8. **Position**
9. **Square**
10. **Tile**
11. **TileFactory**
12. **Complex Tests**

### Test Details:

### 1.  `AbilityTrack`

#### 1. Initial state test

- **Test**: Verify the ability track's initial state after being created.
- **Steps**: Initialize the ability track, check that it is empty, and no rewards or abilities are unlocked
- **Expected Outcome**: The initial number of markers for each color is zero, and no rewards or abilities are unlocked

#### 2. Label the track test

- **Test**: Verify that the number of marks increases correctly and unlock rewards and abilities in specific locations
- **Steps**: Mark the competency track twice
- **Expected Outcome**: The number of markers is increased correctly

#### 3. Unlock the Bonus Test

- **Test**: Verify that the reward is unlocked at the predetermined marked location
- **Steps**: Use getBonusesAvailable(String color) to verify the number of available rewards.
- **Expected Outcome**: The number of available rewards is correct

#### 4. Unlock the ability test

- **Test**: Unlock abilities at predetermined marker locations
- **Steps**: Use getAbilitiesAvailable(String color) to verify the number of available capabilities.
- **Expected Outcome**: After the function is called to unlock the capability, the correct number of available capacities is available

#### 5. Use the Bonus Test

- **Test**: Verify that the number of available rewards is reduced and cannot be used again if there are no available rewards.
- **Steps**: Try to use a reward when you have it to use, and you can't use it again if you don't
- **Expected Outcome**: Successful use when there is a reward, and failed when no reward can be used

#### 6. Use the Ability Test

- **Test**: Verify that the number of available capabilities is reduced and cannot be used again when there are no available capabilities.
- **Steps**: Try to use the ability when you have the ability to use it, and you can't use it again if you don't have the ability
- **Expected Outcome**: Use success when you have the ability, and try to use it when you don't have the ability to use it

#### 7.  Track completion test

- **Test**: Verify the maximum length of the marker to the track
- **Steps**: When the maximum length of the track is marked, the function is called to verify the returned result
- **Expected Outcome**: The returned result is true

#### 8. Anomaly testing

- **Test**: Verify that the color of the marker does not exist or that an exception is thrown when the track length is exceeded
- **Steps**: Try marking colors that don't exist or exceeding the track length
- **Expected Outcome**: Verify that an exception is thrown or an error is handled

------



### 2. `**AbilityTrackEntry**`

#### 1. Initial state test

- **Test**: Verify the ability track entry available bonus, and abilities is zero.
- **Steps**: Initialize the ability track entry and check whether the available bonus and abilities are 0
- **Expected Outcome**: The available bonuses and abilities are 0

#### 2. Marking test

- **Test**: Make sure that the number of tokens increases properly after tagging, and that rewards and abilities are unlocked correctly
- **Steps**: The marking function is invoked twice to check the number of marks
- **Expected Outcome**: The number of markers increases normally after two markers

#### 3. Unlock the Bonus test

- **Test**: Verify that the number of available rewards is correctly increased after unlocking the reward, and the reward can be used
- **Steps**: Once the function is called to unlock the reward, check the number of available rewards
- **Expected Outcome**: The number of available rewards is correct, and the rewards can be used normally

#### 4. Unlock the ability test

- **Test**: After verifying that the number of available abilities is correctly increased after unlocking the ability, the ability can be used
- **Steps**: After the invoked function unlocks the capability, check the number of available capacities
- **Expected Outcome**: If the number of available capabilities is correct, the capabilities can be used normally

#### 5. Use bonus and ability tests

- **Test**: Verification capabilities and rewards cannot be used indefinitely
- **Steps**: Use Competencies and Rewards, check the availability of competencies and rewards after use
- **Expected Outcome**: The available quantity decreases and cannot be used again when there is no available quantity.

#### 6. Boundary condition testing

- **Test**: The number of verification markers cannot exceed the length of the track
- **Steps**: Increase the number of markers and check the condition of the track when the number of markers exceeds the track length
- **Expected Outcome**: An exception is thrown when the number of markers exceeds the maximum value

#### 7. Get the next bonus and ability location

- **Test**: Verify where the next reward and competency are located
- **Steps**: Check the return values for the bonus and ability distances
- **Expected Outcome**: Returns the correct distance from the next bonus and ability

------



### 3. `BoardTest`

#### 1. Initial State Test
- **Test**: Verify the board's initial state after being created.
- **Steps**: Initialize a board, and for each square, check that it is empty, has no window, and contains no tile.
- **Expected Outcome**: All squares should be empty and have no windows or tiles.

#### 2. Tile Placement Test
- **Test**: Ensure tiles are placed on the board correctly and update square states.
- **Steps**: Place a tile on the board, then verify the squares occupied by the tile reflect the correct state (tile presence, window status).
- **Expected Outcome**: The squares should correctly reflect the tile's state after placement.

#### 3. Overlapping Tile Placement Test
- **Test**: Verify that placing overlapping tiles is not allowed.
- **Steps**: Place a tile, then try to place another tile in the same position, and verify that the placement is rejected.
- **Expected Outcome**: The second tile should not be placed.

#### 4. Out-of-Bounds Tile Placement Test
- **Test**: Ensure tiles cannot be placed outside the board's boundaries.
- **Steps**: Attempt to place a tile at various out-of-bounds positions and verify that placement fails.
- **Expected Outcome**: The placement should be rejected for all out-of-bounds positions.

#### 5. Floating Tile Placement Test
- **Test**: Verify that tiles cannot be placed without support (floating).
- **Steps**: Attempt to place a tile in a position where it would not be supported by tiles below, and ensure the placement fails.
- **Expected Outcome**: The placement should be rejected.

#### 6. Score Calculation Test (Row Completed)
- **Test**: Ensure that completing a row on the board results in the correct score.
- **Steps**: Fill a row with tiles that contain windows, then calculate the score.
- **Expected Outcome**: Completing a row with windows should result in 2 points.

#### 7. Score Calculation Test (Column Without Windows)
- **Test**: Ensure that completing a column without windows results in the correct score.
- **Steps**: Fill a column without windows and calculate the score.
- **Expected Outcome**: Completing a column without windows should result in 2 points.

#### 8. Clearing the Board Test
- **Test**: Ensure the board can be cleared correctly.
- **Steps**: Place a tile, clear the board, and verify that all squares are empty with no windows or tiles.
- **Expected Outcome**: After clearing, the board should be in its initial state.

---

### 4. `Dice`

#### 1. Dice roll test

- **Test**: Verify that the number of dice generated is correct and that the colors and values are within the expected range
- **Steps**: Check the number of dice generated after calling the function, as well as their colors and values
- **Expected Outcome**: The number of dice is correct, and the colors and values are within the range

#### 2. Get the dice test

- **Test**: Verify that the acquired dice object is correct
- **Steps**: Call the function to get a dice
- **Expected Outcome**: The acquired dice object is correct and not empty

#### 3. Result change test

- **Test**: Verify the randomness of the color of the dice after rerolling
- **Steps**: Roll the dice twice and check the results of the rolls
- **Expected Outcome**: The result of the two dice rolls changes

### 5. `Die`

#### 1. Initial state test

- **Test**: Once the dice are created, verify the initial values and colors
- **Steps**: Create a dice, get their initial values and colors, and check if they are within a valid range
- **Expected Outcome**: The initial value and color of the dice are within the valid range

#### 2. Dice roll test

- **Test**: After rolling the dice, verify the initial values and colors
- **Steps**: Roll the dice, get their initial value and color, and check if it is within the valid range
- **Expected Outcome**: The initial value and color of the dice are within the valid range

#### 3. Multiple rolls of the dice test

- **Test**: Verify the randomness of the color of the dice after rerolling
- **Steps**: Roll the dice twice and check the results of the rolls
- **Expected Outcome**: The result of the two dice rolls changes

### 6. `GameTest`

#### 1. Player Turn Rotation
- **Test**: Verify that turns rotate correctly between players.
- **Steps**: Initialize the game, call `nextTurn()` twice, and check if the current player changes.
- **Expected Outcome**: Players should alternate turns, and after two turns, the first player should be active again.

#### 2. Tile Availability
- **Test**: Ensure tiles are marked as available or unavailable based on usage.
- **Steps**: Check if the large tile is available, mark it as used, then check its availability again. Also, check the small tile's availability.
- **Expected Outcome**: The large tile should be unavailable after being used, while the small tile should remain available.

#### 3. Player Tile Selection
- **Test**: Verify if a player can select a tile using dice and unlocked bonuses.
- **Steps**: Set up dice and bonuses, then test if the player can select the large tile.
- **Expected Outcome**: The player should be able to select the tile if they have sufficient dice and bonuses.

#### 4. Marking the Ability Track
- **Test**: Check that marking the ability track unlocks bonuses correctly.
- **Steps**: Mark the red ability track multiple times and check the number of marks and available bonuses after each mark.
- **Expected Outcome**: Marks and bonuses should increase as the track is marked.

---

### 7. `PlayerTest`

#### 1. Tile Placement
- **Test**: Verify that a tile can be placed on the board at a valid position.
- **Steps**: Place a tile at position (0, 0) and check the tile's properties.
- **Expected Outcome**: The placed tile should match the original tile's name, color, size, and shape.

#### 2. Tile Placement Validity
- **Test**: Check if a tile can be placed at specific positions and rotations.
- **Steps**:
    1. Place a tile at position (0, 0) and ensure it succeeds.
    2. Try placing a tile at an invalid position (e.g., -1, -1).
    3. Test overlapping by placing a tile at (0, 0) and then try to place another tile in the same position.
- **Expected Outcome**: The tile should be placed correctly at (0, 0), fail for invalid positions, and not overlap.

#### 3. Score Calculation
- **Test**: Calculate the player's score after placing tiles.
- **Steps**: Place tiles on the board and calculate the score.
- **Expected Outcome**: The score should follow the correct scoring logic (e.g., 0 if no scoring conditions are met).

#### 4. Ability Track Retrieval
- **Test**: Verify that the player's ability track can be retrieved.
- **Steps**: Call `getAbilityTrack()` and check if it returns a non-null value.
- **Expected Outcome**: The ability track should not be null.

---

### 8. `PositionTest`

#### 1. Getters (`getX` and `getY`)
- **Objective**: Verify `getX()` and `getY()` return correct values.
- **Steps**:
    1. Initialize a `Position` with (2, 3).
    2. Call `getX()` and `getY()` to check the values.
- **Expected Outcome**: `getX()` should return 2, and `getY()` should return 3.

#### 2. Setters (`setX` and `setY`)
- **Objective**: Verify `setX()` and `setY()` update values correctly.
- **Steps**:
    1. Initialize a `Position` with (2, 3).
    2. Call `setX(5)` and `setY(6)` to update values.
    3. Check if `getX()` returns 5 and `getY()` returns 6.
- **Expected Outcome**: `getX()` should return 5, and `getY()` should return 6.

#### 3. Equality (`equals`)
- **Objective**: Verify `equals()` works for different cases.
- **Steps**:
    1. Test equality with a `Position` having the same and different values.
    2. Test with `null` and a different object type.
- **Expected Outcome**: Equal positions should match, while different values, null, and other types should not.

#### 4. Equality Properties
- **Objective**: Ensure `equals()` follows reflexive, symmetric, and transitive properties.
- **Steps**:
    1. Reflexive: Check if `Position` equals itself.
    2. Symmetric: Check if `a.equals(b)` and `b.equals(a)` both return true.
    3. Transitive: Check if `a.equals(b)`, `b.equals(c)`, and `a.equals(c)` return true.
- **Expected Outcome**: All equality properties should hold.

#### 5. `hashCode()` for Equal Positions
- **Objective**: Verify that positions with the same values return the same hash code.
- **Steps**: Create two `Position` objects with (2, 3) and compare hash codes.
- **Expected Outcome**: Hash codes should be the same.

#### 6. `hashCode()` for Different Positions
- **Objective**: Ensure positions with different values have different hash codes.
- **Steps**: Create two `Position` objects with (2, 3) and (5, 7) and compare hash codes.
- **Expected Outcome**: Hash codes should differ.

---

### 9. `SquareTest`

#### 1. Initial State
- **Objective**: Verify that a new `Square` object is initialized correctly.
- **Steps**: Create a new `Square` and check if it is empty and has no window.
- **Expected Outcome**: The square should be empty and not have a window upon initialization.

#### 2. `setTile()` Method
- **Objective**: Verify that `setTile()` correctly sets a tile and updates the window status.
- **Steps**:
    1. Set a tile in the square and specify if it has a window.
    2. Check if the square is no longer empty, verify the window status, and ensure the correct tile is set.
    3. Test again by setting the tile without a window.
- **Expected Outcome**: The square should correctly reflect its non-empty state, window state, and tile properties.

#### 3. Clearing a Tile
- **Objective**: Ensure clearing a tile (setting it to null) works as expected.
- **Steps**:
    1. Set a tile with a window in the square.
    2. Clear the tile by setting it to null and indicate no window.
    3. Check if the square is empty and has no window.
- **Expected Outcome**: The square should be empty and without a window after clearing the tile.

---

### 10. `TileTest`

#### 1. Tile Properties
- **Objective**: Check the tile's name, color, and size.
- **Steps**: Create a tile, call `getName()`, `getColor()`, and `getSize()`.
- **Expected Outcome**: Values should match the tile's properties.

#### 2. Tile Shape
- **Objective**: Verify the tile's shape coordinates.
- **Steps**: Call `getShape()` and compare positions with expected values.
- **Expected Outcome**: Shape positions should be correct.

#### 3. Tile Rotation
- **Objective**: Ensure the tile rotates properly.
- **Steps**: Rotate the tile 90 degrees and check new positions.
- **Expected Outcome**: Positions should be updated after rotation.

#### 4. Tile Movement
- **Objective**: Ensure the tile moves correctly.
- **Steps**: Move the tile by a position offset and check the new positions.
- **Expected Outcome**: Positions should reflect the correct movement offset.

#### 5. Window Setting
- **Objective**: Verify that the windows are set correctly.
- **Steps**: Set windows with a boolean array, check values, and test with an invalid array length.
- **Expected Outcome**: Windows should update correctly, and an exception should be thrown for an invalid array length.

---

### 11. `TileFactoryTest`

#### 1. Valid Tile Creation
- **Test**: Verify that valid tile names create tiles correctly.
- **Steps**: Pass valid tile names to `TileFactory.createTile()` and check if the tile's properties (name, color, size, shape) are correct.
- **Expected Outcome**: Tiles should be created with the correct properties.

#### 2. Invalid Tile Creation
- **Test**: Ensure invalid tile names return `null` or throw exceptions.
- **Steps**: Pass invalid tile names to `TileFactory.createTile()` and verify that `null` is returned or an exception is thrown.
- **Expected Outcome**: Invalid tile names should not result in valid tile creation.

---

### Complex Tests

#### `XYWBonusTest`

##### 1. Test Blue Bonus Increases Tile Size
- **Objective**: Verify that the blue bonus ability allows the player to select a larger blue tile than normal.
- **Scenario**: Normally, when a player rolls three blue dice, they can select a blue tile of size 3. The blue ability should allow them to select a size 4 tile instead.
- **Steps**:
    1. Simulate a player unlocking the blue bonus ability.
    2. Create a set of dice with three blue dice.
    3. First, verify the player can select a size 3 blue tile without using the bonus.
    4. Use the blue bonus ability.
    5. Check if the player can now select a size 4 blue tile with the same three blue dice.
- **Expected Outcome**:
    - Without the blue bonus, the player should only be able to select a size 3 blue tile.
    - After activating the blue bonus, the player should be able to select a size 4 blue tile using the same set of blue dice.

##### 2. Test Red Ability Reroll
- **Objective**: Verify that the red bonus ability allows the player to reroll non-red dice.
- **Scenario**: Players can unlock the red ability, which grants them a reroll for dice that are not red. This test checks if the reroll works correctly.
- **Steps**:
    1. Unlock the red ability for the player.
    2. Set up an initial roll with a mix of dice colors (e.g., red, green, yellow, purple, white).
    3. Use the red ability to reroll all non-red dice.
    4. Check the resulting dice values after the reroll.
- **Expected Outcome**: Only non-red dice should be rerolled, while the red dice should remain unchanged. The player should end up with a new roll for all non-red dice.

##### 3. Test Purple and Blue Combination
- **Objective**: Ensure that players can use the purple and blue abilities together, enabling them to place an extra tile with a window.
- **Scenario**: The purple ability allows a player to place an extra single-square tile, while the blue ability gives the tile a window.
- **Steps**:
    1. Unlock both the purple and blue abilities for the player.
    2. Use the purple ability to place an extra single-square tile on the board.
    3. Use the blue ability to give the extra tile a window.
    4. Verify that the extra tile is placed on the board and check if it has a window.
- **Expected Outcome**: The player should be able to place the extra single-square tile and the tile should have a window when the blue ability is used.

##### 4. Test Green Ability to Change Dice Color
- **Objective**: Verify that the green ability allows the player to change the color of their dice to a desired color.
- **Scenario**: The green ability enables players to convert the color of specific dice (e.g., yellow dice) into another color (e.g., red).
- **Steps**:
    1. Unlock the green ability for the player.
    2. Create a set of dice (e.g., three yellow dice).
    3. Use the green ability to change all yellow dice to red.
    4. Verify that the dice colors have changed correctly.
- **Expected Outcome**: After using the green ability, all yellow dice should be converted to red dice, and their color property should reflect this change.

---

#### GJTTilePlacementTest

##### 1. Tile Placement Affects Score and Ability Track Progression

- **Objective**: Ensure that placing tiles to complete a special row or column results in immediate effects, such as increased score and progression on the ability track.

- **Scenario**: In this game, when a player places a tile that completes a row or column (especially one containing a coat-of-arms), the player should immediately gain points and advance on the ability track.

- **Steps**:
  1. Simulate a two-player game and initialize the first player.
  2. Unlock the player's blue ability track to prepare for tile placement effects.
  3. Place a tile in a position that completes a row containing a coat-of-arms.
  4. Evaluate the row completion to check if the player’s score increases.
  5. Verify that the ability track progresses as a result of completing the row.

- **Expected Outcome**:

  - Upon placing the tile and completing the row, the player’s score should increase.

  - The player’s blue ability track should advance by marking off the next square.

  - The ability track progression should be correctly reflected, unlocking potential bonuses or abilities.

    

------



#### `XYWendGameTest`

##### 1. Game Ends When Player Reaches Max Score
- **Objective**: Ensure that the game ends when a player reaches the maximum score of 12 points, and no further actions can be taken.
- **Scenario**: In this game, once a player reaches the maximum score (12 points), the game should end, and no further actions such as placing tiles or advancing turns should be allowed.
- **Steps**:
    1. Simulate a two-player game.
    2. Manually adjust the score of player 1 to reach 12 points.
    3. Check that the game state reflects that the game has ended.
    4. Attempt to perform further actions, such as advancing to the next turn or placing tiles.
    5. Verify that these actions are not allowed and that an exception or error is raised if attempted.
- **Expected Outcome**:
    - Once a player reaches 12 points, the game should enter the "game ended" state.
    - No further actions (e.g., advancing turns, placing tiles) should be allowed.
    - Any attempt to continue the game should raise an appropriate error or prevent the action from completing.

