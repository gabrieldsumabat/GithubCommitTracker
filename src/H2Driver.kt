package com.rubrikloud

import java.sql.Connection
import java.sql.DriverManager


object H2Driver {

    init {
        createTable()
    }

    private fun connect(): Connection {
        //  Database credentials --> Should be ENV Variables and hashed stored in a creds file
        val DB_URL = "jdbc:h2:./h2db"
        val USER = "sa"
        val PASS = ""
        val conn = DriverManager.getConnection(DB_URL, USER, PASS)
        return conn
    }

    private fun createTable() {
        val conn = connect()
        try {
            val st = conn.createStatement()
            // For production instances there should also be a multicol index
            val sql = "DROP TABLE IF EXISTS GITCOMMITS;" +
                    "CREATE TABLE IF NOT EXISTS GITCOMMITS (" +
                    " commitId VARCHAR(255), " +
                    " name VARCHAR(255), " +
                    " date TIMESTAMP, " +
                    " message CLOB, " +
                    " url VARCHAR(255), " +
                    " repo VARCHAR(255), " +
                    " PRIMARY KEY ( commitId ))"
            st.executeUpdate(sql)
            st.closeOnCompletion()
            conn.commit()
            conn.close()
            println("Created GITCOMMITS table in database")
        } catch (e: Exception) {
            e.printStackTrace()
            if (!conn.isClosed) {
                conn.rollback()
                conn.close()
            }
        }
    }

    fun mergeRecords(values: String) {
        val conn = connect()
        try {
            val st = conn.createStatement()
            val sqlString = "MERGE INTO GITCOMMITS KEY (commitId,name,date,message,url,repo) VALUES $values;"
            st.execute(sqlString)
            st.close()
            conn.commit()
            conn.close()
            println("Added Latest Github Commits to Memory!")
        } catch (e: Exception) {
            e.printStackTrace()
            if (!conn.isClosed) {
                conn.rollback()
                conn.close()
            }
        }
    }

    fun selectRecords(limit: Int): ArrayList<GithubCommit> {
        val selectedArray = ArrayList<GithubCommit>()
        val conn = connect()
        try {
            val st = conn.createStatement()
            val sqlString = "SELECT TOP $limit commitId, name, date, message, url, repo FROM GITCOMMITS ORDER BY date DESC;"
            val resultSet = st.executeQuery(sqlString)
            while (resultSet.next()) {
                selectedArray.add(GithubCommit(
                    resultSet.getString("commitId"),
                    resultSet.getString("name"),
                    resultSet.getString("date"),
                    resultSet.getString("message"),
                    resultSet.getString("url"),
                    resultSet.getString("repo")
                    ))
            }
            st.close()
            conn.close()
        } catch (e: Exception) {
            e.printStackTrace()
            if (!conn.isClosed) {
                conn.close()
            }
        }
        return selectedArray
    }
}