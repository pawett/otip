package com.cognitionis.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

import domain.Document;

import com.mongodb.DB;

public class MongoDBConnector {

	public static MongoDatabase getConnection()
	{
		//String dbURI = "mongodb://tipsem:tipsem@localhost:27027/tipsem";
		//MongoClientURI uri = new MongoClientURI(dbURI);
		//MongoClient mongoClient = new MongoClient(uri);
		 MongoClient mongoClient = new MongoClient("localhost");
		return mongoClient.getDatabase("tipsem");
	}
	
	
}
