package com.offcn.pressure

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

import scala.concurrent.duration._

class MallSimulation extends Simulation {

  val userFeeder = jdbcFeeder("jdbc:mysql://localhost:3306/point_mall_test?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true&zeroDateTimeBehavior=convertToNull", "alex", "Becauseofyou7,", "select * from user_order_base_info").random
  val goodsFeeder = jdbcFeeder("jdbc:mysql://localhost:3306/point_mall_test?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true&zeroDateTimeBehavior=convertToNull", "alex", "Becauseofyou7,", "select id goodsId,init_point from goods").random
  val headers_json = Map("Content-Type" -> "application/json")

  val httpConf = http
    .baseUrl("http://localhost:9003")
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36")

  // 1. 下单预校验
  val gold = scenario("mall-test")
    .feed(userFeeder)
    .feed(goodsFeeder)
    // 2. 下单
    .exec(http("order-test")
      .post("/mall/app/order/pay")
      .headers(headers_json)
      .header("Authorization", "eyJhbGciOiJIUzUxMiJ9.eyJjcmVhdGVfdGltZSI6MTU2NzQyNjEwMzgwNSwidXNlck5hbWUiOiLlvpDkuJbnkKYiLCJleHAiOjE1Njc1MTI1MDMsInVzZXJJZCI6InhzcTcxNjQ0In0.MM7sHl85lT3Y6WBYR9AqPmpRXF7C5I1EYYFWxcJoo4naUzyTxhCdwisupSadrLGsyMDhz-YwrBwgsZtPnrc6Zw")
      .body(StringBody("""{ "buynum": 1,"smscode": "1","goodsId": "${goodsId}","willCost": "${init_point}","willStep":1,"userinfo":{"openid": "${openid}","userName": "${user_name}","tel": "${tel}","userMail": "${user_mail}","address": "${address}","addressDetail": "${address_detail}","addressJson": "${address_json}"}}""")).asJson
      .check(status.is(200)))
  //    .exec(http("pre-order")
  //      .get("/mall/app/order/preCheck?openid=${openid}&goodsId=${id}")
  //      .check(jsonPath("$.code").saveAs("code")))
  //    .exec {
  //      session =>
  //        val res = session("code").as[String]
  //        if ("0".equals(res)) {
  //
  //        } else {
  //          println("不可下单")
  //        }
  //        session
  //    }

  setUp(
    gold.inject(rampUsers(20) during (10 seconds)).throttle()
  )
}
