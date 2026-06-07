# System Architecture Analysis (Java Online Exam System)

This document details the functions and purposes of each class in the Online Exam System, following UML (Unified Modeling Language) principles to describe the relationships between classes. The content is designed to be comprehensive yet accessible, allowing readers without a system development background to fully understand the project architecture and use it as a reference for presentations (PPT).

## 0. Quick Start Guide: How to Run & Operate the System

### Prerequisites
- Ensure you have the Java Development Kit (JDK) installed on your system.
- Ensure your terminal (such as VS Code Terminal or PowerShell) is pointing to the root directory of this project.

### Compilation & Execution
1. **Compile the system**:
   Open your terminal and run the following command to compile all `.java` files into `.class` files:
   ```bash
   javac -encoding UTF-8 -d . com/exam/*.java com/exam/ui/*.java
   ```
2. **Run the system**:
   After a successful compilation, start the application by running:
   ```bash
   java com.exam.Main
   ```

### Operating Instructions
When the graphical user interface (GUI) launches, you will see two main roles to choose from:
- **Student**: Click the "Student" card, enter your name, and select an available exam from the dropdown menu to start taking a test. You can interact with the options and see your real-time progress. At the end, you will receive your score.
- **Instructor**: Click the "Instructor" card and enter your name to access the dashboard. From here, you can:
  - **View Results**: Check the scores of all students who have taken exams.
  - **Add Exam**: Create a new blank exam paper.
  - **Add Question**: Add new multiple-choice or true/false questions to an existing exam.

## 1. Core Domain Models

### `User` Hierarchy (User Roles)
* **`User` (Abstract Class)**: 
  * **Purpose**: Serves as the base abstract class for all physical users in the system, providing a consistent identity framework. It encapsulates basic attributes (`id`, `name`) and declares the abstract method `executeRole()`.
  * **Relationship**: Forms a **Generalization** relationship with `Student` and `Instructor`. External systems operate uniformly using this type.
* **`Student` (Concrete Class)**: 
  * **Purpose**: Inherits from the `User` class and represents a student taking an exam. It implements the `executeRole` method, transferring system control to the dedicated student interface.
  * **Relationship**: **Generalization** from the `User` abstract class. Interacts with the external interface layer via **Delegation**.
* **`Instructor` (Concrete Class)**: 
  * **Purpose**: Inherits from the `User` class and represents a system administrator or exam creator. It implements the `executeRole` method, directing the execution flow to the dedicated instructor interface.
  * **Relationship**: **Generalization** from the `User` abstract class. Interacts with the external interface layer via **Delegation**.

### `Question` Hierarchy (Questions and Grading)
* **`Gradable` (Interface)**: 
  * **Purpose**: Defines the behavioral contract for "gradable" objects in the system. Declares the `calculateScore(Answer)` method responsible for returning the corresponding score.
  * **Relationship**: **Realization** by the `Question` class.
* **`Question` (Abstract Class)**: 
  * **Purpose**: Serves as the base abstract class for all specific question types. Encapsulates common question data structures, such as question ID (`id`), description (`description`), and maximum score (`maxScore`).
  * **Relationship**: **Realization** of the `Gradable` interface. Forms a **Generalization** relationship with `MultipleChoiceQuestion` and `TrueFalseQuestion`.
* **`MultipleChoiceQuestion` (Concrete Class)**: 
  * **Purpose**: Specifically handles the business logic for "single-choice questions". Encapsulates the list of options (`options`) and the correct answer (`correctAnswer`), and implements specific text-matching grading.
  * **Relationship**: **Generalization** from the `Question` abstract class.
* **`TrueFalseQuestion` (Concrete Class)**: 
  * **Purpose**: Handles the logic for "true/false questions". Records the correct answer as a boolean value (`correctAnswer`) and implements specific boolean-matching grading.
  * **Relationship**: **Generalization** from the `Question` abstract class.
* **`Answer` (Data Class)**: 
  * **Purpose**: Records the actual answer provided by a student for a single question, containing the question ID (`questionId`) and the answer string (`content`).
  * **Relationship**: Acts as a Data Transfer Object (DTO), used as an input parameter for grading by the `Gradable` interface within this hierarchy.

### `Exam` Hierarchy (Tests and Papers)
* **`ExamPaper` (Data Class)**: 
  * **Purpose**: Represents a static "default exam template", containing a title (`title`) and description (`description`). It provides `getTotalPossibleScore()` for quick calculation of the maximum score.
  * **Relationship**: Internally holds `Question` objects, forming an **Aggregation** relationship.
* **`Exam` (Instance Class)**: 
  * **Purpose**: Represents an "active, dynamic testing instance" with a unique identifier (`examId`). Responsible for iterating through questions to settle the final score (`submit`).
  * **Relationship**: Internally holds `Question` objects, forming an **Aggregation** relationship. Upon completion, it **Creates** a `Result` object.
* **`Result` (Data Class)**: 
  * **Purpose**: Encapsulates the final settlement information after a student completes an exam, including the name, exam title, final score, and feedback.
  * **Relationship**: Created by the `Exam` object in the same hierarchy, then handed over to the external layer for storage.

## 2. Data & Service Layers

### Data Access Objects (DAO Pattern)
* **`ExamDao` (Interface)**: 
  * **Purpose**: Defines the standard contract for data access operations regarding "exam papers and question banks". Includes reading (`loadExams`) and adding (`addQuestion`).
  * **Relationship**: **Realization** by the `CsvExamDao` class within the hierarchy.
* **`CsvExamDao` (Concrete Class)**: 
  * **Purpose**: Handles low-level I/O operations for serializing and deserializing question bank data with the `questions.csv` file.
  * **Relationship**: **Realization** of the `ExamDao` interface. Briefly relies on external factory objects via **Dependency** to instantiate questions during the read process.
* **`ResultDao` (Interface)**: 
  * **Purpose**: Defines the standard contract for read/write operations of "exam scores". Includes saving (`saveResult`) and reading (`getAllResults`).
  * **Relationship**: **Realization** by the `CsvResultDao` class within the hierarchy.
* **`CsvResultDao` (Concrete Class)**: 
  * **Purpose**: Formats grades and writes them into the `results.csv` file.
  * **Relationship**: **Realization** of the `ResultDao` interface.

### Service & Repository
* **`ExamCatalog` (Repository Class)**: 
  * **Purpose**: Caches the list of exam papers in memory, centrally managing all `ExamPaper` templates.
  * **Relationship**: Holds an external array of `ExamPaper` objects, forming an **Aggregation** relationship. Also holds the external `ExamDao` via **Dependency Injection**.
* **`ExamService` (Service Class)**: 
  * **Purpose**: The core of the system's business logic. It provides high-level APIs for creating exam instances, assigning questions, and executing the grading workflow (`conductExam`).
  * **Relationship**: Briefly relies on the external `Exam` object via **Dependency** to perform calculations.
* **`QuestionFactory` (Factory Class)**: 
  * **Purpose**: Centralizes the complex logic of object creation. Instantiates specific question subclasses by evaluating strings.
  * **Relationship**: Responsible for **Creating** concrete `MultipleChoiceQuestion` or `TrueFalseQuestion` objects.

## 3. User Interface Layer (View & Controller)

* **`MainMenu` / `StudentMenu` / `InstructorMenu`**: 
  * **Purpose**: Constitutes the system's menus and workflow guidance modules, responsible for identity verification and dialog input for business workflows.
  * **Relationship**: Transfers control among each other via **Delegation**. Responsible for **Creating** subsequent MVC view components.
* **`ExamFrame` (View)**: 
  * **Purpose**: The graphical GUI window component entirely responsible for "visual presentation". Includes progress labels, question text, option buttons, etc.
  * **Relationship**: Separated from the Controller, unilaterally **Associated** by the Controller to passively update the screen.
* **`ExamController` (Controller)**: 
  * **Purpose**: Acts as the communication hub between View and Model. It maintains the exam state, handles button events, and finally delegates score settlement to the external service layer.
  * **Relationship**: **Associated** with the `ExamFrame` within the hierarchy, and calls external services to update logic.
