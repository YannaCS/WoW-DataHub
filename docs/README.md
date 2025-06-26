
**Professional World of Warcraft Data Management and Analytics Platform**

A comprehensive database management system designed for MMORPG data analytics, featuring ETL processes, real-time dashboards, and scalable architecture.

## 🚀 Features

- **ETL Pipeline**: Extract, Transform, Load WoW character data
- **Professional Dashboard**: Real-time analytics and insights
- **Database Management**: Complete MMORPG database schema
- **Web Interface**: User-friendly management interface
- **API Integration**: Ready for Battle.net API integration
- **Scalable Architecture**: Enterprise-grade design patterns

## 🏗️ Architecture

```
WoW DataHub
├── ETL Pipeline (Extract, Transform, Load)
├── Database Layer (MySQL with 15+ tables)
├── Business Logic (Services & DAOs)
├── Web Interface (Servlets & JSP)
└── Dashboard (Professional Analytics UI)
```

## 📋 Prerequisites

- **Java 17+**
- **MySQL 8.0+**
- **Apache Tomcat 9.0+**
- **Web Browser** (Chrome, Firefox, Safari)

## 🛠️ Installation

### 1. Clone Repository
```bash
git clone https://github.com/yourusername/wow-datahub.git
cd wow-datahub
```

### 2. Database Setup
```bash
# Create database
mysql -u root -p < src/main/resources/sql/schema.sql

# Load reference data
mysql -u root -p < src/main/resources/sql/initial-data.sql

# Optional: Load test data
mysql -u root -p < src/main/resources/sql/test-data.sql
```

### 3. Configuration
Update `src/main/resources/application.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/CS5200Project
db.username=your_username
db.password=your_password
```

### 4. Build & Deploy
```bash
# Compile Java files
javac -cp "webapp/WEB-INF/lib/*" src/main/java/game/**/*.java -d build/

# Create WAR file
jar -cvf wow-datahub.war -C webapp . -C build .

# Deploy to Tomcat
cp wow-datahub.war $TOMCAT_HOME/webapps/
```

## 🚀 Usage

### Running ETL Process
```bash
# Run standalone ETL
java -cp "webapp/WEB-INF/lib/*:build" game.Driver

# Expected output:
# INFO: Starting WoW DataHub ETL Process
# INFO: Extracted 5 characters from WoW API
# INFO: Successfully processed character: Arthas
# INFO: ETL Process completed successfully
```

### Web Interface
1. Start Tomcat server
2. Navigate to `http://localhost:8080/wow-datahub`
3. Access the professional dashboard

### Dashboard Features
- **Player Analytics**: Track player progression and statistics
- **Character Management**: View and manage character data
- **Economy Tracking**: Monitor in-game currency and wealth
- **ETL Monitoring**: Real-time ETL process status
- **System Health**: Database and application metrics

## 📊 Database Schema

### Core Tables
- **Players**: Real-world players/users
- **Characters**: In-game characters
- **Items/Equipment/Weapons**: Item hierarchy
- **Clans**: Guilds and organizations
- **Currencies**: Game currencies and caps
- **Statistics**: Character attributes and stats

### ETL Tables
- **CharacterWealth**: Currency holdings
- **CharacterUnlockedJob**: Class progression
- **CharacterStatistics**: Stat values
- **EquipmentBonuse**: Item bonuses

## 🔧 Development

### Project Structure
```
src/main/java/game/
├── model/          # Data models
├── dal/            # Data access layer
├── etl/            # ETL processes
├── servlet/        # Web controllers
└── Driver.java     # Main application

webapp/
├── WEB-INF/
│   ├── lib/        # JAR dependencies
│   └── views/      # JSP templates
├── css/            # Stylesheets
├── js/             # JavaScript
└── dashboard.html  # Main dashboard
```

### Adding New Features
1. **New Model**: Add to `game.model` package
2. **New DAO**: Add to `game.dal` package
3. **New ETL**: Add to `game.etl` package
4. **New Web Page**: Add JSP to `webapp/WEB-INF/views/`

## 🧪 Testing

### Unit Tests
```bash
# Run all tests
java -cp "webapp/WEB-INF/lib/*:build:test" org.junit.runner.JUnitCore game.test.AllTests
```

### Integration Tests
```bash
# Test ETL process
java -cp "webapp/WEB-INF/lib/*:build" game.Driver

# Test database connection
java -cp "webapp/WEB-INF/lib/*:build" game.dal.ConnectionManager
```

## 📈 Performance

- **Database**: Optimized indexes for common queries
- **ETL**: Batch processing for large datasets
- **Web**: Efficient servlet-based architecture
- **Caching**: Configurable caching for frequent queries

## 🔒 Security

- **SQL Injection**: Parameterized queries throughout
- **Input Validation**: Server-side validation for all inputs
- **Authentication**: Ready for user authentication system
- **Logging**: Comprehensive audit logging

## 📚 API Documentation

### ETL Endpoints
- `POST /etl/run` - Trigger ETL process
- `GET /etl/status` - Check ETL status
- `GET /etl/logs` - View ETL logs

### Data Endpoints
- `GET /api/players` - List all players
- `GET /api/characters` - List all characters
- `GET /api/statistics` - Get system statistics

## 🤝 Contributing

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** your changes (`git commit -m 'Add amazing feature'`)
4. **Push** to the branch (`git push origin feature/amazing-feature`)
5. **Open** a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Authors

- **Your Name** - *Initial work* - [GitHub Profile](https://github.com/yourusername)

## 🙏 Acknowledgments

- **World of Warcraft** - Inspiration for the data model
- **Blizzard Entertainment** - For the amazing game universe
- **Apache Tomcat** - Web server platform
- **MySQL** - Database management system
- **Bootstrap** - Frontend framework

## 📞 Support

For support, email support@wowdatahub.com or join our [Discord](https://discord.gg/wowdatahub).

## 🗺️ Roadmap

- [ ] **Real Battle.net API Integration**
- [ ] **Advanced Analytics Dashboard**
- [ ] **Multi-server Support**
- [ ] **REST API Expansion**
- [ ] **Mobile App Integration**
- [ ] **Machine Learning Insights**

---

**Made with ❤️ for the World of Warcraft community**