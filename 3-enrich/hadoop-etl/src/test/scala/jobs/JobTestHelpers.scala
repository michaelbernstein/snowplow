/*
 * Copyright (c) 2012-2013 SnowPlow Analytics Ltd. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package com.snowplowanalytics.snowplow.enrich.hadoop
package jobs

// Scala
import scala.collection.mutable.ListBuffer

// Specs2
import org.specs2.matcher.Matcher
import org.specs2.matcher.Matchers._

// Scalding
import com.twitter.scalding._

/**
 * Holds helpers for running integration
 * tests on SnowPlow EtlJobs.
 */
object JobTestHelpers {

  /**
   * The current version of our Hadoop ETL
   */
  val EtlVersion = "hadoop-0.3.2"

  /**
   * A Specs2 matcher to check if a Scalding
   * output sink is empty or not.
   */
  val beEmpty: Matcher[ListBuffer[_]] =
    ((_: ListBuffer[_]).isEmpty, "is not empty")

  /**
   * How Scalding represents input lines
   */
  type ScaldingLines = List[(String, String)]

  /**
   * A case class to make it easy to write out input
   * lines for Scalding jobs without manually appending
   * line numbers.
   *
   * @param l The repeated String parameters
   */
  case class Lines(l: String*) {

    val lines = l.toList
    val numberedLines = number(lines)

    /**
     * Numbers the lines in the Scalding format.
     * Converts "My line" to ("0" -> "My line")
     *
     * @param lines The List of lines to number
     * @return the List of ("line number" -> "line")
     *         tuples.
     */
    private def number(lines: List[String]): ScaldingLines =
      for ((l, n) <- lines zip (0 until lines.size)) yield (n.toString -> l)
  }

  /**
   * Implicit conversion from a Lines object to
   * a ScaldingLines, aka List[(String, String)],
   * ready for Scalding to use.
   *
   * @param lines The Lines object
   * @return the ScaldingLines ready for Scalding
   */
  implicit def Lines2ScaldingLines(lines : Lines): ScaldingLines = lines.numberedLines 

  // Standard JobTest definition used by all integration tests
  val EtlJobTest = 
    JobTest("com.snowplowanalytics.snowplow.enrich.hadoop.EtlJob").
      arg("input_folder", "inputFolder").
      arg("input_format", "cloudfront").
      arg("maxmind_file", "-"). // Not needed when running locally, but error if not set
      arg("output_folder", "outputFolder").
      arg("bad_rows_folder", "badFolder").
      arg("exceptions_folder", "exceptionsFolder")
}