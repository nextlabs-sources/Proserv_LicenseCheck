Please run the LicenseChecker.jar as follows:

1. The deployment structure is as follows:
    conf/
        (log4j.properties)
    lib/
        (jar files)
    LicenseChecker.jar

2. Copy the license.dat to the root folder. So the structure becomes:
    conf/
    lib/
    LicenseChecker.jar
    license.dat

3. Run the LicenseChecker.jar with the following command.
   a. Include all jar files in the classpath, and the folder containing license.dat
   b. Pass the command line arguments - the license.jar file path, the output file (to store license properties)

   java -cp ..\LicenseChecker;..\LicenseChecker\*;..\LicenseChecker\lib\*;..\LicenseChecker\conf
   com.nextlabs.license.checker.LicenseChecker D:\LicenseChecker\lib\license.jar
   D:\LicenseChecker\lib\license.properties



--------------------------------------------------------