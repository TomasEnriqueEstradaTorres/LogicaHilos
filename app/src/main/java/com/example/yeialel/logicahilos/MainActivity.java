package com.example.yeialel.logicahilos;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // TODO Ponga aquí la URL de la imagen que desea descargar
    public static String img = "https://img.imagenescool.com/ic/lunes/lunes_159.jpg";
    //OJO--> la direccion de interner de la imagen tiene que ser parecida a esta, que la direccion
    //del servidor donde esta alojada.

    // Crearemos el recurso a la memoria del teléfono, definimos la ruta hasta el nombre del archivo
    public static String pathImagen = Environment.getExternalStorageDirectory().toString() + "/imagen.jpg";

    // Declaramos los componentes de la UI
    private Button botonDescarga;
    private ProgressBar barraProgreso;
    private ImageView imagenVista;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Añadimos el listener el botón
        botonDescarga = (Button) findViewById(R.id.buttonDescarrega);
        botonDescarga.setOnClickListener(this);

        // Ponemos la barra de progreso con un máximo de 100
        barraProgreso = (ProgressBar) findViewById(R.id.progressBar);
        barraProgreso.setMax(100);

        // Asignamos el IMAGEVIEW de la vista
        imagenVista = (ImageView) findViewById(R.id.imageView);
    }

    /**AsyncTask que descarga la imagen de la red, recibe Strings como parámetro de entrada,
       actualiza el progreso con integers y no devuelve nada */
    private class TareaDescarga extends AsyncTask<String, Integer, Void> {
        // OnPreExecute ejecuta antes de que doInBackground
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Eliminamos la imagen y ponemos la barra de progreso a 0
            barraProgreso.setProgress(0);
            imagenVista.setImageDrawable(null);
        }

        // Tarea que hace las operaciones de red, no se puede manipular la UI desde aquí
        @Override
        protected Void doInBackground(String... url) {
            try {
                URL imagen = new URL(url[0]);  // Tomamos la URL que se ha pasado como argumento
                // Hacemos la conexión a la URL y miramos el tamaño de la imagen
                HttpURLConnection connection = (HttpURLConnection) imagen.openConnection();
                int totalImagen= connection.getContentLength();
                // Creamos el input y un buffer donde iremos leyendo la información
                InputStream inputstream = (InputStream) imagen.getContent();
                byte[] bufferImagen = new byte[1024];  // servira para almacenar los bytes que tiene la imagen
                // Creamos la salida, es decir, allí donde guardaremos la información (ruta de la imagen)
                OutputStream outputstream = new FileOutputStream(pathImagen);
                int descargado = 0;
                int count;
                /** El bucle se ejecutara mientras haya información que leer.
                 * Lo que esto hace es leer los bytes que tiene la imagen que se fue recibiendo
                 * y se leeran hasta que halla -1 que es cuando ya no hay mas que leer           */
                while ((count = inputstream.read(bufferImagen)) != -1) {
                    descargado += count;  // Se acumulamos todo lo que ha leído
                    /**Calculamos el porcentaje respecto del total y lo enviamos a publishProgress
                     * Esto es para que la barra de progreso sepa cuanto va a tardar en descargar la
                     * imagen                      */
                    publishProgress(((descargado * 100) / totalImagen));
                    outputstream.write(bufferImagen, 0, count); // Guardamos el disco lo que hemos descargado
                }
                // Cerramos los "stream", esto es para no gastar mas recursos de lo necesario.
                inputstream.close();
                outputstream.close();
            } catch (IOException exception) {
                Log.d("ERROR", "Algo no ha ido bien!");
                return null;
            }
            // No pasamos ninguna información al onPostExecute
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // Actualizamos la barra de progreso con el valor que se nos ha enviado desde doInBackground
            barraProgreso.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // Cuando termina la tarea, ponemos la imagen al IMAGEVIEW
            imagenVista.setImageDrawable(Drawable.createFromPath(pathImagen));
        }
    }

    @Override
    public void onClick(View v) {
        // Ejecutamos el AsyncTask pasándole como argumento la ruta del imagen.
        if (v == botonDescarga) new TareaDescarga().execute(img);
    }
}

/**CONCLUSION
 * Lo que hace el programa es recibir un objeto de tipo 'OutputStream' que sera la imagen para luego
 * leer sus bytes y reconstruir la imagen y mostrarla en un ImagenView y al mismo tiempo calculando
 * por medio de la cantidad de bytes obtenidos el tiempo que tardara la barra de progreso en mostrar
 * cuanto se tarda la imagen en descargar.
 */