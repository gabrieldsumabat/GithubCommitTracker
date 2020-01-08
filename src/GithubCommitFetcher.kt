package com.rubrikloud

import org.json.JSONArray
import org.json.JSONObject
import java.lang.Thread.sleep


class GithubCommitFetcher : Runnable {

    override fun run() {
        while(true) {
            loadGitCommitToDb()
            sleep(30000)
        }
    }

    fun loadGitCommitToDb() {
        val httpResponseCommit = khttp.get(
            url = "https://api.github.com/search/commits",
            headers = mapOf("Accept" to "application/vnd.github.cloak-preview"),
            params = mapOf("q" to "bug", "per_page" to "100","sort" to "author-date", "order" to "desc")
        )

        val jsonCommit = httpResponseCommit.jsonObject
        val commitJsonArray = jsonCommit.get("items") as JSONArray
        val arrayOfRecordValues = convertJsonArrayToGithubCommitArray(commitJsonArray)
        val arrayOfString = arrayOfRecordValues.map { it.getMergeValuesString() }
        H2Driver.mergeRecords(values =  arrayOfString.joinToString(","))
    }

    private fun convertJsonArrayToGithubCommitArray(jsonArray:JSONArray): ArrayList<GithubCommit> {
        val stringArray = ArrayList<GithubCommit>()
        jsonArray.forEach { json ->
            val jsonObject = json as JSONObject
            val githubCommit = getGithubCommitFromJson(jsonObject)
            stringArray.add(githubCommit)
        }
        return stringArray
    }

    private fun getGithubCommitFromJson(jsonObject:JSONObject):GithubCommit {
        val commitID = jsonObject.get("node_id")
        val committerName = jsonObject.getJSONObject("commit").getJSONObject("committer").get("name")
        val commitDate = jsonObject.getJSONObject("commit").getJSONObject("committer").get("date")
        val commitMessage = jsonObject.getJSONObject("commit").get("message").toString().replace("['\"]".toRegex(),"")
        val commitUrl = jsonObject.get("url")
        val commitRepo = jsonObject.get("html_url").toString().split("/")[4]
        return GithubCommit(commitID.toString(),committerName.toString(),commitDate.toString(),commitMessage,commitUrl.toString(),commitRepo)
    }
}





