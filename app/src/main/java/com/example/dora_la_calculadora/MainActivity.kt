package mx.docentes.uacj.dora_la_calculadora

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Shadow
import mx.docentes.uacj.dora_la_calculadora.ui.theme.Dora_la_calculadoraTheme

data class BotonModelo(
    val id: String,
    var numero: String,
    var operacion_aritmetica: OperacionesAritmeticas = OperacionesAritmeticas.Ninguna,
    var operacion_a_mostrar: String = "",
    var sonido: Uri? = null
) {}

enum class EstadosCalculadora{
    CuandoEstaEnCero,
    AgregandoNumeros,
    SeleccionadoOperacion,
    MostrandoResultado
}

enum class OperacionesAritmeticas{
    Ninguna, // Esta es la opcion por default y sirve para hacer nada
    Suma,
    Resta,
    Multiplicacion,
    Division,
    Resultado
}

var hileras_de_botones_a_dibujar = arrayOf(
    arrayOf(
        BotonModelo("boton_9", "9", OperacionesAritmeticas.Multiplicacion, "*", Uri.parse("android.resource://mx.docentes.uacj.dora_la_calculadora/raw/sonido1")),
        BotonModelo("boton_8", "8", sonido = Uri.parse("android.resource://mx.docentes.uacj.dora_la_calculadora/raw/sonido2")),
        BotonModelo("boton_7", "7", OperacionesAritmeticas.Division, "/", Uri.parse("android.resource://mx.docentes.uacj.dora_la_calculadora/raw/sonido3")),
    ),
    arrayOf(
        BotonModelo("boton_6", "6", sonido = Uri.parse("android.resource://mx.docentes.uacj.dora_la_calculadora/raw/sonido4")),
        BotonModelo("boton_5", "5", OperacionesAritmeticas.Resultado, "=", Uri.parse("android.resource://mx.docentes.uacj.dora_la_calculadora/raw/sonido5")),
        BotonModelo("boton_4", "4", sonido = Uri.parse("android.resource://mx.docentes.uacj.dora_la_calculadora/raw/sonido6")),
    ),
    arrayOf(
        BotonModelo("boton_3", "3", OperacionesAritmeticas.Suma, "+", Uri.parse("android.resource://mx.docentes.uacj.dora_la_calculadora/raw/sonido7")),
        BotonModelo("boton_2", "2", sonido = Uri.parse("android.resource://mx.docentes.uacj.dora_la_calculadora/raw/sonido8")),
        BotonModelo("boton_1", "1", OperacionesAritmeticas.Resta, "-", Uri.parse("android.resource://mx.docentes.uacj.dora_la_calculadora/raw/sonido9")),
    ),
    arrayOf(
        BotonModelo("boton_punto", ".", sonido = Uri.parse("android.resource://mx.docentes.uacj.dora_la_calculadora/raw/sonido10")),
        BotonModelo("boton_0", "0", sonido = Uri.parse("android.resource://mx.docentes.uacj.dora_la_calculadora/raw/sonido11")),
        BotonModelo("boton_operacion", "OP", sonido = Uri.parse("android.resource://mx.docentes.uacj.dora_la_calculadora/raw/sonido12")),
    )
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Dora_la_calculadoraTheme {
                Calculadora(modifier = Modifier.fillMaxSize(), contexto = this)
            }
        }
    }
}

@Composable
fun Calculadora(modifier: Modifier, contexto: Context? = null) {
    var pantalla_calculadora = remember { mutableStateOf("0") }
    var numero_anterior = remember { mutableStateOf("0") }
    var estado_de_la_calculadora = remember { mutableStateOf(EstadosCalculadora.CuandoEstaEnCero) }
    var operacion_seleccionada = remember { mutableStateOf(OperacionesAritmeticas.Ninguna) }

    fun pulsar_boton(boton: BotonModelo){
        Log.v("BOTONES_INTERFAZ", "Se ha pulsado el boton ${boton.id} de la interfaz")
        Log.v("OPERACION_SELECCIONADA", "La operacion seleccionada es ${operacion_seleccionada.value}")

        when(estado_de_la_calculadora.value){
            EstadosCalculadora.CuandoEstaEnCero -> {
                if(boton.id == "boton_0"){
                    return
                }
                else if(boton.id == "boton_punto"){
                    pantalla_calculadora.value = pantalla_calculadora.value + boton.numero
                    return
                }

                pantalla_calculadora.value = boton.numero
                estado_de_la_calculadora.value = EstadosCalculadora.AgregandoNumeros

            }

            EstadosCalculadora.AgregandoNumeros -> {
                if(boton.id == "boton_operacion"){
                    estado_de_la_calculadora.value = EstadosCalculadora.SeleccionadoOperacion
                    return
                }

                pantalla_calculadora.value = pantalla_calculadora.value + boton.numero
            }

            EstadosCalculadora.SeleccionadoOperacion -> {
                if(     boton.operacion_aritmetica != OperacionesAritmeticas.Ninguna &&
                    boton.operacion_aritmetica != OperacionesAritmeticas.Resultado
                ){
                    operacion_seleccionada.value = boton.operacion_aritmetica
                    estado_de_la_calculadora.value = EstadosCalculadora.CuandoEstaEnCero

                    numero_anterior.value = pantalla_calculadora.value

                    pantalla_calculadora.value = "0"
                    return
                }
                // Aqui imprimimos el resultado
                else if(boton.operacion_aritmetica == OperacionesAritmeticas.Resultado &&
                    operacion_seleccionada.value != OperacionesAritmeticas.Ninguna){

                    when(operacion_seleccionada.value){

                        OperacionesAritmeticas.Suma -> {
                            pantalla_calculadora.value = numero_anterior.value + "+" + pantalla_calculadora.value
                        }
                        OperacionesAritmeticas.Resta -> {
                            pantalla_calculadora.value = numero_anterior.value + "-" + pantalla_calculadora.value
                        }
                        OperacionesAritmeticas.Multiplicacion -> {
                            pantalla_calculadora.value = numero_anterior.value + "*" + pantalla_calculadora.value
                        }
                        OperacionesAritmeticas.Division -> {
                            pantalla_calculadora.value = numero_anterior.value + "/" + pantalla_calculadora.value
                        }

                        else -> {}
                    }


                    estado_de_la_calculadora.value = EstadosCalculadora.MostrandoResultado
                    return
                }

                estado_de_la_calculadora.value = EstadosCalculadora.AgregandoNumeros
            }


            EstadosCalculadora.MostrandoResultado -> {
                numero_anterior.value  = ""

                pantalla_calculadora.value = "0"

                estado_de_la_calculadora.value = EstadosCalculadora.CuandoEstaEnCero
            }
        }
    }


    Column(modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "${pantalla_calculadora.value}", modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .fillMaxHeight(0.33f)
            .background(Color.Blue)
            .height(50.dp),
            textAlign = TextAlign.Right,
            color = Color.White,
            fontSize = 56.sp
        )

        // Deberia jugar mas con el estilo de aqui
        Column(modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray)) {
            for(fila_de_botones in hileras_de_botones_a_dibujar){
                Row(horizontalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier.fillMaxWidth()) {
                    for(boton_a_dibujar in fila_de_botones){
                        when(estado_de_la_calculadora.value){
                            EstadosCalculadora.SeleccionadoOperacion -> {
                                Boton(boton_a_dibujar.operacion_a_mostrar, alPulsar = {
                                    pulsar_boton(boton_a_dibujar)
                                }, contexto = contexto, sonido = boton_a_dibujar.sonido)
                            }
                            else -> {
                                Boton(boton_a_dibujar.numero, alPulsar = {
                                    pulsar_boton(boton_a_dibujar)
                                }, contexto = contexto, sonido = boton_a_dibujar.sonido)
                            }
                        }

                    }
                }
            }
        }
    }


}

@Composable
fun Boton(etiqueta: String, sonido: Uri? = null, alPulsar: () -> Unit = {}, contexto: Context? = null){
    fun reproducir_sonido(){
        if(sonido != null && contexto != null){
            val reproductor_audio: MediaPlayer = MediaPlayer.create(contexto, sonido)

            reproductor_audio.start()
        }

    }

    Button(onClick = { alPulsar(); reproducir_sonido() }, modifier = Modifier
        .fillMaxHeight(0.2F)
        .shadow(elevation = 10.dp, shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))) {
        // https://developer.android.com/develop/ui/compose/layouts/basics
        Box{
            Image(
                painter = painterResource(R.drawable.images),
                contentDescription = "Una foto de perfil del conde de contar",
                modifier = Modifier.size(25.dp)
            )

            Text(
                etiqueta, modifier = Modifier,
                textAlign = TextAlign.Center,
                color = Color.White,
                style = TextStyle(
                    shadow = Shadow(
                        color = Color.Black,
                        offset = Offset(2f, 2f),
                        blurRadius = 4f)
                )
            )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Dora_la_calculadoraTheme {
        Calculadora(modifier = Modifier.fillMaxSize())
    }
}

@Preview(showBackground = true)
@Composable
fun mostrar_boton(){
    Dora_la_calculadoraTheme {
        Boton("4")
    }
}