# METEOR BLITZ — Proyecto POO UAM

**Universidad Autónoma de Manizales — Programación Orientada a Objetos**
Docente: Diana Henao | Entrega: 21 de mayo de 2026

---

## Integrantes

- **Laura Jaramillo** — Carpeta Model + sprites/sonidos
- **Tomas Osorio** — Carpeta View
- **Tomas Varona** — Carpeta Controller

---

## Descripcion del juego

Meteor Blitz es un videojuego 2D tipo Breakout desarrollado con Java Swing. El jugador controla
una paleta horizontal para lanzar una pelota que destruye ladrillos. Aliens invasores caen desde
arriba; si alcanzan la paleta el jugador pierde una vida.

Caracteristicas:

- 3 niveles con dificultad progresiva
- 4 tipos de ladrillo: Normal (1 HP), Reforzado (2 HP), PowerBrick, Indestructible
- 3 power-ups: Expansion de paleta, Pelota lenta, Vida extra
- Aliens invasores que bajan en diagonal
- Sistema de vidas (3 iniciales, maximo 5)
- Puntaje acumulado entre niveles
- Ranking Top 3 guardado en scores.txt
- 6 efectos de sonido WAV, 16 sprites PNG
- Bucle de juego a 60 FPS con delta-time

---

## Arquitectura MVC

src/main/java/controller/ -> GameController, InputHandler, Main
src/main/java/model/ -> Entity, Ball, Paddle, Player, Brick (y subclases),
Alien, AlienDrifter, PowerUpDrop (y subclases),
LevelManager, GameTimer, ScoreManager, Activatable
src/main/java/view/ -> GamePanel, MainFrame
src/main/resources/images/ -> 16 sprites PNG
src/main/resources/sounds/ -> 6 efectos WAV

---

## Estados del juego

WELCOME -> INSTRUCTIONS -> WELCOME
WELCOME -> [Iniciar] -> PLAYING
PLAYING -> [sin vidas] -> GAMEOVER
PLAYING -> [ladrillos = 0] -> WIN_LEVEL -> PLAYING (siguiente nivel)
WIN_LEVEL (nivel 3) -> WIN_GAME
GAMEOVER / WIN_GAME -> [Enter] -> WELCOME

---

## Controles

Flechas izq/der (o A/D) : mover paleta
Mouse (mover)            : mover paleta
SPACE                    : lanzar pelota
P                        : pausar / reanudar
Enter                    : confirmar en menus

---

## Compilar y ejecutar

**Opcion 1 — IntelliJ IDEA (recomendado)**

1. Abrir IntelliJ y seleccionar "Open" sobre la carpeta raiz del proyecto.
2. Clic derecho en `src/main/java` → Mark Directory as → Sources Root.
3. Clic derecho en `src/main/resources` → Mark Directory as → Resources Root.
4. Abrir el archivo `Main.java` (en `controller/`) y presionar el boton verde de Run.

**Opcion 2 — Script automatico (Windows)**

Hacer doble clic en el archivo `compilar_y_ejecutar.bat` incluido en la raiz del proyecto.
El script compila todo y lanza el juego automaticamente.

**Opcion 3 — Linea de comandos (Windows, requiere Java 11+)**

Abrir una terminal (cmd) en la carpeta raiz del proyecto y ejecutar:

javac -encoding UTF-8 -d out -sourcepath src/main/java src/main/java/controller/Main.java
xcopy /E /Y src\main\resources out\
java -cp out main.java.controller.Main
---

## Pilares POO

Abstraccion  : Entity, Brick, Alien, PowerUpDrop (clases abstractas base)
Herencia     : Entity->Ball/Paddle; Brick->4 subclases; Alien->AlienDrifter;
PowerUpDrop->ExpandDrop/SlowBallDrop/ExtraLifeDrop
Polimorfismo : drop.activate(paddle,ball) y brick.hit() con comportamiento especifico
Encapsulamiento: atributos private, addScore() valida positivos, loseLife() controla flujo
Interfaz     : Activatable.activate(Paddle,Ball) para polimorfismo de power-ups
