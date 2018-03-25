/**
 * 
 * CSC 540
 * 
 * Wolf Inns
 * Hotel Management Database System
 * 
 * Team C
 * Abhay Soni                   (asoni3)
 * Aurora Tiffany-Davis         (attiffan)
 * Manjusha Trilochan Awasthi   (mawasth)
 * Samantha Scoggins            (smscoggi)
 *
 */

// Imports
import java.util.Scanner;
import java.sql.*;
import java.text.NumberFormat;

// WolfInns class
public class WolfInns {
    
    // DECLARATIONS
    
    // Declare constants - commands
    
    private static final String CMD_MAIN =                  "MAIN";
    private static final String CMD_QUIT =                  "QUIT";
    private static final String CMD_BILLING =               "BILLING";
    private static final String CMD_REPORTS =               "REPORTS";
    private static final String CMD_MANAGE =                "MANAGE";
    
    private static final String CMD_BILLING_GENERATE =      "BILLFORSTAY";
    
    private static final String CMD_REPORT_REVENUE =        "REVENUE";
    private static final String CMD_REPORT_HOTELS =         "HOTELS";
    private static final String CMD_REPORT_ROOMS =          "ROOMS";
    private static final String CMD_REPORT_STAFF =          "STAFF";
    private static final String CMD_REPORT_CUSTOMERS =      "CUSTOMERS";
    private static final String CMD_REPORT_STAYS =          "STAYS";
    private static final String CMD_REPORT_SERVICES =       "SERVICES";
    private static final String CMD_REPORT_PROVIDED =       "PROVIDED";
    
    private static final String CMD_MANAGE_HOTEL_ADD =      "ADDHOTEL";
    private static final String CMD_MANAGE_HOTEL_UPDATE =   "UPDATEHOTEL";
    private static final String CMD_MANAGE_HOTEL_DELETE =   "DELETEHOTEL";
    private static final String CMD_MANAGE_STAFF_DELETE =   "DELETESTAFF";
    
    private static final String CMD_MANAGE_ROOM_ADD =       "ADDROOM";
    private static final String CMD_MANAGE_ROOM_UPDATE =    "UPDATEROOM";
    private static final String CMD_MANAGE_ROOM_DELETE =    "DELETEROOM"; 
    
    // Declare constants - connection parameters
    private static final String JDBC_URL = "jdbc:mariadb://classdb2.csc.ncsu.edu:3306/smscoggi";
    private static final String JDBC_USER = "smscoggi";
    private static final String JDBC_PASSWORD = "200157888";
    
    // Declare variables - high level
    private static Connection jdbc_connection;
    private static Statement jdbc_statement;
    private static ResultSet jdbc_result;
    private static String currentMenu;
    
    // Declare variables - prepared statements
    private static PreparedStatement jdbcPrep_insertNewHotel;
    private static PreparedStatement jdbcPrep_updateNewHotelManager;
    private static PreparedStatement jdbcPrep_udpateHotelName;
    private static PreparedStatement jdbcPrep_updateHotelStreetAddress;
    private static PreparedStatement jdbcPrep_updateHotelCity;
    private static PreparedStatement jdbcPrep_udpateHotelState;
    private static PreparedStatement jdbcPrep_updateHotelPhoneNum;
    private static PreparedStatement jdbcPrep_updateHotelManagerID;
    private static PreparedStatement jdbcPrep_demoteOldManager;
    private static PreparedStatement jdbcPrep_promoteNewManager;
    private static PreparedStatement jdbcPrep_getNewestHotelID;
    private static PreparedStatement jdbcPrep_getHotelSummaryForAddress;
    private static PreparedStatement jdbcPrep_getHotelSummaryForPhoneNumber;
    private static PreparedStatement jdbcPrep_getHotelSummaryForStaffMember;
    private static PreparedStatement jdbcPrep_getHotelByID;
    private static PreparedStatement jdbcPrep_deleteHotel;  
    
    // Declare variables - prepared statements - Rooms
    private static PreparedStatement jdbcPrep_insertNewRoom;
    private static PreparedStatement jdbcPrep_isValidRoomNumber;	// while adding a new room
    
    /* Why is the scanner outside of any method?
     * See https://stackoverflow.com/questions/13042008/java-util-nosuchelementexception-scanner-reading-user-input
     */
    private static Scanner scanner;
    
    // SETUP
    
    /** 
     * Print available commands
     * 
     * Arguments -  menu -  The menu we are currently in (determines available commands).
     *                      For example main, reports, etc.
     * Return -     None
     * 
     * Modifications:   03/07/18 -  ATTD -  Created method.
     *                  03/08/18 -  ATTD -  Add ability to print entire Provided table.
     *                  03/09/18 -  ATTD -  Add ability to delete a hotel.
     *                  03/11/18 -  ATTD -  Add ability to report revenue.
     *                  03/11/18 -  ATTD -  Add ability to generate bill for customer stay.
     *                  03/12/18 -  ATTD -  Add ability to delete a staff member.
     *                  03/23/18 -  ATTD -  Add ability to update basic information about a hotel.
     *                                      Use new general error handler. 
     *                  03/24/18 -  MTA  -  Added ability to support Manage task for Room i.e add, update and delete room
     */
    public static void printAvailableCommands(String menu) {
        
        try {
            
            System.out.println("");
            System.out.println(menu + " Menu available commands:");
            System.out.println("");
            
            switch (menu) {
                case CMD_MAIN:
                    System.out.println("'" + CMD_BILLING + "'");
                    System.out.println("\t- bill customers");
                    System.out.println("'" + CMD_REPORTS + "'");
                    System.out.println("\t- run reports");
                    System.out.println("'" + CMD_MANAGE + "'");
                    System.out.println("\t- manage the hotel chain (add hotels, etc)");
                    System.out.println("'" + CMD_QUIT + "'");
                    System.out.println("\t- exit the program");
                    System.out.println("");
                    break;
                case CMD_BILLING:
                    System.out.println("'" + CMD_BILLING_GENERATE + "'");
                    System.out.println("\t- generate a bill and an itemized receipt for a customer stay");
                    System.out.println("'" + CMD_MAIN + "'");
                    System.out.println("\t- go back to the main menu");
                    System.out.println("");
                    break;
                case CMD_REPORTS:
                    System.out.println("'" + CMD_REPORT_REVENUE + "'");
                    System.out.println("\t- run report on a hotel's revenue during a given date range");
                    System.out.println("'" + CMD_REPORT_HOTELS + "'");
                    System.out.println("\t- run report on hotels");
                    System.out.println("'" + CMD_REPORT_ROOMS + "'");
                    System.out.println("\t- run report on rooms");
                    System.out.println("'" + CMD_REPORT_STAFF + "'");
                    System.out.println("\t- run report on staff");
                    System.out.println("'" + CMD_REPORT_CUSTOMERS + "'");
                    System.out.println("\t- run report on customers");
                    System.out.println("'" + CMD_REPORT_STAYS + "'");
                    System.out.println("\t- run report on stays");
                    System.out.println("'" + CMD_REPORT_SERVICES + "'");
                    System.out.println("\t- run report on service types");
                    System.out.println("'" + CMD_REPORT_PROVIDED + "'");
                    System.out.println("\t- run report on services provided to guests");
                    System.out.println("'" + CMD_MAIN + "'");
                    System.out.println("\t- go back to the main menu");
                    System.out.println("");
                    break;
                case CMD_MANAGE:
                    System.out.println("'" + CMD_MANAGE_HOTEL_ADD + "'");
                    System.out.println("\t- add a hotel");
                    System.out.println("'" + CMD_MANAGE_HOTEL_UPDATE + "'");
                    System.out.println("\t- update information about a hotel");
                    System.out.println("'" + CMD_MANAGE_HOTEL_DELETE + "'");
                    System.out.println("\t- delete a hotel");
                    System.out.println("'" + CMD_MANAGE_STAFF_DELETE + "'");
                    System.out.println("\t- delete a staff member");
                    
                    System.out.println("'" + CMD_MANAGE_ROOM_ADD + "'");
                    System.out.println("\t- add a room");
                    System.out.println("'" + CMD_MANAGE_ROOM_UPDATE + "'");
                    System.out.println("\t- update details of the room");
                    System.out.println("'" + CMD_MANAGE_ROOM_DELETE + "'");
                    System.out.println("\t- delete a room");
                    
                    System.out.println("'" + CMD_MAIN + "'");
                    System.out.println("\t- go back to the main menu");
                    System.out.println("");
                    break;
                default:
                    break;
            }
            

        
        }
        catch (Throwable err) {
            handleError(err);
        }
        
    }
    
    /** 
     * Establish a connection to the database
     * 
     * Arguments -  None
     * Return -     None
     * 
     * Modifications:   03/07/18 -  ATTD -  Created method.
     *                  03/23/18 -  ATTD -  Use new general error handler.
     */
    public static void connectToDatabase() {
        
        try {
            
            // Get JDBC driver
            Class.forName("org.mariadb.jdbc.Driver");
            
            // Initialize JDBC stuff to null
            jdbc_connection = null;
            jdbc_statement = null;
            jdbc_result = null;
            
            // Establish connection
            jdbc_connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
            jdbc_statement = jdbc_connection.createStatement();
            
        }
        catch (Throwable err) {
            handleError(err);
        }
        
    }
    
    /** 
     * Create prepared statements
     * 
     * Arguments -  None
     * Return -     None
     * 
     * Modifications:   03/20/18 -  ATTD -  Created method.
     *                  03/21/18 -  ATTD -  Fix off-by-one error in updating new hotel manager.
     *                                      Add more prepared statements to use when inserting new hotel.
     *                  03/23/18 -  ATTD -  Add support for updating basic information about a hotel.
     *                                      Use new general error handler.
     *                  03/24/18 -  ATTD -  Add support for deleting a hotel.
     */
    public static void createPreparedStatements() {
        
        try {
            
            // Declare variables
            String reusedSQLVar;
            
            /* Insert new hotel
             * Indices to use when calling this prepared statement:
             * 1 - name
             * 2 - street address
             * 3 - city
             * 4 - state
             * 5 - phone number
             * 6 - manager ID
             * 7 - manager ID (again)
             */
            reusedSQLVar = 
                "INSERT INTO Hotels (Name, StreetAddress, City, State, PhoneNum, ManagerID) " + 
                "SELECT ?, ?, ?, ?, ?, ? " + 
                "FROM Staff " + 
                "WHERE " + 
                "Staff.ID = ? AND " + 
                "Staff.ID NOT IN (SELECT DCStaff FROM Rooms WHERE DCStaff IS NOT NULL) AND " + 
                "Staff.ID NOT IN (SELECT DRSStaff FROM Rooms WHERE DRSStaff IS NOT NULL);";
            jdbcPrep_insertNewHotel = jdbc_connection.prepareStatement(reusedSQLVar);
            
            /* Update new hotel manager
             * to give new manager correct job title and hotel assignment
             * intended to be called in same transaction as insertion of new hotel
             * therefore the insertion is not yet committed
             * therefore max ID needs incremented
             * Indices to use when calling this prepared statement: n/a
             */
            reusedSQLVar = 
                "UPDATE Staff " + 
                "SET JobTitle = 'Manager', HotelID = (SELECT MAX(ID) FROM Hotels) " + 
                "WHERE " + 
                "ID = (SELECT ManagerID FROM Hotels WHERE ID = (SELECT MAX(ID) FROM Hotels));";
            jdbcPrep_updateNewHotelManager = jdbc_connection.prepareStatement(reusedSQLVar);
            
            /* Update hotel name
             * Indices to use when calling this prepared statement: 
             * 1 -  hotel name
             * 2 -  hotel ID
             */
            reusedSQLVar = 
                "UPDATE Hotels " + 
                "SET Name = ? " + 
                "WHERE ID = ?;";
            jdbcPrep_udpateHotelName = jdbc_connection.prepareStatement(reusedSQLVar);

            /* Update hotel street address
             * Indices to use when calling this prepared statement: 
             * 1 -  hotel street address
             * 2 -  hotel ID
             */
            reusedSQLVar = 
                "UPDATE Hotels " + 
                "SET StreetAddress = ? " + 
                "WHERE ID = ?;";
            jdbcPrep_updateHotelStreetAddress = jdbc_connection.prepareStatement(reusedSQLVar); 

            /* Update hotel city
             * Indices to use when calling this prepared statement: 
             * 1 -  hotel city
             * 2 -  hotel ID
             */
            reusedSQLVar = 
                "UPDATE Hotels " + 
                "SET City = ? " + 
                "WHERE ID = ?;";
            jdbcPrep_updateHotelCity = jdbc_connection.prepareStatement(reusedSQLVar); 

            /* Update hotel state
             * Indices to use when calling this prepared statement: 
             * 1 -  hotel state
             * 2 -  hotel ID
             */
            reusedSQLVar = 
                "UPDATE Hotels " + 
                "SET State = ? " + 
                "WHERE ID = ?;";
            jdbcPrep_udpateHotelState = jdbc_connection.prepareStatement(reusedSQLVar); 
            
            /* Update hotel phone number
             * Indices to use when calling this prepared statement: 
             * 1 -  hotel phone number
             * 2 -  hotel ID
             */
            reusedSQLVar = 
                "UPDATE Hotels " + 
                "SET PhoneNum = ? " + 
                "WHERE ID = ?;";
            jdbcPrep_updateHotelPhoneNum = jdbc_connection.prepareStatement(reusedSQLVar); 
            
            /* Update hotel managerID
             * Indices to use when calling this prepared statement: 
             * 1 -  hotel manager ID
             * 2 -  hotel ID
             */
            reusedSQLVar = 
                "UPDATE Hotels " + 
                "SET ManagerID = ? " + 
                "WHERE ID = ?;";
            jdbcPrep_updateHotelManagerID = jdbc_connection.prepareStatement(reusedSQLVar);
            
            /* Demote the old manager of a given hotel to front desk representative
             * Indices to use when calling this prepared statement: 
             * 1 -  hotel ID
             */
            reusedSQLVar = 
                "UPDATE Staff " + 
                "SET JobTitle = 'Front Desk Representative' " + 
                "WHERE JobTitle = 'Manager' AND HotelID = ?;";
            jdbcPrep_demoteOldManager = jdbc_connection.prepareStatement(reusedSQLVar);
            
            /* Promote a staff member to management of a hotel
             * Indices to use when calling this prepared statement: 
             * 1 -  hotel ID
             * 2 -  staff ID
             */
            reusedSQLVar = 
                "UPDATE Staff " + 
                "SET JobTitle = 'Manager', HotelID = ? " + 
                "WHERE ID = ?;";
            jdbcPrep_promoteNewManager = jdbc_connection.prepareStatement(reusedSQLVar);

            /* Get the ID of the newest hotel in the DB
             * Indices to use when calling this prepared statement: n/a
             */
            reusedSQLVar = "SELECT MAX(ID) FROM Hotels;";
            jdbcPrep_getNewestHotelID = jdbc_connection.prepareStatement(reusedSQLVar);
            
            /* Get a summary of info about the hotel which has a particular street address, city, state
             * Indices to use when calling this prepared statement:
             * 1 -  street address
             * 2 -  city
             * 3 -  state
             */
            reusedSQLVar = 
                "SELECT " + 
                "StreetAddress, City, State, ID AS HotelID, Name AS HotelName " + 
                "FROM Hotels WHERE StreetAddress = ? AND City = ? AND State = ?;";
            jdbcPrep_getHotelSummaryForAddress = jdbc_connection.prepareStatement(reusedSQLVar);
            
            /* Get a summary of info about the hotel which has a particular phone number
             * Indices to use when calling this prepared statement:
             * 1 -  phone number
             */
            reusedSQLVar = 
                "SELECT " + 
                "PhoneNum AS PhoneNumber, ID AS HotelID, Name AS HotelName " + 
                "FROM Hotels WHERE PhoneNum = ?;";
            jdbcPrep_getHotelSummaryForPhoneNumber = jdbc_connection.prepareStatement(reusedSQLVar);
            
            /* Get a summary of info about the hotel to which a staff member is assigned
             * Indices to use when calling this prepared statement:
             * 1 -  staff ID
             */
            reusedSQLVar = 
                "SELECT " + 
                "Staff.ID AS StaffID, Staff.Name AS StaffName, Hotels.ID AS HotelID, Hotels.Name AS HotelName " + 
                "FROM Staff, Hotels WHERE Staff.ID = ?" + 
                " AND Staff.HotelID = Hotels.ID;";
            jdbcPrep_getHotelSummaryForStaffMember = jdbc_connection.prepareStatement(reusedSQLVar);
            
            /* Get all values of a given tuple (by ID) from the Hotels table
             * Indices to use when calling this prepared statement:
             * 1 -  ID
             */
            reusedSQLVar = 
                "SELECT * FROM Hotels WHERE ID = ?;";
            jdbcPrep_getHotelByID = jdbc_connection.prepareStatement(reusedSQLVar);
            
            /* Delete a hotel (by ID)
             * Indices to use when calling this prepared statement:
             * 1 -  ID
             */
            reusedSQLVar = 
                "DELETE FROM Hotels WHERE ID = ? AND ID NOT IN " + 
                "(SELECT HotelID FROM Stays WHERE CheckOutTime IS NULL OR EndDate IS NULL);";
            jdbcPrep_deleteHotel = jdbc_connection.prepareStatement(reusedSQLVar);
            
        }
        catch (Throwable err) {
            handleError(err);
        }
        
    }
        
    // TABLE CREATION
    
    /** 
     * Drop database tables, if they exist
     * (to support running program many times)
     * 
     * Arguments -  None
     * Return -     None
     * 
     * Modifications:   03/07/18 -  ATTD -  Created method.
     *                  03/23/18 -  ATTD -  Use new general error handler.
     */
    public static void dropExistingTables() {

        try {
            
            // Declare variables
            DatabaseMetaData metaData;
            String tableName;

            /* Find out what tables already exist
             * https://docs.oracle.com/javase/8/docs/api/java/sql/DatabaseMetaData.html
             */
            metaData = jdbc_connection.getMetaData();
            jdbc_result = metaData.getTables(null, null, "%", null);
            
            // Go through and delete each existing table
            while (jdbc_result.next()) {
                // Get table name
                tableName = jdbc_result.getString(3);
                /* Drop disable foreign key checks to avoid complaint
                 * https://stackoverflow.com/questions/4120482/foreign-key-problem-in-jdbc
                 */
                jdbc_statement.executeUpdate("SET FOREIGN_KEY_CHECKS=0");
                // Drop table
                jdbc_statement.executeUpdate("DROP TABLE " + tableName);
                // Re-establish normal foreign key checks
                jdbc_statement.executeUpdate("SET FOREIGN_KEY_CHECKS=1");
            }
            
        }
        catch (Throwable err) {
            handleError(err);
        }
        
    }
    
    /** 
     * Create database tables
     * 
     * Note:    CHECK    
     *              Per https://dev.mysql.com/doc/refman/5.7/en/create-table.html,
     *              "The CHECK clause is parsed but ignored by all storage engines",
     *          ASSERTION
     *              Per https://stackoverflow.com/questions/34769321/unexplainable-mysql-error-when-trying-to-create-assertion
     *              "This list does not include CREATE ASSERTION, so MariaDB does not support this functionality"
     *          So unfortunately there are some data entry error checks that we must perform
     *          in the application rather than letting the DBMS do it for us
     * 
     * Arguments -  None
     * Return -     None
     * 
     * Modifications:   03/07/18 -  ATTD -  Created method.
     *                  03/08/18 -  ATTD -  Changed state to CHAR(2).
     *                  03/09/18 -  ATTD -  Added on delete rules for foreign keys.
     *                  03/11/18 -  ATTD -  Added amount owed to Stays relation.
     *                  03/12/18 -  ATTD -  Changed Provided table to set staff ID to NULL when staff member is deleted.
     *                  03/12/18 -  ATTD -  Corrected JDBC transaction code (add try-catch).
     *                  03/14/18 -  ATTD -  Changed service type names to enum, to match project assumptions.
     *                  03/17/18 -  ATTD -  Billing address IS allowed be NULL (when payment method is not card) per team discussion.
     *                  03/23/18 -  ATTD -  Use new general error handler.
     *                  03/24/18 -  MTA -   Name the primary key constraints.
     */
    public static void createTables() {
        
        try {

            // Drop all tables that already exist, so that we may run repeatedly
            dropExistingTables();
            
            // Start transaction
            jdbc_connection.setAutoCommit(false);
            
            try {
            
                /* Create table: Customers
                 * phone number to be entered as 10 digit int ex: 9993335555
                 * requires "BIGINT" instead of just "INT"
                 * SSN to be entered as 9 digit int ex: 100101000
                 * requires "BIGINT" instead of just "INT"
                 */
                jdbc_statement.executeUpdate("CREATE TABLE Customers ("+
                    "SSN BIGINT NOT NULL,"+
                    "Name VARCHAR(255) NOT NULL,"+
                    "DOB DATE NOT NULL,"+
                    "PhoneNum BIGINT NOT NULL,"+
                    "Email VARCHAR(255) NOT NULL,"+
                    "CONSTRAINT PK_CUSTOMERS PRIMARY KEY (SSN)"+
                ")");
    
                // Create table: ServiceTypes
                jdbc_statement.executeUpdate("CREATE TABLE ServiceTypes ("+
                    "Name ENUM('Phone','Dry Cleaning','Gym','Room Service','Catering','Special Request') NOT NULL,"+
                    "Cost INT NOT NULL,"+
                    "CONSTRAINT PK_SERVICE_TYPES PRIMARY KEY (Name)"+
                ")");
    
                /* Create table: Staff
                 * phone number to be entered as 10 digit int ex: 9993335555
                 * requires "BIGINT" instead of just "INT"
                 */
                jdbc_statement.executeUpdate("CREATE TABLE Staff ("+
                    "ID INT NOT NULL AUTO_INCREMENT,"+
                    "Name VARCHAR(255) NOT NULL,"+
                    "DOB DATE NOT NULL,"+
                    "JobTitle VARCHAR(255),"+
                    "Dep VARCHAR(255) NOT NULL,"+
                    "PhoneNum BIGINT NOT NULL,"+
                    "Address VARCHAR(255) NOT NULL,"+
                    "HotelID INT,"+
                    "CONSTRAINT PK_STAFF PRIMARY KEY(ID)"+
                ")");
    
                /* Create table: Hotels
                 * this is done after Staff table is created
                 * because manager ID references Staff table
                 * phone number to be entered as 10 digit int ex: 9993335555
                 * requires "BIGINT" instead of just "INT"
                 */
                jdbc_statement.executeUpdate("CREATE TABLE Hotels ("+
                    "ID INT NOT NULL AUTO_INCREMENT,"+
                    "Name VARCHAR(255) NOT NULL,"+
                    "StreetAddress VARCHAR(255) NOT NULL,"+
                    "City VARCHAR(255) NOT NULL,"+
                    "State CHAR(2) NOT NULL,"+
                    "PhoneNum BIGINT Not Null,"+
                    "ManagerID INT Not Null,"+
                    "CONSTRAINT PK_HOTELS Primary Key(ID),"+
                    "CONSTRAINT UC_HACS UNIQUE (StreetAddress, City, State),"+
                    "CONSTRAINT UC_HPN UNIQUE (PhoneNum),"+
                    "CONSTRAINT UC_HMID UNIQUE (ManagerID),"+
                    /* If a manager is deleted from the system and not replaced in the same transaction, no choice but to delete hotel
                     * A hotel cannot be without a manager
                     */
                    "CONSTRAINT FK_HMID FOREIGN KEY (ManagerID) REFERENCES Staff(ID) ON DELETE CASCADE"+
                ")");
    
                /* Alter table: Staff
                 * needs to happen after Hotels table is created
                 * because hotel ID references Hotels table
                 */
                jdbc_statement.executeUpdate("ALTER TABLE Staff "+
                    "ADD CONSTRAINT FK_STAFFHID "+
                     /* If a hotel is deleted, no need to delete the staff that work there,
                      * NULL is allowed (currently unassigned staff)
                      */
                    "FOREIGN KEY (HotelID) REFERENCES Hotels(ID) ON DELETE SET NULL"
                ); 
    
                // Create table: Rooms
                jdbc_statement.executeUpdate("CREATE TABLE Rooms ("+
                    "RoomNum INT NOT NULL,"+
                    "HotelID INT NOT NULL,"+
                    "Category VARCHAR(255) NOT NULL,"+
                    "MaxOcc INT NOT NULL,"+
                    "NightlyRate DOUBLE NOT NULL,"+
                    "DRSStaff INT,"+
                    "DCStaff INT,"+
                    "CONSTRAINT PK_ROOMS PRIMARY KEY(RoomNum,HotelID),"+
                    // If a hotel is deleted, then the rooms within it should also be deleted
                    "CONSTRAINT FK_ROOMHID FOREIGN KEY (HotelID) REFERENCES Hotels(ID) ON DELETE CASCADE,"+
                    /* If a staff member dedicated to a room is deleted by the end of a transaction
                     * then something has probably gone wrong, because that staff member should have been replaced
                     * to maintain continuous service
                     * Nonetheless, not appropriate to delete the room in this case
                     * NULL is allowed
                    */
                    "CONSTRAINT FK_ROOMDRSID FOREIGN KEY (DRSStaff) REFERENCES Staff(ID) ON DELETE SET NULL,"+
                    "CONSTRAINT FK_ROOMDCID FOREIGN KEY (DCStaff) REFERENCES Staff(ID) ON DELETE SET NULL"+
                ")");
    
                // Create table: Stays
                jdbc_statement.executeUpdate("CREATE TABLE Stays ("+
                    "ID INT NOT NULL AUTO_INCREMENT,"+
                    "StartDate DATE NOT NULL,"+
                    "CheckInTime TIME NOT NULL,"+
                    "RoomNum INT NOT NULL,"+
                    "HotelID INT NOT NULL,"+
                    "CustomerSSN BIGINT NOT NULL,"+
                    "NumGuests INT NOT NULL,"+
                    "CheckOutTime TIME,"+
                    "EndDate DATE,"+
                    "AmountOwed DOUBLE,"+
                    "PaymentMethod ENUM('CASH','CARD') NOT NULL,"+
                    "CardType ENUM('VISA','MASTERCARD','HOTEL'),"+
                    "CardNumber BIGINT,"+
                    "BillingAddress VARCHAR(255),"+
                    "CONSTRAINT PK_STAYS PRIMARY KEY(ID),"+
                    "CONSTRAINT UC_STAYKEY UNIQUE (StartDate, CheckInTime,RoomNum, HotelID),"+
                    /* If a room is deleted, then the stay no longer makes sense and should be deleted
                     * Need to handle room/hotel together as a single foreign key
                     * Because a foreign key is supposed to point to a unique tuple
                     * And room number by itself is not unique
                    */
                    "CONSTRAINT FK_STAYRID FOREIGN KEY (RoomNum, HotelID) REFERENCES Rooms(RoomNum, HotelID) ON DELETE CASCADE,"+
                    // If a customer is deleted, then the stay no longer makes sense and should be deleted
                    "CONSTRAINT FK_STAYCSSN FOREIGN KEY (CustomerSSN) REFERENCES Customers(SSN) ON DELETE CASCADE"+
                ")");
    
                // Create table: Provided
                jdbc_statement.executeUpdate("CREATE TABLE Provided ("+
                    "ID INT NOT NULL AUTO_INCREMENT,"+
                    "StayID INT NOT NULL,"+
                    "StaffID INT,"+
                    "ServiceName ENUM('Phone','Dry Cleaning','Gym','Room Service','Catering','Special Request') NOT NULL,"+
                    "CONSTRAINT PK_PROVIDED PRIMARY KEY(ID),"+
                    // If a stay is deleted, then the service provided record no longer makes sense and should be deleted
                    "CONSTRAINT FK_PROVSTAYID FOREIGN KEY (StayID) REFERENCES Stays(ID) ON DELETE CASCADE,"+
                    // If a staff member is deleted, then the service provided record still makes sense but has staff ID as NULL
                    "CONSTRAINT FK_PROVSTAFFID FOREIGN KEY (StaffID) REFERENCES Staff(ID) ON DELETE SET NULL,"+
                    // If a service type is deleted, then the service provided record no longer makes sense and should be deleted
                    "CONSTRAINT FK_PROVSERV FOREIGN KEY (ServiceName) REFERENCES ServiceTypes(Name) ON DELETE CASCADE"+
                ")");
                
                // If success, commit
                jdbc_connection.commit();
                
                System.out.println("Tables created successfully!");
            
            }
            catch (Throwable err) {
                
                // Handle error
                handleError(err);
                // Roll back the entire transaction
                jdbc_connection.rollback();
                
            }
            finally {
                // Restore normal auto-commit mode
                jdbc_connection.setAutoCommit(true);
            }
            
        }
        catch (Throwable err) {
            handleError(err);
        }
        
    }
    
    // TABLE POPULATION 
    
    /** 
     * Populate Customers Table
     * 
     * Arguments -  None
     * Return -     None
     * 
     * Modifications:   03/07/18 -  ATTD -  Created method.
     *                  03/07/18 -  MTA -   Populated method.
     *                  03/08/18 -  ATTD -  Shifted some string constants purely for readability (no functional changes).
     *                  03/12/18 -  ATTD -  Corrected JDBC transaction code (add try-catch).
     *                  03/23/18 -  ATTD -  Use new general error handler.
     */
    public static void populateCustomersTable() {
        
        try {
            
            // Start transaction
            jdbc_connection.setAutoCommit(false);
            
            try {
            
                // Populating data for Customers
                // TODO: use prepared statement instead
                jdbc_statement.executeUpdate("INSERT INTO Customers"+
    				"(SSN, Name, DOB, PhoneNum, Email) VALUES "+
    				"(555284568, 'Isaac Gray', '1982-11-12', '9194562158', 'issac.gray@gmail.com');");
                jdbc_statement.executeUpdate("INSERT INTO Customers"+ 
    				"(SSN, Name, DOB, PhoneNum, Email) VALUES "+ 
    				"(111038548, 'Jay Sharp', '1956-07-09', '9191237548', 'jay.sharp@gmail.com');"); 
                jdbc_statement.executeUpdate("INSERT INTO Customers "+ 
    				"(SSN, Name, DOB, PhoneNum, Email) VALUES "+ 
    				"(222075875, 'Jenson Lee', '1968-09-25', '9194563217', 'jenson.lee@gmail.com');");
                jdbc_statement.executeUpdate("INSERT INTO Customers "+ 
    				"(SSN, Name, DOB, PhoneNum, Email) VALUES "+ 
    				" (333127845, 'Benjamin Cooke', '1964-01-07', '9191256324', 'benjamin.cooke@gmail.com');");
                jdbc_statement.executeUpdate("INSERT INTO Customers "+ 
    				"(SSN, Name, DOB, PhoneNum, Email) VALUES "+ 
    				" (444167216, 'Joe Bradley', '1954-04-07', '9194587569', 'joe.bradley@gmail.com');");
                jdbc_statement.executeUpdate("INSERT INTO Customers "+ 
    				"(SSN, Name, DOB, PhoneNum, Email) VALUES "+ 
    				" (666034568, 'Conor Stone', '1975-06-04', '9194567216', 'conor.stone@gmail.com');");
                jdbc_statement.executeUpdate("INSERT INTO Customers "+ 
    				"(SSN, Name, DOB, PhoneNum, Email) VALUES "+ 
    				" (777021654, 'Elizabeth Davis', '1964-07-26', '9195432187', 'elizabeth.davis@gmail.com');");
                jdbc_statement.executeUpdate("INSERT INTO Customers "+ 
    				"(SSN, Name, DOB, PhoneNum, Email) VALUES "+ 
    				" (888091545, 'Natasha Moore', '1966-08-14', '9194562347', 'natasha.moore@gmail.com');");
                jdbc_statement.executeUpdate("INSERT INTO Customers "+
                    "(SSN, Name, DOB, PhoneNum, Email) VALUES "+
                    "(888092545, 'Gary Vee', '1996-03-10', '9199237455', 'gary.vee@gmail.com');");
                jdbc_statement.executeUpdate("INSERT INTO Customers "+
                    "(SSN, Name, DOB, PhoneNum, Email) VALUES "+
                    "(888090545, 'Gary Vee', '1996-03-10', '9199237455', 'gary.vee@gmail.com');");
                
                // If success, commit
                jdbc_connection.commit();
                
                System.out.println("Customers table loaded!");
    		
            }
            catch (Throwable err) {
                
                // Handle error
                handleError(err);
                // Roll back the entire transaction
                jdbc_connection.rollback();
                
            }
            finally {
                // Restore normal auto-commit mode
                jdbc_connection.setAutoCommit(true);
            }
            
        }
        catch (Throwable err) {
            handleError(err);
        }
        
    }
    
    /** 
     * Populate ServiceTypes Table
     * 
     * Arguments -  None
     * Return -     None
     * 
     * Modifications:   03/07/18 -  ATTD -  Created method.
     *                  03/07/18 -  MTA -   Populated method.
     *                  03/08/18 -  ATTD -  Shifted some string constants purely for readability (no functional changes).
     *                  03/12/18 -  ATTD -  Corrected JDBC transaction code (add try-catch).
     *                  03/14/18 -  ATTD -  Changed service type names to match job titles, to make queries easier.
     *                  03/23/18 -  ATTD -  Use new general error handler.
     */
    public static void populateServiceTypesTable() {
        
        try {
            
            // Start transaction
            jdbc_connection.setAutoCommit(false);
            
            try {
            
                // Populating data for ServiceTypes
                // TODO: use prepared statement instead
                jdbc_statement.executeUpdate("INSERT INTO ServiceTypes "+ 
    				"(Name, Cost) VALUES "+
    				"('Phone', 25);");
    			jdbc_statement.executeUpdate("INSERT INTO ServiceTypes "+ 
    				"(Name, Cost) VALUES "+
    				"('Dry Cleaning', 20);");
    			jdbc_statement.executeUpdate("INSERT INTO ServiceTypes "+ 
    				"(Name, Cost) VALUES "+
    				"('Gym', 35);");
    			jdbc_statement.executeUpdate("INSERT INTO ServiceTypes "+ 
    				"(Name, Cost) VALUES "+
    				"('Room Service', 25);");
    			jdbc_statement.executeUpdate("INSERT INTO ServiceTypes "+ 
    				"(Name, Cost) VALUES "+
    				"('Catering', 50);");
    			jdbc_statement.executeUpdate("INSERT INTO ServiceTypes "+ 
    				"(Name, Cost) VALUES "+
    				"('Special Request', 40);");
    			
                // If success, commit
                jdbc_connection.commit();
    			
    			System.out.println("ServiceTypes table loaded!");

            }
            catch (Throwable err) {
                
                // Handle error
                handleError(err);
                // Roll back the entire transaction
                jdbc_connection.rollback();
                
            }
            finally {
                // Restore normal auto-commit mode
                jdbc_connection.setAutoCommit(true);
            }
            
        }
        catch (Throwable err) {
            handleError(err);
        }
        
    }
    
    /** 
     * Populate Staff Table
     * 
     * Arguments -  None
     * Return -     None
     * 
     * Modifications:   03/07/18 -  ATTD -  Created method.
     *                  03/07/18 -  MTA -   Populated method.
     *                  03/08/18 -  ATTD -  Shifted some string constants purely for readability (no functional changes).
     *                  03/09/18 -  ATTD -  Removed explicit setting of ID (this is auto incremented).
     *                  03/10/18 -  ATTD -  Removed explicit setting of hotel ID to null.
     *                  03/12/18 -  ATTD -  Corrected JDBC transaction code (add try-catch).
     *                  03/16/18 -  ATTD -  Changing departments to emphasize their meaninglessness.
     *                  03/23/18 -  ATTD -  Use new general error handler.
     */
    public static void populateStaffTable() {
        
        try {
            
            // Start transaction
            jdbc_connection.setAutoCommit(false);
            
            try {
            
                // Populating data for Staff
                
        		// Staff for Hotel#1
                // TODO: use prepared statement instead
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Zoe Holmes', '1980-10-02', 'Manager', 'A', 8141113134, '123 6th St. Melbourne, FL 32904');");
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Katelyn Weeks', '1970-04-20', 'Front Desk Representative', 'B', 6926641058, '123 6th St. Melbourne, FL 32904');");
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Abby Huffman', '1990-12-14', 'Room Service', 'C', 6738742135, '71 Pilgrim Avenue Chevy Chase, MD 20815');");
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Oliver Gibson', '1985-05-12', 'Room Service', 'A', 1515218329, '70 Bowman St. South Windsor, CT 06074');");
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Michael Day', '1983-02-25', 'Catering', 'B', 3294931245, '4 Goldfield Rd. Honolulu, HI 96815');");
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('David Adams', '1985-01-17', 'Dry Cleaning', 'C', 9194153214, '44 Shirley Ave. West Chicago, IL 60185');");
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Ishaan Goodman', '1993-04-19', 'Gym', 'A', 5203201425, '514 S. Magnolia St. Orlando, FL 32806');");
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Nicholas Read', '1981-01-14', 'Catering', 'B', 2564132017, '236 Pumpkin Hill Court Leesburg, VA 20175');");
        		
        		// Staff for Hotel#2
        		// TODO: use prepared statement instead
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Dominic Mitchell', '1971-03-13', 'Manager', 'A', 2922497845, '7005 South Franklin St. Somerset, NJ 08873');");
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Oliver Lucas', '1961-05-11', 'Front Desk Representative', 'A', 2519881245, '7 Edgefield St. Augusta, GA 30906');");
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Molly Thomas', '1987-07-10', 'Room Service', 'B', 5425871245, '541 S. Holly Street Norcross, GA 30092');");
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    		        "('Caitlin Cole', '1989-08-15', 'Catering', 'B', 4997845612, '7 Ivy Ave. Traverse City, MI 49684');");
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Victoria Medina', '1989-02-04', 'Dry Cleaning', 'C', 1341702154, '8221 Trenton St. Jamestown, NY 14701');");
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Will Rollins', '1982-07-06', 'Gym', 'C', 7071264587, '346 Beacon Lane Quakertown, PA 18951');");
        		
        		// Staff for Hotel#3
        		// TODO: use prepared statement instead
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Masen Shepard', '1983-01-09', 'Manager', 'A', 8995412364, '3 Fulton Ave. Bountiful, UT 84010');");
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Willow Roberts', '1987-02-08', 'Front Desk Representative', 'A', 5535531245, '7868 N. Lees Creek Street Chandler, AZ 85224');");
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Maddison Davies', '1981-03-07', 'Room Service', 'A', 6784561245, '61 New Road Ithaca, NY 14850');");
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Crystal Barr', '1989-04-06', 'Catering', 'B', 4591247845, '9094 6th Ave. Macomb, MI 48042');");
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Dayana Tyson', '1980-05-05', 'Dry Cleaning', 'B', 4072134587, '837 W. 10th St. Jonesboro, GA 30236');");
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Tommy Perry', '1979-06-04', 'Gym', 'B', 5774812456, '785 Bohemia Street Jupiter, FL 33458');");
        		
        		// Staff for Hotel#4
        		// TODO: use prepared statement instead
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Joshua Burke', '1972-01-10', 'Manager', 'C', 1245214521, '8947 Briarwood St. Baldwin, NY 11510');");
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Bobby Matthews', '1982-02-14', 'Front Desk Representative', 'C', 5771812456, '25 W. Dogwood Lane Bemidji, MN 56601');");
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Pedro Cohen', '1983-04-24', 'Room Service', 'C', 8774812456, '9708 Brickyard Ave. Elyria, OH 44035');");
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Alessandro Beck', '1981-06-12', 'Catering', 'A', 5774812452, '682 Glen Ridge St. Leesburg, VA 20175');");
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Emily Petty', '1984-08-19', 'Dry Cleaning', 'A', 5772812456, '7604 Courtland St. Easley, SC 29640');");
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Rudy Cole', '1972-01-09', 'Gym', 'A', 5774812856, '37 Marconi Drive Owensboro, KY 42301');");
        		
        		// Staff for Hotel#5
        		// TODO: use prepared statement instead
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Blair Ball', '1981-01-10', 'Manager', 'A', 8854124568, '551 New Saddle Ave. Cape Coral, FL 33904');");
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Billy Lopez', '1982-05-11', 'Front Desk Representative', 'B', 5124562123, '99 Miles Road Danbury, CT 06810');");
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Lee Ward', '1983-06-12', 'Room Service', 'B', 9209124562, '959 S. Tailwater St. Ridgewood, NJ 07450');");
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Ryan Parker', '1972-08-13', 'Catering', 'B', 1183024152, '157 State Dr. Attleboro, MA 02703');");
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Glen Elliott', '1971-09-14', 'Catering', 'B', 6502134785, '9775 Clinton Dr. Thornton, CO 80241');");
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Ash Harrison', '1977-02-15', 'Dry Cleaning', 'C', 9192451365, '9924 Jefferson Ave. Plainfield, NJ 07060');");
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Leslie Little', '1979-12-16', 'Gym', 'C', 9192014512, '7371 Pin Oak St. Dalton, GA 30721');");
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Mason West', '1970-10-17', 'Gym', 'C', 6501231245, '798 W. Valley Farms Lane Saint Petersburg, FL 33702');");
        		
        		//Staff for Hotel#6
        		// TODO: use prepared statement instead
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Riley Dawson', '1975-01-09', 'Manager', 'C', 1183021245, '898 Ocean Court Hilliard, OH 43026');");
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Gabe Howard', '1987-03-01', 'Front Desk Representative', 'A', 6501421523, '914 Edgefield Dr. Hartselle, AL 35640');");
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Jessie Nielsen', '1982-06-02', 'Room Service', 'A', 7574124587, '7973 Edgewood Road Gallatin, TN 37066');");
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Gabe Carlson', '1983-08-03', 'Room Service', 'A', 5771245865, '339 Pine Lane Tampa, FL 33604');");
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Carmen Lee', '1976-01-04', 'Catering', 'A', 9885234562, '120 Longbranch Drive Port Richey, FL 34668');");
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    		        "(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Mell Tran', '1979-06-05', 'Dry Cleaning', 'A', 9162451245, '32 Pearl St. Peoria, IL 61604');");
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Leslie Cook', '1970-10-08', 'Gym', 'B', 6501245126, '59 W. High Ridge Street Iowa City, IA 52240');");
        		
        		//Staff for Hotel#7
        		// TODO: use prepared statement instead
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Rory Burke', '1971-01-05', 'Manager', 'B', 7702653764, '9273 Ridge Drive Winter Springs, FL 32708');");
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Macy Fuller', '1972-02-07', 'Front Desk Representative', 'B', 7485612345, '676 Myers Street Baldwin, NY 11510');");
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Megan Lloyd', '1973-03-01', 'Room Service', 'B', 7221452315, '849 George Lane Park Ridge, IL 60068');");
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Grace Francis', '1974-04-09', 'Catering', 'B', 3425612345, '282 Old York Court Mechanicsburg, PA 17050');");
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Macy Fuller', '1975-05-02', 'Dry Cleaning', 'C', 4665127845, '57 Shadow Brook St. Hudson, NH 03051');");
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Cory Hoover', '1976-06-12', 'Gym', 'C', 9252210735, '892 Roosevelt Street Ithaca, NY 14850');");
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Sam Graham', '1977-07-25', 'Gym', 'C', 7226251245, '262 Bayberry St. Dorchester, MA 02125');");
        		
        		//Staff for Hotel#8
        		// TODO: use prepared statement instead
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Charlie Adams', '1981-01-01', 'Manager', 'C', 6084254152, '9716 Glen Creek Dr. Newark, NJ 07103');");
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Kiran West', '1985-02-02', 'Front Desk Representative', 'C', 9623154125, '68 Smith Dr. Lexington, NC 27292');");
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Franky John', '1986-03-03', 'Room Service', 'A', 8748544152, '6 Shirley Road Fairborn, OH 45324');");
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Charlie Bell', '1985-04-04', 'Room Service', 'A', 9845124562, '66 Elm Street Jupiter, FL 33458');");
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Jamie Young', '1986-06-05', 'Catering', 'A', 9892145214, '8111 Birch Hill Avenue Ravenna, OH 44266');");
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Jackie Miller', '1978-08-06', 'Dry Cleaning', 'A', 9795486234, '9895 Redwood Court Glenview, IL 60025');");
        		jdbc_statement.executeUpdate("INSERT INTO Staff "+
    				"(Name, DOB, JobTitle, Dep, PhoneNum, Address) VALUES "+
    				"('Jude Cole', '1979-03-07', 'Gym', 'A', 9195642251, '8512 Cambridge Ave. Lake In The Hills, IL 60156');");
        		
                // If success, commit
                jdbc_connection.commit();
             
        		System.out.println("Staff table loaded!");
    		
            }
            catch (Throwable err) {
                
                // Handle error
                handleError(err);
                // Roll back the entire transaction
                jdbc_connection.rollback();
                
            }
            finally {
                // Restore normal auto-commit mode
                jdbc_connection.setAutoCommit(true);
            }
            
        }
        catch (Throwable err) {
            handleError(err);
        }
        
    }
    
    /** 
     * Populate Hotels Table
     * 
     * Arguments -  None
     * Return -     None
     * 
     * Modifications:   03/07/18 -  ATTD -  Created method.
     *                  03/07/18 -  MTA -   Populated method.
     *                  03/08/18 -  ATTD -  Shifted some string constants purely for readability (no functional changes).
     *                  03/09/18 -  ATTD -  Calling method to insert hotels (which also update's new manager's staff info).
     *                  03/11/18 -  ATTD -  Removed 9th hotel.
     *                  03/12/18 -  ATTD -  Corrected JDBC transaction code (add try-catch).
     *                  03/23/18 -  ATTD -  Use new general error handler.
     */
    public static void populateHotelsTable() {
        
        try {
            
            // Start transaction
            jdbc_connection.setAutoCommit(false);
            
            try {
            
                // Populating data for Hotels
                updateInsertHotel("The Plaza", "768 5th Ave", "New York", "NY", 9194152368L, 1, false);
                updateInsertHotel("DoubleTree", "4810 Page Creek Ln", "Raleigh", "NC", 9192012364L, 9, false);
        		updateInsertHotel("Ramada", "1520 Blue Ridge Rd", "Raleigh", "NC", 9190174632L, 15, false);
        		updateInsertHotel("Embassy Suites", "201 Harrison Oaks Blvd", "Raleigh", "NC", 6502137942L, 21, false);
        		updateInsertHotel("Four Seasons", "57 E 57th St", "New York", "NY", 6501236874L, 27, false);
        		updateInsertHotel("The Pierre", "2 E 61st St", "New York", "NY", 6501836874L, 35, false);
        		updateInsertHotel("Fairfield Inn & Suites", "0040 Sellona St", "Raleigh", "NC", 6501236074L, 42, false);
        		updateInsertHotel("Mandarin Oriental", "80 Columbus Cir", "New York", "NY", 6591236874L, 49, false);
        		
                // If success, commit
                jdbc_connection.commit();
                
        		System.out.println("Hotels table loaded!");
            
            }
            catch (Throwable err) {
                
                // Handle error
                handleError(err);
                // Roll back the entire transaction
                jdbc_connection.rollback();
                
            }
            finally {
                // Restore normal auto-commit mode
                jdbc_connection.setAutoCommit(true);
            }
            
        }
        catch (Throwable err) {
            handleError(err);
        }
        
    }
    
    /** 
     * Update Staff Table
     * 
     * Arguments -  None
     * Return -     None
     * 
     * Modifications:   03/07/18 -  MTA -   Created method.
     *                  03/12/18 -  ATTD -  Corrected JDBC transaction code (add try-catch).
     *                  03/23/18 -  ATTD -  Use new general error handler.
     */
    public static void updateHotelIdForStaff() {
    	
    	 try {
             
             // Start transaction
             jdbc_connection.setAutoCommit(false);
             
             try {
             
                 // Update(Assign) HotelId for Staff 
                 // TODO: use prepared statement instead
                 jdbc_statement.executeUpdate("UPDATE Staff SET HotelID = 1 WHERE ID >=1 AND ID <=8;");
                 jdbc_statement.executeUpdate("UPDATE Staff SET HotelID = 2 WHERE ID >=9 AND ID <=14;");
                 jdbc_statement.executeUpdate("UPDATE Staff SET HotelID = 3 WHERE ID >=15 AND ID <=20;");
                 jdbc_statement.executeUpdate("UPDATE Staff SET HotelID = 4 WHERE ID >=21 AND ID <=26;");
                 jdbc_statement.executeUpdate("UPDATE Staff SET HotelID = 5 WHERE ID >=27 AND ID <=34;");
                 jdbc_statement.executeUpdate("UPDATE Staff SET HotelID = 6 WHERE ID >=35 AND ID <=41;");
                 jdbc_statement.executeUpdate("UPDATE Staff SET HotelID = 7 WHERE ID >=42 AND ID <=48;");
                 jdbc_statement.executeUpdate("UPDATE Staff SET HotelID = 8 WHERE ID >=49 AND ID <=55;");
                 
                 // If success, commit
                 jdbc_connection.commit();
    			
                 System.out.println("Hotel Id's updated for Staff!");
             
             }
             catch (Throwable err) {
                 
                 // Handle error
                 handleError(err);
                 // Roll back the entire transaction
                 jdbc_connection.rollback();
                 
             }
             finally {
                 // Restore normal auto-commit mode
                 jdbc_connection.setAutoCommit(true);
             }
             
         }
         catch (Throwable err) {
             handleError(err);
         }
    }
        
    /** 
     * Populate Rooms Table
     * 
     * Arguments -  None
     * Return -     None
     * 
     * Modifications:   03/07/18 -  ATTD -  Created method.
     *                  03/07/18 -  MTA -   Populated method.
     *                  03/08/18 -  ATTD -  Shifted some string constants purely for readability (no functional changes).
     *                  03/11/18 -  ATTD -  Changed dedicated presidential suite staff for hotel 9 to avoid staff conflicts.
     *                  03/11/18 -  ATTD -  Removed hotel 9.
     *                  03/12/18 -  ATTD -  Do not set DRSStaff and DCStaff to NULL explicitly (no need to).
     *                                      Do not set DRSStaff and DCStaff to non-NULL, for rooms not currently occupied.
     *                  03/12/18 -  ATTD -  Corrected JDBC transaction code (add try-catch).
     *                  03/23/18 -  ATTD -  Use new general error handler.
     */
    public static void populateRoomsTable() {
        
        try {
            
            // Start transaction
            jdbc_connection.setAutoCommit(false);
            
            try {
            
                // Populating data for Rooms
                
                // Hotel # 1
                // TODO: use prepared statement instead
        		jdbc_statement.executeUpdate("INSERT INTO Rooms "+
    				" (RoomNum, HotelID, Category, MaxOcc, NightlyRate) VALUES " +
    				" (1, 1, 'ECONOMY', 3, 150);");
        		jdbc_statement.executeUpdate("INSERT INTO Rooms "+
    				" (RoomNum, HotelID, Category, MaxOcc, NightlyRate) VALUES " +
    				" (2, 1, 'PRESIDENTIAL_SUITE', 4, 450);");
        		jdbc_statement.executeUpdate("INSERT INTO Rooms "+
    				" (RoomNum, HotelID, Category, MaxOcc, NightlyRate) VALUES " +
    				" (3, 1, 'EXECUTIVE_SUITE', 4, 300);");
        		// Hotel # 2
        		// TODO: use prepared statement instead
        		jdbc_statement.executeUpdate("INSERT INTO Rooms "+
    				" (RoomNum, HotelID, Category, MaxOcc, NightlyRate) VALUES " +
    				" (1, 2, 'DELUXE', 3, 200);");
        		jdbc_statement.executeUpdate("INSERT INTO Rooms "+
    				" (RoomNum, HotelID, Category, MaxOcc, NightlyRate) VALUES " +
    				" (2, 2, 'ECONOMY', 3, 125);");
        		jdbc_statement.executeUpdate("INSERT INTO Rooms "+
    				" (RoomNum, HotelID, Category, MaxOcc, NightlyRate) VALUES " +
    				" (3, 2, 'EXECUTIVE_SUITE', 4, 250);");
        		// Hotel # 3
        		// TODO: use prepared statement instead
        		jdbc_statement.executeUpdate("INSERT INTO Rooms "+
    				" (RoomNum, HotelID, Category, MaxOcc, NightlyRate) VALUES " +
    				" (1, 3, 'PRESIDENTIAL_SUITE', 3, 550);");
        		jdbc_statement.executeUpdate("INSERT INTO Rooms "+
    				" (RoomNum, HotelID, Category, MaxOcc, NightlyRate) VALUES " +
    				" (2, 3, 'ECONOMY', 2, 350);");
        		jdbc_statement.executeUpdate("INSERT INTO Rooms "+
    				" (RoomNum, HotelID, Category, MaxOcc, NightlyRate) VALUES " +
    				" (3, 3, 'DELUXE', 3, 450);");
        		// Hotel # 4
        		// TODO: use prepared statement instead
        		jdbc_statement.executeUpdate("INSERT INTO Rooms "+
    				" (RoomNum, HotelID, Category, MaxOcc, NightlyRate) VALUES " +
    				" (1, 4, 'ECONOMY', 4, 100);");
        		jdbc_statement.executeUpdate("INSERT INTO Rooms "+
    				" (RoomNum, HotelID, Category, MaxOcc, NightlyRate) VALUES " +
    				" (2, 4, 'EXECUTIVE_SUITE', 4, 250);");
        		// Hotel # 5
        		// TODO: use prepared statement instead
        		jdbc_statement.executeUpdate("INSERT INTO Rooms "+
    				" (RoomNum, HotelID, Category, MaxOcc, NightlyRate) VALUES " +
    				" (1, 5, 'DELUXE', 3, 300);");
        		jdbc_statement.executeUpdate("INSERT INTO Rooms "+
    				" (RoomNum, HotelID, Category, MaxOcc, NightlyRate) VALUES " +
    				" (2, 5, 'EXECUTIVE_SUITE', 4, 400);");
        		jdbc_statement.executeUpdate("INSERT INTO Rooms "+
    				" (RoomNum, HotelID, Category, MaxOcc, NightlyRate) VALUES " +
    				" (3, 5, 'PRESIDENTIAL_SUITE', 4, 500);");
        		// Hotel # 6
        		// TODO: use prepared statement instead
        		jdbc_statement.executeUpdate("INSERT INTO Rooms "+
    				" (RoomNum, HotelID, Category, MaxOcc, NightlyRate) VALUES " +
    				" (1, 6, 'ECONOMY', 2, 220);");
        		jdbc_statement.executeUpdate("INSERT INTO Rooms "+
    		        " (RoomNum, HotelID, Category, MaxOcc, NightlyRate) VALUES " +
    				" (2, 6, 'DELUXE', 4, 350);");
        		// Hotel # 7
        		// TODO: use prepared statement instead
        		jdbc_statement.executeUpdate("INSERT INTO Rooms "+
    				" (RoomNum, HotelID, Category, MaxOcc, NightlyRate) VALUES " +
    				" (1, 7, 'ECONOMY', 2, 125);");
        		jdbc_statement.executeUpdate("INSERT INTO Rooms "+
    				" (RoomNum, HotelID, Category, MaxOcc, NightlyRate) VALUES " +
    				" (2, 7, 'EXECUTIVE_SUITE', 4, 400);");
        		// Hotel # 8
        		// TODO: use prepared statement instead
        		jdbc_statement.executeUpdate("INSERT INTO Rooms "+
    				" (RoomNum, HotelID, Category, MaxOcc, NightlyRate) VALUES " +
    				" (1, 8, 'ECONOMY', 2, 200);");
        		jdbc_statement.executeUpdate("INSERT INTO Rooms "+
    				" (RoomNum, HotelID, Category, MaxOcc, NightlyRate) VALUES " +
    				" (2, 8, 'DELUXE', 3, 250);");
        		jdbc_statement.executeUpdate("INSERT INTO Rooms "+
    				" (RoomNum, HotelID, Category, MaxOcc, NightlyRate) VALUES " +
    				" (3, 8, 'EXECUTIVE_SUITE', 3, 300);");
        		jdbc_statement.executeUpdate("INSERT INTO Rooms "+
    				" (RoomNum, HotelID, Category, MaxOcc, NightlyRate, DRSStaff, DCStaff) VALUES " +
                    " (4, 8, 'PRESIDENTIAL_SUITE', 4, 450, 51, 53);");
        		
                // If success, commit
                jdbc_connection.commit();
    
                System.out.println("Rooms Table loaded!");
            
            }
            catch (Throwable err) {
                
                // Handle error
                handleError(err);
                // Roll back the entire transaction
                jdbc_connection.rollback();
                
            }
            finally {
                // Restore normal auto-commit mode
                jdbc_connection.setAutoCommit(true);
            }
            
        }
        catch (Throwable err) {
            handleError(err);
        }
        
    }
    
    /** 
     * Populate Stays Table
     * 
     * Arguments -  None
     * Return -     None
     * 
     * Modifications:   03/07/18 -  ATTD -  Created method.
     *                  03/07/18 -  MTA -   Populated method.
     *                  03/08/18 -  ATTD -  Shifted some string constants purely for readability (no functional changes).
     *                  03/09/18 -  ATTD -  Removed explicit setting of ID (this is auto incremented).
     *                  03/11/18 -  ATTD -  Added amount owed to Stays relation.
     *                  03/11/18 -  ATTD -  Removed hotel 9.
     *                                      It was added to demonstrate that we can have NULL values for check out time, end date.
     *                                      This demonstration is instead added into some of the existing stays for hotels 1-8.
     *                                      The reason for this change is just to keep the amount of data to a reasonably low level 
     *                                      to help us think through queries and updates more quickly.
     *                  03/11/18 -  ATTD -  Do not set amount owed here (risk of calculating wrong when we calculate by hand).
     *                                      Instead, set by running billing report on the stay.
     *                  03/12/18 -  ATTD -  Corrected JDBC transaction code (add try-catch).
     *                  03/17/18 -  ATTD -  Billing address IS allowed be NULL (when payment method is not card) per team discussion.
     *                  03/23/18 -  ATTD -  Use new general error handler.
     */
    public static void populateStaysTable() {
        
        try {
            
            // Start transaction
            jdbc_connection.setAutoCommit(false);
            
            try {
            
                // Stays where the guest has already checked out'
                // TODO: use prepared statement instead
        		jdbc_statement.executeUpdate("INSERT INTO Stays "+
    				" (StartDate, CheckInTime, RoomNum, HotelID, CustomerSSN, NumGuests, CheckOutTime, EndDate, PaymentMethod, CardType, CardNumber, BillingAddress) VALUES "+ 
    				" ('2018-01-12', '20:10:00', 1, 1, 555284568, 3, '10:00:00', '2018-01-20', 'CARD', 'VISA', '4400123454126587', '7178 Kent St. Enterprise, AL 36330');");
        		jdbc_statement.executeUpdate("INSERT INTO Stays "+
    				" (StartDate, CheckInTime, RoomNum, HotelID, CustomerSSN, NumGuests, CheckOutTime, EndDate, PaymentMethod) VALUES "+ 
    				" ('2018-02-15', '10:20:00', 3, 2, 111038548, 2, '08:00:00', '2018-02-18', 'CASH');");
        		jdbc_statement.executeUpdate("INSERT INTO Stays "+
    				" (StartDate, CheckInTime, RoomNum, HotelID, CustomerSSN, NumGuests, CheckOutTime, EndDate, PaymentMethod, CardType, CardNumber, BillingAddress) VALUES "+ 
    				" ('2018-03-01', '15:00:00', 1, 3, 222075875, 1, '13:00:00', '2018-03-05', 'CARD', 'HOTEL', '1100214521684512', '178 Shadow Brook St. West Chicago, IL 60185');");
        		jdbc_statement.executeUpdate("INSERT INTO Stays "+
    				" (StartDate, CheckInTime, RoomNum, HotelID, CustomerSSN, NumGuests, CheckOutTime, EndDate, PaymentMethod, CardType, CardNumber, BillingAddress) VALUES "+ 
    				" ('2018-02-20', '07:00:00', 2, 4, 333127845, 4, '15:00:00', '2018-02-27', 'CARD', 'MASTERCARD', '4400124565874591', '802B Studebaker Drive Clinton Township, MI 48035');");
        		jdbc_statement.executeUpdate("INSERT INTO Stays "+
    				" (StartDate, CheckInTime, RoomNum, HotelID, CustomerSSN, NumGuests, CheckOutTime, EndDate, PaymentMethod, CardType, CardNumber, BillingAddress) VALUES "+ 
    				" ('2018-03-05', '11:00:00', 3, 5, 444167216, 4, '08:00:00', '2018-03-12', 'CARD', 'VISA', '4400127465892145', '83 Inverness Court Longwood, FL 32779');");
        		jdbc_statement.executeUpdate("INSERT INTO Stays "+
    				" (StartDate, CheckInTime, RoomNum, HotelID, CustomerSSN, NumGuests, CheckOutTime, EndDate, PaymentMethod) VALUES "+ 
    				" ('2018-03-01', '18:00:00', 1, 6, 666034568, 1, '23:00:00', '2018-03-01', 'CASH');");
        		// Stays that are still going on
        		// TODO: use prepared statement instead
        		jdbc_statement.executeUpdate("INSERT INTO Stays "+
    				" (StartDate, CheckInTime, RoomNum, HotelID, CustomerSSN, NumGuests, PaymentMethod, CardType, CardNumber, BillingAddress) VALUES "+ 
    				" ('2018-01-20', '06:00:00', 2, 7, 777021654, 3, 'CARD', 'HOTEL', '1100214532567845', '87 Gregory Street Lawndale, CA 90260');");
        		jdbc_statement.executeUpdate("INSERT INTO Stays "+
    				" (StartDate, CheckInTime, RoomNum, HotelID, CustomerSSN, NumGuests, PaymentMethod, CardType, CardNumber, BillingAddress) VALUES "+ 
    				" ('2018-02-14', '09:00:00', 4, 8, 888091545, 2, 'CARD', 'VISA', '4400178498564512', '34 Hall Ave. Cranberry Twp, PA 16066');");
        		
                // If success, commit
                jdbc_connection.commit();
        		
                System.out.println("Stays table loaded!");
            
            }
            catch (Throwable err) {
                
                // Handle Error
                handleError(err);
                // Roll back the entire transaction
                jdbc_connection.rollback();
                
            }
            finally {
                // Restore normal auto-commit mode
                jdbc_connection.setAutoCommit(true);
            }
            
        }
        catch (Throwable err) {
            handleError(err);
        }
        
    }
    
    /** 
     * Populate Provided Table
     * 
     * Arguments -  None
     * Return -     None
     * 
     * Modifications:   03/07/18 -  ATTD -  Created method.
     *                  03/07/18 -  MTA -   Populated method.
     *                  03/08/18 -  ATTD -  Shifted some string constants purely for readability (no functional changes).
     *                  03/09/18 -  ATTD -  Removed explicit setting of ID (this is auto incremented).
     *                  03/11/18 -  ATTD -  Added another gym stay to stay ID 1, to more fully exercise ability to produce itemized receipt
     *                                      (itemized receipt needs to sum costs for all instances of the same service type).
     *                  03/12/18 -  ATTD -  Corrected JDBC transaction code (add try-catch).
     *                  03/14/18 -  ATTD -  Changed service type names to match job titles, to make queries easier.
     *                  03/23/18 -  ATTD -  Use new general error handler.
     */
    public static void populateProvidedTable() {
        
        try {
            
            // Start transaction
            jdbc_connection.setAutoCommit(false);
            
            try {
            
                // Populating data for Provided
                // TODO: use prepared statement instead
        		jdbc_statement.executeUpdate("INSERT INTO Provided " + 
    				" (StayID, StaffID, ServiceName) VALUES " +
    				" (1, 7, 'Gym')");
                jdbc_statement.executeUpdate("INSERT INTO Provided " + 
                    " (StayID, StaffID, ServiceName) VALUES " +
                    " (1, 7, 'Gym')");
        		jdbc_statement.executeUpdate("INSERT INTO Provided " + 
    				" (StayID, StaffID, ServiceName) VALUES " +
    				" (1, 5, 'Catering')");
        		jdbc_statement.executeUpdate("INSERT INTO Provided " + 
    				" (StayID, StaffID, ServiceName) VALUES " +
    				" (2, 11, 'Room Service')");
        		jdbc_statement.executeUpdate("INSERT INTO Provided " + 
    				" (StayID, StaffID, ServiceName) VALUES " +
    				" (3, 19, 'Dry Cleaning')");
        		jdbc_statement.executeUpdate("INSERT INTO Provided " + 
    				" (StayID, StaffID, ServiceName) VALUES " +
    				" (4, 26, 'Gym')");
        		jdbc_statement.executeUpdate("INSERT INTO Provided " + 
    				" (StayID, StaffID, ServiceName) VALUES " +
    				" (5, 32, 'Dry Cleaning')");
        		jdbc_statement.executeUpdate("INSERT INTO Provided " + 
    				" (StayID, StaffID, ServiceName) VALUES " +
    				" (6, 38, 'Room Service')");
        		jdbc_statement.executeUpdate("INSERT INTO Provided " + 
    				" (StayID, StaffID, ServiceName) VALUES " +
    				" (7, 48, 'Gym')");
        		jdbc_statement.executeUpdate("INSERT INTO Provided " + 
    				" (StayID, StaffID, ServiceName) VALUES " +
    				" (8, 54, 'Dry Cleaning')");
        		
                // If success, commit
                jdbc_connection.commit();
                
        		System.out.println("Provided table loaded!");
    		
            }
            catch (Throwable err) {
                
                // Handle error
                handleError(err);
                // Roll back the entire transaction
                jdbc_connection.rollback();
                
            }
            finally {
                // Restore normal auto-commit mode
                jdbc_connection.setAutoCommit(true);
            }
            
        }
        catch (Throwable err) {
            handleError(err);
        }
        
    }
    
    /** 
     * Update Stays Table with amount owed for each stay that has actually ended
     * 
     * Arguments -  None
     * Return -     None
     * 
     * Modifications:   03/11/18 -  ATTD -  Created method.
     *                  03/23/18 -  ATTD -  Use new general error handler.
     */
    public static void updateAmountOwedForStays() {
        
        try {
            
            // Declare variables (this method calls another which uses our global jdbc result, so we need to disambiguate)
            ResultSet local_jdbc_result;
            int stayID;

            // Find the stays that have actually ended (no transaction needed for a query)
            // TODO: use prepared statement instead
            local_jdbc_result = jdbc_statement.executeQuery("SELECT ID FROM Stays WHERE EndDate IS NOT NULL");
            
            // Go through and update amount owed for all stays that have actually ended
            while (local_jdbc_result.next()) {
                stayID = local_jdbc_result.getInt(1);
                queryItemizedReceipt(stayID, false);
            }
            
        }
        catch (Throwable err) {
            handleError(err);
        }
        
    }
    
    // BILLING
    
    /** 
     * Billing task: Generate bill and itemized receipt for a customer's stay
     * 
     * Arguments -  None
     * Return -     None
     * 
     * Modifications:   03/11/18 -  ATTD -  Created method.
     *                  03/23/18 -  ATTD -  Use new general error handler.
     */
    public static void generateBillAndReceipt() {
        
        try {

            // Declare variables
            int stayID;
            
            // Get stay ID
            System.out.print("\nEnter the stay ID\n> ");
            stayID = Integer.parseInt(scanner.nextLine());
            
            // Call method to actually interact with the DB
            queryItemizedReceipt(stayID, true);
            
        }
        catch (Throwable err) {
            handleError(err);
        }
        
    }
    
    // REPORTS
    
    /** 
     * Report task: Report revenue earned by a hotel over a date range
     * 
     * Arguments -  None
     * Return -     None
     * 
     * Modifications:   03/11/18 -  ATTD -  Created method.
     *                  03/23/18 -  ATTD -  Use new attribute / value sanity checking method.
     *                                      Use new general error handler.
     */
    public static void reportHotelRevenue() {

        try {
            
            // Declare local variables
            int hotelID;
            String hotelIdAsString = "";
            String startDate = "";
            String endDate = "";
            
            // Get name
            System.out.print("\nEnter the hotel ID\n> ");
            hotelIdAsString = scanner.nextLine();
            if (isValueSane("ID", hotelIdAsString)) {
                hotelID = Integer.parseInt(hotelIdAsString);
                // Get start date
                System.out.print("\nEnter the start date\n> ");
                startDate = scanner.nextLine();
                if (isValueSane("StartDate", startDate)) {
                    // Get end date
                    System.out.print("\nEnter the end date\n> ");
                    endDate = scanner.nextLine();
                    if (isValueSane("EndDate", endDate)) {
                        // Call method to actually interact with the DB
                        queryHotelRevenue(hotelID, startDate, endDate);
                    }
                }
            }
            
        }
        catch (Throwable err) {
            handleError(err);
        }
        
    }

    
    /** 
     * Report task: Report all results from a given table
     * 
     * Arguments -  tableName - The table to print out
     * Return -     None
     * 
     * Modifications:   03/07/18 -  ATTD -  Created method.
     *                  03/23/18 -  ATTD -  Use new general error handler.
     */
    public static void reportEntireTable(String tableName) {

        try {
            
            // Report entire table (no transaction needed for a query)
            System.out.println("\nEntries in the " + tableName + " table:\n");
            // TODO: use prepared statement instead
            jdbc_result = jdbc_statement.executeQuery("SELECT * FROM " + tableName);
            printQueryResultSet(jdbc_result);
            
        }
        catch (Throwable err) {
            handleError(err);
        }
        
    }
    
    /** 
     * Report task: Report all values of a single tuple of the Hotels table, by ID
     * 
     * Arguments -  id -        The ID of the tuple.
     * Return -     None
     * 
     * Modifications:   03/23/18 -  ATTD -  Created method.
     */
    public static void reportHotelByID(int id) {

        try {
            
            // Get entire tuple from table
            jdbcPrep_getHotelByID.setInt(1, id);
            jdbc_result = jdbcPrep_getHotelByID.executeQuery();
            
            // Print result
            System.out.println("\nHotel Information:\n");
            printQueryResultSet(jdbc_result);
            
        }
        catch (Throwable err) {
            handleError(err);
        }
        
    }
    
    // PRINT
    
    /** 
     * Print query result set
     * Modified from, but inspired by: https://coderwall.com/p/609ppa/printing-the-result-of-resultset
     * 
     * Arguments -  resultSetToPrint -  The result set to print
     * Return -     None
     * 
     * Modifications:   03/07/18 -  ATTD -  Created method.
     *                  03/08/18 -  ATTD -  Made printout slightly prettier.
     *                  03/08/18 -  ATTD -  At the end, print number of records in result set.
     *                  03/21/18 -  ATTD -  Print column label instead of column name.
     *                  03/23/18 -  ATTD -  Use new general error handler.
     */
    public static void printQueryResultSet(ResultSet resultSetToPrint) {
        
        try {
            
            // Declare variables
            ResultSetMetaData metaData;
            String columnName;
            String tupleValue;
            int numColumns;
            int i;
            int numTuples;

            // Is there anything useful in the result set?
            if (jdbc_result.next()) {
                
                // Get metadata
                metaData = jdbc_result.getMetaData();
                numColumns = metaData.getColumnCount();
                
                /* Print column headers
                 * use column label instead of column name,
                 * otherwise you will not see the effect of aliasing
                 */
                for (i = 1; i <= numColumns; i++) {
                    columnName = metaData.getColumnLabel(i);
                    System.out.print(padRight(columnName, getNumPadChars(metaData,i)));
                }
                System.out.println("");
                System.out.println("");
                
                // Go through the result set tuple by tuple
                numTuples = 0;
                do {
                    for (i = 1; i <= numColumns; i++) {
                        tupleValue = jdbc_result.getString(i);
                        System.out.print(padRight(tupleValue, getNumPadChars(metaData,i)));
                    }
                    System.out.print("\n");
                    numTuples++;
                } while(jdbc_result.next());
                
                // Print number of records found
                System.out.println("");
                System.out.println("(" + numTuples + " entries)");
                System.out.println("");
                
            } else {
                // Tell the user that the result set is empty
                System.out.println("(no results)\n");
            }
            
        }
        catch (Throwable err) {
            handleError(err);
        }
        
    }
    
    /** 
     * Figure out how many characters to include in a padded string for a given column of a given result,
     * based on the result set metadata
     * 
     * Arguments -  metaData -          Meta data for the result set
     *              colNum -            The column number
     * Return -     numPadChars -       The number of characters to include in a padded string
     * 
     * Modifications:   03/08/18 -  ATTD -  Created method.
     *                  03/09/18 -  ATTD -  Another minor tweak.
     *                  03/23/18 -  ATTD -  Use new general error handler.
     *                  03/24/18 -  ATTD -  Tweaked (didn't perfect).
     */
    public static int getNumPadChars(ResultSetMetaData metaData, int colNum) {
        
        // Declare constants
        // TODO: find some smarter way to print query result set, this approach still has the Hotels table printing out funny, for example
        final int NUM_PAD_CHARS_ID =            5;
        final int NUM_PAD_CHARS_DEP =           5;
        final int NUM_PAD_CHARS_STATE =         6;
        final int NUM_PAD_CHARS_NUMBER =        12;
        final int NUM_PAD_CHARS_DATE_TIME =     11;
        final int NUM_PAD_CHARS_DEFAULT =       30;
        final int NUM_PAD_CHARS_FULL_ADDRESS =  55;
        final int NUM_PAD_CHARS_NAME =          20;
        
        // Declare variables
        int numPadChars = NUM_PAD_CHARS_DEFAULT;
        String columnType;
        String columnName;
        
        try {

            columnType = metaData.getColumnTypeName(colNum);
            columnName = metaData.getColumnName(colNum);
            if (columnName.equals("ID")) {
                numPadChars = NUM_PAD_CHARS_ID;
            }
            else if (columnName.equals("Dep")) {
                numPadChars = NUM_PAD_CHARS_DEP;
            }
            else if (columnName.equals("Address")) {
                numPadChars = NUM_PAD_CHARS_FULL_ADDRESS;
            }
            else if (columnName.equals("State")) {
                numPadChars = NUM_PAD_CHARS_STATE;
            }
            else if (columnName.equals("Name")) {
                numPadChars = NUM_PAD_CHARS_NAME;
            }
            else if (columnType == "INTEGER" || columnType == "BIGINT" || columnType == "DOUBLE") {
                numPadChars = NUM_PAD_CHARS_NUMBER;
            }
            else if (columnType == "DATE" || columnType == "TIME") {
                numPadChars = NUM_PAD_CHARS_DATE_TIME;
            }
            
        }
        catch (Throwable err) {
            handleError(err);
        }
        
        return numPadChars;
         
    }
    
    /** 
     * Pad a string with space characters to reach a given number of total characters
     * https://stackoverflow.com/questions/388461/how-can-i-pad-a-string-in-java/391978#391978
     * 
     * Arguments -  stringIn -          The result set to print
     *              numDesiredChars -   The desired number of characters in the padded result
     * Return -     stringOut -         The padded string
     * 
     * Modifications:   03/08/18 -  ATTD -  Created method.
     *                  03/23/18 -  ATTD -  Use new general error handler.
     */
    public static String padRight(String stringIn, int numDesiredChars) {
        
        // Declare variables
        String stringOut = stringIn;
        
        try {
            // Pad string
            stringOut = String.format("%1$-" + numDesiredChars + "s", stringIn); 
        }
        catch (Throwable err) {
            handleError(err);
        }
        
        return stringOut;
         
    }

    // MANAGE
    
    /** 
     * Management task: Add a new hotel
     * 
     * Arguments -  None
     * Return -     None
     * 
     * Modifications:   03/08/18 -  ATTD -  Created method.
     *                  03/23/18 -  ATTD -  Use new attribute / value sanity checking method.
     *                                      Use new general error handler.
     */
    public static void manageHotelAdd() {

        try {
            
            // Declare local variables
            String hotelName = "";
            String streetAddress = "";
            String city = "";
            String state = "";
            String phoneNumAsString = "";
            String managerIdAsString = "";
            long phoneNum = 0;
            int managerID = 0;
            
            // Get name
            System.out.print("\nEnter the hotel name\n> ");
            hotelName = scanner.nextLine();
            if (isValueSane("Name", hotelName)) {
                // Get street address
                System.out.print("\nEnter the hotel's street address\n> ");
                streetAddress = scanner.nextLine();
                if (isValueSane("StreetAddress", streetAddress)) {
                    // Get city
                    System.out.print("\nEnter the hotel's city\n> ");
                    city = scanner.nextLine();
                    if (isValueSane("City", city)) {
                        // Get state
                        System.out.print("\nEnter the hotel's state\n> ");
                        state = scanner.nextLine();
                        if (isValueSane("State", state)) {
                            // Get phone number
                            System.out.print("\nEnter the hotel's phone number\n> ");
                            phoneNumAsString = scanner.nextLine();
                            if (isValueSane("PhoneNum", phoneNumAsString)) {
                                phoneNum = Long.parseLong(phoneNumAsString);
                                // Get manager
                                System.out.print("\nEnter the hotel's manager's staff ID\n> ");
                                managerIdAsString = scanner.nextLine();
                                if (isValueSane("ManagerID", managerIdAsString)) {
                                    managerID = Integer.parseInt(managerIdAsString);
                                    // Okay, at this point everything else I can think of can be caught by a Java exception or a SQL exception
                                    updateInsertHotel(hotelName, streetAddress, city, state, phoneNum, managerID, true);
                                }
                            }
                        }
                    }
                }
            }
            
        }
        catch (Throwable err) {
            handleError(err);
        }
        
    }
    
    /** 
     * Management task: Change information about a hotel
     * 
     * Arguments -  None
     * Return -     None
     * 
     * Modifications:   03/23/18 -  ATTD -  Created method.
     */
    public static void manageHotelUpdate() {

        try {

            // Declare local variables
            String hotelIdAsString = "";
            String attributeToChange = "";
            String valueToChangeTo;
            int hotelID = 0;
            boolean userWantsToStop = false;
            
            // Print hotels to console so user has some context
            reportEntireTable("Hotels");
            
            // Get hotel ID
            System.out.print("\nEnter the hotel ID for the hotel you wish to make changes for\n> ");
            hotelIdAsString = scanner.nextLine();
            if (isValueSane("ID", hotelIdAsString)) {
                hotelID = Integer.parseInt(hotelIdAsString);
                // Print just that hotel to console so user has some context
                reportHotelByID(hotelID);
                // Keep updating values until the user wants to stop
                while (userWantsToStop == false) {
                    // Get name of attribute they want to change
                    System.out.print("\nEnter the name of the attribute you wish to change (or press <Enter> to stop)\n> ");
                    attributeToChange = scanner.nextLine();
                    if (isValueSane("AnyAttr", attributeToChange)) {
                        // Get value they want to change the attribute to
                        System.out.print("\nEnter the value you wish to change this attribute to (or press <Enter> to stop)\n> ");
                        valueToChangeTo = scanner.nextLine();
                        if (isValueSane("AnyAttr", valueToChangeTo)) {
                            // Okay, at this point everything else I can think of can be caught by a Java exception or a SQL exception
                            updateChangeHotelInfo(hotelID, attributeToChange, valueToChangeTo);
                        }
                        else {
                            userWantsToStop = true;
                        }
                    }
                    else {
                        userWantsToStop = true;
                    }
                }
                // Report results of all the updates
                reportHotelByID(hotelID);
            }
            
        }
        catch (Throwable err) {
            handleError(err);
        }
        
    }
    
    /** 
     * Management task: Delete a hotel
     * 
     * Arguments -  None
     * Return -     None
     * 
     * Modifications:   03/09/18 -  ATTD -  Created method.
     *                  03/23/18 -  ATTD -  Use new general error handler.
     *                  03/24/18 -  ATTD -  Provide more context for user.
     *                                      Change hotel ID from long to int.
     */
    public static void manageHotelDelete() {

        try {
            
            // Declare local variables
            int hotelID = 0;
            
            // Print hotels to console so user has some context
            reportEntireTable("Hotels");
            
            // Get ID of hotel to delete
            System.out.print("\nEnter the hotel ID for the hotel you wish to delete\n> ");
            hotelID = Integer.parseInt(scanner.nextLine());

            // Call method to actually interact with the DB
            updateDeleteHotel(hotelID, true);
            
        }
        catch (Throwable err) {
            handleError(err);
        }
        
    }
    
    /**
     * Task: Manage Rooms
     * 
     * Operation: Add a room
     * 
     * Arguments -  None
     * Returns -    None
     * 
     * Modifications: 03/24/18 - MTA - Added functionality to add new room
     */
    public static void manageRoomAdd() {
    	
    	try { 
    		  
    		String hotelId = getValidDataFromUser("RoomHotelId", "\nEnter the hotel id for which you are adding new room\n> ");
    		
    		String roomNumber = getValidDataFromUser("RoomNumber", "\nEnter the room number\n> ", hotelId); 
    		
    		String category = getValidDataFromUser("RoomCategory", "\nEnter the room's category.\nAvailable options are 'Economy', 'Deluxe', 'Executive Suite', 'Presidential Suite' \n>");
               
    		String maxOccupancy = getValidDataFromUser("RoomMaxOccupancy", "\nEnter the room's maximum occupancy\n> "); 
    		
    		String nightlyRate = getValidDataFromUser("RoomNightlyRate", "\nEnter the room's nightly rate\n> "); 
              
            addRoom(Integer.parseInt(roomNumber), Integer.parseInt(hotelId), category, Integer.parseInt(maxOccupancy), Integer.parseInt(nightlyRate));
           
        }
        catch (Throwable err) {
            handleError(err);
        }
    }
    
    /**
     * Task: Helper method to get data from user
     * 
     * Arguments: fieldName - Name of the field (used while checking if entered data is sane)
     *            message - The message asking user to enter the data 
     *            params(Optional) - Extra parameters needed to validate the sanity 
     * 
     * Returns: Valid user entered data 
     * 
     * Modifications: 03/24/18 - MTA - Added method
     */
    public static String getValidDataFromUser (String fieldName, String message, String...params ) {
    	
    	boolean isValid = true;
    	
    	// Ask user to enter the data
    	System.out.println(message);  
    	String value = scanner.nextLine(); 
    	
    	// Check if valid
    	if (fieldName.equalsIgnoreCase("RoomNumber")) {
    		// Extra checks for Room Number
    		isValid = isValueSane(fieldName, value) && isRoomNumberAvailable(Integer.parseInt(params[0]), Integer.parseInt(value));
    	} else {
    		isValid = isValueSane(fieldName, value);
    	} 
        
    	// If invalid, let user re-enter the value
        while(!isValid) {
        	System.out.println("Re-" + message);
        	value = scanner.nextLine(); 
        	if (fieldName.equalsIgnoreCase("RoomNumber")) {
        		// Extra checks for Room Number
        		isValid = isValueSane(fieldName, value) && isRoomNumberAvailable(Integer.parseInt(params[0]), Integer.parseInt(value));
        	} else {
        		isValid = isValueSane(fieldName, value);
        	}
        } 
        
        // Now the data is valid, return it
        return value;
    }
    
    /**
     * Task: Manage
     * Operation: Update room details
     * 
     * Modifications: 03/24/18 - MTA - Added functionality to update room details
     */
    public static void manageRoomUpdate() {
    	
    }
    
    /**
     * Task: Manage
     * Operation: Delete a room
     * 
     * Modifications: 03/24/18 - MTA - Added functionality to delete room
     */
    public static void manageRoomDelete() {
    	
    }
    
    /** 
     * Management task: Delete a staff member
     * 
     * Arguments -  None
     * Return -     None
     * 
     * Modifications:   03/12/18 -  ATTD -  Created method.
     *                  03/23/18 -  ATTD -  Use new general error handler.
     */
    public static void manageStaffDelete() {

        try {
            
            // Declare local variables
            long staffID = 0;
            
            // Get name
            System.out.print("\nEnter the staff ID\n> ");
            staffID = Long.parseLong(scanner.nextLine());

            // Call method to actually interact with the DB
            updateDeleteStaff(staffID, true);
            
        }
        catch (Throwable err) {
            handleError(err);
        }
        
    }
    
    // SANITY CHECK VALUES
    
    /** 
     * Given a value, and an indication of its intended meaning, determine if it's "sane"
     * This method exists in part because the version of MariaDB suggested for use in this project
     * supports neither CHECK nor ASSERTION
     * 
     * Arguments -  attributeName - The name of the attribute ("PhoneNum", "Address", etc)
     *              proposedValue - The proposed value for the attribute (as a string)
     * Return -     okaySoFar -     True if the value seems sane at first glance
     * 
     * Modifications:   03/23/18 -  ATTD -  Created method.
     */
    public static boolean isValueSane(String attributeName, String proposedValue) {
        
        // Declare local variables
        boolean okaySoFar = true;
        
        try {
            
            if (okaySoFar && attributeName.length() == 0) {
                System.out.println("Attribute not entered (cannot proceed)\n");
                okaySoFar = false;
            }
            if (okaySoFar && proposedValue.length() == 0) {
                System.out.println("Value not entered (cannot proceed)\n");
                okaySoFar = false;
            }
            /* Check for malformed state
             * DBMS seems to accept a malformed date with no complaints
             * even though we define as CHAR(2)
             */
            if (okaySoFar && attributeName.equals("State") && proposedValue.length() != 2) {
                System.out.println("State '" + proposedValue + "' malformed, should have 2 letters (cannot proceed)\n");
                okaySoFar = false;
            }
            // Check for malformed phone number
            if (okaySoFar && attributeName.equals("PhoneNum") && proposedValue.length() != 10) {
                System.out.println("Phone number '" + proposedValue + "' malformed, should have 10 digits (cannot proceed)\n");
                okaySoFar = false;
            }
            /* Check for malformed date
             * DBMS seems to accept a malformed date with no complaints
             * https://stackoverflow.com/questions/2149680/regex-date-format-validation-on-java
             */
            if (okaySoFar && (attributeName.contains("Date") || attributeName.equals("DOB") ) && proposedValue.matches("\\d{4}-\\d{2}-\\d{2}") == false) {
                System.out.println("Date must be entered in the format 'YYYY-MM-DD' (cannot proceed)\n");
                okaySoFar = false;
            }
            /* Check for "bad" manager
             * Don't know of a way to have DBMS check that manager isn't dedicated to a presidential suite (ASSERTION not supported)
             */
            if (okaySoFar && attributeName.equals("ManagerID")) {
                // TODO: use prepared statement instead
                jdbc_result = jdbc_statement.executeQuery(
                        "SELECT Staff.ID, Staff.Name, Rooms.RoomNum, Rooms.hotelID " + 
                        "FROM Staff, Rooms " + 
                        "WHERE Staff.ID = " + 
                        Integer.parseInt(proposedValue) + 
                        " AND (Rooms.DRSStaff = " + Integer.parseInt(proposedValue) + " OR Rooms.DCStaff = " + Integer.parseInt(proposedValue) + ")");
                
                if (jdbc_result.next()) {
                    System.out.println("\nThis manager cannot be used, because they are already dedicated to serving a presidential suite\n");
                    jdbc_result.beforeFirst();
                    printQueryResultSet(jdbc_result);
                    okaySoFar = false;
                }
            }
            
            /* ******************************** VALIDATIONS FOR ADDING NEW ROOM ************************************* */
           
            // Check if entered hotel id for room is valid ( i.e non-negative number) 
            try{
            	 if (attributeName.equalsIgnoreCase("RoomHotelId") && Integer.parseInt(proposedValue) <= 0) {
           		     System.out.println("\nERROR: Hotel ID should be a positive number");
                	 okaySoFar = false;
                 }  
            } catch(NumberFormatException nfe) {
            	System.out.println("\nERROR: Hotel ID should be a number");
            }
            
            // Check if entered room number is valid ( i.e non-negative number)
            try{
            	 if (attributeName.equalsIgnoreCase("RoomNumber") && Integer.parseInt(proposedValue) <= 0) {
            		 System.out.println("\nERROR: Room Number should be a positive number");
                 	 okaySoFar = false;
                 }  
            } catch(NumberFormatException nfe) {
            	System.out.println("\nERROR: Room number should be a number");
            }  
            
            // Check if entered room category is valid ( i.e 'Economy', 'Deluxe', 'Executive Suite', 'Presidential Suite' )
            if (attributeName.equalsIgnoreCase("RoomCategory") && 
        	     !(proposedValue.equals("Economy") || proposedValue.equals("Deluxe") || proposedValue.equals("Executive Suite") || proposedValue.equals("Presidential Suite"))) {
        		 	System.out.println("\nERROR: Allowed values for room category are 'Economy', 'Deluxe', 'Executive Suite', 'Presidential Suite' ");
        		 	okaySoFar = false; 
            } 
            
            // Check if entered room max occupancy is valid ( i.e non-negative number)
            try{
            	 if (attributeName.equalsIgnoreCase("RoomMaxOccupancy")) {
            		 if (Integer.parseInt(proposedValue) <= 0) {
            			 System.out.println("\nERROR: Room Max Occupancy should be a positive number");
                      	 okaySoFar = false; 	
            		 } 
            		 if (Integer.parseInt(proposedValue) > 4) {
            			 System.out.println("\nERROR: Maximum allowed value for Room Occupancy is 4");
                      	 okaySoFar = false; 	
            		 } 
                 }
            } catch(NumberFormatException nfe) {
            	System.out.println("\nERROR: Room Max Occupancy should be a number");
            }
             
            // Check if entered Room Nightly rate is valid ( i.e non-negative number)
            try{
            	 if (attributeName.equalsIgnoreCase("RoomNightlyRate") && Integer.parseInt(proposedValue) <= 0) {
            		 System.out.println("\nERROR: Room Nightly rate should be a positive number");
                 	 okaySoFar = false;
                 }
            } catch(NumberFormatException nfe) {
            	System.out.println("\nERROR: Room Nightly rate should be a number");
            }
             
        }
        catch (Throwable err) {
            handleError(err);
            okaySoFar = false;
        }
        
        // Return
        return okaySoFar;
        
    }
    
    // QUERIES
    
    /** 
     * DB Query: Stay bill and itemized receipt
     * 
     * Note:    Since we want to produce an itemized receipt, and then pull from it the total amount owed by the customer, it would be awesome if we could use the VIEW feature
     *          (sort of a stored query that can be operated on like a table).
     *          Per http://www.mysqltutorial.org/mysql-views.aspx, "You cannot use subqueries in the FROM clause of the SELECT statement that defines the view before MySQL 5.7.7"
     *          Per https://classic.wolfware.ncsu.edu/wrap-bin/mesgboard/csc:540::001:1:2018?task=ST&Forum=8&Topic=7, "We are running version 5.5.57 it seems"
     * 
     * Arguments -  stayID -        The ID of the stay for which we wish to generate a bill and an itemized receipt
     *              reportResults - True if we wish to report the itemized receipt and total amount owed
     *                              (false for mass calculation of amounts owed on stays)
     * Return -     None
     * 
     * Modifications:   03/11/18 -  ATTD -  Created method.
     *                  03/23/18 -  ATTD -  Use new general error handler.
     */
    public static void queryItemizedReceipt (int stayID, boolean reportResults) {

        try {
            
            // Declare variables
            NumberFormat currency;
            String itemizedReceiptSQL;
            double amountOwedBeforeDiscount = 0d;
            double amountOwedAfterDiscount = 0d;
            
            // Start transaction
            jdbc_connection.setAutoCommit(false);
            
            try {
                
                // Generate an itemized receipt for the stay
                // TODO: use prepared statement instead
                itemizedReceiptSQL = 
                        "(" + 
                            "SELECT 'NIGHT' AS Item, Nights AS Qty, NightlyRate AS ItemCost, Nights * NightlyRate AS TotalCost " + 
                            "FROM (" + 
                                "SELECT DATEDIFF(EndDate, StartDate) AS Nights, NightlyRate " + 
                                "FROM (" + 
                                    "SELECT StartDate, EndDate, NightlyRate " + 
                                    "FROM Rooms NATURAL JOIN (" + 
                                        "SELECT StartDate, EndDate, RoomNum, HotelID " + 
                                        "FROM Stays " + 
                                        "WHERE ID = " + 
                                        stayID +
                                    ") AS A " + 
                                ") AS B " + 
                            ") AS C " + 
                        ") " + 
                        "UNION " + 
                        "(" + 
                            "SELECT Name AS Item, COUNT(*) AS Qty, Cost AS ItemCost, COUNT(*) * Cost AS TotalCost " + 
                            "FROM (" + 
                                "ServiceTypes NATURAL JOIN (" + 
                                    "SELECT StayID, ServiceName AS Name " + 
                                    "FROM Provided " + 
                                    "WHERE StayID = " + 
                                    stayID + 
                                ") AS ServicesProvided " + 
                            ") GROUP BY Item" + 
                        ")";
                jdbc_result = jdbc_statement.executeQuery(itemizedReceiptSQL + ";");
                
                // Print the itemized receipt and the total amount owed
                if (reportResults) {
                    System.out.println("\nItemized Receipt for Stay ID: " + stayID + "\n");
                    printQueryResultSet(jdbc_result);
                }
                
                // Calculate the total amount owed, both before and after the possible hotel credit card discount
                // TODO: use prepared statement instead
                jdbc_result = jdbc_statement.executeQuery(
                        "SELECT SUM(TotalCost) FROM (" + 
                        itemizedReceiptSQL + 
                        ") AS ItemizedReceipt;");
                jdbc_result.next();
                amountOwedBeforeDiscount = jdbc_result.getDouble(1);
                // TODO: use prepared statement instead
                jdbc_result = jdbc_statement.executeQuery(
                        "SELECT IF((SELECT CardType FROM Stays WHERE ID = " + 
                        stayID + 
                        ") = 'HOTEL', SUM(TotalCost) * 0.95, SUM(TotalCost)) FROM (" + 
                        itemizedReceiptSQL + 
                        ") AS ItemizedReceipt;");
                jdbc_result.next();
                amountOwedAfterDiscount = jdbc_result.getDouble(1);
                
                // Update the stay with the total amount owed
                // TODO: use prepared statement instead
                jdbc_statement.executeUpdate("UPDATE Stays SET AmountOwed = " + amountOwedAfterDiscount + " WHERE ID = " + stayID);
                
                // If success, commit
                jdbc_connection.commit();
                
                // Print the total amount owed
                if (reportResults) {
                    currency = NumberFormat.getCurrencyInstance();
                    System.out.println("\nTotal Amount Owed Before Discount: " + currency.format(amountOwedBeforeDiscount));
                    System.out.println("\nWolfInns Credit Card Discount Applied: " + (amountOwedBeforeDiscount == amountOwedAfterDiscount ? "0%" : "5%"));
                    System.out.println("\nTotal Amount Owed After Discount: " + currency.format(amountOwedAfterDiscount) + "\n");
                }
                
            }
            catch (Throwable err) {
                // Handle error
                handleError(err);
                // Roll back the entire transaction
                jdbc_connection.rollback(); 
            }
            finally {
                // Restore normal auto-commit mode
                jdbc_connection.setAutoCommit(true);
            }
            
        }
        catch (Throwable err) {
            handleError(err);
        }
        
    }
    
    /** 
     * DB Query: Hotel Revenue
     * 
     * Arguments -  hotelID -       The ID of the hotel
     *              queryStartDate -     The start date of the date range we want revenue for
     *              queryEndDate -       The end date of the date range we want revenue for
     * Return -     None
     * 
     * Modifications:   03/09/18 -  ATTD -  Created method.
     *                  03/23/18 -  ATTD -  Use new general error handler.
     */
    public static void queryHotelRevenue (int hotelID, String queryStartDate, String queryEndDate) {

        try {
            
            // Declare variables
            double revenue;
            NumberFormat currency;

            /* Get revenue for one hotel from a date range
             * Revenue is earned when the guest checks OUT
             * So we always look at the end date for the customer's stay
             * No transaction needed for a query
             */
            // TODO: use prepared statement instead
            jdbc_result = jdbc_statement.executeQuery("SELECT SUM(AmountOwed) " + 
                    "FROM Stays " +
                    "WHERE HotelID = " + hotelID + " AND Stays.EndDate >= '" + queryStartDate + "' AND Stays.EndDate <= '" + queryEndDate + "'");
            
            jdbc_result.next();
            revenue = jdbc_result.getDouble(1);
            
            // Print report
            currency = NumberFormat.getCurrencyInstance();
            System.out.println("\nRevenue earned: " + currency.format(revenue) + "\n");
            
        }
        catch (Throwable err) {
            handleError(err);
        }
        
    }
    
    // UPDATES
    
    /** 
     * DB Update: Insert Hotel
     * 
     * Note:    This does NOT support chain reaction of moving managers between hotels.
     *          Manager of new hotel CANNOT be existing manager of another hotel.
     * 
     * Arguments -  hotelName -     The name of the hotel to insert
     *              streetAddress - The street address of the hotel to insert
     *              city -          The city in which the hotel is located
     *              state -         The state in which the hotel is located
     *              phoneNum -      The phone number of the hotel
     *              managerID -     The staff ID of the person to promote to manager of the new hotel
     *              reportSuccess - True if we should print success message to console (should be false for mass population of hotels)
     * Return -     None
     * 
     * Modifications:   03/09/18 -  ATTD -  Created method.
     *                  03/12/18 -  ATTD -  Add missing JDBC commit statement.
     *                  03/16/18 -  ATTD -  Changing departments to emphasize their meaninglessness.
     *                  03/20/18 -  ATTD -  Switch to using prepared statements.
     *                  03/21/18 -  ATTD -  Debug inserting new hotel with prepared statements.
     *                  03/23/18 -  ATTD -  Use new general error handler.
     */
    public static void updateInsertHotel (String hotelName, String streetAddress, String city, String state, long phoneNum, int managerID, boolean reportSuccess) {
        
        // Declare variables
        int newHotelID;
        
        try {

            // Start transaction
            jdbc_connection.setAutoCommit(false);
            
            try {

                /* Insert new hotel, using prepared statement
                 * Drop disable unique checks to avoid complaint
                 * This is needed because the hotel may get a manager who is already managing a different hotel
                 * We will fix this shortly but we need to avoid complaint in the meantime
                 */
                jdbcPrep_insertNewHotel.setString(1, hotelName);
                jdbcPrep_insertNewHotel.setString(2, streetAddress);
                jdbcPrep_insertNewHotel.setString(3, city);
                jdbcPrep_insertNewHotel.setString(4, state);
                jdbcPrep_insertNewHotel.setLong(5, phoneNum);
                jdbcPrep_insertNewHotel.setInt(6, managerID);
                jdbcPrep_insertNewHotel.setInt(7, managerID); 
                jdbcPrep_insertNewHotel.executeUpdate();
                
                // Update new hotel's manager (job title and hotel assignment), using prepared statement
                jdbcPrep_updateNewHotelManager.executeUpdate();

                // If success, commit
                jdbc_connection.commit();
                
                // Then, tell the user about the success
                if (reportSuccess) {
                    jdbc_result = jdbcPrep_getNewestHotelID.executeQuery();
                    jdbc_result.next();
                    newHotelID = jdbc_result.getInt(1);
                    System.out.println("\n'" + hotelName + "' hotel added (hotel ID: " + newHotelID + ")!\n");
                }
                
            }
            catch (Throwable err) {
                
                // Handle error
                handleError(err);
                
                // Roll back the entire transaction
                jdbc_connection.rollback();
                
            }
            finally {
                // Restore normal auto-commit mode
                jdbc_connection.setAutoCommit(true);
            }
            
        }
        catch (Throwable err) {
            handleError(err);
        }
        
    }
    
    /** 
     * DB Update: Change Hotel Information
     * 
     * Arguments -  hotelID -           The ID of the hotel to update
     *              attributeToChange - The attribute to update
     *              valueToChangeTo -   The value to update this attribute to (as a string)
     * Return -     None
     * 
     * Modifications:   03/23/18 -  ATTD -  Created method.
     */
    public static void updateChangeHotelInfo (int hotelID, String attributeToChange, String valueToChangeTo) {
        
        try {

            // Safeguard - make sure changes will commit
            jdbc_connection.setAutoCommit(true);
            
            // Update hotel info, using prepared statement
            switch (attributeToChange) {
                case "Name":
                    jdbcPrep_udpateHotelName.setString(1, valueToChangeTo);
                    jdbcPrep_udpateHotelName.setInt(2, hotelID);
                    jdbcPrep_udpateHotelName.executeUpdate();
                    break;
                case "StreetAddress":
                    jdbcPrep_updateHotelStreetAddress.setString(1, valueToChangeTo);
                    jdbcPrep_updateHotelStreetAddress.setInt(2, hotelID);
                    jdbcPrep_updateHotelStreetAddress.executeUpdate();
                    break;
                case "City":
                    jdbcPrep_updateHotelCity.setString(1, valueToChangeTo);
                    jdbcPrep_updateHotelCity.setInt(2, hotelID);
                    jdbcPrep_updateHotelCity.executeUpdate();
                    break;
                case "State":
                    jdbcPrep_udpateHotelState.setString(1, valueToChangeTo);
                    jdbcPrep_udpateHotelState.setInt(2, hotelID);
                    jdbcPrep_udpateHotelState.executeUpdate();
                    break;
                case "PhoneNum":
                    jdbcPrep_updateHotelPhoneNum.setLong(1, Long.parseLong(valueToChangeTo));
                    jdbcPrep_updateHotelPhoneNum.setInt(2, hotelID);
                    jdbcPrep_updateHotelPhoneNum.executeUpdate();
                    break;
                case "ManagerID":
                    /* This one is a special case
                     * 1 -  Demote old manager to front desk representative
                     * 2 -  Actually update the manager ID of the hotel
                     * 3 -  Update new manager to have the correct job title and hotel assignment
                     */
                    jdbc_connection.setAutoCommit(false);
                    try {
                        // Demote old manager
                        jdbcPrep_demoteOldManager.setInt(1, hotelID);
                        jdbcPrep_demoteOldManager.executeUpdate();
                        // Update hotel manager ID
                        jdbcPrep_updateHotelManagerID.setLong(1, Integer.parseInt(valueToChangeTo));
                        jdbcPrep_updateHotelManagerID.setInt(2, hotelID);
                        jdbcPrep_updateHotelManagerID.executeUpdate();
                        // Promote new manager
                        jdbcPrep_promoteNewManager.setInt(1, hotelID);
                        jdbcPrep_promoteNewManager.setInt(2, Integer.parseInt(valueToChangeTo));
                        jdbcPrep_promoteNewManager.executeUpdate();
                        // If success, commit
                        jdbc_connection.commit();
                    }
                    catch (Throwable err) {
                        // Handle error
                        handleError(err);
                        // Roll back the entire transaction
                        jdbc_connection.rollback(); 
                    }
                    finally {
                        // Restore normal auto-commit mode
                        jdbc_connection.setAutoCommit(true);
                    }
                    break;
                default:
                    System.out.println(
                        "\nCannot update the '" + attributeToChange + "' attribute of a hotel, because this is not a recognized attribute for Wolf Inns hotels\n"
                    );
                    break;
            }
            
        }
        catch (Throwable err) {
            handleError(err);            
        }
        
    }
    
    /** 
     * DB Update: Delete Hotel
     * 
     * Why does this method exist at all when it is so dead simple?
     * 1. To keep with the pattern of isolating methods that directly interact with the DBMS, 
     * from those that interact with the user (readability of code)
     * 2. In case in the future we find some need for mass deletes.
     * 
     * Arguments -  hotelID -       The ID of the hotel
     *              reportSuccess - True if we should print success message to console (should be false for mass deletion of hotels)
     * Return -     None
     * 
     * Modifications:   03/09/18 -  ATTD -  Created method.
     *                  03/23/18 -  ATTD -  Use new general error handler.
     *                  03/24/18 -  ATTD -  Use prepared statement.
     *                                      Do not claim success unless the hotel actually got deleted.
     */
    public static void updateDeleteHotel (int hotelID, boolean reportSuccess) {

        try {

            /* Remove the hotel from the Hotels table
             * No need to explicitly set up a transaction, because only one SQL command is needed
             */
            jdbcPrep_deleteHotel.setInt(1, hotelID);
            jdbcPrep_deleteHotel.executeUpdate();
            
            // Did the deletion succeed?
            jdbcPrep_getHotelByID.setInt(1, hotelID);
            jdbc_result = jdbcPrep_getHotelByID.executeQuery();
            if (jdbc_result.next()) {
                // Always complain about a failure (never fail silently)
                System.out.println(
                    "\nHotel ID " + 
                    hotelID + 
                    " was NOT deleted (this can happen if a customer is currently staying in the hotel)\n"
                );
            }
            else {
                // Tell the user about the success, if we're supposed to
                if (reportSuccess) {
                    System.out.println("\nHotel ID " + hotelID + " deleted!\n");
                }
            }
            
        }
        catch (Throwable err) {
             handleError(err);
        }
        
    }
    
    /** 
     * DB Update: Delete Staff Member
     * 
     * Arguments -  staffID -       The ID of the staff member
     *              reportSuccess - True if we should print success message to console (should be false for mass deletion of staff members)
     * Return -     None
     * 
     * Modifications:   03/12/18 -  ATTD -  Created method.
     *                  03/23/18 -  ATTD -  Use new general error handler.
     */
    public static void updateDeleteStaff (long staffID, boolean reportSuccess) {

        try {
            
            // Start transaction
            jdbc_connection.setAutoCommit(false);
            
            try {

                // Declare variables
                int numStaffBefore = 0;
                int numStaffAfter = 0;
                
                // How many staff members exist before the attempt
                // TODO: use prepared statement instead
                jdbc_result = jdbc_statement.executeQuery("SELECT count(*) FROM Staff");
                jdbc_result.next();
                numStaffBefore = jdbc_result.getInt(1);
                
                // Remove the staff member from the Staff table
                // TODO: use prepared statement instead
                jdbc_statement.executeUpdate(
                    "DELETE " +
                    "FROM Staff " +
                    "WHERE ID = " + staffID + " AND ID NOT IN ( " +
                        "SELECT DRSStaff AS X " +
                        "FROM Rooms " +
                        "WHERE (DRSStaff IS NOT NULL) AND ( " +
                            "RoomNum IN ( " +
                                "SELECT RoomNum " +
                                "FROM Stays " +
                                "WHERE EndDate IS NULL OR CheckOutTime IS NULL " +
                            ") " +
                        ") " +
                        "UNION ALL " +
                        "SELECT DCStaff AS X " +
                        "FROM Rooms " +
                        "WHERE (DCStaff IS NOT NULL) AND ( " +
                            "RoomNum IN ( " +
                                "SELECT RoomNum FROM Stays " +
                                "WHERE EndDate IS NULL OR CheckOutTime IS NULL " +
                            ") " +
                        ") " +
                    ");"
                );
                
                // How many staff members exist after the attempt
                // TODO: use prepared statement instead
                jdbc_result = jdbc_statement.executeQuery("SELECT count(*) FROM Staff");
                jdbc_result.next();
                numStaffAfter = jdbc_result.getInt(1);
                
                // If success, commit
                jdbc_connection.commit();
                
                // Then, tell the user about the results
                if (numStaffBefore == numStaffAfter) {
                    System.out.println(
                        "\nStaff ID " + 
                        staffID + 
                        " was NOT deleted " + 
                        "(cannot delete staff members who do not exist, or who are currently dedicated to serving a room)\n"
                    );
                }
                else if (reportSuccess) {
                    System.out.println("\nStaff ID " + staffID + " was deleted!\n");
                }         
                
            }
            catch (Throwable err) {
                
                // Handle error
                handleError(err);
                
                // Roll back the entire transaction
                jdbc_connection.rollback();
                
            }
            finally {
                // Restore normal auto-commit mode
                jdbc_connection.setAutoCommit(true);
            }
            
        }
        catch (Throwable err) {
            handleError(err);
        }
        
    } 
    
    /** 
     * DB Update: Add new room
     * 
     * Arguments -  roomNumber    - Room number 
     *              hotelId       - Hotel to which the room belongs
     *              category      - Room Category ('Economy', 'Deluxe', 'Executive Suite', 'Presidential Suite')
     *              maxOccupancy  - Max Occupancy for the room
     *              nightlyRate   - Nightly rate for the room
     * Return -     None
     * 
     * Modifications:   03/24/18 -  MTA -  Added method. 
     */
    public static void addRoom( int roomNumber, int hotelId, String category, int maxOccupancy, int nightlyRate) {
    
        try {

            // Start transaction
            jdbc_connection.setAutoCommit(false);
            
            try {
            	
            	String insertRoomSQL = "INSERT INTO Rooms (RoomNum, HotelID, Category, MaxOcc, NightlyRate) VALUES (? , ?, ?, ?, ?); ";
            	jdbcPrep_insertNewRoom = jdbc_connection.prepareStatement(insertRoomSQL);
  
            	jdbcPrep_insertNewRoom.setInt(1, roomNumber);
            	jdbcPrep_insertNewRoom.setInt(2, hotelId);
            	jdbcPrep_insertNewRoom.setString(3, category);
            	jdbcPrep_insertNewRoom.setInt(4, maxOccupancy);
            	jdbcPrep_insertNewRoom.setInt(5, nightlyRate);  
                 
            	jdbcPrep_insertNewRoom.executeUpdate();

                // If success, commit
                jdbc_connection.commit();
                
                System.out.println("\n'Room has been successfully added to the database! \n");        
            } 
            catch (Throwable ex) {
                
                // Handle pk violation
            	if (ex.getMessage().matches("(.*)Duplicate entry(.*)for key 'PRIMARY'(.*)")) { 
            		handleError(ex, "PK_ROOMS");
            	} else {
            		handleError(ex);	
            	} 
                
                // Roll back the entire transaction
                jdbc_connection.rollback();
                
            }
            finally {
                // Restore normal auto-commit mode
                jdbc_connection.setAutoCommit(true);
            }
            
        }
        catch (Throwable err) {
            handleError(err);
        }
    }
    
    /** 
     * DB Check: When adding new room, check if room number entered by the user is already associated with some other room in this hotel
     * 
     * Arguments -  roomNumber    - Room number entered by user
     *              hotelId       - Hotel Id entered by user 
     * Return -     boolean       - True if the room number is already associated with some other room in this hotel
     * 
     * Modifications:   03/24/18 -  MTA -  Added method. 
     */
    public static boolean isRoomNumberAvailable(int hotelId, int roomNumber) {  
    	
        try {
        	
			 String sql = "SELECT COUNT(*) AS CNT FROM Rooms WHERE hotelID = ? AND RoomNum = ? ;";
			 
			 jdbcPrep_isValidRoomNumber = jdbc_connection.prepareStatement(sql); 
			 jdbcPrep_isValidRoomNumber.setInt(1, hotelId);
			 jdbcPrep_isValidRoomNumber.setInt(2, roomNumber);   
			 
			 ResultSet rs = jdbcPrep_isValidRoomNumber.executeQuery();
			 int cnt = 0;
			 
			 while (rs.next()) {
				cnt = rs.getInt("CNT"); 	
			 }
			 
			 if (cnt > 0) {
				 System.out.println("ERROR: This room number is already associated with different room in this hotel");
				 return false;
			 }   
			 
        }
        catch (Throwable err) {
            handleError(err);
        } 
        
        return true; 
    }
    
    /** 
     * General error handler
     * Turn obscure error stack into human-understandable feedback
     * 
     * Arguments -  err -   An error object.
     *           -  pkViolation - Name of the primary key constraint being violated
     * Return -     None
     * 
     * Modifications:   03/23/18 -  ATTD -  Created method.
     *                  03/24/18 -  MTA -   Handle primary key violation.
     */
    public static void handleError(Throwable err, String... pkViolation) {
        
        // Declare variables
        String errorMessage;
        
        try {
            
            // Handle specific errors
            errorMessage = err.toString();
            
            // HOTELS constraint violated
            if (errorMessage.contains("UC_HACS")) {
                System.out.println(
                    "\nCannot use this address / city / state for the hotel, because it is already used for another hotel\n"
                );
            }
            else if (errorMessage.contains("UC_HPN")) {
                System.out.println(
                    "\nCannot use this phone number for the hotel, because it is already used for another hotel\n"
                );
            }
            else if (errorMessage.contains("UC_HMID")) {
                System.out.println(
                    "\nCannot use this manager for the hotel, because they are already managing another hotel\n"
                );
            }
            else if (errorMessage.contains("FK_HMID")) {
                System.out.println(
                        "\nCannot use this manager for the hotel, because they are not registered as a Wolf Inns staff member\n"
                    );
            }
            else if (errorMessage.contains("PK_HOTELS")) {
                System.out.println(
                    "\nCannot use this ID for the hotel, because it is already used for another hotel\n"
                );
            }
            
            // STAFF constraint violated
            else if (errorMessage.contains("FK_STAFFHID")) {
                System.out.println(
                    "\nCannot assign the staff member to this hotel, because it is not registered as a Wolf Inns hotel\n"
                );
            }
            else if (errorMessage.contains("PK_STAFF")) {
                System.out.println(
                    "\nCannot use this ID for the Staff, because it is already used for another staff member\n"
                );
            }
            
            // ROOMS constraint violated
            else if (errorMessage.contains("FK_ROOMHID")) {
                System.out.println(
                    "\nCannot assign the room to this hotel, because it is not registered as a Wolf Inns hotel\n"
                );
            }
            else if (errorMessage.contains("FK_ROOMDRSID")) {
                System.out.println(
                    "\nCannot assign this staff member as dedicated room service staff, because they are not registered as a Wolf Inns staff member\n"
                );
            }
            else if (errorMessage.contains("FK_ROOMDCID")) {
                System.out.println(
                    "\nCannot assign this staff member as dedicated catering staff, because they are not registered as a Wolf Inns staff member\n"
                );
            }
            else if (errorMessage.contains("PK_ROOMS") || pkViolation[0].contains("PK_ROOMS")) {
            	System.out.println(
                    "\nCannot use this room number for the hotel, because it is already used for another room\n"
                );
            }
            
            // STAYS constraint violated
            else if (errorMessage.contains("UC_STAYKEY")) {
                System.out.println(
                    "\nCannot use this combination of start date / check in time / room number / hotel ID the stay, because it is already used for another stay\n"
                );
            }
            else if (errorMessage.contains("FK_STAYRID")) {
                System.out.println(
                    "\nCannot use this combination of room number / hotel ID the stay, because it is not registered as a Wolf Inns hotel room\n"
                );
            }
            else if (errorMessage.contains("FK_STAYCSSN")) {
                System.out.println(
                    "\nCannot use this customer SSN the stay, because there is no registered Wolf Inns customer with that SSN\n"
                );
            }
            else if (errorMessage.contains("PK_STAYS")) {
            	System.out.println(
                    "\nCannot use this ID for the stay, because it is already used for another stay\n"
                );
            }
                        
            // PROVIDED constraint violated
            else if (errorMessage.contains("FK_PROVSTAYID")) {
                System.out.println(
                    "\nCannot use this stay ID for the provided service, because it is not registerd as a Wolf Inns customer stay\n"
                );
            }
            else if (errorMessage.contains("FK_PROVSTAFFID")) {
                System.out.println(
                    "\nCannot use this staff member for the provided service, because they are not registerd as a Wolf Inns staff member\n"
                );
            }
            else if (errorMessage.contains("FK_PROVSERV")) {
                System.out.println(
                    "\nCannot use this service name for the provided service, because it is not registerd as a Wolf Inns available service\n"
                );
            }
            else if (errorMessage.contains("PK_PROVIDED")) {
                System.out.println(
                    "\nCannot use this ID for the service provided, because it is already used for another provided service\n"
                );
            }
            
            // Customer constraint violated
            else if (errorMessage.contains("PK_CUSTOMERS")) {
                System.out.println(
                    "\nCannot use this SSN for the customer, because it is already used for another customer\n"
                );
            }
            
            // Service Type constraint violated
            else if (errorMessage.contains("PK_SERVICE_TYPES")) {
                System.out.println(
                    "\nCannot use this name for the service type, because it is already used for another service type\n"
                );
            } 
            
            // Number format error
            else if (errorMessage.contains("NumberFormatException")) {
                System.out.println("Cannot use this value because it it not a number\n");
            }
            
            // Don't know what happened, best we can do is print the stack trace as-is
            else {
                err.printStackTrace();
            }
            
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
        
    }
    
    // MAIN
    
    /* MAIN function
     * 
     * Welcomes the user, states available commands, listens to and acts on user commands
     * 
     * Arguments -  None
     * Return -     None
     * 
     * Modifications:   03/07/18 -  ATTD -  Created method.
     *                  03/08/18 -  ATTD -  Add ability to print entire Provided table.
     *                  03/08/18 -  ATTD -  Add sub-menus (report, etc) off of main menu.
     *                  03/09/18 -  ATTD -  Add ability to delete a hotel.
     *                  03/11/18 -  ATTD -  Add ability to report revenue.
     *                  03/11/18 -  ATTD -  Add ability to generate bill for customer stay.
     *                  03/12/18 -  ATTD -  Add ability to delete staff member.
     *                  03/20/18 -  ATTD -  Add call to new method for creating prepared statements.
     *                  03/21/18 -  ATTD -  Close more resources during clean-up.
     *                  03/23/18 -  ATTD -  Add ability to update basic information about a hotel.
     *                                      Use new general error handler.
     *                  03/24/18 -  MTA -   Add ability to add room.
     *                  03/24/18 -  ATTD -  Close prepared statement for deleting a hotel.
     */
    public static void main(String[] args) {
        
        try {
        
            // Declare local variables
            boolean quit = false;
            String command;
            
            // Print welcome
            System.out.println("\nWelcome to Wolf Inns Hotel Management System");
            
            // Connect to database
            System.out.println("\nConnecting to database...");
            connectToDatabase();
            
            // Create prepared statements
            System.out.println("\nCreating prepared statements...");
            createPreparedStatements();
            
            // Create tables
            System.out.println("\nCreating tables...");
            createTables();
            
            // Populate tables
            System.out.println("\nPopulating tables...");
            populateCustomersTable();
            populateServiceTypesTable();
            populateStaffTable();
            populateHotelsTable();
            updateHotelIdForStaff();
            populateRoomsTable();
            populateStaysTable();
            populateProvidedTable();
            updateAmountOwedForStays();
            
            // Print available commands
            printAvailableCommands(CMD_MAIN);
            
            // Watch for user input
            currentMenu = CMD_MAIN;
            scanner = new Scanner(System.in);
            while (quit == false) {
                System.out.print("> ");
                command = scanner.nextLine();
                switch (currentMenu) {
                    case CMD_MAIN:
                        // Check user's input (case insensitively)
                        switch (command.toUpperCase()) {
                            case CMD_BILLING:
                                // Tell the user their options in this new menu
                                printAvailableCommands(CMD_BILLING);
                                // Remember what menu we're in
                                currentMenu = CMD_BILLING;
                            break;
                            case CMD_REPORTS:
                                // Tell the user their options in this new menu
                                printAvailableCommands(CMD_REPORTS);
                                // Remember what menu we're in
                                currentMenu = CMD_REPORTS;
                                break;
                            case CMD_MANAGE:
                                // Tell the user their options in this new menu
                                printAvailableCommands(CMD_MANAGE);
                                // Remember what menu we're in
                                currentMenu = CMD_MANAGE;
                                break;
                            case CMD_QUIT:
                                quit = true;
                                break;
                            default:
                                // Remind the user about what commands are available
                                System.out.println("\nCommand not recognized");
                                printAvailableCommands(CMD_MAIN);
                                break;
                        }
                        break;
                    case CMD_BILLING:
                        // Check user's input (case insensitively)
                        switch (command.toUpperCase()) {
                            case CMD_BILLING_GENERATE:
                                generateBillAndReceipt();
                                break;
                            case CMD_MAIN:
                                // Tell the user their options in this new menu
                                printAvailableCommands(CMD_MAIN);
                                // Remember what menu we're in
                                currentMenu = CMD_MAIN;
                                break;
                            default:
                                // Remind the user about what commands are available
                                System.out.println("\nCommand not recognized");
                                printAvailableCommands(CMD_BILLING);
                                break;
                        }
                        break;
                    case CMD_REPORTS:
                        // Check user's input (case insensitively)
                        switch (command.toUpperCase()) {
                            case CMD_REPORT_REVENUE:
                                reportHotelRevenue();
                            break;
                            case CMD_REPORT_HOTELS:
                                reportEntireTable("Hotels");
                                break;
                            case CMD_REPORT_ROOMS:
                                reportEntireTable("Rooms");
                                break;
                            case CMD_REPORT_STAFF:
                                reportEntireTable("Staff");
                                break;
                            case CMD_REPORT_CUSTOMERS:
                                reportEntireTable("Customers");
                                break;
                            case CMD_REPORT_STAYS:
                                reportEntireTable("Stays");
                                break;
                            case CMD_REPORT_SERVICES:
                                reportEntireTable("ServiceTypes");
                                break;
                            case CMD_REPORT_PROVIDED:
                                reportEntireTable("Provided");
                                break;
                            case CMD_MAIN:
                                // Tell the user their options in this new menu
                                printAvailableCommands(CMD_MAIN);
                                // Remember what menu we're in
                                currentMenu = CMD_MAIN;
                                break;
                            default:
                                // Remind the user about what commands are available
                                System.out.println("\nCommand not recognized");
                                printAvailableCommands(CMD_REPORTS);
                                break;
                        }
                        break;
                    case CMD_MANAGE:
                        // Check user's input (case insensitively)
                        switch (command.toUpperCase()) {
                        case CMD_MANAGE_HOTEL_ADD:
                            manageHotelAdd();
                            break;
                        case CMD_MANAGE_HOTEL_UPDATE:
                            manageHotelUpdate();
                            break;
                        case CMD_MANAGE_HOTEL_DELETE:
                            manageHotelDelete();
                            break;
                        case CMD_MANAGE_STAFF_DELETE:
                            manageStaffDelete();
                            break;
                        case CMD_MANAGE_ROOM_ADD:
                        	manageRoomAdd();
                            break;
                        case CMD_MANAGE_ROOM_UPDATE:
                        	manageRoomUpdate();
                            break;
                        case CMD_MANAGE_ROOM_DELETE:
                        	manageRoomDelete();
                            break;
                        case CMD_MAIN:
                            // Tell the user their options in this new menu
                            printAvailableCommands(CMD_MAIN);
                            // Remember what menu we're in
                            currentMenu = CMD_MAIN;
                            break;
                        default:
                            // Remind the user about what commands are available
                            System.out.println("\nCommand not recognized");
                            printAvailableCommands(CMD_MANAGE);
                            break;
                        }
                        break;
                    default:
                        break;
                }
            }
            
            // Clean up
            scanner.close();
            jdbc_statement.close();
            jdbc_result.close();
            jdbcPrep_insertNewHotel.close();
            jdbcPrep_updateNewHotelManager.close();
            jdbcPrep_udpateHotelName.close();
            jdbcPrep_updateHotelStreetAddress.close();
            jdbcPrep_updateHotelCity.close();
            jdbcPrep_udpateHotelState.close();
            jdbcPrep_updateHotelPhoneNum.close();
            jdbcPrep_updateHotelManagerID.close();
            jdbcPrep_demoteOldManager.close();
            jdbcPrep_promoteNewManager.close();
            jdbcPrep_getNewestHotelID.close();
            jdbcPrep_getHotelSummaryForAddress.close();
            jdbcPrep_getHotelSummaryForPhoneNumber.close();
            jdbcPrep_getHotelSummaryForStaffMember.close();
            jdbcPrep_getHotelByID.close();
            jdbcPrep_deleteHotel.close(); 
            jdbcPrep_insertNewRoom.close();
            jdbcPrep_isValidRoomNumber.close();
            jdbc_connection.close();
        
        }
        catch (Throwable err) {
            handleError(err);
        }

    }

}
