# WoW DataHub Setup Guide 🛠️

Complete step-by-step setup instructions for the WoW DataHub system.

## 📋 Prerequisites Checklist

Before starting, ensure you have:

- [ ] **Java 17 or higher** installed
- [ ] **MySQL 8.0 or higher** installed and running
- [ ] **Apache Tomcat 9.0+** (optional, for web deployment)
- [ ] **Git** for version control
- [ ] **IDE** (Eclipse, IntelliJ IDEA, or VS Code)

## 🔧 Step 1: Environment Setup

### Java Installation
```bash
# Check Java version
java -version

# Should show Java 17 or higher
# If not installed, download from: https://adoptium.net/
```

### MySQL Installation
```bash
# Check MySQL version
mysql --version

# Should show MySQL 8.0 or higher
# If not installed, download from: https://dev.mysql.com/downloads/mysql/
```

### MySQL Configuration
```sql
-- Create MySQL user (optional)
CREATE USER 'wowdatahub'@'localhost' IDENTIFIED BY 'secure_password';
GRANT ALL PRIVILEGES ON CS5200Project.* TO 'wowdatahub'@'localhost';
FLUSH PRIVILEGES;
```

## 📥 Step 2: Project Setup

### Clone Repository
```bash
git clone https://github.com/yourusername/wow-datahub.git
cd wow-datahub
```

### Directory Structure Verification
Ensure your project has this structure:
```
WoW-DataHub/
├── src/main/java/game/
├── src/main/resources/
├── webapp/WEB-INF/lib/
├── config/
├── docs/
└── scripts/
```

## 📚 Step 3: Dependencies Setup

### Required JAR Files
Download and place in `webapp/WEB-INF/lib/`:

1. **MySQL Connector** (already have):
   - `mysql-connector-j-8.4.0.jar`

2. **JSTL Libraries** (already have):
   - `taglibs-standard-impl-1.2.5.jar`
   - `taglibs-standard-spec-1.2.5.jar`

3. **JSON Processing** (download needed):
   - `jackson-core-2.15.2.jar`
   - `jackson-databind-2.15.2.jar`
   - `jackson-annotations-2.15.2.jar`

4. **HTTP Client** (download needed):
   - `httpclient-4.5.14.jar`
   - `httpcore-4.4.16.jar`
   - `commons-logging-1.2.jar`

5. **Logging** (download needed):
   - `log4j-core-2.20.0.jar`
   - `log4j-api-2.20.0.jar`

### Download Script
```bash
# Create download script (optional)
cd webapp/WEB-INF/lib/

# Jackson JARs
wget https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-core/2.15.2/jackson-core-2.15.2.jar
wget https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-databind/2.15.2/jackson-databind-2.15.2.jar
wget https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-annotations/2.15.2/jackson-annotations-2.15.2.jar

# HTTP Client JARs
wget https://repo1.maven.org/maven2/org/apache/httpcomponents/httpclient/4.5.14/httpclient-4.5.14.jar
wget https://repo1.maven.org/maven2/org/apache/httpcomponents/httpcore/4.4.16/httpcore-4.4.16.jar
wget https://repo1.maven.org/maven2/commons-logging/commons-logging/1.2/commons-logging-1.2.jar

# Logging JARs
wget https://repo1.maven.org/maven2/org/apache/logging/log4j/log4j-core/2.20.0/log4j-core-2.20.0.jar
wget https://repo1.maven.org/maven2/org/apache/logging/log4j/log4j-api/2.20.0/log4j-api-2.20.0.jar
```

## 🗃️ Step 4: Database Setup

### Create Database Schema
```bash
# Navigate to project root
cd /path/to/wow-datahub

# Execute schema creation
mysql -u root -p < src/main/resources/sql/schema.sql
```

### Load Reference Data
```bash
# Load essential reference data
mysql -u root -p < src/main/resources/sql/initial-data.sql
```

### Optional: Load Test Data
```bash
# Load sample data for development
mysql -u root -p < src/main/resources/sql/test-data.sql
```

### Verify Database Setup
```sql
-- Connect to MySQL
mysql -u root -p

-- Use the database
USE CS5200Project;

-- Check tables were created
SHOW TABLES;

-- Should show 15 tables:
-- AvailableJobs, CharacterStatistics, CharacterUnlockedJob, 
-- CharacterWealth, Characters, Clans, Currencies, 
-- EquipmentBonuse, Equipments, Gears, Items, 
-- JobsForGear, Players, Statistics, Weapons

-- Check reference data
SELECT COUNT(*) FROM AvailableJobs;  -- Should show 14
SELECT COUNT(*) FROM Statistics;     -- Should show 12
SELECT COUNT(*) FROM Currencies;     -- Should show 10
```

## ⚙️ Step 5: Configuration

### Update Database Configuration
Edit `src/main/resources/application.properties`:
```properties
# Update these values for your setup
db.url=jdbc:mysql://localhost:3306/CS5200Project
db.username=root
db.password=your_mysql_password
```

### Update Connection Manager
Edit `src/main/java/game/dal/ConnectionManager.java`:
```java
// Update these constants
private static final String USER = "root";
private static final String PASSWORD = "your_mysql_password";  
private static final String SCHEMA = "CS5200Project";
```

## 🔨 Step 6: Build and Compile

### Compile Java Files
```bash
# Create build directory
mkdir -p build

# Compile all Java files
javac -cp "webapp/WEB-INF/lib/*" -d build src/main/java/game/**/*.java

# Check for compilation errors
echo $?  # Should return 0 if successful
```

### Verify Compilation
```bash
# Check build directory structure
ls -la build/game/
# Should show: dal, etl, model, servlet packages

# Check class files exist
find build -name "*.class" | wc -l
# Should show multiple class files
```

## 🚀 Step 7: Testing

### Test Database Connection
```bash
# Test connection
java -cp "webapp/WEB-INF/lib/*:build" game.dal.ConnectionManager
```

### Run ETL Process
```bash
# Run full ETL process
java -cp "webapp/WEB-INF/lib/*:build" game.Driver

# Expected output:
# INFO: Starting WoW DataHub ETL Process
# INFO: Resetting database schema...
# INFO: Creating database tables...
# INFO: All 15 tables created successfully
# INFO: Starting WoW ETL process...
# INFO: Extracted 5 characters from WoW API
# INFO: Successfully processed character: [Character Name]
# INFO: WoW ETL process completed
# INFO: === WoW DATAHUB SUMMARY ===
# INFO: Players: 5
# INFO: Characters: 5
# INFO: Weapons: 5
# INFO: ETL Process completed successfully
```

### Verify Data Loading
```sql
-- Check data was loaded
USE CS5200Project;

SELECT COUNT(*) FROM Players;      -- Should show 5
SELECT COUNT(*) FROM Characters;   -- Should show 5  
SELECT COUNT(*) FROM Weapons;      -- Should show 5
SELECT COUNT(*) FROM Clans;        -- Should show several

-- Check sample data
SELECT c.firstName, cl.clanName, w.itemName 
FROM Characters c 
JOIN Clans cl ON c.clan = cl.clanName
JOIN Weapons w ON c.weaponWeared = w.itemID;
```

## 🌐 Step 8: Web Deployment (Optional)

### Tomcat Setup
```bash
# Create WAR file
jar -cvf wow-datahub.war -C webapp . -C build .

# Deploy to Tomcat
cp wow-datahub.war $TOMCAT_HOME/webapps/

# Start Tomcat
$TOMCAT_HOME/bin/startup.sh
```

### Access Web Interface
1. Open browser to `http://localhost:8080/wow-datahub`
2. You should see the professional dashboard
3. Navigate through different sections

## 🐛 Troubleshooting

### Common Issues

#### Compilation Errors
```bash
# Missing JARs
Error: cannot find symbol
Solution: Verify all JARs are in webapp/WEB-INF/lib/

# Java version issues  
Error: release version not supported
Solution: Use Java 17 or higher
```

#### Database Connection Issues
```bash
# Access denied
Error: Access denied for user 'root'@'localhost'
Solution: Check MySQL username/password in ConnectionManager.java

# Database doesn't exist
Error: Unknown database 'CS5200Project'
Solution: Run schema.sql to create database
```

#### ETL Process Issues
```bash
# Table doesn't exist
Error: Table 'CS5200Project.Items' doesn't exist
Solution: Run schema.sql and initial-data.sql

# Foreign key constraint fails
Error: Cannot add or update a child row
Solution: Ensure reference data is loaded (initial-data.sql)
```

### Verification Checklist

- [ ] Java 17+ installed and JAVA_HOME set
- [ ] MySQL 8.0+ running on port 3306
- [ ] All 11 JAR files in webapp/WEB-INF/lib/
- [ ] Database CS5200Project created with 15 tables
- [ ] Reference data loaded (jobs, statistics, currencies)
- [ ] application.properties configured correctly
- [ ] ConnectionManager.java updated with correct credentials
- [ ] Java files compile without errors
- [ ] ETL process runs successfully
- [ ] Web application deploys to Tomcat (if using)

## 🎯 Next Steps

After successful setup:

1. **Explore the Dashboard**: Navigate to the web interface
2. **Review ETL Logs**: Check logs/wow-datahub.log for process details
3. **Customize Configuration**: Adjust settings in application.properties
4. **Add Real API Integration**: Replace simulator with Battle.net API
5. **Extend Functionality**: Add new features and analytics

## 📞 Getting Help

If you encounter issues:

1. **Check Logs**: Review logs in the logs/ directory
2. **Verify Prerequisites**: Ensure all requirements are met
3. **Database Status**: Verify MySQL is running and accessible
4. **File Permissions**: Check that all files are readable
5. **Java Classpath**: Ensure all JARs are properly included

For additional support, refer to the main README.md or create an issue in the repository.

---

**Setup complete! 🎉 Your WoW DataHub is ready to use.**