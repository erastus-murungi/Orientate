# Orientate

## Table of Contents


1. [Overview](#Overview)
2. [Product Spec](#Product-Spec)
3. [Wireframes](#Wireframes)
4. [Schema](#Schema)


## Overview

### Description

An app for insititutions to send students critical information and for students to connect and help out each other during orientations. Students can opt to join chat rooms with other students with similar interests, where they can help out each other.


### App Evaluation

* **Category:** Social / Business
* **Mobile:** App is primarily meant for Mobile. Provies maps make it easy to go to location events, and chatrooms for speaking with other students. Website is viewing only.
* **Story:** Makes the whole process of orientation easier by providing a centralized location for all important orientation events and announcements. The app also helps set up support systems for students finding it hard to connect with others in new environments e.g international students.


* **Market:** Any student going through orientation in a new school.
* **Habit:** This app would let people have an easy daily checkin for important news schools have for them and events they must attend.

* **Scope:** This app would start with orientations for schools only but would soon include orientations for events and conferences.


## Product Spec
### User Stories

#### 1. Required Must-have Stories
* Students and institutions can set up accounts
* Schools can send orientation schedules to students
* Orientation schedules can be stored in a server and persisted locally
* Students can join chatrooms and be given recommendations on chatrooms to join *rethink this, maybe make it a little basic*
* A feed which details crucial announcements and events to attend
* Links to support systems offered by the school



#### 2. Optional Nice-to-have stories
* Improved matching algorithms *don't overdo this*
* Inlined webviews
* Tag events and announcements to get reminders


### 2. Screen Archetypes
* Log in or sign up screen
    * Student can log in using their Facebook Account, ... (more if time allows) or just create a fresh new Orientate account
* Invites fragment view
    * Student can receive and verify invites from schools

* Dashboard
    * Students can see traverse to either an events screen, announcements screen, or a chatrooms screen

* User Page
    * Users can edit their profile info and configure settings for their individual accounts

* Events
   * Provide locations to the events, and a MapView to location
   
### 3. Wireframes
![](https://i.imgur.com/jy8SM3r.jpg)


## Schema

#### User
|  Property     | Type     | Description |
|  --------     | -------- | --------                           |
|  objectID     | String     | Primary Key|
|  emailVerified| Boolean | Indicates whether a users email has been verified after signing up
| createdAt     | Date     | Timestamp recorded when user was first created
| username      | String   | Another primary key |
| password      | String   | User's unique password|
| email         | String   | User's unique email |
| is_student     | Boolean | Is set to true if the User is a student and false if user is an Institution |
| profile_image | File     | the user's profile picture |

#### Student
|  Property     | Type      | Description |
| ---------     | ------    | -------     |
| objectID      | String    | Primary key |
| first_name    | String    | Required User's first name|
| middle_name   | String    | Optional User's second name|
| last_name     | String    | Required User's last name |
| user          | Pointer<User> | Foreign Key |
| dob           | String     | Optional user's date of birth |
| enrolled_at   | Pointer<Institution>| Foreign Key |



#### Institution
| Property | Type | Description |
| -------- | ---- | --------     |
| objectID | String | Primary Key  |
| createdAt | Date | Timestamp indicating when an Institution is added to the database
| updatedAt | Date | Timestamp indicating the time when Institution info is updated|
| url | String | A web address for the school's homepage |


#### Event
| Property | Type | Description |
| -------- | ---- | --------     |
| objectID | String | Primary Key  |
| createdAt | Date | Timestamp indicating when an Event is added to the database |
| updatedAt | Date | Timestamp indicating the time when Event info is updated|
|event_content | Pointer<EventContent>| Extra info about the event |
| is_recurring | Boolean | True if the event is scheduled to happen multiple times. Defaults to False | 
| upvote_count | Number | the number of upvotes the event has gotten from students. Defaults to 0 |
| owned_by | Pointer<Institution> | Foreign Key. Points to the institution hosting the event |
| must_attend | Boolean | True if the event is mandatory for all and False otherwise |
| starting_on | Date | The datetime when the event will start. This field is required |
| ending_on   | Date | The datetime when the event is expected to end. This field is optional|
| where | GeoPoint | The location of the event |
| sponsor  | Array[String]  | The names of the sponsors of the event |
| url   | String | Link to a webpage for extra information about event |


#### Announcement
| Property | Type | Description |
| -------- | ---- | --------     |
| objectID | String | Primary Key  |
| createdAt | Date | Timestamp indicating when an Announcement is added to the database |
| updatedAt | Date | Timestamp indicating the time when Announcement info is updated|
| urgency_level | Number | Level of importance of the announcement. Is translated to user a user readable string by the application |
| title | String | The title of the announcement |
| body  | String | Further explanation regarding the announcement |
| owned_by | Pointer<Institution> | The institution which released the announcement |
| url | String | A link where additional info about the announcement may be found |











