package com.shakti.ai.ai

import android.content.Context
import com.google.ai.client.generativeai.GenerativeModel
import com.shakti.ai.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiService(context: Context) {

    private val apiKey = BuildConfig.GEMINI_API_KEY.takeIf { it.isNotBlank() } ?: "DEMO_MODE"
    private val isApiKeyValid = apiKey != "DEMO_MODE" && apiKey != "your_api_key_here"

    // System instructions for different AI purposes
    private val sathiSystemInstruction = """
        You are Sathi AI, a compassionate mental health companion for women.
        Your role:
        - Listen without judgment
        - Provide culturally sensitive mental health support
        - Offer coping strategies and techniques
        - Encourage professional help when needed
        - Support in Hindi, English, and regional languages
        - Focus on Indian women's specific challenges
        
        IMPORTANT RULES:
        - Never provide medical diagnosis
        - Always encourage seeing a professional for serious issues
        - Be supportive and empathetic
        - Provide actionable advice
        - Keep responses concise (under 500 chars)
    """.trimIndent()

    private val nyayaSystemInstruction = """
        You are Nyaya AI, a legal advisor for women's rights in India.
        Your expertise:
        - Indian Penal Code (IPC) sections related to women
        - Domestic Violence Act
        - Dowry Act
        - Sexual Harassment at Workplace (POSH) Act
        - Protection of Women from Sexual Harassment Act
        - Divorce and property laws
        
        Tasks:
        - Auto-generate FIRs based on victim complaints
        - Explain legal rights in simple terms
        - Draft legal notices and restraining orders
        - Suggest appropriate legal actions
        - Connect with pro-bono lawyers
        
        IMPORTANT:
        - Provide section numbers with explanations
        - Always recommend professional legal counsel
        - Keep language simple and jargon-free
    """.trimIndent()

    private val dhanShaktiSystemInstruction = """
        You are Dhan Shakti AI, a financial advisor for women's economic independence.
        Your expertise:
        - Micro-credit and loans
        - Investment strategies
        - Budgeting and savings
        - Business startup guidance
        - Government schemes for women
        - Financial literacy
        
        Tasks:
        - Assess loan eligibility
        - Create personalized investment plans
        - Suggest government schemes
        - Provide business ideas based on skills
        - Calculate financial goals timelines
        
        FOCUS:
        - Low-cost solutions for poor women
        - Government subsidies and schemes
        - Risk-free investment options
        - Savings discipline
    """.trimIndent()

    private val gyaanSystemInstruction = """
        You are Gyaan AI, an educational advisor for women's skill development.
        Your expertise:
        - Skill assessment
        - Career recommendations
        - Upskilling pathways
        - Scholarship finder
        - Course recommendations
        - Industry demand analysis
        
        Tasks:
        - Identify skill gaps
        - Suggest learning resources
        - Match with scholarships
        - Create learning timelines
        - Connect with vocational training
        
        FOCUS:
        - Women-centric education
        - Low-cost/free resources
        - High-demand skills
        - Flexible learning schedules
    """.trimIndent()

    private val swasthyaSystemInstruction = """
        You are Swasthya AI, a reproductive health companion.
        Your expertise:
        - Menstrual cycle tracking
        - Reproductive health education
        - Symptom analysis
        - Telemedicine facilitation
        - Nutrition for women
        - Sexual and reproductive rights
        
        Tasks:
        - Track menstrual cycles
        - Predict ovulation and fertile windows
        - Suggest health specialists
        - Provide health education
        - Connect with telemedicine doctors
        
        IMPORTANT:
        - Privacy is paramount
        - No diagnosis, only suggestions
        - Normalize menstruation discussions
        - Empower with knowledge
    """.trimIndent()

    private val rakshaSystemInstruction = """
        You are Raksha AI, a domestic violence support system.
        Your expertise:
        - Domestic violence patterns recognition
        - Safety planning
        - Emergency resources
        - Escape route planning
        - Emotional support
        - Legal remedies
        
        Tasks:
        - Identify abuse patterns
        - Create personalized safety plans
        - Connect with shelters and NGOs
        - Provide psychological first aid
        - Guide through legal processes
        
        CRITICAL:
        - Maintain absolute confidentiality
        - Never minimize abuse
        - Always prioritize safety
        - Emergency contacts readily available
    """.trimIndent()

    private val arogyaSystemInstruction = """
        You are Arogya AI, a health and wellness advisor.
        Your expertise:
        - General health advice
        - Nutrition planning
        - Fitness guidance
        - Disease prevention
        - Health education
        
        Tasks:
        - Provide general health advice
        - Create personalized nutrition plans
        - Suggest fitness routines
        - Educate on disease prevention
        - Connect with health specialists
        
        IMPORTANT:
        - Provide accurate and reliable information
        - Always recommend professional medical counsel
        - Keep language simple and jargon-free
    """.trimIndent()

    // Different specialized models for different AI purposes - LAZY INITIALIZATION
    private val sathiModel by lazy {
        GenerativeModel(
            modelName = "gemini-2.0-flash-exp",
            apiKey = apiKey
        )
    }

    private val nyayaModel by lazy {
        GenerativeModel(
            modelName = "gemini-2.0-flash-exp",
            apiKey = apiKey
        )
    }

    private val dhanShaktiModel by lazy {
        GenerativeModel(
            modelName = "gemini-2.0-flash-exp",
            apiKey = apiKey
        )
    }

    private val gyaanModel by lazy {
        GenerativeModel(
            modelName = "gemini-2.0-flash-exp",
            apiKey = apiKey
        )
    }

    private val swasthyaModel by lazy {
        GenerativeModel(
            modelName = "gemini-2.0-flash-exp",
            apiKey = apiKey
        )
    }

    private val rakshaModel by lazy {
        GenerativeModel(
            modelName = "gemini-2.0-flash-exp",
            apiKey = apiKey
        )
    }

    private val arogyaModel by lazy {
        GenerativeModel(
            modelName = "gemini-2.0-flash-exp",
            apiKey = apiKey
        )
    }

    // Generic model for other tasks
    private val generalModel by lazy {
        GenerativeModel(
            modelName = "gemini-2.0-flash-exp",
            apiKey = apiKey
        )
    }

    // Call Sathi AI for mental health
    suspend fun callSathiAI(userMessage: String): String = withContext(Dispatchers.IO) {
        try {
            if (!isApiKeyValid) {
                return@withContext getDemoResponse("sathi", userMessage)
            }
            val fullPrompt = "$sathiSystemInstruction\n\nUser: $userMessage"
            val response = sathiModel.generateContent(fullPrompt)
            response.text ?: "I'm here to support you. Could you tell me more?"
        } catch (e: Exception) {
            "I encountered an issue. Please try again: ${e.message}"
        }
    }

    // Call Nyaya AI for legal advice
    suspend fun callNyayaAI(userMessage: String): String = withContext(Dispatchers.IO) {
        try {
            if (!isApiKeyValid) {
                return@withContext getDemoResponse("nyaya", userMessage)
            }
            val fullPrompt = "$nyayaSystemInstruction\n\nUser: $userMessage"
            val response = nyayaModel.generateContent(fullPrompt)
            response.text ?: "Let me help you understand your legal rights."
        } catch (e: Exception) {
            "Unable to process legal query: ${e.message}"
        }
    }

    // Call Dhan Shakti AI for financial advice
    suspend fun callDhanShaktiAI(userMessage: String): String = withContext(Dispatchers.IO) {
        try {
            if (!isApiKeyValid) {
                return@withContext getDemoResponse("dhanshakti", userMessage)
            }
            val fullPrompt = "$dhanShaktiSystemInstruction\n\nUser: $userMessage"
            val response = dhanShaktiModel.generateContent(fullPrompt)
            response.text ?: "Let's work on your financial independence."
        } catch (e: Exception) {
            "Financial calculation failed: ${e.message}"
        }
    }

    // Call Gyaan AI for education
    suspend fun callGyaanAI(userMessage: String): String = withContext(Dispatchers.IO) {
        try {
            if (!isApiKeyValid) {
                return@withContext getDemoResponse("gyaan", userMessage)
            }
            val fullPrompt = "$gyaanSystemInstruction\n\nUser: $userMessage"
            val response = gyaanModel.generateContent(fullPrompt)
            response.text ?: "Let's find the best learning path for you."
        } catch (e: Exception) {
            "Education suggestion failed: ${e.message}"
        }
    }

    // Call Swasthya AI for health
    suspend fun callSwasthyaAI(userMessage: String): String = withContext(Dispatchers.IO) {
        try {
            if (!isApiKeyValid) {
                return@withContext getDemoResponse("swasthya", userMessage)
            }
            val fullPrompt = "$swasthyaSystemInstruction\n\nUser: $userMessage"
            val response = swasthyaModel.generateContent(fullPrompt)
            response.text ?: "Let me help with your health and wellness."
        } catch (e: Exception) {
            "Health information unavailable: ${e.message}"
        }
    }

    // Call Raksha AI for domestic violence support
    suspend fun callRakshaAI(userMessage: String): String = withContext(Dispatchers.IO) {
        try {
            if (!isApiKeyValid) {
                return@withContext getDemoResponse("raksha", userMessage)
            }
            val fullPrompt = "$rakshaSystemInstruction\n\nUser: $userMessage"
            val response = rakshaModel.generateContent(fullPrompt)
            response.text ?: "Your safety is our priority. How can I help?"
        } catch (e: Exception) {
            "Emergency support unavailable: ${e.message}"
        }
    }

    // Call Arogya AI for general health advice
    suspend fun callArogyaAI(userMessage: String): String = withContext(Dispatchers.IO) {
        try {
            if (!isApiKeyValid) {
                return@withContext getDemoResponse("arogya", userMessage)
            }
            val response = arogyaModel.generateContent(userMessage)
            response.text ?: "Let me provide you with general health advice."
        } catch (e: Exception) {
            "Health advice unavailable: ${e.message}"
        }
    }

    // Multi-turn conversation (chat history)
    suspend fun callSathiAIWithHistory(
        messages: List<Pair<String, String>>
    ): String = withContext(Dispatchers.IO) {
        try {
            if (!isApiKeyValid) {
                return@withContext "Thank you for sharing. In demo mode, full conversation history is not available. Please add your Gemini API key in local.properties for full functionality."
            }
            val chat = sathiModel.startChat()
            for ((role, text) in messages) {
                chat.sendMessage(text)
            }
            val response = chat.sendMessage("Continue our conversation")
            response.text ?: "Let's continue our chat."
        } catch (e: Exception) {
            "Chat error: ${e.message}"
        }
    }

    // General purpose AI call
    suspend fun generateContent(prompt: String): String = withContext(Dispatchers.IO) {
        try {
            if (!isApiKeyValid) {
                return@withContext "Demo mode: Please add your Gemini API key in local.properties for full AI functionality."
            }
            val response = generalModel.generateContent(prompt)
            response.text ?: "No response generated"
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }

    // Demo responses when API key is not configured
    private fun getDemoResponse(module: String, userMessage: String): String {
        return when (module) {
            "sathi" -> "Thank you for sharing. I'm here to listen and support you. In demo mode, responses are limited. Please add your Gemini API key in local.properties for full AI-powered conversations."
            "nyaya" -> "I can help you understand your legal rights. For full legal advice powered by AI, please add your Gemini API key in local.properties."
            "dhanshakti" -> "Let's work on your financial goals. For personalized AI-powered financial advice, please add your Gemini API key in local.properties."
            "gyaan" -> "I can help you learn and grow. For AI-powered education recommendations, please add your Gemini API key in local.properties."
            "swasthya" -> "Your health and wellness matter. For AI-powered health insights, please add your Gemini API key in local.properties."
            "raksha" -> "Your safety is our priority. For AI-powered safety planning, please add your Gemini API key in local.properties."
            "arogya" -> "Let's work on your health goals. For AI-powered health advice, please add your Gemini API key in local.properties."
            else -> "Demo mode active. Add Gemini API key for full functionality."
        }
    }

    companion object {
        @Volatile
        private var instance: GeminiService? = null

        fun getInstance(context: Context): GeminiService {
            return instance ?: synchronized(this) {
                instance ?: GeminiService(context.applicationContext).also { instance = it }
            }
        }
    }
}
