# Distributed Queue Service

This project implements a simple distributed queue service with a RESTful API for managing queues of messages. The service supports both a simple in-memory queue service (`SimpleQueueMessageService`) and a distributed version (`DistributedQueueMessageService`).

## REST API

### Enqueue a Message

**Endpoint:** `POST /api/{queueName}`

**Description:** Adds a new message to the specified queue.

**Request:**
```json
{
  "payload": "Your message content"
}
```
**Response:**
- Status code: 200 OK


### Dequeue a Message

**Endpoint:** `GET /api/{queueName}`

**Description:** Retrieves the next message from the specified queue. Returns a 204 No Content status if there's no message in the queue after the specified timeout (default is 10 seconds).

**Query Parameters:**
- `timeout` (optional): Timeout in milliseconds (default is 10000).

**Response:**
- Status code: 200 OK
```json
{
  "payload": "Your message content"
}
```

## Implementations
### SimpleQueueMessageService
This implementation is a basic in-memory queue service. It is suitable for single-node applications where no distributed messaging is required. The service is ideal for testing and development purposes.

### DistributedQueueMessageService
This implementation extends the functionality to support a distributed environment. Nodes can discover each other in the network and keep their queues in sync. Each node runs an instance of this service, and messages are broadcasted to other nodes for synchronization.

### Getting Started
1. Clone the repository: git clone https://github.com/kumetin/bunnymq.git
2. Navigate to the project directory: `cd bunnymq`
3. Build the project: `mvn clean install`
4. Run the application: `java -jar target/bunnymq.jar`

By default, the application runs on http://localhost:8080. You can test the API using your preferred REST client.

## Configuration
- The `DistributedQueueMessageService` assumes that each node runs on a different port. The server.port property in the application.properties file is used to identify each node.

- Node discovery is simulated in this example. In a real-world scenario, you would need a more sophisticated mechanism for node discovery and communication.

## Contributing
Feel free to contribute to this project by submitting issues or pull requests. Your feedback and contributions are highly appreciated.

## License
This project is licensed under the ZIBI License.
That means you're free to copy, duplicate, modify and wipe your bum with it cause it doesn't really worth much.


