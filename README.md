<div align="center">
  <h1>BMI Calculator App with AI-Based Personalized Health Tips</h1>
  <p><i>A modern Android application developed using Kotlin and Jetpack Compose.</i></p>
</div>

<hr />

<h2><u>Project Overview</u></h2>
<p>
  The BMI Calculator App is more than just a simple calculation tool; it combines health tracking with smart, personalized recommendations. 
  Users can securely manage their health data and receive insights.
</p>

<h3><u>Latest Update: Advanced AI Agent</u></h3>
<p>
  I am currently updating the AI agent integration to provide even more personalized health tips. By leveraging deeper user profile data and advanced prompt engineering, the app will deliver more precise diet plans, workout routines, and motivational content tailored to individual goals.
</p>

<h3><u>Core Capabilities</u></h3>
<ul>
  <li><b>Authentication:</b> Register and Login using Email or Google Authentication.</li>
  <li><b>Profile Management:</b> Create and delete multiple user profiles.</li>
  <li><b>Health Tracking:</b> Input height and weight with built-in validation.</li>
  <li><b>AI Integration:</b> Receive personalized health tips, diet suggestions, and exercise recommendations.</li>
</ul>

<hr />

<h2><u>Technologies Used</u></h2>
<table>
  <tr>
    <th>Category</th>
    <th>Tools & Services</th>
  </tr>
  <tr>
    <td><b>Development</b></td>
    <td>Android Studio, Kotlin, Jetpack Compose</td>
  </tr>
  <tr>
    <td><b>Backend</b></td>
    <td>Firebase Authentication, Firestore Database</td>
  </tr>
  <tr>
    <td><b>AI Engine</b></td>
    <td>Groq AI API (Agent Update in Progress)</td>
  </tr>
  <tr>
    <td><b>Concurrency</b></td>
    <td>Kotlin Coroutines</td>
  </tr>
</table>

<hr />

<h2><u>BMI Calculation & Categories</u></h2>
<p>The application calculates BMI using the standard formula: <b>BMI = Weight (kg) / Height (m²)</b>.</p>

<table>
  <thead>
    <tr>
      <th>Category</th>
      <th>BMI Range</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Underweight</td>
      <td>Less than 18.5</td>
    </tr>
    <tr>
      <td>Normal</td>
      <td>18.5 - 24.9</td>
    </tr>
    <tr>
      <td>Overweight</td>
      <td>25 - 29.9</td>
    </tr>
    <tr>
      <td>Obese</td>
      <td>30 and above</td>
    </tr>
  </tbody>
</table>

<hr />

<h2><u>How to Build and Run</u></h2>
<ol>
  <li><b>Clone the Project:</b> Open the project in Android Studio.</li>
  <li><b>Firebase Configuration:</b> 
    <ul>
      <li>Create a Firebase project and enable Auth and Firestore.</li>
      <li>Place the <code>google-services.json</code> inside the <code>app/</code> directory.</li>
    </ul>
  </li>
  <li><b>Sync:</b> Click "Sync Now" in Android Studio to update Gradle dependencies.</li>
  <li><b>Run:</b> Connect a device with USB Debugging enabled or use an emulator, then click Run.</li>
</ol>

<hr />

<h2><u>Features & UI</u></h2>
<ul>
  <li><b>Modern UI:</b> Card-based layout with a Floating Action Button.</li>
  <li><b>Dynamic Feedback:</b> Real-time data updates and dynamic BMI color badges.</li>
  <li><b>User Privacy:</b> Secure cloud storage and password visibility toggles.</li>
</ul>

<div align="">
  <p><i>Developed as a demonstration of clean, responsive UI and AI-driven health suggestions.</i></p>
</div>
