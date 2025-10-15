# HackHub: A Platform for Hackathon Management

This repository contains the project for the development of **HackHub**, a web platform for managing hackathon events.

## Project Goal

The objective is to create a web platform that supports the organization of hackathons, the registration of teams, and the submission of their projects. Hackathons are group events in which teams participate.

## Hackathon Lifecycle

Each hackathon on the platform follows a well-defined lifecycle, divided into four states:
* **Open for Registration**
* **In Progress**
* **In Review**
* **Concluded**

## Platform Actors

The system defines several roles (actors) with specific permissions and functionalities.

### Visitor
* An unauthenticated user.
* Can view public information about hackathons.
* Must register and log in to use other features.

### User
* A registered user who manages their participation through teams.
* Can create a new team by inviting other users.
* Can accept an invitation to join an existing team.
* **Constraint:** Can only belong to one team at a time.

### Team Member
* Can view the list of all hackathons.
* Can register their team for a hackathon.
* Can submit the project entry by the specified deadline.
* Can update the submission until the deadline.

### Staff Member
* A general category that includes Organizers, Judges, or Mentors assigned to a specific hackathon.
* Can view the list of all hackathons in the system.
* Can access team submissions, but only for the hackathons they are assigned to.

### Organizer
* A Staff Member who creates new hackathons.
* Defines essential information: name, rules, deadlines, dates, location, prize money, maximum team size, a Judge, and one or more Mentors.
* Can add more Mentors even after the hackathon has been created.
* Declares the winning team after the Judge's evaluation.

### Mentor
* A Staff Member who supports teams during the event.
* Views support requests from teams.
* Can propose a call, which is scheduled through an external calendar system.
* Can report a rule violation to the Organizer.

### Judge
* A Staff Member in charge of evaluating submissions at the end of the hackathon.
* Views all submissions for the hackathon they are assigned to.
* For each submission, provides an evaluation consisting of a written review and a numerical score (from 0 to 10).

## External Systems

The platform will integrate with the following external services:

* **Calendar**: An external service for scheduling and booking calls between Mentors and teams.
* **Payment System**: An external service for disbursing the prize money to the winning team.

## Technical Constraints and Details

* **Development**: The project must be developed in `Java` and subsequently ported to `Spring Boot`.
* **Presentation Layer**: The presentation layer is at the developer's discretion and can be limited to a command-line interface and/or `REST APIs`.
* **Design Patterns**: The use of at least two design patterns, other than the `Singleton`, is required.
