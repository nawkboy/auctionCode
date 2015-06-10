# Auction Code Overview and Setup
Example auction code demonstrating the use of a quasi-fluent internal DSL in contrast to Gherkin.

# Abbreviated Setup Procedures

***Only a few people in the class need to setup the example auction code on their laptops.*** If setting up the auction code on your laptop is out of your comfort zone, please don't worry about it.

### Install JDK

Install a Java JDK and make it available on the command line by setting PATH or equivalent.

Java JDK (Java SE Development Kit 8):
http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html

##### Hints

A somewhat older JDK will likely work fine. The code is older and doesn't make use of any 1.8 language features.

If you can successfully run: "javac --version" and "java --version" on the command line and get the same version numbers you should be in business.

### Install Maven

Install Maven and make it available on the command line.

http://maven.apache.org/

##### Hints

If you can successfully run: "mvn --help" you are likely good.

### Install Git

Install Git as appropriate to your workstation. Details can be found at: http://git-scm.com/

##### Hints

Mac installs this automatically as part of XCode tools. All I did on my mac was drop to a command line and run "git --help" at which point OSX automatically prompted to download and install the XCode command line tools including git.

### Clone Code

Pull down the example auction code.

````
git clone git@github.com:nawkboy/auctionCode.git
````

### Execute Maven

Try building the code.

From directory with pom.xml file:

````
prompt>mvn clean
prompt>mvn install
````

##### Hints 

If this is your first time to run maven on a project, expect to see maven download a whole lot of maven plugins and project dependencies. The second time you run maven on the project it will be much quieter.

If "mvn install" runs successfully you should be good. This will will have compiled the code and run all junit tests.

### IDE

Install an appropriate Java IDE and make sure you can run the auctionCode in it.

I recommend the free community edition of IntelliJ: http://www.jetbrains.com/idea/

##### Hints

If you choose IntelliJ, just open the Maven pom.xml file and IntelliJ will automatically build an IntelliJ project using information in the Maven pom.xml file. Eclipse usage with a Maven Eclipse plugin installed should be similarly easy.

The first time you run IntelliJ on a project, you will typically have to tell it where your JDK is.

If using IntelliJ you should be able to right click and run any test or directory of tests. You should also be able to easily run a test in the debugger and have it slam into a breakpoint you set.

### Code Navigation

Take a look around at the code. I recommend the following entry points:

##### Automated Acceptance Tests
````
com.acme.attdexample.InvoiceCalculationFeatureTest
com.acme.attdexample.NonFluentInvoiceCalculationFeatureTest
````

##### One of several Unit Tests
````
com.acme.auction.AuctionListingTest
````

# Note on Test Coverage

This code was written to demonstrate the advantages of a quasi-fluent internal DSL for ATDD in contrast to Gherkin. As such the unit test coverage is not as extensive as it would be for real production code. Just because I didn't test all the edge conditions of the main source code doesn't mean you shouldn't in real world enterprise code. Of course, in real world production code you would also have a real database and real web services that communicate over the wire. :)
