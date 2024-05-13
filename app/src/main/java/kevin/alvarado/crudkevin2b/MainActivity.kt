package kevin.alvarado.crudkevin2b

import Modelo.ClaseConexion
import Modelo.dataClassMascotas
import RecyclerViewHelper.Adaptador
import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kevin.alvarado.crudkevin2b.R.id.rcvMascotas
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //1- Mandar a llamar a todos los elementos
        val txtNombre = findViewById<EditText>(R.id.txtNombre)
        val txtPeso = findViewById<EditText>(R.id.txtPeso)
        val txtEdad = findViewById<EditText>(R.id.txtEdad)
        val btnAgregar = findViewById<Button>(R.id.btnAgregar)
        val rcvMascotas = findViewById<RecyclerView>(R.id.rcvMascotas)

        //Primer paso para mostrar datos
        //Asignarle un layout al RCV
        rcvMascotas.layoutManager = LinearLayoutManager(this)

        ////// TODO: MOSTRAR DATOS //////

        fun obtenerDatos(): List<dataClassMascotas> {
            //1- Crea un objeto de la clase conexion
            val objConexion = ClaseConexion().cadenaConexion()

            //2- Creo un Statement
            val statement = objConexion?.createStatement()
            val resulSet = statement?.executeQuery("select * from tbMascota")!!

            val mascotas = mutableListOf<dataClassMascotas>()

            //Recorro todos los registros de la base de datos
            while (resulSet.next()) {
                val nombre = resulSet.getString("nombreMascota")
                val mascota = dataClassMascotas(nombre)
                mascotas.add(mascota)
            }
            return mascotas
        }

        //Asignar el adaptador al ReycleView
        CoroutineScope(Dispatchers.IO).launch {
            val mascotaDB = obtenerDatos()
            withContext(Dispatchers.Main){
                val adapter = Adaptador(mascotaDB)
                rcvMascotas.adapter = adapter
            }
        }







        //2- programar el boton para agregar
        btnAgregar.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                //1- Creo un objeto dentro de la clase conexion
                val objConexion = ClaseConexion().cadenaConexion()

                //2- Creo una variable que contenga un PrepareStatement
                val addMascota = objConexion?.prepareStatement("insert into tbMascota values(?, ?, ?)")!!
                addMascota.setString(1, txtNombre.text.toString())
                addMascota.setInt(2, txtPeso.text.toString().toInt())
                addMascota.setInt(3, txtEdad.text.toString().toInt())
                addMascota.executeUpdate()

                //Refresco la lista
                val nuevasMascotas = obtenerDatos()
                withContext(Dispatchers.Main){
                    (rcvMascotas.adapter as? Adaptador)?.actualizarLista(nuevasMascotas)
                }
            }
        }

    }
}