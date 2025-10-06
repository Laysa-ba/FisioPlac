package com.example.fisioplac

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    // Declaração de variáveis com 'lateinit' pois serão inicializadas no onCreate
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var doctorNameTextView: TextView

    // Lista de especialidades, pode ser nula inicialmente
    private var doctorSpecialties: ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Inicialização das instâncias do Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Referências aos componentes da UI (Views)
        doctorNameTextView = findViewById(R.id.text_view_doctor_name)
        val cardGeriatrica: CardView = findViewById(R.id.card_geriatrica)
        val cardTraumato: CardView = findViewById(R.id.card_traumato_ortopedica)
        val logoutButton: ImageButton = findViewById(R.id.btn_logout)
        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation_bar)

        // Carrega os dados do doutor e ajusta a UI
        loadDoctorProfile(cardGeriatrica, cardTraumato)

        // Define a ação de clique para o botão de logout
        logoutButton.setOnClickListener {
            performLogout()
        }

        // Define a ação de clique para o card de geriatria
        cardGeriatrica.setOnClickListener {
            val intent = Intent(this, GeriatricaActivity::class.java)
            intent.putExtra("ESPECIALIDADE_SELECIONADA", "geriatria")
            startActivity(intent)
        }

        // Define a ação de clique para o card de traumato-ortopédica
        cardTraumato.setOnClickListener {
            // Ação futura: Criar e abrir a TraumatoActivity
            Toast.makeText(this, "Ficha de Traumato-Ortopédica em desenvolvimento.", Toast.LENGTH_SHORT).show()
        }

        // Configuração do menu de navegação inferior
        bottomNav.selectedItemId = R.id.nav_home
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_new_file -> {
                    val intent = Intent(this, NewFileActivity::class.java)
                    intent.putStringArrayListExtra("ESPECIALIDADES_LISTA", doctorSpecialties)
                    startActivity(intent)
                    true
                }
                R.id.nav_home -> true // Já estamos na Home, não faz nada
                R.id.nav_profile -> {
                    Toast.makeText(this, "Perfil clicado (implementação futura)", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }

    /**
     * Carrega o perfil do doutor do Firestore e ajusta a visibilidade dos cards de acesso rápido.
     */
    private fun loadDoctorProfile(cardGeriatrica: CardView, cardTraumato: CardView) {
        // Uso do safe call (?.) para obter o UID de forma segura
        val uid = auth.currentUser?.uid

        if (uid != null) {
            db.collection("doutores").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val doctorName = document.getString("nome")
                        // Uso de string templates para formatar o texto
                        doctorNameTextView.text = "Dr(a). $doctorName"

                        // Cast seguro para a lista de especialidades
                        doctorSpecialties = document.get("especialidades") as? ArrayList<String>

                        // Bloco 'let' para executar código apenas se a lista não for nula
                        doctorSpecialties?.let { specialties ->
                            if (specialties.contains("geriatria")) {
                                cardGeriatrica.visibility = View.VISIBLE
                            }
                            if (specialties.contains("traumatoortopedica")) {
                                cardTraumato.visibility = View.VISIBLE
                            }
                        }
                    } else {
                        doctorNameTextView.text = "Doutor(a) (Dados incompletos)"
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao carregar os dados.", Toast.LENGTH_SHORT).show()
                    doctorNameTextView.text = "Erro de conexão"
                }
        } else {
            // Se não houver usuário logado, volta para a tela de login
            performLogout()
        }
    }

    /**
     * Encerra a sessão do usuário e redireciona para a tela de login.
     */
    private fun performLogout() {
        auth.signOut()
        val intent = Intent(this, MainActivity::class.java)
        // Limpa a pilha de atividades para que o usuário não possa voltar
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}