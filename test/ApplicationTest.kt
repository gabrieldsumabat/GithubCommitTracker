package com.rubrikloud

import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {

    @BeforeTest
    fun prepare() {
        //Populate the Database table, appears that the API returns static data?
        GithubCommitLoader().loadGitCommitToDb()
    }

    @Test
    fun testCommits() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/commits?num_results=1").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                val expectedText = "{\n" +
                        "  \"commits\": [\n" +
                        "    {\n" +
                        "      \"commitId\": \"MDY6Q29tbWl0NjMxMTAyNDo2ZGY3Mzg1Y2YwNGNkZjQwMDMxNjRmMDMwNzFiMWMzZGIyY2U2Njc5\",\n" +
                        "      \"committerName\": \"Pragun\",\n" +
                        "      \"datetimeStamp\": \"2079-01-27 04:29:52.0\",\n" +
                        "      \"message\": \"Removed a bug in pwsw-daemon to report correct energy_consumed when switch is off.\",\n" +
                        "      \"url\": \"https://api.github.com/repos/mitmedialab/grassroots-mobile/commits/6df7385cf04cdf4003164f03071b1c3db2ce6679\",\n" +
                        "      \"repository\": \"grassroots-mobile\"\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}"
                assertEquals(expectedText, response.content)
            }
            handleRequest(HttpMethod.Get, "/commits?num_results=a").apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
                val expectedText = "Expected numeric value for num_results, got: a"
                assertEquals(expectedText, response.content)
            }
        }
    }
}
