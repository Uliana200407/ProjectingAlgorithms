# pa-andreeva-ul

## Application Launch Instructions

To launch the application, you need to follow these steps:
## Labwork1
### Choice 1: Launching DefaultExternalMergeSort

To generate the dataset for sorting from the request and launch defExternaMergeSort execute the following commands in your terminal:
- Compile the DefaultExternalMergeSortFile Java project
```bash
javac DefaultExternalMergeSortFile.java

```

- Run DefaultExternalMergeSortFile 
```bash
java DefaultExternalMergeSortFile
```

### Choice 2: Launching ExternalMergeSort
To generate the dataset for sorting from the request and launch ExternaMergeSort execute the following commands in your terminal:
- Compile the ExternalMergeSortFile Java project

```bash
javac ExternalMergeSortFile.java
```
- Run java ExternalMergeSortFile to launch main algorithm and get sorted file with elapsed time
```bash
java ExternalMergeSortFile
```
### Choice 3: Launching all the app together in the main file.
To launch the main file with two algorithms with the switcher for the users choice, execute the following commands in your terminal:
- Compile the Main Java project
```bash
javac Main.java
```
- Run java Main to launch two algorithms and get sorted file with elapsed time
```bash
java Main
```
### Step 4: Launching Unit Tests

To run unit tests for the project, follow these steps:

1. **Download and Unpack Apache Maven:**
   - Download the Apache Maven binary archive (apache-maven-3.9.4-bin.zip).
   - Unpack the downloaded archive to your desired location.

2. **Open the Project in Terminal:**
   - Ensure that you have the `pom.xml` file in your project directory.

3. **Configure Maven in Terminal:**
   - Add Maven to your system's PATH by running the following command in your terminal (replace `<Maven_Home>` with the path to the Maven folder you unpacked):
     ```bash
     export PATH=<Maven_Home>/bin:$PATH
     ```

4. **Verify Maven Installation:**
   - Confirm that Maven has been downloaded and configured successfully by running the following command:
     ```bash
     mvn -version
     ```

5. **Compile the Project with Maven:**
   - Compile the project by running the following command in your project directory:
     ```bash
     mvn compile
     ```

6. **Launch Unit Tests:**
   - Run the unit tests with Maven using the following command:
     ```bash
     mvn test
     ```

This will execute the unit tests for your project.

## Labwork2

### Choice 1: MazeSolver

To generate the dataset for sorting from the request and launch MazeSolver execute the following commands in your terminal:
- Compile the Java code using the javac command:
```bash
javac MazeSolver.java

```

- After successfully compiling the code, you can run the program using the java command:
```bash
java MazeSolver
```
## Labwork3
### Choice 1:
- Save this code in a file named HelloApplication.java in your working directory.
- Open the terminal and navigate to the directory where the HelloApplication.java file is located. You can do this using the cd command:
```bash
cd /path/to/directory/with/HelloApplication.java

```
- After moving to the correct directory, you can use the javac command to compile the program:
```bash
javac HelloApplication.java

```
- After compilation, you can run the program using the java command:
 ```bash
java com.example.labwork3.HelloApplication

 ```
## Labwork4

 ```bash
# Navigate to the directory where your Java file is located
cd /path/to/your/java/file/directory

# Compile the Java file (replace KnapsackGeneticAlgorithm.java with your actual file name)
javac KnapsackGeneticAlgorithm.java

# Run the Java program (replace KnapsackGeneticAlgorithm with your main class name)
java KnapsackGeneticAlgorithm

 ```

## Labwork5

This Java program implements a genetic algorithm to solve the knapsack problem. The knapsack problem is a classic optimization problem where the goal is to select a subset of items with maximum total value without exceeding weight and volume constraints.

**Clone the Repository:**

   ```bash
   git clone https://github.com/your-username/genetic-algorithm-knapsack.git
   cd genetic-algorithm-knapsack
  ```


**Compile and launch the program:**
   ```bash

javac GeneticAlgorithm.java

java GeneticAlgorithm
 ```
