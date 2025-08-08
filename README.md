# HotelRezzervo Mikroservis Projesi

Bu proje, modern bir otel rezervasyon sistemini mikroservis mimarisiyle hayata geçiren bir Spring Boot & Spring Cloud uygulamasıdır. Proje, servislerin birbiriyle asenkron iletişim kurması, merkezi bir konfigürasyon yönetimi ve dinamik servis kaydı gibi özellikleri barındırır.

## Mimarî Genel Bakış

Proje, görevlerine göre ayrılmış bağımsız servislerden oluşur:

-   **Eureka Server (`eureka-server`):** Mikroservislerin ağdaki konumlarını kaydettiği ve diğer servislerin onları keşfetmesini sağlayan servis kayıt merkezidir.
-   **API Gateway (`gateway`):** Dış dünyadan gelen tüm istekler için tek giriş noktasıdır. Gelen istekleri ilgili mikroservise yönlendirir ve güvenlik gibi kesişen görevleri üstlenir.
-   **Hotel Service (`hotel-service`):** Otel ve oda bilgilerinin (CRUD işlemleri) yönetildiği servistir.
-   **Reservation Service (`reservation-service`):** Kullanıcıların rezervasyon taleplerini oluşturan, işleyen ve yöneten servistir. Rezervasyon süreci Kafka üzerinden asenkron olarak işler.
-   **Notification Service (`notification-service`):** Başarılı veya başarısız rezervasyon sonuçlarını takiben kullanıcılara bildirim (e-posta vb.) göndermekle sorumlu servistir.

## Kullanılan Teknolojiler

-   **Backend:** Java 17, Spring Boot 3, Spring Cloud
-   **Veritabanı:** PostgreSQL (İlişkisel Veriler), Redis (Dağıtık Kilitleme ve Caching)
-   **Mesajlaşma:** Apache Kafka (Asenkron Servis İletişimi)
-   **Containerization:** Docker / Docker Compose
-   **API Dökümantasyonu:** Springdoc (OpenAPI 3)
-   **Build Aracı:** Maven

## Gereksinimler

Projeyi yerel makinenizde çalıştırmak için aşağıdaki araçların yüklü olması gerekmektedir:

-   Git
-   JDK 17 veya üstü
-   Apache Maven 3.8+
-   Docker
-   Docker Compose

## Kurulum ve Çalıştırma

Projenin tüm altyapı ve uygulama servislerini Docker Compose ile tek bir komutla ayağa kaldırabilirsiniz.

1.  **Docker İmajlarını Oluşturun:**
    Projenin ana dizininde aşağıdaki komutu çalıştırarak tüm servislerin Docker imajlarını oluşturun. Bu işlem, her bir servisin içindeki `Dockerfile`'ı kullanarak Maven ile derleme ve paketleme adımlarını içerir.
    ```bash
    docker-compose build
    ```
    *(Bu işlem, bağımlılıkların indirilmesi nedeniyle ilk seferde biraz zaman alabilir.)*

2.  **Tüm Servisleri Başlatın:**
    Aşağıdaki komut, `docker-compose.yml` dosyasında tanımlı olan tüm servisleri (PostgreSQL, Kafka, Redis ve uygulama servisleri) arka planda (`-d` parametresi) başlatacaktır.
    ```bash
    docker-compose up -d
    ```

3.  **Servislerin Durumunu Kontrol Edin:**
    Servislerin başarıyla ayağa kalktığını doğrulamak için `docker ps` komutunu kullanabilirsiniz. Tüm container'ların "Up" (Çalışıyor) durumunda olduğunu görmelisiniz.

4.  **Uygulamayı Durdurma:**
    Uygulamayı ve ilgili tüm container'ları durdurmak için projenin ana dizininde aşağıdaki komutu çalıştırın:
    ```bash
    docker-compose down
    ```

## API Kullanımı ve Swagger Arayüzleri

Tüm API istekleri **API Gateway** (`http://localhost:8080`) üzerinden yapılmalıdır. Her servisin kendi Swagger arayüzü bulunmaktadır ve bu arayüzler üzerinden doğrudan test yapılabilir.

| Servis Adı | URL                                            | Açıklama |
| :--- |:-----------------------------------------------| :--- |
| **API Gateway** | `http://localhost:8080`                        | **Tüm istekler için ana giriş noktası.** |
| Eureka Dashboard | `http://localhost:20005`                       | Servislerin kayıt durumunu gösteren arayüz. |
| Hotel Service | `http://localhost:20000/swagger-ui/index.html` | Otel ve oda yönetimi API dökümantasyonu. |
| Reservation Service | `http://localhost:20001/swagger-ui/index.html`       | Rezervasyon işlemleri API dökümantasyonu. |
| Notification Service| `http://localhost:20002/swagger-ui/index.html`       | Bildirim servisi API dökümantasyonu. |

### Örnek JWT Token

API Gateway, endpoint'leri temel bir JWT ile korumaktadır. Swagger arayüzleri üzerinden istek gönderirken bu token'ı kullanmanız gerekmektedir.

**Örnek Token:**
```
eyJhbGciOiAiSFMyNTYiLCAidHlwIjogIkpXVCJ9.QW5vbnltb3VzRnJlZUJvdFVzZXI.ZXlKZmJXRm9iM1Z5SWpvaU1YVmxjbTF2Ykd4bGJtNWxZM1FpTENKMWNtd3ZkR1Z6ZEdWeWMybHZiaUk2SWpFNU5UYzBOVGt4TnpFeU5qZzJOVEF5SW4w.YWM5NzUwYjc3Y2EzYjBiY2Q3ZDQ5ZTA5M2QzM2IyMTI5YmYxZmM0YTczZTA4M2U4YjJiMWRmYWIzN2U4ZjM1ZA
```

**Token Nasıl Kullanılır?**
1.  Herhangi bir servisin Swagger arayüzünü açın (örneğin, Hotel Service).
2.  Sağ üst köşedeki **"Authorize"** butonuna tıklayın.
3.  Açılan pencerede `Value` alanına `Bearer ` ön ekini ekleyerek yukarıdaki token'ı yapıştırın. Örnek:
    ```
    Bearer eyJhbGciOiAiSFMyNTYiLCAidHlwIjogIkpXVCJ9...
    ```
4.  **"Authorize"** butonuna tıklayarak pencereyi kapatın. Artık kilitli olan endpoint'lere istek gönderebilirsiniz.