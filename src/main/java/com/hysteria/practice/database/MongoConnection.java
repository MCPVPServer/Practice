package com.hysteria.practice.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.MongoClientSettings;
import java.util.Objects;

public class MongoConnection {

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;

    public MongoConnection(String uri, String fallbackDatabase) {
        try {
            if (uri != null && !uri.isEmpty()) {
                ConnectionString connectionString = new ConnectionString(uri);
                MongoClientSettings settings = MongoClientSettings.builder()
                        .applyConnectionString(connectionString)
                        .build();
                mongoClient = MongoClients.create(settings);

                String dbName = connectionString.getDatabase();
                if (dbName == null || dbName.isEmpty()) dbName = fallbackDatabase;
                mongoDatabase = mongoClient.getDatabase(dbName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MongoConnection(String host, int port, String database) {
        this(host, port, null, null, database);
    }

    public MongoConnection(String host, int port, String username, String password, String database) {
        try {
            MongoClientSettings settings;
            if (username != null && !username.isEmpty()) {
                MongoCredential credential = MongoCredential.createCredential(username, database, password.toCharArray());
                settings = MongoClientSettings.builder()
                        .applyToClusterSettings(builder -> builder.hosts(java.util.Collections.singletonList(new ServerAddress(host, port))))
                        .credential(credential)
                        .build();
            } else {
                settings = MongoClientSettings.builder()
                        .applyToClusterSettings(builder -> builder.hosts(java.util.Collections.singletonList(new ServerAddress(host, port))))
                        .build();
            }
            mongoClient = MongoClients.create(settings);
            mongoDatabase = mongoClient.getDatabase(database);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MongoDatabase getMongoDatabase() {
        return mongoDatabase;
    }

    public boolean isConnected() {
        return mongoDatabase != null;
    }
}
