package com.example.fisioplac

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView // Novo import
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth // Novo import
import com.google.firebase.firestore.FirebaseFirestore // Novo import

class HomeActivity : AppCompatActivity() {

    // Instâncias do Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    // TextView para exibir o nome
    private lateinit var doctorNameTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // 1. Inicializa o Firebase e Views
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Assumindo que você tem um TextView com este ID no seu activity_home.xml
        doctorNameTextView = findViewById(R.id.text_view_doctor_name)

        // 2. Implementação do Nome
        loadDoctorName()

        // 3. Botão de Logout (Canto Superior Direito)
        val logoutButton: ImageButton = findViewById(R.id.btn_logout)
        logoutButton.setOnClickListener {
            performLogout() // Chama a nova função de logout
        }

        // 4. Acesso Rápido - Ficha Geriátrica
        val cardGeriatrica: CardView = findViewById(R.id.card_geriatrica)
        cardGeriatrica.setOnClickListener {
            val intent = Intent(this, GeriatricaActivity::class.java)
            startActivity(intent)
        }

        // 5. Menu de Navegação Inferior
        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation_bar)
        bottomNav.selectedItemId = R.id.nav_home

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_new_file -> {
                    val intent = Intent(this, NewFileActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_home -> true
                R.id.nav_profile -> {
                    Toast.makeText(this, "Perfil clicado (implementação futura)", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }

    /**
     * Carrega o nome do doutor a partir do Firestore e atualiza o TextView.
     */
    private fun loadDoctorName() {
        val uid = auth.currentUser?.uid

        if (uid != null) {
            // Coleção 'doctors', documento é o UID
            db.collection("doutores").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // O nome do campo no banco é 'nome'
                        val doctorName = document.getString("nome")

                        if (doctorName != null) {
                            // Atualiza o TextView com o nome do doutor
                            doctorNameTextView.text = "Dr(a). $doctorName"
                        } else {
                            // Caso o campo 'nome' não exista
                            doctorNameTextView.text = "Doutor(a) (Nome não encontrado)"
                        }
                    } else {
                        doctorNameTextView.text = "Doutor(a) (Dados incompletos)"
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao carregar o nome.", Toast.LENGTH_SHORT).show()
                    doctorNameTextView.text = "Erro de conexão"
                }
        } else {
            // Redireciona para o login se não houver UID (deveria ter sido pego na MainActivity)
            performLogout()
        }
    }

    //Encerra a sessão do Firebase e redireciona para a tela de Login.

    private fun performLogout() {
        // Encerra a sessão atual do Firebase Auth
        auth.signOut()

        // Redireciona para a tela de Login
        val intent = Intent(this, MainActivity::class.java)

        // Flags para limpar a pilha de activities e garantir que o usuário não volte para a Home
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()

        Toast.makeText(this, "Sessão encerrada.", Toast.LENGTH_SHORT).show()
    }
}