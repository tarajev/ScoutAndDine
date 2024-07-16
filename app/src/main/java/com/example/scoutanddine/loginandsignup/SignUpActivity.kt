package com.example.scoutanddine.loginandsignup

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.scoutanddine.MainActivity
import com.example.scoutanddine.data.FirebaseObject
import com.example.scoutanddine.ui.theme.ScoutAndDineTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth

class SignUpActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private var imageUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        setContent {
            ScoutAndDineTheme {
                SignUpScreen(
                    onSignUpClick = { email, password, username, name, phoneNumber, profilePicture ->
                        signUp(email, password, username, name, phoneNumber, profilePicture.toString()) //privremeno
                    },
                    onSignInClick = {
                        val intent = Intent(this, SignInActivity::class.java)
                        startActivity(intent)
                    },
                    onPickImageClick = { pickImageLauncher.launch("image/*") },
                    imageUri = imageUri
                )
            }
        }
    }

    private fun signUp(email: String, password: String, username: String, name: String, phoneNumber : String, profilePicture : Any) {
        auth.createUserWithEmailAndPassword(email.trim(), password.trim())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("com.example.scoutanddine.loginandsignup.SignUpActivity", "createUserWithEmail:success")
                    FirebaseObject.addUser(email,username,name,phoneNumber,name)
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("com.example.scoutanddine.loginandsignup.SignUpActivity", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
@Composable
fun SignUpScreen(
    onSignUpClick: (String, String, String, String, String, Any) -> Unit,
    onSignInClick: () -> Unit,
    onPickImageClick: () -> Unit,
    imageUri: Uri?
) {
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var username by remember { mutableStateOf(TextFieldValue("")) }
    var phoneNumber by remember { mutableStateOf(TextFieldValue("")) }
    var name by remember { mutableStateOf(TextFieldValue("")) }
    var profilePictureUri by remember { mutableStateOf<Uri?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top Bar
        Box(
            modifier = Modifier
                .background(Color(0xFF00573F))
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = "Scout&Dine",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Sign Up",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 24.dp)
                    .align(Alignment.CenterHorizontally)
            )

            // Username Field
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Name Field
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Phone Number Field
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email Field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Upload Image Button
            Button(
                onClick = onPickImageClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00573F)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)

            ) {
                Text(text = "Upload Image", color = Color.White)
            }

            imageUri?.let {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = null,
                    modifier = Modifier.size(128.dp)
                )
            }

            Spacer(modifier = Modifier.height(5.dp))

            // Sign Up Button
            Button(
                onClick = {
                   // profilePictureUri?.let {
                        onSignUpClick(
                            email.text,
                            password.text,
                            username.text,
                            name.text,
                            phoneNumber.text,
                            name.text
                          //  it
                        )
                   // }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00573F)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text(text = "Sign Up", color = Color.White)
            }

            Spacer(modifier = Modifier.height(5.dp))

            // Sign In Link
            TextButton(
                onClick = onSignInClick,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Already have an account? Sign In", color = Color(0xFF00573F))
            }
        }
    }
}
