package com.example.fisioplac

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class GeriatricaActivity : AppCompatActivity() {

    // Instâncias do Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    // Componentes da UI
    private lateinit var cpfEditText: EditText
    private lateinit var cardPatientInfo: MaterialCardView
    private lateinit var textViewPatientName: TextView
    private lateinit var textViewBirthdate: TextView
    private lateinit var textViewAge: TextView
    private lateinit var textViewSex: TextView
    private lateinit var iniciarFichaButton: Button

    // Variáveis de estado
    private var foundPatientId: String? = null
    private var selectedSpecialty: String? = null
    private var isPatientSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_geriatrica)

        // 1. Inicializa o Firebase e os componentes da UI
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        cpfEditText = findViewById(R.id.edit_text_cpf)
        cardPatientInfo = findViewById(R.id.card_patient_info)
        textViewPatientName = findViewById(R.id.text_view_patient_name)
        textViewBirthdate = findViewById(R.id.text_view_birthdate)
        textViewAge = findViewById(R.id.text_view_age)
        textViewSex = findViewById(R.id.text_view_sex)
        iniciarFichaButton = findViewById(R.id.btn_iniciar_ficha)

        // 2. Recebe a especialidade da tela anterior
        selectedSpecialty = intent.getStringExtra("ESPECIALIDADE_SELECIONADA")
        if (selectedSpecialty == null) {
            Toast.makeText(this, "Erro: Especialidade não definida.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // 3. Adiciona o TextWatcher para busca automática
        cpfEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length < 11) {
                    resetPatientSelection()
                }
                if (s.toString().length == 11) {
                    searchPatient(s.toString())
                }
            }
        })

        // 4. Lógica de clique no card do paciente
        cardPatientInfo.setOnClickListener {
            isPatientSelected = !isPatientSelected

            if (isPatientSelected) {
                cardPatientInfo.setCardBackgroundColor(ContextCompat.getColor(this, R.color.verde_claro))
                iniciarFichaButton.isEnabled = true
            } else {
                cardPatientInfo.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white))
                iniciarFichaButton.isEnabled = false
            }
        }

        // 5. LÓGICA DE CLIQUE NO BOTÃO "INICIAR FICHA" (ATUALIZADA)
        iniciarFichaButton.setOnClickListener {
            if (isPatientSelected && foundPatientId != null) {
                // Cria a intenção de ir para a próxima tela
                val intent = Intent(this, Tela10FichaActivity::class.java)

                // Opcional, mas recomendado: Passa o ID e o nome do paciente para a próxima tela
                intent.putExtra("PACIENTE_ID", foundPatientId)
                intent.putExtra("PACIENTE_NOME", textViewPatientName.text.toString())

                startActivity(intent)
            } else {
                Toast.makeText(this, "Por favor, selecione um paciente primeiro.", Toast.LENGTH_SHORT).show()
            }
        }

        // 6. Configura a navegação
        setupNavigation()
    }

    private fun searchPatient(cpf: String) {
        resetPatientSelection()

        val cleanedCpf = cpf.replace(Regex("[^0-9]"), "")
        val doctorUid = auth.currentUser?.uid ?: return

        db.collection("pacientes").whereEqualTo("cpf", cleanedCpf).get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(this, "Nenhum paciente encontrado com este CPF.", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                val patientDocument = documents.first()
                val patientId = patientDocument.id

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
                            foundPatientId = patientId
                            populatePatientCard(patientDocument)
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("FIRESTORE_ERROR", "Falha ao verificar vínculo: ", e)
                        Toast.makeText(this, "Erro ao verificar vínculo. Verifique o Logcat.", Toast.LENGTH_LONG).show()
                    }
            }
            .addOnFailureListener { e ->
                Log.e("FIRESTORE_ERROR", "Falha ao buscar paciente por CPF: ", e)
                Toast.makeText(this, "Erro ao buscar paciente. Verifique o Logcat.", Toast.LENGTH_LONG).show()
            }
    }

    private fun resetPatientSelection() {
        foundPatientId = null
        isPatientSelected = false
        cardPatientInfo.visibility = View.GONE
        cardPatientInfo.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white))
        iniciarFichaButton.isEnabled = false
    }

    private fun populatePatientCard(document: com.google.firebase.firestore.DocumentSnapshot) {
        val patientName = document.getString("nome") ?: "Nome não encontrado"
        val sex = document.getString("sexo") ?: "Não informado"
        val birthTimestamp = document.getTimestamp("dataNascimento")

        textViewPatientName.text = patientName
        textViewSex.text = "Sexo: $sex"

        if (birthTimestamp != null) {
            val birthDate = birthTimestamp.toDate()
            val sdf = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("pt", "BR"))
            textViewBirthdate.text = "Nascimento: ${sdf.format(birthDate)}"

            val birthCal = Calendar.getInstance()
            birthCal.time = birthDate
            val todayCal = Calendar.getInstance()

            var age = todayCal.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR)
            if (todayCal.get(Calendar.DAY_OF_YEAR) < birthCal.get(Calendar.DAY_OF_YEAR)) {
                age--
            }

            textViewAge.text = "Idade: $age anos"
        } else {
            textViewBirthdate.text = "Nascimento: Não informado"
            textViewAge.text = "Idade: Não informada"
        }

        cardPatientInfo.visibility = View.VISIBLE
    }

    private fun setupNavigation() {
        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation_bar)
        val backButton: ImageButton = findViewById(R.id.btn_back)

        bottomNav.selectedItemId = R.id.nav_new_file

        backButton.setOnClickListener {
            finish()
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