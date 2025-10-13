package com.example.fisioplac

//noinspection SuspiciousImport
import android.R
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity


class Tela1FichaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela1_ficha) // Use o nome do seu arquivo de layout

        // 1. Pegar a lista de opções do strings.xml
        val estadosCivis = getResources().getStringArray(R.array.opcoes_estado_civil)

        // 2. Criar o Adapter
        val adapter = ArrayAdapter<String?>(this, R.layout.simple_list_item_1, estadosCivis)

        // 3. Encontrar o seu AutoCompleteTextView pelo ID
        val autoCompleteTextView = findViewById<AutoCompleteTextView?>(R.id.actv_estado_civil)

        // 4. Ligar o adapter ao componente
        autoCompleteTextView.setAdapter<ArrayAdapter<String?>?>(adapter)
    }
}