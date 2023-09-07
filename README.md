# Oneweek - Interview Planning Application

## Overview
* [Project information](#project-information)
  * [Used technologies](#used-technologies)
* [API](#api)
  * [Exceptions](#exceptions)
  * [Authentication & authorization](#authentication--authorization)
  * [Implemented API](#implemented-api)
* [Setting-up the project](#setting-up-the-project)
  * [Getting the project](#getting-the-project) 
  * [Configuring docker](#configuring-docker)
* [Additional materials](#additional-materials)
  * [Postman collection](#postman-collection)
  * [Performance testing](#performance-testing)

## Project information
Interview planning application is a RESTful service designed for better communication between interviewers and candidates through coordinators.

Application supports next basic functionality:
  - Creating slots as Interviewer
  - Creating slots as Candidate
  - Creating bookings for already created Interviewer and Candidate slots as Coordinator

### Used technologies
- Java
- Spring
  - Boot
  - Data
    - Hibernate
    - PostgreSQL
  - Security
    - OAuth2
  - Web
- Facebook API

## API
This section describes all implemented and planned to implement endpoints.

### Exceptions

All exceptions in API have next structure:
<pre>
{
    "errorCode": "snake_case_meaningful_string",
    "errorMessage": "English user friendly message"
}
</pre>

You can face next exceptions during using the API:

<table>
  <thead align="center">
    <tr>
      <td>Http Response Status</td>
      <td>Group (use case)</td>
      <td>Error Code</td>
      <td>Error Message</td>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td align="center" rowspan="15">400<br>(Business validation)</td>
      <td align="center" rowspan="2">Booking</td>
      <td align="center">invalid_subject</td>
      <td>Provided subject is invalid.</td>
    </tr>
    <tr>
      <td align="center">invalid_description</td>
      <td>Provided description is invalid.</td>
    </tr>
    <tr>
      <td align="center" rowspan="2">Booking Limit</td>
      <td align="center">invalid_booking_limit</td>
      <td>Value of booking limit is not correct.</td>
    </tr>
    <tr>
      <td align="center">booking_limit_is_exceeded</td>
      <td>Interviewer isn't allowed to have more bookings.</td>
    </tr>
    <tr>
      <td align="center" rowspan="7">Slots Interaction</td>
      <td align="center">slots_not_intersecting</td>
      <td>Provided slots have not free joint time period.</td>
    </tr>
    <tr>
      <td align="center">cannot_edit_this_week</td>
      <td>This week can't be edited.</td>
    </tr>
    <tr>
      <td align="center">invalid_boundaries</td>
      <td>Time boundaries of slot or booking are invalid.</td>
    </tr>
    <tr>
      <td align="center">invalid_day_of_week</td>
      <td>Cannot arrange booking on this day.</td>
    </tr>
    <tr>
      <td align="center">slot_is_booked</td>
      <td>Slot you are trying to occur is booked.</td>
    </tr>
    <tr>
      <td align="center">slot_is_overlapping</td>
      <td>Slot overlaps already existed one.</td>
    </tr>
    <tr>
      <td align="center">slot_is_in_the_past</td>
      <td>New date for this slot is in the past.</td>
    </tr>
    <tr>
      <td align="center" rowspan="4">User Interaction</td>
      <td align="center">self_revoking</td>
      <td>The user cannot change or delete himself.</td>
    </tr>
    <tr>
      <td align="center">user_already_has_role</td>
      <td>This user already has another role.</td>
    </tr>
    <tr>
      <td align="center">not_interviewer</td>
      <td>Provided user is not interviewer.</td>
    </tr>
    <tr>
      <td align="center">not_coordinator</td>
      <td>Provided user is not coordinator.</td>
    </tr>
    <tr>
      <td align="center" rowspan="8">401<br>(Authentication)</td>
      <td align="center" rowspan="8">Auth</td>
      <td align="center">not_authenticated</td>
      <td>You are not authenticated to perform this action.</td>
    </tr>
    <tr>
      <td align="center">bad_facebook_token</td>
      <td>Invalid OAuth access token - cannot parse access token.</td>
    </tr>
    <tr>
      <td align="center">bad_token</td>
      <td>Token does not start with 'Bearer'.</td>
    </tr>
    <tr>
      <td align="center">expired_token</td>
      <td>Token has expired.</td>
    </tr>
    <tr>
      <td align="center">bad_token_signature</td>
      <td>Given JWT signature does not match locally computed signature.</td>
    </tr>
    <tr>
      <td align="center">malformed_token</td>
      <td>Unable to read JWT JSON value.</td>
    </tr>
    <tr>
      <td align="center">unsupported_token</td>
      <td>JWT in a particular format/configuration that does not match the format expected by the application.</td>
    </tr>
    <tr>
      <td align="center">bad_credentials</td>
      <td>Incorrect credentials.</td>
    </tr>
    <tr>
      <td align="center">403<br>(Access denied)</td>
      <td align="center">Auth</td>
      <td align="center">not_authenticated</td>
      <td>You are not authenticated to perform this action.</td>
    </tr>
    <tr>
      <td align="center" rowspan="5">404<br>(Wrong identifier)</td>
      <td align="center">Booking</td>
      <td align="center">booking_not_found</td>
      <td>Booking by given id was not found.</td>
    </tr>
    <tr>
      <td align="center" rowspan="3">Slots Interaction</td>
      <td align="center">candidate_slot_not_found</td>
      <td>Candidate slot by given id was not found.</td>
    </tr>
    <tr>
      <td align="center">interviewer_slot_not_found</td>
      <td>Interviewer slot by given id was not found.</td>
    </tr>
    <tr>
      <td align="center">user_not_found</td>
      <td>User not found.</td>
    </tr>
    <tr>
      <td align="center">User Interaction</td>
      <td align="center">interviewer_not_found</td>
      <td>Invalid interviewer's id in the path.</td>
    </tr>
  </tbody>
</table>

### Authentication & authorization

This section describes all interactions with API authentication.
API uses JWT to authenticate any request and Facebook Token to create JWT.

#### Getting the Facebook Token

To get the Facebook Token you need to know the client id of API.
In order to get it, you can perform:

`GET /oauth2/facebook/v15.0`

As a response, you will get:

<pre>
{
    "clientId": "CLIENT_ID",
    "redirectUri": "REDIRECT_URI",
    "tokenRequestUrl": "https://www.facebook.com/v15.0/dialog/oauth?client_id=CLIENT_ID&redirect_uri=REDIRECT_URI&response_type=token"
}
</pre>

Follow the link presented in `tokenRequestUrl`, pass the Facebook authentication, and you will be redirected to `redirectUri` with your facebook token as `access_token` URL param.  

#### Getting the JWT

To perform any authenticated or authorized request you should provide your Facebook Token to the next endpoint:

`POST /authenticate`

With requiered data parameter `{"facebookToken": "EAAHC..."}`.

As the response you will get JSON Web Token as
<pre>
{
  "token": "eyJhb..."
}
</pre>

The possible exceptions are:
- 401 - bad_facebook_token_exception
- 401 - bad_credentials

Gained JWT should be put in request header as a parameter `Authorization` with value `Bearer eyJhb...`.

#### Edge cases

You can face next exceptions while using JWT within your request:
- 401 - not_authenticated_exception 
- 401 - bad_token_exception
- 401 - expired_token_exception
- 401 - bad_token_signature_exception
- 401 - malformed_token_exception
- 401 - unsupported_token_exception
- 403 - access_denied_exception

#### Users
There are four groups of users:
- Guests - users without authentication
- Candidates - users that have passed authentication but have not Interviewer or Coordinator role
- Interviewers - users that have passed authentication and have Interviewer role
- Coordinator - users that have passed authentication and have Coordinator role

#### User obtention endpoint
Interviewers and Coordinators can perform next endpoint to obtain their user information:

Request: `GET /me`

Response:
<pre>
{
  "email": "example@google.com",
  "role": "INTERVIEWER",
  "id": 123
}
</pre>

The `id` field is present only for users with `INTERVIEWER` or `COORDINATOR` roles.

### Implemented API
This section describes all already implemented business logic endpoints. Section is divided by users.

#### Guest
Guests can only get current and next week of num or [authenticate](#authentication--authorization)

##### Get current number of week:
Request: `GET /weeks/current`

Response: 
<pre>
{
  "weekNum": 44
}
</pre>

##### Get next number of week:
Request: `GET /weeks/next`

Response:
<pre>
{
  "weekNum": 45
}
</pre>

#### Candidate
Candidate can create or update own slots following next requirements:
- Slots must be in future
- Slot has to be 1.5 hours or more and rounded to 30 minutes
- Slot is defined as exact date and time diapason
- Updating is enabled only if there is no bookings for this slot

##### Creating Slot
Request: `POST /candidates/current/slots`

Data parameters:
- `date` - date of candidate slot
- `from` - start time of slot in format HH:mm
- `to` - end time of slot in format HH:mm

Response:
<pre>
{
    "date": "2022-12-06",
    "from": "09:00",
    "to": "17:00"
}
</pre>

Possible [exception](#exceptions) groups:
- Slots Interaction

##### Updating Slot
Request: `POST /candidates/current/slots/{slotId}`

URL parametres:
- `slotId` - id of slot to edit (can be obtained by [getting slots](#getting-slots) endpoint)

Data parametres:
- `date` - date of candidate slot
- `from` - start time of slot in format HH:mm
- `to` - end time of slot in format HH:mm

Response:
<pre>
{
    "date": "2022-12-06",
    "from": "09:00",
    "to": "17:00"
}
</pre>

Possible [exception](#exceptions) groups:
- Slots Interaction

##### Getting Slots
Request: `GET /candidates/current/slots`

Response:
<pre>
{
  "candidateSlotDtoList": [
    {
      "date": "22.01.2022",
      "from": "9:00",
      "to": "17:00""
    },
    {
      "date": "23.01.2022",
      "from": "13:00", 
      "to": "20:00""
    }
  ]
}
</pre>

#### Interviewer
Interviewer can create or update own slots following next requirements:
- Creation until end of Friday (00:00) of current week
- Slot means day of week + time diapason
- Slot boundaries are rounded to 30 minutes
- Slot duration must be greater or equal 1.5 hours
- Slot start time cannot be less than 8:00
- Slot end time cannot be greater than 22:00

Also, Interviewers can set booking limit for next week (maximum amount of booking that Coordinators will be able to create for that certain Interviewer, and it's certain week):
- If maximum number of bookings is not set for certain week, the previous week limit is actual
- If limit was never set, any number of bookings can be assigned to interviewer

Getting of own slots is available only for current and next week.

##### Creating Slot
Request: `POST /interviewers/{interviewerId}/slots`

URL parameters:
- `interviewerId` - id of current Interviewer (can be obtained by [/me](#user-obtention-endpoint) endpoint).

Data parametres:
- `week` - number of week (can be obtained by [/weeks/\*\*](#guest) endpoints)
- `dayOfWeek` - day of week (MON, TUE, WED, THU, FRI)
- `from` - start time of slot in format HH:mm
- `to` - end time of slot in format HH:mm

Response:
<pre>
{
    "interviewerId": 1,
    "interviewerSlotId": 1,
    "week": 50,
    "dayOfWeek": "TUE",
    "from": "16:00",
    "to": "19:00"
}
</pre>

Possible [exception](#exceptions) groups:
- Slots Interaction
- User Interaction

##### Updating Slot
Request: `POST /interviewers/{interviewerId}/slots/{slotId}`

URL parametres:
- `interviewerId` - id of current Interviewer (can be obtained by [/me](#user-obtention-endpoint) endpoint).
- `slotId` - id of slot to edit (can be obtained by [getting slots](#getting-slots-1) endpoint)

Data parametres:
- `week` - number of week (can be obtained by [/weeks/\*\*](#guest) endpoints)
- `dayOfWeek` - day of week (MON, TUE, WED, THU, FRI)
- `from` - start time of slot in format HH:mm
- `to` - end time of slot in format HH:mm

Response:
<pre>
{
    "interviewerId": 1,
    "interviewerSlotId": 1,
    "week": 50,
    "dayOfWeek": "TUE",
    "from": "16:00",
    "to": "19:00"
}
</pre>

Possible [exception](#exceptions) groups:
- Slots Interaction
- User Interaction

##### Getting Slots
Requests:
- `GET /interviewers/{interviewerId}/slots/current` (for getting current week slots) 
- `GET /interviewers/{interviewerId}/slots/next` (for getting next week slots)

URL parameters:
- `interviewerId` - id of current Interviewer (can be obtained by [/me](#user-obtention-endpoint) endpoint).

Response:
<pre>
{
    "interviewerSlotDtoList": [
        {
            "interviewerId": 1,
            "interviewerSlotId": 1,
            "week": 50,
            "dayOfWeek": "TUE",
            "from": "16:00",
            "to": "16:00"
        },
        {
            "interviewerId": 1,
            "interviewerSlotId": 2,
            "week": 50,
            "dayOfWeek": "WED",
            "from": "16:00",
            "to": "16:00"
        }
    ]
}
</pre>

##### Setting Booking Limit
Request: `POST /interviewers/{interviewerId}/bookingLimit`

URL parameters:
- `interviewerId` - id of current Interviewer (can be obtained by [/me](#user-obtention-endpoint) endpoint).

Data parameters:
- `bookingLimit` - limit of bookings per next week

Response:
<pre>
{
    "userId": 1,
    "weekNum": 50,
    "bookingLimit": 127
}
</pre>

Possible [exception](#exceptions) groups:
- User Interaction
- Booking Limit

##### Getting Booking Limit
Requests:
- `GET /interviewers/1/booking-limits/current-week` (for getting current booking limit for current week)
- `GET /interviewers/1/booking-limits/next-week` (for getting current booking limit for next week)

URL parameters:
- `interviewerId` - id of current Interviewer (can be obtained by [/me](#user-obtention-endpoint) endpoint).

Response:
<pre>
{
    "userId": 1,
    "weekNum": 49,
    "bookingLimit": 1000
}
</pre>

#### Coordinator
Coordinator can see all the candidates and interviewers slots, update any Interviewer's time slot, create or update bookings, providing:
- interviewer slot ID
- candidate slot ID
- start and end time (must be 1.5 hours inside both slots)
- subject (0-255 chars) and description (up to 4000 chars)

Also, Coordinator can grant or revoke Interviewer or Coordinator roles by email.
First Coordinator email in the system is defined by [environment variable](#configuring-environmental-variables). 

##### Getting Dashboard
Request: `GET /weeks/{weekNum}/dashboard`

URL parameters:
- `weekNum` - number of week to form dashboard from (can be obtained by [/weeks/\*\*](#guest) endpoints)

Response:
<pre>
{
    "weekNum": 50,
    "dashboard": {
        "2022-12-15": {
            "interviewerSlots": [],
            "candidateSlots": [],
            "bookings": {}
        },
        "2022-12-14": {
            "interviewerSlots": [
                {
                    "interviewerSlotId": 2,
                    "from": "16:00",
                    "to": "19:00",
                    "interviewerId": 2,
                    "bookings": []
                }
            ],
            "candidateSlots": [],
            "bookings": {}
        },
        "2022-12-13": {
            "interviewerSlots": [],
            "candidateSlots": [
                {
                    "candidateSlotId": 1,
                    "from": "10:00",
                    "to": "14:00",
                    "candidateEmail": "bielobrov.8864899@stud.op.edu.ua",
                    "candidateName": "Артур Белобров",
                    "bookings": []
                }
            ],
            "bookings": {}
        },
        "2022-12-12": {
            "interviewerSlots": [
                {
                    "interviewerSlotId": 1,
                    "from": "15:00",
                    "to": "19:00",
                    "interviewerId": 2,
                    "bookings": [
                        1
                    ]
                }
            ],
            "candidateSlots": [
                {
                    "candidateSlotId": 2,
                    "from": "09:00",
                    "to": "17:00",
                    "candidateEmail": "bielobrov.8864899@stud.op.edu.ua",
                    "candidateName": "Артур Белобров",
                    "bookings": [
                        1
                    ]
                }
            ],
            "bookings": {
                "1": {
                    "bookingId": 1,
                    "subject": "Monday interview",
                    "description": "Interview for Java Developer position",
                    "interviewerSlotId": 1,
                    "candidateSlotId": 2,
                    "from": "15:00",
                    "to": "16:30"
                }
            }
        },
        "2022-12-18": {
            "interviewerSlots": [],
            "candidateSlots": [],
            "bookings": {}
        },
        "2022-12-17": {
            "interviewerSlots": [],
            "candidateSlots": [],
            "bookings": {}
        },
        "2022-12-16": {
            "interviewerSlots": [],
            "candidateSlots": [],
            "bookings": {}
        }
    }
}
</pre>

##### Creating Booking
Request: `POST /bookings`

Data parameters:
- `interviewerSlotId` - id of interviewer slot with free time for needed time period
- `candidateSlotId` - id of candidate slot with free time for needed time period
- `from`, `to` - booking boundaries (time period) in format HH:MM (1:30 hours)
- `subject` - subject of booking (0 - 255 chars)
- `description` - description of booking (up to 4000 chars)

Response:
<pre>
{
    "interviewerSlotId": 1,
    "candidateSlotId": 2,
    "from": "15:00",
    "to": "16:30",
    "subject": "Monday interview",
    "description": "Interview for Java Developer position"
}
</pre>

Possible [exception](#exceptions) groups:
- Slots Interaction
- Booking
- Booking Limit
- User Interaction

##### Updating Booking
Request: `POST /bookings/{booking-id}`

URL parameters:
- `booking-id` - id of booking to update

Data parameters:
- `interviewerSlotId` - id of interviewer slot with free time for needed time period
- `candidateSlotId` - id of candidate slot with free time for needed time period
- `from`, `to` - booking boundaries (time period) in format HH:MM (1:30 hours)
- `subject` - subject of booking (0 - 255 chars)
- `description` - description of booking (up to 4000 chars)

Response:
<pre>
{
    "interviewerSlotId": 1,
    "candidateSlotId": 2,
    "from": "15:00",
    "to": "16:30",
    "subject": "Monday interview",
    "description": "Interview for Java Developer position"
}
</pre>

Possible [exception](#exceptions) groups:
- Slots Interaction
- Booking
- Booking Limit
- User Interaction

##### Deleting Booking
Request: `DELETE /bookings/{booking-id}`

URL parameters:
- `booking-id` - id of booking to delete

Response:
<pre>
{
    "interviewerSlotId": 1,
    "candidateSlotId": 2,
    "from": "15:00",
    "to": "16:30",
    "subject": "Monday interview",
    "description": "Interview for Java Developer position"
}
</pre>

Possible [exception](#exceptions) groups:
- Booking

##### Getting users by role
Requests:
- `GET /users/interviewers` - getting list of all interviewers in the system
- `GET /users/coordinators` - getting list of all coordinators in the system

Response:
<pre>
{
    "users": [
        {
            "email": "azofer77@gmail.com",
            "role": "INTERVIEWER",
            "id": 2
        }
    ]
}
</pre>

##### Granting role by email
Requests:
- `POST /users/interviewers` - granting interviewer role by email
- `POST /users/coordinators` - granting coordinator role by email

Data parameters:
- `email` - attached to facebook email of user to grant the appropriate role

Response:
<pre>
{
    "email": "example@gmail.com",
    "role": "INTERVIEWER",
    "id": 3
}
</pre>

Possible [exception](#exceptions) groups:
- User Interaction

##### Revoking role by email

While operating revoke functionality notice, that coordinator can not revoke himself. 

Requests:
- `DELETE /users/interviewers/{id}` - to revoke interviewer role by id
- `DELETE /users/coordinators/{id}` - to revoke coordinator role by id

URL parameters:
- `id` - id of user to delete

Response:
<pre>
{
    "email": "example@gmail.com",
    "role": "INTERVIEWER",
    "id": 3
}
</pre>

Possible [exception](#exceptions) groups:
- User Interaction

## Setting-up the project
This section describes all needed steps to launch the application.
You can set up the project as it is via [docker](#configuring-docker) or via [your IDE](#running-via-ide).

### Getting the project
First of all, you need to get the project. You can do this by two ways:
- [Getting zip project file](#getting-zip-project-file)
- [Cloning the repository](#cloning-the-repository)

#### Getting zip project file
To download project in zip follow the [link](https://github.com/GrEFeRFeeD/intellistart-java-2022-oneweek/archive/refs/heads/main.zip).

After downloading, unzip the archive and go to __intellistart-java-2022-oneweek-main__ directory.

#### Cloning the repository
To clone the repository run the console and type:

`git clone https://github.com/GrEFeRFeeD/intellistart-java-2022-oneweek.git`

After cloning is done change the directory to __intellistart-java-2022-oneweek-main__ by the following command:

`cd intellistart-java-2022-oneweek-main`

### Configuring docker
This section describes all needed steps to launch application via docker.

#### Configuring environmental variables
Before launching the application via docker, you need to created `api.env` file with next environment variables.
The example of such file represented in `example.env` file. The needed variables are:
- `APPLICATION_PORT` - port on which application will be run
- `JWT_SECRET` - defines secret work to assign the JWT
- `JWT_VALIDITY` - validity of JWT in seconds
- `JWT_CACHING` - how long JWT will be cached in seconds
- `FIRST_COORDINATOR_EMAIL` - facebook account attached email of first coordinator that will be automatically added to DB  
- `FACEBOOK_CLIENT_ID` - application client id provided by facebook
- `FACEBOOK_SECRET` - application secret provided by facebook
- `FACEBOOK_REDIRECT_URI` - URI to which you will be redirected after oauth2. Configures by facebook application
- `HIBERNATE_DDL_AUTO` - hibernate DDL launch mode:
  - `validate`: validates the schema, makes no changes to the database.
  - `update`: updates the schema.
  - `create`: creates the schema, destroying previous data.
  - `create-drop`: drop the schema when the SessionFactory is closed explicitly, typically when the application is stopped.
  - none: does nothing with the schema, makes no changes to the database.
- `DATABASE_PORT` - port on which database will be run
- `POSTGRES_USER` - name of default postgresql user
- `POSTGRES_PASSWORD` - password of default postgresql user
- `POSTGRES_DB` - postgresql database name
- `REDIS_HOSTNAME` - hostname of the server to connect redis DB
- `REDIS_PORT` - port of the given hostname to connect redis DB

#### Launching the application
Once the `api.env` is created with proper variables you can launch docker with application through running the following command:

`docker-compose --env-file api.env up`

### Running via IDE

Open the [gained from repository](#getting-the-project) project with your IDE.

After you open the project, set up the 11 JDK do the `maven clean` operation.

You can launch the application with [all needed environmental variables](#configuring-environmental-variables) by configuring them in your IDE.
In this case you need to set up the PostgreSQL database with appropriate __POSTGRES_USER__, __POSTGRES_PASSWORD__, __DATABASE_PORT__ and __POSTGRES_DB__ values. 

As an option you can launch the application without the inner DBMS. In order to do that, replace your __/src/main/resources/application.yml__ configuration file with:
<pre>
server:
  port: ${APPLICATION_PORT}

jwt:
  secret: ${JWT_SECRET}
  validity: ${JWT_VALIDITY}

first-coordinator-email: ${FIRST_COORDINATOR_EMAIL}

spring:
  security:
    oauth2:
      client:
        registration:
          facebook:
            clientId: ${FACEBOOK_CLIENT_ID}
            clientSecret: ${FACEBOOK_SECRET}
            redirectUri: ${FACEBOOK_REDIRECT_URI}

  jpa:
    show-sql: true
    generate-ddl: true
    hibernate:
      ddl-auto: ${HIBERNATE_DDL_AUTO}
</pre>

In such case you still must satisfy next environmental variables:
- APPLICATION_PORT
- JWT_SECRET
- JWT_VALIDITY
- JWT_CACHING
- FIRST_COORDINATOR_EMAIL
- FACEBOOK_CLIENT_ID
- FACEBOOK_SECRET
- FACEBOOK_REDIRECT_URI
- REDIS_HOSTNAME
- REDIS_PORT

Execute the __intellistart.interviewplanning.InterviewPlanningApplication__ class in order to run the project.

## Additional materials
This section describes all additional information about the project that does not match the topics below and materials putted to __/docs__ directory of the repository.

### Postman collection
The project has [Postman collection](https://github.com/GrEFeRFeeD/intellistart-java-2022-oneweek/tree/main/docs/postman) to demonstrate the work of implemented endpoints. 

### Performance testing
The project has passed performance testing with JMeter.

The performance profile could be found by the [link](https://github.com/GrEFeRFeeD/intellistart-java-2022-oneweek/tree/main/docs/jmeter).

To run the tests, put the StartDataLoader in __intellistart.interviewplanning.initialization__ instead of already existed file.

