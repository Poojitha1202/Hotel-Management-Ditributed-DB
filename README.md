# 🏨 Distributed Database Builder — Hotel Management System

A distributed database project that breaks a hotel management system into two specialized database fragments running on separate cloud instances, with a Java application that automatically routes queries to the correct fragment based on a Global Data Catalog.

Built as a team project for **CSCI 5408 — Distributed and Cloud Computing** at Dalhousie University.

---

## What it does

A typical hotel system has dozens of entities — guests, rooms, reservations, inventory, suppliers, transactions, employees, and so on. Putting all of that into a single monolithic database works, but it doesn't scale well and mixes very different workloads (guest bookings vs. supply chain vs. financials).

This project splits the system into two MySQL databases hosted on Google Cloud:

- **Fragment 1 — Hotel & Guest Management:** Everything related to the guest-facing side of the hotel — reservations, rooms, employees, events, feedback, loyalty programs, dining, maintenance.
- **Fragment 2 — Business Operations & External Relations:** Everything behind the scenes — inventory, suppliers, transactions, invoices, partnerships, marketing.

A Java application sits on top of both fragments. When a user submits a SQL query, the app:

1. Parses the query to extract the table name
2. Looks up that table in a **Global Data Catalog (GDC)** to find which fragment hosts it
3. Connects to the correct database fragment and runs the query
4. Returns the result

The key idea is **transparency** — the user doesn't need to know which fragment holds which table. They just write a query, and the app figures out where to send it.

---

## Architecture

```
                    ┌─────────────────────┐
                    │   User Query        │
                    │ "SELECT * FROM ..." │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │  Java Application   │
                    │  (Query Router)     │
                    └──────────┬──────────┘
                               │
                  ┌────────────┼────────────┐
                  │            │            │
                  ▼            ▼            ▼
          ┌──────────────┐  ┌──────┐  ┌──────────────┐
          │  Fragment 1  │  │ GDC  │  │  Fragment 2  │
          │   (GCP VM)   │  │      │  │   (GCP VM)   │
          │              │  └──────┘  │              │
          │ Hotel &      │            │  Business    │
          │ Guest Mgmt   │            │  Operations  │
          └──────────────┘            └──────────────┘
```

The **Global Data Catalog (GDC)** lives inside Fragment 1 and stores metadata for every table in the system — table name, site name, site IP, data type, last updated, and so on. It's what makes the routing transparent.

---

## Tech stack

- **Java** — query router and GDC lookup application
- **MySQL** — two database instances (one per fragment)
- **Google Cloud Platform (GCP)** — Compute Engine VMs hosting the MySQL instances
- **JDBC** — database connectivity
- **draw.io** — ER diagrams
- **IntelliJ IDEA** — development environment

---

## Database design

The system models 41 entities covering everything a real hotel needs to operate:

**Guest-facing:** Guest, Room, RoomDetail, RoomAmenities, Reservation, ReservationServices, ReservationHistory, DiningReservation, RestaurantBar, Service, Amenity, Feedback, LoyaltyProgram, GuestLoyaltyProgram, LoyaltyProgramHistory

**Operational:** Hotel, Employee, EmployeeSchedule, EmployeeScheduleHistory, Departments, Event, EventSpaces, Maintenance, MaintenanceHistory, Incident, IncidentHistory, Careers

**Business & external:** Inventory, Product, Supplier, Address, Transaction, TransactionHistory, Invoice, Payment_Method, OffersPromotion, OfferHistory, Newsletter, Social_Media, Partnerships, travel_agent

### Design issues we worked through

A few real problems came up during ER design:

- **Fan trap** between Guest, Reservation, Room, and Service — the original model made it ambiguous which services belonged to which reservation. We fixed this by introducing a `ReservationServices` join entity that ties services directly to a reservation rather than to the guest.
- **Time-variant design issues** in entities whose values change over time (Reservation, OffersPromotion, Incident, Transaction, EmployeeSchedule, Maintenance, LoyaltyProgram). For each one, we added a corresponding `History` entity in a 1:M relationship, so the original entity holds the current state and the history table tracks all changes over time.
- **Normalization** — the schema satisfies 2NF and 3NF. No partial dependencies (every non-key attribute depends on the full primary key) and no transitive dependencies (no non-key attribute depends on another non-key attribute).

---

## How fragmentation works

Since the project doesn't model multiple hotel chains, we went with **database-level fragmentation** rather than table-level. Each fragment is its own MySQL instance on a separate GCP VM, and each focuses on one functional area of the hotel.

A few entities (like `Address`, `Hotel`, and `OffersPromotion`) appear in both fragments. This is intentional — replicating these reduces cross-database joins and keeps each fragment self-contained for its core workload.

---

## How the query router works

The Java application has four main classes:

- **`Sprint2`** — entry point. Prompts the user for a SQL query and orchestrates the lookup → connect → execute flow.
- **`QueryParser`** — pulls the table name out of the SQL query using a regex (handles `FROM`, `UPDATE`, and `INTO` keywords).
- **`SiteIpFetcher`** — queries the GDC to find which site hosts the table the user is asking about.
- **`DatabaseExecutor`** — opens a JDBC connection to the correct fragment, runs the query, and returns or prints the result.

So when someone runs `SELECT * FROM Product`, the parser pulls out `Product`, the GDC tells us it's on Fragment 2 (`34.123.28.213`), and the executor connects there and runs the query. If they instead run `SELECT * FROM Guest`, the GDC routes it to Fragment 1 (`104.154.95.157`). The user never sees any of this routing.

---

## What's in this repo

- ER diagrams (initial draft + final issue-free version)
- Dependency diagram covering all 41 entities
- DDL statements for both fragments
- GDC schema and sample entries
- Java source code for the query router (`Sprint2`, `QueryParser`, `SiteIpFetcher`, `DatabaseExecutor`)
- Final report with full design walkthrough

GitLab: [csci5408-sprint4](https://git.cs.dal.ca/himanshi/csci5408-sprint4)

---

## Team

This was a four-person team project. Everyone contributed to research, design, and implementation:

- **Himanshi Verma** — B00976966
- **Bhavya Mukesh Dave** — B00982225
- **Jay Jagdishbhai Patel** — B00981520
- **Poojitha Mummadi** — B00951877

---

## Course

**CSCI 5408 — Distributed and Cloud Computing**
Dalhousie University, Winter 2024

---
