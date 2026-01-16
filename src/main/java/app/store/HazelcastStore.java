package app.store;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import app.model.Student;
import com.google.gson.Gson;

public class HazelcastStore {
    static HazelcastInstance hz;
    static IMap<String, String> map; // String olarak sakla
    static Gson gson = new Gson();

    public static void init() {
        hz = HazelcastClient.newHazelcastClient(); // config dosyasına bağlanır
        map = hz.getMap("ogrenciler");
        
        for (int i = 0; i < 10000; i++) {
            String id = "2025" + String.format("%06d", i);
            Student s = new Student(id, "Ad Soyad " + i, "Bilgisayar");
            map.put(id, gson.toJson(s)); // JSON string olarak sakla
        }
        
        System.out.println("-------------------------------------");
        System.out.println("HAZELCAST çalışıyor");
        System.out.println("-------------------------------------");
    }

    public synchronized static Student get(String id) {
        String json = map.get(id);
        return json != null ? gson.fromJson(json, Student.class) : null;
    }
}

// siege -H "Accept: application/json" -c10 -r100 "http://172.19.48.1:8080/nosql-lab-hz/2025000001"
// time seq 1 100 | xargs -n1 -P10 -I{} curl -s "http://172.19.48.1:8080/nosql-lab-hz/2025000001"
