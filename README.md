#Flux 
<br/>
##About
Flux is a game originally created by Joe Ferrer and Erwin Umali, computer science majors from U.P. Diliman, for a computer science networking course project. It is a game that was inspired from lessons learned in Physics classes (i.e. Electromagnetism) and other ideas coming from all sorts of other stuff.

##Development
Currently, there is no official stand-alone Flux executable/program because the original project only required a public presentation (i.e. compiling Flux using eclipse and demonstrating it). However, further developments and building stand-alone server and client jar files are being considered. Additions/ experimentations/ improvements/ corrections to this game are welcome. 

##Usage
To compile Flux, you need to setup eclipse with Slick2d. You can refer any site which provides a guide on how to do this. Also, not all the files in this repository are used in Flux (some files were used for testing purposes only). After finishing setting up Slick2d in eclipse, run 'MyServer.java' first. Then run 'Intermission1SlickBasicGame.java' to connect and play. 

Note, in connecting, you will be asked your IP first. If you're the host just enter 127.0.0.1 or your IP itself. If you're a client wanting to join the host's game, type the host's IP.

![](https://raw.githubusercontent.com/joeferrer/Flux/master/screenshots/Screenshot_1.png)

The game is set in dark space, where you, a triangular flux carrier, color blue or green, must gather and collect 10 charges in your station as quickly as possible. Charges appear as small white squares in the game. A player's station appears as a circular spot with the same color as the color of the player (__note:__ _this is also the only other object seen by the player at the start of the game_). To gather charges, you must pass over a charge. To collect a charge, you must pass over your station. The first to collect 10 charges wins the game. Movement in the game follows the controls below:
>
__Go Up = W__
> 
__Go Down = S__
> 
__Go Left = A__ 
> 
__Go Right = D__


![](https://raw.githubusercontent.com/joeferrer/Flux/master/screenshots/Screenshot_2.png)

![](https://raw.githubusercontent.com/joeferrer/Flux/master/screenshots/Screenshot_3.png)

![](https://raw.githubusercontent.com/joeferrer/Flux/master/screenshots/Screenshot_5.png)

![](https://raw.githubusercontent.com/joeferrer/Flux/master/screenshots/Screenshot_6.png)

![](https://raw.githubusercontent.com/joeferrer/Flux/master/screenshots/Screenshot_7.png)

![](https://raw.githubusercontent.com/joeferrer/Flux/master/screenshots/Screenshot_12.png)

![](https://raw.githubusercontent.com/joeferrer/Flux/master/screenshots/Screenshot_13.png)

But by wary, collecting charges isn't that easy. As you gather charges, you are more suceptible to getting damaged. That is because of the concept of Flux. When an enemy attacks you, that is, when your carrier enters an enemy's flux field, which is a large circle that temporarily appears around the enemy carrier as it attacks, you get damaged by (the charge you carry)/epsilon, where epsilon is a defined constant equal to 0.008. For any carrier to attack, having a minimum of 1 charge is required. Of course you can also attack as well provided that you carry at least 1 charge. The control for attacking is specified below.

>
Attack = SPACE_BAR

![](https://raw.githubusercontent.com/joeferrer/Flux/master/screenshots/Screenshot_10.png)


If your hp (_indicated below your flux carrier_) becomes zero, you will restart at your starting point at the start of the game. Therefore, the more charges you carry the greater the damage you're capable of taking. If you carry no charge, you are of course invulnerable to damage but are also incapable of attacking. 

![](https://raw.githubusercontent.com/joeferrer/Flux/master/screenshots/Screenshot_9.png)

To wrap things up, this is an easy-to-play game that is tricky, fun, suspenseful, thrilling and appreciative of some Physics concepts (_i.e. electromagnetism_)

Enjoy Playing