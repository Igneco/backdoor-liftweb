package im.mange.backdoor.postoffice

import im.mange.backdoor.Resettable
import org.joda.time.DateTime

case class Letter(who: String, when: DateTime, what: Any)

object RememberingPostOffice extends PostOffice with Resettable {
  private var lettersByAddresse: scala.collection.concurrent.TrieMap[String, Seq[Letter]] =
    new scala.collection.concurrent.TrieMap()

  override def post(message: Any, to: String): Unit = {
    //TODO: make this a debug flag
    val current = lettersByAddresse.getOrElse(to, Seq.empty[Letter])
    lettersByAddresse.update(to, current :+ Letter(to, new DateTime(), message))
  }

  def reset() {
    lettersByAddresse = new scala.collection.concurrent.TrieMap()
  }

  def validate(to: String, expected: Any) = lettersFor(to).contains(expected)
  def lettersFor(me: String) = lettersByAddresse(me).map(_.what)
  def allLetters = lettersByAddresse.values.flatten
}

trait PostOffice {
  def post(message: Any, to: String): Unit
}

object NullPostOffice extends PostOffice {
  override def post(message: Any, to: String) {}
}

