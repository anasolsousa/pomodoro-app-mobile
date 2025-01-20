package anabianca.pomodorofocusapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var signUpButton: Button
    private lateinit var loadingProgressBar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializa Firebase
        auth = Firebase.auth
        db = Firebase.firestore

        if (auth.currentUser != null) {

            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }


        emailEditText = findViewById(R.id.editTextEmail)
        passwordEditText = findViewById(R.id.editTextPassword)
        loginButton = findViewById(R.id.buttonLogin)
        signUpButton = findViewById(R.id.buttonSignUp)
        loadingProgressBar = findViewById(R.id.progressBar)

        // Verifica se já está logado
        checkCurrentUser()

        loginButton.setOnClickListener {
            loginUser()
        }

        signUpButton.setOnClickListener {
            registerUser()
        }
    }

    private fun checkCurrentUser() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            navigateToMain()
        }
    }

    private fun loginUser() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (validateInput(email, password)) {
            showLoading(true)

            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    saveUserData(it.user?.uid)
                    showLoading(false)
                    navigateToMain()
                }
                .addOnFailureListener { e ->
                    showLoading(false)
                    showError("Erro no login: ${e.message}")
                }
        }
    }

    private fun registerUser() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (validateInput(email, password)) {
            showLoading(true)

            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    saveUserData(it.user?.uid)
                    showLoading(false)
                    navigateToMain()
                }
                .addOnFailureListener { e ->
                    showLoading(false)
                    showError("Erro no cadastro: ${e.message}")
                }
        }
    }

    private fun saveUserData(userId: String?) {
        userId?.let {
            val user = hashMapOf(
                "email" to emailEditText.text.toString(),
                "createdAt" to FieldValue.serverTimestamp()
            )

            db.collection("users")
                .document(it)
                .set(user)
                .addOnSuccessListener {
                    Log.d("Firebase", "Usuário salvo com sucesso!")
                }
                .addOnFailureListener { e ->
                    Log.w("Firebase", "Erro ao salvar usuário", e)
                }
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            emailEditText.error = "Digite o email"
            return false
        }
        if (password.isEmpty()) {
            passwordEditText.error = "Digite a senha"
            return false
        }
        if (password.length < 6) {
            passwordEditText.error = "A senha deve ter pelo menos 6 caracteres"
            return false
        }
        return true
    }

    private fun showLoading(show: Boolean) {
        loadingProgressBar.visibility = if (show) View.VISIBLE else View.GONE
        loginButton.isEnabled = !show
        signUpButton.isEnabled = !show
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}