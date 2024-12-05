# **JamCraft**

**Authors:** Yanis Ouadahi, Samuel Roland, Jarod Streckeisen, Timothée Van Hove

JamCraft is a collaborative web app where multiple users can create music together by mixing and editing sound samples. If you know GarageBand, think of JamCraft as its collaborative cousin, but simplified and designed to work seamlessly in a browser. Users can contribute their own sound samples, manipulate tracks on a shared timeline, and see live updates from others. It's all about making music together in real time.

## **Description of the Application**

JamCraft is built to push the boundaries of real-time collaboration using gRPC technology. The core idea is to have a shared environment where users can upload sound samples into a global library, add them to shared timelines, and edit them collaboratively. Each user sees the same timeline in real time and can interact with it. To make this happen, we use stacked timelines that let multiple users arrange and edit sound samples simultaneously.

We decided to go with Quarkus for the backend and PostgreSQL to store metadata about samples, project configurations, and user activity. The samples themselves will be stored on the server, and we’re still deciding between using database BLOBs or a filesystem. On the frontend, we’ll use a web app to give users a sleek and intuitive interface to interact with the timelines.

The business domain is collaborative music creation. This means we focus on real-time interaction, simplicity, and responsiveness, while allowing users to unleash their creativity.

### **Why gRPC?**

We chose gRPC because it’s designed for fast communication and offers advanced bidirectional streaming, which fits perfectly with the collaborative nature of JamCraft. Unlike traditional HTTP/REST, gRPC uses Protobuf for communication, which are lightweight and fast. This allows us to handle real-time updates, cursor tracking, and global timeline editing with minimal delay.

Here’s what we’ll explore with gRPC:

- **Bidirectional Streaming:** Both clients and the server will continuously send and receive updates, making collaboration instant.
- **Concurrent Streams:** Multiple users will connect simultaneously, testing how gRPC handles the load.
- **High Throughput:** We’ll push thousands of operations per second to measure latency and see how far we can go.
- **Protobuf Flexibility:** As we evolve our features, we’ll test how easy it is to add fields without breaking existing functionality.
- **Security:** We'll implement encryption (TLS/SSL) and explore token-based authentication for user sessions.

## **Features**

### **User Stories**

1. As a user, I want to upload sound samples to a global library so that everyone can use them.
2. As a user, I want to browse the global library to find samples to add to the project.
3. As a user, I want to arrange sound samples on shared timelines so that I can create music with others.
4. As a user, I want to cut and edit sound samples on the timeline so that I can make fine adjustments.
5. As a user, I want to see other users’ mouse cursors moving in real time so that I know who’s doing what.
6. As a user, I want to join a shared session after choosing my username so that I can contribute.
7. As a user, I want to delete or move samples on the timeline so that I can adjust the arrangement.
8. As a user, I want the application to save the project state so that I don’t lose my work if I disconnect.

### **Application Features**

 The main feature is the shared timeline, where everyone sees the same project and can make changes in real time. Each user’s mouse cursor is tracked and displayed, making collaboration more intuitive. There will also be basic editing tools for trimming and splitting samples.

The application is designed for one shared session where everyone connects together. This keeps it simple while focusing on the real-time aspect of collaboration. Users choose a name before joining, making the session personal and easy to identify who’s working on what.

### **Nice to Haves**

If time permits, we’d like to add features like user-specific permissions (e.g., locking tracks so only some users can edit them), advanced editing tools like fade-in/out, and the ability to save multiple sessions or projects. Another cool feature could be live audio chat, but that’s a stretch goal.

## **Architecture**

JamCraft is a multi-tier application with a clear separation of concerns. The frontend is the user interface, the backend handles logic and communication, and the database stores persistent data.

The backend uses gRPC for real-time communication and PostgreSQL to store project data. Samples themselves are stored either as BLOBs in the database or in a dedicated folder on the server. The entire backend runs in a Docker container for consistency and easy deployment.

For messaging, we might use JMS for asynchronous notifications or background tasks, such as notifying users when a new sample is uploaded or performing long-running operations like sample format conversions.

We’ll use diagrams to illustrate the architecture, including:

- A global architecture diagram showing how the frontend, backend, and database interact.
- A relational schema for the database to show how we organize data like samples, timelines, and users.
- A messaging flow diagram for real-time updates, highlighting how gRPC streams keep everything in sync.
