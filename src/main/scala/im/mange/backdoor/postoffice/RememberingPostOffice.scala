package im.mange.backdoor.postoffice

import im.mange.backdoor.Resettable
import org.joda.time.DateTime

case class Entry(when: DateTime, what: Any)

object RememberingPostOffice extends PostOffice with Resettable {
  private var lettersByAddresse: scala.collection.concurrent.TrieMap[String, Seq[Entry]] =
    new scala.collection.concurrent.TrieMap()

  override def post(message: Any, to: String): Unit = {
    //TODO: make this a debug flag
    val current = lettersByAddresse.getOrElse(to, Seq.empty[Entry])
    lettersByAddresse.update(to, current :+ Entry(new DateTime(), message))
//    println(s"$to got $message - now has: ${lettersByAddresse(to).size}")
  }

  def reset() {
    lettersByAddresse = new scala.collection.concurrent.TrieMap()
  }

  //TODO: validateReceived? or something
  def validate(to: String, expected: Any) = {
    //TODO: obviously we can make this tighter ..
    val all = lettersFor(to)
//    println(s"all: $all")
    val r = all.contains(expected)
//    println(s"$expected for $to in ${all.mkString}? $r - ${lettersByAddresse.keys} - ${lettersByAddresse.contains(to)}")
    r
  }

  def lettersFor(me: String) = lettersByAddresse(me).map(_.what)

  def state = lettersByAddresse
}

trait PostOffice {
  def post(message: Any, to: String): Unit
}

object NullPostOffice extends PostOffice {
  override def post(message: Any, to: String) {}
}

