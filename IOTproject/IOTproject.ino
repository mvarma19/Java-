#include <SPI.h>
#include <Ethernet.h>
 #include <LiquidCrystal.h>
#include <SoftwareSerial.h>
#include "HX711.h"  //You must have this library in your arduino library folder
 
#define DOUT  3
#define CLK  2
// initialize the library with the numbers of the interface pins
LiquidCrystal lcd(A4,5,6,7,8,9);
 
HX711 scale(DOUT, CLK);
 
//Change this calibration factor as per your load cell once it is found you many need to vary it in thousands
float calibration_factor = -96650; //-106600 worked for my 40Kg max scale setup 

// MAC address from Ethernet shield sticker under board
byte mac[] = { 0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED };
IPAddress ip(192, 168, 1, 15); // IP address, may need to change depending on network
EthernetServer server(80);  // create a server at port 80
int x=0;

SoftwareSerial gsm(15,A5);  
String readString1;
char server1[] = "localhost";    // name address for Google (using DNS)
int weight=99;

void setup()
{
    Ethernet.begin(mac, ip);    // initialize Ethernet device
    server.begin();             // start to listen for clients
    pinMode(A3, INPUT);  // GAS sensor
   lcd.begin(16, 2);
 lcd.setCursor(0,0);
 lcd.print("Gas Status     ");
 scale.set_scale();
  scale.tare(); //Reset the scale to 0
 Serial.begin(9600);
 gsm.begin(9600);
  long zero_factor = scale.read_average(); //Get a baseline reading
  Serial.print("Zero factor: "); //This can be used to remove the need to tare the scale. Useful in permanent scale projects.
  Serial.println(zero_factor);

  
}

void loop()
{
   int gas=digitalRead(A3);
   if(gas==1 && scale.get_units() > 0.5)
   {
      lcd.setCursor(0,1);
        lcd.print("                ");
    
   }
    
   scale.set_scale(calibration_factor); //Adjust to this calibration factor
 
  Serial.print("Reading: ");
  Serial.print(scale.get_units(), 3);
  Serial.print(" kg"); //Change this to kg and re-adjust the calibration factor if you follow SI units like a sane person
  //Serial.print(" calibration_factor: ");
  //Serial.print(calibration_factor);
  lcd.setCursor(0,0);
  lcd.print("weight   ");
  lcd.print(scale.get_units());
  Serial.println();

 
      EthernetClient client = server.available();  // try to get client

    if (client) {  // got client?
        boolean currentLineIsBlank = true;
        while (client.connected()) {
            if (client.available()) {   // client data available to read
                char c = client.read(); // read 1 byte (character) from client
                 readString1 += c;
                // last line of client request is blank and ends with \n
                // respond to client only after last line received
                if (c == '\n' && currentLineIsBlank) {
                    // send a standard http response header
                    client.println("HTTP/1.1 200 OK");
                    client.println("Content-Type: text/html");
                    client.println("Connnection: close");
                    client.println();
                    // send web page
                    client.println("<!DOCTYPE html>");
                    client.println("<html>");
                    client.println("<head>");
                    client.println("<title>IoT based Gas Cylinder Weight and leakage detection</title>");
                    client.println("<meta http-equiv=\"refresh\" content=\"1\">");
                    client.println("</head>");
                    client.println("<body>");
                    client.println("<h1><center>IoT Gas Cylinder Notification</center></h1>");
                    client.println("<p><b>Status of the Gas is: <\b></p>");
                    GetSwitchState(client);
                    client.print("<p>Weight of Gas is:  kg </p> ");
                    client.print(scale.get_units());
                     if(scale.get_units() < 1 && scale.get_units() > 0.5)
 {
  lcd.setCursor(0,1);
        lcd.print("Alert: CYLINDER IS SOON GOING TO BE EMPTY ");
         client.println("   Refill the Cylinder ");
     Serial.print("Refill .. SMS sent "); 
  gsm.println("AT"); //To check if gsm module is working
          delay(2000);
          gsm.println("AT+CMGF=1"); // set the SMS mode to text
          delay(500);
          gsm.print("AT+CMGS=");
          delay(500);
          gsm.print((char)34); //ASCII of ”
          delay(500);
          gsm.print("+918378969989");
          delay(500);
          gsm.println((char)34);
          delay(1500);
          gsm.println("Gas cylinder needs to be refilled."); //this is the message to be sent
           delay(1500);
          gsm.println((char)26);
          
 }
  
                     client.println("</body>");
                    client.println("</html>");
                    break;
                }
                // every line of text received from the client ends with \r\n
                if (c == '\n') {
                    // last character on line of received text
                    // starting new line with next character read
                    currentLineIsBlank = true;
                } 
                else if (c != '\r') {
                    // a text character was received from client
                    currentLineIsBlank = false;
                }
            } // end if (client.available())
        } // end while (client.connected())
        delay(1);      // give the web browser time to receive the data
        client.stop(); // close the connection
          
            
            //clearing string for next read
    } // end if (client)
}

void GetSwitchState(EthernetClient cl)
{   
    
    if (digitalRead(A3))
    {
         
       // cl.println("<p>3 ON</p>");
      
        cl.println("<p>Gas Not Detected </p>");
       
    }
    else {
        cl.println("<p>Gas Detected</p>");
        gsm.println("AT"); //To check if gsm module is working
          delay(2000);
          gsm.println("AT+CMGF=1"); // set the SMS mode to text
          delay(500);
          gsm.print("AT+CMGS=");
          delay(500);
          gsm.print((char)34); //ASCII of ”
          delay(500);
          gsm.print("+918378969989");
          delay(500);
          gsm.println((char)34);
          delay(1500);
          gsm.println("Alert : Gas detected"); //this is the message to be sent
           delay(1500);
          gsm.println((char)26);
          
        lcd.setCursor(0,1);
        lcd.print("Alert Gas Leak ");
         Serial.print(" Gas leak "); 
    
    }
    
    
}
