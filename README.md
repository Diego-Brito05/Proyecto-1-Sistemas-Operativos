Planteamiento del Problema
El objetivo de este proyecto es que los estudiantes desarrollen un simulador
que les permita comprender y aplicar conceptos fundamentales de la planificación
de procesos en sistemas operativos en sistemas monoprocesador. A través del
desarrollo de este simulador, se espera que los estudiantes analicen y encuentren la
configuración óptima del sistema en función de dos factores clave: precio y
rendimiento. Esto incluirá la gestión de procesos, el manejo de excepciones y la
implementación de diversas políticas de planificación.

El simulador deberá permitir la creación de procesos por parte del usuario,
quienes definirán características como el nombre del proceso, la cantidad de
instrucciones y, CPU bound (Muy consumidor del procesador) y I/O bound (Muy
consumidor de E/S). Si un proceso es I/O bound, deberá especificarse cuántos
ciclos se necesitan para generar una excepción y cuántos para satisfacerla.
Además, el sistema debe permitir la configuración de la duración de un ciclo. Este
parámetro podrá ser modificado tanto al inicio como durante la ejecución de la
simulación, y deberá ser guardado en un csv o json (lo que les sea más
conveniente) para que sea cargado en la ejecución inicial del proyecto.

La simulación en sí debe proporcionar una visualización clara de los estados
de los procesos, mostrando información esencial como el estado actual de las colas
de listos y bloqueados, y los datos de cada PCB (incluyendo valores de registros, ID
y nombre del proceso). Así cómo también se debe mostrar el proceso que se está
ejecutando en el momento, y la instrucción cuya dirección se encuentra en el
program counter. Además, debe ser posible alternar entre diferentes políticas de
planificación, como FCFS, SJF y Round Robin, para el cual se debe estar
ejecutando el sistema operativo.
El simulador podrá incluir gráficas que ilustran el rendimiento del sistema a lo
largo del tiempo. Estas gráficas proporcionarán una representación visual que
permita comprender mejor la eficiencia del sistema bajo diferentes configuraciones.
Las medidas de rendimiento requeridas son las siguientes: Procesos completados

por unidad de tiempo (throughput), utilización del procesador (tiempo que la CPU
está ocupada), equidad, tiempo de respuesta (tiempo de espera promedio por
proceso).
El simulador debe indicar en todo momento si se está ejecutando el sistema
operativo o un programa de usuario. Asimismo, debe visualizar de forma clara cómo
los procesos entran y salen de las listas de "listos", "bloqueados", y sus respectivas
colas de “suspendido” (listos suspendidos, bloqueados suspendidos).
Es importante destacar que el simulador debe ser lo suficientemente flexible
para permitir la configuración dinámica de ciertos parámetros, como la duración de
los ciclos, durante la ejecución (esta medida se implementa para que se pueda
apreciar mejor el rendimiento y movimiento de la simulación en tiempo de
ejecución, esto no es así en los Sistemas Operativos reales). Además, las
configuraciones realizadas deben poder guardarse para ser cargadas en futuras
ejecuciones. El sistema operativo del simulador deberá gestionar los cambios de
procesos y atender excepciones de manera eficiente.

Es importante recordar que dependiendo del tipo de consumo del procesador
de un proceso, este puede verse beneficiado o no por el algoritmo de planificación,
por lo que esto debe ser considerado.

En cuanto a las métricas de rendimiento, se espera que el simulador registre
los promedios generales del sistema. Esto permitirá a los estudiantes evaluar el
impacto de sus decisiones y ajustar las configuraciones para mejorar el rendimiento.
La simulación deberá ser lo suficientemente robusta para modelar escenarios
complejos. Se introducirá el concepto del estado suspendido para gestionar
procesos que requieran más memoria para terminar de ejecutarse (procesos que no
tengan todo su espacio de direcciones cargado en la memoria principal), liberando
así recursos de memoria principal y haciendo el comportamiento del sistema más
realista. Para ello, se debe considerar la categorización de las colas a largo plazo,
mediano plazo y corto plazo. Los principios considerados para la toma de

decisiones del SO para decidir qué procesos serán sacados de memoria principal
para liberar espacio deberán ser investigados y justificados el día de la defensa.
Finalmente, un aspecto crucial del proyecto será la visualización clara y
detallada del proceso de planificación. La interfaz gráfica deberá mostrar de
forma explícita cómo el planificador (scheduler) selecciona los procesos y,
fundamentalmente, cómo se reorganizan las colas de procesos listos después de
cada ciclo o evento del sistema.
Requerimientos funcionales
● Deben hacer uso de Hilos/Threads para la simulación de los procesos y
Semáforos/Semaphores para garantizar exclusión mútua.
● Se deben desarrollar 6 políticas de planificación que se muestran en el
Stallings. El ordenamiento de la cola posterior a la selección deberá de ser
programado por ustedes.
● Se debe hacer uso de una interfaz gráfica que permita observar durante la
simulación en tiempo de ejecución los estados de:
○ Cola de procesos del sistema, tanto de procesos listos cómo de
procesos bloqueados. Incluyendo una lista de los procesos culminados
■ Cualquier cambio en su ordenamiento debe ser visible
inmediatamente (por ejemplo, al aplicar un algoritmo con
prioridades o al finalizar un quantum en Round Robin).
■ La aplicación deberá generar un log de eventos de texto donde
se registre cada decisión importante del planificador (ej:
"Procesador selecciona Proceso C", "Proceso A entra en estado
de bloqueo"), o algún otro instrumento que permita ver que el
procesador está seleccionando los procesos adecuadamente
según los recursos del sistema y el estado actual del mismo.
○ Valor del program counter y qué proceso se está ejecutando en el
procesador.
○ Los siguientes elementos del PCB por proceso (tanto en las colas
como en el CPU):
■ ID (generado dinámicamente y único)
■ STATUS: Running, Blocked, Ready
■ Nombre
■ Estado del PC

■ Estado del MAR
○ Selector de tipo de algoritmo de planificación, e indicador de cual tipo
de planificación se está usando a tiempo real.
○ Si el sistema operativo se está ejecutando o se encuentra siendo
ejecutado un proceso de usuario por CPU.
○ El número de ciclo de reloj global dentro de la simulación desde que
se inicia.

● La simulación debe permitir en tiempo de ejecución:
○ Intercambiar los tipos de algoritmos de planificación de procesos.
○ La duración de un ciclo de ejecución. (en segundos o ms)
● Desde la interfaz se le debe poder indicar al programa los siguientes
parámetros, para que sean escritos en un archivo (CSV o JSON) y utilizados
en futuras simulaciones:
○ Funcionamiento General de la simulación
■ Duración del ciclo de ejecución de una instrucción. (En ms o
segundos)
○ Carga de procesos
■ Número de instrucciones por programa, o longitud.
■ Si el proceso es CPU bound o I/O bound.
■ El número de ciclos para realizar una excepción (para que el
proceso haga una solicitud de E/S)
■ El número de ciclos en el que se completa la solicitud de dicha
excepción.

● Respecto a la implementación de los estados de los procesos:
○ El sistema deberá implementar un modelo de estados que incluya:
Nuevo, Listo, Ejecución, Bloqueado, Terminado y Suspendido.
○ El sistema deberá identificar procesos que requieran más memoria
para terminar de ejecutarse, y gestionar la transición de los mismos al
estado "Suspendido".
○ El sistema deberá gestionar la transición del estado "Suspendido" de
vuelta al estado "Listo" una vez que la condición que causó el bloqueo
prolongado se haya resuelto.

● Mostrar en un mismo gráfico la utilidad con respecto al tiempo de los
estudios del rendimiento del sistema. Puede hacerse uso de librerías de
gráficos para Java

NOTA: Con el fin de minimizar la complejidad del proyecto y estandarizar. Se debe
asumir que:
● Todas las instrucciones se ejecutan en un único ciclo de instrucción.
● Por simplicidad, en este proyecto, todos los procesos se ejecutan de manera
lineal. Eso quiere decir que el PC y el MAR incrementarán una unidad por
cada ciclo del reloj.
● La asignación de procesos será de manera dinámica, por lo que habrá una
única cola de listos para el procesador.
● El tratamiento de las excepciones debe realizarse con el uso de Threads, y
cada “Thread” de excepción debe regresar al procesador en donde fue
generado.

Consideraciones
● El proyecto puede ser elaborado máximo por 2 personas (3 si alguno
queda solo)
● Se permiten proyectos de compañeros de diferentes secciones.
● Solo se permite el uso de librerías para presentar la gráfica y leer el CSV,
JSON, SQL, etc.
● No se permite uso de librerías para estructuras de datos, como ArrayList,
Queue, etc.
● Tener un repositorio en GitHub es obligatorio.
● Para la entrega, junto al código, se debe entregar un informe donde se
detalle la funcionalidad de las clases y métodos más importantes del
proyecto junto a las conclusiones de las configuraciones para cada
planificación, no hace falta documentar todo el código.
● La entrega del trabajo consta de el informe en .PDF y el link de github. Se
deberán enviar ambos a sleon@correo.unimet.edu.ve y
mginez@correo.unimet.edu.ve antes de las 7:00 am del viernes de
semana 7.
● Se requiere que hagan el proyecto en versiones posteriores a java 21,
para poder asegurar un manejo adecuado del uso de los repositorios tanto
entre los miembros del equipo como en la corrección.
● El viernes de semana 7, los estudiantes deberán realizar una asignación

presencial en el salón habitual, en la cual cada uno demostrará sus
conocimientos sobre el proyecto. Esta asignación será calificada; en caso de
ser reprobada, la nota del proyecto para ese integrante se calculará en
base a diez (10) puntos. La presencia de todos los integrantes del equipo
es obligatoria, ya que la asignación es individual.
● Los alumnos que no realicen la asignación, serán calificados en base a 0
(cero).
● Los proyectos sin interfaz gráfica, serán calificados en base a 0 (cero). No
se corregirá código para validar funcionamiento, más si para verificar.
Que los miembros del equipo lo comprendan.
● Los proyectos sin repositorio en GitHub, serán calificados en base a 0
(cero).
● Los programas que no se ejecuten adecuadamente, serán calificados en
base a 0 (cero).
● Los proyectos que no sean realizados en Java (específicamente en el IDE
de Netbeans) serán calificados en base a 0 (cero) .
● Es importante que cada uno de los miembros de cada equipo posea un buen
conocimiento general del funcionamiento de cada módulo de la solución.
