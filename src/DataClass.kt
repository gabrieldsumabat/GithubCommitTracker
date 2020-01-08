package com.rubrikloud

data class GithubCommit(val commitId:String,
                        val committerName:String,
                        val datetimeStamp:String,
                        val message:String,
                        val url:String,
                        val repository:String) {

    fun getMergeValuesString(): String {
        return "('$commitId','$committerName','$datetimeStamp','$message','$url','$repository')"
    }
}

data class Commits(val commits:ArrayList<GithubCommit>)