package com.example.fisioplac

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log // IMPORT NECESSÁRIO
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class GeriatricaActivity : AppCompatActivity() {

    // Instâncias do Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    // Componentes da UI
    private lateinit var cpfEditText: EditText
    private lateinit var patientNameTextView: TextView

    // Variáveis para guardar dados importantes
    private var foundPatientId: String? = null
    private var selectedSpecialty: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_geriatrica)

        // 1. Inicializa o Firebase e os componentes da UI
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        cpfEditText = findViewById(R.id.edit_text_cpf)
        patientNameTextView = findViewById(R.id.text_view_patient_name)

        // 2. Recebe a especialidade da tela anterior
        selectedSpecialty = intent.getStringExtra("ESPECIALIDADE_SELECIONADA")
        if (selectedSpecialty == null) {
            Toast.makeText(this, "Erro: Especialidade não definida.", Toast.LENGTH_LONG).show()
            finish() // Fecha a tela se não houver especialidade
            return
        }

        // 3. Adiciona o "espião" de texto para busca automática
        cpfEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                // Limpa o resultado anterior se o usuário apagar o texto
                if (s.toString().length < 11) {
                    patientNameTextView.visibility = View.GONE
                    foundPatientId = null
                }
                // Dispara a busca automaticamente quando o CPF tiver 11 dígitos
                if (s.toString().length == 11) {
                    searchPatient(s.toString())
                }
            }
        })

        // 4. Adiciona a ação de "selecionar" o paciente quando o nome aparece
        patientNameTextView.setOnClickListener {
            if (foundPatientId != null) {
                Toast.makeText(this, "${patientNameTextView.text} selecionado!", Toast.LENGTH_SHORT).show()
                // Futuramente, aqui você pode habilitar o resto da ficha para preenchimento
            }
        }

        // 5. Configura a navegação
        setupNavigation()
    }

    /**
     * Função principal que busca o paciente no Firebase.
     */
    private fun searchPatient(cpf: String) {
        val cleanedCpf = cpf.replace(Regex("[^0-9]"), "")
        val doctorUid = auth.currentUser?.uid ?: return

        // Esconde o resultado anterior antes de uma nova busca
        patientNameTextView.visibility = View.GONE
        foundPatientId = null

        // Passo 1: Busca na coleção 'pacientes'
        db.collection("pacientes").whereEqualTo("cpf", cleanedCpf).get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(this, "Nenhum paciente encontrado com este CPF.", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                val patientDocument = documents.first()
                val patientId = patientDocument.id
                val patientName = patientDocument.getString("nome")

                // Passo 2: Verifica na coleção 'vinculos'
                db.collection("vinculos")
                    .whereEqualTo("medicoId", doctorUid)
                    .whereEqualTo("pacienteId", patientId)
                    .whereEqualTo("especialidade", selectedSpecialty)
                    .whereEqualTo("ativo", true)
                    .get()
                    .addOnSuccessListener { vinculoDocuments ->
                        if (vinculoDocuments.isEmpty) {
                            Toast.makeText(this, "Paciente não vinculado a você para esta especialidade.", Toast.LENGTH_LONG).show()
                        } else {
                            // Sucesso! Mostra o nome e guarda o ID.
                            patientNameTextView.text = patientName
                            patientNameTextView.visibility = View.VISIBLE
                            foundPatientId = patientId
                        }
                    }
                    .addOnFailureListener { e -> // ALTERADO AQUI
                        Log.e("FIRESTORE_ERROR", "Falha ao verificar vínculo: ", e)
                        Toast.makeText(this, "Erro ao verificar vínculo. Verifique o Logcat.", Toast.LENGTH_LONG).show()
                    }
            }
            .addOnFailureListener { e -> // ALTERADO AQUI
                Log.e("FIRESTORE_ERROR", "Falha ao buscar paciente por CPF: ", e)
                Toast.makeText(this, "Erro ao buscar paciente. Verifique o Logcat.", Toast.LENGTH_LONG).show()
            }
    }

    /**
     * Configura a navegação (botão de voltar e menu inferior).
     */
    private fun setupNavigation() {
        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation_bar)
        val backButton: ImageButton = findViewById(R.id.btn_back)

        bottomNav.selectedItemId = R.id.nav_new_file

        backButton.setOnClickListener {
            finish() // Simplifica a ação de voltar
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    })
                    true
                }
                R.id.nav_new_file -> true
                R.id.nav_profile -> {
                    Toast.makeText(this, "Perfil clicado (implementação futura)", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }
}