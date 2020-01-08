package com.rubrikloud

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.html.respondHtml
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import kotlinx.html.*

fun main(args: Array<String>) {
    //Start the Github Service Thread to Populate Database Table
    val commitServiceThread = Thread(GithubCommitFetcher())
    commitServiceThread.start()
    //Start Main Web Application
    io.ktor.server.netty.EngineMain.main(args)
}

@Suppress("unused")
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }

    routing {
        route("/commits") {
            get {
                val limit = call.parameters["num_results"] ?: "10"
                if (limit.matches("[0-9]*".toRegex())) {
                    val commits = H2Driver.selectRecords(limit.toInt())
                    call.respond(Commits(commits))
                } else {
                    call.respond(HttpStatusCode.BadRequest,"Expected numeric value for num_results, got: $limit")
                }

            }
        }

        get("/") {
            val records = H2Driver.selectRecords(50)
            call.respondHtml {
                body {
                    h1 { +"Top 100 Latest Commits Containing the Word Bug" }
                    ul {
                        for (commit in records) {
                            li { +"$commit" }
                            br { }
                        }
                    }
                }
            }
        }
    }
}


