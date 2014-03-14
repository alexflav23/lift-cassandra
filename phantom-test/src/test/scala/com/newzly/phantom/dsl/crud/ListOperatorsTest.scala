package com.newzly.phantom.dsl.crud

import org.scalatest.concurrent.PatienceConfiguration
import org.scalatest.time.SpanSugar._
import com.datastax.driver.core.utils.UUIDs
import com.newzly.phantom.Implicits._
import com.newzly.phantom.helper.BaseTest
import com.newzly.phantom.tables.{ Recipe, Recipes }
import com.newzly.util.finagle.AsyncAssertionsHelper._

class ListOperatorsTest extends BaseTest {
  implicit val s: PatienceConfiguration.Timeout = timeout(10 seconds)
  val keySpace = "listoperators"


  it should "append an item to a list" in {
    Recipes.insertSchema

    val recipe = Recipe.sample
    val id = UUIDs.timeBased()
    val insert = Recipes.insert
      .value(_.uid, id)
      .value(_.url, recipe.url)
      .value(_.description, recipe.description)
      .value(_.ingredients, recipe.ingredients)
      .value(_.last_checked_at, recipe.lastCheckedAt)
      .value(_.props, recipe.props)
      .future()

    val operation = for {
      insertDone <- insert
      update <- Recipes.update.where(_.url eqs recipe.url).modify(_.ingredients append "test").future()
      select <- Recipes.select(_.ingredients).where(_.url eqs recipe.url).one
    } yield select

    operation.successful {
      items => {
        items.isDefined shouldBe true
        items.get shouldBe recipe.ingredients ::: List("test")
      }
    }
  }

  it should "append several items to a list" in {

    val recipe = Recipe.sample
    val id = UUIDs.timeBased()
    val insert = Recipes.insert
      .value(_.uid, id)
      .value(_.url, recipe.url)
      .value(_.description, recipe.description)
      .value(_.ingredients, recipe.ingredients)
      .value(_.last_checked_at, recipe.lastCheckedAt)
      .value(_.props, recipe.props)
      .future()

    val appendable = List("test", "test2")

    val operation = for {
      insertDone <- insert
      update <- Recipes.update.where(_.url eqs recipe.url).modify(_.ingredients appendAll appendable).future()
      select <- Recipes.select(_.ingredients).where(_.url eqs recipe.url).one
    } yield select

    operation.successful {
      items => {
        items.isDefined shouldBe true
        items.get shouldBe recipe.ingredients ::: appendable
      }
    }
  }

  it should "prepend an item to a list" in {
    Recipes.insertSchema

    val recipe = Recipe.sample
    val id = UUIDs.timeBased()
    val insert = Recipes.insert
      .value(_.uid, id)
      .value(_.url, recipe.url)
      .value(_.description, recipe.description)
      .value(_.ingredients, recipe.ingredients)
      .value(_.last_checked_at, recipe.lastCheckedAt)
      .value(_.props, recipe.props)
      .future()

    val operation = for {
      insertDone <- insert
      update <- Recipes.update.where(_.url eqs recipe.url).modify(_.ingredients prepend "test").future()
      select <- Recipes.select(_.ingredients).where(_.url eqs recipe.url).one
    } yield select

    operation.successful {
      items => {
        items.isDefined shouldBe true
        items.get shouldBe List("test") :::  recipe.ingredients
      }
    }
  }

  it should "prepend several items to a list" in {
    Recipes.insertSchema

    val recipe = Recipe.sample
    val id = UUIDs.timeBased()
    val insert = Recipes.insert
      .value(_.uid, id)
      .value(_.url, recipe.url)
      .value(_.description, recipe.description)
      .value(_.ingredients, recipe.ingredients)
      .value(_.last_checked_at, recipe.lastCheckedAt)
      .value(_.props, recipe.props)
      .future()

    val appendable = List("test", "test2")
    val operation = for {
      insertDone <- insert
      update <- Recipes.update.where(_.url eqs recipe.url).modify(_.ingredients prependAll appendable).future()
      select <- Recipes.select(_.ingredients).where(_.url eqs recipe.url).one
    } yield select

    operation.successful {
      items => {
        items.isDefined shouldBe true
        items.get shouldBe appendable.reverse ::: recipe.ingredients
      }
    }
  }

  it should "remove an item from a list" in {
    Recipes.insertSchema

    val list = List("test, test2")
    val recipe = Recipe.sample.copy(ingredients = list)
    val id = UUIDs.timeBased()
    val insert = Recipes.insert
      .value(_.uid, id)
      .value(_.url, recipe.url)
      .value(_.description, recipe.description)
      .value(_.ingredients, recipe.ingredients)
      .value(_.last_checked_at, recipe.lastCheckedAt)
      .value(_.props, recipe.props)
      .future()

    val operation = for {
      insertDone <- insert
      update <- Recipes.update.where(_.url eqs recipe.url).modify(_.ingredients remove list.head).future()
      select <- Recipes.select(_.ingredients).where(_.url eqs recipe.url).one
    } yield select

    operation.successful {
      items => {
        items.isDefined shouldBe true
        items.get shouldBe list.tail
      }
    }
  }

  it should "remove multiple items from a list" in {
    Recipes.insertSchema

    val list = List("test, test2, test3, test4, test5")
    val recipe = Recipe.sample.copy(ingredients = list)
    val id = UUIDs.timeBased()
    val insert = Recipes.insert
      .value(_.uid, id)
      .value(_.url, recipe.url)
      .value(_.description, recipe.description)
      .value(_.ingredients, recipe.ingredients)
      .value(_.last_checked_at, recipe.lastCheckedAt)
      .value(_.props, recipe.props)
      .future()

    val operation = for {
      insertDone <- insert
      update <- Recipes.update.where(_.url eqs recipe.url).modify(_.ingredients removeAll list.tail).future()
      select <- Recipes.select(_.ingredients).where(_.url eqs recipe.url).one
    } yield select

    operation.successful {
      items => {
        items.isDefined shouldBe true
        items.get shouldBe List(list.head)
      }
    }
  }



}
