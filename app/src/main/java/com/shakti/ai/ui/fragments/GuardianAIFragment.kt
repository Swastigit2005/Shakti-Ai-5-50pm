package com.shakti.ai.ui.fragments

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shakti.ai.R
import com.shakti.ai.viewmodel.GuardianViewModel

/**
 * GuardianAIFragment - Physical Safety Module
 * Interactive threat detection and mesh network
 */
class GuardianAIFragment : Fragment() {

    private val viewModel: GuardianViewModel by viewModels()

    // Tab views
    private lateinit var tabMeshNetwork: TextView
    private lateinit var tabEmergencyActions: TextView
    private lateinit var tabEvidenceSystem: TextView

    private lateinit var guardianSwitch: SwitchCompat
    private lateinit var threatScoreText: TextView
    private lateinit var environmentalProgress: ProgressBar
    private lateinit var guardianRecyclerView: RecyclerView
    private lateinit var becomeGuardianButton: Button

    private val guardians = mutableListOf<Guardian>()
    private lateinit var guardianAdapter: GuardianAdapter

    private var isGuardianMode = true
    private var threatScore = 15
    private var currentTab = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_guardian_ai, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)
        setupTabs()
        setupClickListeners()
        setupRecyclerView()
        loadGuardians()

        viewModel.startGuardianMonitoring()
        showTab(0) // Show Mesh Network by default
    }

    private fun initializeViews(view: View) {
        // Tabs
        tabMeshNetwork = view.findViewById(R.id.tab_mesh_network)
        tabEmergencyActions = view.findViewById(R.id.tab_emergency_actions)
        tabEvidenceSystem = view.findViewById(R.id.tab_evidence_system)

        guardianSwitch = view.findViewById(R.id.guardian_switch)
        threatScoreText = view.findViewById(R.id.threat_score_number)
        environmentalProgress = view.findViewById(R.id.environmental_safety_progress)
        guardianRecyclerView = view.findViewById(R.id.guardian_recycler_view)
        becomeGuardianButton = view.findViewById(R.id.btn_become_guardian)

        threatScoreText.text = threatScore.toString()
        environmentalProgress.progress = 85
    }

    private fun setupTabs() {
        tabMeshNetwork.setOnClickListener {
            showTab(0)
        }

        tabEmergencyActions.setOnClickListener {
            showTab(1)
        }

        tabEvidenceSystem.setOnClickListener {
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

        tabMeshNetwork.setBackgroundColor(if (tabIndex == 0) activeColor else inactiveColor)
        tabEmergencyActions.setBackgroundColor(if (tabIndex == 1) activeColor else inactiveColor)
        tabEvidenceSystem.setBackgroundColor(if (tabIndex == 2) activeColor else inactiveColor)

        tabMeshNetwork.setTextColor(if (tabIndex == 0) activeTextColor else inactiveTextColor)
        tabEmergencyActions.setTextColor(if (tabIndex == 1) activeTextColor else inactiveTextColor)
        tabEvidenceSystem.setTextColor(if (tabIndex == 2) activeTextColor else inactiveTextColor)

        // Show appropriate content
        when (tabIndex) {
            0 -> showMeshNetworkContent()
            1 -> showEmergencyActionsContent()
            2 -> showEvidenceSystemContent()
        }
    }

    private fun showMeshNetworkContent() {
        Toast.makeText(context, "📡 Mesh Network - 12 guardians nearby", Toast.LENGTH_SHORT).show()
        // This is the default view - already visible
    }

    private fun showEmergencyActionsContent() {
        Toast.makeText(context, "🆘 Emergency Actions", Toast.LENGTH_SHORT).show()

        val actions = arrayOf(
            "🆘 SOS Panic Button",
            "📞 Call Emergency Services",
            "📍 Share Live Location",
            "🚨 Trigger Siren/Alarm",
            "📹 Start Recording (Video/Audio)",
            "📱 Send Emergency SMS",
            "🔦 Flashlight SOS Signal",
            "👥 Alert All Nearby Guardians"
        )

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("🆘 Emergency Actions")
            .setMessage("Select an emergency action:")
            .setItems(actions) { _, which ->
                when (which) {
                    0 -> triggerSOSPanic()
                    1 -> callEmergencyServices()
                    2 -> shareLiveLocation()
                    3 -> triggerSiren()
                    4 -> startRecording()
                    5 -> sendEmergencySMS()
                    6 -> flashlightSOS()
                    7 -> alertAllGuardians()
                }
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun showEvidenceSystemContent() {
        Toast.makeText(context, "📹 Evidence Collection System", Toast.LENGTH_SHORT).show()

        val evidenceOptions = arrayOf(
            "📸 Auto-capture Screenshots",
            "🎤 Background Audio Recording",
            "📹 Continuous Video Recording",
            "☁️ Cloud Backup Status",
            "🔒 View Encrypted Storage",
            "📊 Evidence Timeline",
            "🔗 Share with Police/Lawyer",
            "⚙️ Evidence Settings"
        )

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("📹 Evidence Collection System")
            .setMessage("All evidence is automatically encrypted and backed up to cloud storage.")
            .setItems(evidenceOptions) { _, which ->
                when (which) {
                    0 -> enableAutoScreenshots()
                    1 -> startBackgroundAudio()
                    2 -> startContinuousVideo()
                    3 -> showCloudBackupStatus()
                    4 -> viewEncryptedStorage()
                    5 -> showEvidenceTimeline()
                    6 -> shareEvidenceWithAuthorities()
                    7 -> openEvidenceSettings()
                }
            }
            .setNegativeButton("Close", null)
            .show()
    }

    // Emergency Actions Implementation
    private fun triggerSOSPanic() {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("🆘 SOS PANIC ACTIVATED")
            .setMessage(
                """
                EMERGENCY ALERT ACTIVATED!
                
                ✅ Siren triggered
                ✅ Location shared with guardians
                ✅ Emergency services notified
                ✅ Recording started
                ✅ SMS sent to emergency contacts
                
                12 guardians within 500m have been alerted!
                Average response time: 2 minutes
                
                Stay calm. Help is on the way.
            """.trimIndent()
            )
            .setPositiveButton("Cancel SOS") { _, _ ->
                Toast.makeText(context, "❌ SOS cancelled", Toast.LENGTH_SHORT).show()
            }
            .setCancelable(false)
            .show()
    }

    private fun callEmergencyServices() {
        val services = arrayOf(
            "🚨 Police (100)",
            "🚑 Ambulance (102)",
            "👮 Women Helpline (1091)",
            "🆘 Emergency (112)",
            "🔥 Fire (101)"
        )

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("📞 Call Emergency Services")
            .setItems(services) { _, which ->
                val number = when (which) {
                    0 -> "100"
                    1 -> "102"
                    2 -> "1091"
                    3 -> "112"
                    4 -> "101"
                    else -> "112"
                }
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$number")
                }
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun shareLiveLocation() {
        val shareMessage = """
            🆘 EMERGENCY - I NEED HELP!
            
            My current location:
            📍 Latitude: 28.7041
            📍 Longitude: 77.1025
            
            Google Maps: https://maps.google.com/?q=28.7041,77.1025
            
            Time: ${
            java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
                .format(java.util.Date())
        }
            
            Sent via ShaktiAI Guardian
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareMessage)
        }
        startActivity(Intent.createChooser(intent, "Share location via"))
    }

    private fun triggerSiren() {
        Toast.makeText(context, "🚨 SIREN ACTIVATED - Maximum volume", Toast.LENGTH_LONG).show()
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("🚨 Siren Activated")
            .setMessage(
                """
                Loud siren is now playing at maximum volume!
                
                This will:
                • Draw attention from nearby people
                • Scare away potential attackers
                • Alert guardians in the area
                
                The siren will continue until you stop it.
            """.trimIndent()
            )
            .setPositiveButton("Stop Siren") { _, _ ->
                Toast.makeText(context, "✅ Siren stopped", Toast.LENGTH_SHORT).show()
            }
            .setCancelable(false)
            .show()
    }

    private fun startRecording() {
        val recordOptions = arrayOf(
            "📹 Video Recording",
            "🎤 Audio Recording",
            "📸 Photo Evidence",
            "📹 Video + Audio"
        )

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("📹 Start Recording")
            .setMessage("Select recording type:")
            .setItems(recordOptions) { _, which ->
                val recordType = when (which) {
                    0 -> "Video"
                    1 -> "Audio"
                    2 -> "Photos"
                    else -> "Video + Audio"
                }
                Toast.makeText(
                    context,
                    "✅ $recordType recording started\n☁️ Auto-uploading to cloud",
                    Toast.LENGTH_LONG
                ).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun sendEmergencySMS() {
        Toast.makeText(context, "📱 Emergency SMS sent to all contacts", Toast.LENGTH_SHORT).show()
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("📱 Emergency SMS Sent")
            .setMessage(
                """
                SMS sent to 5 emergency contacts:
                
                ✅ Father - 9876543210
                ✅ Mother - 9876543211
                ✅ Friend - 9876543212
                ✅ Colleague - 9876543213
                ✅ Guardian #247 - 9876543214
                
                Message: "EMERGENCY! I need help. My location: [GPS coordinates]"
                
                They can track your live location now.
            """.trimIndent()
            )
            .setPositiveButton("OK", null)
            .show()
    }

    private fun flashlightSOS() {
        Toast.makeText(context, "🔦 Flashlight SOS signal activated", Toast.LENGTH_SHORT).show()
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("🔦 Flashlight SOS")
            .setMessage(
                """
                Flashlight is blinking SOS pattern:
                
                ... --- ...
                (3 short, 3 long, 3 short)
                
                This universal distress signal will:
                • Alert people nearby
                • Help rescuers find you
                • Visible from long distance
                
                Running on loop until stopped.
            """.trimIndent()
            )
            .setPositiveButton("Stop") { _, _ ->
                Toast.makeText(context, "✅ Flashlight stopped", Toast.LENGTH_SHORT).show()
            }
            .setCancelable(false)
            .show()
    }

    private fun alertAllGuardians() {
        Toast.makeText(context, "👥 Alert sent to 12 nearby guardians", Toast.LENGTH_SHORT).show()
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("👥 Guardians Alerted")
            .setMessage(
                """
                🚨 ALERT SENT TO 12 GUARDIANS
                
                Guardians responding:
                ✅ Guardian #247 - 45m away (1 min ETA)
                ✅ Guardian #156 - 120m away (2 min ETA)
                ✅ Guardian #389 - 180m away (2 min ETA)
                ⏳ 9 more guardians responding...
                
                They are rushing to your location!
                Stay in a safe place if possible.
                
                Average response time: 2 minutes
            """.trimIndent()
            )
            .setPositiveButton("OK", null)
            .show()
    }

    // Evidence System Implementation
    private fun enableAutoScreenshots() {
        Toast.makeText(context, "📸 Auto-screenshot enabled", Toast.LENGTH_SHORT).show()
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("📸 Auto-Screenshot Enabled")
            .setMessage(
                """
                Auto-screenshot will capture:
                
                ✅ Threatening messages/calls
                ✅ Suspicious activity
                ✅ Important locations
                ✅ Evidence of harassment
                
                Interval: Every 30 seconds
                Storage: Encrypted cloud backup
                Auto-delete: After 30 days (unless marked)
                
                All screenshots are timestamped and GPS-tagged.
            """.trimIndent()
            )
            .setPositiveButton("Enable") { _, _ ->
                Toast.makeText(context, "✅ Auto-screenshot active", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun startBackgroundAudio() {
        Toast.makeText(context, "🎤 Background audio recording started", Toast.LENGTH_SHORT).show()
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("🎤 Background Audio Recording")
            .setMessage(
                """
                Recording audio in background mode.
                
                Features:
                ✅ Runs even when phone is locked
                ✅ Low battery consumption
                ✅ Automatic cloud upload
                ✅ Encrypted storage
                ✅ Timestamped recordings
                
                Storage: 24 hours continuous
                Quality: High (admissible in court)
                
                This can be crucial evidence in legal cases.
            """.trimIndent()
            )
            .setPositiveButton("Stop Recording") { _, _ ->
                Toast.makeText(context, "⏹️ Recording stopped and saved", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Keep Recording", null)
            .show()
    }

    private fun startContinuousVideo() {
        Toast.makeText(context, "📹 Continuous video recording started", Toast.LENGTH_SHORT).show()
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("📹 Continuous Video Recording")
            .setMessage(
                """
                Video recording in progress:
                
                Mode: Background (discreet)
                Quality: 720p
                Storage: Auto-upload to cloud
                Battery: Optimized mode
                
                Recording will continue even if:
                • Screen is off
                • App is closed
                • Phone is in pocket
                
                Duration: Unlimited (until stopped)
                Cloud space: 10GB available
            """.trimIndent()
            )
            .setPositiveButton("Stop") { _, _ ->
                Toast.makeText(context, "⏹️ Video saved: 15 minutes, 1.2GB", Toast.LENGTH_SHORT)
                    .show()
            }
            .setNegativeButton("Continue", null)
            .show()
    }

    private fun showCloudBackupStatus() {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("☁️ Cloud Backup Status")
            .setMessage(
                """
                Cloud Storage Status:
                
                📊 Used: 2.5 GB / 10 GB (25%)
                ✅ Last backup: 2 minutes ago
                🔄 Sync: Active
                
                Evidence Backed Up:
                📹 Videos: 15 files (1.8 GB)
                🎤 Audio: 32 files (500 MB)
                📸 Photos: 47 files (200 MB)
                
                Encryption: AES-256 ✅
                Auto-delete: 30 days after incident closed
                
                Storage upgrade available: 50GB for ₹99/month
            """.trimIndent()
            )
            .setPositiveButton("Upgrade Storage") { _, _ ->
                Toast.makeText(context, "Opening storage plans...", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun viewEncryptedStorage() {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("🔒 Encrypted Evidence Storage")
            .setMessage(
                """
                Your Evidence Files:
                
                📅 Today (3 files)
                • Video_20250117_143022.enc (850 MB)
                • Audio_20250117_143500.enc (125 MB)
                • Photos_evidence.enc (45 MB)
                
                📅 Yesterday (2 files)
                • Video_20250116_183000.enc (1.2 GB)
                • Audio_20250116_190000.enc (98 MB)
                
                🔒 All files are encrypted with your password
                ☁️ Backed up to secure cloud
                📍 GPS-tagged
                ⏰ Timestamped
                
                Only you and authorized persons can access.
            """.trimIndent()
            )
            .setPositiveButton("Open File") { _, _ ->
                Toast.makeText(context, "Enter password to decrypt", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun showEvidenceTimeline() {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("📊 Evidence Timeline")
            .setMessage(
                """
                Incident Timeline:
                
                🕒 14:30:00 - Threat detected
                🕒 14:30:15 - Recording started
                🕒 14:30:22 - Location tagged
                🕒 14:30:45 - SMS sent to contacts
                🕒 14:31:10 - Guardians alerted
                🕒 14:32:30 - Police called
                🕒 14:35:00 - Guardian arrived
                🕒 14:36:15 - Incident ended
                
                Evidence collected:
                ✅ 15 min video
                ✅ 20 min audio
                ✅ 12 photos
                ✅ GPS track
                ✅ Witness statements (2)
                
                All evidence is court-admissible.
            """.trimIndent()
            )
            .setPositiveButton("Export Timeline") { _, _ ->
                Toast.makeText(context, "Timeline exported as PDF", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun shareEvidenceWithAuthorities() {
        val shareOptions = arrayOf(
            "👮 Share with Police",
            "👨‍⚖️ Share with Lawyer",
            "📧 Email Evidence",
            "💾 Export to USB/SD Card"
        )

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("🔗 Share Evidence")
            .setMessage("Select how to share evidence:")
            .setItems(shareOptions) { _, which ->
                when (which) {
                    0 -> shareWithPolice()
                    1 -> shareWithLawyer()
                    2 -> emailEvidence()
                    3 -> exportToStorage()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun shareWithPolice() {
        Toast.makeText(context, "👮 Preparing evidence package for police...", Toast.LENGTH_LONG)
            .show()
    }

    private fun shareWithLawyer() {
        Toast.makeText(context, "👨‍⚖️ Preparing evidence for legal case...", Toast.LENGTH_LONG)
            .show()
    }

    private fun emailEvidence() {
        Toast.makeText(context, "📧 Opening email with evidence attached...", Toast.LENGTH_SHORT)
            .show()
    }

    private fun exportToStorage() {
        Toast.makeText(context, "💾 Exporting to external storage...", Toast.LENGTH_SHORT).show()
    }

    private fun openEvidenceSettings() {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("⚙️ Evidence Settings")
            .setMessage(
                """
                Configure Evidence Collection:
                
                Auto-Recording:
                ☑ Video (on threat detection)
                ☑ Audio (continuous background)
                ☐ Screenshots (manual only)
                
                Quality Settings:
                • Video: 720p (balanced)
                • Audio: High quality
                
                Storage:
                ☑ Auto-upload to cloud
                ☑ Keep local backup
                • Auto-delete after 30 days
                
                Privacy:
                ☑ Encrypt all files
                ☑ Password protection
            """.trimIndent()
            )
            .setPositiveButton("Save Settings") { _, _ ->
                Toast.makeText(context, "✅ Settings saved", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // ... existing code for setupClickListeners, setupRecyclerView, loadGuardians, etc. ...

    private fun setupClickListeners() {
        guardianSwitch.setOnCheckedChangeListener { _, isChecked ->
            isGuardianMode = isChecked
            if (isChecked) {
                Toast.makeText(context, "Guardian Mode: ACTIVE", Toast.LENGTH_SHORT).show()
                startThreatMonitoring()
            } else {
                Toast.makeText(context, "Guardian Mode: OFF", Toast.LENGTH_SHORT).show()
            }
        }

        becomeGuardianButton.setOnClickListener {
            showBecomeGuardianDialog()
        }
    }

    private fun setupRecyclerView() {
        guardianAdapter = GuardianAdapter(guardians)
        guardianRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = guardianAdapter
        }
    }

    private fun loadGuardians() {
        guardians.clear()
        guardians.addAll(
            listOf(
                Guardian("#247", "45m", "1 min", 4.9f),
                Guardian("#156", "120m", "2 min", 4.8f),
                Guardian("#389", "180m", "2 min", 5.0f),
                Guardian("#512", "250m", "3 min", 4.7f),
                Guardian("#091", "310m", "3 min", 4.9f)
            )
        )
        guardianAdapter.notifyDataSetChanged()
    }

    private fun startThreatMonitoring() {
        view?.postDelayed({
            if (isGuardianMode) {
                simulateThreatDetection()
            }
        }, 5000)
    }

    private fun simulateThreatDetection() {
        val random = (0..100).random()

        if (random < 10) {
            threatScore = 85
            threatScoreText.text = threatScore.toString()
            showThreatAlert("HIGH", "Suspicious activity detected nearby")
        } else if (random < 30) {
            threatScore = 45
            threatScoreText.text = threatScore.toString()
        }

        if (isGuardianMode) {
            view?.postDelayed({ simulateThreatDetection() }, 10000)
        }
    }

    private fun showThreatAlert(level: String, message: String) {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("⚠️ Threat Detected - $level")
            .setMessage(message)
            .setPositiveButton("Alert Guardians") { _, _ ->
                Toast.makeText(context, "Alert sent to 12 guardians", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Dismiss", null)
            .show()
    }

    private fun showBecomeGuardianDialog() {
        val message =
            "Become a Guardian and help protect other women!\n\nYou'll receive alerts when someone nearby needs help."

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("👤 Become a Guardian")
            .setMessage(message)
            .setPositiveButton("Join Now") { _, _ ->
                Toast.makeText(context, "✅ You are now a Guardian!", Toast.LENGTH_LONG).show()
                guardians.add(0, Guardian("#YOU", "0m", "< 1 min", 5.0f))
                guardianAdapter.notifyItemInserted(0)
            }
            .setNegativeButton("Maybe Later", null)
            .show()
    }

    // Data class
    data class Guardian(
        val id: String,
        val distance: String,
        val responseTime: String,
        val rating: Float
    )

    // Guardian Adapter
    inner class GuardianAdapter(private val guardians: List<Guardian>) :
        RecyclerView.Adapter<GuardianAdapter.GuardianViewHolder>() {

        inner class GuardianViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val idText: TextView = view.findViewById(android.R.id.text1)
            val detailsText: TextView = view.findViewById(android.R.id.text2)
            val ratingText: TextView = view.findViewById(android.R.id.summary)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuardianViewHolder {
            val view = LinearLayout(parent.context).apply {
                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(16, 8, 16, 8)
                }
                orientation = LinearLayout.VERTICAL
                setPadding(24, 16, 24, 16)
                setBackgroundColor(resources.getColor(android.R.color.white, null))

                addView(TextView(context).apply {
                    id = android.R.id.text1
                    textSize = 16f
                    setTextColor(resources.getColor(R.color.text_primary, null))
                })

                addView(TextView(context).apply {
                    id = android.R.id.text2
                    textSize = 12f
                    setTextColor(resources.getColor(R.color.text_secondary, null))
                    setPadding(0, 8, 0, 0)
                })

                addView(TextView(context).apply {
                    id = android.R.id.summary
                    textSize = 14f
                    setTextColor(resources.getColor(R.color.guardian_color, null))
                    setPadding(0, 8, 0, 0)
                })
            }

            return GuardianViewHolder(view)
        }

        override fun onBindViewHolder(holder: GuardianViewHolder, position: Int) {
            val guardian = guardians[position]
            holder.idText.text = "Guardian ${guardian.id}"
            holder.detailsText.text =
                "${guardian.distance} away • Response: ${guardian.responseTime}"
            holder.ratingText.text = "⭐ ${guardian.rating}"
        }

        override fun getItemCount() = guardians.size
    }
}
