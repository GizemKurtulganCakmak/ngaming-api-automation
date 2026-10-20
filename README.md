NGaming API Test Otomasyonu
- Bu proje, NGaming teknik değerlendirme çalışması kapsamında Nesine Popular Coupons API servisinin fonksiyonel ve veri tutarlılığı kontrollerini otomatikleştirmek amacıyla hazırlanmıştır.

Kullanılan Teknolojiler ve Amacı:
- Java 21	Programlama dili
- Maven	Bağımlılık ve proje yönetimi
- REST Assured	API test otomasyonu
- Java HttpClient	HTTP hata yanıtlarının kontrolü
- Cucumber	BDD senaryo yönetimi
- JUnit 5	Test çalıştırma altyapısı
- Jackson	JSON–POJO dönüşümü
- JSON Schema Validator	Yanıt yapısı doğrulama
- Allure	Test sonuçlarının raporlanması

Test Edilen Servis:
- HTTP Metodu: GET
- Endpoint:
https://pc.nesine.com/v1/PopularCoupons
- Query Parameter: eventCount
- API’nin temel URL bilgisi Maven üzerinden baseUrl parametresi ile değiştirilebilir.

Test Kapsamı:
- API Yanıt ve Veri Doğrulamaları
- HTTP durum kodu ve Content-Type kontrolü
- JSON yanıtının POJO nesnelerine dönüştürülmesi
- JSON Schema doğrulaması
- Kuponların etkinlik sayısı tutarlılığı
- Minimum ve maksimum oran kontrolü
- Spor türü ve Sport ID eşleşmesi
- Kupon sıralama kontrolü

Query Parameter Testleri:
- Geçerli parametre değerleri: 2, 3
- Geçersiz parametre değerleri: -1, abc, 999
- Parametre gönderilmediğinde varsayılan davranış
- eventCount=5 için ayrı tutulan bilinen durum testi

Ek Test Senaryoları:
- Duplicate Event Validation
- Bir kupon içerisindeki etkinliklerin betradarId üzerinden benzersizliği doğrulanır.

Minimum Event Date Validation:
- Kupondaki minEventDateEpoch değeri ile etkinliklerin en erken startDateEpoch değeri karşılaştırılır.

Proje Yapısı:
ngaming-api-automation/
├── pom.xml
├── README.md
└── src/
└── test/
├── java/com/gizemcakmak/automation/
│   ├── client/
│   ├── config/
│   ├── context/
│   ├── hooks/
│   ├── model/
│   ├── runners/
│   ├── steps/
│   └── validation/
└── resources/
├── features/
├── schemas/
├── allure.properties
└── junit-platform.properties

Kurulum ve Çalıştırma:
- Gereksinimler: Java 21 ve Maven 3.9 veya üzeri.
- Proje klasöründe terminal açılarak aşağıdaki komutlar çalıştırılabilir.

Standart test koşumu:
- mvn clean test '-Dcucumber.filter.tags=not @knownIssue'

Smoke testleri:
- mvn clean test '-Dcucumber.filter.tags=@smoke'

Ek senaryolar:
- mvn clean test '-Dcucumber.filter.tags=@extra'

Bilinen durum senaryosu:
- mvn clean test '-Dcucumber.filter.tags=@knownIssue'

Farklı API adresiyle çalıştırma:
- mvn clean test '-DbaseUrl=https://pc.nesine.com' '-Dcucumber.filter.tags=not @knownIssue'

Paralel Test Çalıştırma:
- Cucumber senaryoları JUnit Platform yapılandırması üzerinden paralel çalışabilecek şekilde düzenlenmiştir.
- Paralel yürütme ayarları junit-platform.properties dosyasında bulunmaktadır.
- Testlerin birbirinden bağımsız çalışabilmesi için yanıt ve doğrulama bilgileri senaryo bazında yönetilmektedir.

Allure Raporlama:
- Test sonuçları otomatik olarak aşağıdaki dizinde oluşturulur:
target/allure-results

- Allure Commandline kurulu olduğunda rapor şu komutla görüntülenebilir:
allure serve target/allure-results

- Rapor üzerinden senaryo sonuçları, hata detayları, çalışma süreleri ve paralel yürütme bilgileri incelenebilir.

Bilinen Durumlar:
- eventCount=5 davranışı
- eventCount=5 parametresiyle yapılan bazı isteklerde API’nin 6 etkinlik içeren kuponlar döndürdüğü gözlemlenmiştir.
- Bu davranışın beklenen filtreleme kuralına uygunluğu API sözleşmesi üzerinden netleştirilmelidir.
- İlgili senaryo @knownIssue etiketiyle korunmuş ve standart regresyon koşumunun dışında tutulmuştur.

Sonuç:
- Proje kapsamında API yanıt doğrulama, veri bütünlüğü, query parameter kontrolleri, paralel test yürütme ve Allure raporlama uygulanmıştır.
- Bilinen durum hariç tutularak gerçekleştirilen son regresyon koşumunda 11 senaryonun tamamı başarıyla çalışmıştır.
- Test sonuçları API’nin güncel veri ve davranışına göre değişiklik gösterebilir.