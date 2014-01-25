package com.newzly.phantom.helper

import java.util.{ Date, UUID }
import scala.util.Random


object Sampler {

  /**
   * Returns a pseudo-random number between min and max, inclusive.
   * The difference between min and max can be at most
   * <code>Integer.MAX_VALUE - 1</code>.
   *
   * @param min Minimum value
   * @param max Maximum value.  Must be greater than min.
   * @return Integer between min and max, inclusive.
   * @see java.util.Random#nextInt(int)
   */
  def getARandomInteger(min: Int = Int.MinValue, max: Int = Int.MaxValue): Int = {
    val rand = new Random()
    rand.nextInt((max - min) + 1) + min
  }

  /**
   * Get a unique random generated string.
   * This uses the default java GUID implementation.
   * @return A random string with 64 bits of randomness.
   */
  def getAUniqueString: String = {
    UUID.randomUUID().toString
  }
}

object TableHelper {

  /**
   * Generates a random unique row for a TestRow cassandra table.
   * @return A unique Test Row with nested JSON structures..
   */
  def getAUniqueJsonTestRow: TestRow = {
    TestRow(
      Sampler.getAUniqueString,
      Some(Sampler.getARandomInteger()),
      SimpleMapOfStringsClass(Map(Sampler.getAUniqueString -> Sampler.getARandomInteger())),
      Some(SimpleMapOfStringsClass(Map(Sampler.getAUniqueString -> Sampler.getARandomInteger()))),
      Map(Sampler.getAUniqueString -> SimpleMapOfStringsClass(Map(Sampler.getAUniqueString -> Sampler.getARandomInteger())))
    )
  }

  /**
   * Generate a unique article.
   * @param order The order index of the article.
   * @return A unique article.
   */
  def getAUniqueArticle(order: Long = Sampler.getARandomInteger()): Article = {
    Article(
      Sampler.getAUniqueString,
      UUID.randomUUID(),
      order
    )
  }

  /**
   * Generate a unique recipe using the sampler.
   * @return A Recipe.
   */
  def getAUniqueRecipe: Recipe = {
    Recipe(
      Sampler.getAUniqueString,
      Some(Sampler.getAUniqueString),
      Seq(Sampler.getAUniqueString, Sampler.getAUniqueString),
      None,
      Some(Sampler.getARandomInteger()),
      new Date(),
      Map.empty[String, String]
    )
  }
}
