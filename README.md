# 🪨 What Beats Rock?

**What Beats Rock?** is a playful, AI-powered game that transforms the classic rock-paper-scissors mechanic into an infinite loop of logic, creativity, and fun. Users are prompted with a question like:

> **What beats rock?**

They respond (e.g., "Paper"), the AI justifies the answer, awards points, and continues the chain:

> **What beats paper?**  
> *User: Fire.*  
> *AI: Fire burns paper. +3 points!*

The game continues until the user repeats an answer or gives an invalid one.


---


## ✨ Features

- 🎮 Conversational, turn-based gameplay
- 🧠 AI justifications for every response
- 🎯 Points awarded based on creativity or logic
- 🔄 Endless chain until an answer is repeated
- 👑 Leaderboard and highscores for more gamation


---


## 🔁 Forked From Itinerar-AI

This project is **forked from [Itinerar-AI](https://github.com/donald-okara/Itinerar-AI)**, an AI demo app originally built in 8 hours as a creative AI starter template.

We reused:

- The chatbot engine and UI architecture
- State handling and conversation modelling

and repurposed it into a game setting for creative fun!


---


## 🧩 Tech Stack

- **Jetpack Compose** for UI
- **Voyager** for navigation
- **Kotlin Coroutines + StateFlow** for state handling
- **Vertex AI** for AI logic
- **Firebase** for persistence


---


## 🚀 Getting Started

1. Clone the repo  
   `git clone https://github.com/your-org/what-beats-rock.git`

2. Open in Android Studio (Giraffe or newer)

3. Connect to your Firebase project with authentication enabled

4. Add your web client id to `local.properties`:  

5. Run on an emulator or physical device (API 26+)

