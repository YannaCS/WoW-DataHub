# WoW DataHub 🎮

A comprehensive database management system for World of Warcraft-inspired game data, featuring full CRUD operations (Create, Read, Update and Delete
), business rule enforcement, ETL processes, and analytics dashboards.

## 📋 Table of Contents
- [Project Overview](https://github.com/YannaCS/WoW-DataHub/blob/main/README.md#-project-overview)
- [Features](https://github.com/YannaCS/WoW-DataHub/blob/main/README.md#-features)
- [Technology Stack](https://github.com/YannaCS/WoW-DataHub/blob/main/README.md#%EF%B8%8F-technology-stack)
- [Database Schema](https://github.com/YannaCS/WoW-DataHub/blob/main/README.md#%EF%B8%8F-database-schema)
- [Project Structure](https://github.com/YannaCS/WoW-DataHub/blob/main/README.md#-project-structure)
- [Setup Instructions](https://github.com/YannaCS/WoW-DataHub/blob/main/README.md#-setup-instructions)
- [Usage Guide](https://github.com/YannaCS/WoW-DataHub/blob/main/README.md#-usage-guide)
- [Business Rules](#business-rules)
- [ETL Process](#etl-process)
- [API Integration](#api-integration)

## 🎯 Project Overview

WoW DataHub is a full-stack web application that manages player and character data for a multiplayer online role-playing game (MMORPG). The system supports complex relationships between players, characters, items, currencies, and game statistics while enforcing strict business rules through database constraints and triggers.  

### Key Highlights
- **Interconnected database tables** with full referential integrity
- **Business rule enforcement** through SQL triggers and Java validation
- **Dynamic ETL processes** for data population from WoW API
- **Real-time analytics** with 9 database views (7 shown on dashboard)
- **Responsive web interface** with Bootstrap 5

## ✨ Features

### Core Functionality
- **Character System**: 
  - Search characters by Player's last name
  - Sort the result by chosen fields and order
  - View character's detailed report
  - Switch the equipped weapon with things only in the inventory and follow the business rules
- **Inventory Management**: 
  - Full inventory system with stack size validation
  - Equipment slots with type-specific constraints
- **Item System**: Weapons, gear, and consumables with stat bonuses
- **Currency System**: Multiple currencies with cap enforcement

### Advanced Features
- **Business Rule Validation**: Automated enforcement of game mechanics
- **ETL Integration**: 
  - Default data loader for static game content
  - Dynamic data generator for players and characters
  - Blizzard API integration
- **Analytics Dashboard**: Real-time statistics and visualizations
- **Character Reports**: Detailed views of character inventory and status

## 🛠️ Technology Stack

### Backend
- **Java** - Core application logic
- **Jakarta Servlets** - Web request handling
- **JDBC** - Database connectivity
- **MySQL** - Database management system

### Frontend
- **JSP (JavaServer Pages)** - Dynamic web pages
- **Bootstrap 5.3** - Responsive UI framework
- **Chart.js** - Data visualization
- **JavaScript** - Interactive features

### Build & Deployment
- **Eclipse IDE** - Development environment
- **Apache Tomcat 11** - Application server

## 🗄️ Database Schema

The database consists of 18 tables organized into several domains:

### Core Tables
- `Players` - User accounts with activity tracking
- `Characters` - Game characters linked to players
- `Clans` - Race and clan combinations
- `Items` - Base table for all game items

### Item Hierarchy
- `Equipments` → `Weapons` & `Gears`
- `Consumables` - Potions and food items
- `EquipmentBonuse` & `ConsumableItemBonuse` - Stat modifiers

### Character Data
- `CharacterStatistics` - Character attribute values
- `CharacterUnlockedJob` - Job progression tracking
- `CharacterWealth` - Currency holdings
- `Inventory` & `EquippedItems` - Item management

### Game Configuration
- `Statistics` - Available character attributes
- `Currencies` - Game currencies with caps
- `JobsForGear` - Equipment job restrictions

[ERD](https://github.com/YannaCS/WoW-DataHub/blob/ea631aedf792ee2096e4ea6c207319b77664d9af/assets/ERD.jpg)

## 📁 Project Structure

```
WoW-DataHub/
├── src/main/java/game/
│   ├── dal/                 # Data Access Layer
│   │   ├── ConnectionManager.java
│   │   ├── PlayersDao.java
│   │   ├── CharactersDao.java
│   │   └── ... (other DAOs)
│   ├── model/              # Data Models
│   │   ├── Players.java
│   │   ├── Characters.java
│   │   └── ... (other models)
│   ├── servlet/            # Web Controllers
│   │   ├── HomeController.java
│   │   ├── FindCharacter.java
│   │   └── ... (other servlets)
│   ├── service/            # Business Logic
│   │   └── BusinessRulesService.java
│   ├── etl/                # ETL Processes
│   │   ├── DefaultDataETL.java
│   │   ├── WoWDataETL.java
│   │   └── WoWApiClient.java
│   ├── sql/                # SQL Scripts
│   │   └── BusinessRuleTriggers.java
│   └── Driver.java         # Main application
├── src/main/webapp/
│   ├── Home.jsp
│   ├── FindCharacter.jsp
│   ├── CharacterDetailReport.jsp
│   ├── WeaponUpdate.jsp
│   ├── ETL.jsp
│   └── WEB-INF/
│       └── web.xml
```

## 🚀 Setup Instructions

### Prerequisites
- Java 17 or higher
- MySQL 8.0
- Apache Tomcat 11
- Eclipse IDE (recommended)

### Database Setup
1. Install MySQL and create a user:
```sql
CREATE USER 'root'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON *.* TO 'root'@'localhost';
```

2. Update database credentials in `ConnectionManager.java` if needed:
```java
private static final String USER = "root";
private static final String PASSWORD = "password";
private static final String HOSTNAME = "localhost";
private static final int PORT = 3306;
```

### Application Setup
1. Clone the repository:
```bash
git clone https://github.com/YannaCS/WoW-DataHub.git
cd WoW-DataHub
```

2. Import project into Eclipse:
   - File → Import → Existing Maven Projects
   - Select the project directory

3. Run the initial setup:
   - Execute `Driver.java` to create schema and load default data
   - This creates the database, tables, views, and triggers

### Tomcat Configuration
1. Add project to Tomcat server in Eclipse
2. Start Tomcat server
3. Access application at: `http://localhost:8080/WoW-DataHub/home`

## 📖 Usage Guide

### Home Dashboard
- View overall statistics and analytics
- Monitor daily active players
- See job and clan distributions
- Track top players by level and wealth
<img width="1509" alt="image" src="https://github.com/user-attachments/assets/a8a4c626-ce7f-4bb1-8be2-d83e08208bf3" />
<img width="957" alt="image" src="https://github.com/user-attachments/assets/f38c06ef-d9c9-432d-992b-5ec94ce58682" />
<img width="1433" alt="image" src="https://github.com/user-attachments/assets/bf5d6087-c35d-45d8-8b3d-dd148ed52a64" />


### Character Management
1. **Find Characters**: Search by player last name
2. **Character Details**: View complete character information
3. **Update Weapon**: Change character's equipped weapon
<img width="1280" alt="image" src="https://github.com/user-attachments/assets/b3e971c8-7287-4d1e-9628-c1b0d89f328e" />
<img width="1461" alt="image" src="https://github.com/user-attachments/assets/8cd2f64c-5cc0-42c8-bac1-0dd40fc1ce9c" />
<img width="1449" alt="image" src="https://github.com/user-attachments/assets/a4f043b4-525b-4cae-87b2-9eb678d9ca8c" />
<img width="1458" alt="image" src="https://github.com/user-attachments/assets/4070263c-5bf2-4960-95e5-cb3c3298eeef" />


### ETL Process
1. Navigate to ETL page
2. Click "Run Dynamic ETL" to add more players and characters
3. Monitor record counts as data is added
<img width="1150" alt="image" src="https://github.com/user-attachments/assets/bc258907-4f16-42ec-8f5e-e0a7eb994efe" />

### Optional: WoW API Integration
To enable real Blizzard API data:
1. Get credentials from [Blizzard Developer Portal](https://develop.battle.net/)
2. Update `/src/main/java/game/config/WoWApiConfig.java`:
```java
public static final String CLIENT_ID = "your_client_id";
public static final String CLIENT_SECRET = "your_client_secret";
```

## 📏 Business Rules

The system enforces several critical business rules:

### Character Rules
- Character names must be unique (firstName + lastName combination)
- Characters must always have a weapon equipped
- Character's current job determined by equipped weapon

### Currency Rules
- Currency amounts cannot exceed defined caps
- Weekly acquisition limits enforced where applicable

### Item Rules
- Stack sizes cannot exceed item's maximum
- Equipment slots enforce type restrictions
- Only weapons allowed in MAIN_HAND slot

### Job Rules
- Job levels range from 1-100
- XP requires an associated job level
- Characters must have at least one unlocked job

[All Business Rules](https://github.com/YannaCS/WoW-DataHub/blob/deaf7341603ddaf7b894f690b0b5fd039ff128eb/assets/business%20rules.pdf)

## 🔄 ETL Process

### Default Data ETL
Loads static game content on initial setup:
- 18 clans across 5 races
- 20 character statistics
- 20 WoW currencies
- 100+ weapons
- 150+ gear items
- 100+ consumables

### Dynamic Data ETL
Generates player and character data:
- Creates 50 players with activity tracking
- Generates 100 characters with proper relationships
- Adds inventory, equipped items, and wealth
- Ensures all business rules are satisfied

## 🔌 API Integration

The system includes optional integration with Blizzard's WoW API:
- Fetches real realm data
- Retrieves actual item information
- Falls back to generated data if unavailable
