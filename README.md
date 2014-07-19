Karma
=====

Plugin that was/is used on our server as a reputation system to reward (and punish) players for certain actions.

## Features:

* Lightweight
* Karma top list
* MySQL storage

## Permissions

Permission | Purpose | Default
--- | --- | ---
`karma.give` | Allows a player to give out karma | ops
`karma.take` | Allows a player to take karma away from others | ops
`karma.view` | View others karma | ops
`karma.announcement` | Show when a player receives karma | true
`karma.display` | Display your own karma | true
`karma.top` | Display the top 10 players | true

## config.yml

    database:
      hostname:
      portnmbr:
      database:
      username:
      password:
      maxConnections: 5
    cooldown: 30
    top: 10
    message:
      #can use <player> <karma> <reason>
      given: You have given karma to <player> for <reason>
      taken: You have taken karma from <player> for <reason>
  
      #can use <player> <admin> <karma> <reason>
      broadcastgive: <admin> has given <player> karma for <reason>.
      broadcasttake: <admin> has taken away karma from <player> for <reason>
  
      #can use <player> <admin>
      cooldown: You have tried to give/take <player> karma too soon.
  
      #can use <player>
      notfound: Could not find <player>.
  
      #can use <player> <karma>
      display: You have <karma> karma.
      view: <player> has <karma> karma.
  
      #can use <top>
      notop: Could not get top <top> 

You can use the following color codes and tags to customize the messages:

__Color codes__:

* `$0` = BLACK
* `$1` = DARK_BLUE
* `$2` = DARK_GREEN
* `$3` = DARK_AQUA
* `$4` = DARK_RED
* `$5` = DARK_PURPLE
* `$6` = GOLD
* `$7` = GRAY
* `$8` = DARK_GRAY
* `$9` = BLUE
* `$a` = GREEN
* `$b` = AQUA
* `$c` = RED
* `$d` = LIGHT_PURPLE
* `$e` = YELLOW
* `$f` = WHITE

__Tags__:

* `<player>` - Players name.
* `<admin>` - admins name
* `<karma>` - players karma
* `<top>` - number of player to display with /karmatop
* `<reason>` -  an optional reason for giving or taking karma. Will be 'null' if not used.

## Depencies

_none_

## Version

0.4.3

## License

[WTFPL](LICENSE)