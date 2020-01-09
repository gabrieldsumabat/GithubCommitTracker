Github Bug Commit Tracker
Gabriel Sumabat

Ktor Application for tracking the latest commits on Github containing the word 'Bug'

Paths:
    /                         -> Returns the latest 100 commits
    /commits?num_results{int} -> Returns {int} number of records [Default value is 10]

Services:
    Application         -> Main Web Application, serves all content
    GithubCommitLoader  -> (Runnable) Loads H2 Database with the latest commits

Build Tool: Maven

To run: 'mvn package' and run the built jar

Notes:
    1. It appears the Github Commit Search API is returning static data
    2. Coroutines is a valid option for the Fetcher if multiple terms are desired
