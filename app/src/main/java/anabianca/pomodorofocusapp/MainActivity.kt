package anabianca.pomodorofocusapp

import anabianca.pomodorofocusapp.databinding.DialogSetTimerBinding
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import android.media.MediaPlayer

class MainActivity : AppCompatActivity() {

        private lateinit var circleView: CountdownCircleView
        private lateinit var startButton: ImageView
        private lateinit var stopButton: ImageView
        private lateinit var resetButton: ImageView
        private lateinit var setTimerButton: ImageView
        private lateinit var auth: FirebaseAuth
        private lateinit var db: FirebaseFirestore

        private var totalTime = 25 * 60 * 1000L // 25 minutos em milissegundos
        private val interval = 1000L // Atualiza a cada 1 segundo

        private var timer: CountDownTimer? = null
        private var timeRemaining = totalTime // Tempo restante
        private var isTimerRunning = false

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)


            val btnNote: ImageView = findViewById(R.id.btnNote)

            // Clique no botão para abrir a NoteActivity
            btnNote.setOnClickListener {
                val intent = Intent(this, NoteActivity::class.java)
                startActivity(intent)
            }

            // Inicializa Firebase
            auth = Firebase.auth
            db = Firebase.firestore

            // Verifica se usuário está logado
            if (auth.currentUser == null) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                return
            }

            circleView = findViewById(R.id.circleView)
            startButton = findViewById(R.id.btnStart)
            stopButton = findViewById(R.id.btnStop)
            resetButton = findViewById(R.id.btnReset)
            setTimerButton = findViewById(R.id.btnSetTimer)
             // botao pra trocar o tempo

            val logoutButton = findViewById<ImageView>(R.id.btnLogout) // Corrigimos apenas o ID

            updateButtonStates()
            updateTimerText(totalTime)

            startButton.setOnClickListener {
                startTimer()
            }

            stopButton.setOnClickListener {
                stopTimer()
            }

            resetButton.setOnClickListener {
                resetTimer()
            }

            // Configurar tempo personalizado
            setTimerButton.setOnClickListener {
                showSetTimerDialog()
            }

            // Configurar logout
            logoutButton.setOnClickListener {
                signOut()
            }

        }
        private fun startTimer() {
            timer = object : CountDownTimer(timeRemaining, interval) {
                override fun onTick(millisUntilFinished: Long) {
                    timeRemaining = millisUntilFinished
                    val progressFraction = millisUntilFinished / totalTime.toFloat()
                    circleView.updateProgress(progressFraction)
                    updateTimerText(millisUntilFinished)

                }

                override fun onFinish() {
                    circleView.updateProgress(0f)
                    updateTimerText(0)
                    isTimerRunning = false
                    updateButtonStates()
                    Toast.makeText(this@MainActivity, "Tempo finalizado!", Toast.LENGTH_SHORT).show()
                }
            }.start()

            isTimerRunning = true
            updateButtonStates()
        }

        private fun stopTimer() {
            timer?.cancel()
            isTimerRunning = false
            updateButtonStates()
        }

        private fun resetTimer() {
            stopTimer()
            timeRemaining = totalTime
            circleView.updateProgress(1f)
            updateTimerText(totalTime)
            updateButtonStates()
        }

        private fun updateTimerText(millisRemaining: Long) {
            val minutes = (millisRemaining / 1000) / 60
            val seconds = (millisRemaining / 1000) % 60
            val timeText = String.format("%02d:%02d", minutes, seconds)
            circleView.updateTimerText(timeText)
        }

        private fun updateButtonStates() {
            startButton.isEnabled = !isTimerRunning
            stopButton.isEnabled = isTimerRunning
            resetButton.isEnabled = timeRemaining < totalTime || !isTimerRunning
        }
    private fun showSetTimerDialog() {
        val dialogBinding = DialogSetTimerBinding.inflate(LayoutInflater.from(this))
        val dialog = AlertDialog.Builder(this)
            .setTitle("Set the time")
            .setView(dialogBinding.root)
            .setPositiveButton("Confirm") { _, _ ->
                val minutesInput = dialogBinding.etMinutes.text.toString()
                if (minutesInput.isNotEmpty()) {
                    val minutes = minutesInput.toLongOrNull()
                    if (minutes != null && minutes > 0) {
                        totalTime = minutes * 60 * 1000L
                        timeRemaining = totalTime
                        resetTimer()
                        Toast.makeText(this, "Tempo atualizado para $minutes minutos", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Por favor, insira um número válido.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

        // Função para fazer logout
        private fun signOut() {
            auth.signOut() // Faz logout do Firebase
            Toast.makeText(this, "Você saiu!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java)) // Redireciona para a página de login
            finish() // Encerra a atividade atual
        }
    }