class Employee {
private:
    int employeeId;                  
    std::string employeeName;        
    std::string employeeDepartment; 
    bool isCurrentlyWorking;        

public:
    void saveEmployeeDetailsToDatabase();  
    void generateEmployeeReportInXml();    
    void generateEmployeeReportInCsv();    
    void terminateEmployeeContract();      
    bool checkIfEmployeeIsWorking();       
};
