# **CodewaveService**

## **Overview**

Codewave is an online audiobook generation service primarily based on Spring Boot, MySQL, and Redis. It allows users to upload various types of text files (epub, txt, pdf) and choose different Text-to-Speech (TTS) models to generate audio.

The system supports adding different processing models based on configuration files and configuring different concurrency levels for each model to adapt to their capabilities. 

By using a message queue implemented with Redis, the system can effectively schedule tasks and manage peak loads. The task processing flow, implemented with the Chain of Responsibility pattern, allows for flexible combination and execution of functionalities like file splitting, result concatenation, and uploading to S3.

### **Key Features**

Codewave's core capability lies in the integration and management of TTS models for audio generation tasks. Built upon TTS models, it offers

- **TTS Model Integration:**
    - Flexible configuration of the supported concurrency for models and the maximum text length allowed per request. This enables Codewave to process texts longer than the length typically permitted by models
- **Concurrent Processing:**
    - A message queue implemented via Redis, coupled with thread pools, facilitates the processing of a large number of concurrent requests and efficient utilization of model capabilities
- **Task Management**：  ****
    - 1 ****Management of task status. Users can monitor the status of ongoing tasks - waiting, running, success, or failed.
    - 2 Management of task results. With Codewave, you can easily view and listen to your generated audiobooks online.

## How to use

1 Establish  mysql and redis  connection, run the database initialization sql

2 Create two empty folders named text and tts in the resources directory

3 Fill in the ak, sk in S3Util with your own, and make sure you fill in all the ak, sk you need, if you want to use a service like openai.

4 Running the program, you can check that all interfaces are working correctly from swagger portal

## How to add **new TTS models**

To integrate a new TTS model into CodewaveService, follow these steps:

1. **Update `application.yml` Configuration:**
    - Add new configuration items for the TTS model in the **`application.yml`** file.
    - These configurations should include:
        - The model's name.
        - The supported number of concurrent requests the model can handle
        - The maximum character length the model can process in a single request.
2. **Implement a New Class in `TTSModels`:**
    - Create a new class within the **`TTSModels`** directory of the project.
    - This class must be registered as a bean using `@Component` with model name
    - Implement the **`TTSModel`** interface in this class
        
        input: String text you want to generate
        
        output: String  Base64 encoded MP3.
        

### **Example: Integrating OpenAI's TTS Model Into Codewave**

As an illustrative example, let's consider the process of integrating an OpenAI TTS model:

- **Step 1:** Add OpenAI Model Configuration in **`application.yml`**
    - Add entries for the OpenAI model, specifying its name, the number of requests it can handle concurrently, and the maximum length of text it can process at once.
- **Step 2:** Create **`OpenaiModel`** Class in **`TTSModels`**
    - In the **`TTSModels`** directory, create a new class named **`OpenaiModel`**.
    - Register this class as a bean in the project's configuration.
    - Implement the **`TTSModel`** interface, where the primary functionality would be to interact with the OpenAI API to convert text to speech.
    - This implementation should handle constructing the network request, including authentication and sending the text data to OpenAI's API, receiving the audio response, and converting it into the required format (Base64 encoded MP3).

## System Design

### **Initialization Phase**

### **Configuration File Parsing**

At system startup, the **`TTSModelProperties`** class is responsible for reading the TTS model configurations defined in **`application.yml`**, including the model's name, concurrency level, and other necessary attributes.

### **Creation and Binding of Thread Pools and Processing Strategies**

The **`TaskProcessingConfig`** class, based on configurations in **`TTSModelProperties`**, creates corresponding thread pools for each model type. Additionally, it is responsible for creating instances of **`TaskProcessingStrategy`** for each model type. This process utilizes both **Factory** and **Strategy** patterns, ensuring the flexibility and replaceability of processing strategies.

### **Setting up Redis Message Queues**

For each model type, the system creates corresponding message queues in Redis. The **`TaskListener`** class is instantiated and begins to listen to these Redis queues.

![TTSModel  design](https://github.com/Rajahn/CodewaveService/assets/39303094/5ad03485-bf6d-44a6-8496-059c425aa581)

### **Request Processing Phase**

### **Publishing of Tasks and Queue Storage**

Upon user request arrival, the system first saves the task's status in the database, then serializes the task into JSON format and stores it in the corresponding Redis queue. This step achieves peak shaving, helping to smooth out high request volumes during peak periods.

### **Task Monitoring and Execution**

For each model, the corresponding **`TaskListener`** retrieves tasks from the Redis queue and executes them using the designated processing strategy and thread pool. The execution process involves a **Chain of Responsibility** design, including steps such as file splitting, model invocation, result concatenation, and uploading to S3. This design makes the entire processing flow more flexible and expandable.

### **Application of Design Patterns**

- **Strategy Pattern**: Implemented in processing strategies, allowing the choice of different processing logics based on the model type of the task.
- **Factory Pattern**: Used in creating thread pools and processing strategy instances, providing customized components for different model types.
- **Chain of Responsibility Pattern**: Implemented in the task processing flow, where each step is designed as a link in the chain, making the process easy to modify and expand.
