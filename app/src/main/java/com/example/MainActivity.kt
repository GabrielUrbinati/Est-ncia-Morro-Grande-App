package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.auth.LoginScreen
import com.example.ui.auth.RegisterScreen
import com.example.ui.emergency.EmergencyQuickAccessScreen
import com.example.ui.lgpd.LgpdTermsDialog
import com.example.ui.patient.PatientHomeScreen
import com.example.ui.patient.PatientIntercorrenciasScreen
import com.example.ui.patient.PatientLecturesScreen
import com.example.ui.patient.PatientNonVerbalScreen
import com.example.ui.patient.PatientWalletScreen
import com.example.ui.professional.ProfessionalHomeScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.GradivaScreen
import com.example.ui.viewmodel.GradivaViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: GradivaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    GradivaApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun GradivaApp(viewModel: GradivaViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    when (uiState.currentScreen) {
        is GradivaScreen.Login -> LoginScreen(viewModel = viewModel)
        is GradivaScreen.Register -> RegisterScreen(viewModel = viewModel)
        is GradivaScreen.EmergencyQuickAccess -> EmergencyQuickAccessScreen(viewModel = viewModel)
        is GradivaScreen.PatientHome -> PatientHomeScreen(viewModel = viewModel)
        is GradivaScreen.PatientLectures -> PatientLecturesScreen(viewModel = viewModel)
        is GradivaScreen.PatientIntercorrencias -> PatientIntercorrenciasScreen(viewModel = viewModel)
        is GradivaScreen.PatientWallet -> PatientWalletScreen(viewModel = viewModel)
        is GradivaScreen.PatientNonVerbalCards -> PatientNonVerbalScreen(viewModel = viewModel)
        is GradivaScreen.ProfessionalHome, is GradivaScreen.ProfessionalPatientDetail -> ProfessionalHomeScreen(viewModel = viewModel)
        else -> LoginScreen(viewModel = viewModel)
    }

    if (uiState.showLgpdDialog) {
        LgpdTermsDialog(
            topic = uiState.lgpdDialogType,
            onDismiss = { viewModel.dismissLgpdModal() }
        )
    }
}

