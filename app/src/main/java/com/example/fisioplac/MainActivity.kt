package com.example.fisioplac

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fisioplac.HomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.fisioplac.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Verifica se o usuário já está logado
        if (auth.currentUser != null) {
            // Se já estiver logado, tenta carregar os dados para navegar
            loadDoctorDataAndNavigate()
            return
        }

        binding.buttonLogin.setOnClickListener {
            performLogin()
        }
    }

    private fun performLogin() {
        val cpf = binding.editTextCpf.text.toString().trim()
        val password = binding.editTextSenha.text.toString()

        if (cpf.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Preencha o CPF e a senha.", Toast.LENGTH_SHORT).show()
            return
        }

        val emailForAuth = "$cpf@fisioplac.com"

        auth.signInWithEmailAndPassword(emailForAuth, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login efetuado com sucesso!", Toast.LENGTH_SHORT).show()
                    loadDoctorDataAndNavigate()
                } else {
                    Toast.makeText(this, "Falha na autenticação: Verifique o CPF e a senha.",
                        Toast.LENGTH_LONG).show()
                }
            }
    }

    // Função modificada para corrigir o fluxo de Intent e mostrar o erro real
    private fun loadDoctorDataAndNavigate() {
        val uid = auth.currentUser?.uid

        if (uid == null) {
            Toast.makeText(this, "Erro: UID do usuário não encontrado.", Toast.LENGTH_LONG).show()
            return
        }

        db.collection("doutores").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val doctorName = document.getString("nome") ?: "Doutor(a) Padrão"

                    // ⭐️ CORREÇÃO 1: Cria e usa a Intent correta com o dado
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.putExtra("USER_NAME", doctorName)

                    navigateToHome(intent) // Chama a função de navegação passando a Intent
                } else {
                    Toast.makeText(this, "Erro: Dados do doutor não encontrados no banco.", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                // ⭐️ CORREÇÃO 2: MOSTRA O ERRO REAL DO FIREBASE PARA DIAGNÓSTICO
                Toast.makeText(this, "FALHA DE CONEXÃO/PERMISSÃO: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    // Função de navegação modificada para receber a Intent com dados
    private fun navigateToHome(intent: Intent) {
        startActivity(intent)
        finish()
    }

    // Função auxiliar para o onCreate, no caso de usuário já logado
    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}