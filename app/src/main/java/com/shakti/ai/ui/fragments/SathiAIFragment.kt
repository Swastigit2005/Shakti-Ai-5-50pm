package com.shakti.ai.ui.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shakti.ai.R
import com.shakti.ai.viewmodel.SathiViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * SathiAIFragment - Mental Health Support Module
 * Interactive implementation with Gemini AI integration via ViewModel
 */
class SathiAIFragment : Fragment() {

    // Use ViewModel for Gemini API integration
    private val viewModel: SathiViewModel by viewModels()

    private lateinit var sharedPreferences: SharedPreferences

    // Tab views
    private lateinit var tabAiCompanion: TextView
    private lateinit var tabMentalHealth: TextView
    private lateinit var tabSupportResources: TextView

    // UI Elements
    private lateinit var messageInput: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var voiceButton: Button
    private lateinit var uploadButton: Button
    private lateinit var breathingButton: Button
    private lateinit var gratitudeButton: Button
    private lateinit var supportGroupButton: Button
    private lateinit var emergencyButton: Button
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var moodScore: TextView
    private lateinit var anxietyScore: TextView
    private lateinit var conversationCount: TextView
    private lateinit var moodProgress: ProgressBar
    private lateinit var anxietyProgress: ProgressBar

    // Media recording
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false
    private var audioFilePath: String? = null

    // Chat messages
    private val chatMessages = mutableListOf<ChatMessage>()
    private lateinit var chatAdapter: ChatAdapter

    // Current active tab
    private var currentTab = 0 // 0: AI Companion, 1: Mental Health, 2: Support Resources

    // Permissions
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.RECORD_AUDIO] == true -> {
                startVoiceRecording()
            }
            else -> {
                Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val pickMediaLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                handleMediaUpload(uri)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sathi_ai, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences("SathiAI", Context.MODE_PRIVATE)

        initializeViews(view)
        setupTabs()
        setupClickListeners()
        setupRecyclerView()
        loadSavedData()
        observeViewModel()

        // Initialize Sathi AI session via ViewModel
        if (viewModel.chatMessages.value.isEmpty()) {
            viewModel.initializeSathiSession()
        }

        // Show AI Companion tab by default
        showTab(0)
    }

    private fun initializeViews(view: View) {
        // Tabs
        tabAiCompanion = view.findViewById(R.id.tab_ai_companion)
        tabMentalHealth = view.findViewById(R.id.tab_mental_health)
        tabSupportResources = view.findViewById(R.id.tab_support_resources)

        // Main UI elements
        messageInput = view.findViewById(R.id.message_input)
        sendButton = view.findViewById(R.id.btn_send_message)
        voiceButton = view.findViewById(R.id.btn_voice_message)
        uploadButton = view.findViewById(R.id.btn_upload_media)
        breathingButton = view.findViewById(R.id.btn_breathing_exercise)
        gratitudeButton = view.findViewById(R.id.btn_gratitude_journal)
        supportGroupButton = view.findViewById(R.id.btn_support_group)
        emergencyButton = view.findViewById(R.id.btn_emergency_helpline)
        chatRecyclerView = view.findViewById(R.id.chat_recycler_view)
        moodScore = view.findViewById(R.id.mood_score)
        anxietyScore = view.findViewById(R.id.anxiety_score)
        conversationCount = view.findViewById(R.id.conversation_count)
        moodProgress = view.findViewById(R.id.mood_progress)
        anxietyProgress = view.findViewById(R.id.anxiety_progress)
    }

    private fun setupTabs() {
        tabAiCompanion.setOnClickListener {
            showTab(0)
        }

        tabMentalHealth.setOnClickListener {
            showTab(1)
        }

        tabSupportResources.setOnClickListener {
            showTab(2)
        }
    }

    private fun showTab(tabIndex: Int) {
        currentTab = tabIndex

        // Update tab styling
        val activeColor = ContextCompat.getColor(requireContext(), R.color.primary_light)
        val inactiveColor = ContextCompat.getColor(requireContext(), android.R.color.transparent)
        val activeTextColor = ContextCompat.getColor(requireContext(), R.color.text_primary)
        val inactiveTextColor = ContextCompat.getColor(requireContext(), R.color.text_secondary)

        tabAiCompanion.setBackgroundColor(if (tabIndex == 0) activeColor else inactiveColor)
        tabMentalHealth.setBackgroundColor(if (tabIndex == 1) activeColor else inactiveColor)
        tabSupportResources.setBackgroundColor(if (tabIndex == 2) activeColor else inactiveColor)

        tabAiCompanion.setTextColor(if (tabIndex == 0) activeTextColor else inactiveTextColor)
        tabMentalHealth.setTextColor(if (tabIndex == 1) activeTextColor else inactiveTextColor)
        tabSupportResources.setTextColor(if (tabIndex == 2) activeTextColor else inactiveTextColor)

        // Show appropriate content based on tab
        when (tabIndex) {
            0 -> showAiCompanionContent()
            1 -> showMentalHealthContent()
            2 -> showSupportResourcesContent()
        }
    }

    private fun showAiCompanionContent() {
        // This is the default chat interface - already visible
        Toast.makeText(context, "AI Companion - Chat with Sathi", Toast.LENGTH_SHORT).show()
    }

    private fun showMentalHealthContent() {
        Toast.makeText(context, "Mental Health Tools", Toast.LENGTH_SHORT).show()

        val options = arrayOf(
            "Mental Health Assessment (PHQ-9)",
            "Anxiety Screening (GAD-7)",
            "Depression Screening",
            "Sleep Quality Assessment",
            "Stress Management Tips",
            "Goal Setting for Wellbeing",
            "Track Your Progress",
            "Find Professional Help Nearby"
        )

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Mental Health Tools")
            .setMessage("Select a tool to assess and improve your mental wellbeing:")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> startPHQ9Assessment()
                    1 -> startGAD7Assessment()
                    2 -> startDepressionScreening()
                    3 -> startSleepAssessment()
                    4 -> showStressManagementTips()
                    5 -> setWellbeingGoals()
                    6 -> showProgressTracking()
                    7 -> findProfessionalHelp()
                }
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun showSupportResourcesContent() {
        Toast.makeText(context, "Support Resources", Toast.LENGTH_SHORT).show()

        val resources = """
            Mental Health Support Resources:
            
            HELPLINES (24/7):
            • NIMHANS: 080-4611-0007
            • Vandrevala Foundation: 1860-2662-345
            • iCall: 9152987821
            • Women Helpline: 1091
            • Emergency: 112
            
            GOVERNMENT SERVICES:
            • National Mental Health Programme
            • District Mental Health Programme
            • Ayushman Bharat - Health & Wellness Centers
            
            TREATMENT OPTIONS:
            • Government Hospitals (Free)
            • NIMHANS Outpatient Services
            • Primary Health Centers
            • Community Mental Health Centers
            
            SELF-HELP RESOURCES:
            • Mindfulness & Meditation Apps
            • Cognitive Behavioral Therapy (CBT) Workbooks
            • Online Support Groups
            • Mental Health Educational Videos
            
            SUPPORT GROUPS:
            • Depression Support Circle
            • Anxiety & Panic Support Group
            • Women's Wellness Community
            • Crisis Support Network
            
            EDUCATIONAL MATERIALS:
            • Understanding Mental Health
            • Coping with Stress & Anxiety
            • Building Resilience
            • Self-Care Strategies
        """.trimIndent()

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Support Resources")
            .setMessage(resources)
            .setPositiveButton("Call Helpline") { _, _ ->
                emergencyButton.performClick()
            }
            .setNeutralButton("Join Support Group") { _, _ ->
                supportGroupButton.performClick()
            }
            .setNegativeButton("Close", null)
            .show()
    }

    // Mental Health Assessment Functions
    private fun startPHQ9Assessment() {
        val questions = arrayOf(
            "Little interest or pleasure in doing things?",
            "Feeling down, depressed, or hopeless?",
            "Trouble falling or staying asleep?",
            "Feeling tired or having little energy?",
            "Poor appetite or overeating?",
            "Feeling bad about yourself?",
            "Trouble concentrating?",
            "Moving or speaking slowly?",
            "Thoughts of self-harm?"
        )

        var currentQuestion = 0
        val scores = IntArray(questions.size)

        fun askQuestion() {
            if (currentQuestion < questions.size) {
                val options = arrayOf(
                    "Not at all (0)",
                    "Several days (1)",
                    "More than half the days (2)",
                    "Nearly every day (3)"
                )

                android.app.AlertDialog.Builder(requireContext())
                    .setTitle("PHQ-9 Assessment (${currentQuestion + 1}/${questions.size})")
                    .setMessage("Over the last 2 weeks, how often have you been bothered by:\n\n${questions[currentQuestion]}")
                    .setItems(options) { _, which ->
                        scores[currentQuestion] = which
                        currentQuestion++
                        askQuestion()
                    }
                    .setCancelable(false)
                    .show()
            } else {
                showPHQ9Results(scores)
            }
        }

        askQuestion()
    }

    private fun showPHQ9Results(scores: IntArray) {
        val totalScore = scores.sum()
        val severity = when {
            totalScore <= 4 -> "Minimal depression"
            totalScore <= 9 -> "Mild depression"
            totalScore <= 14 -> "Moderate depression"
            totalScore <= 19 -> "Moderately severe depression"
            else -> "Severe depression"
        }

        val recommendation = when {
            totalScore <= 4 -> "Your scores suggest minimal or no depression. Continue practicing self-care!"
            totalScore <= 9 -> "Your scores suggest mild depression. Consider lifestyle changes and monitoring your mood."
            totalScore <= 14 -> "Your scores suggest moderate depression. We recommend speaking with a mental health professional."
            else -> "Your scores suggest significant depression. Please seek professional help immediately. Call NIMHANS: 080-4611-0007"
        }

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("PHQ-9 Results")
            .setMessage(
                """
                Total Score: $totalScore/27
                Severity: $severity
                
                $recommendation
                
                Note: This is a screening tool, not a diagnosis. Please consult a mental health professional for proper evaluation.
            """.trimIndent()
            )
            .setPositiveButton("Get Professional Help") { _, _ ->
                findProfessionalHelp()
            }
            .setNeutralButton("Talk to Sathi") { _, _ ->
                showTab(0)
                viewModel.sendMessageToSathi(
                    "I just completed a mental health assessment. Can you help me understand my results and what steps I should take?",
                    5
                )
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun startGAD7Assessment() {
        Toast.makeText(context, "Starting GAD-7 Anxiety Screening...", Toast.LENGTH_SHORT).show()
        // Similar to PHQ-9 but for anxiety
        viewModel.sendMessageToSathi(
            "I'd like to do an anxiety screening. Can you guide me through it?",
            5
        )
        showTab(0)
    }

    private fun startDepressionScreening() {
        Toast.makeText(context, "Starting Depression Screening...", Toast.LENGTH_SHORT).show()
        startPHQ9Assessment()
    }

    private fun startSleepAssessment() {
        Toast.makeText(context, "Sleep Quality Assessment", Toast.LENGTH_SHORT).show()
        viewModel.sendMessageToSathi(
            "I'm having trouble sleeping. Can you help me assess my sleep quality and give me tips?",
            4
        )
        showTab(0)
    }

    private fun showStressManagementTips() {
        val tips = """
            Stress Management Techniques:
            
            1. DEEP BREATHING (4-7-8 Technique)
               • Breathe in for 4 seconds
               • Hold for 7 seconds
               • Exhale for 8 seconds
               • Repeat 4 times
            
            2. PROGRESSIVE MUSCLE RELAXATION
               • Tense and relax each muscle group
               • Start from toes, work up to head
            
            3. MINDFULNESS MEDITATION
               • Focus on present moment
               • Observe thoughts without judgment
               • 10 minutes daily
            
            4. PHYSICAL ACTIVITY
               • 30 minutes daily walking
               • Yoga or stretching
               • Dance or any movement you enjoy
            
            5. HEALTHY LIFESTYLE
               • Regular sleep schedule
               • Balanced diet
               • Limit caffeine & alcohol
               • Stay hydrated
            
            6. SOCIAL CONNECTION
               • Talk to friends/family
               • Join support groups
               • Share your feelings
            
            7. TIME MANAGEMENT
               • Prioritize tasks
               • Take regular breaks
               • Learn to say no
            
            8. HOBBIES & RELAXATION
               • Reading, music, art
               • Nature walks
               • Journaling
        """.trimIndent()

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Stress Management Tips")
            .setMessage(tips)
            .setPositiveButton("Start Breathing Exercise") { _, _ ->
                breathingButton.performClick()
            }
            .setNeutralButton("Talk to Sathi") { _, _ ->
                showTab(0)
                viewModel.sendMessageToSathi("I need help managing my stress. Can you guide me?", 4)
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun setWellbeingGoals() {
        val input = EditText(requireContext()).apply {
            hint =
                "What's your wellbeing goal? (e.g., Exercise 3x/week, Sleep 8 hours, Meditate daily)"
            setPadding(50, 40, 50, 40)
        }

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Set Wellbeing Goal")
            .setMessage("Set a realistic, achievable goal for your mental and physical wellbeing:")
            .setView(input)
            .setPositiveButton("Save Goal") { _, _ ->
                val goal = input.text.toString()
                if (goal.isNotBlank()) {
                    Toast.makeText(context, "Goal saved: $goal", Toast.LENGTH_LONG).show()
                    viewModel.sendMessageToSathi(
                        "I've set a new wellbeing goal: $goal. Can you help me create an action plan?",
                        7
                    )
                    showTab(0)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showProgressTracking() {
        val progress = """
            Your Mental Wellbeing Progress:
            
            CURRENT METRICS:
            • Mood Score: ${moodProgress.progress}%
            • Anxiety Level: ${anxietyProgress.progress}%
            • Conversations: ${conversationCount.text}
            
            WEEKLY TREND:
            • Mood: +5% improvement
            • Anxiety: -8% reduction
            • Active Days: 4/7 days
            
            MILESTONES ACHIEVED:
            First conversation with Sathi
            Completed 10 conversations
            Used breathing exercise
            Started gratitude journal
            
            RECOMMENDATIONS:
            • Continue daily check-ins
            • Practice breathing exercises
            • Join a support group
            • Set weekly wellbeing goals
            
            Keep up the great work!
        """.trimIndent()

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Progress Tracking")
            .setMessage(progress)
            .setPositiveButton("Continue Improving") { _, _ ->
                showTab(0)
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun findProfessionalHelp() {
        val professionals = """
            Find Professional Mental Health Help:
            
            GOVERNMENT HOSPITALS (Free/Subsidized):
            • NIMHANS, Bangalore: 080-4611-0007
            • IHBAS, Delhi: 011-2582-5069
            • Lokopriya Gopinath Bordoloi Regional Institute of Mental Health, Tezpur
            
            AYUSHMAN BHARAT:
            • Health & Wellness Centers
            • Free basic mental health services
            
            TELE-MANAS (24/7):
            • National Tele Mental Health Programme
            • Call: 14416 or 1800-891-4416
            
            FINDING NEARBY HELP:
            1. Government PHC/CHC in your area
            2. District Mental Health Programme
            3. Medical College Psychiatry Dept
            
            ONLINE THERAPY:
            • Practo
            • 1mg
            • Tata Health (Affordable options)
            
            COSTS:
            • Government: Free - ₹100
            • Online: ₹500 - ₹2000/session
            • Private: ₹1000 - ₹3000/session
            
            Remember: Seeking help is a sign of strength!
        """.trimIndent()

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Professional Help")
            .setMessage(professionals)
            .setPositiveButton("Call NIMHANS") { _, _ ->
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:08046110007")
                }
                startActivity(intent)
            }
            .setNeutralButton("Call Tele-MANAS") { _, _ ->
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:14416")
                }
                startActivity(intent)
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun setupClickListeners() {
        sendButton.setOnClickListener {
            sendMessage()
        }

        voiceButton.setOnClickListener {
            handleVoiceRecording()
        }

        uploadButton.setOnClickListener {
            openMediaPicker()
        }

        breathingButton.setOnClickListener {
            startBreathingExercise()
        }

        gratitudeButton.setOnClickListener {
            openGratitudeJournal()
        }

        supportGroupButton.setOnClickListener {
            joinSupportGroup()
        }

        emergencyButton.setOnClickListener {
            showEmergencyContacts()
        }
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter(chatMessages)
        chatRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = chatAdapter
            // Ensure recycler view doesn't interfere with parent scrolling
            isNestedScrollingEnabled = true
        }
    }

    private fun observeViewModel() {
        // Observe chat messages from ViewModel
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.chatMessages.collect { messages ->
                chatMessages.clear()
                messages.forEach { (sender, text) ->
                    chatMessages.add(
                        ChatMessage(
                            text = text,
                            isUser = sender == "User",
                            timestamp = SimpleDateFormat(
                                "h:mm a",
                                Locale.getDefault()
                            ).format(Date())
                        )
                    )
                }
                chatAdapter.notifyDataSetChanged()
                if (chatMessages.isNotEmpty()) {
                    chatRecyclerView.scrollToPosition(chatMessages.size - 1)
                }
            }
        }

        // Observe mood score
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.moodScore.collect { score ->
                val mood = (score * 10).coerceIn(0, 100)
                moodProgress.progress = mood
                moodScore.text = "$mood%"

                val anxiety = 100 - mood
                anxietyProgress.progress = anxiety
                anxietyScore.text = "$anxiety%"

                saveData()
            }
        }

        // Observe loading state
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                sendButton.isEnabled = !isLoading
                messageInput.isEnabled = !isLoading

                if (isLoading) {
                    sendButton.alpha = 0.5f
                    messageInput.hint = "AI is thinking..."
                } else {
                    sendButton.alpha = 1.0f
                    messageInput.hint = "Type your message..."
                }
            }
        }

        // Observe crisis detection
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isCrisisDetected.collect { isCrisis ->
                if (isCrisis) {
                    emergencyButton.setBackgroundColor(
                        resources.getColor(android.R.color.holo_red_light, null)
                    )
                    Toast.makeText(
                        context,
                        "⚠️ Crisis detected. Emergency resources available.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun loadSavedData() {
        val savedMood = sharedPreferences.getInt("mood_score", 65)
        val savedAnxiety = sharedPreferences.getInt("anxiety_score", 35)
        val savedConversations = sharedPreferences.getInt("conversation_count", 0)

        moodProgress.progress = savedMood
        anxietyProgress.progress = savedAnxiety
        moodScore.text = "$savedMood%"
        anxietyScore.text = "$savedAnxiety%"
        conversationCount.text = savedConversations.toString()
    }

    private fun saveData() {
        sharedPreferences.edit().apply {
            putInt("mood_score", moodProgress.progress)
            putInt("anxiety_score", anxietyProgress.progress)
            putInt("conversation_count", conversationCount.text.toString().toIntOrNull() ?: 0)
            apply()
        }
    }

    private fun sendMessage() {
        val message = messageInput.text.toString().trim()
        if (message.isNotEmpty()) {
            messageInput.text.clear()

            // Call ViewModel with Gemini API integration
            val moodRating = moodProgress.progress / 10
            viewModel.sendMessageToSathi(message, moodRating)

            // Update conversation count
            val count = conversationCount.text.toString().toIntOrNull() ?: 0
            conversationCount.text = (count + 1).toString()
            saveData()
        } else {
            Toast.makeText(context, "Please enter a message", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleVoiceRecording() {
        if (isRecording) {
            stopVoiceRecording()
        } else {
            checkAudioPermissionAndRecord()
        }
    }

    private fun checkAudioPermissionAndRecord() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                startVoiceRecording()
            }
            else -> {
                requestPermissionLauncher.launch(
                    arrayOf(Manifest.permission.RECORD_AUDIO)
                )
            }
        }
    }

    private fun startVoiceRecording() {
        try {
            audioFilePath =
                "${requireContext().externalCacheDir?.absolutePath}/audio_${System.currentTimeMillis()}.3gp"

            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(audioFilePath)
                prepare()
                start()
            }

            isRecording = true
            voiceButton.text = "⏹️ Stop Recording"
            voiceButton.backgroundTintList = android.content.res.ColorStateList.valueOf(
                resources.getColor(android.R.color.holo_red_light, null)
            )
            Toast.makeText(context, "🎤 Recording started...", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Toast.makeText(context, "Failed to start recording: ${e.message}", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun stopVoiceRecording() {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            isRecording = false
            voiceButton.text = "🎤 Voice Message"
            voiceButton.backgroundTintList = android.content.res.ColorStateList.valueOf(
                resources.getColor(R.color.sathi_color, null)
            )

            Toast.makeText(context, "✅ Recording saved!", Toast.LENGTH_SHORT).show()

            // Send voice message indicator via ViewModel
            viewModel.sendMessageToSathi("🎤 Voice message recorded", 5)
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to stop recording: ${e.message}", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun openMediaPicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        pickMediaLauncher.launch(intent)
    }

    private fun handleMediaUpload(uri: Uri) {
        Toast.makeText(context, "📎 Media uploaded: ${uri.lastPathSegment}", Toast.LENGTH_SHORT)
            .show()

        // Send media upload indicator via ViewModel
        viewModel.sendMessageToSathi(
            "📎 Shared an image. Can you help me understand my feelings about it?",
            5
        )
    }

    private fun startBreathingExercise() {
        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setTitle("🫁 Breathing Exercise")
            .setMessage(
                """
                Let's practice the 4-7-8 breathing technique:
                
                1. Breathe in slowly through your nose for 4 seconds
                2. Hold your breath for 7 seconds
                3. Breathe out slowly through your mouth for 8 seconds
                4. Repeat 4-5 times
                
                Focus on your breath and let go of tension.
                This technique helps reduce anxiety and promote relaxation.
            """.trimIndent()
            )
            .setPositiveButton("Start") { _, _ ->
                // Send via ViewModel - Gemini API integration
                viewModel.sendMessageToSathi(
                    "I just completed a breathing exercise. How can this help me manage my stress better?",
                    6
                )

                // Slightly improve mood
                val currentMood = moodProgress.progress
                val newMood = minOf(100, currentMood + 5)
                moodProgress.progress = newMood
                moodScore.text = "$newMood%"

                val newAnxiety = 100 - newMood
                anxietyProgress.progress = newAnxiety
                anxietyScore.text = "$newAnxiety%"

                saveData()
            }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()
    }

    private fun openGratitudeJournal() {
        val input = EditText(requireContext())
        input.hint = "What are you grateful for today?"
        input.setPadding(50, 40, 50, 40)

        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setTitle("💗 Gratitude Journal")
            .setMessage("Taking time to appreciate the good things in life can significantly improve your mood and mental well-being.")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val gratitude = input.text.toString()
                if (gratitude.isNotEmpty()) {
                    // Send via ViewModel - Gemini API integration
                    viewModel.sendMessageToSathi(
                        "I'm grateful for: $gratitude. Can you help me understand why gratitude is important?",
                        7
                    )

                    // Boost mood score
                    val currentMood = moodProgress.progress
                    val newMood = minOf(100, currentMood + 10)
                    moodProgress.progress = newMood
                    moodScore.text = "$newMood%"

                    val newAnxiety = 100 - newMood
                    anxietyProgress.progress = newAnxiety
                    anxietyScore.text = "$newAnxiety%"

                    saveData()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()
    }

    private fun joinSupportGroup() {
        val groups = arrayOf(
            "Anxiety Support Group (45 members)",
            "Depression Support Circle (32 members)",
            "Women's Wellness Community (128 members)",
            "Crisis Support Network (67 members)"
        )

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("👥 Join Support Group")
            .setItems(groups) { _, which ->
                val selectedGroup = groups[which]
                Toast.makeText(context, "✅ Joined: $selectedGroup", Toast.LENGTH_SHORT).show()

                // Send via ViewModel - Gemini API integration
                viewModel.sendMessageToSathi(
                    "I just joined a support group: $selectedGroup. What should I expect and how can it help me?",
                    6
                )
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEmergencyContacts() {
        val message = """
            🆘 Emergency Mental Health Helplines:
            
            NIMHANS Helpline: 080-4611-0007
            (Available 24/7 for mental health emergencies)
            
            Women Helpline: 1091
            (For women in distress)
            
            National Emergency: 112
            (For immediate danger)
            
            Vandrevala Foundation: 1860-2662-345
            (24/7 Mental Health Support)
            
            iCall: 9152987821
            (Psychosocial Helpline - English/Hindi)
            
            If you're having thoughts of self-harm, please call these numbers immediately. Your life matters. 💜
        """.trimIndent()

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("🚨 Emergency Support")
            .setMessage(message)
            .setPositiveButton("Call NIMHANS") { _, _ ->
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:08046110007")
                }
                startActivity(intent)
            }
            .setNeutralButton("Call Women Helpline") { _, _ ->
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:1091")
                }
                startActivity(intent)
            }
            .setNegativeButton("Close", null)
            .show()
    }

    override fun onPause() {
        super.onPause()
        saveData()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaRecorder?.release()
        mediaRecorder = null
        saveData()
    }

    // Data classes
    data class ChatMessage(
        val text: String,
        val isUser: Boolean,
        val timestamp: String
    )

    // Chat Adapter
    inner class ChatAdapter(private val messages: List<ChatMessage>) :
        RecyclerView.Adapter<ChatAdapter.MessageViewHolder>() {

        inner class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val messageText: TextView = view.findViewById(android.R.id.text1)
            val timeText: TextView = view.findViewById(android.R.id.text2)
            val card: CardView = view as CardView
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
            val card = CardView(parent.context).apply {
                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(16, 8, 16, 8)
                }
                radius = 16f
                cardElevation = 4f
                isClickable = true
                isFocusable = true

                val layout = LinearLayout(context).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(24, 20, 24, 20)

                    addView(TextView(context).apply {
                        id = android.R.id.text1
                        textSize = 14f
                        setTextColor(resources.getColor(R.color.text_primary, null))
                        setLineSpacing(4f, 1.1f)
                    })

                    addView(TextView(context).apply {
                        id = android.R.id.text2
                        textSize = 10f
                        setTextColor(resources.getColor(R.color.text_secondary, null))
                        setPadding(0, 8, 0, 0)
                    })
                }

                addView(layout)
            }

            return MessageViewHolder(card)
        }

        override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
            val message = messages[position]
            holder.messageText.text = message.text
            holder.timeText.text = message.timestamp

            // Different styling for user and AI messages
            if (message.isUser) {
                holder.card.setCardBackgroundColor(resources.getColor(R.color.sathi_color, null))
                holder.messageText.setTextColor(resources.getColor(R.color.white, null))
                holder.timeText.setTextColor(resources.getColor(R.color.white, null))
            } else {
                holder.card.setCardBackgroundColor(resources.getColor(R.color.white, null))
                holder.messageText.setTextColor(resources.getColor(R.color.text_primary, null))
                holder.timeText.setTextColor(resources.getColor(R.color.text_secondary, null))
            }
        }

        override fun getItemCount() = messages.size
    }
}
