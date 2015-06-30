package im.mange.backdoor.expect

trait Expectation {
  def isVerified: Boolean
  //TODO: def describeFailure
}

case class Verify(expectation: Expectation)
