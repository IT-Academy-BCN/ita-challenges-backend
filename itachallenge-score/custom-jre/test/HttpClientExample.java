import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

public class HttpClientExample {
    public static void main(String[] args) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://example.com"))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response: " + response.body());
        } catch (Exception e) {
            System.out.println("Expected failure: " + e.getMessage());
        }
    }
}


/* Expected output:
**
** Exception in thread "main" java.lang.NoClassDefFoundError: java/net/http/HttpClient
**        at HttpClientExample.main(HttpClientExample.java:9)
** Caused by: java.lang.ClassNotFoundException: java.net.http.HttpClient
**        at java.base/jdk.internal.loader.BuiltinClassLoader.loadClass(Unknown Source)
**        at java.base/jdk.internal.loader.ClassLoaders$AppClassLoader.loadClass(Unknown Source)
**        at java.base/java.lang.ClassLoader.loadClass(Unknown Source)
**        ... 1 more*/