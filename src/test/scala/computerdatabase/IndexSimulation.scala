package computerdatabase

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef.jdbcFeeder

import scala.concurrent.duration._

class IndexSimulation extends Simulation {

  val userFeeder = jdbcFeeder("jdbc:mysql://172.26.197.93:3306/offcn_card_test?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true&zeroDateTimeBehavior=convertToNull", "root", "Offcn#wxsyb!(1sTudEnt", "select appid from ma_info").random

  val stuFeeder = jdbcFeeder("jdbc:mysql://172.26.197.93:3306/offcn_card_test?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true&zeroDateTimeBehavior=convertToNull", "root", "Offcn#wxsyb!(1sTudEnt", "select openid from student").random

  val indexInfo = scenario("index-test").feed(userFeeder).feed(stuFeeder).exec(http("index-simulation").get("/app/punchInfo?appid=${appid}&openid=${openid}").check(status.is(200)))

  val httpConf = http
    .baseUrl("http://localhost:9002")
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36")

  setUp(indexInfo.inject(rampUsers(4000) during (20 seconds)).throttle(reachRps(1000) in (10 seconds), holdFor(6 minutes)).protocols(httpConf))
}
