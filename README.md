# TicTacToe-Server
Servidor del juego de TicTacToe Multijugador.

# Requisitos
- Base de datos MySQL (leer más abajo para ver la BBDD pública)

# Cómo usar el servidor
Para usar el servidor hay que ejecutar el archivo 'server.jar' haciendo doble click o con el comando 'java -jar server.jar' desde una terminal en el mismo directorio que el archivo. Una vez abierto, en el primer inicio se generará un archivo 'config.properties' y el servidor se cerrará, este archivo lo hay que modificar con las credenciales de la base de datos en donde se almacenarán todos los datos de los usuarios. Luego de realizar los cambios pertinentes en el archivo .properties, iniciaremos nuevamente el servidor y si vemos el mensaje de que la table 'accounts' ha sido creada todo va bien, si no, repasar los pasos anteriores.
Para que el servidor sea accesible desde fuera de nuestra red local, se deberá abrir el puerto especificado en el archivo config.
Para jugar, se ingresa en el menú de Multijugador en el juego (repo: TicTacToe-MP) y se coloca la IP seguida de dos puntos y el puerto del servidor, por ejemplo '1.1.1.1:2222' y se inicia sesión o se registra una cuenta nueva.

IP para conectarse a tu propio server y darsela a el compañero: http://cualesmiip.com/ (va esa ip y luego el puerto)

# Datos de la base de datos pública

server_port=9999

dbUsername=sAvPElBwoc

dbPassword=jHLx3J4242

dbPort=3306

dbHost=remotemysql.com

# Lista de comandos del servidor
- list: muestra la lista de partidas en progreso.
- purge: elimina todas las partidas creadas.
- stop: cierra el servidor.

# Imagenes
![alt text](https://cdn.discordapp.com/attachments/317157900365594645/638266175821840384/unknown.png)
![alt text](https://cdn.discordapp.com/attachments/317157900365594645/638266473181216818/unknown.png)
![alt text](https://cdn.discordapp.com/attachments/317157900365594645/638266610116984872/unknown.png)

