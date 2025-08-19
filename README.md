<img width="1271" height="609" alt="state-machine(2)" src="https://github.com/user-attachments/assets/1b20dc51-b5d6-43f0-8858-6c7e80470989" />

# State Machine with Spring

This project demonstrates an implementation of a state machine using **Spring State Machine**. It models the lifecycle of a payment process with defined states and events that trigger transitions between those states.

## Features

- **States** representing the payment status (e.g., `PENDING`, `COMPLETED`, `FAILED`).
- **Events** that drive state transitions (e.g., `APPROVE`, `RETRY`, `CANCEL`).
- **State machine configuration** using Spring State Machine to define states, transitions, and actions.
- **Reactive service layer** utilizing Project Reactor for asynchronous, non-blocking processing of payment events.

## Overview

The state machine manages the flow of payment processing, ensuring that state transitions occur only on valid events. This approach helps in managing complex workflows with clear state management and event-driven behavior.

## Technologies Used

- Java 17+
- Spring Boot
- Spring State Machine
- Project Reactor (Mono & Flux)
- Maven

## Usage

1. Clone the repository
2. Build the project with Maven
3. Run the Spring Boot application
4. Use the service layer to send events and observe state transitions reactively

## Author

Felipe Cardoso

---

For more details, visit the repository or contact the author.
