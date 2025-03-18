# User Stories for Teacher's Gradebook and Report Card System

## **Authentication**
- **As a teacher**, I want to register an account so that I can securely access the gradebook system.
- **As a teacher**, I want to log in with my username and password so that I can access the system and manage student data.

---

## **Group Management**
- **As a teacher**, I want to create groups of students so that I can organize students by class or subject.
- **As a teacher**, I want to view a list of all groups so that I can manage my classes effectively.
- **As a teacher**, I want to update group details so that I can correct or modify group information.
- **As a teacher**, I want to delete groups so that I can remove obsolete or unused groups.

---

## **Student Management**
- **As a teacher**, I want to add new students to a group so that I can maintain accurate records for each class.
- **As a teacher**, I want to view a list of all students in a group so that I can easily identify and manage them.
- **As a teacher**, I want to edit student information (e.g., name, email, phone) so that I can update their details as needed.
- **As a teacher**, I want to delete students from the system so that I can remove inactive or incorrect records.

---

## **Group-Student Relationship**
- **As a teacher**, I want to assign students to groups so that I can organize them by class or subject.
- **As a teacher**, I want to view a list of students by group so that I can track their progress and performance.
- **As a teacher**, I want to remove students from a group so that I can remove inactive or incorrect records.

---

## **Grade Categories and Weights Management**
- **As a teacher**, I want to define grade categories (e.g., assignments, exams, projects)  and weights so that I can organize and record grades accurately.
- **As a teacher**, I want to view a list of grade categories and their corresponding weights so that I can verify the grading system.

---

## **Grade Management**
- **As a teacher**, I want to add grades for assignments, exams, and projects so that I can record students' performance in each category.
- **As a teacher**, I want to view a list of grades for a student so that I can track their progress.
- **As a teacher**, I want to edit grades so that I can correct any errors in recording.
- **As a teacher**, I want to delete grades so that I can remove incorrect or irrelevant entries.
- **As a teacher**, I want to calculate overall averages and weighted averages for a student so that I can assess their performance fairly.

---

## **Report Card Generation**
- **As a teacher**, I want to generate a report card for a student so that I can provide detailed feedback on their performance.
- **As a teacher**, I want to include grades categorized by assignments, exams, and projects in the report card so that it is comprehensive.
- **As a teacher**, I want to export the report card as a PDF file so that I can share it with students or parents.
- **As a teacher**, I want to print the report card directly from the system so that I can provide hard copies.

---

## **General System Features**
- **As a teacher**, I want the application to handle login errors gracefully so that I can recover from mistakes without frustration.
- **As a teacher**, I want the application to validate all inputs (e.g., grades, weights, student details) so that I can avoid entering incorrect data.
- **As a teacher**, I want the system to have a clean and intuitive interface so that I can navigate and perform tasks efficiently.

---

## **Localization**

### **Language Selection Functionality**
**As** a teacher,  
**I want** to select my preferred language in the application,  
**So that** I can view and interact with GradeBook in my familiar language.  

**Acceptance Criteria**:  
- Users can select a language from the settings page.  
- The interface updates immediately upon language change without requiring a restart.  

---

### **UI Text Internationalization Support**  
**As** a developer,  
**I want** all UI text to be stored in translatable resource files,  
**So that** I can easily add or modify translations for different languages.  

**Acceptance Criteria**:  
- All UI text is loaded from resource files (e.g., `.properties`, `JSON`, or `XML`).  
- No hardcoded UI text is allowed.  
- The default language is English, and missing translations fall back to English.  

---

### **UI Text Translation**
**As** a translator,
**I want** to translate all UI text into the specified language,
**So that** users can interact with the application in their preferred language.

**Acceptance Criteria**:
- All UI text is translated accurately and contextually.
- Translations are reviewed and approved by a language expert.
- Translated text is consistent with the application's tone and style.

---

### **Multilingual Database Storage**  
**As** a developer,
**I want** the database to store and manage text content in multiple languages,  
**So that** course names, assignment descriptions, and other data can support multilingual users.  

**Acceptance Criteria**:  
- The database schema supports multilingual fields (e.g., `title_en`, `title_zh`).  
- UTF-8 encoding is used to support all languages.  

---

### **Language-Based Data Querying**
**As** a teacher,
**I want** to query data based on my selected language,
**So that** I can view course details, student information, and grades in my preferred language.

**Acceptance Criteria**:
- Language data can be queried based on the user’s selected language.  
- The system displays data in the user’s selected language.
- On report cards, the system displays all grades information in the selected language.

---

### **Character Encoding and Locale Management**  
**As** a GradeBook system architect,  
**I want** to ensure the database correctly stores and retrieves multilingual text,  
**So that** all user input is processed correctly and displayed in the right date, currency, and format.  

**Acceptance Criteria**:  
- All text fields use UTF-8 encoding.  
- The database supports formatting for different locales (e.g., `YYYY-MM-DD` vs. `DD/MM/YYYY`).  
- Queries return properly formatted data based on user locale settings.  

---

### **Sprint Backlog Update**  
**As** a Scrum Master,  
**I want** to add localization-related tasks to the product backlog,  
**So that** the team can clearly track localization work in future iterations.  

**Acceptance Criteria**:  
- The product backlog includes tasks for UI and database localization.  
- Each task has defined priority, estimated effort, and acceptance criteria.  

---

### **Translation Resource Management**  
**As** a GradeBook project manager,  
**I want** to identify necessary translation resources and content management tools,  
**So that** language translations can be efficiently completed and seamlessly integrated into the system.  

**Acceptance Criteria**:  
- Identify required translation resources (e.g., professional translators, machine translation APIs).  
- Select appropriate content management tools (e.g., POEditor, Crowdin).  
- Ensure translation workflows are compatible with development processes.  

---








