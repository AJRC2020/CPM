package org.feup.apm.acme.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import org.feup.apm.acme.*
import kotlin.concurrent.thread

class LoginActivity : AppCompatActivity() {
    private val backButton by lazy { findViewById<ImageButton>(R.id.loginBackButton)}
    private val progressBar by lazy { findViewById<ProgressBar>(R.id.progressBarLogin)}
    private val loginButton by lazy { findViewById<Button>(R.id.loginConfirmButton)}
    private val loginNicknameFieldInput by lazy { findViewById<EditText>(R.id.loginNicknameFieldInput)}
    private val loginPasswordFieldInput by lazy { findViewById<EditText>(R.id.loginPasswordFieldInput)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        checkIfLoggedIn(this)

        loginButton.setOnClickListener {
            if (loginNicknameFieldInput.text.toString().trim().isNotEmpty() &&
                loginPasswordFieldInput.text.toString().trim().isNotEmpty()
            ) {
                loading(progressBar, listOf(loginButton))
                val username = loginNicknameFieldInput.text.toString()
                val password = loginPasswordFieldInput.text.toString()
                if (keysPresent(username)) {
                    thread {
                        val result = login(this, username, password)
                        if (result) {
                            val uuid = getUUID(this, username)
                            if (uuid) {
                                this.runOnUiThread {
                                    stopLoading(progressBar, listOf(loginButton))

                                    val intent = Intent(this, UserProfile::class.java)
                                    startActivity(intent)


                                }
                            } else {
                                this.runOnUiThread {
                                    stopLoading(progressBar, listOf(loginButton))
                                    createSnackBar("Could not retrieve user id, please retry", this)
                                }
                            }
                        } else {
                            this.runOnUiThread {
                                stopLoading(progressBar, listOf(loginButton))
                                createSnackBar(
                                    "Error logging in, please make sure your credentials are correct",
                                    this
                                )
                            }
                        }


                    }

                } else {
                    createSnackBar(
                        "This device is unable to log into this account, please make sure you are using the device you registered your account in",
                        this
                    )
                }
            }else {
                    createSnackBar("Nickname and password fields can't be empty", this)
                }

        }

        backButton.setOnClickListener {
            finish()
        }
    }
}