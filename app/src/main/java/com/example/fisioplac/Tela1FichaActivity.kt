package com.example.fisioplac // Certifique-se que o package está correto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView

class Tela1FichaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela1_ficha) // Usa o nome do seu arquivo XML

        // --- Configuração do Dropdown de Estado Civil ---

        // 1. Pega a lista de opções do strings.xml
        val opcoesEstadoCivil = resources.getStringArray(R.array.opcoes_estado_civil)
        // 2. Cria o "gerente" (Adapter) que vai mostrar as opções
        val adapterEstadoCivil = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcoesEstadoCivil)
        // 3. Encontra o componente no layout
        val autoCompleteEstadoCivil = findViewById<AutoCompleteTextView>(R.id.actv_estado_civil)
        // 4. Conecta o "gerente" ao componente
        autoCompleteEstadoCivil.setAdapter(adapterEstadoCivil)


        // --- Configuração do Dropdown de Escolaridade ---

        val opcoesEscolaridade = resources.getStringArray(R.array.opcoes_escolaridade)
        val adapterEscolaridade = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcoesEscolaridade)
        val autoCompleteEscolaridade = findViewById<AutoCompleteTextView>(R.id.actv_escolaridade)
        autoCompleteEscolaridade.setAdapter(adapterEscolaridade)
    }
}