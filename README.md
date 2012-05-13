# HungerGame plugin
Just another Hunger Game plugin for Minecraft.

---
Create and edit games using commands.  
Place snapshots and schematics in the games' folders.

#### Restore region
> The game region is restored before a game starts, using snapshots.  
> Add some randomness to each game, using schematics.

#### Joining
> After the region is restored and the random schematics are placed,  
> joining will be enabled for some amount of time.  

#### Teleporting and resetting players
> When enough players joined the game, the players will teleport to the game's spawn locations.  
> All the players' information is saved before they teleport, like inventory and health.  

#### Invulnerability
> Players have invulnerability for some time, when the game starts.  

#### Feast/Start-Feast
> A chest will be placed. And when a player opens it, a *private inventory* is created that contains the so-called *start feast*. This way, players can all loot from the same chest without interfering with eachother.  

> The so-called *feast* starts after some amount of time, after the game starts. It works in the same way as the *start-feast*.  

#### Die/Disconnect/Win
> When a player is killed, he is kicked from the server. And all his information is restored. So, when the player reconnects, he will be in the same state he was before the game started.  

> When a player disconnects, he has some amount of time to reconnect to the server, without being kicked from the game.

> The last player standing is the winner

---
## Configuration

> Every game gets it's own folder in the plugin's data folder.  
> In it, there's a game.yml file, a snapshots folder and schematics folder.  
> The game.yml file can be edited with */hunger edit help*

#### Snapshots
> Place zero or more snapshots of the world in *HungerGame/games/game-id/snapshots*.  
> When there are no snapshots, the default snapshot from WorldEdit is used.  
> When there are multiple snapshots, a snapshot is randomly chosen each game.

> > **For example:** Prepare the game region. Copy the world's folder, and paste it in the game's snapshots folder.  
> > Then, light everything on fire. And copy the world again, with a different name.  
> > Now, when the game starts, sometimes the area will be on fire. And sometimes it will not be on fire.

#### Schematics
> Schematics can be used to add randomness at a smaller scale.  

> Schematics will be placed after the region is restored.  
> One schematic is randomly selected from each subfolder of *HungerGame/games/game-id/schematics*.

> > **For example:** Prepare an area with TNT booby-traps. Save the area as a schematic, using WorldEdit. Then, rearrange the booby-traps and save another schematic.  
> > Place both schematics in a new subfolder of the game's schematics folder. For example: *HungerGame/games/game-id/schematics/boobytrap-area*
Now, when the game starts, you'll never know where the booby-traps are. 