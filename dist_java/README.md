# YZU Course Selection Script  
* **Automation Sign up for Courses/Periodic Inspection Vacancy of Courses**  
* **Real-Time Information**  
* **Secure Course Selection**  
* **Human Behavior Simulation (not attack or crack from server backend)**  
* **Fast Cracking of Verification Code**  
* **EXE File Direct Execution, or Cross-Platform Software (JVM)**  

# Disclaimer
* **Using YZU Course Selection Script is at your own risk.**  
* **Any behavior leads to commercial/academic/moral issues User takes full responsibility.**  
* **This project is only for academic research use only.**  

# JRE Version Requirement

* **Java Runtime Environment >= 1.8.0**

# Library Requirements

* **Java Selenium 4**
* **Tensorflow 1.14**
* **Opencv 3.4.7**

# WebDriver Compatible

* **Chromedriver (chrome)**
* **Geckodriver (Firefox)**

# How to use 

* **AutoChooseCourse : Source code project**  
* **dist_java : Release version**  

## **1. Setup Requirements and library**  
**download and extract lib.zip in 'lib' & jre1.8.0_181.zip in 'bin' from the link:**  
https://drive.google.com/drive/folders/1LoHw8VCSPx-OyNA7xzsKzuJmHZHk_A3u?usp=share_link

**check document arrangement**  

dist_java/  
 │&emsp;│  
 │&emsp;├── WebDriver/  
 │&emsp;│  
 │&emsp;│  
 │&emsp;├── **lib/**  
 │&emsp;│&emsp;&ensp;&nbsp;├── tensorflow-1.14.0.jar  
 │&emsp;│&emsp;&ensp;&nbsp;├── selenium-api-4.8.3.jar  
 │&emsp;│&emsp;&ensp;&nbsp;├── opencv-347.jar  
 │&emsp;│&emsp;&ensp;&nbsp;└── ...  
 │&emsp;└── **jre1.8.0_181/**  
 │&emsp;&emsp;&ensp;&ensp;&emsp;&emsp;&emsp;├── bin/  
 │&emsp;&emsp;&ensp;&ensp;&emsp;&emsp;&emsp;├── lib/  
 │&emsp;&emsp;&ensp;&ensp;&emsp;&emsp;&emsp;├── COPYRIGHT  
 │&emsp;&emsp;&ensp;&ensp;&emsp;&emsp;&emsp;└── ...  
 
 AutoChooseCourse/src/autochoosecourse  
 │&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;│  
 │&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;└──  **lib/**  
 │&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&ensp;├── tensorflow-1.14.0.jar  
 │&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&ensp;├── selenium-api-4.8.3.jar  
 │&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&ensp;├── opencv-347.jar  
 │&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&ensp;└── ...  
 
 ## **2. Build/Run Source Code**  

* **Build java code with Netbeans IDE**  
  * **Prerequisite : Java SE Development Kit 8**  
  * **Packup .jar to .exe (Launch4j)**  
* **Precompiled-code of exe file download from the above link, drag into 'dist_java/'**  

