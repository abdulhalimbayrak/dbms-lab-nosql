
package app.store;

import com.mongodb.client.*;
import org.bson.Document;
import app.model.Student;
import com.google.gson.Gson;

public class MongoStore {
    static MongoClient client;
    static MongoCollection<Document> collection;
    static Gson gson = new Gson();

    public static void init() {
        client = MongoClients.create("mongodb://localhost:27017"); // bağlantı adresi burada
        collection = client.getDatabase("nosqllab").getCollection("ogrenciler");
        collection.drop(); // eski kayıtları temizle
        for (int i = 0; i < 10000; i++) {
            String id = "2025" + String.format("%06d", i);
            Student s = new Student(id, "Ad Soyad " + i, "Bilgisayar");
            collection.insertOne(Document.parse(gson.toJson(s)));
        }
        System.out.println("-------------------------------------");
        System.out.println("MONGO çalışıyor");
        System.out.println("-------------------------------------");
    }

    public synchronized static Student get(String id) {
        Document doc = collection.find(new Document("ogrenciNo", id)).first();
        return doc != null ? gson.fromJson(doc.toJson(), Student.class) : null;
    }
}

// siege -H "Accept: application/json" -c10 -r100 "http://172.19.48.1:8080/nosql-lab-mon/2025000001"
// time seq 1 100 | xargs -n1 -P10 -I{} curl -s "http://172.19.48.1:8080/nosql-lab-mon/2025000001"
