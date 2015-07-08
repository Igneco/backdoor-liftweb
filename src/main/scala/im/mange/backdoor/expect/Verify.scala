package im.mange.backdoor.expect

trait Expectation {
  def isVerified: Boolean
  def describeFailure: String
}

case class Verify(expectation: Expectation)
