Las funcionalidades de la app son las siguientes:

1) Carga inicial de los personajes en la primera página.

Nada más comenzar la app se hará la carga de estos y se rellenará un Spinner que permitirá mostrar los personajes de 100 en 100.

2)Selección de página.

Cambiando la página en el spinner, se irá a la que corresponda.

3) Buscar en página.

Introduciendo un texto en el EditText de arriba se mostrarán los personajes en la página actual que cumplan con ello.

4) Actualizar resultados/ Reestablecer

Siempre que se pulse este botón la app mirará de nuevo todos los personajes para establecer el número de páginas correcto y volverá a cargar la totalidad de los elementos que correspondan en la página.

5)Mostrar detalle del personaje.

Pulsando sobre el personaje, se mostrará su descripción y/o sus apariciones en comics, a no ser que esto no exista.

Para hacer la app se ha seguido una metodología TDD, en la que se han hecho, a grandes rasgos, las siguientes pruebas.


Pruebas que muestran que un elemento definido en un layout se muestra como debe.

Prueba de que el GridView se rellena correctamente tras la petición GET que devuelve el total de páginas del que se sacan los personajes.

Prueba de que se puede realizar una petición GET  con respuesta correcta que devuelve una parte de todos los personajes.

Prueba de que se puede realizar una petición GET con respuesta correcta que devuelve el detalle de un personaje.

Prueba de que se pueden coger todos los datos necesarios con la petición GET que devuelve una parte de todos los personajes.

Prueba de que se pueden coger todos los datos necesarios con la perición GET que devuelve el detalle de un personaje.

Prueba de que el GridView se rellena correctamente tras la petición GET que devuelve una parte de todos los personajes.

Prueba de que el GridView se rellena correctamente tras la petición GET que devuelve una parte de todos los personajes, usando el texto como filtro.

Ver que inicialmente se cargan los personajes de la primera página.

Ver que al seleccionar otra página en el spinner se muestran los personajes de esa página.

Ver que pulsando el botón actualizar resultados/reestablecer se recuenta el número de páginas y se cargan los personajes.

Ver que pulsando sobre el personaje se puede ir a la pantalla de detalle y que se muestra según se debe.
