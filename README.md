<div align="center">
	
<a href="https://jitpack.io/#max1mde/EntitySize"><img src="https://jitpack.io/v/max1mde/EntitySize.svg"></a>
<a href="https://discord.gg/2UTkYj26B4" target="_blank"><img src="https://img.shields.io/badge/Discord_Server-7289DA?style=flat&logo=discord&logoColor=white" alt="Join Discord Server" style="border-radius: 15px; height: 20px;"></a>

  <img src="https://imgur.com/yMuZdvu.gif">
  
<br>
<br>

<img src="https://github.com/max1mde/EntitySize/assets/114857048/2288ecc1-2ed8-4e8e-814e-d4923d57bb0e">

<br>

<p>
With this plugin, you can:
Change the size of any living entity (bigger & smaller)
with optional steps for a transition.

Modify it for entities or players with a specific name, UUID, entity ID, scoreboard tag, the entity you are looking at, or entities in a specific range around you!

There are also some other optional modifiers like:
Movement speed, jump strength, step height, etc. (Look into the config)
To make it more playable for a player with a different scale.

This plugin overrides the vanilla player attributes!
</p>

  
</div>

# Commands

```
/entitysize reload (Reload config)
/entitysize reset <optional player / @a> (Reset size to default)
/entitysize <size> [time] (Change your own size)
/entitysize player <player> <size> [time]
/entitysize entity looking <size> [time] (The entity you are looking at)
/entitysize entity tag <tag> <size> [time] (All entities with a specific scoreboard tag)
/entitysize entity name <name> <size> [time] (All entities with a specific name)
/entitysize entity uuid <uuid> <size> [time] (Entity with that uuid)
/entitysize entity range <blocks> <size> [time] (Entities in a specific range from your location)
```

# Config
```yml
General:
  bStats: true
  language: en_us
Size:
  Transition: true
  TransitionSteps: 30
  IsReachMultiplier: true
  IsStepHeightMultiplier: true
  IsSpeedMultiplier: true
  IsJumpMultiplier: true
  IsSaveFallDistanceMultiplier: true
  IsReachReverted: false
  IsStepHeightReverted: false
  IsSpeedReverted: false
  IsJumpReverted: false
  IsSaveFallDistanceReverted: false
  ReachMultiplier: 1
  StepHeightMultiplier: 1
  SpeedMultiplier: 1
  JumpMultiplier: 1
  SaveFallDistanceMultiplier: 1
```

# Permissions
```
EntitySize.sizelimit.max.<number>
EntitySize.sizelimit.min.<number>
EntitySize.self (Change own size)

entitysize.commands (Allows the use of the entitysize command)
entitysize.player (Allows the use of the player subcommand)
entitysize.entity (Allows the use of the entity subcommand)
entitysize.reload (Allows the use of the reload subcommand)
entitysize.reset (Allows the use of the reset subcommand)
entitysize.reset.player (Allows the ability to reset any player)
entitysize.reset.all (Allows the ability to reset all players, using @a)
entitysize.self (Allows the use of the self subcommand)
entitysize.entity.looking (Allows selecting the entity being looked at)
entitysize.entity.tag (Allows selecting the entity by tag)
entitysize.entity.name (Allows selecting the entity by name)
entitysize.entity.uuid (Allows selecting the entity by uuid)
entitysize.entity.range (Allows selecting the entity by range)
```

# API
Gradle
```
repositories {
	mavenCentral()
	maven { url 'https://jitpack.io' }
}

dependencies {
	 compileOnly 'com.github.max1mde:EntitySize:1.5.4'
}
```
Maven
```xml
<dependency>
        <groupId>com.github.max1mde</groupId>
        <artifactId>EntitySize</artifactId>
        <version>1.5.4</version>
        <scope>provided</scope>
</dependency>
```

Add 

```java
EntityModifierService modifierService = EntitySize.getSizeService();

modifierService.resetSize(player);
modifierService.setSize(livingEntity, newScale);
modifierService.getEntity(player, range);
```

# Support
https://discord.com/invite/4pA7VUeQs4

![image](https://github.com/user-attachments/assets/8d79fd86-77ef-4f5b-a563-58e8448af5d4)
