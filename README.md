# **CodewaveService**

## **Overview**

Codewave is an online audiobook generation service primarily based on Spring Boot, MySQL, and Redis. It allows users to upload various types of text files (epub, txt, pdf) and choose different Text-to-Speech (TTS) models to generate audio.

The system supports adding different processing models based on configuration files and configuring different concurrency levels for each model to adapt to their capabilities and configuring different maximum number of words to be processed in a single request . 

By using a message queue implemented with Redis, the system can effectively schedule tasks and manage peak loads. The task processing flow, implemented with the Chain of Responsibility pattern, allows for flexible combination and execution of functionalities like file splitting, result concatenation, and uploading to S3.

## **Key Features**

Codewave's core capability lies in the integration and management of TTS models for audio generation tasks. Built upon TTS models, it offers

- **TTS Model Integration:**
    - Flexible configuration of the supported concurrency for models and the maximum text length allowed per request. This enables Codewave to process texts longer than the length typically permitted by models
- **Concurrent Processing:**
    - A message queue implemented via Redis, coupled with thread pools, facilitates the processing of a large number of concurrent requests and efficient utilization of model capabilities
- **Task Management**：  ****
    - 1 ****Management of task status. Users can monitor the status of ongoing tasks - waiting, running, success, or failed.
    - 2 Management of task results. With Codewave, you can easily view and listen to your generated audiobooks online.

# How to use

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

# System Design
**Overall**
![codewave design doc](https://github.com/Rajahn/CodewaveService/assets/39303094/30497563-3f83-4c77-ac3f-b467b764af47)

## **Initialization Phase**

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

## **Chain-of-responsibility model realizes process handling**

![pro1](https://github.com/Rajahn/CodewaveService/assets/39303094/e6e2f35f-74e0-40d4-a44d-da0ea7516639)

### Core structure

1. **`TextToAudioChain`**: this is the main driver of the chain of responsibility. It initializes and executes a series of **`Processors`** to complete the conversion from text to audio.
2. **`Processor`**: a processing unit where each **`Processor`** accomplishes a specific task. For example, one **`Processor`** might extract text from a PDF, while another **`Processor`** might convert text to audio. After each **`Processor`** has processed the task, it passes the result to the next **`Processor`**.
3. **`ProcessingContext`**: this is a data container used to pass data between the various **`Processors`** in the chain of responsibility. It may contain information such as text, files, binary data, etc., depending on the needs of the Processor.

![pro2](https://github.com/Rajahn/CodewaveService/assets/39303094/08f76cbc-45e3-40d6-9a6a-ddf2f03fcbd3)

Factory Patterns for Selecting a Suitable File Processor

1. **`ConverterProcessorFactory`**: This is a factory class responsible for generating the appropriate **`ConverterProcessor`** instance based on the input file type (e.g. PDF, TXT or EPUB).
2. **`PdfConverter`**, **`TxtConverter`**, **`EpubConverter`**: these are specific implementations of **`ConverterProcessor`**, each converter is responsible for processing a specific type of file and extracting text from it.
![pro3](https://github.com/Rajahn/CodewaveService/assets/39303094/d8726e55-2d6d-45cb-a43e-922f2f354b84)

Other processors

1. **`TextPreprocessor`**: responsible for pre-processing the raw text extracted from the file, e.g. clearing specific characters, formatting the text, etc.
2. **`TextFileWriter`**: writes the pre-processed text to a new file, usually a temporary file, for subsequent processing.
3. **`TextSplitter`**: Splits the text into smaller parts or sections, which makes the audio generation step more efficient and improves quality.
4. **`AudioGenerator`**: Converts each text segment to audio.
5. **`AudioMerger`**: Merges all individual audio clips into one complete audio file.
6. **`S3Uploader`**: Uploads the merged audio file to S3 or other cloud storage.

![pro4](https://github.com/Rajahn/CodewaveService/assets/39303094/9aff56e1-3826-4959-8fe2-c0aa04e1ed1c)

Application 1 of the decorator pattern , Logging Processing and Execution Time Logging by Stage

The **`LoggingProcessorDecorator`** is an application of the Decorator pattern that is intended to enhance the functionality of the **`Processor`** without modifying its structure. The main purpose of this decorator is to add logging before and after the execution of the processor, thus allowing us to better trace and diagnose each step in the processing chain

![pro5](https://github.com/Rajahn/CodewaveService/assets/39303094/4369f950-16e9-4ebf-91ba-effeee182aa7)

Application 2 of the decorator pattern, based on custom annotations to implement the retry mechanism, add the annotation on the critical processor that needs to be retried, then in the TextToAudioChain run to this processor, it will be wrapped with a retry decorator, to achieve the critical exception throwing and retry operation.

1. **`CriticalProcessor`**: This is an annotation to mark certain processors as "critical". If a processor marked as critical fails, it throws a critical exception, which may cause the entire processing chain to terminate.
2. **`ProcessorException`**: This exception is thrown when a processor fails. This exception can carry an instance of the failed processor, allowing the caller to know which processor had the problem.
3. **`RetryableProcessorDecorator`**: This is another decorator that tries to re-execute the processor it decorates until a predetermined number of retries is reached.

![pro6](https://github.com/Rajahn/CodewaveService/assets/39303094/80d478fc-3409-45b5-9acc-8c6e2b90a924)

The builder pattern updates the state of a task as it executes


