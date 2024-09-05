package com.example.scoutanddine.loginandsignup

import android.content.Context
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalContext
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
    private var imageUriState: MutableState<Uri?> = mutableStateOf(null)

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUriState.value = uri
        Log.d("SLIKA", "${imageUriState.value?.encodedPath}")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        setContent {
            val context = LocalContext.current
            ScoutAndDineTheme {
                SignUpScreen(
                    context = context,
                    onSignUpClick = { email, password, username, name, phoneNumber, profilePicture ->
                        signUp(email, password, username, name, phoneNumber, profilePicture)
                    },
                    onSignInClick = {
                        val intent = Intent(this, SignInActivity::class.java)
                        startActivity(intent)
                    },
                    onPickImageClick = { pickImageLauncher.launch("image/*") },
                    imageUriState = imageUriState
                )
            }
        }
    }

    private fun signUp(email: String, password: String, username: String, name: String, phoneNumber : String, profilePicture : Uri) {
        auth.createUserWithEmailAndPassword(email.trim(), password.trim())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("com.example.scoutanddine.loginandsignup.SignUpActivity", "createUserWithEmail:success")
                    FirebaseObject.addUser(email,username,name,phoneNumber,profilePicture,
                        onSuccess = {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    })
                  /*  val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)*/
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
    context: Context,
    onSignUpClick: (String, String, String, String, String, Uri) -> Unit,
    onSignInClick: () -> Unit,
    onPickImageClick: () -> Unit,
    imageUriState: MutableState<Uri?>
) {
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var username by remember { mutableStateOf(TextFieldValue("")) }
    var phoneNumber by remember { mutableStateOf(TextFieldValue("")) }
    var name by remember { mutableStateOf(TextFieldValue("")) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
    ) {
        // Top Bar
        Box(
            modifier = Modifier
                .background(Color(51,204, 255))
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
                modifier = Modifier
                    .padding(bottom = 12.dp)
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
                colors = ButtonDefaults.buttonColors(containerColor = Color(51,204, 255)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text(text = "Upload Image", color = Color.White)
            }

            if (imageUriState.value != null) {
                Text(
                    text = "File uploaded",
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .align(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            } else {
                Text(
                    text = "No file selected",
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .align(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(5.dp))

            // Sign Up Button
            Button(
                onClick = {
                    imageUriState.value?.let { uri ->
                        onSignUpClick(
                            email.text,
                            password.text,
                            username.text,
                            name.text,
                            phoneNumber.text,
                            uri
                        )
                    } ?: run {
                        // Show an error message if imageUri is null
                        Toast.makeText(context, "Please select an image", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(51,204, 255)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                Text(text = "Sign Up", color = Color.White)
            }


            Spacer(modifier = Modifier.height(5.dp))

            // Sign In Link
            TextButton(
                onClick = onSignInClick,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Already have an account? Sign In", color = Color(0,0,0))
            }
        }
    }
}
