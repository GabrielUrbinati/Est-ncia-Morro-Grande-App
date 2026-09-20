package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.MessageDigest

@Database(
    entities = [
        UserEntity::class,
        LgpdConsentEntity::class,
        MedicalRecordEntity::class,
        CrisisEvolutionEntity::class,
        TherapeuticLectureEntity::class,
        LectureAttendanceEntity::class,
        IntercorrenciaEntity::class,
        PatientWalletTransactionEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun lgpdConsentDao(): LgpdConsentDao
    abstract fun medicalRecordDao(): MedicalRecordDao
    abstract fun crisisEvolutionDao(): CrisisEvolutionDao
    abstract fun lectureDao(): TherapeuticLectureDao
    abstract fun attendanceDao(): LectureAttendanceDao
    abstract fun intercorrenciaDao(): IntercorrenciaDao
    abstract fun walletDao(): PatientWalletDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private const val DB_NAME = "morro_grande_v1_database"

        fun hashPassword(password: String): String {
            val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val appContext = context.applicationContext
                val instance = Room.databaseBuilder(
                    appContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                .fallbackToDestructiveMigration(dropAllTables = true)
                .fallbackToDestructiveMigrationOnDowngrade(dropAllTables = true)
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialHospitalData(database)
                    }
                }
            }
        }

        suspend fun populateInitialHospitalData(db: AppDatabase) {
            try {
                val userDao = db.userDao()
                val medicalDao = db.medicalRecordDao()
                val consentDao = db.lgpdConsentDao()
                val evolutionDao = db.crisisEvolutionDao()
                val lectureDao = db.lectureDao()
                val attendanceDao = db.attendanceDao()
                val intercorrenciaDao = db.intercorrenciaDao()
                val walletDao = db.walletDao()

                if (userDao.getUserCount() > 0) return

            // 1. Seed Patient (Beatriz Helena de Souza)
            val patient = UserEntity(
                id = 1,
                name = "Beatriz Helena de Souza",
                cpf = "123.456.789-00",
                birthDate = "14/05/1996",
                email = "paciente@morrogrande.com.br",
                phone = "(11) 98765-4321",
                passwordHash = hashPassword("123456"),
                role = "PATIENT",
                recordId = "EMG-2026-4081"
            )
            val pId = userDao.insertUser(patient)

            // 2. Seed Patient LGPD Consents
            consentDao.insertConsent(
                LgpdConsentEntity(
                    userId = pId,
                    userEmail = "paciente@morrogrande.com.br",
                    consentType = "TERMS_OF_USE",
                    termVersion = "v3.1-2026.MorroGrande",
                    purpose = "Termos Gerais de Acompanhamento Terapêutico Estância Morro Grande"
                )
            )
            consentDao.insertConsent(
                LgpdConsentEntity(
                    userId = pId,
                    userEmail = "paciente@morrogrande.com.br",
                    consentType = "PRIVACY_POLICY",
                    termVersion = "v3.1-2026.MorroGrande",
                    purpose = "Política de Privacidade e Proteção de Dados Estância Morro Grande"
                )
            )
            consentDao.insertConsent(
                LgpdConsentEntity(
                    userId = pId,
                    userEmail = "paciente@morrogrande.com.br",
                    consentType = "SENSITIVE_HEALTH_DATA_ART11",
                    termVersion = "v3.1-2026.MorroGrande",
                    purpose = "Autorização Específica para Tratamento de Dados de Saúde Mental e Acompanhamento Clínico (Art. 11 LGPD)"
                )
            )

            // 3. Seed Medical Record
            val medicalRecord = MedicalRecordEntity(
                id = 1,
                userId = pId,
                patientName = "Beatriz Helena de Souza",
                recordId = "EMG-2026-4081",
                bloodType = "O+",
                severeAllergies = "HALOPERIDOL (Distonia severa) | DIPIRONA (Choque anafilático)",
                psychiatricDiagnoses = "F31.2 Transtorno Afetivo Bipolar em Acompanhamento Terapêutico e Reabilitação Psicossocial",
                continuousMedication = "Carbonato de Lítio 300mg 12/12h; Quetiapina 50mg à noite",
                rescueProtocolMedication = "Clonazepam 0.5mg VO SOS sob autorização médica em episódios agudos de ansiedade",
                knownTriggers = "Ambientes com ruído estridente, privação de sono, cobranças abruptas",
                calmingTechniques = "Caminhada na trilha verde da estância, respiração guiada, acolhimento com a psicóloga",
                emergencyPhoneSamu = "(11) 4617-8000",
                emergencyPhoneHospital = "(11) 4617-8000",
                familyContactName = "Mariana de Souza",
                familyContactRelationship = "Mãe / Responsável Legal",
                familyContactPhone = "(11) 98765-4321",
                attendingDoctorName = "Dr. Alexandre Castro",
                attendingDoctorCrm = "CRM-SP 148.290",
                attendingDoctorPhone = "(11) 99123-8877",
                nonVerbalAssistiveNote = "Paciente em plano terapêutico individualizado na Estância Morro Grande."
            )
            medicalDao.insertOrUpdateRecord(medicalRecord)

            // 4. Seed Professional: Psychologist (Dra. Camila Rodrigues)
            val psychologist = UserEntity(
                id = 2,
                name = "Dra. Camila Rodrigues",
                cpf = "456.789.123-22",
                birthDate = "10/11/1988",
                email = "psicologa@morrogrande.com.br",
                phone = "(11) 99234-5566",
                passwordHash = hashPassword("123456"),
                role = "PROFESSIONAL",
                profession = "Psicólogo",
                councilType = "CRP",
                councilNumber = "06/142980",
                councilState = "SP"
            )
            userDao.insertUser(psychologist)

            // 5. Seed Professional: Psychiatrist (Dr. Alexandre Castro)
            val doctor = UserEntity(
                id = 3,
                name = "Dr. Alexandre Castro",
                cpf = "321.654.987-11",
                birthDate = "22/08/1982",
                email = "medico@morrogrande.com.br",
                phone = "(11) 99123-8877",
                passwordHash = hashPassword("123456"),
                role = "PROFESSIONAL",
                profession = "Médico",
                councilType = "CRM",
                councilNumber = "148290",
                councilState = "SP"
            )
            userDao.insertUser(doctor)

            // 6. Seed Therapeutic Lectures / Workshops
            val l1 = TherapeuticLectureEntity(
                id = 1,
                title = "Manejo da Ansiedade e Habilidades de Enfrentamento",
                speaker = "Psicóloga Dra. Camila Rodrigues (CRP 06/142980)",
                scheduleTime = "Segunda-feira, 10h00",
                location = "Salão Terapêutico Morro Grande",
                category = "PREVENCAO_RECAIDA",
                description = "Estratégias cognitivo-comportamentais práticas para regulação emocional e manejo da fissura e gatilhos cotidianos."
            )
            val l2 = TherapeuticLectureEntity(
                id = 2,
                title = "Grupo Terapêutico: Os 12 Passos e Autonomia Pessoal",
                speaker = "Terapeuta Marcos Vinicius",
                scheduleTime = "Terça-feira, 15h30",
                location = "Espaço Terapêutico Jardim Verde",
                category = "GRUPO_TERAPEUTICO",
                description = "Reunião de partilha guiada, princípios de espiritualidade prática e reconstrução de projetos de vida."
            )
            val l3 = TherapeuticLectureEntity(
                id = 3,
                title = "Oficina de Psicoeducação: Comunicação Não-Violenta e Família",
                speaker = "Psicóloga Dra. Juliana Prado (CRP 06/158220)",
                scheduleTime = "Quinta-feira, 14h00",
                location = "Auditório Central Estância Morro Grande",
                category = "PSICOEDUCACAO",
                description = "Compreensão de dinâmicas relacionais, expressão clara de sentimentos e resolução pacífica de conflitos."
            )
            val l4 = TherapeuticLectureEntity(
                id = 4,
                title = "Arteterapia e Mindfulness ao Ar Livre",
                speaker = "Terapeuta Ocupacional Beatriz Martins (CREFITO 3/99214)",
                scheduleTime = "Sexta-feira, 09h30",
                location = "Lago e Bosque da Estância",
                category = "ARTETERAPIA",
                description = "Pintura, modelagem e exercícios sensoriais em contato direto com a natureza de Morro Grande."
            )
            lectureDao.insertLecture(l1)
            lectureDao.insertLecture(l2)
            lectureDao.insertLecture(l3)
            lectureDao.insertLecture(l4)

            // 7. Seed Lecture Attendance History for Beatriz
            attendanceDao.insertAttendance(
                LectureAttendanceEntity(
                    patientUserId = pId,
                    patientName = "Beatriz Helena de Souza",
                    lectureId = 1,
                    lectureTitle = "Manejo da Ansiedade e Habilidades de Enfrentamento",
                    dateStr = "18/09/2026",
                    status = "PRESENTE",
                    notes = "Participou ativamente, compartilhou estratégias de respiração e anotou os pontos no caderno terapêutico.",
                    registeredBy = "Dra. Camila Rodrigues (Psicóloga)",
                    timestamp = System.currentTimeMillis() - 86400000L * 2
                )
            )
            attendanceDao.insertAttendance(
                LectureAttendanceEntity(
                    patientUserId = pId,
                    patientName = "Beatriz Helena de Souza",
                    lectureId = 4,
                    lectureTitle = "Arteterapia e Mindfulness ao Ar Livre",
                    dateStr = "19/09/2026",
                    status = "PRESENTE",
                    notes = "Excelente integração com os demais colegas no bosque; concluiu trabalho em aquarela demonstrando serenidade.",
                    registeredBy = "Beatriz Martins (Terapeuta Ocupacional)",
                    timestamp = System.currentTimeMillis() - 86400000L * 1
                )
            )
            attendanceDao.insertAttendance(
                LectureAttendanceEntity(
                    patientUserId = pId,
                    patientName = "Beatriz Helena de Souza",
                    lectureId = 2,
                    lectureTitle = "Grupo Terapêutico: Os 12 Passos e Autonomia Pessoal",
                    dateStr = "15/09/2026",
                    status = "JUSTIFICADO",
                    notes = "Ausência justificada devido a consulta médica e realização de exames laboratoriais no ambulatório.",
                    registeredBy = "Dra. Camila Rodrigues (Psicóloga)",
                    timestamp = System.currentTimeMillis() - 86400000L * 5
                )
            )

            // 8. Seed Intercorrências Clínicas & Terapêuticas
            intercorrenciaDao.insertIntercorrencia(
                IntercorrenciaEntity(
                    patientUserId = pId,
                    patientName = "Beatriz Helena de Souza",
                    title = "Ansiedade Noturna e Insônia Transitória",
                    severity = "LEVE",
                    description = "Paciente relatou inquietação ao deitar por volta das 22h, com pensamentos acelerados sobre a família.",
                    conductAdopted = "Atendimento pela Psicóloga Camila: realizada escuta acolhedora, exercício de respiração 4-7-8 e oferecido chá de camomila. Paciente adormeceu tranquilamente às 23h20.",
                    vitalSigns = "PA 118/75 mmHg | FC 76 bpm | SatO2 99%",
                    professionalName = "Dra. Camila Rodrigues",
                    professionalRole = "Psicóloga",
                    timestamp = System.currentTimeMillis() - 86400000L * 3,
                    resolved = true
                )
            )
            intercorrenciaDao.insertIntercorrencia(
                IntercorrenciaEntity(
                    patientUserId = pId,
                    patientName = "Beatriz Helena de Souza",
                    title = "Desconforto com Barulho na Área Comum",
                    severity = "LEVE",
                    description = "Apresentou sobrecarga sensorial durante manutenção do gramado externo.",
                    conductAdopted = "Conduzida à sala de leitura e acolhimento silencioso. Orientada a utilizar fone com ruído branco suave até término do trabalho externo.",
                    vitalSigns = "PA 120/80 mmHg | FC 80 bpm",
                    professionalName = "Enf. Luciana Mendes",
                    professionalRole = "Equipe de Enfermagem",
                    timestamp = System.currentTimeMillis() - 86400000L * 1,
                    resolved = true
                )
            )

            // 9. Seed Patient Wallet Transactions (Saldo, Depósitos da Família e Gastos da Cantina)
            walletDao.insertTransaction(
                PatientWalletTransactionEntity(
                    patientUserId = pId,
                    patientName = "Beatriz Helena de Souza",
                    type = "DEPOSITO",
                    amount = 300.00,
                    description = "Depósito mensal da família (Mariana de Souza)",
                    category = "DEPOSITO_FAMILIA",
                    registeredBy = "Administração Estância Morro Grande",
                    timestamp = System.currentTimeMillis() - 86400000L * 10
                )
            )
            walletDao.insertTransaction(
                PatientWalletTransactionEntity(
                    patientUserId = pId,
                    patientName = "Beatriz Helena de Souza",
                    type = "GASTO",
                    amount = 24.50,
                    description = "Cantina: 2 Águas de coco geladas, suco de uva integral e cookies integrais",
                    category = "CANTINA",
                    registeredBy = "Cantina Morro Grande",
                    timestamp = System.currentTimeMillis() - 86400000L * 8
                )
            )
            walletDao.insertTransaction(
                PatientWalletTransactionEntity(
                    patientUserId = pId,
                    patientName = "Beatriz Helena de Souza",
                    type = "GASTO",
                    amount = 38.00,
                    description = "Farmácia / Higiene: Sabonete hidratante, hidratante labial e escova dental",
                    category = "HIGIENE_PESSOAL",
                    registeredBy = "Recepção / Farmácia Interna",
                    timestamp = System.currentTimeMillis() - 86400000L * 6
                )
            )
            walletDao.insertTransaction(
                PatientWalletTransactionEntity(
                    patientUserId = pId,
                    patientName = "Beatriz Helena de Souza",
                    type = "DEPOSITO",
                    amount = 150.00,
                    description = "Depósito complementar via PIX pela família",
                    category = "DEPOSITO_FAMILIA",
                    registeredBy = "Administração Estância Morro Grande",
                    timestamp = System.currentTimeMillis() - 86400000L * 4
                )
            )
            walletDao.insertTransaction(
                PatientWalletTransactionEntity(
                    patientUserId = pId,
                    patientName = "Beatriz Helena de Souza",
                    type = "GASTO",
                    amount = 45.00,
                    description = "Serviço de autocuidado: Corte e lavagem de cabelo na estância",
                    category = "BARBEARIA",
                    registeredBy = "Setor de Convivência",
                    timestamp = System.currentTimeMillis() - 86400000L * 2
                )
            )
            walletDao.insertTransaction(
                PatientWalletTransactionEntity(
                    patientUserId = pId,
                    patientName = "Beatriz Helena de Souza",
                    type = "GASTO",
                    amount = 18.00,
                    description = "Cantina: Suco natural de maracujá e mix de castanhas",
                    category = "CANTINA",
                    registeredBy = "Cantina Morro Grande",
                    timestamp = System.currentTimeMillis() - 3600000L * 14
                )
            )
            walletDao.insertTransaction(
                PatientWalletTransactionEntity(
                    patientUserId = pId,
                    patientName = "Beatriz Helena de Souza",
                    type = "GASTO",
                    amount = 52.00,
                    description = "Pedido de Mercado feito à Equipe: Bolachas integrais, achocolatado, café solúvel e refrigerante zero",
                    category = "MERCADO",
                    registeredBy = "Equipe de Apoio Morro Grande (Compra em Cidade)",
                    timestamp = System.currentTimeMillis() - 3600000L * 7
                )
            )
            walletDao.insertTransaction(
                PatientWalletTransactionEntity(
                    patientUserId = pId,
                    patientName = "Beatriz Helena de Souza",
                    type = "GASTO",
                    amount = 22.00,
                    description = "Pedido de Tabacaria / Cigarro autorizado: Maço entregue com controle diário de consumo",
                    category = "CIGARRO",
                    registeredBy = "Coordenação de Pátio e Convivência",
                    timestamp = System.currentTimeMillis() - 3600000L * 2
                )
            )
            } catch (e: Throwable) {
                android.util.Log.e("AppDatabase", "Error populating initial data", e)
            }
        }
    }
}

