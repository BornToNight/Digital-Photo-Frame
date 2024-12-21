
#include <Arduino.h>
#include <ESP8266WiFi.h>
#include <Hash.h>
#include <ESPAsyncTCP.h>
#include <ESPAsyncWebServer.h>
#include <Adafruit_Sensor.h>
#include <DHT.h>

const char* ssid     = "name"; // Wi-Fi name
const char* password = "password"; // Wi-Fi password

#define DHTPIN D4     // Подключение Датчика dht11 к ESP

// Выбор датчика
#define DHTTYPE    DHT11     // DHT 11
//#define DHTTYPE    DHT22     // DHT 22 (AM2302)
//#define DHTTYPE    DHT21     // DHT 21 (AM2301)

DHT dht(DHTPIN, DHTTYPE);

// Переменные для хранения Температуры и Влажности
float t = 0.0;
float h = 0.0;

// Создаём AsyncWebServer object на 80 порту
AsyncWebServer server(80);

unsigned long previousMillis = 0;    // Время последнего обновления датчика DHT


const long interval = 600000;        // Обновлять показания датчика DHT каждые 10 минут

// Вывод HTML страницы в браузер
const char index_html[] PROGMEM = R"rawliteral(
<!DOCTYPE HTML><html>
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <style>
    html {
     font-family: Arial;
     display: inline-block;
     margin: 0px auto;
     text-align: center;
    }
    h2 { font-size: 30px; text-decoration: underline;}       // Заголовок ПОГОДА СЕЙЧАС
    p { font-size: 30px; }
    .units { font-size: 20px; }
    #temperature { font-size: 35px; color: red; }           // цифры ТЕМПЕРАТУРА
    .dht-labels{
      font-size: 30px;
      vertical-align:middle;
      padding-bottom: 15px; color: green;
    }
  </style>
</head>
<body>
  <h2>Погода сейчас!!!</h2>
  <p>
    <span class="dht-labels" style="font-size: 25px; color: green;">Температура</span>
    <span id="temperature" style="font-size: 35px; color: red;">%TEMPERATURE%</span>
    <sup class="units">&deg;C</sup>
  </p>
  <p>
    <span class="dht-labels" style="font-size: 25px; color: green;">Влажность</span>
    <span id="humidity" style="font-size: 35px; color: blue;">%HUMIDITY%</span>
    <sup class="units">%</sup>
  </p>
</body>
<script>
setInterval(function ( ) {
  var xhttp = new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
      document.getElementById("temperature").innerHTML = this.responseText;
    }
  };
  xhttp.open("GET", "/temperature", true);
  xhttp.send();
}, 10000 ) ;

setInterval(function ( ) {
  var xhttp = new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
      document.getElementById("humidity").innerHTML = this.responseText;
    }
  };
  xhttp.open("GET", "/humidity", true);
  xhttp.send();
}, 10000 ) ;
</script>
</html>)rawliteral";

// Replaces placeholder with DHT values
String processor(const String& var){
    if(var == "TEMPERATURE"){
        return String(t);
    }
    else if(var == "HUMIDITY"){
        return String(h);
    }
    return String();
}

void setup(){
    // Serial port for debugging purposes
    Serial.begin(115200);
    dht.begin();

    WiFi.mode(WIFI_STA);
    WiFi.begin(ssid, password);

    while (WiFi.status() != WL_CONNECTED) {
        delay(1000);
        Serial.print(".");
    }

    Serial.println("");
    Serial.println("WiFi connected");
    Serial.println("IP address: ");
    Serial.println(WiFi.localIP());

    // Route for root / web page
    server.on("/", HTTP_GET, [](AsyncWebServerRequest *request){
        request->send_P(200, "text/html", index_html, processor);
    });
    server.on("/temperature", HTTP_GET, [](AsyncWebServerRequest *request){
        request->send_P(200, "text/plain", String(t).c_str());
    });
    server.on("/humidity", HTTP_GET, [](AsyncWebServerRequest *request){
        request->send_P(200, "text/plain", String(h).c_str());
    });

    // Start server
    server.begin();
}

void loop(){

    unsigned long currentMillis = millis();
    if (currentMillis - previousMillis >= interval) {
        // Сохраняем время последнего обновления значения DHT
        previousMillis = currentMillis;
        // Считываем температуру
        float newT = dht.readTemperature();
        if (isnan(newT)) {
            Serial.println("Ошибка при считывании температуры с датчика DHT");
        }
        else {
            t = newT;
            Serial.println(t);
        }
        // Считываем влажность
        float newH = dht.readHumidity();
        if (isnan(newH)) {
            Serial.println("Ошибка при считывании влажности с датчика DHT");
        }
        else {
            h = newH;
            Serial.println(h);
        }
    }
}
